package Page2_1_X;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;

public class Page2_1_X_RecyclerViewAdapter extends RecyclerView.Adapter<Page2_1_X_RecyclerViewAdapter.ViewHolder> {
    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    private ArrayList<Detail_item> items = new ArrayList<>();

    // 어댑터에 들어갈 리스트
    private ArrayList<String > listData;
    private Context context;

    public Page2_1_X_RecyclerViewAdapter(ArrayList<Detail_item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page2_1_1_x_recycleritem, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Detail_item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.detailContent.setText(item.getContent());

        holder.onBind(position);

        // 마지막 아이템일 때는 라인 지우기
        if (position == getItemCount() - 1 ) {
            holder.line.setVisibility(View.INVISIBLE);
            if (item.getTitle() == "기타 정보") {
                holder.content.setText("눌러서 더보기");
                holder.content.setTextAppearance(R.style.page2_1_x_moreText);
            }
        }

        // 마지막 아이템 클릭 리스너 달기
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 마지막 아이템 눌리면 반응하기
                if ( position == getItemCount() - 1 && item.getTitle() == "기타 정보" ) {
                    Toast.makeText(context, "눌렸당", Toast.LENGTH_SHORT).show();
                    if (selectedItems.get(position)) {
                        // 펼쳐진 Item을 클릭 시
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private View line;
        private TextView detailContent;
        int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.detail_title);
            content = itemView.findViewById(R.id.detail_content);
            line = itemView.findViewById(R.id.horizon_line);
            detailContent = itemView.findViewById(R.id.page2_1_x_contents);
        }

        void onBind( int position) {
            this.position = position;
            changeVisibility(selectedItems.get(position));
        }

        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 150;
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
                    detailContent.getLayoutParams().height = value;
                    detailContent.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    detailContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }
    }

    public static class Detail_item {
        String title;
        String content;
        String type;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        Detail_item(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}
