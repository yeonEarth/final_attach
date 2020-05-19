package Page2_1_1;

import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hansol.spot_200510_hs.R;
//import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class Page2_1_1_ViewPagerAdapter extends RecyclerView.Adapter<Page2_1_1_ViewPagerAdapter.ViewHolder> implements Page2_1_1.Recyclerview_Rearrange {

    //뷰페이져 관련
    private Context context;
    private ArrayList<course> items;
    private FragmentManager fragmentManager;
    private Page2_1_1.Recyclerview_Rearrange recyclerview_rearrange;

    //뷰페이져 화면 up&down 관련
    private String determine_API = "delete";
    private int prePosition = -1;
    private boolean isFirst = true;
    public static SparseBooleanArray selectedItems = new SparseBooleanArray();

    //Activity와 어댑터를 연결
    public Page2_1_1_ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<course> items, Page2_1_1.Recyclerview_Rearrange recyclerview_rearrange) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.recyclerview_rearrange = recyclerview_rearrange;
    }

    @Override
    public Page2_1_1_ViewPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_page2_1_1_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Page2_1_1_ViewPagerAdapter.ViewHolder holder, final int position) {
        //첫번째 아이템은 펼쳐져서 보임
        if(isFirst) {
            if(position==0){

                //height 값을 임의로 준다.
                int dpValue = 380;
                float d = context.getResources().getDisplayMetrics().density;
                int height = (int) (dpValue * d);
                holder.vp_bg.getLayoutParams().height = height;
                holder.vp_bg.requestLayout();

                selectedItems.put(position, true);
                prePosition = position;

                //인터넷 유무 체크
                int isNetworkConnect = NetworkStatus.getConnectivityStatus(context);
                if(isNetworkConnect == 3) {
                    Toast.makeText(context, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
                    determine_API = "delete";
                } else
                    determine_API = "make";
            }
            else{
                determine_API = "delete";
                isFirst = false;
            }
        }

        //프래그먼트 어댑터와 연결(프래그먼트를 리사이클러뷰 사이즈만큼 생성)
        final BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(fragmentManager,position, determine_API);
        holder.vp.setAdapter(bannerPagerAdapter);
        holder.vp.setId(position+1);

        holder.tabLayout.setupWithViewPager(holder.vp);
        holder.onBind(position);

        //number
        holder.number_btn.setText("0"+ Integer.toString(position+1));

        //뷰페이져 화면 Up&Down 버튼 누르면
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * (true = 화면이 보이는 상태) -> 화면을 지워준다.
                 * 'delete' 를 프래그먼트에 전달하면 API를 실행하지 않는다.
                 *
                 * (false = 화면이 접혀있는 상태) -> 화면을 보여준다.
                 * 'make' 를 프래그먼트에 전달하면 API 연결을 실행하고 리사이클러뷰에 뿌려준다.
                 *
                 * preposition 은 이전에 눌렀던 화면 위치를 저장하는 변수.
                 * */

                if(selectedItems.get(position)){
                    selectedItems.delete(position);
                    determine_API = "delete";
                }
                else {
                    //인터넷 유무 체크
                    int isNetworkConnect = NetworkStatus.getConnectivityStatus(context);
                    if(isNetworkConnect == 3) {
                        Toast.makeText(context, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
                        determine_API = "delete";
                    } else
                        determine_API = "make";

                    selectedItems.delete(prePosition);
                    selectedItems.put(position, true);
                }
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(position);
                prePosition = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //뒤로가기 눌렀을때 현재 확장되어있는 레이아웃을 닫아준다.
    @Override
    public void onRearrange() {
        selectedItems.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewPager vp;
        TabLayout tabLayout;
        private  int position;
        RelativeLayout vp_bg;
        Button btn, number_btn, updown_img;
        TextView tab_customText;

        public ViewHolder(View itemView) {
            super(itemView);
            vp =  itemView.findViewById(R.id.page2_1_viewpager);
            tabLayout=itemView.findViewById(R.id.tabs);
            btn = itemView.findViewById(R.id.page2_1_updown_btn);
            vp_bg = itemView.findViewById(R.id.viewpager_backgound);
            number_btn = itemView.findViewById(R.id.page2_1_number_btn);
            updown_img = itemView.findViewById(R.id.updown_img);
            tab_customText = itemView.findViewById(R.id.tab_customText);
        }

        void onBind( int position) {
            this.position = position;
            changeVisibility(selectedItems.get(position));
        }

        //화면을 생성할때 부드럽게 주기위한 애니메이션 함수
        private void changeVisibility(final boolean isExpanded) {

            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 380;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            va.setDuration(600);   // Animation이 실행되는 시간, n/1000초
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    int value = (int) animation.getAnimatedValue();                // value는 height 값
                    vp_bg.getLayoutParams().height = value;                        // imageView의 높이 변경
                    vp_bg.requestLayout();
                    vp.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE);   // imageView가 실제로 사라지게하는 부분
                    updown_img.setBackgroundResource(isExpanded ? R.drawable.ic_down_btn : R.drawable.ic_up_btn);
                }
            });
            va.start();
        }

    }

    //프래그먼트&탭레이아웃 연결 어댑터
    public class BannerPagerAdapter extends FragmentStatePagerAdapter {

        //api를 연결할지 말지를 결정 (delete of make 의 값을 넣음)
        String determine_API;
        int number;

        //뷰페이저와 프래그먼트를 연결해주는 부분
        public BannerPagerAdapter(FragmentManager fm, int i, String determine_API) {
            super(fm);
            this.number = i;
            this.determine_API = determine_API;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Page2_1_1_Fragment.newInstance(items.get(number).subject, items.get(number).st1, determine_API);
                case 1:
                    return Page2_1_1_Fragment.newInstance(items.get(number).subject, items.get(number).st2, determine_API);
                case 2:
                    return Page2_1_1_Fragment.newInstance(items.get(number).subject, items.get(number).st3, determine_API);
                case 3:
                    return Page2_1_1_Fragment.newInstance(items.get(number).subject, items.get(number).st4, determine_API);
                default:
                    return null;
            }
        }

        public String getText(String text) {
            this.determine_API = text;
            return determine_API;
        }

        //생성될 프래그먼트 개수
        @Override
        public int getCount() {
            return 4;
        }

        //탭레이아웃에 값을 넣음
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return items.get(number).st1;
                case 1:
                    return items.get(number).st2;
                case 2:
                    return items.get(number).st3;
                case 3:
                    return items.get(number).st4;
                default:
                    return null;
            }
        }
    }
}
