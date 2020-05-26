package Page1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.Page0;
import com.example.hansol.spot_200510_hs.R;

import java.util.List;

import Menu.Page4_2;
import Page2_X.Page2_X;
import Page3.Page3_Main;

public class Second_RecyclerviewAdapater extends RecyclerView.Adapter<Second_RecyclerviewAdapater.ViewHolder> {

    Context context;
    private int position2;
    private List<String> items;  //리사이클러뷰 안에 들어갈 값 저장

    public Second_RecyclerviewAdapater(Context context, List<String> items){
        this.context = context;
        this.items =  items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_second_recyclerview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.subTitle.setText(items.get(position));
        holder.subTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (items.get(position)){
                    case "나의 여행 기록":
                        Intent intent = new Intent(context, Page4_2.class);
                        context.startActivity(intent);
                        break;
                    case "여행유형 테스트":
                        Intent intent2 = new Intent(context, Page0.class);
                        context.startActivity(intent2);
                        break;
                    case "여행 카테고리 보기":
                        Intent intent3 = new Intent(context, Page2_1.class);
                        context.startActivity(intent3);
                        break;
                    case "도시 검색하기":
                        Intent intent4 = new Intent(context, Page2_X.class);
                        context.startActivity(intent4);
                        break;
                    case "내가 찜한 관광지":
                        Intent intent5 = new Intent(context, Page1_1_1.class);
                        context.startActivity(intent5);
                        break;
                    case "기차 스케쥴짜기":
                        Intent intent6 = new Intent(context, Page3_Main.class);
                        context.startActivity(intent6);
                        break;
                    case "일정 등록하기":
                        Toast.makeText(context, "아직안만들엇다", Toast.LENGTH_SHORT).show();
//                        Intent intent7 = new Intent(context, Page3_Main.class);
//                        context.startActivity(intent7);
                        break;
                    default:
                }
                Log.i("눌렸다", items.get(position)+"-"+position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.menu_subTitle);
        }
    }
}
