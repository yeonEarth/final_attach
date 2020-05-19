package Page2_1_1;

public class course {
    //관광지 주제, 추천코스의 역1, 역2, 역3, 역4
    String subject, st1, st2, st3, st4;

    public course(String subject, String st1, String st2, String st3, String st4) {
        super();
        this.subject = subject;
        this.st1 = st1;
        this.st2 = st2;
        this.st3 = st3;
        this.st4 = st4;
    }


    public String getSt1() {
        return st1;
    }

    public void setSt1(String st1) {
        this.st1 = st1;
    }

    public String getSt2() {
        return st2;
    }

    public void setSt2(String st2) {
        this.st2 = st2;
    }

    public String getSt3() {
        return st3;
    }

    public void setSt3(String st3) {
        this.st3 = st3;
    }

    public String getSt4() {
        return st4;
    }

    public void setSt4(String st4) {
        this.st4 = st4;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
