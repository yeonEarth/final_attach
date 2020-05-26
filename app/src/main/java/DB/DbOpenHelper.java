package DB;

/**
 *http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class  DbOpenHelper {

    private static final String DATABASE_NAME = "HeartCity_data(2).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }


    public DbOpenHelper(Context context){
        this.mCtx = context;
    }


    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }


    public void create(){
        mDBHelper.onCreate(mDB);
    }


    public void close(){
        mDB.close();
    }


    // Insert DB
    public long insertColumn(String userid, String name, String city, String type, String image, String click){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.USERID, userid);
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.CITYNAME, city);
        values.put(DataBases.CreateDB.TYPE, type);
        values.put(DataBases.CreateDB.IMAGE, image);
        values.put(DataBases.CreateDB.CLICK, click);
        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }

    // Update DB
    public boolean updateColumn(long id, String userid, String name, String cityname, String type, String image, String click){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.USERID, userid);
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.CITYNAME, cityname);
        values.put(DataBases.CreateDB.TYPE, type);
        values.put(DataBases.CreateDB.IMAGE, image);
        values.put(DataBases.CreateDB.CLICK, click);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }


    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
    }


    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }


    // Delete DB
    public boolean deleteColumnByContentID(String contentId){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "userid="+contentId, null) > 0;
    }


    // Select DB
    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    // 컨텐츠id 검색
    public Cursor selectIdCulumns(String contentID) {
//        Cursor c = mDB.rawQuery("SELECT * FROM usertable WHERE userid = '" + contentID + "'", null);
//        return c;
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, contentID, null, null, null,null);
    }


    // sort by column
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT DISTINCT * FROM usertable ORDER BY " + sort + " ASC;", null);
        return c;
    }

    // 도시 이름만 검색 및 중복 제거
    public Cursor sortCityColumn(String sort) {
        Cursor c = mDB.rawQuery( "SELECT DISTINCT cityname FROM usertable ORDER BY " + sort + " ASC;", null);
        return c;
    }
}
