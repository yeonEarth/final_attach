package Page3_1_1_1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1_schedule.Page1_Main;
import Page2_1_1.NetworkStatus;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public  class Page3_1_1_1_Main extends AppCompatActivity implements Page3_1_1_1_addCityBottomSheet.onSetList {
    TextView title;
    TextView addSpot;
    String split_1 [];
    String date= null, dayPass = null;
    String day1_date, day2_date, day3_date, day4_date, day5_date, day6_date, day7_date;
    Button save_btn;

    //리사이클러뷰 관련
    ArrayList<String> next_data;
    ArrayList<String> next_data_second;
    ArrayList<Page3_1_1_1_dargData> getitem = new ArrayList<>();
    ArrayList<RecycleItem> list = new ArrayList<>();

    //어댑터 관련
    LayoutInflater inflater;
    Page3_1_1_1_trainAdapter adapter;

    //데이터베이스 관련
    Train_DbOpenHelper dbOpenHelper;

    //page1_full_schedule
    ArrayList<Page1_Main.RecycleItem> All_items;
    int dayNumber = 0 ;
    String db_key;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_1_1_main);


        //데이터베이스 연결
        dbOpenHelper = new Train_DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();


        //값을 받아옴(2) page1_full_schedule에서 옴
        Intent intent = getIntent();
        db_key = null;
        dayNumber = intent.getIntExtra("dayNumber", dayNumber);
        db_key = intent.getStringExtra("key");


        //값을 받아옴(1) page3_1에서 옴
        if(db_key == null) {
            next_data = (ArrayList<String>) intent.getSerializableExtra("next_data");
            date = (intent.getExtras().getString("date")).replaceAll("[^0-9]", "");
            dayPass = intent.getExtras().getString("dayPass");
        }


        save_btn = (Button)findViewById(R.id.page3_save_btn);
        title = (TextView) findViewById(R.id.page3_1_1_1_title);

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
        logo = (ImageButton) findViewById(R.id.main_logo_page3_1_1_1);

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





        //(1)page3_1 에서 오면
        if(db_key == null) {
            //날짜를 더할때 실제 날짜 반영해서 더해야함
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date_format = null;
            try {
                date_format = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date_format);
            day1_date = date;
            calendar.add(Calendar.DATE, 1);
            day2_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day3_date = dateFormat.format(calendar.getTime());


            //앞에서 전달한 값을 쪼갬
            for (int i = 0; i < next_data.size(); i++) {
                split_1 = next_data.get(i).split(",");
                getitem.add(new Page3_1_1_1_dargData(split_1[0], split_1[1]));
            }

            //리사이클러뷰 리스트에 추가
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "1일차", day1_date, "", "",""));

            //환승인지 아닌지 걸러내는 작업
            for (int i = 0; i < next_data.size() - 1; i++) {
                if (!getitem.get(i).getNumber().contains("환승")) {
                    //직행
                    if (!getitem.get(i + 1).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), day1_date, "", "",""));
                    }

                    //환승 1회
                    else if (!getitem.get(i + 2).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), day1_date, "", "",""));
                    }

                    //환승 2회
                    else if (!getitem.get(i + 3).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + ",(환승)" + getitem.get(i + 2).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), "(환승)" + getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), day1_date, "", "",""));
                    }

                    //환승 3회
                    else {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + ",(환승)" + getitem.get(i + 2).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), "(환승)" + getitem.get(i + 2).getName() + ",(환승)" + getitem.get(i + 3).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 3).getName() + "," + getitem.get(i + 4).getName(), "(환승)" + getitem.get(i).getName() + "," + getitem.get(i + 4).getName(), day1_date, "", "",""));
                    }
                }
            }

            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "2일차", day2_date, "", "",""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "3일차", day3_date, "", "",""));



            //5일차면
            if (dayPass.contains("5")) {
                //날짜 더해줌
                calendar.add(Calendar.DATE, 1);
                day4_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day5_date = dateFormat.format(calendar.getTime());

                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date, "", "",""));
            }


            //7일차면
            else if (dayPass.contains("7")) {
                //날짜 더해줌
                calendar.add(Calendar.DATE, 1);
                day4_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day5_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day6_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day7_date = dateFormat.format(calendar.getTime());

                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "6일차", day6_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "7일차", day7_date, "", "",""));
            }
        }


        //(2) schedule에서 값을 받아오면
        if(db_key != null) {
            getDatabase(db_key);
            title.setText("여행 일정 수정");
            save_btn.setText("수정완료하기");
        }


        // 레이아웃 안에 레이아웃 만들기
        LinearLayout contentsLayout = (LinearLayout) findViewById(R.id.page3_1_1_box_round);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.page3_1_1_1_recyclerview, contentsLayout, true);


        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.scheduleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 리사이클러뷰에 Adapter 객체 지정.
        int isNetworkConnect  = NetworkStatus.getConnectivityStatus(Page3_1_1_1_Main.this);
        adapter = new Page3_1_1_1_trainAdapter(list, getSupportFragmentManager(), isNetworkConnect);
        recyclerView.setAdapter(adapter);


        // 드래그 이벤트
        ItemTouchHelper.Callback callback = new TrainItemTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        adapter.setTouchHelper(touchHelper);


        // 관광지 추가하기 버튼 누르면
        addSpot = (TextView) findViewById(R.id.page3_1_1_1_1_addbtn);
        addSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page3_1_1_1_addCityBottomSheet addCityBottomSheet = Page3_1_1_1_addCityBottomSheet.getInstance();
                addCityBottomSheet.show(getSupportFragmentManager(), "AddCityBottomSheet");
            }
        });


        //일정 저장 버튼 누르면 ( 현재날짜+시간을 key로 데베에 저장)
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //page3_1에서 온 거라면
                if(db_key == null){
                    //현재시간얻기(데이터베이스의 기본키가 됨)
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmmss");
                    String formatDate = simpleDateFormat.format(date).trim();

                    dbOpenHelper.open();
                    for(int i=0; i < list.size(); i++){
                        dbOpenHelper.insertColumn(
                                "no",
                                formatDate,
                                list.get(i).date,
                                list.get(i).text,
                                list.get(i).text_shadow,
                                list.get(i).train_time,
                                list.get(i).contentId);
                    }
                    dbOpenHelper.close();

                    Intent intent = new Intent(getApplicationContext(), Page1_Main.class);
                    intent.putExtra("key", formatDate);
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

                else{
                    dbOpenHelper.open();
                    dbOpenHelper.deleteColumnByKey(db_key);
                    for(int i=0; i < list.size(); i++){
                        dbOpenHelper.updateColumn(
                                "no",
                                db_key,
                                list.get(i).date,
                                list.get(i).text,
                                list.get(i).text_shadow,
                                list.get(i).train_time,
                                list.get(i).contentId);
                    }
                    dbOpenHelper.close();

                    Intent intent = new Intent(getApplicationContext(), Page1_Main.class);
                    Log.i("키????", db_key);
                    intent.putExtra("key", db_key);
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

            }
        });
    }



    //관심 관광지 추가 인터페이스
    @Override
    public void onsetlist(String text, String cityname) {
        boolean isAdd = false;

        //해당되는 도시 아래에 넣기 위함
        for(int i =0; i < list.size(); i++){
            if(list.get(i).type == Page3_1_1_1_trainAdapter.CHILD){
                String text_split[] = list.get(i).text.split(",");

                //불필요한 공백 제거
                String station = text_split[1].trim();

                if(station.contains(cityname)){
                    list.add(i+1, new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",  text,  list.get(i).date, "", "",cityname));
                    adapter.notifyDataSetChanged();
                    isAdd = true;
                    break;
                }
            }
        }

        //해당되는 도시가 없으면
        if(!isAdd)
            Toast.makeText(getApplicationContext(), "해당되는 정차역이 없습니다.", Toast.LENGTH_LONG).show();
    }



    public static class RecycleItem {
        int type;
        String text;
        String text_shadow;
        String date;
        String train_time;
        String station_code;
        String contentId;

        String cityName;
        String title;

        public String getCityName() {
            return cityName;
        }

        public String getTitle() {
            return title;
        }

        public RecycleItem(String title, String cityName) {
            this.title = title;
            this.cityName = cityName;
        }

        public RecycleItem(int type, String text_shadow, String text, String  date, String train_time, String station_code, String contentId){
            this.type = type;
            this.text_shadow = text_shadow;
            this.text = text;
            this.date = date;
            this.train_time = train_time;
            this.station_code = station_code;
            this.contentId = contentId;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setTrain_time(String train_time) {
            this.train_time = train_time;
        }

        public void setStation_code(String station_code) {
            this.station_code = station_code;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

    }



    //뒤로가기 화면 전환 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
    }


    //액티비티 닫히면 리스트 초기화
    @Override
    protected void onPause() {
        super.onPause();
        list.clear();
        getitem.clear();
    }

    //데이터베이스 받기(앞에서 저장한 값만 바로 보여줌)
    private void getDatabase(String db_key){
        String db_key2 = db_key.trim();
        Cursor iCursor = dbOpenHelper.selecteNumber(db_key2);
        list.clear();
        List<String> date = new ArrayList<>();
        List<String> daypase = new ArrayList<>();
        List<String> station = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> contendId = new ArrayList<>();

        while(iCursor.moveToNext()) {
            String tempDate     = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass  = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation  = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime     = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            Log.i("로그다", tempDate + "/" + tempDayPass + "/" + tempStation + "/" + tempTime + "/" + tempContentId);
            date.add(tempDate);
            daypase.add(tempDayPass);
            station.add(tempStation);
            time.add(tempTime);
            contendId.add(tempContentId);
        }
        for (int i = 0; i < iCursor.getCount(); i++) {
            //일차
            if ( station.get(i).equals("") && time.get(i).equals("") && contendId.get(i).equals("")) {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", daypase.get(i), date.get(i), "", "",""));
            }

            //기차시간
            else if (contendId.get(i).equals("")) {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, station.get(i), daypase.get(i), date.get(i), "", "",""));
            }

            //시티
            else {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",daypase.get(i), date.get(i), "", "",""));
            }
        }

    }


}
