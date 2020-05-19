package Page2_1_X;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.hansol.spot_200510_hs.R;

import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class Page2_1_X extends AppCompatActivity {

    boolean buttonState = false;
    Button add_btn;
    TextView title;
    String spot_title;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2_1_x);

        title = (TextView)findViewById(R.id.page2_1_x_title);

        scrollView = (ScrollView)findViewById(R.id.page2_1_x_scrollView);
        scrollView.smoothScrollBy(0,0);

        // 프레그먼트뷰로 넘길 ImageView
        ArrayList<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.page1_1_mint_box);
        imageList.add(R.drawable.box_round3);
        imageList.add(R.drawable.box_round2);

        // 뷰페이져와 뷰페이져 어댑터 연결
        ViewPager viewPager = findViewById(R.id.page2_1_x_viewpager);
        Page2_1_X_FragmentAdapter page2_1_1_fragmentAdapter = new Page2_1_X_FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(page2_1_1_fragmentAdapter);

        // 뷰페이저 미리보기 만들기 -> 마진값주기
        viewPager.setClipToPadding(false);
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (30 * d);
        viewPager.setPadding(margin, 0, margin, 0);
        viewPager.setPageMargin(margin / 2);

        // 프레그먼트 어댑터에 프레그먼트 추가, Image갯수만큼
        for (int i = 0 ; i < imageList.size() ; i++) {
            Page2_1_X_ImageFragment page2_1_x_imageFragment = new Page2_1_X_ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("imgRes", imageList.get(i));
            page2_1_x_imageFragment.setArguments(bundle);
            page2_1_1_fragmentAdapter.addItem(page2_1_x_imageFragment);
        }
        page2_1_1_fragmentAdapter.notifyDataSetChanged();

        // 앞에서 값 받아오기
        Intent intent = getIntent();
        spot_title = intent.getStringExtra("title");

        title.setText(spot_title);

        // 맵뷰 구현
        MapView mapView = new MapView(this);
        RelativeLayout mapViewContainer = (RelativeLayout)findViewById(R.id.page2_1_1_map);
        mapViewContainer.addView(mapView);

        // 버튼 눌림효과
        add_btn = (Button)findViewById(R.id.page2_1_1_like);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼 이미 눌려있을 때
                if (buttonState) {
                    buttonState = false;
                    add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_2);
                } else {
                    // 버튼 처음 누를 때
                    buttonState = true;
                    add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_1);
                }
            }
        });
    }
}
