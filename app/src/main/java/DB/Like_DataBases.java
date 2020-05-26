package DB;

import android.provider.BaseColumns;

public final class Like_DataBases {

    //데이터베이스 테이블 구성
    public static final class CreateDB implements BaseColumns {
        public static final String LIKE = "like";
        public static final String NICKNAME = "nickname";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +LIKE+" text  not null ); "
                +NICKNAME+" text not null ); ";
    }
}
