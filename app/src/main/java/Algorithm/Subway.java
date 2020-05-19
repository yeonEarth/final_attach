package Algorithm;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Subway extends ArrayList<Station> implements Cloneable {
    private static final long serialVersionUID = 1L;
    int lenth = 0;

    //생성자 리스트
    public Subway() {
    }

    /**
     * 역추가(다형성)
     *
     * @param code 역 코드
     * @param name 역 이름
     * @param id 열차노선
     */


        public void addStation(String code, String name, String id, String time) throws SubwayException {
        Station s = new Station();
        s.setCode(code);
        s.setName(name);
        s.setId(id);
        s.setTime(time);
        addStation(s);
        lenth++;
    }

    @Override
    public final boolean add(Station station) {
        throw new RuntimeException("More not supported");
    }

    /**
     * 역추가(다형성)
     * @param station 역 클래스
     */

    private void addStation(Station station) throws SubwayException {
        //역코드가 중복일시, 에러를 내보낸다.
        if (getStation(station.getCode()) != null) {
            throw new SubwayException("same code");
        }
        super.add(station);
    }

    //코드로 역을 찾는다
    public Station getStation(String code) {
        for(Station s : this){
            if( s.getCode().equals(code)){
                return s;
            }
        }
        return null;
    }


    public String toString(){
        String ret = "";
        for ( Station s : this) {
            ret += s.toString() + "\r\n";
        }
        return ret;
    }

    //clone을 재정의한다.

    @NonNull
    @Override
    public Subway clone() {
        try {
            return (Subway) super.clone();
        } catch (Throwable e){
            return null;
        }
    }
}
