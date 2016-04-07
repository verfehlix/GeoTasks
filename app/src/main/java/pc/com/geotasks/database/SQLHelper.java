package pc.com.geotasks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ich on 07.04.2016.
 */

public class SQLHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContainer.Task.TABLE_NAME + " (" +
                    TaskContainer.Task._ID + " INTEGER PRIMARY KEY," +
                    TaskContainer.Task.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContainer.Task.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GeoTasks.db";

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void initDatabase(){
        //If db exists return except db version changes
        //            db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    //Accessing FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
}
