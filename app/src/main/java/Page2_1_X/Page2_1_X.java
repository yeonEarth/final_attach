package Page2_1_X;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hansol.spot_200510_hs.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import DB.DbOpenHelper;
import DB.Heart_page;
import Page1.Page1_1_1;
import Page2_1_1.OnItemClick;
import Page2_X.Page2_X_CategoryBottom;

public class Page2_1_X extends AppCompatActivity implements OnItemClick {

    String Url_front = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=";
    Double x, y;                                         //전달받은 역의 x,y 좌표

    // DB
    OnItemClick mCallBack;
    private DbOpenHelper mDbOpenHelper;

    //name_1를 "  " 단위로 쪼개서 넣은 배열
    String name_1[];    // 공통정보
    String detailIntro[];   // 소개정보
    String detailInfo[]; // 반복 정보
    String real_detailInfo[];
    String detailImage[];

    ArrayList<Integer> defaultImage = new ArrayList<>();    // 이미지 없을 때 보여지는 화면

    String returnResult, url, id, click;
    String addr;

    boolean buttonState = false;
    Button add_btn;
    TextView title;
    String spot_title;
    String contentID;
    String image, cityName;
    String type, cat1, cat2 = "";    // 앞에서 받아온 타입 저장
    String contentTypeId;   // 아이디로 저장
    ImageView back_btn;

    boolean isExpand = false;

    MapPOIItem marker;
    MapView mapView;

    ScrollView scrollView;

    Page2_1_X_RecyclerViewAdapter adapter;
    private ArrayList<Page2_1_X_RecyclerViewAdapter.Detail_item> items = new ArrayList<>(); // 어댑터로 넘길 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2_1_x);

        // 이거 선언해야 DB들어감,,,,, 개뻘짓거리했슴,, 8ㅅ8
        mCallBack = Page2_1_X.this;

        title = (TextView)findViewById(R.id.page2_1_x_title);

        scrollView = (ScrollView)findViewById(R.id.page2_1_x_scrollView);
        scrollView.smoothScrollBy(0,0);

        back_btn = (ImageView) findViewById(R.id.page2_1_x_back_btn);

        //데이터베이스 관련
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        // 뒤로가기 버튼 눌렀을 때
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        defaultImage.add(R.drawable.no_image);

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

        // 앞에서 값 받아오기
        Intent intent = getIntent();
        spot_title = intent.getStringExtra("title");
        contentID = intent.getStringExtra("contentID");
        type = intent.getStringExtra("contenttypeid");
        image = intent.getStringExtra("image");
        cityName = intent.getStringExtra("cityname");

        url_code();

        title.setText(spot_title);

        Log.i("왜?ㅋ", contentID+spot_title+type);

        // 버튼 눌림효과
        add_btn = (Button) findViewById(R.id.page2_1_1_like);

        click = mCallBack.isClick(contentID);
        if (click.equals(contentID)) {
            buttonState = true;
            add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_2);
        } else {
            buttonState = false;
            add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_1);
        }

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼 이미 눌려있을 때
                if (buttonState) {
                    buttonState = false;
                    add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_1);
                    mCallBack.delete_db(contentID);
                    Toast.makeText(getApplicationContext(),"관심관광지를 취소했습니다",Toast.LENGTH_SHORT).show();
                } else {
                    // 버튼 처음 누를 때
                    buttonState = true;
                    add_btn.setBackgroundResource(R.drawable.ic_icon_add_float_2);
                    mCallBack.make_db(contentID, spot_title, cityName, type, image, "1");   //countId랑 title을 db에 넣으려고 함( make_db라는 인터페이스 이용)
                    mCallBack.make_dialog();                                       //db에 잘 넣으면 띄우는 다이얼로그(위와 마찬가지로 인터페이스 이용
                    Toast.makeText(getApplicationContext(),"관심관광지를 추가했습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 맵뷰 구현
        mapView = new MapView(this);
        RelativeLayout mapViewContainer = (RelativeLayout)findViewById(R.id.page2_1_1_map);
        mapViewContainer.addView(mapView);
//        mapView.setClickable(true);
        marker = new MapPOIItem();

        settingAPI_Data();

        Log.i("경도 위도", x+"  " + y);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(y, x), 2, true);

        if (detailImage != null) {
            for (int i = 0 ; i < detailImage.length ; i++) {
                Page2_1_X_ImageFragment page2_1_x_imageFragment = new Page2_1_X_ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("imgRes", detailImage[i]);
                bundle.putStringArray("imgRes3", detailImage);
                page2_1_x_imageFragment.setArguments(bundle);
                page2_1_1_fragmentAdapter.addItem(page2_1_x_imageFragment);
            }

            for (int i = 0 ; i < detailImage.length ; i++) {
                Log.i("보내지나요?", detailImage[i]);
            }
            page2_1_1_fragmentAdapter.notifyDataSetChanged();
        }
        // 이미지 없을 때
        else {
            for (int i = 0 ; i < defaultImage.size(); i++) {
                Page2_1_X_ImageFragment page2_1_x_imageFragment = new Page2_1_X_ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("imgRes2", defaultImage.get(i));
                page2_1_x_imageFragment.setArguments(bundle);
                page2_1_1_fragmentAdapter.addItem(page2_1_x_imageFragment);
            }
            page2_1_1_fragmentAdapter.notifyDataSetChanged();
        }

        //지도가 확대되지 않도록 함
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isExpand)
                    return true;
                else
                    return false;
            }
        });

        // 리사이클러뷰 구현
        RecyclerView recyclerView = findViewById(R.id.page2_1_x_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Page2_1_X_RecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);
    }

    // api 연결 후 값 정제
    private void settingAPI_Data() {
        SearchTask task = new SearchTask();
        SearchTask2 task2 = new SearchTask2();
        SearchTask3 task3 = new SearchTask3();
        SearchTask4 task4 = new SearchTask4();
        // 공통정보 값
        try {
            String RESULT = task.execute().get();
            Log.i("전달 받은 값", RESULT);

            if (RESULT.length() != 0) {

                addr = "";
                String mapx = "";
                String mapy = "";
                String name = "";
                String tel = "";

                // 묶인 값 분류하기
                name_1 = RESULT.split("  ");

                if (name_1.length == 5) {
                    addr = name_1[0];
                    mapx = name_1[1];
                    mapy = name_1[2];
                    tel = name_1[3];
                    name = name_1[4];
                    Log.i("y: ", mapy);
                } else {
                    addr = name_1[0];
                    mapx = name_1[1];
                    mapy = name_1[2];
                    name = name_1[3];
                    tel = null;
                    Log.i("y: ", mapy);
                }

                Log.i("주소: ", addr);
                Log.i("x: ", mapx);
                Log.i("y: ", mapy);
                Log.i("이름: ", name);

                // 값이 있을 경우에 넣어주기
                if (tel != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("연락처", tel));
                }
                if (addr != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("주소", addr));
                }

                //마커 만들기
                x = Double.valueOf(mapx);
                y = Double.valueOf(mapy);
                marker.setTag(0);
                marker.setItemName(addr);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(y, x));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(marker);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 소개 정보 값
        try {
            String RESULT = task2.execute().get();
            Log.i("소개정보 전달 받은 값", RESULT);

            if (RESULT.length() != 0) {
                // 묶인 값 분류하기
                detailIntro = RESULT.split("  ");

                String infoCenter = "";
                String useDate = "";
                String useTime = "";

                if (detailIntro.length == 1) {
                    infoCenter = detailIntro[0];
                    useDate = null;
                    useTime = null;
                } else if (detailIntro.length == 2) {
                    infoCenter = detailIntro[0];
                    useDate = null;
                    useTime = detailIntro[1];
                } else if (detailIntro.length == 2) {
                    infoCenter = detailIntro[0];
                    useDate = detailIntro[1];
                    useTime = null;
                } else {
                    infoCenter = detailIntro[0];
                    useDate = detailIntro[1];
                    useTime = detailIntro[2];
                }

//                // 스트링 비교할땐ㄴ equals쓰자^^^6,,,, 눈물 좔좔
//                Log.i("useTime", useTime);
                if (useTime != null) {
                    if (useTime.equals("10")) {
                        useTime = null;
                    }
                }
                if (infoCenter != null) {
                    if (infoCenter.equals("10")) {
                        infoCenter = null;
                    }
                }

                if (infoCenter != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("연락처", infoCenter));
                }
                if (useDate != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("휴일", useDate));
                }
                if (useTime != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("개관시간", useTime));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 반복 정보 값: 기타 정보
        try {
            String RESULT = task3.execute().get();
            Log.i("반복정보 전달 받은 값", RESULT);

            if (RESULT.length() != 0) {
                detailInfo = RESULT.split("\n");
                String infoName[] = new String[detailInfo.length];
                String infoText[] = new String[detailInfo.length];

                for (int i = 0 ; i < detailInfo.length ; i++) {
                    real_detailInfo = detailInfo[i].split("  ");

                    if (real_detailInfo.length == 1) {
                        // 줄별로 분리하기
                        infoName[i] = real_detailInfo[0];
                        infoText[i] = null;
                    } else if (real_detailInfo.length == 2) {
                        // 줄별로 분리하기
                        infoName[i] = real_detailInfo[0];
                        infoText[i] = real_detailInfo[1];
                    }
                }
                String DetailResult = "";

                for (int i = 0 ; i < detailInfo.length ; i++) {
                    // 마지막 항목일 때
                    if (infoName[i] == infoName[infoName.length-1]) {
                        DetailResult += infoName[i] + "\n" +  infoText[i];
                    } else {
                        DetailResult += infoName[i] + "\n" +  infoText[i] + "\n\n";
                    }

                }

                if (real_detailInfo != null) {
                    items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("기타 정보", DetailResult));
                }

//                if (real_detailInfo != null) {
//                    for (int i = 0 ; i < detailInfo.length  ; i++) {
//                        items.add(new Page2_1_X_RecyclerViewAdapter.Detail_item("기타 정보", infoName[i] + "\n" + infoText + "\n\n"));
//                    }
//                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 이미지 정보 값
        try {
            String RESULT = task4.execute().get();
            Log.i("이미지 받은 값", RESULT);

            if (RESULT.length() != 0) {
                detailImage = RESULT.split("\n");
                for (int i = 0 ; i < detailImage.length ; i++) {
                    detailImage[i] = Url_front + detailImage[i];

                    Log.i("잘 받아오니", detailImage[i]);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //URL에 들어갈 contentTypeId, cat1, cat2를 지정하는 작업
    private void url_code() {
        switch (type) {
            case "자연":
                contentTypeId = "12";
                cat1 = "A01";
                cat2 = "";
                break;
            case "역사":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0201";
                break;
            case "휴양":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0202";
                break;
            case "체험":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0203";
                break;
            case "산업":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0204";
                break;
            case "건축/조형":
                contentTypeId = "12";
                cat1 = "A02";
                cat2 = "A0205";
                break;
            case "문화":
                contentTypeId = "14";
                cat1 = "A02";
                cat2 = "A0206";
                break;
            case "레포츠":
                contentTypeId = "28";
                cat1 = "A03";
                cat2 = "";
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(double x, double y, String name) {

    }

    @Override
    public void make_db(String countId, String name, String cityname, String type, String image, String click) {
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(countId, name, cityname, type, image, click);
        mDbOpenHelper.close();
    }

    @Override
    public void delete_db(String contentId) {
        mDbOpenHelper.open();
        mDbOpenHelper.deleteColumnByContentID(contentId);
        mDbOpenHelper.close();

        // 다이얼로그 띄우기
        delete_dialog();
    }

    @Override
    public String isClick(String countid) {
        mDbOpenHelper.open();
        Cursor iCursor = mDbOpenHelper.selectIdCulumns(countid);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());

        while (iCursor.moveToNext()) {
            String userId = iCursor.getString(iCursor.getColumnIndex("userid"));

            id = userId;
        }
        mDbOpenHelper.close();

        return id;
    }

    public void delete_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("관심관광지 삭제 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(getApplicationContext(), Heart_page.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(getApplicationContext(), Page1_1_1.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text) {

    }

    // api 연결
    class SearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("시작", "시작");

            url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&MobileApp=AppTest&MobileOS=ETC" +
                    "&contentId=" + contentID +
                    "&defaultYN=Y&addrinfoYN=Y&mapinfoYN=Y";

            URL xmlUrl;
            returnResult = "";

            try {
                boolean item = false;
                boolean title = false;
                boolean tel = false;
                boolean addr1 = false;
                boolean mapx =false;
                boolean mapy = false;

                xmlUrl = new URL(url);
                Log.d("url", url);
                xmlUrl.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("title")) {
                                title = true;
                            }
                            if (parser.getName().equals("tel")) {
                                tel = true;
                            }
                            if (parser.getName().equals("addr1")) {
                                addr1 = true;
                            }
                            if (parser.getName().equals("mapx")) {
                                mapx = true;
                            }
                            if (parser.getName().equals("mapy")) {
                                mapy = true;
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (title) {
                                Spanned tempTitle = Html.fromHtml(parser.getText());
                                returnResult += tempTitle.toString() + "  ";
                                title = false;
                            }
                            if (tel) {
                                Spanned tempTel = Html.fromHtml(parser.getText());
                                returnResult += tempTel.toString() + "  ";
                                tel = false;
                            }
                            if (addr1) {
                                Spanned tempAddr1 = Html.fromHtml(parser.getText());
                                returnResult += tempAddr1.toString() + "  ";
                                addr1 = false;
                            }
                            if (mapx) {
                                Spanned tempMapx = Html.fromHtml(parser.getText());
                                returnResult += tempMapx.toString() + "  ";
                                mapx = false;
                            }
                            if (mapy) {
                                Spanned tempMapy = Html.fromHtml(parser.getText());
                                returnResult += tempMapy.toString() + "  ";
                                mapy = false;
                            }
                            break;
                        }

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnResult;
        }
    }

    class SearchTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("소개정보 시작", "시작2");

            url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&contentId=" + contentID +
                    "&contentTypeId=" + contentTypeId +
                    "&MobileApp=AppTest&MobileOS=ETC";

            URL xmlURL;
            returnResult = "";

            try {
                boolean item = false;
                // 타입이 12일때
                boolean restdate = false;
                boolean usetime = false;
                boolean infocenter = false;

                // 타입이 14일때
                boolean restdateculture = false;
                boolean usertimeculture = false;
                boolean infocenterculture = false;

                // 타입이 28일때
                boolean restdateleports = false;
                boolean usertimeleports = false;
                boolean infocenterleports = false;

                xmlURL = new URL(url);
                Log.d("url", url);
                xmlURL.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlURL.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }

                            // 타입 12
                            if (parser.getName().equals("restdate")) {
                                Log.i("restDate", "태그시작");
                                restdate = true;
                            }
                            if (parser.getName().equals("usetime")) {
                                Log.i("useTime", "태그시작");
                                usetime = true;
                            }
                            if (parser.getName().equals("infocenter")) {
                                Log.i("태그", "태그시작");
                                infocenter = true;
                            }

                            // 타입14
                            if (parser.getName().equals("restdateculture")) {
                                restdateculture = true;
                            }
                            if (parser.getName().equals("usetimeculture")) {
                                usertimeculture = true;
                            }
                            if (parser.getName().equals("infocenterculture")) {
                                infocenterculture = true;
                            }

                            // 타입28
                            if (parser.getName().equals("restdateleports")) {
                                restdateleports = true;
                            }
                            if (parser.getName().equals("usetimeleports")) {
                                usertimeleports = true;
                            }
                            if (parser.getName().equals("infocenterleports")) {
                                infocenterleports = true;
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            // 12
                            if (restdate) {
                                Log.i("restDate", "쉬는날" + restdate);
                                Spanned tempRestDate = Html.fromHtml(parser.getText());
                                returnResult += tempRestDate.toString() + "  ";
                                restdate = false;
                            }
                            if (usetime) {
                                Log.i("useTime", "개관시간" + parser.getText());
                                Spanned useTime = Html.fromHtml(parser.getText());
                                returnResult += useTime.toString() + "  ";
                                usetime = false;
                            }
                            if (infocenter) {
                                Spanned tempInfoCenter = Html.fromHtml(parser.getText());
                                returnResult += tempInfoCenter.toString() + "  ";
                                infocenter = false;
                            }

                            // 14
                            if (restdateculture) {
                                Spanned tempRestDateCulture = Html.fromHtml(parser.getText());
                                returnResult += tempRestDateCulture.toString() + "  ";
                                restdateculture = false;
                            }
                            if (usertimeculture) {
                                Spanned tempUserTimeCulture = Html.fromHtml(parser.getText());
                                returnResult += tempUserTimeCulture.toString() + "  ";
                                usertimeculture = false;
                            }
                            if (infocenterculture) {
                                Spanned tempInfoCenterCulture = Html.fromHtml(parser.getText());
                                returnResult += tempInfoCenterCulture.toString() + "  ";
                                infocenterculture = false;
                            }

                            // 28
                            if (restdateleports) {
                                Spanned tempRestDateLeports = Html.fromHtml(parser.getText());
                                returnResult += tempRestDateLeports.toString() + "  ";
                                restdateleports = false;
                            }
                            if (usertimeleports) {
                                Spanned tempUserTimeLeports = Html.fromHtml(parser.getText());
                                returnResult += tempUserTimeLeports.toString() + "  ";
                                usertimeleports = false;
                            }
                            if (infocenterleports) {
                                Spanned tempInfoCenterLeports = Html.fromHtml(parser.getText());
                                returnResult += tempInfoCenterLeports.toString() + "  ";
                                infocenterleports = false;
                            }
                            break;
                        }

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return returnResult;
        }
    }

    // 반복정보 조회 api : 입장료 받아오기
    class SearchTask3 extends AsyncTask<String, Void, String > {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("반복정보 시작", "시작3");

            url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?serviceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&numOfRows=10&pageNo=1" +
                    "&MobileOS=ETC&MobileApp=AppTest" +
                    "&contentId=" + contentID +
                    "&contentTypeId=" + contentTypeId;

//            url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?serviceKey=" +
//                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
//                    "contentId=" + contentID +
//                    "&contentTypeId=" + contentTypeId +
//                    "&MobileOS=ETC&MobileApp=AppTest";

            URL xmlURL;
            returnResult = "";

            try {
                boolean item = false;
                boolean infoname = false;
                boolean infotext = false;

                xmlURL = new URL(url);
                Log.d("url", url);
                xmlURL.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlURL.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("infoname")) {
                                infoname = true;
                            }
                            if (parser.getName().equals("infotext")) {
                                infotext = true;
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (infoname) {
                                Log.i("infoname", "태그시작" + parser.getText());
                                Spanned infoName = Html.fromHtml(parser.getText());
                                returnResult += infoName.toString() + "  ";
                                infoname = false;
                            }
                            if (infotext) {
                                Log.i("restDate", "태그시작" + parser.getText());
                                Spanned infoText = Html.fromHtml(parser.getText());
                                returnResult += infoText.toString() + "\n";
                                infotext = false;
                            }
                            break;
                        }

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return returnResult;
        }
    }

    class SearchTask4 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("이미지 정보 받아오기", "시작4");

            url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage?serviceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&MobileOS=ETC&MobileApp=AppTest" +
                    "&contentId=" + contentID +
                    "&imageYN=Y";

            URL xmlUrl;
            returnResult = "";

            try {
                boolean item = false;
                boolean originimgurl = false;

                xmlUrl = new URL(url);
                Log.d("url", url);
                xmlUrl.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("originimgurl")) {
                                originimgurl = true;
                            }
                            break;
                        }
                        case XmlPullParser.TEXT: {
                            if (originimgurl) {
                                returnResult += parser.getText() + "\n";
                                originimgurl = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return returnResult;
        }
    }
}
