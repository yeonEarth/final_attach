package Page2;

// 어댑터와 공유해 사용
public class Recycler_item {
    String image;
    String title;
    String contentviewID;
    String type;
    String areaCode;
    String sigunguCode;

    public String getImage() {
        return this.image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContentviewID() {
        return this.contentviewID;
    }

    public String getType() {
        return this.type;
    }

    public String getAreaCode() { return this.areaCode; }

    public String getSigunguCode() { return this.sigunguCode; }


    public Recycler_item(String image, String title, String contentviewID, String type, String areaCode, String sigunguCode) {
        this.image = image;
        this.title = title;
        this.contentviewID = contentviewID;
        this.type = type;
        this.areaCode = areaCode;
        this.sigunguCode = sigunguCode;
    }
}
