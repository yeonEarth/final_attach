package Page3_1;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

public class Page3_1_fragment2 extends Fragment {
    Page3_1_Main page31Main;

    //svg 지도로 값을 보내기 위한 핸들러
    Handler handler = new Handler();

    //출발역, 도착역을 담기 위한 변수
    String startStation, endStation;

    //경유역들을 담기 위한 변수
    List<String> middleStation = new ArrayList<>();

    //중간역(지나가는 역들)을 담기 위한 변수
    List<String> otherStation = new ArrayList<>();


    public Page3_1_fragment2(){}
    public static Page3_1_fragment2 newInstance(String text){
        Page3_1_fragment2 fragment2 = new Page3_1_fragment2();
        Bundle args = new Bundle();
        args.putString("result", text);
        fragment2.setArguments(args);
        return fragment2;
    }


    //액티비티와 프래그먼트를 붙일 대 호출되는 메소드,
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        page31Main = (Page3_1_Main) getActivity();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        page31Main= null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page3_1_viewpager_map, container, false);

        final WebView web = (WebView)v.findViewById(R.id.page3_1_map);

        //액티비티에서 값을 전달 받음
        Bundle extra = getArguments();
        String result = extra.getString("result");
        Log.i("알고리즘 값", result);


        //데이터 분류
        ongetdata(result);


        //웹뷰 자바스크립트 사용가능하도록 선언
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setDisplayZoomControls(false);  //웹뷰 돋보기 없앰
        web.setWebViewClient(new WebViewClient());


        //웹뷰 줌기능
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        web.setScrollY(70);


        //웹뷰를 로드함
        web.loadUrl("file:///android_asset/map.html");


        //이게 있어야지 지도에 나타남
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i < middleStation.size(); i++){
                    web.loadUrl("javascript:setMessage('"+ middleStation.get(i)+"', '#4DD9A9', '#1B503D')");
                }

                for(int i=0; i < otherStation.size(); i++){
                    web.loadUrl("javascript:setMessage('"+ otherStation.get(i)+"', '#b5b3b3', '#999999')");
                }

                web.loadUrl("javascript:setMessage('"+startStation+"', '#FE9D0D', '#C18428')");
                web.loadUrl("javascript:setMessage('"+endStation+"', '#FE800D', '#874407')");

            }
        }, 500); // 0.5초후


        return v;
    }


    //알고리즘 값을 정리
    public void ongetdata(String result) {
        boolean checkStart = false;

        //줄바꿈 단위로 나눈 것을 개수/출발/시간/도착 으로 쪼갬
        String[] split_result = result.split("\n");
        for (int i = 0; i < split_result.length; i++) {
            String[] split_result2 = split_result[i].split(",");

            //그냥 지나감
            if (split_result[i].contains("개수") || split_result[i].contains("시간"))
                continue;

            //첫번째 출발은 startSTation에 넣고(출발)
            //두번째 출발부터 middleStation에 넣고(경유)
            //나머지 역들은 otherSTation에 넣는다.(그냥 지나가는 역들)
            else if (split_result[i].contains("출발")) {

                if (!checkStart) {
                    startStation = split_result2[1];
                    checkStart = true;
                } else
                    middleStation.add(split_result2[1]);

                if(1 < split_result2.length -1 ){
                    for(int p =2; p < split_result2.length; p++){
                        otherStation.add(split_result2[p]);
                    }
                }
            }


            //마지막 도착만 추가
            else if (split_result[i].contains("도착") && i == split_result.length - 1) {
                endStation = split_result2[1];
            }

            //환승역은 모두 otherStation에 추가
            else if (split_result[i].contains("환승")) {
                for(int p =1; p < split_result2.length; p++){
                    otherStation.add(split_result2[p]);
                }
            }
        }
    }



}
