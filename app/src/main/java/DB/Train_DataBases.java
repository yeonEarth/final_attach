package DB;

/**
 * http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
 */

import android.provider.BaseColumns;

public final class Train_DataBases {

    //데이터베이스 테이블 구성
    public static final class CreateDB implements BaseColumns {
        public static final String TEMPORARY = "temporary";
        public static final String NUMBER = "number";
        public static final String DATE = "date";
        public static final String DAYPASS = "daypass";
        public static final String STATION = "station";
        public static final String TIME = "time";
        public static final String CONTENTID = "contentid";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +TEMPORARY+" text , "
                +NUMBER+" text not null , "
                +DATE+" text not null , "
                +DAYPASS+" text , "
                +STATION+" text , "
                +TIME+" text , "
                +CONTENTID+" text );";
    }
}