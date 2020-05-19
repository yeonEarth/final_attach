package Page2_1_1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hansol.spot_200510_hs.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import DB.DbOpenHelper;
import DB.Heart_page;
import Page1.Page1_1_1;
import Page2.Recycler_item;
import Page2_X.Page2_X_CategoryBottom;

public class Page2_1_1_Fragment extends Fragment implements OnItemClick{
    Page2_1_1 mainActivity;
    private String  station, subject, isMake;
    private String contentTypeId, cat1, cat2;
    private DbOpenHelper mDbOpenHelper;

    //역 이름을 받아서 지역코드랑 시군구코드 받기 위한 배열
    int station_code = 49;
    String[] arr_line = null;
    String[] _name = new String[station_code];           //txt에서 받은 역이름
    String[] _areaCode = new String[station_code];       //txt에서 받은 지역코드
    String[] _sigunguCode = new String[station_code];    //txt에서 받은 시군구코드
    String  areaCode, sigunguCode;
    String returnResult, url;

    String name_1[];  //returnResult를 줄바꿈 단위로 쪼개서 넣은 배열/ name_1[0]에는 한 관광지의 이름,url,contentId,위치가 다 들어가 있다.
    String name_2[] = new String[3];  //name_1를 "  " 단위로 쪼개서 넣은 배열/ [0]= contentID/ [1]=mapx/ [2]에= mapy/ [3]= img_Url/ [4]= name이 들어가 있다.

    //xml 파싱한 값을 분류해서 쪼개 넣음
    String name[] = new String[20];        //관광지 이름
    String img_Url[] = new String[20];     //이미지 URL
    String contentid[] = new String[20];   //관광지ID

    //리사이클러뷰에 연결할 데이터
    List<Recycler_item> items = new ArrayList<>();

    public Page2_1_1_Fragment() {}

    public static  Page2_1_1_Fragment newInstance(String subject, String station, String isMake) {
        Page2_1_1_Fragment fragment = new  Page2_1_1_Fragment();
        Bundle args = new Bundle();
        args.putString("subject", subject);   //관광지 주제
        args.putString("station", station);   //관광지 이름
        args.putString("isMake", isMake);     //api 연결 유무
        fragment.setArguments(args);
        return fragment;
    }

    //액티비티와 프래그먼트를 붙일 때 호출되는 메소드,
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (Page2_1_1) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            station = getArguments().getString("station");
            subject = getArguments().getString("subject");
            isMake = getArguments().getString("isMake");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_page2_1_1, container, false);

        mDbOpenHelper = new DbOpenHelper(mainActivity);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        Button btn = v.findViewById(R.id.page2_1_fragment_more_btn);
        btn.setText("'" + station + "'의 다른 관광지 더 보기");

        //리사이클러뷰 구현 부분
        RecyclerView recyclerView = v.findViewById(R.id.page2_1_fragment_recyclerview);
        recyclerView.setLayoutManager( new LinearLayoutManager(mainActivity));
        recyclerView.setHasFixedSize(true);

        // mkae = api 연결 // delete = api 연결 X
        if(isMake.equals("make")) {
            //txt 값 읽기
            settingList();
            //전달된 관광지 주제의 지역코드, 시군구코드 찾기
            compareStation();
            //url에 들어갈 contentTypeId, cat1, cat2 코드를 찾기
            url_code();

            //관광 api 연결
            SearchTask task = new SearchTask();
            try {
                String RESULT = task.execute().get();
                Log.i("전달 받은 값", RESULT);

                //사진링크, 타이틀(관광명), 분야뭔지 분리
                name_1 = RESULT.split("\n");

                for (int i = 0; i < name_1.length; i++) {
                    name_2 = name_1[i].split("  ");

                    //img_Url이 없는 경우도 있기 때문에, length = 3 = 있음/ 2 = 없음
                    if (name_2.length == 3) {
                        contentid[i] = name_2[0];
                        img_Url[i] = name_2[1];
                        name[i] = name_2[2];
                    } else {
                        contentid[i] = name_2[0];
                        img_Url[i] = null;
                        name[i] = name_2[1];
                    }
                }

                //리사이클러에 들어갈 데이터를 넣는다
                for (int i = 0; i < name_1.length; i++) {
                    items.add(new Recycler_item(img_Url[i], name[i], contentid[i] , subject));
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }

        //리사이클러뷰 연결
        recyclerView.setAdapter(new Page2_1_1_CardViewAdapter(items, mainActivity, station, this));
        return v;
    }





    //앞 액티비티에서 선택된 역과 같은 역을 찾는다.
    private void compareStation(){
        for(int i=0; i<_name.length; i++){
            if(station.equals(_name[i])){
                areaCode = _areaCode[i];
                sigunguCode = _sigunguCode[i];
            }
        }
    }

    //txt 돌려 역 비교할 배열 만들기(이름 지역코드 동네코드)<-로 구성
    private void settingList(){
        String readStr = "";
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;
        try{
            inputStream = assetManager.open("station_code_page2.txt");
            //버퍼리더에 대한 설명 참고 : https://coding-factory.tistory.com/251
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr_all = readStr.split("\n"); //txt 내용을 줄바꿈 기준으로 나눈다.

        //한 줄의 값을 띄어쓰기 기준으로 나눠서, 역명/지역코드/시군구코드 배열에 넣는다.
        for(int i=0; i <arr_all.length; i++) {
            arr_line = arr_all[i].split(",");

            _name[i] = arr_line[0];         //서울
            _areaCode[i] = arr_line[1];     //1
            _sigunguCode[i] = arr_line[2];  //0
        }
    }

    @Override
    public void onClick(double x, double y, String name) {

    }

    @Override
    public void make_db(String countId, String name, String cityname) {
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(countId, name, cityname);
        mDbOpenHelper.close();
    }

    @Override
    public void delete_db(String contentId) {
        mDbOpenHelper.open();
        mDbOpenHelper.deleteColumnByContentID(contentId);
        mDbOpenHelper.close();

        delete_dialog();
    }

    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mainActivity);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(mainActivity, Page1_1_1.class);
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

    public void delete_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mainActivity);
        builder.setTitle("관심관광지 삭제 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(mainActivity, Heart_page.class);
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

    //관광api 연결
    class SearchTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            //초기화 단계에서 사용
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            //Log.d("시작", "시작");

            //시군구코드가 0 일 때와 0이 아닐때를 구분해서 url을 넣어준다.
            if(sigunguCode.equals("0")){
                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "&pageNo=1&numOfRows=5&MobileApp=AppTest&MobileOS=ETC&arrange=B" +
                        "&contentTypeId=" + contentTypeId +
                        "&sigunguCode=" +
                        "&areaCode=" + areaCode +
                        "&cat1=" + cat1 + "&cat2=" + cat2 + "&cat3=" +
                        "&listYN=Y";
            } else {
                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "&pageNo=1&numOfRows=5&MobileApp=AppTest&MobileOS=ETC&arrange=B" +
                        "&contentTypeId=" + contentTypeId +
                        "&sigunguCode=" + sigunguCode +
                        "&areaCode=" + areaCode +
                        "&cat1=" + cat1 + "&cat2=" + cat2 + "&cat3=" +
                        "&listYN=Y";
            }

            URL xmlUrl;
            returnResult = "";

            try {
                boolean title = false;
                boolean firstimage = false;
                boolean item = false;
                boolean contentid = false;

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
                            if (parser.getName().equals("contentid")) {
                                contentid = true;
                                // Log.d("태그 시작", "태그 시작2");
                            }
                            if (parser.getName().equals("firstimage")) {
                                firstimage = true;
                                // Log.d("태그 시작", "태그 시작3");
                            }
                            if (parser.getName().equals("title")) {
                                title = true;
                                //Log.d("태그 시작", "태그 시작4");
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (contentid) {
                                returnResult += parser.getText() + "  ";
                                contentid = false;
                            }
                            if (firstimage) {
                                returnResult += parser.getText() + "  ";
                                firstimage = false;
                            }
                            if (title) {
                                returnResult += parser.getText() + "\n";
                                //Log.d("태그 받음", "태그받음4");
                                title = false;
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
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("err", "erro");
            }
            return returnResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    //URL에 들어갈 contentTypeId, cat1, cat2를 지정하는 작업
    private void url_code() {
        switch (subject) {
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
}
