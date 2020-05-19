package Page3_1_1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

public class Page3_1_1_1_bottomSheet_Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<Api_Item> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public static final int HEADER = 0;
    public static final int CHILD = 1;


    public Page3_1_1_1_bottomSheet_Adapter(ArrayList<Api_Item> list, Context context){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.page3_1_1_1_apisheet_item, null);
            viewHolder = new ViewHolder();
            viewHolder.depTime = convertView.findViewById(R.id.api_dep) ;
            viewHolder.arrTime = convertView.findViewById(R.id.api_arr) ;
            viewHolder.startCityNAme = convertView.findViewById(R.id.api_startCity) ;
            viewHolder.trainNumber = convertView.findViewById(R.id.api_trainNumber) ;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.depTime.setText(list.get(position).depTime);
        viewHolder.arrTime.setText(list.get(position).arrTime);
        viewHolder.startCityNAme.setText(list.get(position).spendTime);
        viewHolder.trainNumber.setText(list.get(position).trainNumber);

        return convertView;
    }

    class ViewHolder{
        TextView depTime ;
        TextView arrTime ;
        TextView startCityNAme ;
        TextView trainNumber ;
    }


    //아이템 변수 선언
    public static class Api_Item implements Comparable<Api_Item>{
        int type;
        String depTime;
        String arrTime;
        String spendTime;
        String trainNumber;

        public Api_Item(int type, String dep, String arr, String spend, String train){
            this.type = type;
            this.depTime = dep;
            this.arrTime = arr;
            this.spendTime = spend;
            this.trainNumber= train;
        }

        String getDepTime() {
            return this.depTime;
        }
        String getArrTime() {
            return this.arrTime;
        }
        String getSpendTime() {
            return this.spendTime;
        }
        String getTrainNumber() {
            return this.trainNumber;
        }

        @Override
        public int compareTo(Api_Item o) {
            return this.depTime.compareTo(o.depTime);
        }
    }
}
