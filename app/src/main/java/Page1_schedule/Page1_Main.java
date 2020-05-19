package Page1_schedule;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hansol.spot_200510_hs.R;
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

import DB.Train_DbOpenHelper;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_Main extends AppCompatActivity implements  Page1_pagerAdapter.send_expand{

    //최상위 레이아웃
    ScrollView page1_scrollView;
    ImageView last_station;
    TextView userName;
    TextView startStation, endStation;

    // 날짜 관련 변수들
    String time, forcomparedate;
    SimpleDateFormat sdf, forcompare;
    Calendar myCalendar = Calendar.getInstance();
    EditText editDate;

    //뷰페이저 관련
    boolean isFirst = false;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    PageIndicatorView pageIndicatorView;
    List<String> arrayLocal= new ArrayList<>();
    int position = 0;

    //기차시간표 관련
    ListView dataList;
    LinearLayout table_title;
    Page1_ListAdapter itemAdapter;

    //api 관련
    private  String receiveMsg;
    private  String [] data_split;
    private  ArrayList<Api_Item> completeList;              //정제된 리스트값
    private  ArrayList<Api_Item> header_data;               //정제전 헤더값
    private  ArrayList<Api_Item> child1_data;               //정제전 차일드 값(경유1
    private  ArrayList<Api_Item> child2_data;               //정제전 차일드 값(경유2
    private  ArrayList<Api_Item> child3_data;               //정제전 차일드 값(경유3
    private  String[] arr_line;
    private  String[] arr_all;
    private  String[] _name = new String[238];                                              //txt에서 받은 역이름
    private  String[] _code = new String[238];                                              //txt에서 받은 역코드
    private  String startCode, endCode, trainCode;
    private  String[] trainCodelist = {"01", "02", "03"}; //, "04", "08", "09", "15"
    String date;

    //전체 스케쥴 관련
    Button all_schedule_btn;
    LinearLayout schedule_layout;
    LayoutInflater inflater;
    Page1_ScheduleAdapter adapter;
    ArrayList<RecycleItem> All_items = new ArrayList<RecycleItem>();
    ArrayList<RecycleItem> Day_items = new ArrayList<RecycleItem>();

    //데이터베이스 관련
    private String db_key;
    private Train_DbOpenHelper mDbOpenHelper;
    private static ArrayList<Database_Item> db_data = new ArrayList<Database_Item>();
    private String startDate;
    private List<String> station = new ArrayList<String>();
    private List<String> stationWithTransfer = new ArrayList<String>();

    //dp 변환
    float d;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1_main_schedule);

        //픽셀을 dp 로 변환하기 위함
        d = Page1_Main.this.getResources().getDisplayMetrics().density;

        //객체연결
        editDate = (EditText) findViewById(R.id.page1_date);
        dataList = (ListView) findViewById(R.id.list_item);
        viewPager = (ViewPager) findViewById(R.id.page1_pager);
        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        all_schedule_btn = (Button) findViewById(R.id.page1_schedule_btn);
        schedule_layout = (LinearLayout) findViewById(R.id.page1_schedule_layout);
        userName = (TextView)findViewById(R.id.page1_userName);
        startStation = (TextView)findViewById(R.id.page1_startTxt);
        endStation = (TextView)findViewById(R.id.page1_endTxt);
        page1_scrollView = (ScrollView) findViewById(R.id.page1_scrollView);
        last_station = (ImageView)findViewById(R.id.page1_timeTable_lastimg);
        table_title = (LinearLayout)findViewById(R.id.table_title);

        completeList= new ArrayList<Api_Item>();
        header_data = new ArrayList<Api_Item>();
        child1_data = new ArrayList<>();
        child2_data = new ArrayList<>();
        child3_data = new ArrayList<>();
        pagerAdapter = new Page1_pagerAdapter(this, this, arrayLocal);

        //page3_1_1_1_1 에서 일정저장하기 누를때 받아옴
        Intent get = getIntent();
        db_key = get.getStringExtra("key");


        // 현재 날짜 출력
        String myFormat = String.format("%s%s%s%s", "yyyy년 ", "MM월 ", "dd일 ", "EE요일");
        String FormatforCompare = String.format("yyyyMMdd");
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        forcompare = new SimpleDateFormat(FormatforCompare, Locale.KOREA);
        time = sdf.format(myCalendar.getTime());
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
        startDate = db_data.get(0).date;
        int myear = Integer.parseInt(startDate.substring(0,4));
        int mmonth = Integer.parseInt(startDate.substring(4,6));
        int mday = Integer.parseInt(startDate.substring(6));

        Calendar start_date = Calendar.getInstance();
        start_date.set(myear, mmonth-1, mday);

        long today = myCalendar.getTimeInMillis()/86400000;
        long dday = start_date.getTimeInMillis()/86400000;
        long count = dday - today;

        if((int)count != 0){
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
            String station_split[] = station.get(i).split("-");
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
        viewPager.setAdapter(pagerAdapter);
        pageIndicatorView.setCount(arrayLocal.size());


        //뷰페이저 양쪽 미리보기
        int dpValue = 32;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin / 2);


        //첫번째 뷰페이저에 들어갈 열차시간표
        if(!isFirst){
            Day_schedule_data(stationWithTransfer.get(0));
            send_Api(stationWithTransfer.get(0));
            startStation.setText(arrayLocal.get(0));
            endStation.setText(arrayLocal.get(1));
            isFirst = true;
        }


        //뷰페이저 리스너
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageSelected(int position) {
                // 데이터 요청
                completeList.clear();
                header_data.clear();
                child1_data.clear();
                child2_data.clear();
                child3_data.clear();
                if (position != arrayLocal.size() - 1){
                    Day_items.clear();
                    send_Api(stationWithTransfer.get(position));
                    startStation.setText(arrayLocal.get(position));
                    endStation.setText(arrayLocal.get(position+1));
                    last_station.setVisibility(View.INVISIBLE);
                    table_title.setVisibility(View.VISIBLE);
                    Day_schedule_data(stationWithTransfer.get(position));
                    adapter.notifyDataSetChanged();
                }
                else {
                    //마지막 역이기 때문에 시간표 리스트 초기화 및 갱신
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
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.paeg1_recyclerview, schedule_layout, true);
        RecyclerView schedule_recyclerview = (RecyclerView) findViewById(R.id.page1_scheedule_recyclerview);
        schedule_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Page1_ScheduleAdapter(Day_items);
        schedule_recyclerview.setAdapter(adapter);
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
                    trianjsonParser(receiveMsg, isMiddle);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }

        //출발 시간 정렬
        Collections.sort(header_data);

        //직행이라면
        if (isMiddle == 0) {
            completeList = header_data;
        }

        //환승한다면
        else {
            switch (isMiddle) {

                //1회 환승
                case 1:
                    Collections.sort(child1_data);

                    //첫번째 기차의 시간표이 기준
                    for (int i = 0; i < header_data.size(); i++) {
                        String[] header_time_split = header_data.get(i).getArrTime().split(":");
                        Log.i("뭔데", "사이즈:" + String.valueOf(header_data.size()) + "/도착시간:" + String.valueOf( header_data.get(i).getArrTime()+"/넘버"+String.valueOf(i) ));

                        //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                        if (Integer.parseInt(header_time_split[0].replaceAll("[^0-9]", "")) < 3)
                            continue;

                        //도착시간과 환승역의 출발시간을 비교해서 리스트에 넣어줌
                        for (int p = 0; p < child1_data.size(); p++) {
                            String[] child1_time_split = child1_data.get(p).getDepTime().split(":");
                            if (Integer.parseInt(header_time_split[0]) <= Integer.parseInt(child1_time_split[0])
                                    && Integer.parseInt(header_time_split[1].substring(0,2)) + 10 < Integer.parseInt(child1_time_split[1])) {
                                 completeList.add(new Api_Item(
                                        Page1_ListAdapter.CHILD,
                                        header_data.get(i).getDepTime() + "\n" + child1_data.get(p).getDepTime(),
                                        header_data.get(i).getArrTime() + "\n" + child1_data.get(p).getArrTime(),
                                        header_data.get(i).getTrainNumber() + "\n" + child1_data.get(p).getTrainNumber()
                                ));
                                break;
                            }
                        }
                    }
                    break;


                //2회 환승
                case 2:
                    Collections.sort(child1_data);
                    Collections.sort(child2_data);

                    //첫번째 기차의 시간표이 기준
                    for (int i = 0; i < header_data.size(); i++) {
                        String[] header_time_split = header_data.get(i).getArrTime().split(":");

                        //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                        if (Integer.parseInt(header_time_split[0].replaceAll("[^0-9]", "")) < 3)
                            continue;

                        //환승(1)
                        for (int p = 0; p < child1_data.size(); p++) {
                            String[] child1_Deptime_split = child1_data.get(p).getDepTime().split(":");
                            String[] child1_Arrtime_split = child1_data.get(p).getArrTime().split(":");

                            //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                            if (Integer.parseInt(child1_Arrtime_split[0].replaceAll("[^0-9]", "")) < 3)
                                continue;
                            ;

                            //환승(2)
                            //도착시간과 환승역의 출발시간을 비교해서 리스트에 넣어줌
                            for (int t = 0; t < child2_data.size(); t++) {
                                String[] child2_time_split = child2_data.get(t).getDepTime().split(":");

                                if (Integer.parseInt(header_time_split[0]) <= Integer.parseInt(child1_Deptime_split[0])
                                        && Integer.parseInt(header_time_split[1].substring(0,2)) + 10 < Integer.parseInt(child1_Deptime_split[1].substring(0,2))
                                        && Integer.parseInt(child1_Arrtime_split[0]) <= Integer.parseInt(child2_time_split[0])
                                        && Integer.parseInt(child1_Arrtime_split[1].substring(0,2)) + 10 < Integer.parseInt(child2_time_split[1].substring(0,2))) {

                                    completeList.add(new Api_Item(
                                            Page1_ListAdapter.CHILD,
                                            header_data.get(i).getDepTime() + "\n" + child1_data.get(p).getDepTime() + "\n" + child2_data.get(t).getDepTime(),
                                            header_data.get(i).getArrTime() + "\n" + child1_data.get(p).getArrTime() + "\n" + child2_data.get(t).getArrTime(),
                                            header_data.get(i).getTrainNumber() + "\n" + child1_data.get(p).getTrainNumber() + "\n" + child2_data.get(t).getTrainNumber()
                                    ));
                                    break;
                                }
                            }
                        }
                    }
                    break;


                //3회 환승
                case 3:
                    Collections.sort(child1_data);
                    Collections.sort(child2_data);
                    Collections.sort(child3_data);

                    //첫번째 기차의 시간표이 기준
                    for (int i = 0; i < header_data.size(); i++) {
                        String[] header_time_split = header_data.get(i).getArrTime().split(":");

                        //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                        if (Integer.parseInt(header_time_split[0].replaceAll("[^0-9]", "")) < 3)
                            continue;

                        //환승(1)
                        for (int p = 0; p < child1_data.size(); p++) {
                            String[] child1_Deptime_split = child1_data.get(p).getDepTime().split(":");
                            String[] child1_Arrtime_split = child1_data.get(p).getArrTime().split(":");

                            //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                            if (Integer.parseInt(child1_Arrtime_split[0].replaceAll("[^0-9]", "")) < 3)
                                continue;

                            //환승(2)
                            for (int t = 0; t < child2_data.size(); t++) {
                                String[] child2_Deptime_split = child2_data.get(t).getDepTime().split(":");
                                String[] child2_Arrtime_split = child2_data.get(t).getArrTime().split(":");

                                //도착시간이 자정을 넘기면 스케줄에 포함하지 않음
                                if (Integer.parseInt(child2_Arrtime_split[0].replaceAll("[^0-9]", "")) < 3)
                                    continue;

                                //환승(3)
                                //도착시간과 환승역의 출발시간을 비교해서 리스트에 넣어줌
                                for (int g = 0; g < child3_data.size(); g++) {
                                    String[] child3_time_split = child3_data.get(g).getDepTime().split(":");

                                    if (Integer.parseInt(header_time_split[0]) <= Integer.parseInt(child1_Deptime_split[0])
                                            && Integer.parseInt(header_time_split[1].substring(0,2)) + 10 < Integer.parseInt(child1_Deptime_split[1].substring(0,2))
                                            && Integer.parseInt(child1_Arrtime_split[0]) <= Integer.parseInt(child2_Deptime_split[0])
                                            && Integer.parseInt(child1_Arrtime_split[1].substring(0,2)) + 10 < Integer.parseInt(child2_Deptime_split[1].substring(0,2))
                                            && Integer.parseInt(child2_Arrtime_split[0]) <= Integer.parseInt(child3_time_split[0])
                                            && Integer.parseInt(child2_Arrtime_split[1].substring(0,2)) + 10 < Integer.parseInt(child3_time_split[1].substring(0,2))) {

                                        completeList.add(new Api_Item(
                                                Page1_ListAdapter.CHILD,
                                                header_data.get(i).getDepTime() + "\n" + child1_data.get(p).getDepTime() + "\n" + child2_data.get(t).getDepTime() + "\n" + child3_data.get(g).getDepTime(),
                                                header_data.get(i).getArrTime() + "\n" + child1_data.get(p).getArrTime() + "\n" + child2_data.get(t).getArrTime() + "\n" + child3_data.get(g).getArrTime(),
                                                header_data.get(i).getTrainNumber() + "\n" + child1_data.get(p).getTrainNumber() + "\n" + child2_data.get(t).getTrainNumber() + "\n" + child3_data.get(g).getTrainNumber()
                                        ));
                                        break;
                                    }
                                }


                            }
                        }
                    }
                    break;
            }
        }
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
                url = new URL("http://openapi.tago.go.kr/openapi/service/TrainInfoService/" +
                        "getStrtpntAlocFndTrainInfo?serviceKey=7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D&" +
                        "numOfRows=2" +
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
    public String[] trianjsonParser(String jsonString, int isMiddle){
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

                switch (isMiddle) {
                    case 0:
                        header_data.add(new Api_Item(Page1_ListAdapter.HEADER, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2),  traingradename));
                        Log.i("여기서부터..?11", arrTime);
                        break;
                    case 1:
                        child1_data.add(new Api_Item(Page1_ListAdapter.CHILD, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2),  traingradename));
                        Log.i("여기서부터..?22", arrTime);
                        break;
                    case 2:
                        child2_data.add(new Api_Item(Page1_ListAdapter.CHILD, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2),  traingradename));
                        break;
                    case 3:
                        child3_data.add(new Api_Item(Page1_ListAdapter.CHILD, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2),  traingradename));
                        break;
                    default:
                        break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  arraysum;
    }



    //혜택확인 누르면 레이아웃 펼쳐지기 위한 인터페이스
    @Override
    public void send(boolean isExpand) {
        if (!isExpand){
            viewPager.getLayoutParams().height = (int)(350*d);
            viewPager.requestLayout();
        } else {
            viewPager.getLayoutParams().height = (int)(200*d);
            viewPager.requestLayout();
        }
    }


   //화면 꺼지면
    @Override
    protected void onResume() {
        super.onResume();
        scrollList();
    }


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
        public int describeContents() {
            return 0;
        }

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

}

