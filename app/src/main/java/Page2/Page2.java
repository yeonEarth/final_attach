package Page2;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.appbar.AppBarLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import DB.DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1.Page1_1_1;
import Page1.Page2_1;
import Page2_1_1.Page2_1_1;
import Page2_1_1.course;
import Page2_X.NetworkStatus;
import Page2_X.Page2_X_CategoryBottom;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page2 extends AppCompatActivity implements Page2_OnItemClick {

    int station_code = 999;
    String[] arr_line = null;
    String[] _name = new String[station_code];           //txt에서 받은 역이름
    String[] _areaCode = new String[station_code];       //txt에서 받은 지역코드
    String[] _sigunguCode = new String[station_code];    //txt에서 받은 시군구코드
    String[] _x = new String[station_code];              //txt에서 받은 x좌표
    String[] _y = new String[station_code];              //txt에서 받은 y좌표
    String[] _benefitURL = new String[station_code];     //txt에서 받은 혜택url
    String st_name, areaCode, sigunguCode, benefitURL, cityName;            //전달받은 역의 지역코드, 시군구코드, 혜택URL, 도시 이름
    Double x, y;                                         //전달받은 역의 x,y 좌표


    ArrayList<course> items = new ArrayList<>();

    //리사이클러뷰에 연결할 데이터
    List<Recycler_item> cardview_items = new ArrayList<>();
    Page2_CardView_adapter adapter;
    private DbOpenHelper mDbOpenHelper;

    //레이아웃 관련
    AppBarLayout appBarLayout;
    boolean isExpand = false;
    TextView spot_error_txt;
    boolean isLoadData = true;

    //메뉴 관련
    private ImageButton menu_edit;
    private ImageView userImg;
    private TextView userText1;
    private TextView userText2;
    private RecyclerView recyclerView1;
    private Switch positionBtn;
    private Switch alramBtn;
    Main_RecyclerviewAdapter adapter2;
    ArrayList<String> name = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;
    private Context context;

    //기기의 높이를 구한다.
    float d;
    int height;

    Page2 mainActivity;
    private String  subject, station, id;
    private String contentTypeId, cat1, cat2;

    //역 이름을 받아서 지역코드랑 시군구코드 받기 위한 배열
    String returnResult, url;

    String name_1[];  //returnResult를 줄바꿈 단위로 쪼개서 넣은 배열/ name_1[0]에는 한 관광지의 이름,url,contentId,위치가 다 들어가 있다.
    String name_2[] = new String[3];  //name_1를 "  " 단위로 쪼개서 넣은 배열/ [0]= contentID/ [1]=mapx/ [2]에= mapy/ [3]= img_Url/ [4]= name이 들어가 있다.

    //xml 파싱한 값을 분류해서 쪼개 넣음
    String name2[] = new String[station_code];        //관광지 이름
    String img_Url[] = new String[station_code];     //이미지 URL
    String contentid[] = new String[station_code];   //관광지ID
    String sigungucode_arr[] = new String[station_code];   // 시군구 코드
    String areacode_arr[] = new String[station_code];  // area 코드

    //page2 코스 텍스트
    TextView t1, t2, t3, t4, t5, t6, t7, t8;

    TextView subject_title;

    ImageButton logo;

    Button all_cat_btn, schedulePlus_btn,schedulePlus_btn2;

    //page2 코스 더보기
    TextView courseMore;

    LinearLayout courseBox1, courseBox2; // 코스 눌렀을 때

    //관광지 주제별 코스를 저장하는 배열
    String getSubject;
    String[] st1;
    String[] st2;
    String[] st3;
    String[] st4;

    ScrollView scrollView;
    int page = 1;     //api 페이지 수

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        //기기의 높이를 구한다.
        d = Page2.this.getResources().getDisplayMetrics().density;
        Display display = getWindowManager().getDefaultDisplay();  // in Activity
        Point size = new Point();
        display.getRealSize(size);
        height = size.y - (int)(100 * d);

        mDbOpenHelper = new DbOpenHelper(Page2.this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        //scrollView = (ScrollView)findViewById(R.id.page2_scroll);
        //scrollView.smoothScrollBy(0, 0);

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
        adapter2 = new Main_RecyclerviewAdapter(name, context);
        recyclerView1.setAdapter(adapter2);

        //리사이클러뷰 헤더
        name.add("0");
        name.add("1");
        name.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        t1 = (TextView) findViewById(R.id.page2_course_txt1);
        t2 = (TextView) findViewById(R.id.page2_course_txt2);
        t3 = (TextView) findViewById(R.id.page2_course_txt3);
        t4 = (TextView) findViewById(R.id.page2_course_txt4);
        t5 = (TextView) findViewById(R.id.page2_course2_txt1);
        t6 = (TextView) findViewById(R.id.page2_course2_txt2);
        t7 = (TextView) findViewById(R.id.page2_course2_txt3);
        t8 = (TextView) findViewById(R.id.page2_course2_txt4);

        courseBox1 = (LinearLayout) findViewById(R.id.page2_course_1);
        courseBox2 = (LinearLayout) findViewById(R.id.page2_course_2);

        subject_title = (TextView) findViewById(R.id.page2_cat);
        spot_error_txt = (TextView)findViewById(R.id.spot_error_txt2);

        courseMore = (TextView) findViewById(R.id.page2_courseMore);
        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);

        logo = (ImageButton) findViewById(R.id.main_logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page2.this, Page1.Page1.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                //overridePendingTransition(0,0);
                startActivity(intent);

            }
        });



        //위아래로 드래그 했을 때 변화를 감지하는 부분
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {

                    //Log.d("접혔어!!!!", "접혔어!!");
                    if(isExpand){
                        changeVisibility(false, height);
                        isExpand = false;

                    }

                } else if (verticalOffset == 0) {
                    // Log.d("확장됐어!!", "확장쓰!!");
                } else {
                    // Log.d("중간이야!!!", "중간이야!!!!");
                }
            }
        });

        //세로 드래그 문제를 해결하기 위한 부분
        //https://do-dam.tistory.com/entry/CoordinatorLayout-App-Bar-%EB%93%9C%EB%9E%98%EA%B7%B8-%EB%B9%84%ED%99%9C%EC%84%B1%ED%99%94-%EC%83%81%EB%8B%A8-%EC%8A%A4%ED%81%AC%EB%A1%A4-%EA%B5%AC%ED%98%84
        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }


        //텍스트뷰 밑줄
        courseMore.setPaintFlags(courseMore.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        // 카테고리 전체 보기
        all_cat_btn = (Button) findViewById(R.id.page2_cat_btn);
        all_cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page2_1.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        //코스 전체 추가 버튼
        schedulePlus_btn = (Button) findViewById(R.id.page2_schedulePlus_btn_1);
        schedulePlus_btn2 = (Button) findViewById(R.id.page2_schedulePlus_btn_2);

        schedulePlus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList <String> page2_course = new ArrayList<>();
                page2_course.add(t1.getText().toString());
                page2_course.add(t2.getText().toString());
                page2_course.add(t3.getText().toString());
                page2_course.add(t4.getText().toString());
                Intent intent = new Intent(getApplicationContext(), Page3_Main.class);
                intent.putExtra("page2_course", page2_course);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        schedulePlus_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList <String> page2_course2 = new ArrayList<>();
                page2_course2.add(t5.getText().toString());
                page2_course2.add(t6.getText().toString());
                page2_course2.add(t7.getText().toString());
                page2_course2.add(t8.getText().toString());
                Intent intent = new Intent(getApplicationContext(), Page3_Main.class);
                intent.putExtra("page2_course2", (Serializable) page2_course2);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        //앞에서 값을 받아옴
        Intent intent = getIntent();
        // 메인에서 받아올 때
        if (intent.hasExtra("subject_name")) {
            subject = intent.getStringExtra("subject_name");

            //인터넷 유무 체크
            int isNetworkConnect = NetworkStatus.getConnectivityStatus(getApplicationContext());

            if(isNetworkConnect == 3) {
                spot_error_txt.setVisibility(View.VISIBLE);
                Log.d("ddd","ddd");
            }
            //url에 들어갈 contentTypeId, cat1, cat2 코드를 찾기
            else {
                //url에 들어갈 contentTypeId, cat1, cat2 코드를 찾기
                url_code();
                settingList();
                settingAPI_Data();

            }


        } else {
            // 카테고리에서 받아올 때
            subject = intent.getStringExtra("subject_name_from_Page2_1");
            int isNetworkConnect = NetworkStatus.getConnectivityStatus(getApplicationContext());

            if(isNetworkConnect == 3) {
                spot_error_txt.setVisibility(View.VISIBLE);
            }
            else {
                url_code();
                settingAPI_Data();
                //url에 들어갈 contentTypeId, cat1, cat2 코드를 찾기

            }
        }

        Toast.makeText(getApplicationContext(), getSubject, Toast.LENGTH_SHORT).show();

        getData();

        //리사이클러뷰 구현 부분
        RecyclerView recyclerView = findViewById(R.id.page2_recyclerview);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                //아이템들은 한 칸씩 차지
//                return 1;
//            }
//        });

        recyclerView.setLayoutManager( gridLayoutManager);
        recyclerView.setHasFixedSize(true);


        //리사이클러뷰 연결
        adapter = new Page2_CardView_adapter(cardview_items, mainActivity, "cityname", this);
        recyclerView.setAdapter(adapter);

        courseMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), subject, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Page2.this, Page2_1_1.class);
                intent.putExtra("subject_title", subject);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 아이템 뷰 선택했을 때
        // 첫번째 코스 선택 시
        courseBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page2_1_1.class);
                intent.putExtra("position", 0);
                intent.putExtra("subject_title", subject);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // 두 번째 코스 선택 시
        courseBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page2_1_1.class);
                intent.putExtra("position", 1);
                intent.putExtra("subject_title", subject);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        //more loading
        final NestedScrollView nestedScrollView = (NestedScrollView)findViewById(R.id.nestScrollView2);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int  visibleItemCount,  totalItemCount, pastVisiblesItems;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(v.getChildAt(v.getChildCount() -1) != null) {
                    if( (scrollY >= (v.getChildAt(v.getChildCount() -1).getMeasuredHeight() -  v.getMeasuredHeight() )) && scrollY > oldScrollY) {

                        visibleItemCount = gridLayoutManager.getChildCount();
                        totalItemCount = gridLayoutManager.getItemCount() ;
                        pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                        //받아온 api 개수가 20개가 안되면 다음 페이지가 없다고 판단. false로 바꿔줌
                        if(name_1.length < 5){
                            isLoadData = false;
                        }

                        //isLoadData가 true이면
                        if(isLoadData) {
                            if( (visibleItemCount + pastVisiblesItems) >= totalItemCount ){

                                page++;

                                //관광 api 연결 부분
                                settingAPI_Data();

                                //메시지 갱신 위치
                                adapter.notifyDataSetChanged();

                            }
                        }

                        //데이터가 더 없을 때
                        else {
                            noData_Dialog();
                        }
                    }
                }
            }
        });



    }

    //txt 돌려 역 비교할 배열 만들기(이름 지역코드 동네코드)<-로 구성
    private void settingList(){
        String readStr = "";
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;
        try{
            inputStream = assetManager.open("station_code.txt");
            //버퍼리더에 대한 설명 참고 : https://coding-factory.tistory.com/251
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr_all = readStr.split("\n"); //txt 내용을 줄바꿈 기준으로 나눈다.

        //한 줄의 값을 띄어쓰기 기준으로 나눠서, 역명/지역코드/시군구코드 배열에 넣는다.
        for(int i=0; i <arr_all.length; i++) {
            arr_line = arr_all[i].split(" ");

            _name[i] = arr_line[0];         //서울
            _areaCode[i] = arr_line[1];     //1
            _sigunguCode[i] = arr_line[2];  //0
            _y[i] = arr_line[3];            //y좌표
            _x[i] = arr_line[4];            //x좌표
            _benefitURL[i] = arr_line[5];
        }
    }

    private String compareStation(String area, String sigunguCode){
        Log.i("맞냐?? ", area + "/" + sigunguCode);
        for(int p = 0; p < 234;p ++){
            if(_sigunguCode[p].trim().equals(sigunguCode.trim()) && _areaCode[p].trim().equals(area.trim())){
                Log.i("맞냐 ", "oo");
                cityName = _name[p];
            }
        }
        return cityName;
    }



    private void settingAPI_Data() {
        SearchTask task = new SearchTask();
        try {
            String RESULT = task.execute().get();
            Log.i("전달 받은 값", RESULT);

            if(RESULT.length() != 0) {

                //사진링크, 타이틀(관광명), 분야뭔지 분리
                name_1 = RESULT.split("\n");

                for (int i = 0; i < name_1.length; i++) {
                    name_2 = name_1[i].split("  ");

                    //img_Url이 없는 경우도 있기 때문에, length = 3 = 있음/ 2 = 없음
                    if (name_2.length == 5) {
                        areacode_arr[i] = name_2[0];
                        contentid[i] = name_2[1];
                        img_Url[i] = name_2[2];
                        sigungucode_arr[i] = name_2[3];
                        name2[i] = name_2[4];
                    } else {
                        areacode_arr[i] = name_2[0];
                        contentid[i] = name_2[1];
                        img_Url[i] = null;
                        sigungucode_arr[i] = name_2[2];
                        name2[i] = name_2[3];
                    }
                }

                //리사이클러에 들어갈 데이터를 넣는다
                for (int i = 0; i < name_1.length; i++) {
                    cardview_items.add(new Recycler_item(img_Url[i], name2[i], contentid[i], subject, areacode_arr[i], sigungucode_arr[i]));
                }
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }

    }

    //화면을 생성할때 부드럽게 주기위한 애니메이션 함수
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeVisibility(final boolean isExpanded, int height) {

        // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
        float d = Page2.this.getResources().getDisplayMetrics().density;
        final int applayout_height = (int) (445 * d);
        final int map_height = (int) (277 * d);

        final ValueAnimator va = isExpanded ? ValueAnimator.ofInt(applayout_height, height) : ValueAnimator.ofInt(height, applayout_height);
        va.setDuration(600);   // Animation이 실행되는 시간, n/1000초
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (int) animation.getAnimatedValue();                // value는 height 값
                appBarLayout.getLayoutParams().height = value;
                appBarLayout.requestLayout();

            }
        });

        final ValueAnimator va2 = isExpanded ? ValueAnimator.ofInt(applayout_height, height) : ValueAnimator.ofInt(height, map_height);
        va2.setDuration(600);   // Animation이 실행되는 시간, n/1000초
        va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (int) animation.getAnimatedValue();                // value는 height 값


            }
        });

        va2.start();
        va.start();
    }

    private void getData () {
        switch (subject) {
            case "자연":
                st1 = new String[]{"횡성", "여수", "제천"};
                st2 = new String[]{"강릉", "남원", "경주"};
                st3 = new String[]{"동해", "임실", "포항"};
                st4 = new String[]{"삼척", "군산", "영덕"};
                break;
            case "역사":
                st1 = new String[]{"광주", "전주", "나주", "경주", "제천"};
                st2 = new String[]{"김제", "남원", "보성", "안동", "충주"};
                st3 = new String[]{"익산", "곡성", "순천", "영주", "청주"};
                st4 = new String[]{"군산", "임실", "화순", "서울", "천안"};
                break;
            case "휴양":
                st1 = new String[]{"아산", "동해", "포항"};
                st2 = new String[]{"보령", "동두천", "부산"};
                st3 = new String[]{"정읍", "서울", "아산"};
                st4 = new String[]{"여수", "고양", "서울"};
                break;
            case "체험":
                st1 = new String[]{"익산", "곡성", "의정부"};
                st2 = new String[]{"보성", "순천", "양평"};
                st3 = new String[]{"광양", "광양", "강릉"};
                st4 = new String[]{"순천", "논산", "보성"};
                break;
            case "산업":
                st1 = new String[]{"서울","김제"};
                st2 = new String[]{"인천","광양"};
                st3 = new String[]{"대전","창원"};
                st4 = new String[]{"대구","부전"};
                break;
            case "건축/조형":
                st1 = new String[]{"서울", "인천"};
                st2 = new String[]{"인천", "서울"};
                st3 = new String[]{"군산", "가평"};
                st4 = new String[]{"부산", "춘천"};
                break;
            case "문화":
                st1 = new String[]{"목포", "수원"};
                st2 = new String[]{"광주", "천안"};
                st3 = new String[]{"공주", "대전"};
                st4 = new String[]{"청주", "대구"};
                break;
            case "레포츠":
                st1 = new String[]{"남양주", "서울"};
                st2 = new String[]{"평창", "남양주"};
                st3 = new String[]{"횡성", "광명"};
                st4 = new String[]{"성남", "가평"};
                break;
            default:
                break;
        }

        if (st1.length != 0) {
            for (int i = 0; i < st1.length; i++) {
                items.add(new course(getSubject, st1[i], st2[i], st3[i], st4[i]));
            }
        }

        subject_title.setText("#" + subject);

        t1.setText(items.get(0).getSt1());
        t2.setText(items.get(0).getSt2());
        t3.setText(items.get(0).getSt3());
        t4.setText(items.get(0).getSt4());
        t5.setText(items.get(1).getSt1());
        t6.setText(items.get(1).getSt2());
        t7.setText(items.get(1).getSt3());
        t8.setText(items.get(1).getSt4());
    }

    //URL에 들어갈 contentTypeId, cat1, cat2를 지정하는 작업
    private void url_code() {
        switch (subject) {
            case "자연":
                contentTypeId = "12";
                cat1 = "A01";
                cat2 = "";
                break;
            case "역사":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0201";
                break;
            case "휴양":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0202";
                break;
            case "체험":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0203";
                break;
            case "산업":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0204";
                break;
            case "건축/조형":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0205";
                break;
            case "문화":
                contentTypeId = "14";
                cat1 = "A02";
                cat2 = "A0206";
                break;
            case "레포츠":
                contentTypeId = "28";
                cat1 = "A03";
                cat2 = "";
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(double x, double y, String name) {

    }

    @Override
    public void make_db(String countId, String name, String cityname, String type, String image, String click, String areaCode, String sigunguCode) {
        cityname = compareStation(areaCode, sigunguCode);
        mDbOpenHelper.open();
        Log.d("???","");
        mDbOpenHelper.insertColumn(countId, name, cityname, type, image, click);
        mDbOpenHelper.close();
    }

    @Override
    public String isClick(String countid) {
        mDbOpenHelper.open();
        Cursor iCursor = mDbOpenHelper.selectIdCulumns(countid);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());

        while (iCursor.moveToNext()) {
            String userId = iCursor.getString(iCursor.getColumnIndex("userid"));

            id = userId;
        }
        mDbOpenHelper.close();

        return id;
    }



    @Override
    public void delete_db(String contentId) {
        mDbOpenHelper.open();
        mDbOpenHelper.deleteColumnByContentID(contentId);
        mDbOpenHelper.close();

        delete_dialog();
    }

    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Page2.this);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(Page2.this, Page1_1_1.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text) {

    }

    public void delete_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Page2.this);
        builder.setTitle("관심관광지 삭제 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(Page2.this, Page1_1_1.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //로딩할 데이터가 더이상 없을 때
    public void noData_Dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("마지막 데이터 입니다.");
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    //관광api 연결
    class SearchTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            //초기화 단계에서 사용
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            //Log.d("시작", "시작");

            url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&pageNo=" + page+
                    "&numOfRows=5&MobileApp=AppTest&MobileOS=ETC&arrange=B" +
                    "&contentTypeId=" + contentTypeId +
                    "&sigunguCode=" +
                    "&areaCode="+
                    "&cat1=" + cat1 + "&cat2=" + cat2 + "&cat3=" +
                    "&listYN=Y";


            URL xmlUrl;
            returnResult = "";

            try {
                boolean title = false;
                boolean firstimage = false;
                boolean item = false;
                boolean contentid = false;
                boolean sigungucode = false;
                boolean areacode = false;

                xmlUrl = new URL(url);
                Log.d("url", url);
                xmlUrl.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("contentid")) {
                                contentid = true;
                                // Log.d("태그 시작", "태그 시작2");
                            }
                            if (parser.getName().equals("firstimage")) {
                                firstimage = true;
                                // Log.d("태그 시작", "태그 시작3");
                            }
                            if (parser.getName().equals("title")) {
                                title = true;
                                //Log.d("태그 시작", "태그 시작4");
                            }
                            if (parser.getName().equals("sigungucode")) {
                                Log.i("시군구코드", "시군구태그시작");
                                sigungucode = true;
                            }
                            if (parser.getName().equals("areacode")) {
                                Log.i("지역코드", "지역코드태그시작");
                                areacode = true;
                            }

                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (contentid) {
                                returnResult += parser.getText() + "  ";
                                contentid = false;
                            }
                            if (firstimage) {
                                returnResult += parser.getText() + "  ";
                                firstimage = false;
                            }
                            if (sigungucode) {
                                returnResult += parser.getText() + "  ";
                                Log.i("시군구코드", parser.getText());
                                sigungucode = false;
                            }
                            if (areacode) {
                                returnResult += parser.getText() + "  ";
                                Log.i("지역코드", parser.getText());
                                areacode = false;
                            }
                            if (title) {
                                returnResult += parser.getText() + "\n";
                                //Log.d("태그 받음", "태그받음4");
                                title = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("err", "erro");
            }
            return returnResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
