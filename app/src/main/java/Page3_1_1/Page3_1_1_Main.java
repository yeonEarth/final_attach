package Page3_1_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;
import com.example.hansol.spot_200510_hs.send_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Page3_1.Page3_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page3_1_1_Main extends AppCompatActivity implements Page3_1_1_addBottomSheet.onSetList, Page3_1_1_addConformDialog.GoAlgorithPage {

    Page3_1_1_Main page3_1_1_main;
    RecyclerView recyclerView;
    Button  add_city, revise_done;

    //앞에서 전달한 값
    String result, date, dayPass;

    //도시추가시 도시가 들어갈 순서를 받기 위한 변수
    int number = 0;

    //리사이클러뷰 관련
    private ArrayList<Page3_1_1_dargData> list = new ArrayList<>();
    private Page3_1_1_adapter adapter = new Page3_1_1_adapter(list);
    boolean checkStart = false;
    List<item_data> add_list = new ArrayList<>();
    ArrayList<String> result_name = new ArrayList<String>();
    ArrayList<String> result_number = new ArrayList<String>();

    //txt값을 분류하기 위한 부분
    private List<String> getdata_list = new ArrayList<String>();
    String[] code_name = null;
    String[] code = new String[237];
    String[] name = new String[237];
    String readStr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_1_main);

        //앞에서 보내는 값을 받음
        Intent intent = getIntent();
        result =  intent.getStringExtra("result");
        date = intent.getExtras().getString("date");
        dayPass = intent.getExtras().getString("dayPass");


        //리사이클러뷰
        recyclerView = (RecyclerView)findViewById(R.id.page3_1_1_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(page3_1_1_main));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        //드래그 기능
        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        adapter.setTouchHelper(touchHelper);


        //리사이클러뷰-드래그 연결
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);


        //앞에서 받아온 알고리즘 값을 정리
        ongetdata(result);


        //환승역은 걸러서 list에 추가해준다.
        for(int i =0; i < add_list.size(); i++){
            if(!add_list.get(i).getNumber().contains("환승")){
                list.add(new Page3_1_1_dargData(add_list.get(i).getName() , number));
                number ++;
            }
        }


        //도시 추가하기 버튼
        add_city = (Button) findViewById(R.id.page3_1_1_listAdd);
        add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Page3_1_1_addBottomSheet add = Page3_1_1_addBottomSheet.getInstance();
                add.show(getSupportFragmentManager(), "add");
            }
        });


        //도시추가 됐을 때 알고리즘 돌리겠냐는 바텀시트 뜸
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Page3_1_1_addConformDialog conformDialog = Page3_1_1_addConformDialog.getInstance(list.get(list.size()-1).getName(), date, dayPass);
                conformDialog.show(getSupportFragmentManager(), "confirm");
            }
        });


        //수정완료 버튼 누르면, list 순서대로 알고리즘을 돌린다.
        revise_done = (Button) findViewById(R.id.page3_1_1_reviseDone_btn);
        revise_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingList();

                //값을 전달할 리스트
                ArrayList<send_data> send_list = new ArrayList<send_data>();

                //선택된 역 이름을 번호와 함께 넘겨준다.
                for(int i=0; i<list.size(); i++){
                    for(int j=0; j<237; j++){
                        if(list.get(i).getName().equals(name[j])){
                            send_list.add(new send_data(code[j],name[j]) );
                        }
                    }
                }

                Intent intent = new Intent(Page3_1_1_Main.this, Page3_1_Main.class);
                intent.putExtra("list", (Serializable) send_list);
                intent.putExtra("date", date);  //날짜
                intent.putExtra("dayPass", dayPass);
                intent.putExtra("reRvise_done", "reRvise_done");
                overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }



    //알고리즘 값을 정리
    public void ongetdata(String result) {

        //줄바꿈 단위로 나눈 것을 개수/출발/시간/도착 으로 쪼갬
        String[] split_result = result.split("\n");
        for (int i = 0; i < split_result.length; i++) {
            String[] split_result2 = split_result[i].split(",");

            //그냥 지나감
            if (split_result[i].contains("개수") || split_result[i].contains("시간"))
                continue;

            else if (split_result[i].contains("출발")) {
                result_name.add(split_result2[1]);
                if (!checkStart) {
                    result_number.add("출발");
                    checkStart = true;
                } else
                    result_number.add("경유");
            }

            else if (split_result[i].contains("도착") && i == split_result.length - 1) {
                result_name.add(split_result2[1]);
                result_number.add("도착");
            }

            else if (split_result[i].contains("환승")) {
                result_name.add(split_result2[1]);
                result_number.add("환승");
            }
        }

        //리스트에 추가
        for (int i = 0; i < result_name.size(); i++) {
            add_list.add(new item_data(result_number.get(i), result_name.get(i)));
        }

    }



    //도시추가 바텀시트의 인터페이스
    @Override
    public void onsetlist(String text) {
        if(list.size() < 10){
            list.add(new Page3_1_1_dargData(text , number));
            adapter.notifyDataSetChanged();
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("추가할 수 있는 개수를 초과했습니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }
    }



    //추가된 역을 반영해서 새로운 알고리즘을 돌리기 위한 인터페이스
    @Override
    public void go_algorithm_page() {
        settingList();

        //값을 전달할 리스트(send_list)와 도착역과 추가역의 순서를 바꿔줄 리스트(send_list_change)
        ArrayList<send_data> send_list = new ArrayList<send_data>();
        ArrayList<send_data> send_list_change = new ArrayList<send_data>();

        //선택된 역 이름을 번호와 함께 넘겨준다.
        for(int i=0; i<list.size(); i++){
            for(int j=0; j<237; j++){
                if(list.get(i).getName().equals(name[j])){
                    send_list.add(new send_data(code[j],name[j]) );
                }
            }
        }

        //마지막 역은 추가된 역이므로 마지막전역(진짜 도착역)과 순서를 바꿔야 한다.
        send_list_change.add(new send_data(send_list.get(send_list.size()-1).getCode(), list.get(list.size()-1).getName()));
        send_list_change.add(new send_data(send_list.get(send_list.size()-2).getCode(), list.get(list.size()-2).getName()));

        send_list.remove(send_list.size()-1);
        send_list.remove(send_list.size()-1);

        send_list.add(send_list_change.get(0));
        send_list.add(send_list_change.get(1));


        Intent intent = new Intent(this, Page3_1_Main.class);
        intent.putExtra("list", (Serializable) send_list);
        intent.putExtra("date", date);  //날짜
        intent.putExtra("dayPass", dayPass);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }



    //txt 값 정리
    private void settingList() {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open("station3.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while (((str = reader.readLine()) != null)) {
                readStr += str + "\n";
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr = readStr.split("\n");
        for (int i = 0; i < arr.length; i++) {
            code_name = arr[i].split(",");
            code[i] = code_name[0];
            name[i] = code_name[1];
            getdata_list.add(name[i]);
        }
    }



    //뒤로가기 화면 전환 효과 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
    }



    public static class item_data  {
        String number;
        String name;

        String getNumber() {
            return this.number;
        }

        String getName() {
            return this.name;
        }

        public item_data(String number, String name) {
            this.number  = number;
            this.name  = name;
        }
    }


}
