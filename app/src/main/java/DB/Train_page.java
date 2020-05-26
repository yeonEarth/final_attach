package DB;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

//아래 블로그를 보며 이해
//http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
public class Train_page extends AppCompatActivity {
    private static final String TAG = "Train_Main";
    long nowIndex;
    String ID;
    String number;
    String sort = "userid";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private Train_DbOpenHelper mDbOpenHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traindb_page);

        //데베 값 넣어줄 리스트
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.train_db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);

        //데베 연결
        mDbOpenHelper = new Train_DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "눌렸네"+arrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
        }
    };

    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            Log.d("Long Click", "position = " + position);
            nowIndex = Long.parseLong(arrayIndex.get(position));
            AlertDialog.Builder dialog = new AlertDialog.Builder(Train_page.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Train_page.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            mDbOpenHelper.deleteColumnByKey(arrayIndex.get(position));
                            showDatabase(sort);
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Train_page.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    };


    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempNumber = iCursor.getString(iCursor.getColumnIndex("number"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            String Result = tempNumber + "/"
                    + tempDate + "/"
                    + tempDayPass + "/"
                    + tempStation + "/"
                    + tempTime + "/"
                    + tempContentId + "/";

            number = tempNumber;
            arrayData.add(Result);
            arrayIndex.add(tempNumber);
        }

        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
    }


}
