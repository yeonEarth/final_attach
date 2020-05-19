package Page3_1_1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.Collections;

public class Page3_1_1_adapter extends RecyclerView.Adapter<Page3_1_1_adapter.MyViewHolder> implements SwipeAndDragHelper.ActionCompletionContract{
    private ArrayList<Page3_1_1_dargData> mDataset;
    private ItemTouchHelper touchHelper;
    private Context context;


    public Page3_1_1_adapter(ArrayList<Page3_1_1_dargData> myData){
        this.mDataset = myData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page3_1_1_item, parent,false);
            return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });

        //순서부분
        if(position < 9) {
            holder.number.setText("0"+ String.valueOf(position+1));
        }
        else
            holder.number.setText(String.valueOf(position+1));

        //색깔부분
        if(position == 0) {
            holder.circle.setBackgroundResource(R.drawable.circle);
        } else if( position < mDataset.size()-1){
            holder.circle.setBackgroundResource(R.drawable.circle3);
        } else
            holder.circle.setBackgroundResource(R.drawable.circle4);


        //줄3개 누르면 드래그 됨.
        holder.dragline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });


        //삭제 버튼을 누르면 삭제 됨.
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataset.size() <4){
                    Toast.makeText(context, "더이상 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mDataset.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mDataset.size());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    //드래그 했을 때
    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Collections.swap(mDataset, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
        notifyItemChanged(oldPosition);
        notifyItemChanged(newPosition);
        //notifyItemRangeChanged(0, mDataset.size());
    }


    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }


    //content 뷰 홀더
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, number;
        public Button delete, add;
        public RelativeLayout dragline;
        public ImageView circle;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.page3_1_1_name);
            number =  itemView.findViewById(R.id.page3_1_1_number);
            delete =  itemView.findViewById(R.id.page3_1_1_delete_btn);
            add =  itemView.findViewById(R.id.page3_1_1_listAdd);
            dragline =  itemView.findViewById(R.id.page3_1_1_dragLine);
            circle =  itemView.findViewById(R.id.page3_1_1_circle);
        }
    }
}
