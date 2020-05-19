package Page1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;

public class Page1_1_1 extends AppCompatActivity {
    Page1_1_1_Adapter adapter;
    ArrayList<String> name = new ArrayList<>();
    private List<Recycler_item> items = new ArrayList<Recycler_item>();

    private ArrayList<String > mySpot = new ArrayList<String >();
    private ArrayList<String > myCity = new ArrayList<String >();

    private DbOpenHelper mDbOpenHelper;
    String sort = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_1_1);

        // 리사이클러뷰 설정
        RecyclerView recyclerView = findViewById(R.id.page1_1_1_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Page1_1_1_Adapter(name, items);
        recyclerView.setAdapter(adapter);

        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);

        // 리사이클러뷰 헤더
        name.add("나의 관광지");


        for (int i = 0 ; i < mySpot.size() ; i++) {
            items.add(new Recycler_item("", mySpot.get(i), "1234", "역사", myCity.get(i)));
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
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            String tempCityName = iCursor.getString(iCursor.getColumnIndex("cityname"));

            mySpot.add(tempName);
            myCity.add(tempCityName);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
