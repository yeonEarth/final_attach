package Page2;

// 어댑터와 공유해 사용
public class Recycler_item {
    String image;
    String title;
    String contentviewID;
    String type;

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

    public Recycler_item(String image, String title, String contentviewID, String type) {
        this.image = image;
        this.title = title;
        this.contentviewID = contentviewID;
        this.type = type;
    }
}
