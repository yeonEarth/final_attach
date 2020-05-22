package Page1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;

import DB.DbOpenHelper;
import DB.Heart_page;
import Page2_X.Page2_X;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import Page2_X.Page2_X_Main;
import Page2.Page2;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1 extends AppCompatActivity implements View.OnClickListener {

    // 부모 뷰
    private LinearLayout container;
    private LinearLayout container2;
    private LinearLayout city_container;

    //툴바 관련
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;
    boolean isExpand = false;

    String cat_text = null;   // 카테고리 이름
    String cat_text2 = null;
    String cat_text3 = null;
    String cat_text4 = null;

    ImageButton main_schedule;
    ImageButton main_register;
    ImageButton main_like;
    TextView search_spot;

    int[] score = new int[8];
    // 도시 저장할 어레이리스트
    ArrayList<String> cityList = new ArrayList<String>();   // 모든 데이터 다들어감
    ArrayList<String> real_cityList = new ArrayList<String>();  // 중복제거 찐도시 리스트

    // 사진 저장할 어레이리스트
    ArrayList<Integer> cityPicture = new ArrayList<Integer>();  // 모든 데이터 다 들어감
    ArrayList<Integer> real_cityPicture = new ArrayList<Integer>();     // 중복제거 찐 도시사진 리스트

    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();

    private DbOpenHelper mDbOpenHelper;
    String sort = "userid";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        scrollView = (NestedScrollView) findViewById(R.id.nestScrollView_page1);
        setSupportActionBar(toolbar);

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

        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);

        final Intent intent = getIntent();
        // 나중에 하기 버튼 눌렀을 때 임의의 값 넘겨주기
        if (intent.hasExtra("Main")) {
            score = intent.getIntArrayExtra("Main");
        } else {
            // 설문조사 진행 했을 때
            score = intent.getIntArrayExtra("Page9");
        }

        main_schedule = (ImageButton) findViewById(R.id.main_schedule);
        main_register = (ImageButton) findViewById(R.id.main_register);
        main_like = (ImageButton) findViewById(R.id.main_spot);
        search_spot = (TextView) findViewById(R.id.search_spot);

        // 메인 스케줄 버튼 눌렀을 때
        main_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(getApplicationContext(), Page3_Main.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);

            }
        });

        // 일정등록 버튼 눌렀을 때
        main_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), Heart_page.class);
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

        // 도시 검색 버튼 눌렀을 때
        search_spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent spotIntent = new Intent(getApplicationContext(), Page2_X.class);
                spotIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                spotIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(spotIntent);
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
}
