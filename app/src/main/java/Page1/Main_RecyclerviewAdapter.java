package Page1;


import android.animation.ValueAnimator;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;


public class Main_RecyclerviewAdapter extends  RecyclerView.Adapter<Main_RecyclerviewAdapter.ViewHolder> {

    //리아시클러뷰 안 리사이클러뷰 관련
    Second_RecyclerviewAdapater adapter;
    private List<String> real_items = new ArrayList<String>();

    // adapter에 들어갈 list 입니다.
    private ArrayList<String> listData;
    private Context context;

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    private boolean isFirst = true;


    public Main_RecyclerviewAdapter(ArrayList<String> data, Context context){
        this.listData = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_main_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        //순서먼저 검색
        if(listData.get(position) == "0"){

            //첫번째만 펼쳐져 있기 위함
            if(isFirst) {
                selectedItems.put(position, true);
                prePosition = position;
                isFirst = false;
                real_items.add( "나의 여행 기록");
                real_items.add( "여행유형 테스트");
            }

            viewHolder.icon.setBackgroundResource(R.drawable.ic_icon_my);
            viewHolder.textView1.setText("MY메뉴");
        }

        else if(listData.get(position) == "1"){
            viewHolder.icon.setBackgroundResource(R.drawable.ic_icon_city);
            viewHolder.textView1.setText("여행지 탐색하기");
        }

        else if(listData.get(position) == "2"){
            viewHolder.icon.setBackgroundResource(R.drawable.ic_icon_train);
            viewHolder.textView1.setText("여행 계획하기");
        }


        //어댑터 연결
        adapter = new Second_RecyclerviewAdapater(context, real_items);
        viewHolder.recyclerView.setLayoutManager( new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(adapter);


        //확장부분
        viewHolder.onBind(position);
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0:
                        real_items.clear();
                        real_items.add( "나의 여행 기록");
                        real_items.add( "여행유형 테스트");
                        break;
                    case 1:
                        real_items.clear();
                        real_items.add( "여행 카테고리 보기");
                        real_items.add( "도시 검색하기");
                        real_items.add( "내가 찜한 관광지");
                        break;
                    case 2:
                        real_items.clear();
                        real_items.add( "기차 스케쥴짜기");
                        real_items.add( "일정 등록하기");
                        break;
                    default:
                }

                if (selectedItems.get(position)) {
                    selectedItems.delete(position);

                } else {
                    selectedItems.delete(prePosition);
                    selectedItems.put(position, true);
                }

                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(position);
                prePosition = position;
            }
        });
    }



    public int getItemCount() { return listData.size(); }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout layout;
        private ImageView icon;
        private TextView textView1;
        private Button expand_btn;
        private RecyclerView recyclerView;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.menu_layout);
            icon = itemView.findViewById(R.id.menu_header_img);
            textView1 = itemView.findViewById(R.id.menu_title);
            expand_btn = itemView.findViewById(R.id.menu_expand_btn);
            recyclerView = itemView.findViewById(R.id.second_recyclerview);
        }

        void onBind(int position) {
            this.position = position;
            changeVisibility(selectedItems.get(position));
        }


        private void changeVisibility(final boolean isExpanded) {

            int dpValue = 40*real_items.size();
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    recyclerView.getLayoutParams().height = value;
                    recyclerView.requestLayout();
                    recyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    expand_btn.setBackgroundResource(isExpanded ? R.drawable.ic_down_btn : R.drawable.ic_up_btn);
                }
            });
            va.start();
        }



    }
}
