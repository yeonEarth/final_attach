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

public class Page3_DbOpenHelper {

    private static final String DATABASE_NAME = "Page3.db";
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
            db.execSQL(Page3_DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Page3_DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }


    public Page3_DbOpenHelper(Context context){
        this.mCtx = context;
    }


    public Page3_DbOpenHelper open() throws SQLException {
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
    public long insertColumn(String daypass, String station){
        ContentValues values = new ContentValues();
        values.put(Page3_DataBases.CreateDB.DAYPASS, daypass);
        values.put(Page3_DataBases.CreateDB.STATION, station);
        return mDB.insert(Page3_DataBases.CreateDB._TABLENAME0, null, values);
    }


    // Update DB
    public boolean updateColumn(long id, String daypass, String station){
        ContentValues values = new ContentValues();
        values.put(Page3_DataBases.CreateDB.DAYPASS, daypass);
        values.put(Page3_DataBases.CreateDB.STATION, station);
        return mDB.update(Page3_DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }


    // Delete All
    public void deleteAllColumns() {
        mDB.delete(Page3_DataBases.CreateDB._TABLENAME0, null, null);
    }


    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(Page3_DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }


    // Select DB
    public Cursor selectColumns(){
        return mDB.query(Page3_DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }


    // sort by column
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);
        return c;
    }
}
