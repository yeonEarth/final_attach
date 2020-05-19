package Page3_1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

public class Page3_1_fragment1 extends Fragment {
    Page3_1_Main page31Main;

    //'출발'을 한 번만 넣기 위함
    boolean checkStart = false;

    //리사이클러뷰 관련
    List<Page3_1_Main.item_data> data_list = new ArrayList<>();
    ArrayList<String> result_name = new ArrayList<String>();
    ArrayList<String> result_number = new ArrayList<String>();
    ArrayList<String> result_time = new ArrayList<String>();
    String time = "";
    Page3_1_fragment1_adapter adapter = new Page3_1_fragment1_adapter(data_list);

    //프래그먼트 생성 관련(page3_1_Main에서 돌린 알고리즘 값을 받아옴)
    public Page3_1_fragment1(){ }
    public static Page3_1_fragment1 newInstance(String text){
        Page3_1_fragment1 fragment1 = new Page3_1_fragment1();
        Bundle args = new Bundle();
        args.putString("result", text);
        fragment1.setArguments(args);
        return fragment1;
    }


    //액티비티와 프래그먼트를 붙일 때 호출되는 메소드,
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

        View rootview = inflater.inflate(R.layout.page3_1_viewpager_content, container, false);

        //액티비티에서 값을 전달 받음
        Bundle extra = getArguments();
        String result = extra.getString("result");

        //알고리즘 값을 정제해서 arraylist에 add 해줌
        ongetdata(result);

        //리사이클러뷰에 linearLayoutManager 객체지정
        RecyclerView recyclerView = (RecyclerView)rootview.findViewById(R.id.viewpager_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(page31Main));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return rootview;
    }


    //알고리즘 값을 정리
    public void ongetdata(String result) {

            //줄바꿈 단위로 나눈 것을 개수/출발/시간/도착 으로 쪼갬
            String[] split_result = result.split("\n");
            for (int i = 0; i < split_result.length; i++) {
                String[] split_result2 = split_result[i].split(",");

                //그냥 지나감
                if (split_result[i].contains("개수"))
                    continue;

                else if (split_result[i].contains("출발")) {
                    result_name.add(split_result2[1]);
                    if (!checkStart) {
                        result_number.add("출발");
                        checkStart = true;
                    } else
                        result_number.add("경유");
                }

                else if (split_result[i].contains("시간")) {
                    result_time.add(split_result2[1]);
                }

                else if (split_result[i].contains("도착") && i == split_result.length - 1) {
                    result_name.add(split_result2[1]);
                    result_number.add("도착");
                    result_time.add("");
                }

                else if (split_result[i].contains("환승")) {
                    result_name.add(split_result2[1]);
                    result_number.add("환승");
                }
            }

             //시간을 'n시간 n분' 으로 다듬기
            for (int i = 0; i < result_name.size(); i++) {

                if (result_time.get(i).length() > 0) {
                    int Hour = Integer.parseInt(result_time.get(i)) / 60;
                    int Min = Integer.parseInt(result_time.get(i)) % 60;

                    if (Hour == 0) {
                        time = Integer.toString(Min) + "분";
                    } else
                        time = Integer.toString(Hour) + "시간 " + Integer.toString(Min) + "분";

                } else {
                    time = "";
                }

                //정제한 알고리즘 값을 리스트에 넣어줌
                if(result_number.get(i).contains("환승"))
                    data_list.add( new Page3_1_Main.item_data(result_number.get(i), result_name.get(i), "환승, " + time) );
                else
                    data_list.add( new Page3_1_Main.item_data(result_number.get(i), result_name.get(i), time) );
            }
    }


}
