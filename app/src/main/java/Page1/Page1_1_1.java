package Page1;

import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;
import Page2.Page2;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_1_1 extends AppCompatActivity {
    Page1_1_1_Adapter adapter;
    ArrayList<String> name = new ArrayList<>();
    private List<Recycler_item> items = new ArrayList<Recycler_item>();

    private ArrayList<String > mySpot = new ArrayList<String >();
    private ArrayList<String > myCity = new ArrayList<String >();
    private ArrayList<String > myContentId = new ArrayList<String >();
    private ArrayList<String > myType = new ArrayList<String >();
    private ArrayList<String > myImage = new ArrayList<String >();

    private List<String > cityList = new ArrayList<>(); // 도시 저장할 리스트
    private ArrayList<String> allList = new ArrayList<>();  // 모두 다 받을 리스트


    private DbOpenHelper mDbOpenHelper;
    String sort = "cityname";

    //툴바 관련
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;
    boolean isExpand = false;

    ImageButton logo;

    //*******************************여기부터 추가***********************************************************
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
    //ArrayList<String> name = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;
    //******************************************************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_1_1);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = findViewById(R.id.page1_1_1_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Page1_1_1_Adapter(name, items);
        recyclerView.setAdapter(adapter);

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
        adapter2 = new Main_RecyclerviewAdapter(name, context);
        recyclerView1.setAdapter(adapter2);

        logo = (ImageButton) findViewById(R.id.main_logo_page1_1_1);

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



        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);

        // 리사이클러뷰 헤더
        //name.add("나의 관광지");

        // 헤더에 도시 이름 넣기
        for (int i = 0 ; i < cityList.size() ; i++) {
            name.add(cityList.get(i));
        }

        //아이템 넣기
        for (int i = 0 ; i < mySpot.size() ; i++) {
            items.add(new Recycler_item(myImage.get(i), mySpot.get(i), myContentId.get(i), myType.get(i), myCity.get(i)));
        }

        adapter.notifyDataSetChanged();



    }

    //리사이클러뷰 안 리사이클러뷰 데이터 구조
    public class Recycler_item {
        String image;
        String title;
        String contentviewID;
        String type;
        String city;

        String getImage() {
            return this.image;
        }

        String getTitle() {
            return this.title;
        }

        String getContentviewID() {
            return this.contentviewID;
        }

        String getType() {
            return this.type;
        }

        public Recycler_item(String image, String title, String contentviewID, String type, String city) {
            this.image = image;
            this.title = title;
            this.contentviewID = contentviewID;
            this.type = type;
            this.city = city;
        }
    }

    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.sortColumn(sort);
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        String result;
        mySpot.clear();
        myType.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            String tempCityName = iCursor.getString(iCursor.getColumnIndex("cityname"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("userid"));
            String tempType = iCursor.getString(iCursor.getColumnIndex("type"));
            String tempImage = iCursor.getString(iCursor.getColumnIndex("image"));

            mySpot.add(tempName);
            myCity.add(tempCityName);
            myContentId.add(tempContentId);
            myType.add(tempType);
            myImage.add(tempImage);
        }
        Cursor iCursorCityName = mDbOpenHelper.sortCityColumn(sort);

        while (iCursorCityName.moveToNext()) {
            String tempCityName = iCursorCityName.getString(iCursorCityName.getColumnIndex("cityname"));

            cityList.add(tempCityName);
            Log.i("갯수", String.valueOf(cityList.size()));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
