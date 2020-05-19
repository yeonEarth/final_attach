package Page3_1_1;

import java.io.Serializable;

public class Page3_1_1_dargData  implements Serializable {
    private String name;
    private int number;

    public Page3_1_1_dargData(){
    }

    public Page3_1_1_dargData(String name, int number){
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber (int number) {
        this.number = number;
    }
}
