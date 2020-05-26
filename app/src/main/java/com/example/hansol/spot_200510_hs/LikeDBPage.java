package com.example.hansol.spot_200510_hs;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import DB.Like_DbOpenHelper;

public class LikeDBPage extends AppCompatActivity {
    private static final String TAG = "Main";
    long nowIndex;
    String ID;
    String name;

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    private Like_DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_dbpage);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);

        mDbOpenHelper = new Like_DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase();
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), "눌렸네", Toast.LENGTH_SHORT).show();
        }
    };

    public void showDatabase(){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempName = iCursor.getString(iCursor.getColumnIndex("nickname"));
            String tempFav = iCursor.getString(iCursor.getColumnIndex("like"));
            tempName = setTextLength(tempName,10);

            String Result = tempFav + tempName;
            arrayData.add(Result);
            arrayIndex.add(tempIndex);
        }


        arrayAdapter.clear();
        arrayAdapter.addAll(arrayData);
        arrayAdapter.notifyDataSetChanged();
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
}
