package Page3_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hansol.spot_200510_hs.R;
import com.example.hansol.spot_200510_hs.send_data;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;

import Algorithm.Subway;
import Algorithm.SubwayBuilder;
import Algorithm.SubwayController;
import Page3_1_1.Page3_1_1_Main;
import Page3_1_1_1.Page3_1_1_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page3_1_Main extends AppCompatActivity {

    //뷰페이져 관련
    ViewPager viewPager;
    Page3_VPAdapter vpAdapter;
    TabLayout tabLayout;
    ArrayList<send_data> list = new ArrayList<send_data>();

    //이전 액티비티에서 받은 값 관련
    String year, month, day, dayPass;
    String isRevise_done = "false";
    String[] text = new String[10];
    int number = 0;
    String date;

    //알고리즘
    SubwayController controller;
    SubwayBuilder builder;
    Subway subway = null;
    String result = "";
    Context context;

    boolean checkStart = false;     //'출발'을 한 번만 넣기 위함
    ArrayList<String> next_data = new ArrayList<>();
    ArrayList<String> result_name = new ArrayList<String>();
    ArrayList<String> result_number = new ArrayList<String>();


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_main);

        //객체 연결
        context = getApplicationContext();
        builder = new SubwayBuilder();


        //앞에서 값을 받아온다.(1)
        Intent get = getIntent();
        list = (ArrayList<send_data>)get.getSerializableExtra("list");
        number = list.size();
        date = get.getExtras().getString("date");
        dayPass = get.getExtras().getString("dayPass");



        //수정완료 페이지에서 값을 받아오면. (2)
        isRevise_done = get.getExtras().getString("reRvise_done");


        //앞에서 받은 값에서 출발,도착,경유역 정리
        text[0] = (String) list.get(0).getCode();
        text[list.size() - 1] = (String) list.get(list.size() - 1).getCode();
        for (int i = 1; i < list.size() - 1; i++) { text[i] = (String) list.get(i).getCode(); }


        //알고리즘 실행(1) : 최단거리알고리즘
        if(isRevise_done == null){
            algorithm();
        }


        //알고리즘 실행(2) : 지정한 순서대로
        else {
            fix_order_algorithm();
        }


        //뷰페이져 연결
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        tabLayout=(TabLayout)findViewById(R.id.tablayout);
        vpAdapter = new Page3_VPAdapter(getSupportFragmentManager());
        vpAdapter.getText(result);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(vpAdapter);


        //수정하기 버튼 누르면
        Button revise_btn = (Button)findViewById(R.id.page3_1_revise_btn);
        revise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page3_1_Main.this, Page3_1_1_Main.class);
                intent.putExtra("result", result);           //추가된 역
                intent.putExtra("date", date);  //날짜
                intent.putExtra("dayPass", dayPass);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        //다음단계 버튼 누르면
        Button nextStep_btn = (Button) findViewById(R.id.page3_1_nextstep_btn);
        nextStep_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forPage3_1_1_1(result);
                Intent intent = new Intent(Page3_1_Main.this, Page3_1_1_1_Main.class);
                intent.putExtra("next_data", next_data);
                intent.putExtra("date", date);  //날짜
                intent.putExtra("dayPass", dayPass);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }


    //알고리즘 실행
    public void algorithm(){
        // 빌더를 생성한다. - 여기서 txt 파일 받아옴
        try {
            builder.readFile(getApplicationContext(), "station3.txt", "link3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Algorithm.SubwayException e) {
            e.printStackTrace();
        }

        // 지하철 클래스를 만든다.
        subway = builder.build();

        // 검색을 위한 컨트롤러를 만든든다.
        controller = new SubwayController(subway);

        //앞에서 받은 값이 뭐냐에 따라 알고리즘 다르게 진행
        switch (number) {
            case 3:
                //경유지가 1개인 경우
                result = middle_number_1(text[0], text[1], text[2]);
                break;
            case 4:
                //경유지가 2개인 경우
                result = compare_2(text[0], text[1], text[2], text[3]);
                break;
            case 5:
                //경유지가 3개인 경우
                result = compare_3(text[0], text[1], text[2], text[3], text[4]);
                break;
            case 6:
                //경유지가 4개인 경우
                result = compare_4(text[0], text[1], text[2], text[3], text[4], text[5]);
                break;
            case 7:
                //경유지가 5개인 경우
                result = compare_5(text[0], text[1], text[2], text[3], text[4], text[5], text[6]);
                break;
            case 8:
                //경유지가 6개인 경우
                result = compare_6(text[0], text[1], text[2], text[3], text[4], text[5], text[6], text[7]);
                break;
            case 9:
                //경유지가 7개인 경우
                result = compare_7(text[0], text[1], text[2], text[3], text[4], text[5], text[6], text[7], text[8]);
                break;
            case 10:
                //경유지가 8개인 경우
                result = compare_8(text[0], text[1], text[2], text[3], text[4], text[5], text[6], text[7], text[8], text[9]);
                break;
            default:
                break;
        }
    }


    //지정한 순서대로 알고리즘 실행
    public void fix_order_algorithm(){
        // 빌더를 생성
        try {
            builder.readFile(getApplicationContext(), "station3.txt", "link3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Algorithm.SubwayException e) {
            e.printStackTrace();
        }

        // 지하철 클래스
        subway = builder.build();

        // 검색을 위한 컨트롤러
        controller = new SubwayController(subway);

        for(int i =0; i < list.size()-1; i++){
            String search = controller.search(text[i], text[i+1]);

            if(i == list.size()-2)
                result = result + search;
            else
                result = result + search + "\r\n";
        }
    }


    //알고리즘 값을 정리
    public void forPage3_1_1_1(String result) {

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

        //리스트에 저장
        for (int i = 0; i < result_name.size(); i++) {
            next_data.add(result_number.get(i)+ "," + result_name.get(i));
        }
    }


    /**알고리즘 관련**************************************************************************************************/

    //경유지가 1개인 경우
    private String middle_number_1(String station1, String station2, String station3) {
        String search_1 = controller.search(station1, station2); //출발->경유1
        String search_2 = controller.search(station2, station3); //경유1->도착

        return search_1 + "\r\n" + search_2;
    }

    //경유지가 2개인 경우
    public String middle_number_2(String station1, String station2, String station3) {

        String search_1 = controller.search(station1, station2);  //출발->경유1
        String search_2 = controller.search(station1, station3);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return station2;
        }

        else {
            return station3;
        }
    }
    public String compare_2(String station1, String station2, String station3, String station4){
        //경유지가 2개인 경우
        String result = middle_number_2(station1, station2, station3);
        String getText[] = {station1, station2, station3, station4};
        number = 4;
        Log.i("경유지가 2개인 경우의 첫번재 경유역 : ", result);
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }
        Log.i("경유지가 2개에서 reStation0 : ", reStation[0]);
        Log.i("경유지가 2개에서 reStation1 : ", reStation[1]);
        Log.i("경유지가 2개에서 reStation2 : ", reStation[2]);

        String middle = controller.search(station1, reStation[0]);
        String middle2 = controller.search(reStation[0], reStation[1]);
        String middle3 = controller.search(reStation[1], reStation[2]);

        if(!reStation[2].equals(station4)){
            String middle4 = controller.search(reStation[2], station4);
            return middle + "\n" + middle2 +"\n" + middle3 + "\n" + middle4;
        }else
            return middle + "\n" + middle2 +"\n" + middle3;
    }

    //경유지가 3개인 경우
    public String middle_number_3(String station1, String station2, String station3, String station4) {

        String middle1 = middle_number_2(station1, station2, station3);
        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, station4);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        }

        else
            return station4;
    }
    public String compare_3(String station1, String station2, String station3, String station4, String station5){
        //경유지가 3개인 경우
        String result = middle_number_3(station1, station2, station3, station4);
        String getText[] = {station1, station2, station3, station4, station5};

        Log.i("경유지가 3개인 경우의 첫번재 경유역 : ", result);
        number = 5;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }

        Log.i("경유지가 3개에서 reStation0 : ", reStation[0]);
        Log.i("경유지가 3개에서 reStation1 : ", reStation[1]);
        Log.i("경유지가 3개에서 reStation2 : ", reStation[2]);
        Log.i("경유지가 3개에서 reStation3 : ", reStation[3]);

        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_2(reStation[0],reStation[1],reStation[2],reStation[3]);

        return middle + "\n" + middle2;
    }

    //경유지가 4개인 경우
    public String middle_number_4(String station1, String station2, String station3, String station4, String station5) {

        String middle1 = middle_number_2(station1, station2, station3);
        String middle2 = middle_number_2(station1, station4, station5);

        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, middle2);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        }

        else {
            return middle2;
        }
    }
    public String compare_4(String station1, String station2, String station3, String station4, String station5, String station6) {
        //경유지가 4개인 경우
        String result = middle_number_4(station1, station2, station3, station4, station5);
        String getText[] = {station1, station2, station3, station4, station5, station6};

        number = 6;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }
        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_3(reStation[0],reStation[1],reStation[2],reStation[3],reStation[4]);
        return middle + "\n" + middle2;
    }

    //경유지가 5개인 경우
    public String middle_number_5(String station1, String station2, String station3, String station4, String station5, String station6) {

        String middle1 = middle_number_3(station1, station2, station3, station4);
        String middle2 = middle_number_2(station1, station5, station6);

        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, middle2);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        } else
            return middle2;
    }
    public String compare_5(String station1, String station2, String station3, String station4, String station5, String station6, String station7){
        //경유지가 5개인 경우
        String result = middle_number_5(station1, station2, station3, station4, station5, station6);
        String getText[] = {station1, station2, station3, station4, station5, station6, station7};

        number = 7;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }

        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_4(reStation[0],reStation[1],reStation[2],reStation[3],reStation[4],reStation[5]);
        return middle + "\n" + middle2;
    }

    //경유지가 6개인 경우
    public String middle_number_6(String station1, String station2, String station3, String station4, String station5, String station6, String station7) {

        String middle1 = middle_number_3(station1, station2, station3, station4);
        String middle2 = middle_number_3(station1, station5, station6, station7);

        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, middle2);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        } else
            return middle2;
    }
    public String compare_6(String station1, String station2, String station3, String station4, String station5, String station6, String station7, String station8){
        //경유지가 6개인 경우
        String result = middle_number_6(station1, station2, station3, station4, station5, station6, station7);
        String getText[] = {station1, station2, station3, station4, station5, station6, station7, station8};

        number = 8;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }

        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_5(reStation[0],reStation[1],reStation[2],reStation[3],reStation[4],reStation[5],reStation[6]);
        return middle + "\n" + middle2;
    }

    //경유지가 7개인 경우
    public String middle_number_7(String station1, String station2, String station3, String station4, String station5, String station6, String station7, String station8) {

        String middle1 = middle_number_3(station1, station2, station3, station4);
        String middle2 = middle_number_4(station1, station5, station6, station7, station8);

        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, middle2);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        } else
            return middle2;
    }
    public String compare_7(String station1, String station2, String station3, String station4, String station5, String station6, String station7, String station8, String station9){
        //경유지가 7개인 경우
        String result = middle_number_7(station1, station2, station3, station4, station5, station6, station7, station8);
        String getText[] = {station1, station2, station3, station4, station5, station6, station7, station8, station9};

        number = 9;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }

        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_6(reStation[0],reStation[1],reStation[2],reStation[3],reStation[4],reStation[5],reStation[6], reStation[7]);
        return middle + "\n" + middle2;
    }

    //경유지가 8개인 경우
    public String middle_number_8(String station1, String station2, String station3, String station4, String station5, String station6, String station7, String station8, String station9) {

        String middle1 = middle_number_4(station1, station2, station3, station4, station5);
        String middle2 = middle_number_4(station1, station6, station7, station8, station9);

        String search_1 = controller.search(station1, middle1);  //출발->경유1
        String search_2 = controller.search(station1, middle2);  //출발->경유2

        //size(지나간 역 개수)만 추출한다.
        String [] split1 = search_1.split("\n");
        String [] split2 = search_2.split("\n");
        int search_1_int = Integer.parseInt(split1[0].replaceAll("[^0-9]", ""));
        int search_2_int = Integer.parseInt(split2[0].replaceAll("[^0-9]", ""));

        //size 비교해서 최소값을 반환한다. (값이 같으면 첫번째를 반환함)
        int min = Math.min(search_1_int, search_2_int);

        if (min == search_1_int) {
            return middle1;
        } else
            return middle2;
    }
    public String compare_8(String station1, String station2, String station3, String station4, String station5, String station6, String station7, String station8, String station9, String station10){
        //경유지가 8개인 경우
        String result = middle_number_8(station1, station2, station3, station4, station5, station6, station7, station8, station9);
        String getText[] = {station1, station2, station3, station4, station5, station6, station7, station8, station9, station10};

        number = 10;
        String[] reStation = new String[number-1];
        for(int i = 0; i < number-1; i++){
            if(!result.equals(getText[i+1])){
                reStation[i+1] = getText[i+1];
            }else{
                reStation[0] = getText[i+1];
                while(i < number-2){
                    reStation[i+1] = getText[i+2];
                    i++;
                }break;
            }
        }

        String middle = controller.search(station1, reStation[0]);
        String middle2 = compare_7(reStation[0],reStation[1],reStation[2],reStation[3],reStation[4],reStation[5],reStation[6], reStation[7], reStation[8]);
        return middle + "\n" + middle2;
    }


    //arraylist 구성 클래스
    public static class item_data  {
        String number;
        String name;
        String time;

        String getNumber() {
            return this.number;
        }

        String getName() {
            return this.name;
        }

        String getTime() {
            return this.time;
        }

        public item_data(String number, String name, String time) {
            this.number  = number;
            this.name  = name;
            this.time = time;
        }
    }


    //페이지가 꺼지면 모든 리스트를 초기화
    @Override
    protected void onPause() {
        super.onPause();
        next_data.clear();
        result_name.clear();
        result_number.clear();
    }


    //뒤로가기 버튼 누를때 생기는 화면전환 효과 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
    }


}
