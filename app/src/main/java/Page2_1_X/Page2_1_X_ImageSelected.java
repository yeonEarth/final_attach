package Page2_1_X;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hansol.spot_200510_hs.R;

public class Page2_1_X_ImageSelected extends AppCompatActivity {
    String imgList[];
    ImageView selectedImageView;
    Button left_btn, right_btn;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2_1_x_image_selected);

        selectedImageView = (ImageView) findViewById(R.id.page2_1_1_selected_image) ;
        left_btn = (Button) findViewById(R.id.page2_1_1_left_btn);
        right_btn = (Button) findViewById(R.id.page2_1_1_right_btn);

        Intent intent = getIntent();
        imgList =intent.getStringArrayExtra("setImgRes");

        // 이미지 선택하면 이미지 보여주기
        if (imgList != null) {
            Glide.with(getApplicationContext()).load(imgList[0]).centerCrop().into(selectedImageView);
        }

        // 왼쪽 버튼 눌렀을 때
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    Toast.makeText(getApplicationContext(),"처음 사진 입니다", Toast.LENGTH_SHORT).show();
                } else {
                    count --;
                    Glide.with(getApplicationContext()).load(imgList[count]).centerCrop().into(selectedImageView);
                }
            }
        });

        // 오른쪽 버튼 눌렀을 때
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == imgList.length-1) {
                    Toast.makeText(getApplicationContext(), "마지막 사진 입니다", Toast.LENGTH_SHORT).show();
                } else {
                    count ++;
                    Glide.with(getApplicationContext()).load(imgList[count]).centerCrop().into(selectedImageView);
                }
            }
        });
    }

    // 이전으로 돌아가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}

