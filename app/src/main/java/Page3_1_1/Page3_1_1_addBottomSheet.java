package Page3_1_1;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Page3_1_1_addBottomSheet extends BottomSheetDialogFragment {
    Page3_1_1_Main page3_1_1_main;
    onSetList listener;                             //page3_1_1에 값을 전달하기 위한 인터페이스

    private ListView listView;                      // 검색을 보여줄 리스트변수
    private EditText editSearch;                    // 검색어를 입력할 Input 창
    private ImageView searchWindow;

    //리스트뷰 관련
    private Page3_1_1_addBottomAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraydatalist;
    private List<String> datalist;                   // 데이터를 넣은 리스트변수

    //txt 파일 관련
    String readStr = "";
    String[] code_name = null;
    String[] code = new String[238];
    String[] name = new String[238];


    public static Page3_1_1_addBottomSheet getInstance() {
        return new Page3_1_1_addBottomSheet();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        page3_1_1_main = (Page3_1_1_Main) getActivity();

        //인터페이스 연결
        if(context instanceof onSetList){
            listener = (onSetList) context;
        } else {
            throw new RuntimeException(context.toString() + "오류");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.page3_1_1_bottomsheet_list, container, false);

        //자동입력부분 (참고 : https://sharp57dev.tistory.com/11
        searchWindow = (ImageView) rootview.findViewById(R.id.page3_1_1_searchwindow);
        editSearch = (EditText) rootview.findViewById(R.id.page3_1_1_auto);
        listView = (ListView) rootview.findViewById(R.id.listview);

        //리스트를 생성한다.
        datalist = new ArrayList<String>();

        //검색에 사용할 데이터를 미리 저장한다.
        settingList();

        //리스트의 모든 데이터를 arraylist에 복사한다.
        arraydatalist = new ArrayList<String>();
        arraydatalist.addAll(datalist);

        //리스트에 연동될 아답터 생성
        adapter = new Page3_1_1_addBottomAdapter(datalist, page3_1_1_main);

        //리스트뷰에 아답터를 연결
        listView.setAdapter(adapter);

        //input창에 검색어를 입력 시 'addTextChangedListener' 이벤트 리스너를 정의
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                //입력이 시작되면 입력창을 보라색으로 바꾼다
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                      searchWindow.setBackgroundResource(R.drawable.ic_change_search);
                }
                //input창에 무자를 입력할때마다 호출된다.
                //search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });


        //바틈시트 특성상 아래로 드래그하면 바로 다이얼로그가 사라져버림
        //아래로 드래그 했을대 listview가 드래그 되도록 하기 위한 메소드
        listView.setOnTouchListener(new ListView.OnTouchListener() {
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
                v.onTouchEvent(event);
                return true;
            }
        });


        //liatview의 아이템을 클릭했을때
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener != null){
                    listener.onsetlist(datalist.get(position));
                }
                 dismiss();
            }
        });
        return rootview;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.page3_1_mapDown_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    //검색을 수행하는 메소드
    public void search(String charText){

        //문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        datalist.clear();

        //문자 입력이 없을대에는 모든 데이터를 보여준다
        if (charText.length() == 0) {
            searchWindow.setBackgroundResource(R.drawable.ic_enter_city);
            datalist.addAll(arraydatalist);
        }

        //문자 입력을 할 때
        else
        {
            //리스트의 모든 데이터를 검색한다.
            for(int i = 0; i < arraydatalist.size(); i++){
                //arraydatalist의 모든 데이터에 입력받은 단어가 포함되어 있으면 true를 반환
                if(arraydatalist.get(i).toLowerCase().contains(charText))
                {
                    //검색된 데이터를 리스트에 추가한다.
                    datalist.add(arraydatalist.get(i));
                }
            }
        }
        //리스트 데이터가 변경되었으르로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


    //검색에 사용될 데이터를 리스트에 추가
    private void settingList(){
        AssetManager am = getResources().getAssets() ;
        InputStream is = null;
        try{
            is = am.open("station3.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while(((str = reader.readLine()) != null)){ readStr += str +"\n"; }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] arr = readStr.split("\n");
        for(int i=0; i<arr.length; i++){
            code_name = arr[i].split(",");
            code[i] = code_name[0];
            name[i] = code_name[1];
            datalist.add(name[i]);
        }
    }


    //인터페이스 구현
    public interface onSetList {
        void onsetlist(String text);
    }

}

