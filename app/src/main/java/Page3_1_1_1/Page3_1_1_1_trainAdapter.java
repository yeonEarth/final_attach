package Page3_1_1_1;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class Page3_1_1_1_trainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TrainItemTouchHelper.ItemTouchHelperAdapter{
    private ItemTouchHelper touchHelper;
    private ArrayList<Page3_1_1_1_Main.RecycleItem> items = null;

    //헤더바 위치가 0이면 true로 바뀌고 1일차로 움직일 수 없게 만듦
    boolean firstdone = false;

    //헤더인지 아이템인지 확인하는데 필요함/ HEADER:n일차 바 / CHILD:기차시간표 / CITY:관광지 부분
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    public static final int CITY =2;

    //바텀시트 관련(맨 아래 함수)
    int isNetworkConnect;
    boolean isApiError = false;
    FragmentManager fragmentManager;
    String receiveMsg = "";
    String [] data_split;
    ArrayList<Page3_1_1_1_bottomSheet_Adapter.Api_Item> completeList;              //정제된 리스트값
    String[] arr_line;
    String[] arr_all;
    String[] _name = new String[238];                                              //txt에서 받은 역이름
    String[] _code = new String[238];                                              //txt에서 받은 역코드
    String startCode, endCode, trainCode;
    String[] trainCodelist = {"01", "02", "03", "04", "08", "09", "15"};
    String date;


    //부모 액티비티와 연결
    Page3_1_1_1_trainAdapter(ArrayList<Page3_1_1_1_Main.RecycleItem> list, FragmentManager supportFragmentManager, int isNetworkConnect) {
        items = list;
        this.fragmentManager = supportFragmentManager;
        this.isNetworkConnect = isNetworkConnect;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.page3_1_1_1_header, parent, false);
                Page3_1_1_1_trainAdapter.HeaderViewHolder headerViewHolder = new Page3_1_1_1_trainAdapter.HeaderViewHolder(view);
                return headerViewHolder;

            case CHILD:
                LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view2 = inflater2.inflate(R.layout.page3_1_1_1_item,parent,false);
                Page3_1_1_1_trainAdapter.ItemViewHolder itemViewHolder = new Page3_1_1_1_trainAdapter.ItemViewHolder(view2);
                return itemViewHolder;

            case CITY:
                LayoutInflater inflater3 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view3 = inflater3.inflate(R.layout.page3_1_1_1_cityitem, parent, false);
                Page3_1_1_1_trainAdapter.CityViewHolder cityViewHolder = new Page3_1_1_1_trainAdapter.CityViewHolder(view3);
                return cityViewHolder;
        }
        return null;
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Page3_1_1_1_Main.RecycleItem item = items.get(position);
        int itemViewType = getItemViewType(position);


        if (itemViewType == HEADER) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header_title.setText(item.text);

            //1일차는 움직이는 버튼 없앰
            if(position==0 && !firstdone){
                ((HeaderViewHolder) holder).move_img.setVisibility(View.INVISIBLE);
                firstdone = true;
            }
            //움직이는 버튼 누르면 움직임
            headerViewHolder.move_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                    }
                    return false;
                }
            });
        }


        else if (itemViewType == CHILD) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            //이용권바 움직이면 날짜 변동
            if(position > 0){
                for(int i =position; i > 0; i--){
                    if(items.get(i-1).text.contains("일차") ){
                        item.setDate(items.get(i-1).date);
                        break;
                    }
                }
            }

            //도시1-도시2 텍스트 연결 (환승)에 따라 색상,크기 변화 줌
            String text_split[] = item.text.split(",");
            if(text_split[0].contains("환승")){
                itemViewHolder.mCourseText1.setTextSize(11);
                itemViewHolder.mCourseText1.setTextColor(Color.parseColor("#C0C0C0"));
            }else{
                itemViewHolder.mCourseText1.setTextSize(16);
                itemViewHolder.mCourseText1.setTextColor(Color.parseColor("#000000"));
            }
            if(text_split[1].contains("환승")){
                itemViewHolder.mCourseText2.setTextSize(11);
                itemViewHolder.mCourseText2.setTextColor(Color.parseColor("#C0C0C0"));
            }else{
                itemViewHolder.mCourseText2.setTextSize(16);
                itemViewHolder.mCourseText2.setTextColor(Color.parseColor("#000000"));
            }
            itemViewHolder.mCourseText1.setText(text_split[0]);
            itemViewHolder.mCourseText2.setText(text_split[1]);
            itemViewHolder.mShadowText.setText(item.text_shadow);
            itemViewHolder.mTimeText.setText(item.train_time);
            if(item.train_time.length() > 0){
                itemViewHolder.mTimeText.setTextColor(Color.parseColor("#000000"));
                itemViewHolder.mTimeText.setTextSize(16f);
                itemViewHolder.search_img.setVisibility(View.INVISIBLE);
            } else
                itemViewHolder.search_img.setVisibility(View.VISIBLE);
        }


        //itemViewType == CITY
        else {
            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            cityViewHolder.city_text.setText(item.text);

            //삭제누르면
            cityViewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemMove(int fromPos, int toPos) {
        //0번째=1일차로는 움직일 수 없음
        if(toPos != 0 ){
            Page3_1_1_1_Main.RecycleItem target = items.get(fromPos);
            items.remove(fromPos);
            items.add(toPos, target);
            notifyItemMoved(fromPos, toPos);
            notifyItemChanged(fromPos);
            notifyItemChanged(toPos);
        }

    }

    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }




    //헤더 xml 연결
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView header_title;
        private TextView move_btn;
        private LinearLayout list_header;
        private ImageView move_img;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            move_btn = (TextView) itemView.findViewById(R.id.move_btn);
            list_header = (LinearLayout) itemView.findViewById(R.id.list_header);
            move_img = (ImageView)itemView.findViewById(R.id.move_btn_img);
        }
    }


    //촤일드 xml 연결
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTimeText, mCourseText1, mCourseText2, mShadowText; // 시간, 경로
        private ImageView search_img;
        LinearLayout item_touch;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCourseText1 = (TextView) itemView.findViewById(R.id.course1_Page3_1_1);
            mCourseText2 = (TextView) itemView.findViewById(R.id.course2_Page3_1_1);
            mShadowText = (TextView) itemView.findViewById(R.id.searchTime_Page3_1_1_shadow);
            mTimeText = (TextView) itemView.findViewById(R.id.searchTime_Page3_1_1);
            search_img = (ImageView) itemView.findViewById(R.id.search_img);
            item_touch = (LinearLayout) itemView.findViewById(R.id.item_touch);


            //기차 시간표 부분------------------------------------------------여기 아래 수정함
            //page3_1_1_1_bottomSheet_Adapter 자바 파일도 수정함
            //page3_1_1_1_apisheet.xml 에 textview 추가 하시길
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //현재 위치를 받아오기 위함
                    final Context context = v.getContext();
                    final int pos = getAdapterPosition() ;
                    final Page3_1_1_1_Main.RecycleItem item = items.get(pos);

                    //바텀시트에 전달할 값
                    ArrayList<String> data = new ArrayList<>();
                    data.clear();
                    data.add(items.get(pos).text_shadow);
                    String date = items.get(pos).date;

                    //바텀시트 생성
                    final BottomSheetDialog dialog = new BottomSheetDialog(context);
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.page3_1_1_1_apisheet, null);
                    dialog.setContentView(view);

                    //바텀시트 구성
                    ListView listView = view.findViewById(R.id.api_list);
                    TextView error_msg = view.findViewById(R.id.page3_1_1_1_error);
                    completeList= new ArrayList<Page3_1_1_1_bottomSheet_Adapter.Api_Item>();

                    //리스트 초기화
                    completeList.clear();
                    settingList(context);

                    //인터넷 연결이 되어있을 때만 api 돌린 ( 아니면 오류남)-
                    if( isNetworkConnect != 3){
                        send(data, date);
                    }

                    //기차시간표 오류 메시지
                    //(1)인터넷 연결 안되어있을 때 / (2)API 연결 오류 / (3)연결되는 노선이 없음
                    if(completeList.size() < 1){
                        if(isNetworkConnect == 3){
                            error_msg.setText(R.string.train_err_internet);
                        }
                        else if(isApiError ){
                            error_msg.setText(R.string.train_err_api);
                        }
                        else {
                            error_msg.setText(R.string.train_err_course);
                        }
                        error_msg.setVisibility(View.VISIBLE);
                    } else
                        error_msg.setVisibility(View.INVISIBLE);


                    //api 트래픽 다 써서 임의값 넣어놓음
//                    completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, "00:04"+"00:35","00:24"+"02:07", " ", "무궁화"+"ITX-새마을"));
//                    completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, "09:48"+"10:46","10:08"+"12:07", " ", "무궁화"+"ITX-새마을"));
//                    completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, "10:44"+"11:19","11:07"+"12:33", " ", "무궁화"+"ITX-새마을"));
//                    completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, "16:41"+"17:22","17:01"+"18:57", " ", "새마을"+"무궁화"));
//                    completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, "17:46"+"18:27","18:08"+"20:01", " ", "무궁화"+"무궁화"));

                    //바텀시트 어댑터 연결
                    Page3_1_1_1_bottomSheet_Adapter adapter = new Page3_1_1_1_bottomSheet_Adapter(completeList, context);
                    listView.setAdapter(adapter);

                    //스크롤을 원활하게 하기 위함
                    listView.setOnTouchListener(new ListView.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            v.onTouchEvent(event);
                            return true;
                        }
                    });

                    //리스트에서 아이템(시간)을 선택하면
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String[] startTIme = completeList.get(position).getDepTime().split("\n");
                            String[] endTime = completeList.get(position).getArrTime().split("\n");
                            mTimeText.setText(startTIme[0]+" - "+endTime[endTime.length-1]);
                            mTimeText.setTextColor(Color.parseColor("#000000"));
                            mTimeText.setTextSize(16f);
                            items.set(pos, new Page3_1_1_1_Main.RecycleItem(Page3_1_1_1_trainAdapter.CHILD,  item.text_shadow, item.text, item.date,startTIme[0]+" - "+endTime[endTime.length-1] , "",""));
                            search_img.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }
    }



    //시티 xml 연결
    public class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView city_text;
        private Button delete_btn;

        public CityViewHolder(View itemView) {
            super(itemView);
            city_text =  itemView.findViewById(R.id.page3_1_1_1_cityText);
            delete_btn = itemView.findViewById(R.id.page3_1_1_1_tourDelete);
        }
    }




    /*
     *바텀시트 관련 함수
     *****************************************************************************************
     */

    //txt 돌려 역 비교할 배열 만들기(이름 지역코드 동네코드)<-로 구성
    private void settingList(Context context){
        String readStr = "";
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;
        try{
            inputStream = assetManager.open("stationWithcode.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        arr_all = readStr.split("\n"); //txt 내용을 줄바꿈 기준으로 나눈다.

        //한 줄의 값을 띄어쓰기 기준으로 나눠서, 배열에 넣는다.
        for(int i=0; i <arr_all.length; i++) {
            arr_line = arr_all[i].split(",");

            _code[i] = arr_line[0];     //역코드
            _name[i] = arr_line[1];     //이름
        }
    }


    //앞 액티비티에서 선택된 역과 같은 역을 찾는다.
    private void compareStation(String start, String end){
        for(int i=0; i<_name.length; i++){
            if(start.equals(_name[i])){
                startCode = _code[i];
            }
            if(end.equals(_name[i])){
                endCode = _code[i];
            }
        }
    }


    //api 연결
    public  class  Task extends AsyncTask<String, Void, String> {
        private String str;

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try{
                url = new URL("http://openapi.tago.go.kr/openapi/service/TrainInfoService/" +
                        "getStrtpntAlocFndTrainInfo?serviceKey=7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                        "&numOfRows=30" +
                        "&pageNo=1&" +
                        "depPlaceId=" + startCode +
                        "&arrPlaceId=" + endCode +
                        "&depPlandTime=" + date +
                        "&trainGradeCode=" + trainCode +
                        "&_type=json");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }


    //api 값을 정제
    public String[] trianjsonParser(String jsonString){
        String arrplacename = null;
        String arrplandtime = null;
        String depplacename = null;
        String depplandtime = null;
        String traingradename = null;

        String[] arraysum = new String[100];
        String result = "";
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
            JSONObject jsonObject3 = jsonObject1.getJSONObject("body");
            JSONObject jsonObject4 = jsonObject3.getJSONObject("items");
            JSONArray jsonArray = jsonObject4.getJSONArray("item");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                arrplacename = jObject.getString("arrplacename");
                arrplandtime = jObject.getString("arrplandtime");
                depplacename = jObject.getString("depplacename");
                depplandtime = jObject.getString("depplandtime");
                traingradename = jObject.getString("traingradename");

                String depTime = depplandtime.substring(8, 12);
                String arrTime = arrplandtime.substring(8, 12);

                completeList.add(new Page3_1_1_1_bottomSheet_Adapter.Api_Item(Page3_1_1_1_bottomSheet_Adapter.HEADER, depTime.substring(0,2)+":"+depTime.substring(2), arrTime.substring(0,2)+":"+arrTime.substring(2), "["+depplacename+"]", traingradename));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  arraysum;

    }


    //리스트에 넣을 값을 구성(환승작업)
    public void send(ArrayList<String> data, String date) {
        data_split = data.get(0).split(",");
        this.date = date;

        compareStation(data_split[0], data_split[1]);

        //열차 종류별 api 검색(1)
        for (int i = 0; i < trainCodelist.length; i++) {
            trainCode = trainCodelist[i];
            try {
                new Task().execute().get();
                trianjsonParser(receiveMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //출발 시간 정렬
        Collections.sort(completeList);

        //api 오류가 나면 true
        if(receiveMsg.contains("LIMITED") || receiveMsg.contains("SERVICE"))  {
            isApiError = true;
        }
    }




}
