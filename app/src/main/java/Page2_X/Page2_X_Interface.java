package Page2_X;

import java.util.ArrayList;

public interface Page2_X_Interface {
    void onClick(double x, double y, String name);
    void make_db(String countId, String name, String cityname, String type, String image, String click);
    void delete_db(String contentId);
    String isClick(String countid);
    void make_dialog();
    void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text);
}
