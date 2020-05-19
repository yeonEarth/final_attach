package Algorithm;

public class SubwayException extends Exception {
    private static final long serialVersionUID = 1L;
    /**
     * 예외처리
     * @param message
     * throws 에 대해서 공부할 필요 있음 ( throw 와 throws 의 차이점
     */
    public SubwayException(String message){
        super(message);
    }
}
