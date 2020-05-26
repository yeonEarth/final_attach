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

public class Train_DbOpenHelper {

    private static final String DATABASE_NAME = "Train_DB.db";
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
            db.execSQL(Train_DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Train_DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }


    public Train_DbOpenHelper(Context context){
        this.mCtx = context;
    }


    public Train_DbOpenHelper open() throws SQLException {
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
    public long insertColumn(String temporary, String number, String date, String daypass, String station, String time, String contentid){
        ContentValues values = new ContentValues();
        values.put(Train_DataBases.CreateDB.TEMPORARY, temporary);
        values.put(Train_DataBases.CreateDB.NUMBER, number);
        values.put(Train_DataBases.CreateDB.DATE, date);
        values.put(Train_DataBases.CreateDB.DAYPASS, daypass);
        values.put(Train_DataBases.CreateDB.STATION, station);
        values.put(Train_DataBases.CreateDB.TIME, time);
        values.put(Train_DataBases.CreateDB.CONTENTID, contentid);
        return mDB.insert(Train_DataBases.CreateDB._TABLENAME0, null, values);
    }


    // Update DB
    public boolean updateColumn(String temporary, String number, String date, String daypass, String station, String time, String contentid){
        ContentValues values = new ContentValues();
        values.put(Train_DataBases.CreateDB.TEMPORARY, temporary);
        values.put(Train_DataBases.CreateDB.NUMBER, number);
        values.put(Train_DataBases.CreateDB.DATE, date);
        values.put(Train_DataBases.CreateDB.DAYPASS, daypass);
        values.put(Train_DataBases.CreateDB.STATION, station);
        values.put(Train_DataBases.CreateDB.TIME, time);
        values.put(Train_DataBases.CreateDB.CONTENTID, contentid);
        return mDB.update(Train_DataBases.CreateDB._TABLENAME0, values, "number=" + number, null) > 0;
    }


    // Delete All
    public void deleteAllColumns() {
        mDB.delete(Train_DataBases.CreateDB._TABLENAME0, null, null);
    }


    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(Train_DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }


    // Delete DB
    public boolean deleteColumnByKey(String number){
        return mDB.delete(Train_DataBases.CreateDB._TABLENAME0, "number="+number, null) > 0;
    }

    // Select DB
    public Cursor selectColumns(){
        return mDB.query(Train_DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }


    // sort by column
    public Cursor selecteNumber(String sort){
        Cursor c = mDB.rawQuery( "SELECT* FROM usertable  where number=" + "'" + sort + "'", null);
        return c;
    }

    // sort by column
    public Cursor seleckTemporary(String sort){
        Cursor c = mDB.rawQuery( "SELECT* FROM usertable  where temporary=" + "'" + sort + "'", null);
        return c;
    }
}

