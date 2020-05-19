package Page3_1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.List;

public class Page3_1_fragment1_adapter extends RecyclerView.Adapter<Page3_1_fragment1_adapter.ViewHolder> {
    private List<Page3_1_Main.item_data> data = null;


    @NonNull
    @Override
    public Page3_1_fragment1_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.page3_1_viewpager_content_item, parent, false);
        Page3_1_fragment1_adapter.ViewHolder viewHolder = new Page3_1_fragment1_adapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Page3_1_Main.item_data item_data= data.get(position);
        holder.number.setText(item_data.number);
        holder.name.setText(item_data.name);
        holder.time.setText(item_data.time);
        if(item_data.time.contains("환승")) {
            holder.time.setTextColor(Color.parseColor("#c90404"));
        }

        //리사이클러뷰의 마지막 역이면 시간표 부분 안보이게 함
        if(data.size()-1 == position){
            holder.circle.setVisibility(View.INVISIBLE);
            holder.time.setVisibility(View.INVISIBLE);
        }

        //환승/경유/도착에 따라 원 색상 다르게 함
        if(data.get(position).number == "환승"){
            holder.first_circle.setBackgroundResource(R.drawable.circle2);
            holder.number.setTextColor(Color.parseColor("#4DD9A9"));
        }
        else if(data.get(position).number == "경유"){
            holder.first_circle.setBackgroundResource(R.drawable.circle3);
        }
        else if(data.get(position).number == "도착"){
            holder.first_circle.setBackgroundResource(R.drawable.circle4);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    //아이템 뷰를 저장하는 뷰홀더 클래스
    public  class ViewHolder extends RecyclerView.ViewHolder {
       TextView name;
       TextView time;
       TextView number;
       View circle;
       View first_circle;

        ViewHolder(View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조
            name = (TextView) itemView.findViewById(R.id.page3_1_1_name);
            time = (TextView) itemView.findViewById(R.id.recyclerview_time);
            number = (TextView) itemView.findViewById(R.id.page3_1_1_number);
            circle = (View) itemView.findViewById(R.id.circle2);
            first_circle = (View) itemView.findViewById(R.id.first_circle);
        }
    }


    //생성자에서 데이터 리스특 객체를 전달받음
    Page3_1_fragment1_adapter(List<Page3_1_Main.item_data> list) {
        data = list;
    }

}
