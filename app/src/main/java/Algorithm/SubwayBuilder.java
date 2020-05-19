package Algorithm;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SubwayBuilder {


    //singleton 패턴으로 데이터 무결성을 만든다.
    private static SubwayBuilder singleton = null;

    public static SubwayBuilder getInstance(){
        if ( SubwayBuilder.singleton == null) {
            SubwayBuilder.singleton = new SubwayBuilder();
        }
        return SubwayBuilder.singleton;
    }

    //builder 안에 있는 subway 클래스
    private Subway subway;

    public SubwayBuilder(){
        this.subway = new Subway();
    }

    //build하면 클래스를 복제한다.
    public Subway build(){
        return this.subway.clone();
    }

    //역 링크정보 읽기
    public SubwayBuilder readFile(Context context,String subwayFile, String linkFile) throws  SubwayException, IOException {

        //프로퍼티 역정보 취득
        readSubway(context, subwayFile);

        //프로퍼티 링크정보(노선) 취득
        readLink(context, linkFile);
        return this;
    }


    private void readSubway(Context context, String file) throws SubwayException {
        //파일을 전부 읽어들인다.
        String readStr = "";
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;
         try{
             inputStream = assetManager.open(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             String str = null;
             while (((str = reader.readLine()) != null)){ readStr += str + "\n";}
             reader.close();
         } catch (IOException e) {
             e.printStackTrace();
         }

        //구분자 \n로 역정보를 전부 읽어들인다.(라인 하나에 역하나)
        String[] arr_all = readStr.split("\n");

        //데이터가 하나도 없으면 에러
        if(arr_all.length <= 0 ){
            new IOException("아무것도 없다");
        }

        //역을 클래스화
        //for(변수 : 배열) 이 코드의 의미는 arr_all 배열에 들어있는 값들을 하나씩 line 에 넣는다.
        for(String line : arr_all){

            //구분자는 콤마( , )
            String[] buf = line.replace("\r", "").split(",");

            //데이터 이상의 통과
            if(buf.length != 4){
                continue;
            }

            //파일 형식은 코드, 역이름, 열차종류
            this.subway.addStation(buf[0], buf[1], buf[2], buf[3]);
        }
    }


    //파일로 부터 역 링크를 읽어들인다.
    private void readLink(Context context, String file) throws IOException {
        //파일을 전부 읽어들인다.
        String readStr = "";
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;
        try{
            inputStream = assetManager.open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while (((str = reader.readLine()) != null)){ readStr += str + "\n";}
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //구분자 \n로 역정보를 전부 읽어들인다.(라인 하나에 역하나)
        String[] arr_all = readStr.split("\n");

        //데이터가 하나도 없으면 에러
        if(arr_all.length <= 0 ){
            new IOException("아무것도 없다");
        }

        // 링크 만들기
        for (String line : arr_all) {
            // 구분자는 콤마[,]
            String[] buf = line.replace("\r", "").split(",");
            // 데이터 이상은 통과
            if (buf.length != 3) {
                continue;
            }
            // buf[1]의 다음역은 buf[2], 다다음역은 buf[3]
            if ("n".equals(buf[0])) {
                setNextStation(buf[1], buf[2]);
            }
            // buf[1]의 전역은 buf[2]
            else if ("p".equals(buf[0])) {
                setPrevStation(buf[1], buf[2]);
            }
        }
    }


    /**
     * 다음역 세팅
     * @param point 기준 역 정보
     * @param next  다음 역 정보
     */
    public SubwayBuilder setNextStation(Station point, Station next) {
        point.addNext(next);
        next.addPrev(point);
        return this;
    }


    /**
     * 다음역 세팅(다형성)
     * @param pointCode 역 코드
     * @param nextCode  역 코드
     */
    public SubwayBuilder setNextStation(String pointCode, String nextCode) {
        Station point = this.subway.getStation(pointCode);
        Station next = this.subway.getStation(nextCode);
        setNextStation(point, next);
        return this;
    }


    /**
     * 전역 세팅
     *
     * @param point 기준 역 정보
     * @param prev  전역정보
     */
    public SubwayBuilder setPrevStation(Station point, Station prev) {
        point.addPrev(prev);
        prev.addNext(point);
        return this;
    }

    /**
     * 전역 세팅(다형성)
     *
     * @param pointCode 전역 코드
     * @param prevCode  전역 코드
     */
    public SubwayBuilder setPrevStation(String pointCode, String prevCode) {
        Station point = this.subway.getStation(pointCode);
        Station prev = this.subway.getStation(prevCode);
        setPrevStation(point, prev);
        return this;
    }

}
