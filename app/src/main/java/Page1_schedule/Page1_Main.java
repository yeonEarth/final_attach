package Page1_schedule;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hansol.spot_200510_hs.BuildConfig;
import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.snackbar.Snackbar;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import DB.Menu_DbOpenHelper;
import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1.Page1;
import Page2_1_1.NetworkStatus;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_Main extends AppCompatActivity implements   Page1_pagerAdapter.send_expand, Page1_Position, SharedPreferences.OnSharedPreferenceChangeListener {

    //최상위 레이아웃
    NestedScrollView page1_scrollView;
    ImageView last_station;
    TextView userName;
    TextView startStation, endStation;

    // 날짜 관련 변수들
    String time, forcomparedate;
    SimpleDateFormat sdf, forcompare;
    Calendar myCalendar = Calendar.getInstance();
    EditText editDate;

    //뷰페이저 관련
    boolean pastTime;
    boolean forTime;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    PageIndicatorView pageIndicatorView;
    Handler handler = new Handler();
    List<String> arrayLocal= new ArrayList<>();
    int position = 0;

    //기차시간표 관련
    TextView no_data;
    ListView dataList;
    LinearLayout table_title;
    Page1_ListAdapter itemAdapter;

    //api 관련
    private  String receiveMsg;
    private  String [] data_split;
    private  ArrayList<Api_Item> completeList;              //정제된 리스트값
    private  String[] arr_line;
    private  String[] arr_all;
    private  String[] _name = new String[238];                                              //txt에서 받은 역이름
    private  String[] _code = new String[238];                                              //txt에서 받은 역코드
    private  String startCode, endCode, trainCode;
    private  String[] trainCodelist = {"01", "02", "03", "04", "08", "09", "15"};
    String date;
    int isNetworkConnect;

    //전체 스케쥴 관련
    Button all_schedule_btn;
    LinearLayout schedule_layout;
    LayoutInflater inflater;
    Page1_ScheduleAdapter adapter;
    ArrayList<RecycleItem> All_items = new ArrayList<RecycleItem>();
    ArrayList<RecycleItem> Day_items = new ArrayList<RecycleItem>();

    //메뉴 관련
    private Context context;
    private ImageButton menu_edit;
    private ImageView userImg;
    private TextView userText1;
    private TextView userText2;
    private RecyclerView recyclerView1;
    private Switch positionBtn;
    private Switch alramBtn;
    Main_RecyclerviewAdapter adapter2;
    ArrayList<String> name2 = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;

    ImageButton logo;

    //데이터베이스 관련
    private String db_key;
    private Menu_DbOpenHelper menu_dbOpenHelper;
    private Train_DbOpenHelper mDbOpenHelper;
    private static ArrayList<Database_Item> db_data = new ArrayList<Database_Item>();
    private String startDate;
    private List<String> station = new ArrayList<String>();
    private List<String> stationWithTransfer = new ArrayList<String>();

    //dp 변환
    float d;

    //위치서비스 관련
    String key;
    int gotData = -1;
    List<Integer> getPosition= new ArrayList<>();
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1_main_schedule);
        myReceiver = new MyReceiver();
        isNetworkConnect  = NetworkStatus.getConnectivityStatus(Page1_Main.this);


        //픽셀을 dp 로 변환하기 위함
        d = Page1_Main.this.getResources().getDisplayMetrics().density;

        //객체연결
        no_data = (TextView) findViewById(R.id.page1_timeTable_noData);
        editDate = (EditText) findViewById(R.id.page1_date);
        dataList = (ListView) findViewById(R.id.list_item);
        viewPager = (ViewPager) findViewById(R.id.page1_pager);
        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        all_schedule_btn = (Button) findViewById(R.id.page1_schedule_btn);
        schedule_layout = (LinearLayout) findViewById(R.id.page1_schedule_layout);
        userName = (TextView)findViewById(R.id.page1_userName);
        startStation = (TextView)findViewById(R.id.page1_startTxt);
        endStation = (TextView)findViewById(R.id.page1_endTxt);
        last_station = (ImageView)findViewById(R.id.page1_timeTable_lastimg);
        table_title = (LinearLayout)findViewById(R.id.table_title);

        completeList= new ArrayList<Api_Item>();
        pagerAdapter = new Page1_pagerAdapter(this, this, arrayLocal, this);


        page1_scrollView = (NestedScrollView)findViewById(R.id.nestScrollView_page1_schedule);

        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch)findViewById(R.id.menu_postion_btn);
        recyclerView1 = (RecyclerView)findViewById(R.id.menu_recyclerview1);

        mDrawerToggle = new EndDrawerToggle(this,drawer,toolbar2,R.string.open_drawer,R.string.close_drawer){
            @Override //드로어가 열렸을때
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override //드로어가 닫혔을때
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        setSupportActionBar(toolbar2);
        drawer.addDrawerListener(mDrawerToggle);

        //메뉴 안 내용 구성
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new Main_RecyclerviewAdapter(name2, context);
        recyclerView1.setAdapter(adapter2);

        //리사이클러뷰 헤더
        name2.add("0");
        name2.add("1");
        name2.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //메인로고
        logo = (ImageButton) findViewById(R.id.main_logo_page1_schedule);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                //overridePendingTransition(0,0);
                startActivity(intent);

            }
        });

        completeList= new ArrayList<Api_Item>();
        pagerAdapter = new Page1_pagerAdapter(this, this, arrayLocal, this);

        //page3_1_1_1_1 에서 일정저장하기 누를때 받아옴
        Intent get = getIntent();
        db_key = get.getStringExtra("key");


        // 현재 날짜 출력
        String myFormat = String.format("%s%s%s%s", "yyyy년 ", "MM월 ", "dd일 ", "EE요일");
        String FormatforCompare = String.format("yyyyMMdd");
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        forcompare = new SimpleDateFormat(FormatforCompare, Locale.KOREA);
        time = sdf.format(myCalendar.getTime());
        forcomparedate = forcompare.format((myCalendar.getTime()));  //현재 날짜와 일정 등록한 날짜와 비교를 위함
        editDate.setText(time);



        // 전체화면 스크롤뷰 안에 기차 시간표(리스트뷰) 스크롤링
        dataList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                page1_scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });




        /*데이터베이스 연결**************************************************/
        mDbOpenHelper = new Train_DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        //데이터베이스에 있는 값을 리스트에 추가
        getDatabase(db_key);



        //출발 날짜(디데이 계산)
        if(key != null){
            startDate = key;
        } else
            startDate = db_data.get(0).date;

        int myear = Integer.parseInt(startDate.substring(0,4));
        int mmonth = Integer.parseInt(startDate.substring(4,6));
        int mday = Integer.parseInt(startDate.substring(6));

        Calendar start_date = Calendar.getInstance();
        start_date.set(myear, mmonth-1, mday);

        long today = myCalendar.getTimeInMillis()/86400000;
        long dday = start_date.getTimeInMillis()/86400000;
        long count = dday - today;

        if((int)count != 0 && today < dday){
            userName.setText("D-" + String.valueOf((int)count));
        } else {
            userName.setText("지금은 여행 중");
        }



        //출발, 경유, 도착역, 환승역 분류
        for(int i = 0; i<db_data.size(); i++){
            if(db_data.get(i).text_shadow.length() != 0){
                //뷰페이져에 넣기 위한 분류
                station.add(db_data.get(i).text);
                stationWithTransfer.add(db_data.get(i).date+","+db_data.get(i).text_shadow);
            }
        }

        //출발, 경유, 도착역
        for(int i =0; i < station.size(); i++){
            String station_split[] = station.get(i).split(",");
            if(i < station.size() -1){
                arrayLocal.add(station_split[0]);
            }
            else{
                arrayLocal.add(station_split[0]);
                arrayLocal.add(station_split[1]);
            }
        }


        /*스케줄 연결 부분**************************************************/
        //일차, 기차시간, 관광지 부분 분류
        int dayNumber = 0;
        for(int i = 0; i < db_data.size(); i++){

            //일차
            if(db_data.get(i).text_shadow.length() == 0
                    && db_data.get(i).time.length() == 0
                    && db_data.get(i).contentId.length() == 0){
                All_items.add(new RecycleItem(Page1_ScheduleAdapter.HEADER, db_data.get(i).date, db_data.get(i).text, "", "", "" ,""));
                dayNumber++;
            }

            //기차시간
            else if(db_data.get(i).text_shadow.length() != 0){
                All_items.add(new RecycleItem(Page1_ScheduleAdapter.CHILD,  db_data.get(i).date, "", db_data.get(i).time, db_data.get(i).text, "", ""));
            }

            //관심 관광지
            else{
                All_items.add(new RecycleItem(Page1_ScheduleAdapter.CITY,  db_data.get(i).date, "", "", "", db_data.get(i).text, db_data.get(i).contentId));
            }
        }



        /*뷰페이져 부분**************************************************/
        //어댑터 연결
        settingList(Page1_Main.this);


        //뷰페이저 양쪽 미리보기
        int dpValue = 32;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin / 2);


        //현재시간 기준 또는 지도위치 기준 뷰페이져 포커스 이동
        if(gotData == -1){ forTime = true; }
        else {forTime = false; }
        if(forTime) {

            //데이터 초기화
            Day_items.clear();
            completeList.clear();
            getPosition.clear();

            for (int i = 0; i < stationWithTransfer.size(); i++) {
                //현재 날짜와 같은 일정을 찾고  && 지도에서 값을 받지 못했으면
                if (forcomparedate.trim().equals(stationWithTransfer.get(i).substring(0, 8).trim())) {
                    Day_schedule_data(stationWithTransfer.get(i));
                    if (isNetworkConnect != 3) {
                        send_Api(stationWithTransfer.get(i));
                    }
                    Log.i("여긴가", String.valueOf(i));
                    err_message();
                    startStation.setText(arrayLocal.get(i));
                    endStation.setText(arrayLocal.get(i + 1));
                    getPosition.add(i);

                    final int finalI = i;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(finalI);
                        }
                    }, 500);
                    break;
                }

                //해당 일자에 일정이 없을 때 && 지도에서 값을 못 받았을 경우
                else if (Integer.parseInt(forcomparedate.trim()) < Integer.parseInt(stationWithTransfer.get(i).substring(0, 8).trim()) ) {
                    final int finalI = i;
                    Log.i("여긴가", "2");
                    Day_schedule_data(stationWithTransfer.get(i));
                    if (isNetworkConnect != 3) {
                        send_Api(stationWithTransfer.get(i));
                    }
                    err_message();
                    startStation.setText(arrayLocal.get(i));
                    endStation.setText(arrayLocal.get(i + 1));
                    getPosition.add(i);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(finalI);
                        }
                    }, 500);
                    break;
                }
            }
        }

        viewPager.setAdapter(pagerAdapter);
        pageIndicatorView.setCount(arrayLocal.size());


        //뷰페이저 리스너
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageSelected(int position) {
                // 데이터 초기화
                completeList.clear();
                Day_items.clear();

                if (position != arrayLocal.size() - 1){
                    //---------------------------여기 추가: api 연결하기 전에 인터넷 연결 검사(이래야 뻑이 안남)
                    if(isNetworkConnect != 3 && Integer.parseInt(forcomparedate.trim())  <= Integer.parseInt(stationWithTransfer.get(position).substring(0,8).trim()) ){
                        send_Api(stationWithTransfer.get(position));
                        pastTime = false;
                    } else{
                        pastTime = true;
                    }
                    err_message();
                    startStation.setText(arrayLocal.get(position));
                    endStation.setText(arrayLocal.get(position+1));
                    last_station.setVisibility(View.INVISIBLE);
                    table_title.setVisibility(View.VISIBLE);
                    Day_schedule_data(stationWithTransfer.get(position));
                    adapter.notifyDataSetChanged();
                }
                else {
                    //마지막 역이기 때문에 시간표 리스트 초기화 및 갱신
                    no_data.setText("");
                    itemAdapter.notifyDataSetChanged();
                    pageIndicatorView.setSelection(position);
                    last_station.setVisibility(View.VISIBLE);
                    table_title.setVisibility(View.INVISIBLE);
                }
            }
        });




        //전체 스케줄 버튼 누르면
        final int finalDayNumber = dayNumber;
        all_schedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page1_Main.this, Page1_full_Schedule.class);
                intent.putExtra("schedule_data", (Serializable)All_items);
                intent.putExtra("dayNumber", finalDayNumber);
                intent.putExtra("key", db_key);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        //스케쥴 어댑터 연결
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.paeg1_recyclerview, schedule_layout, true);
        RecyclerView schedule_recyclerview = (RecyclerView) findViewById(R.id.page1_scheedule_recyclerview);
        ImageView no_sche = (ImageView)findViewById(R.id.no_sche);
        schedule_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Page1_ScheduleAdapter(Day_items);
        schedule_recyclerview.setAdapter(adapter);

        //스케쥴이 없을 때no_img 띄움
        if(Day_items.size() < 2){
            no_sche.setVisibility(View.VISIBLE);
        } else
            no_sche.setVisibility(View.INVISIBLE);

    }


    //날짜 비교해서 같으면 일일스케줄에 추가
    private void Day_schedule_data(String date){
        String text[] = date.split(",");
        for(int i = 0; i < All_items.size(); i++){
            if( text[0].equals(All_items.get(i).date)){
                Day_items.add(All_items.get(i));
            }
        }
    }


    //현재시간으로 스크롤하기
    public void scrollList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i =0;i<=completeList.size()-1;i++){
                    if(!Page1_Util.lastTime(completeList.get(i).depTime) ) {
                        position = i;
                        break;
                    }
                }
                dataList.smoothScrollToPosition(position);
            }
        },500);
    }


    //데이터베이스 받기(앞에서 저장한 값만 바로 보여줌)
    private void getDatabase(String db_key){
        String db_key2 = db_key.trim();
        Cursor iCursor = mDbOpenHelper.selecteNumber(db_key);
        db_data.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempNumber = iCursor.getString(iCursor.getColumnIndex("number"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            db_data.add(new Database_Item(tempDate, tempDayPass, tempStation, tempTime, tempContentId));
        }
    }


    //txt 돌려 역 비교할 배열 만들기(이름 지역코드 동네코드)<-로 구성
    private void settingList(Context context){
        String readStr = "";
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;
        try{
            inputStream = assetManager.open("stationWithcode.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        arr_all = readStr.split("\n"); //txt 내용을 줄바꿈 기준으로 나눈다.

        //한 줄의 값을 띄어쓰기 기준으로 나눠서, 배열에 넣는다.
        for(int i=0; i <arr_all.length; i++) {
            arr_line = arr_all[i].split(",");

            _code[i] = arr_line[0];     //역코드
            _name[i] = arr_line[1];     //이름
        }
    }


    //리스트에 넣을 값을 구성(환승작업)
    public void send_Api(String data) {
        data_split = data.split(",");
        date = data_split[0];
        Log.i("날짜", date);
        int isMiddle = 0;

        for (int p = 0; p < data_split.length - 2; p++) {

            if (p != 0)
                isMiddle++;

            //열차 코드 받음
            compareStation(data_split[p+1], data_split[p + 2]);

            //열차 종류별 api 검색(1)
            for (int i = 0; i < trainCodelist.length; i++) {
                trainCode = trainCodelist[i];
                try {
                    new Task().execute().get();
                    Log.i("결과값", receiveMsg);
                    trianjsonParser(receiveMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }

        //출발 시간 정렬
        Collections.sort(completeList);

        itemAdapter = new Page1_ListAdapter(this, completeList);
        dataList.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
        scrollList();



    }



    //앞 액티비티에서 선택된 역과 같은 역을 찾는다.
    private void compareStation(String start, String end){
        for(int i=0; i<_name.length; i++){
            if(start.equals(_name[i])){
                startCode = _code[i];
            }
            if(end.equals(_name[i])){
                endCode = _code[i];
            }
        }
    }


    //api 연결
    public  class  Task extends AsyncTask<String, Void, String> {
        private String str;

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try{
                //Z2ABtX1mu7Z%2FVsuir30gFJ%2BRDlNdWq8ujTgba2ZIu%2BRRiT65hy%2BOVzjmZxFXW04kGY08%2FgNrX7w%2BCexXYOz6Jg%3D%3D
                url = new URL("http://openapi.tago.go.kr/openapi/service/TrainInfoService/" +
                        "getStrtpntAlocFndTrainInfo?serviceKey=7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "numOfRows=30" +
                        "&pageNo=1&" +
                        "depPlaceId=" + startCode +
                        "&arrPlaceId=" + endCode +
                        "&depPlandTime=" + date +
                        "&trainGradeCode=" + trainCode +
                        "&_type=json");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }


    //api 값을 정제
    public String[] trianjsonParser(String jsonString){
        String arrplacename = null;
        String arrplandtime = null;
        String depplacename = null;
        String depplandtime = null;
        String traingradename = null;

        String[] arraysum = new String[100];
        String result = "";
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            JSONObject jsonObject3 = jsonObject1.getJSONObject("body");
            JSONObject jsonObject4 = jsonObject3.getJSONObject("items");
            JSONArray jsonArray = jsonObject4.getJSONArray("item");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                arrplacename = jObject.getString("arrplacename");
                arrplandtime = jObject.getString("arrplandtime");
                depplacename = jObject.getString("depplacename");
                depplandtime = jObject.getString("depplandtime");
                traingradename = jObject.getString("traingradename");

                String depTime = depplandtime.substring(8, 12);
                String arrTime = arrplandtime.substring(8, 12);

                completeList.add(new Api_Item(Page1_ListAdapter.HEADER, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2),  traingradename));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  arraysum;
    }

    //오류메시지
    private void err_message(){
        if(completeList.size() < 1){

            //(1)인터넷 연결이 안되어있을 때
            if(isNetworkConnect == 3){
                no_data.setText(R.string.train_err_internet);
            }

            //(2)지난 일정은 시간표 제공 안함
            else if(pastTime){
                no_data.setText(R.string.train_err_time);
            }

            //(3)API연결 오류(공공데이터포털 오류)
            else if( receiveMsg.contains("LIMITED")){
                no_data.setText(R.string.train_err_api);
            }

            //(4)열차 연결이 없는 경우
            else
                no_data.setText(R.string.train_err_course);
        }
        else {
            no_data.setText("");
        }
    }


    //****************인터페이스****************************************
    //혜택확인 누르면 레이아웃 펼쳐지기 위한 인터페이스
    @Override
    public void send(boolean isExpand) {
        if (!isExpand){
            viewPager.getLayoutParams().height = (int)(280*d);
            viewPager.requestLayout();
        } else {
            viewPager.getLayoutParams().height = (int)(200*d);
            viewPager.requestLayout();
        }
    }

    //포커스 되는 도시의 위치를 전달하기 위한 인터페이스
    @Override
    public int gotPosition() {
        return getPosition.get(0);
    }


    //여행 시작 버튼을 누르면 ( page1_viewpager와 연결된 인터페이스)
    @Override
    public void onClick_startBtn() {

        if(!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else if (!checkPermissions()) {
            requestPermissions();
            Toast.makeText(getApplicationContext(), "한번 더 눌러주세요.", Toast.LENGTH_LONG).show();
        } else
        {
            //포그라운드로 도시,날짜,데베키를 보냄
            mService.getData(arrayLocal);
            mService.getDate(startDate);
            mService.sendKey(db_key);
            mService.requestLocationUpdates();
        }
    }

    //뒤로가기버튼 누르면 스케쥴 메인으로 이동
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Page1_Main.this, Page3_Main.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }




    //****************Arraylist 데이터 구성****************************************
    //기차시간표 아이템 변수 선언
    public static class Api_Item implements Comparable<Api_Item>{
        int type;
        String depTime;
        String arrTime;
        String trainNumber;

        public Api_Item(int type, String dep, String arr, String train){
            this.type = type;
            this.depTime = dep;
            this.arrTime = arr;
            this.trainNumber= train;
        }

        String getDepTime() {
            return this.depTime;
        }
        String getArrTime() {
            return this.arrTime;
        }
        String getTrainNumber() {
            return this.trainNumber;
        }

        @Override
        public int compareTo(Api_Item o) {
            return this.depTime.compareTo(o.depTime);
        }
    }


    //데이터베이스 아이템 변수 선언
    public class Database_Item  {
        String date;
        String text;
        String text_shadow;
        String time;
        String contentId;

        public Database_Item(String date, String text, String text_shadow, String time, String contentId) {
            this.date = date;
            this.text = text;
            this.text_shadow = text_shadow;
            this.time = time;
            this.contentId = contentId;
        }

        public String getDate() {
            return date;
        }

        public String getText() {
            return text;
        }

        public String getText_shadow() {
            return text_shadow;
        }

        public String getTime() {
            return time;
        }

        public String getContentId() {
            return contentId;
        }
    }


    //스케줄 아이템 변수 선언
    public static class RecycleItem implements Parcelable {
        int type;
        String date;
        String daypass;
        String train_time;
        String station;
        String contentName;
        String contentId;

        public int getType() {
            return type;
        }

        public String getDate() {
            return date;
        }

        public String getDaypass() {
            return daypass;
        }

        public String getTrain_time() {
            return train_time;
        }

        public String getStation() {
            return station;
        }

        public String getContentName() {
            return contentName;
        }

        public String getContentId() {
            return contentId;
        }

        public RecycleItem(int type, String date, String daypass, String train_time, String station, String contentName, String contentId) {
            this.type = type;
            this.date = date;
            this.daypass = daypass;
            this.train_time = train_time;
            this.station = station;
            this.contentName = contentName;
            this.contentId = contentId;
        }

        protected RecycleItem(Parcel in) {
            type = in.readInt();
            date = in.readString();
            daypass = in.readString();
            train_time = in.readString();
            station = in.readString();
            contentName = in.readString();
            contentId = in.readString();
        }


        public static final Creator<RecycleItem> CREATOR = new Creator<RecycleItem>() {
            @Override
            public RecycleItem createFromParcel(Parcel in) {
                return new RecycleItem(in);
            }

            @Override
            public RecycleItem[] newArray(int size) {
                return new RecycleItem[size];
            }
        };

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(type);
            dest.writeString(date);
            dest.writeString(daypass);
            dest.writeString(train_time);
            dest.writeString(station);
            dest.writeString(contentName);
            dest.writeString(contentId);
        }
    }


    /**위치서비스 관련**********************************************************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        scrollList();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));

        //포그라운드에서 보낸 값
        Intent intent = getIntent();
        gotData = intent.getIntExtra("smsMsg", -1);
        startDate = intent.getStringExtra("date");
        db_key = intent.getStringExtra("key");

        if(gotData!=-1){
            forTime = false;
            //데이터 초기화
            Day_items.clear();
            completeList.clear();
            getPosition.clear();

            Day_schedule_data(stationWithTransfer.get(gotData));
            if (isNetworkConnect != 3) {
                send_Api(stationWithTransfer.get(gotData));
            }
            err_message();
            startStation.setText(arrayLocal.get(gotData));
            endStation.setText(arrayLocal.get(gotData + 1));
            getPosition.add(gotData);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(gotData);
                }
            }, 400);
        }
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }


    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }


    //위치 서비스 연결되어있는지
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //위치 서비스 비활성화시
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Page1_Main.this);
        builder.setTitle("현재 위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.");
        builder.setCancelable(true);
        builder.setPositiveButton("위치 설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    //위치 권한 받았는지 확인
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }


    //위치 권한 요청
    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.nestScrollView_page1_schedule),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(Page1_Main.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(Page1_Main.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    //위치 연결
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                // mService.requestLocationUpdates();
            } else {
                // Permission denied.
                //  setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.nestScrollView_page1_schedule),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.i("어디로 가는 거지", "뭘 누른거지");
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }


    //포그라운드와 연결 ( 핸드폰 껐을 때도 돌아가도록 하는 부분)
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                double latitude = Location_Utils.getLatitude(location);
                double longitude = Location_Utils.getLongitude(location);

                String address = getCurrentAddress(latitude, longitude);
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { }


    //GPS를 주소로 변환
    public String getCurrentAddress( double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioException) {

            //네트워크 문제
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

