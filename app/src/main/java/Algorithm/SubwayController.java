package Algorithm;

import java.util.ArrayList;
import java.util.Stack;

public class SubwayController {

    private Subway subway;

    public SubwayController(Subway subway) {
        this.subway = subway;
    }

    /**
     * 역 탐색
     *
     * @param start 출발 역코드
     * @param end   도착 역코드
     * @return 노선 출력
     */
    public String search(String start, String end){
        Station startStation = this.subway.getStation(start);
        Station endStation = this.subway.getStation(end);
        return search(startStation, endStation);
    }


    /**
     * 역 탐색 (다형성)
     *
     * @param start 출발 역정보
     * @param end   도착 역정보
     * @return 노선 출력
     */
    public String search(Station start, Station end){
        //모든 탐색 정보
        ArrayList<ArrayList<Station>> list = new ArrayList<ArrayList<Station>>();

        //팀색용 버퍼
        Stack<Station> buffer = new Stack<Station>();

        //경로탐색
        nodeExplorer(start, end, buffer, list);

        //출력
        String ret = "";
        int index = 0;
        int size = 999999;

        //노드가 가장 적은 역이 어떤 건지 찾음 (여기가 바로 알고리즘 부분 = 최단 탐색)
        for ( int i = 0; i < list.size(); i++){
            ArrayList<Station> item = list.get(i);
            if(list.get(i).size() < size){
                size = list.get(i).size();
                index = i;
            }
        }

//		//모든 경로를 출력한다.
//		for (ArrayList<Station> item : list) {
//			ret += print(item);
//		}
//		ret += "\r\n\r\n";

        //최단 경로를 출력한다.
        ret += " ";
        ret += print(list.get(index));
        return ret;
    }

//    private String print(ArrayList<Station> item) {
//        StringBuffer sb = new StringBuffer();
//        sb.append("지나간 역 개수는 : " + item.size() + "**\n");
//
//        //item이란 배열을 s에 차례대로 넣음
//        for (Station s : item) {
//        }
//
//        //첫번째 역과 마지막 역만 나타냄
//        sb.append(item.get(0).toString());
//        sb.append(" -> ");
//        sb.append(item.get(item.size() - 1).toString());
//
//        sb.append("\r\n");
//        return sb.toString();
//    }

    private String print(ArrayList<Station> item) {
        StringBuffer sb = new StringBuffer();
        sb.append("개수,"+item.size() + "\n");

        /**
         * 경부선 1 경부
         * 호남선 2 호남
         * 전라선 3 전라
         * 장항선 4 장항
         * 충북선 5 충북
         * 경북선 6 경북
         * 중앙선 7 중앙
         * 태백선 8 태백
         * 영동선 9 영동
         * 대구선 10 대구
         * 동해남부선 11 동남
         * 경전선 12 경전
         * 동해선 13 동해
         * 동해강릉 14 동강
         * 광주송정 15 광주송정
         * 평창선 16 평창
         * 도라산선 17 도라산
         * 정선선 18 정선
         * 춘천선 19 춘천
         * **/

        //열차노선번호를 통해 환승하는지 안한하는지 비교
        boolean transfer = false;
        int time = 0;
        int middletime = 0;
        for(int i =0; i < item.size(); i++){
            time =  time + Integer.parseInt(item.get(i).getTime()) ;

            //출발역을 넣는다.
            if(i==0){
                sb.append("출발,");
                sb.append(item.get(i).toString());
                continue;
            }

            if(i < item.size() -2) {
                //열차 노선번호 비교
                if(item.get(i).getId().contains(item.get(i+2).getId()) || item.get(i+2).getId().contains(item.get(i).getId())){

                    //환승아니면
                    if(transfer){
                        transfer = false;

                    } else {
                        sb.append(","+item.get(i).toString());
                        continue;
                    }
                }

                //열차 노선번호 비교했는데 다를 경우
                else {
                    sb.append(","+ item.get(i).toString());
                    transfer = true;
                    middletime = time;
                    sb.append("\n"+ "시간," +time);
                    sb.append("\n"+"환승,");
                    sb.append(item.get(i+1).toString());
                }
            }

            else {
                if(i == item.size()-1){
                    sb.append("\n"+ "시간," + ((time- Integer.parseInt(item.get(i).getTime())) - middletime ));
                    sb.append("\n"+"도착,");
                    sb.append(item.get(i).toString());
                    //sb.append("\n");
                }
                else {
                    sb.append(","+item.get(i).toString());
                }


            }
        }

        return sb.toString();
    }

    /**
     * 노드 탐색(재귀적으로 탐색한다.)
     *  @param point  현재 탐색 역
     * @param end    종착역
     * @param buffer 버퍼
     * @param list   노드리스트
*
*               재귀의 구조 도봉에서 석계를 간다고 가정할 때 처음 도봉의 전역, 다음역으로 재귀를 호출 전역은 망월사 -> 회룡
*               -> 의정부 북부까지 갔는데 해당 역이 종착역을 만나지 못하면 pop으로 다시 도봉까지 돌아옴 도봉역에서 방학->
*               창동-> 노원 -> 상계 -> 당고개를 가지만 또 해당역이 종착을 못 만나고 pop으로 돌아옴.그러나 중간에
*               노원에서 분기되었기 때문에 노원에서 재탐색을 실시. 쌍문-> 수유 쪽의 경로를 탐색하게 됨 그런 식으로 석계역을
     * @return
     */

    private boolean nodeExplorer(Station point, Station end, Stack<Station> buffer, ArrayList<ArrayList<Station>> list) {
        //탐색역과 종착역이 같으면 도착함
        if(point == end) {

            //탐색 노드 선언
            ArrayList<Station> root = new ArrayList<Station>();

            //노드 담기
            for(Station s : buffer) {
                root.add(s);
            }

            //마지막역 담기
            root.add(point);

            //리스트에 추가
            list.add(root);

            //종료
            return true;
        }

        //현재역이 없으면 재탐색
        if(point == null){
            return false;
        }

        //버퍼에 현재 역 담기
        buffer.push(point);

        //현재역의 전역 개수만큼
        for(int i = 0; i < point.getPrevCount(); i++){
            //버퍼에 현재역이 있으면 돌아가기 ( 지나간 역을 다시 지나가면 의미 없음
            //예)종각에서 시청을 갔는데 시청에서 다시 종각으로 돌아가면 의미 없음
            if(buffer.contains(point.getPrev(i))){
                continue;
            }

            //없으면 전역으로 이동
            if(!nodeExplorer(point.getPrev(i), end, buffer, list)){

                //재탐색이 되면 현재역은 경로가 아님
                if(buffer.size() > 0){
                    buffer.pop();
                }
            }
        }

        //현재역의 다음역 개수만큼
        for(int i =0; i < point.getNextCount(); i++){
            // 버퍼에 현재역이 있으면 돌아가기(지나간 역을 다시 지나가면 의미없음)
            // 예)종각에서 시청을 갔는데 시청에서 다시 종각으로 돌아가면 의미 없음
            if(buffer.contains(point.getNext(i))){
                continue;
            }
            if(!nodeExplorer(point.getNext(i), end, buffer, list)){

                //재탐색이 되면 현재 역은 경로가 아님
                if(buffer.size() > 0){
                    buffer.pop();
                }
            }
        }
        //재탐색
        return false;
    }
}
