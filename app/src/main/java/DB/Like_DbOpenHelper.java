package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Like_DbOpenHelper {
    private static final String DATABASE_NAME = "Like_DB.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Like_DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ Like_DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public Like_DbOpenHelper(Context context){
        this.mCtx = context;
    }


    public Like_DbOpenHelper open() throws SQLException {
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

    // 취향파악 DB insert
    public long insertLikeColumn(String like, String nickname) {
        ContentValues values = new ContentValues();
        values.put(Like_DataBases.CreateDB.LIKE, like);
        values.put(Like_DataBases.CreateDB.NICKNAME, nickname);
        return mDB.insert(Like_DataBases.CreateDB._TABLENAME0, null, values);
    }

    // Select DB
    public Cursor selectColumns(){
        return mDB.query(Like_DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }
}
