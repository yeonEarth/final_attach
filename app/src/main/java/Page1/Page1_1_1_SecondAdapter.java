package Page1;

import android.content.Context;

import DB.DbOpenHelper;
import Page2_1_1.OnItemClick;

import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

public class Page1_1_1_SecondAdapter extends RecyclerView.Adapter<Page1_1_1_SecondAdapter.ViewHolder> {
    Context context;
    private String[] stay = new String[5];  // 하트의 클릭 여부
    private List<Page1_1_1.Recycler_item> items;  //리사이클러뷰 안에 들어갈 값 저장
    private OnItemClick mCallback;

    private List<Page1_1_1.Recycler_item> real_items;   // 찐아이템




    public Page1_1_1_SecondAdapter(List<Page1_1_1.Recycler_item> items, OnItemClick mCallback) {
        this.items = items;
        this.mCallback = mCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page1_1_1_second_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Page1_1_1.Recycler_item item=items.get(position);

        holder.title.setText(items.get(position).title);
        //이미지뷰에 url 이미지 넣기.
        Glide.with(context).load(item.getImage()).centerCrop().into(holder.imageView);
        holder.type.setText(item.type);

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stay[position] = null;
                mCallback.delete_db(item.getContentviewID());
                items.remove(position);
                notifyItemRemoved(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView type;
        TextView title;
        Button heart;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.no_image);
            type = itemView.findViewById(R.id.page1_1_1_cardview_type);
            title = itemView.findViewById(R.id.page1_1_1_cardview_title);
            heart = itemView.findViewById(R.id.page1_1_1_cardview_heart);
        }
    }
}