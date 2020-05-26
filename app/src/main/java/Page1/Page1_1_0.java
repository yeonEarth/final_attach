package Page1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

import Page2.Page2;
import Page2_X.Page2_X;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_1_0 extends AppCompatActivity {
    Button city, course;

    ImageButton logo;

    //메뉴 관련
    private Context context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_1_0);

        city = (Button) findViewById(R.id.page1_1_city);
        course = (Button) findViewById(R.id.page1_1_course);

        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch)findViewById(R.id.menu_postion_btn);
        recyclerView1 = (RecyclerView)findViewById(R.id.menu_recyclerview1);

        logo = (ImageButton) findViewById(R.id.main_logo_page1_1_0);

        //메인 로고 눌렀을때 메인페이지 이동
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                //intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });


        // 도시 탐색하기 눌렀을 때
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(getApplicationContext(), Page2_X.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);

            }
        });

        // 추천 코스 눌렀을 때
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(getApplicationContext(), Page2_1.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);
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
        adapter = new Main_RecyclerviewAdapter(name, context);
        recyclerView1.setAdapter(adapter);

        //리사이클러뷰 헤더
        name.add("0");
        name.add("1");
        name.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
