package Page1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;
import Page2_1_1.OnItemClick;
import Page2_X.Page2_X_CategoryBottom;

public class Page1_1_1_Adapter extends RecyclerView.Adapter<Page1_1_1_Adapter.ViewHolder> implements OnItemClick {
    // 리사이클러뷰 안 리사이클러뷰
    Page1_1_1_SecondAdapter adapter;
    private List<Page1_1_1.Recycler_item> items;
    private DbOpenHelper mDbOpenHelper;
    private boolean isFirst = true;

    String id;

    private List<Page1_1_1.Recycler_item> listForSecond = new ArrayList<>();    // 두 번째 어댑터로 보낼 어레이


    // 어댑터에 들어갈 리스트
    private ArrayList<String > listData;
    private Context context;


    // 아이템 클릭상태 저장
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    public Page1_1_1_Adapter(ArrayList<String> listData, List<Page1_1_1.Recycler_item> items) {
        this.listData = listData;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mDbOpenHelper = new DbOpenHelper(context);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page1_1_1_item, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(isFirst) {
            if(position==0){

                selectedItems.put(position, true);
                prePosition = position;
            }
            else{
                isFirst = false;
            }
        }

        listForSecond.clear();

        for (int i = 0 ; i < items.size() ; i++) {
            if (listData.get(position).equals(items.get(i).city)) {
                listForSecond.add(items.get(i));
            }
        }

        //리사이클러뷰 넣는 부분
        holder.recyclerView.setLayoutManager( new LinearLayoutManager(context));
        adapter = new Page1_1_1_SecondAdapter(items , this);
        holder.recyclerView.setAdapter(adapter);
        holder.cityCount.setText("" + listForSecond.size());

        holder.onBind(position);
        holder.textView1.setText(listData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.get(position)) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(position);
                    holder.togle.setBackgroundResource(R.drawable.ic_down_btn);
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition);
                    // 클릭한 Item의 position을 저장
                    selectedItems.put(position, true);
                    holder.togle.setBackgroundResource(R.drawable.ic_up_btn);
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(position);
                // 클릭된 position 저장
                prePosition = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public void onClick(double x, double y, String name) {

    }

    @Override
    public void make_db(String countId, String name, String cityname, String type, String image, String click) {
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(countId, name, cityname, type, image, click);
        mDbOpenHelper.close();
    }

    @Override
    public void delete_db(String contentId) {
        mDbOpenHelper.open();
        mDbOpenHelper.deleteColumnByContentID(contentId);
        mDbOpenHelper.close();
       // items.clear();
        delete_dialog();
    }

    @Override
    public String isClick(String countid) {
        mDbOpenHelper.open();
        Cursor iCursor = mDbOpenHelper.selectIdCulumns(countid);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());

        while (iCursor.moveToNext()) {
            String userId = iCursor.getString(iCursor.getColumnIndex("userid"));

            id = userId;
        }
        mDbOpenHelper.close();

        return id;
    }

    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(context, Page1_1_1.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    public void delete_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("관심관광지 삭제 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(context, Page1_1_1.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    @Override
    public void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView1;
        private RecyclerView recyclerView;
        private int position;
        private TextView cityCount;
        private TextView togle;
        private View line;
        private ScrollView view;


        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.page1_1_1_city);
            recyclerView = itemView.findViewById(R.id.page1_1_1_fragment_recyclerview);
            cityCount = itemView.findViewById(R.id.page1_1_1_city_number);
            togle = itemView.findViewById(R.id.page1_1_1_togle);
            line = itemView.findViewById(R.id.horizon_line);
            view = itemView.findViewById(R.id.page1_1_1_view);
        }


        void onBind(int position) {
            this.position = position;
            changeVisibility(selectedItems.get(position));
        }


        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 250;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    // imageView의 높이 변경
                    recyclerView.getLayoutParams().height = value;
                    recyclerView.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    recyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }
    }
}
