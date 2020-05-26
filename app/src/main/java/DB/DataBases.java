package DB;

/**
 * http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
 */

import android.provider.BaseColumns;

public final class DataBases {

    //데이터베이스 테이블 구성
    public static final class CreateDB implements BaseColumns {
        public static final String USERID = "userid";
        public static final String NAME = "name";
        public static final String CITYNAME = "cityname";
        public static final String TYPE = "type";
        public static final String IMAGE = "image";
        public static final String CLICK = "click";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +USERID+" text not null , "
                +NAME+" text not null , "
                +CITYNAME+" text not null ,"
                +TYPE+" text not null , "
                +IMAGE+" text not null , "
                +CLICK+" text  not null ); ";
    }
}