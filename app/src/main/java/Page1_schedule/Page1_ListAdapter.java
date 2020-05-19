package Page1_schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

public class Page1_ListAdapter extends BaseAdapter {

    private ArrayList<Page1_Main.Api_Item> data;
    LayoutInflater layoutInflater;
    Context mContext;

    public static final int HEADER = 0;
    public static final int CHILD = 1;


    Page1_ListAdapter(Context context, ArrayList<Page1_Main.Api_Item> itemList){
        data = itemList;
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = view;
        if(convertView == null) convertView = layoutInflater.inflate(R.layout.page1_timetable_item,null);
        TextView arr_time_tv = (TextView)convertView.findViewById(R.id.page1_arrTime);
        TextView dep_time_tv = (TextView)convertView.findViewById(R.id.page1_depTime);
        TextView transfer_tv = (TextView)convertView.findViewById(R.id.page1_transfer);
        LinearLayout item_back = (LinearLayout)convertView.findViewById(R.id.item_back);
        arr_time_tv.setText(data.get(position).arrTime);
        dep_time_tv.setText(data.get(position).depTime);
        transfer_tv.setText(data.get(position).trainNumber);

        //지난 시간일 경우 글자색 gray
        if (Page1_Util.lastTime(data.get(position).depTime)) {
            arr_time_tv.setTextColor(Color.GRAY);
            dep_time_tv.setTextColor(Color.GRAY);
            transfer_tv.setTextColor(Color.GRAY);
        }
        else {
            arr_time_tv.setTextColor(Color.BLACK);
            dep_time_tv.setTextColor(Color.BLACK);
            transfer_tv.setTextColor(Color.BLACK);
        }

        return convertView;
    }

}
