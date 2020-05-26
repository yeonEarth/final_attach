package com.example.hansol.spot_200510_hs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import DB.Like_DbOpenHelper;
import Page1.Page1;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page0_9 extends AppCompatActivity {
    TextView result_name, result_sub_name;
    TextView edit_profile, course;
    ImageView char_img;

    ArrayList<String > a = new ArrayList<String>();

    // 다음 액티비티에 뿌릴 거
    String name, sub;

    private Like_DbOpenHelper mDbOpenHelper;
    String fav = "";    // 취향파악 저장
    String nickName = "";   // 닉네임 저장
    String db_nickName = "";   // db에 저장할 닉네임

    int[] score = new int[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page0_9);

        result_name = (TextView)findViewById(R.id.page0_9_result);
        result_sub_name = (TextView)findViewById(R.id.page0_9_type1) ;
        edit_profile = (TextView)findViewById(R.id.page0_9_edit_profile);
        course = (TextView)findViewById(R.id.page0_9_course);
        char_img = (ImageView)findViewById(R.id.page0_9_char);

        // 앞에서 값 받아오기
        final Intent intent = getIntent();
        score = intent.getIntArrayExtra("Page8");

        resultScore();

        // 프로필편집 버튼 눌렀을 때
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "프로필 편집 버튼 눌림", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Page0_9_PopUp.class);

                intent.putExtra("서브이름", sub);
                intent.putExtra("닉네임", result_name.getText().toString());
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 1);
            }
        });

        for (int i = 0 ; i < score.length - 1 ; i++) {
            fav += score[i] + " ";
        }

        db_nickName = name;

        Log.i("값 확인", fav);

        Log.i("데베 저장", fav+db_nickName);

        // 여행지 탐색 버튼 눌렀을 때
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "여행지 입력 버튼 눌림", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("Page9",score);
                startActivity(intent);

                // DB에 취향 저장하기
                mDbOpenHelper = new Like_DbOpenHelper(Page0_9.this);
                mDbOpenHelper.open();
                mDbOpenHelper.insertLikeColumn(fav, result_name.getText().toString());
                mDbOpenHelper.close();
            }
        });

    }

    // 뒤로가기 버튼 막기
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.hold, R.anim.anim_slide);
    }

    // 경우의수대로 분류하기
    public void resultScore() {
        // 유형 2
        if (score[2] == 0) {
            if (score[3] == 0) {
                name = "열정 개미";
                result_name.setText(name);
                char_img.setBackgroundResource(R.drawable.ic_ant);

            }
        }

        if (score[2] == 1 && score[3] == 1) {
            name = "평화로운 나무늘보2";
            result_name.setText(name);
            char_img.setBackgroundResource(R.drawable.ic_sloth);

        }

        if (score[2] != score[3]) {
            if (score[6] == 0) {
                name = "포토그래퍼 수달";
                result_name.setText(name);
                char_img.setBackgroundResource(R.drawable.ic_otter);
            } else if (score[2] == 1) {
                if (score[2] == 1) {
                    name = "자유로운 영혼";
                    result_name.setText(name);
                    char_img.setBackgroundResource(R.drawable.ic_soul);

                }
            } else if (score[2] == 0) {
                name = "엑셀 인간";
                result_name.setText(name);
                char_img.setBackgroundResource(R.drawable.ic_excel);

            }
        }

        // 유형 1
        if (score[1] == 0) {
            if (score[5] == 0) {
                sub = "액티비티에 관심이 많은";
                result_sub_name.setText(sub);

            }

            if (score[4] == 0 && score[5] == 1) {
                sub = "미지의 세계가 아직 낯선";
                name = "여행 초심자 - 새싹";
                result_sub_name.setText(sub);
                result_name.setText(name);
                char_img.setBackgroundResource(R.drawable.ic_sprout);

            }
            if (score[4] == 1) {
                sub = "트렌드스팟은 꼭 가보는";
                result_sub_name.setText(sub);


                if (score[5] == 0) {
                    sub = "이것저것 궁금한 게 많은";
                    name = "여행 초심자 - 병아리";
                    result_sub_name.setText(sub);
                    result_name.setText(name);
                    char_img.setBackgroundResource(R.drawable.ic_chick);
                }
            }
            if (score[4] == 2) {
                if (score[5] == 1) {
                    sub = "어디든지 다 좋은";
                    result_sub_name.setText(sub);

                }
            }
        }

        if (score[1] == 4) {
            if (score[5] == 0) {
                sub = "액티비티에 관심이 많은";
                result_sub_name.setText(sub);

            }
            if (score[4] == 1) {
                sub = "트렌드스팟은 꼭 가보는";
                result_sub_name.setText(sub);

                if (score[5] == 0) {
                    sub = "이것저것 궁금한 게 많은";
                    name = "여행 초심자 - 병아리";
                    result_sub_name.setText(sub);
                    result_name.setText(name);
                    char_img.setBackgroundResource(R.drawable.ic_chick);
                }
            }
            if (score[4] == 0 || score[4] == 2) {
                if (score[5] == 1) {
                    sub = "어디든지 다 좋은";
                    result_sub_name.setText(sub);
                }
            }
        } else if (score[1] == 1) {
            sub = "자연속에서 힐링하기 좋아하는";
            result_sub_name.setText(sub);
        } else if (score[1] == 3) {
            sub = "역사와 전통에 관심이 많은";
            result_sub_name.setText(sub);
        } else if (score[1] == 2) {
            sub = "금강산도 식후경! 전국 맛집 찾아다니는";
            result_sub_name.setText(sub);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                result_name.setText(result);
                nickName = result;
                db_nickName = nickName;
            }
        }
    }
}
