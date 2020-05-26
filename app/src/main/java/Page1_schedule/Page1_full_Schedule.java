package Page1_schedule;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page3_1_1_1.Page3_1_1_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_full_Schedule extends AppCompatActivity {

    Page1_full_ScheduleAdapter1 adapter1;
    ArrayList<Page1_Main.RecycleItem> All_items = new ArrayList<Page1_Main.RecycleItem>();

    //여행 일차를 알기 위함
    int dayNumber = 0 ;
    String db_key;

    //데이터베이스 관련
    private Train_DbOpenHelper mDbOpenHelper;
    private ArrayList<Database_Item> db_data = new ArrayList<Database_Item>();
    private String startDate;
    private List<String> station = new ArrayList<String>();
    private List<String> stationWithTransfer = new ArrayList<String>();

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1_full_schedule);

        //앞에서 값을 받아온다.
        Intent get = getIntent();
        db_key = get.getStringExtra("key");


        /*데이터베이스 연결**************************************************/
        mDbOpenHelper = new Train_DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();




        //데이터베이스에 있는 값을 리스트에 추가
        getDatabase(db_key);


        //일차, 기차시간, 관광지 부분 분류
        int dayNumber = 0;
        for(int i = 0; i < db_data.size(); i++){

            //일차
            if(db_data.get(i).text_shadow.length() == 0
                    && db_data.get(i).time.length() == 0
                    && db_data.get(i).contentId.length() == 0){
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.HEADER, db_data.get(i).date, db_data.get(i).text, "", "", "" ,""));
                dayNumber++;
            }

            //기차시간
            else if(db_data.get(i).text_shadow.length() != 0){
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.CHILD,  db_data.get(i).date, db_data.get(i).text_shadow, db_data.get(i).time, db_data.get(i).text, "", ""));
            }

            //관심 관광지
            else{
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.CITY,  db_data.get(i).date, "", "", "", db_data.get(i).text, db_data.get(i).contentId));
            }
        }


        String startDate = db_data.get(0).date;
        String endDate = db_data.get(db_data.size()-1).date;


        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch) findViewById(R.id.menu_postion_btn);
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
        logo = (ImageButton) findViewById(R.id.main_logo_page1_full);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.Page1.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                //overridePendingTransition(0,0);
                startActivity(intent);

            }
        });


        //날짜를 반영
        TextView textView = (TextView)findViewById(R.id.page1_full_schedule_date);
        textView.setText(startDate.substring(0,4) + "년 "
        + startDate.substring(4, 6) + "월 "
        + startDate.substring(6) + "일 ~ "
        + endDate.substring(4, 6) + "월 "
        + endDate.substring(6) + "일");


        RecyclerView recyclerView = findViewById(R.id.page1_full_schedule_recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter1 = new Page1_full_ScheduleAdapter1(All_items, dayNumber);
        recyclerView.setAdapter(adapter1);


        //수정하기 버튼 누르면
        FloatingActionButton editBtn = (FloatingActionButton)findViewById(R.id.page1_full_schedule_editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page1_full_Schedule.this, Page3_1_1_1_Main.class);
                intent.putExtra("key", db_key);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }


    //데이터베이스 받기(앞에서 저장한 값만 바로 보여줌)
    private void getDatabase(String db_key){
        String db_key2 = db_key.trim();
        Cursor iCursor = mDbOpenHelper.selecteNumber(db_key2);
        db_data.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempNumber = iCursor.getString(iCursor.getColumnIndex("number"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            Log.i("로그다",tempDate+"/"+ tempDayPass+"/"+ tempStation+"/"+ tempTime+"/"+ tempContentId );
            db_data.add(new Database_Item(tempDate, tempDayPass, tempStation, tempTime, tempContentId));
        }
        mDbOpenHelper.close();
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
}
