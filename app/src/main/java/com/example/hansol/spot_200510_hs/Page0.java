package com.example.hansol.spot_200510_hs;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Page1.Page1;

public class Page0 extends AppCompatActivity {

    TextView btn_later;
    TextView btn_start;

    int[] score = new int[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page0);

        btn_later = (TextView) findViewById(R.id.page0_later_btn);
        btn_start = (TextView) findViewById(R.id.page0_start_btn);

        // 시작하기 버튼 눌렀을 때
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Page0.this, Page0_2.class);
                startActivity(intent);
                // 액티비티 전환 효과
                overridePendingTransition(R.anim.anim_slide_out, R.anim.hold);
            }
        });

        // 나중에 하기 버튼 눌렀을 때
        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "나중에 하기 버튼 눌림", Toast.LENGTH_SHORT).show();
                // 임의의 값 넘겨서 기본 카테고리 보여주기
                score[1] = 3; score[4] = 1; score[5] = 0;
                Intent intent = new Intent(Page0.this, Page1.class);
                intent.putExtra("Main", score);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.example.hansol.spot_200510_hs", PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.e("Key Hash : ", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide, R.anim.hold);
    }
}
