/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Page1_schedule;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import DB.Menu_DbOpenHelper;
import Page1.Page1;


public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice";
    private static final String TAG = LocationUpdatesService.class.getSimpleName();
    private static final String CHANNEL_ID = "channel_01";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();

    //갱신 주기
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //알람 관련
    private static final String NOTIFICATION_ID = "12345678";
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    //위치 관련
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    //액티비티에서 받아온 값
    private List<String> data;
    private String date, key;
    private int length = -1;
    private String  checkpostion;
    private String onoff = null;
    private boolean alramOff = false;

    public LocationUpdatesService() { }
    private Menu_DbOpenHelper dbOpenHelper;

    @Override
    public void onCreate() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }


    //앱이 화면에 실행중일때
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }



    //앞에서 전달한 값을 연결
    public  void getData(List<String> data){
        this.data = data;
    }
    public  void getDate(String date){ this.date = date; }
    public  void sendKey(String key){
        this.key = key;
    }


    //화면을 종료했을 때
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        //데베 접근
        dbOpenHelper = new Menu_DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();

        Cursor iCursor = dbOpenHelper.selectColumns();
        while(iCursor.moveToNext()){
            onoff = iCursor.getString(iCursor.getColumnIndex("userid"));
        }

        if (!alramOff && !mChangingConfiguration && Location_Utils.requestingLocationUpdates(this)&& data.size() > 1) {
            Log.i(TAG, "Starting foreground service" + "/" + String.valueOf(data.size()));
            startForeground(1234, getNotification("nodata"));
        } else {
            stopForeground(true);
        }

        //알림 껐을 때
        if(  onoff.equals("false")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //stopForeground(true);
                mNotificationManager.deleteNotificationChannel(CHANNEL_ID);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
        return true;
    }


    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }


    //위치 연결하는 부분
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates"+data.get(0));
        Location_Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Location_Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }



    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Location_Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Location_Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    //알람 관련
    private Notification getNotification(String text) {

        //누르면 포그라운드에서 갖고 있던 값을 액티비티에 전달
        Intent  intent = new Intent(this, Page1_Main.class);
        intent.putExtra("smsMsg" , length+1);   //찾은 도시의 위치
        intent.putExtra("date",date);                  //날짜
        intent.putExtra("key", key);                   //데베 키
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent activityPendingIntent =
                PendingIntent.getActivity(
                        this,
                0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder;


        //현 위치와 찾아야하는 도시가 맞지 않을 때
        if(text.equals("nodata")){
            builder = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                    .setContentIntent(activityPendingIntent)
                    .setOngoing(true)
                    .setContentText("현재 위치 찾는 중...")
                    .setContentTitle("내로라")
                    .setSmallIcon(R.drawable.ic_heart_on)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                    .setWhen(System.currentTimeMillis());
        }


        //찾아야하는 도시를 찾았을 때
        else {
             builder = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                    .setContentIntent(activityPendingIntent)
                    .setContentText(data.get(0)+"역에 도착했습니다.")
                    .setContentTitle("내로라")
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                     .setDefaults(Notification.DEFAULT_SOUND)
                     .setSmallIcon(R.drawable.ic_heart_on)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis());
        }

        //버전 관련
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }



    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }


    //위치가 갱신 됐을 때
    private void onNewLocation(Location location) {
        mLocation = location;

        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        if (serviceIsRunningInForeground(this)) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            checkpostion = getCurrentAddress(lat, lon);
            Log.i("갱신", checkpostion);
            Log.i("length", String.valueOf(length));


            //시연용
            mNotificationManager.notify(1234, getNotification(checkpostion));
            if(data.size() > 1){
                data.remove(0);
                length++;
            }
            if(data.size() == 1){
                Intent intent2 = new Intent(this, Page1.class);
//                intent.putExtra("smsMsg" , length+1);   //찾은 도시의 위치
//                intent.putExtra("date",date);                  //날짜
                intent2.putExtra("key", "off");                   //데베 키
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
            }



            //현위치와 배열에 있는 도시를 검색
//            if(checkpostion.contains(data.get(0))){
//                if(!onoff.equals("off")) {
//                //    mNotificationManager.notify(1234, getNotification(checkpostion));
//                }
//                data.remove(0);
//                length++;
//                Log.i("포함되어 있음", "굿굿"+String.valueOf(length));
//            }
        }
    }



    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }


    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }



    //GPS를 주소로 변환
    public String getCurrentAddress( double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioException) {
            Toast.makeText(this, "네트워크를 연결해주세요.", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }



}