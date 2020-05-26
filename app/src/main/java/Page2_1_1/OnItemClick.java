package Page2_1_1;

import java.util.ArrayList;

import Page2_X.Page2_X_CategoryBottom;

public interface OnItemClick {
    void onClick(double x, double y, String name);
    void make_db(String countId, String name, String cityname, String type, String image, String click);
    void delete_db(String contentId);
    String isClick(String countid);
    void make_dialog();
    void onData(ArrayList<Page2_X_CategoryBottom.Category_item> text);

}
