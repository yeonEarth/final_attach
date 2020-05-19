package Page3_1_1_1;

public class Page3_1_1_1_dargData {
    private String name;
    private String number;

    public Page3_1_1_1_dargData(){
    }

    public Page3_1_1_1_dargData(String number, String name){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

}
