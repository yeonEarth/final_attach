package Page1_schedule;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.hansol.spot_200510_hs.R;

import java.util.HashMap;
import java.util.List;

public class Page1_pagerAdapter extends PagerAdapter {

    private Context mContext;
    private String stationName;
    private List<String> localArray;
    private HashMap<String, String> benefit = new HashMap<String, String>();

   //인터페이스
    private send_expand send;
    boolean isExpand = false;


    public Page1_pagerAdapter(send_expand send, Context context , List<String> arrayList){
        this.send = send;
        mContext = context;
        localArray = arrayList;
    }


    @Override
    public int getCount() {
        return localArray.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }


    @Override
    public View instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = null ;
        benefit_list();

        if (mContext != null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.page1_viewpager, container, false);
            stationName = localArray.get(position).trim();

            final TextView textView =  view.findViewById(R.id.page1_stnName) ;
            textView.setText(stationName+"역") ;
            final TextView pre_station = view.findViewById(R.id.page1_previousStation);
            final TextView next_station = view.findViewById(R.id.page1_nextStation);
            final TextView now_station = view.findViewById(R.id.page1_nowStation);
            final TextView benefit_text = view.findViewById(R.id.benefit_content);
            final TextView benefit_check = view.findViewById(R.id.page1_giftTxt);
            final Button checkIn_btn =  view.findViewById(R.id.checkIn_btn);
            final LinearLayout page1_pager_layout =  view.findViewById(R.id.page1_pager_layout);
            final RelativeLayout page1_gift_layout =  view.findViewById(R.id.page1_gift_layout);

            //첫번째 페이지가 아니면
            if(position > 0) {
                checkIn_btn.setText("해당역에 도착");
                pre_station.setVisibility(View.INVISIBLE);
                next_station.setVisibility(View.INVISIBLE);
                now_station.setVisibility(View.INVISIBLE);
            }



           //여행 시작하기 or 도착확인 버튼
            checkIn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){

                    //여행 시작하기
                    if(position == 0){
                        checkIn_btn.setText("여행 시작");
                        checkIn_btn.setTextColor(Color.parseColor("#FFFEFE"));
                        checkIn_btn.setSelected(true);
                        page1_pager_layout.setBackgroundResource(R.drawable.rectangle4);
                        page1_gift_layout.setBackgroundResource(R.drawable.rectangle4);
                        textView.setTextColor(Color.parseColor("#2D624F"));
                    }

                    //도착 확인 버튼
                    else {
                        pre_station.setText("< 이전역");
                        pre_station.setVisibility(View.VISIBLE);
                        next_station.setVisibility(View.VISIBLE);
                        now_station.setVisibility(View.VISIBLE);

                        //마지막 페이지가 아니라면
                        if(position != localArray.size()-1){
                            next_station.setVisibility(View.VISIBLE);
                        }

                        checkIn_btn.setText("도착 확인");
                        checkIn_btn.setTextColor(Color.parseColor("#FFFEFE"));
                        checkIn_btn.setSelected(true);
                        page1_pager_layout.setBackgroundResource(R.drawable.rectangle4);
                        page1_gift_layout.setBackgroundResource(R.drawable.rectangle4);
                        textView.setTextColor(Color.parseColor("#2D624F"));
                    }
                }
            });


            //혜택보기 레이아웃 펼치기
            page1_gift_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){
                    float d = mContext.getResources().getDisplayMetrics().density;

                    if(!isExpand){
                        if(benefit.get(stationName)  != null){
                            benefit_text.setText(benefit.get(stationName));
                        } else{
                            benefit_text.setText("해당역은 사은품이 없습니다.");
                        }

                        benefit_check.setText("현재 역에서 제공하는 내일로 사은품입니다.");
                        benefit_check.setTextColor(Color.parseColor("#4D000000"));
                        benefit_text.setVisibility(View.VISIBLE);
                        page1_gift_layout.getLayoutParams().height = (int)(200*d);
                        page1_gift_layout.requestLayout();
                        send.send(isExpand);
                        isExpand = true;

                    }
                    else {
                        benefit_check.setText("정차역 혜택 확인하기");
                        benefit_check.setTextColor(Color.parseColor("#000000"));
                        benefit_text.setVisibility(View.INVISIBLE);
                        page1_gift_layout.getLayoutParams().height =(int)(60*d);
                        page1_gift_layout.requestLayout();
                        send.send(isExpand);
                        isExpand = false;
                    }
                }
            });
        }

        // 뷰페이저에 추가.
        container.addView(view) ;
        return view ;
    }


    //뷰페이져 확장을 위한 인터페이스
    public interface send_expand{
        void send(boolean isExpand);
    }


    //혜택 리스트
    private void benefit_list(){
        benefit.put("서울", "스팀 아이마스크 제공   벡셀 에너지음료 제공    물티슈 제공    포카리스웨트 음료 제공   뽑기 이벤트 (*서울역 현장발권 이벤트)" );
        benefit.put("수원" ,"해시태그 이벤트   스탬프북   물티슈   간이 방석   3색 볼펜");
        benefit.put("영등포","백령도 왕복 여객승선권 주중 할인 50% 쿠폰    어기어때 숙박 할인쿠폰    치약칫솔 세트 (*영등포역 발권 고객 한정)");
        benefit.put("용산" , "이온워터 증정이벤트   콜롬비아나 마스터   스탬프북   돌림판 추첨 (*용산역 발권자 해당)   내일로또 추천 (*용산용 발권자 해당)   연국 옥탑방고양이 할인티켓   박물관이 살아있다 초대권   미션레이스 초대권");
        benefit.put("청량리" ,"담요    방석    물티슈");
        benefit.put("평택" ,"보틀");
        benefit.put("강릉" ,"엽서");
        benefit.put("태백" ,"후드목베개   에코백   코레일 그립톡");
        benefit.put("평창" ,"평창역 홍보대사 엽서");
        benefit.put("대전" ,"국립생태원 워크북 & 3D퍼즐   대전시 지하철 볼거지 & 먹거리 책자   마우스  패드   매직 스티커   어기어때 숙박 할인쿠폰   칫솔 세트   여행용 세트 or 텀블러 or 핸드크림 3종세트");
        benefit.put("제천" ,"물티슈   파일 홀더");
        benefit.put("천안아산" ,"고흐 담요     국립생태원 3D퍼즐     전기기관차 종이 모형     토끼 무드등 & 펜     핫팩 & 손난로     어기어때 할인 쿠폰");
        benefit.put("광주송정" ,"스탬프북");
        benefit.put("목포" ,"환영꾸러미");
        benefit.put("동대구" ,"룰렛 이벤트 (*동대구역 현장발권에 한함)    여행용 키트 (*동대구역 현장발권에 한함)    물티슈    핫팩    땅땅치킨 1만원 상품권");
        benefit.put("부산" ,"물티슈   철도 노선도   벡셀 에너지 음료   장바구니   스탬프북   뽑기 이벤트");
        benefit.put("신경주" ,"불국사 입장권 (*신경주역,경주역,서경주역,불국사역 발권자 한정)");
        benefit.put("안동" ,"숙박 및 시티투어 할인권");
    }

}
