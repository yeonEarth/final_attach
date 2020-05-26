package Page3_1_1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

public class Page3_1_1_1_SecondAdapter extends RecyclerView.Adapter<Page3_1_1_1_SecondAdapter.ViewHolder> {
    Context context;
    private List<Page3_1_1_1_Main.RecycleItem> items;
    private ArrayList<String> listData;
    private OnDismiss onDismiss;
    private Page3_1_1_1_addCityBottomSheet.onSetList listener;

    public Page3_1_1_1_SecondAdapter(ArrayList<String> listData, List<Page3_1_1_1_Main.RecycleItem> items, Page3_1_1_1_addCityBottomSheet.onSetList listener, OnDismiss onDismiss) {
        this.items = items;
        this.listData = listData;
        this.listener = listener;
        this.onDismiss = onDismiss;
    }

    @NonNull
    @Override
    public Page3_1_1_1_SecondAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page3_1_1_1_addcity_second_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Page3_1_1_1_SecondAdapter.ViewHolder holder, final int position) {
        final Page3_1_1_1_Main.RecycleItem item = items.get(position);

        holder.spot.setText(items.get(position).title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, items.get(position).title + "눌렸음", Toast.LENGTH_SHORT).show();
                listener.onsetlist(item.title, item.cityName);
                onDismiss.onDismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView spot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            spot = itemView.findViewById(R.id.spot_name);
        }
    }

    public interface OnDismiss{
        void onDismiss();
    }
}
