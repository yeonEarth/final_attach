package Menu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page2.Page2;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page4_2 extends AppCompatActivity implements Page4_sendData {

    //데베 관련
    private int first_data;
    private Train_DbOpenHelper mDbOpenHelper;
    private ArrayList<Train_Data> train_data = new ArrayList<Train_Data>();
    private ArrayList<Train_Data_forRecyclerview> train_data_forRecyclerview = new ArrayList<Train_Data_forRecyclerview>();

    //리사이클러뷰 관련
    private RecyclerView recyclerView;
    private Page4_2_adapter adapter;
    private Context context;
    private TextView info_no_data;

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

    ImageButton logo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page4_2);
        context = getApplicationContext();

        //(1)레이아웃 추가
        //page4_2.xml
        //page4_2_item.xml

        //(2)데베 수정
        //Train_db 모두 수정

        //데베 관련
        mDbOpenHelper = new Train_DbOpenHelper(Page4_2.this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        showDatabase();


        //데이터가 없으면
        info_no_data = (TextView)findViewById(R.id.page4_2_noData);
        if(train_data.size() == 0){
            info_no_data.setVisibility(View.VISIBLE);
        } else
            info_no_data.setVisibility(View.INVISIBLE);


        // 리사이클러뷰 설정
        recyclerView = findViewById(R.id.page4_2_recyclerview);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        adapter = new Page4_2_adapter(context, train_data_forRecyclerview, this);
        recyclerView.setAdapter(adapter);

        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch)findViewById(R.id.menu_postion_btn);
        recyclerView1 = (RecyclerView)findViewById(R.id.menu_recyclerview1);


        logo = (ImageButton) findViewById(R.id.main_logo_page4_2);
        //메인 로고
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



    }


    //데베 값 받기
    public void showDatabase(){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        train_data.clear();

        while(iCursor.moveToNext()){
            String Key = iCursor.getString(iCursor.getColumnIndex("number"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));

            train_data.add(new Train_Data( Key, tempDate));
        }

        //값 정제
        first_data = 0;
        for(int i =0; i < train_data.size(); i++){
            if(!train_data.get(first_data).keyDate.equals(train_data.get(i).keyDate)){
                train_data_forRecyclerview.add(new Train_Data_forRecyclerview(
                        train_data.get(i-1).keyDate,
                        train_data.get(first_data).date,
                        train_data.get(i-1).date));
                first_data = i+1;
            }
            if(i == train_data.size()-1){
                train_data_forRecyclerview.add(new Train_Data_forRecyclerview(
                        train_data.get(i).keyDate,
                        train_data.get(first_data).date,
                        train_data.get(i).date));
            }
        }


    }


    //데베 삭제할 때
    @Override
    public void onDelete(final String number2, final int position) {
        final String key = number2.trim();
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Page4_2.this);
        builder.setMessage("저장한 스케쥴을 삭제하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDbOpenHelper.open();
                mDbOpenHelper.deleteColumnByKey(key);
                mDbOpenHelper.close();
                train_data_forRecyclerview.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}


//데베에서 바로 받는 값 구성
class Train_Data {
    String keyDate;
    String date;

    public Train_Data(String keyDate, String date) {
        this.keyDate = keyDate;
        this.date = date;
    }
}



    //데베값 정제한 값 구성
    class Train_Data_forRecyclerview {
        String keyDate;
        String startdate;
        String enddate;

        public Train_Data_forRecyclerview(String keyDate, String startdate, String enddate) {
            this.keyDate = keyDate;
            this.startdate = startdate;
            this.enddate = enddate;
        }
}

