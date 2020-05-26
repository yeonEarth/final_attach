package Page2_1_1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import Page2_1_X.Page2_1_X;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hansol.spot_200510_hs.R;

import java.util.List;

import Page2.Recycler_item;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page2_1_1_CardViewAdapter extends RecyclerView.Adapter<Page2_1_1_CardViewAdapter.ViewHolder> {
    private Page2_1_1 mainActivity;
    private String[] stay = new String[5];  // 하트의 클릭 여부
    private List<Recycler_item> items;  //리사이클러뷰 안에 들어갈 값 저장
    OnItemClick mCallback;
    String cityName, click;

    Context context;

    //메인에서 불러올 때, 이 함수를 씀
    public Page2_1_1_CardViewAdapter(List<Recycler_item> items, Page2_1_1 mainActivity, String cityName,  OnItemClick mCallback) {
        this.items=items;
        this.mainActivity = mainActivity;
        this.cityName = cityName;
        this.mCallback = mCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_page2_1_1,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Recycler_item item = items.get(position);

        //이미지뷰에 url 이미지 넣기.
       Glide.with(context).load(item.getImage()).centerCrop().into(holder.image);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(context).load(item.getImage()).apply(requestOptions).into(holder.image);

        holder.title.setText(item.getTitle());
        holder.type.setText(item.getType());

        click = mCallback.isClick(item.getContentviewID());
        if(click ==null){
            click = "";
        }
        if (click.equals(item.getContentviewID())) {
            holder.heart.setBackgroundResource(R.drawable.ic_heart_off);
            stay[position] = "ON";
        } else {
            holder.heart.setBackgroundResource(R.drawable.ic_icon_addmy);
            stay[position] = null;
        }

        //하트누르면 내부 데이터에 저장
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(stay[position]==null){
                    holder.heart.setBackgroundResource(R.drawable.ic_heart_off);
                    mCallback.make_db(item.getContentviewID(), item.getTitle(),cityName, item.getType(), item.getImage(), "1");   //countId랑 title을 db에 넣으려고 함( make_db라는 인터페이스 이용)
                    mCallback.make_dialog();                                       //db에 잘 넣으면 띄우는 다이얼로그(위와 마찬가지로 인터페이스 이용
                    stay[position] = "ON";
                     Toast.makeText(context,"관심관광지를 눌렀습니다",Toast.LENGTH_SHORT).show();

                } else{
                    holder.heart.setBackgroundResource(R.drawable.ic_icon_addmy);
                    mCallback.delete_db(item.getContentviewID());
                    stay[position] = null;
                     Toast.makeText(context,"관심관광지를 취소했습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.page2_1_1_linearitem) {
                    Toast.makeText(context, item.getTitle() + "눌림", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Page2_1_X.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("contentID", item.getContentviewID());
                    intent.putExtra("contenttypeid", item.getType());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("cityname", cityName);
                    context.startActivity(intent);
                }
            }
        });

        //여기에 리스트를 클릭하면, 관광지 상세페이지로 넘어가는거 구현
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,item.getContentviewID(),Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, Page3_1_X_X.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, type;
        CardView cardview;
        Button heart;


        public ViewHolder(View itemView) {
            super(itemView);
            heart = (Button)itemView.findViewById(R.id.cardview_heart);
            image=(ImageView)itemView.findViewById(R.id.page2_1_no_image);
            title=(TextView)itemView.findViewById(R.id.page2_1_title);
            type=(TextView)itemView.findViewById(R.id.cardview_type);
            cardview=(CardView)itemView.findViewById(R.id.page2_1_cardview);
        }
    }
}
