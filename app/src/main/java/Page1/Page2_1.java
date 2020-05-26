package Page1;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.hansol.spot_200510_hs.R;

import Page2.Page2;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page2_1 extends AppCompatActivity implements View.OnClickListener {
    /*
     * subject_1 = 자연
     * subject_2 = 역사
     * subject_3 = 휴양
     * subject_4 = 체험
     * subject_5 = 산업
     * subject_6 = 건축/조형
     * subject_7 = 문화
     * subject_8 = 레포츠
     */

    int[] button_id = {R.id.page2_1_subject_1, R.id.page2_1_subject_2, R.id.page2_1_subject_3,
            R.id.page2_1_subject_4, R.id.page2_1_subject_5, R.id.page2_1_subject_6,
            R.id.page2_1_subject_7, R.id.page2_1_subject_8};
    String[] subject_name = {"자연", "역사", "휴양", "체험", "산업", "건축/조형", "문화", "레포츠"};
    Button[] subject = new Button[button_id.length];

    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2_1);

        for(int i = 0; i<button_id.length; i++){
            subject[i] = (Button) findViewById(button_id[i]);
            subject[i].setOnClickListener(this);


        }
        //뒤로가기 버튼 구현
        ImageView back_btn = (ImageView) findViewById(R.id.all_cat_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        for(int i =0; i < subject.length; i++) {

            if(subject[i].getId() == view.getId()) {
                Intent intent = new Intent(this, Page2.class);
                intent.putExtra("subject_name_from_Page2_1", subject_name[i]);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
    }


    //뒤로가기 화면 전환 효과 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
    }
}
