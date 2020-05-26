package Page3_1_1_1;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;

public class Page3_1_1_1_addCityBottomSheet extends BottomSheetDialogFragment implements Page3_1_1_1_SecondAdapter.OnDismiss {
    Page3_1_1_1_Main page3_1_1_1_main;

    private ArrayList<String> cityname = new ArrayList<>(); // 헤더 도시 이름 들어감
    private List<String > cityList = new ArrayList<>(); // 도시 중복 제거 저장할 리스트
    private ArrayList<String> spot = new ArrayList<>(); // 관광지 들어감

    // 어댑터로 넘길 값
    private ArrayList<String> name = new ArrayList<>();
    private List<Page3_1_1_1_Main.RecycleItem> items = new ArrayList<Page3_1_1_1_Main.RecycleItem>();
    private Page3_1_1_1_SecondAdapter.OnDismiss onDismiss;

    RecyclerView recyclerView;
    private Page3_1_1_1_FirstItemAdapter adapter;
    onSetList listener;

    //데이터베이스 관련
    String sort = "userid";
    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();
    static ArrayList<String> arrayCityName = new ArrayList<String>();
    private DbOpenHelper mDbOpenHelper;


    public static Page3_1_1_1_addCityBottomSheet getInstance() {
        return new Page3_1_1_1_addCityBottomSheet();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        page3_1_1_1_main = (Page3_1_1_1_Main) getActivity();

        if(context instanceof onSetList){
            listener = (onSetList) context;
        } else {
            throw new RuntimeException(context.toString() + " 오류났다.");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.page3_1_1_1_addcity, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.page3_1_1_1_1_recyclerview) ;

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        //데베 연결
        mDbOpenHelper = new DbOpenHelper(page3_1_1_1_main);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        showDatabase(sort);
        Log.d("12ㅓㅓㅓㅓㅓ3", String.valueOf(spot.size()));

        // 헤더에 도시 이름 넣기
        for (int i = 0 ; i < cityList.size() ; i++) {
            Log.d("12ㅓㅓㅓㅓㅓ3",cityList.get(i));
            name.add(cityList.get(i));
        }

        // 아이템 넣기
        for (int i = 0 ; i < spot.size() ; i++) {
            items.add(new Page3_1_1_1_Main.RecycleItem(spot.get(i), cityname.get(i)));

            Log.i("추가", spot.get(i)+ cityname.get(i));
        }

        // 리사이클러뷰 + 어댑터 연결
        adapter = new Page3_1_1_1_FirstItemAdapter(name, items, listener, this);
        recyclerView.setAdapter(adapter);


        //바틈시트 특성상 아래로 드래그하면 바로 다이얼로그가 사라져버림
        //아래로 드래그 했을때 listview가 드래그 되도록 하기 위한 메소드
        recyclerView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        return rootview;
    }


    //바텀시트에서 선택하면
    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(listener != null){
                listener.onsetlist(arrayData.get(position), cityname.get(position));
            }
            dismiss();
        }
    };


    //데이터베이스 가져오는 부분
    public void showDatabase(String sort){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabaseㅀㅀㅎㅎㅎ", "DB Sㅎㅎㅎㅎㅎize: " + iCursor.getCount());
        arrayData.clear();
        arrayIndex.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("userid"));
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            String tempCityname = iCursor.getString(iCursor.getColumnIndex("cityname"));

            String Result = "(" + tempCityname + ")" + tempName;

            spot.add(tempName); // 관광지 넣기 차일드
            cityname.add((tempCityname));   // 도시 이름 넣기 헤더
//            arrayData.add(Result);
            arrayCityName.add(tempIndex);
//            arrayCityName.add(tempCityname);
        }

        Cursor iCursorCityName = mDbOpenHelper.sortCityColumn(sort);

        while (iCursorCityName.moveToNext()) {
            String tempCityName = iCursorCityName.getString(iCursorCityName.getColumnIndex("cityname"));

            cityList.add(tempCityName);
            Log.i("갯수", String.valueOf(cityList.size()));
        }

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss() {
        dismiss();
    }


    //인터페이스 구현-바텀시트 아이템 선택하면 리사이클러뷰에 넣어주기 위함
    public interface onSetList {
        void onsetlist(String text, String cityname);
    }

}

