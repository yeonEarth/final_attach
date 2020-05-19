package Page1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hansol.spot_200510_hs.R;

public class Page1_1_0 extends AppCompatActivity {
    Button city, course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_1_0);

        city = (Button) findViewById(R.id.page1_1_city);
        course = (Button) findViewById(R.id.page1_1_course);

        // 도시 탐색하기 눌렀을 때
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "도시 탐색하기", Toast.LENGTH_SHORT).show();
            }
        });

        // 추천 코스 눌렀을 때
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "추천 코스", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
