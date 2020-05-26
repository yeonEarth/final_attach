package Page1_schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;


public class Page1_full_ScheduleAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Page1_Main.RecycleItem> items = null;

    //헤더인지 아이템인지 확인하는데 필요함/ HEADER:n일차 바 / CHILD:기차시간표 / CITY:관광지 부분
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    public static final int CITY =2;


    //부모 액티비티와 연결
    Page1_full_ScheduleAdapter2(ArrayList<Page1_Main.RecycleItem> list) {
        items = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.page1_recyclerview_header, parent, false);
                Page1_full_ScheduleAdapter2.HeaderViewHolder headerViewHolder = new Page1_full_ScheduleAdapter2.HeaderViewHolder(view);
                return headerViewHolder;

            case CHILD:
                LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater2.inflate(R.layout.page1_recyclerview_item,parent,false);
                Page1_full_ScheduleAdapter2.ItemViewHolder itemViewHolder = new Page1_full_ScheduleAdapter2.ItemViewHolder(view2);
                return itemViewHolder;

            case CITY:
                LayoutInflater inflater3 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view3 = inflater3.inflate(R.layout.page1_recyclerview_cityitem, parent, false);
                Page1_full_ScheduleAdapter2.CityViewHolder cityViewHolder = new Page1_full_ScheduleAdapter2.CityViewHolder(view3);
                return cityViewHolder;
        }
        return null;
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Page1_Main.RecycleItem item = items.get(position);
        int itemViewType = getItemViewType(position);


        if (itemViewType == HEADER) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header_title.setText(item.daypass);
        }

        else if (itemViewType == CHILD) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            String station_split[] = item.station.split(",");
            itemViewHolder.mTimeText.setText(station_split[0] + " - " + station_split[1]);
            itemViewHolder.mCourseText.setText(item.train_time);
        }

        else {
            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            cityViewHolder.city_text.setText(item.contentName);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getItemViewType(int position) {
        return items.get(position).type;
    }


    //헤더 xml 연결
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            header_title =  itemView.findViewById(R.id.page1_scheedule_header);
        }
    }


    //촤일드 xml 연결
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mTimeText, mCourseText; // 시간, 경로

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCourseText =  itemView.findViewById(R.id.page1_scheedule_course);
            mTimeText =  itemView.findViewById(R.id.page1_scheedule_traintext);
        }
    }


    //시티 xml 연결
    public class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView city_text;

        public CityViewHolder(View itemView) {
            super(itemView);
            city_text = (TextView) itemView.findViewById(R.id.page1_scheedule_cityText);
        }
    }


}
