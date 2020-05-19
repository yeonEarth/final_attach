package Page3_1_1_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import DB.Train_DbOpenHelper;
import Page1_schedule.Page1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public  class Page3_1_1_1_Main extends AppCompatActivity implements Page3_1_1_1_addCityBottomSheet.onSetList {
    TextView addSpot;
    String split_1 [];
    String date= null, dayPass = null;
    String day1_date, day2_date, day3_date, day4_date, day5_date, day6_date, day7_date;

    Button save_btn; //

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
    int number = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_1_1_main);

        //데이터베이스 연결
        dbOpenHelper = new Train_DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();

        //값을 받아옴
        Intent intent = getIntent();
        next_data = (ArrayList<String>) intent.getSerializableExtra("next_data");
        next_data_second = (ArrayList<String>) intent.getSerializableExtra("next_data");
        date = (intent.getExtras().getString("date")).replaceAll("[^0-9]", "");
        dayPass = intent.getExtras().getString("dayPass");


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
        for(int i =0; i < next_data.size(); i++){
            split_1 = next_data.get(i).split(",");
            getitem.add(new Page3_1_1_1_dargData(split_1[0], split_1[1]));
        }

        //리사이클러뷰 리스트에 추가
        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "1일차", day1_date, "", "", ""));

        //환승인지 아닌지 걸러내는 작업
        for(int i =0; i < next_data.size()-1; i++){
            if(!getitem.get(i).getNumber().contains("환승") ){
                if(!getitem.get(i+1).getNumber().contains("환승")) {
                    list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + " - " + getitem.get(i + 1).getName(), day1_date  ,""  ,"", ""));
                }
                else if(!getitem.get(i+2).getNumber().contains("환승")) {
                    list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName()+","+getitem.get(i+1).getName()+","+getitem.get(i+2).getName(), getitem.get(i).getName()+" - "+getitem.get(i+2).getName() ,  day1_date  ,"" ,"" , ""));
                } else if(!getitem.get(i+3).getNumber().contains("환승")) {
                    list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD,getitem.get(i).getName()+","+getitem.get(i+1).getName()+","+getitem.get(i+2).getName()+","+getitem.get(i+3).getName() ,getitem.get(i).getName()+" - "+getitem.get(i+3).getName() ,  day1_date ,"" , "", ""));
                } else {
                    list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD,getitem.get(i).getName()+","+getitem.get(i+1).getName()+","+getitem.get(i+2).getName()+","+getitem.get(i+3).getName()+","+getitem.get(i+3).getName() ,getitem.get(i).getName()+" - "+getitem.get(i+4).getName() ,  day1_date ,"", "" ,""));
                }
            }
        }

        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "","2일차",  day2_date, "" , "" ,""));
        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "","3일차",  day3_date, "" , "" ,""));


        //5일차면
        if(dayPass.contains("5")){
            //날짜 더해줌
            calendar.add(Calendar.DATE, 1);
            day4_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day5_date = dateFormat.format(calendar.getTime());

            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date ,"" , "" , ""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date ,"" , "" , ""));
        }


        //7일차면
        else if(dayPass.contains("7")){
            //날짜 더해줌
            calendar.add(Calendar.DATE, 1);
            day4_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day5_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day6_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day7_date = dateFormat.format(calendar.getTime());

            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date , "" , "" , ""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date , "" , "" , ""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "6일차", day6_date , "" , "" , ""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "7일차", day7_date , "" , "" , ""));
        }



        // 레이아웃 안에 레이아웃 만들기
        LinearLayout contentsLayout = (LinearLayout) findViewById(R.id.page3_1_1_box_round);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.page3_1_1_1_recyclerview, contentsLayout, true);


        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.scheduleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 리사이클러뷰에 Adapter 객체 지정.
        adapter = new Page3_1_1_1_trainAdapter(list, getSupportFragmentManager());
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
        save_btn = (Button)findViewById(R.id.page3_save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //현재시간얻기(데이터베이스의 기본키가 됨)
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss");
                String formatDate = simpleDateFormat.format(date);

                dbOpenHelper.open();
                for(int i=0; i < list.size(); i++){
                    dbOpenHelper.insertColumn(formatDate, list.get(i).date, list.get(i).text, list.get(i).text_shadow,
                            list.get(i).train_time, list.get(i).contentId);
                }
                dbOpenHelper.close();

                Intent intent = new Intent(getApplicationContext(), Page1_Main.class);
                intent.putExtra("key", formatDate);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        //초기화 버튼 누르면
        Button reset_btn = (Button)findViewById(R.id.page3_1_1_1_1_reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                String text_split[] = list.get(i).text.split("-");

                //불필요한 공백 제거
                String station = text_split[1].trim();

                if(station.contains(cityname)){
                    list.add(i+1, new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",  text,  list.get(i).date, "", "", cityname));
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



    public class RecycleItem {
        int type;
        String text;
        String text_shadow;
        String date;
        String train_time;
        String station_code;
        String contentId;

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



}
