package Page1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;

import DB.DbOpenHelper;
import DB.Heart_page;
import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import DB.Page3_DbOpenHelper;
import Page1_schedule.LocationUpdatesService;
import Page1_schedule.Location_Utils;
import Page2_X.Page2_X;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hansol.spot_200510_hs.Page0;
import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.appbar.AppBarLayout;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import Page2_X.Page2_X_Main;
import Page2.Page2;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1 extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    // 부모 뷰
    private Context context;
    private LinearLayout container;
    private LinearLayout container2;
    private LinearLayout city_container;

    //툴바 관련
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;
    boolean isExpand = false;


    //메뉴 관련
    private ImageButton menu_edit;
    private ImageView userImg;
    private TextView userText1;
    private TextView userText2;
    private RecyclerView recyclerView1;
    private Switch positionBtn;
    private Switch alramBtn;
    Main_RecyclerviewAdapter adapter;
    ArrayList<String> name = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;

    //위치서비스 관련
    private MyReceiver myReceiver;
    private boolean mBound = false;
    private LocationUpdatesService mService = null;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
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

    private Menu_DbOpenHelper menu_dbOpenHelper;
    private List<String> onoff = new ArrayList<>();


    String cat_text = null;   // 카테고리 이름
    String cat_text2 = null;
    String cat_text3 = null;
    String cat_text4 = null;

    ImageButton main_schedule;
    ImageButton main_register;
    ImageButton main_like;

    int[] score = new int[8];
    // 도시 저장할 어레이리스트
    ArrayList<String> cityList = new ArrayList<String>();   // 모든 데이터 다들어감
    ArrayList<String> real_cityList = new ArrayList<String>();  // 중복제거 찐도시 리스트

    // 사진 저장할 어레이리스트
    ArrayList<Integer> cityPicture = new ArrayList<Integer>();  // 모든 데이터 다 들어감
    ArrayList<Integer> real_cityPicture = new ArrayList<Integer>();     // 중복제거 찐 도시사진 리스트

    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();

    private Page3_DbOpenHelper page3_dbOpenHelper;
    private DbOpenHelper mDbOpenHelper;
    String sort = "userid";

    private Like_DbOpenHelper mLikeDpOpenHelper;    // 취향파악 부분
    String like;
    String mScore[] = new String[8];

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //객체 연결
        setContentView(R.layout.activity_page1_drawer);
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch)findViewById(R.id.menu_postion_btn);
        alramBtn = (Switch)findViewById(R.id.menu_alram_btn);
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
        adapter = new Main_RecyclerviewAdapter(name, context);
        recyclerView1.setAdapter(adapter);

        //리사이클러뷰 헤더
        name.add("0");
        name.add("1");
        name.add("2");

        // DB열기
        menu_dbOpenHelper = new Menu_DbOpenHelper(this);
        menu_dbOpenHelper.open();
        menu_dbOpenHelper.create();
        notity_listner("");



        //위치 스위치 관련
        myReceiver = new MyReceiver();
        setButtonsState(Location_Utils.requestingLocationUpdates(this));
        positionBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {
                    mService.removeLocationUpdates();
                }
            }
        });




        //알림 스위치 버튼
        setButtonsState_notity();
        alramBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    menu_dbOpenHelper.open();
                    menu_dbOpenHelper.deleteAllColumns();
                    menu_dbOpenHelper.insertColumn("true", "0");
                    //  menu_dbOpenHelper.close();

                }else {
                    menu_dbOpenHelper.open();
                    menu_dbOpenHelper.deleteAllColumns();
                    menu_dbOpenHelper.insertColumn("false", "0");
                    //  menu_dbOpenHelper.close();
                }
            }
        });



        //백그라운드에서 위치가 다 돌았을 때 화면 켜지고 연결 해제
        final Intent intent = getIntent();
        String key =  intent.getStringExtra("key");
        if(key != null){
            Log.i("받음?", key);
            Handler handler  = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mService.removeLocationUpdates();
                }
            }, 500);
        }


        page3_dbOpenHelper = new Page3_DbOpenHelper(this);
        page3_dbOpenHelper.open();
        page3_dbOpenHelper.create();



        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        scrollView = (NestedScrollView) findViewById(R.id.nestScrollView_page1);



        //위아래로 드래그 했을 때 변화를 감지하는 부분
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            int oldY = 0;
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // For ScrollView
                Log.i("X-Y", "-" + scrollY);
                if(scrollY > oldY){
                    Log.i("아래로 드래그", "^^");
                    oldY = scrollY;
                } else {
                    Log.i("위로 드래그", "^^");
                    oldY = scrollY;
                }
                // DO SOMETHING WITH THE SCROLL COORDINATES
            }
        });

        SharedPreferences preferences =getSharedPreferences("a", MODE_PRIVATE);
        int firstviewShow = preferences.getInt("First", 0);

        // 1이 아니라면 취향파악페이지 보여주기 = 처음 실행이라면
        if (firstviewShow != 1) {
            Intent intent2 = new Intent(Page1.this, Page0.class);
            startActivity(intent2);
        }

        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();


        // 취향파악 DB열기
        mLikeDpOpenHelper = new Like_DbOpenHelper(this);
        mLikeDpOpenHelper.open();
        mLikeDpOpenHelper.create();

        showDatabase(sort);
        showLikeDB();

        // DB에 값이 있다면
        if (like != null) {
            // mScore에 일단 값을 쪼개서 저장하고
            mScore = like.split(" ");
//            Log.i("mScore", like);
            for (int i = 0 ; i < mScore.length ; i++) {
//                Log.i("mScore", mScore[i]);
                score[i] = Integer.parseInt(mScore[i]); // Int로 캐스팅
//                Log.i("score", String.valueOf(score[i]));
            }
        } else {
            final Intent intent2 = getIntent();
            // 나중에 하기 버튼 눌렀을 때 임의의 값 넘겨주기
            if (intent.hasExtra("Main")) {
                score = intent.getIntArrayExtra("Main");
            } else if (intent.hasExtra("Page9")) {
                // 설문조사 진행 했을 때
                score = intent.getIntArrayExtra("Page9");
            } else {
                // 기본 임의 값
                score[1] = 3;
                score[4] = 1;
                score[5] = 0;
            }
        }


        main_schedule = (ImageButton) findViewById(R.id.main_schedule);
        main_register = (ImageButton) findViewById(R.id.main_register);
        main_like = (ImageButton) findViewById(R.id.main_spot);


        // 메인 스케줄 버튼 눌렀을 때
        main_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_databese();

            }
        });

        // 일정등록 버튼 눌렀을 때-> 도시검색으로 바뀜
        main_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),Page2_X.class);
                intent2.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent2.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
            }
        });

        // 찜한 관광지 눌렀을 때
        main_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent3 = new Intent(getApplicationContext(), Page1_1_1.class);
//                startActivity(intent3);
                if (mySpot.size() == 0) {
                    Intent intent3 = new Intent(getApplicationContext(), Page1_1_0.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent3.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                } else {
                    Intent intent3 = new Intent(getApplicationContext(), Page1_1_1.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent3.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);
                }
            }
        });



        // height값 dp로 지정해서 넣고 싶을 때
        int dpValue = 96;
        int dpValue2 = 15;
        final Context context = getApplicationContext();
        float d = context.getResources().getDisplayMetrics().density;
        int height = (int) (dpValue * d);
        int city_padding = (int) (dpValue2 * d);
        int city_margin = (int) (16 * d);
        int cat_margin = (int) (8 * d);

        // 카테고리 노출 기준
        // 2번
        if (score[1] == 0) {
            cat_text = "체험";   // 카테고리에 넣고
            cityList.add("서울"); // 도시 리스트에 추가하기
            cityList.add("부산");
            cityPicture.add(R.drawable.seoul);  // 사진 리스트에 추가
            cityPicture.add(R.drawable.busan);
        } else if (score[1] == 1) {
            cat_text = "자연";
            cityList.add("가평");
            cityList.add("강릉");
            cityPicture.add(R.drawable.gapyeong);
            cityPicture.add(R.drawable.gangneung);
        } else if (score[1] == 2) {
            cat_text = "음식점";
            cityList.add("광주");
            cityList.add("대구");
            cityPicture.add(R.drawable.gwangju);
            cityPicture.add(R.drawable.daegu);
        } else if (score[1] == 3) {
            cat_text = "역사";
            cityList.add("서울");
            cityList.add("경주");
            cityPicture.add(R.drawable.seoul);
            cityPicture.add(R.drawable.gyeongju);
        } else if (score[1] == 4) {
            cityList.add("서울");
            cityList.add("부산");
            cityList.add("강릉");
            cityList.add("순천");
            cityList.add("전주");
            cityList.add("여수");
            cityPicture.add(R.drawable.seoul);
            cityPicture.add(R.drawable.busan);
            cityPicture.add(R.drawable.gangneung);
            cityPicture.add(R.drawable.suncheon);
            cityPicture.add(R.drawable.jeonju);
            cityPicture.add(R.drawable.yeosu);
        }
        // 5번
        if (score[4] == 0) {
            cat_text2 = "건축/조형";
            cityList.add("서울");
            cityList.add("부산");
            cityPicture.add(R.drawable.seoul);
            cityPicture.add(R.drawable.busan);
        } else if (score[4] == 1) {
            cat_text2 = "체험";
            cityList.add("서울");
            cityList.add("부산");
            cityPicture.add(R.drawable.seoul);
            cityPicture.add(R.drawable.busan);
        } else if (score[4] == 2) {
            cat_text2 = "건축/조형";
            cat_text3 = "체험";
            cityList.add("서울");
            cityList.add("부산");
            cityPicture.add(R.drawable.seoul);
            cityPicture.add(R.drawable.busan);
        }
        // 6번
        if (score[5] == 0) {
            cat_text4 = "레포츠";
            cityList.add("가평");
            cityList.add("단양");
            cityPicture.add(R.drawable.gapyeong);
            cityPicture.add(R.drawable.danyang);
        } else if (score[5] == 1) {
            cat_text4 = "휴양";
            cityList.add("울산");
            cityList.add("평창");
            cityPicture.add(R.drawable.ulsan);
            cityPicture.add(R.drawable.pyeonchang);
        }

        // !!!!! 취향 카테고리 동적 생성 !!!!!
        // 부모 뷰 1 -> 첫 번째 줄
        container = (LinearLayout) findViewById(R.id.course_layout2);
        container2 = (LinearLayout) findViewById(R.id.course_layout3);  // 부모 뷰2 -> 두 번째 줄

        // width, height, gravity설정
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        //params.gravity = Gravity.CENTER_VERTICAL;
        params.weight = 1;
        params.rightMargin = cat_margin;

        // 버튼 동적 생성
        // 2번
        if (cat_text != null) {
            Button cat1 = new Button(this);
            cat1.setOnClickListener(this);
            cat1.setId(R.id.page2_cat1);
            cat1.setText("#" + cat_text);
            cat1.setTextSize(16);
            cat1.setTextColor(Color.BLACK);
            cat1.setLayoutParams(params);
            cat1.setGravity(Gravity.CENTER_VERTICAL);
            cat1.setPadding(city_margin,0,0,0);
            cat1.setHeight(46);
            cat1.setBackgroundResource(R.drawable.box_white);
            container.addView(cat1);    // 부모뷰에 추가

            // 5번
            if (cat_text2 != null) {
                // 문화 겹치면 안뜸
                if (cat_text != cat_text2) {
                    Button cat2 = new Button(this);
                    cat2.setOnClickListener(this);
                    cat2.setId(R.id.page2_cat2);
                    cat2.setText("#" + cat_text2);
                    cat2.setTextSize(16);
                    cat2.setTextColor(Color.BLACK);
                    cat2.setLayoutParams(params);
                    cat2.setBackgroundResource(R.drawable.box_white);
                    cat2.setHeight(46);
                    cat2.setGravity(Gravity.CENTER_VERTICAL);
                    cat2.setPadding(city_margin,0,0,0);
                    container.addView(cat2);    // 부모뷰에 추가
                }
                if (cat_text != cat_text3) {
                    if (cat_text3 != null) {
                        // cat_text에 값이 있고 cat_text3에 값이 있을 때
                        Button cat3 = new Button(this);
                        cat3.setOnClickListener(this);
                        cat3.setId(R.id.page2_cat3);
                        cat3.setText("#" + cat_text3);
                        cat3.setTextSize(16);
                        cat3.setTextColor(Color.BLACK);
                        cat3.setLayoutParams(params);
                        cat3.setBackgroundResource(R.drawable.box_white);
                        cat3.setHeight(46);
                        cat3.setGravity(Gravity.CENTER_VERTICAL);
                        cat3.setPadding(city_margin,0,0,0);
                        container2.addView(cat3);    // 부모뷰2에 추가
                    }
                }
            }
        } else { // 2번에 5번 선택했을 때
            if (cat_text2 != null) {
                Button cat2 = new Button(this);
                cat2.setOnClickListener(this);
                cat2.setId(R.id.page2_cat2);
                cat2.setText("#" + cat_text2);
                cat2.setTextSize(16);
                cat2.setTextColor(Color.BLACK);
                cat2.setLayoutParams(params);
                cat2.setBackgroundResource(R.drawable.box_white);
                cat2.setHeight(46);
                cat2.setGravity(Gravity.CENTER_VERTICAL);
                cat2.setPadding(city_margin,0,0,0);
                container.addView(cat2);    // 부모뷰에 추가
            }
            if (cat_text3 != null) {
                Button cat3 = new Button(this);
                cat3.setOnClickListener(this);
                cat3.setId(R.id.page2_cat3);
                cat3.setText("#" + cat_text3);
                cat3.setTextSize(16);
                cat3.setTextColor(Color.BLACK);
                cat3.setLayoutParams(params);
                cat3.setBackgroundResource(R.drawable.box_white);
                cat3.setHeight(46);
                cat3.setGravity(Gravity.CENTER_VERTICAL);
                cat3.setPadding(city_margin,0,0,0);
                container.addView(cat3);    // 부모뷰2에 추가
            }
        }
        // 6번
        // 첫번째 부모뷰에 들어가는 경우
        if (cat_text == null && cat_text3 == null) {
            if (cat_text4 != null) {
                Button cat4 = new Button(this);
                cat4.setOnClickListener(this);
                cat4.setId(R.id.page2_cat4);
                cat4.setText("#" + cat_text4);
                cat4.setTextSize(16);
                cat4.setTextColor(Color.BLACK);
                cat4.setLayoutParams(params);
                cat4.setBackgroundResource(R.drawable.box_white);
                cat4.setHeight(46);
                cat4.setGravity(Gravity.CENTER_VERTICAL);
                cat4.setPadding(city_margin,0,0,0);
                container.addView(cat4);    // 부모뷰에 추가
            }
        } else if (cat_text == cat_text2) {
            Button cat4 = new Button(this);
            cat4.setOnClickListener(this);
            cat4.setId(R.id.page2_cat4);
            cat4.setText("#" + cat_text4);
            cat4.setTextSize(16);
            cat4.setTextColor(Color.BLACK);
            cat4.setLayoutParams(params);
            cat4.setBackgroundResource(R.drawable.box_white);
            cat4.setHeight(46);
            cat4.setGravity(Gravity.CENTER_VERTICAL);
            cat4.setPadding(city_margin,0,0,0);
            container.addView(cat4);    // 부모뷰에 추가
        }
        // 두 번째 부모뷰에 들어가는 경우
        else if (cat_text != null || cat_text3 != null) {
            if (cat_text4 != null) {
                Button cat4 = new Button(this);
                cat4.setOnClickListener(this);
                cat4.setId(R.id.page2_cat4);
                cat4.setText("#" + cat_text4);
                cat4.setTextSize(16);
                cat4.setTextColor(Color.BLACK);
                cat4.setLayoutParams(params);
                cat4.setBackgroundResource(R.drawable.box_white);
                cat4.setHeight(46);
                cat4.setGravity(Gravity.CENTER_VERTICAL);
                cat4.setPadding(city_margin,0,0,0);
                container2.addView(cat4);    // 부모뷰에 추가

                if (cat_text != null && cat_text2 != null && cat_text3 != null && cat_text4 != null) {

                } else {
                    // 빈 버튼 만들기
                    Button cat3_1 = new Button(this);
                    cat3_1.setVisibility(View.INVISIBLE);
                    cat3_1.setLayoutParams(params);
                    container2.addView(cat3_1);
                }
                if (cat_text != cat_text3) {

                } else {
                    // 빈 버튼 만들기
                    Button cat3_1 = new Button(this);
                    cat3_1.setVisibility(View.INVISIBLE);
                    cat3_1.setLayoutParams(params);
                    container2.addView(cat3_1);
                }
            }
        }

        // !!!!! 도시 동적 생성 !!!!!
        // 부모 뷰
        city_container = (LinearLayout) findViewById(R.id.main_city_layout);

        // width, height, gravity설정
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(height, height);
        //params.gravity = Gravity.CENTER_VERTICAL;
        params2.weight = 1;
        params2.rightMargin = city_margin;


        // 리스트 속 중복 제거 하나하나 비교
        for (int j = 0 ; j < cityList.size() ; j++) {
            if (!real_cityList.contains(cityList.get(j))) {
                real_cityList.add(cityList.get(j).toString());
            }
        }

        // 사진 리스트 속 중복 제거
        for (int i = 0 ; i < cityPicture.size() ; i++) {
            if (!real_cityPicture.contains(cityPicture.get(i))) {
                real_cityPicture.add(cityPicture.get(i));
            }
        }

        for (int i = 0 ; i < real_cityList.size() ; i++) {
            TextView cityName = new TextView(this);
            cityName.setHeight(height);
            cityName.setWidth(height);

            // 이름 지정 및 가져오기
            final City city = new City();
            city.setCityName(real_cityList.get(i).toString());

            cityName.setText(city.getCityName());
            cityName.isClickable();

            cityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 도시 버튼 눌렸을 때
                    Intent cityIntent = new Intent(Page1.this.getApplicationContext(), Page2_X_Main.class);
                    cityIntent.putExtra("Page1_stName", city.getCityName());
                    cityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    cityIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(cityIntent);
                }
            });

            // 백그라운드 사진 가져오기
            city.setCityPic(real_cityPicture.get(i));
            cityName.setBackgroundResource(city.getCityPic());

            Typeface typeface = getResources().getFont(R.font.notosans_bold);
            cityName.setTypeface(typeface);

            cityName.setTextSize(12);
            cityName.setPadding(city_padding,city_padding,city_padding,city_padding);
            cityName.setTextColor(Color.WHITE);
            cityName.setTypeface(cityName.getTypeface(), Typeface.BOLD);
            cityName.setShadowLayer(5, 0, 0, Color.DKGRAY);

//            cityName.setBackgroundResource(R.drawable.round_white);
            cityName.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            cityName.setLayoutParams(params2);
            city_container.addView(cityName);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), Page2.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);

        // 버튼 별 카테고리 이름 넘기기
        if (view.getId() == R.id.page2_cat1) {
            intent.putExtra("subject_name", cat_text);
            Toast.makeText(getApplicationContext(), "1" + cat_text, Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.page2_cat2) {
            intent.putExtra("subject_name", cat_text2);
            Toast.makeText(getApplicationContext(), "2" + cat_text2, Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.page2_cat3) {
            intent.putExtra("subject_name", cat_text3);
            Toast.makeText(getApplicationContext(), "3" + cat_text3, Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.page2_cat4) {
            intent.putExtra("subject_name", cat_text4);
            Toast.makeText(getApplicationContext(), "4" + cat_text4, Toast.LENGTH_SHORT).show();
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public class City {
        private String cityName;
        private int cityPic;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getCityPic() {
            return cityPic;
        }

        public void setCityPic(int cityPic) {
            this.cityPic = cityPic;
        }
    }

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            tempName = setTextLength(tempName,10);

            mySpot.add(tempName);
        }
    }

    public void showLikeDB() {
        Cursor likeCursor = mLikeDpOpenHelper.selectColumns();
        Log.d("showLikeDatabase", "DB Size : " + likeCursor.getCount());

        while (likeCursor.moveToNext()) {
            String tempLike = likeCursor.getString(likeCursor.getColumnIndex("like"));
            like = tempLike;
        }
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }

    // 현재 액티비티 새로고침
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }




    /*위치&알림 스위치 버튼 관련***********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }



    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    //위치 스위치 상태
    private void setButtonsState(boolean requestingLocationUpdates ) {
        if (requestingLocationUpdates) {
            positionBtn.setChecked(true);
        } else if( !requestingLocationUpdates){
            positionBtn.setChecked(false);
        }
    }



    //알림 스위치 상태
    private void setButtonsState_notity() {
        if (onoff.get(0).equals("true")) {
            alramBtn.setChecked(true);
        } else {
            alramBtn.setChecked(false);
        }

    }



    //포그라운드와 연결 ( 핸드폰 껐을 때도 돌아가도록 하는 부분)
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
            }
        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    public void notity_listner(String sort){
        Cursor iCursor = menu_dbOpenHelper.selectColumns();

        while(iCursor.moveToNext()){
            String  id = iCursor.getString(iCursor.getColumnIndex("userid"));
            Log.i("갑자기 왜 안돼", String.valueOf(iCursor.getCount()) + "/" + id);
            onoff.add(id);
        }

        //최초 실행을 위함
        if(iCursor.getCount() == 0){
            menu_dbOpenHelper.insertColumn("true", "0");
            onoff.add("true");
        }
    }

    //데베에 값이 있는지 검사
    void get_databese(){
        Cursor cursor = page3_dbOpenHelper.selectColumns();
        int size = cursor.getCount();

        if(size > 0){
            db_message();
        }

        else{
            Intent scheduleIntent = new Intent(getApplicationContext(), Page3_Main.class);
            scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(scheduleIntent);
        }
    }


    //데베에 값이 있으면 다이얼로그 띄움
    void db_message() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        setTheme(R.style.AlertDialogStyle);
        builder.setTitle("안내");
        builder.setMessage("기존에 입력했던 정보가 있습니다. 불러올까요?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String daypassFromDB = null;
                ArrayList<String> course = new ArrayList<>();

                //데베에서 값을 받아서 뿌려줌
                Cursor cursor = page3_dbOpenHelper.selectColumns();
                while (cursor.moveToNext()) {
                    String tempDaypass = cursor.getString(cursor.getColumnIndex("daypass"));
                    String tempStation = cursor.getString(cursor.getColumnIndex("station"));
                    daypassFromDB = tempDaypass;
                    String station_splite[] = tempStation.split(",");

                    for (int p = 1; p < station_splite.length; p++) {
                        course.add(station_splite[p]);
                    }
                }

                Intent scheduleIntent = new Intent(getApplicationContext(), Page3_Main.class);
                scheduleIntent.putExtra("daypassFromDB", daypassFromDB);
                scheduleIntent.putExtra("stationFromDB", (Serializable) course);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                page3_dbOpenHelper.deleteAllColumns();
                Intent scheduleIntent = new Intent(getApplicationContext(), Page3_Main.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);
            }
        });
        builder.show();
    }


}

