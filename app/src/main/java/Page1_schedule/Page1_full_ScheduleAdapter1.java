package Page1_schedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

public class Page1_full_ScheduleAdapter1 extends  RecyclerView.Adapter<Page1_full_ScheduleAdapter1.ViewHolder> {

    //리아시클러뷰 안 리사이클러뷰 관련
    Page1_full_ScheduleAdapter2 adapter;
    private Context context;

    //앞에서 받아온 값
    ArrayList<Page1_Main.RecycleItem> All_items;
    int dayNumber;

    //전달할 값
    ArrayList<Page1_Main.RecycleItem> Day_items;



    public Page1_full_ScheduleAdapter1(ArrayList<Page1_Main.RecycleItem> All_items, int dayNumber ){
        this.All_items = All_items;
        this.dayNumber = dayNumber;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.page1_full_schedule_item1, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        //출발 날짜 받기
        Day_items = new ArrayList<Page1_Main.RecycleItem>();
        int date = Integer.parseInt(All_items.get(0).date);
        for(int i =0; i < All_items.size(); i++){
            if(All_items.get(i).date.equals(String.valueOf(date+position))){
                Day_items.add(All_items.get(i));
                Log.i("잘넘어오고 있냐", String.valueOf(date+position) + String.valueOf(position) ) ;
            }
        }

        //리사이클러뷰 넣는 부분
        viewHolder.recyclerView.setLayoutManager( new LinearLayoutManager(context));
        adapter = new Page1_full_ScheduleAdapter2(Day_items);
        viewHolder.recyclerView.setAdapter(adapter);
    }


    public int getItemCount() { return dayNumber; }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.page1_full_schedule_recyclerview2);
        }
    }
}
