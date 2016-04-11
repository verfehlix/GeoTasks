package pc.com.geotasks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;

import pc.com.geotasks.model.Task;

/**
 * Created by totto on 07.04.2016.
 */

public class SQLHelper extends SQLiteOpenHelper {
    private static final String SHORT_TEXT_TYPE = " varchar(255)";
    private static final String TEXT_TYPE = " text(255)";
    private static final String DATE_TYPE = " TIMESTAMP(255)";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContainer.Task.TABLE_NAME + " (" +
                    TaskContainer.Task.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    TaskContainer.Task.COLUMN_NAME_NAME + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LONGITUDE + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LATITUDE + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_RADIUS + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_DUE_DATE + DATE_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_TIMESTAMP + " TIMESTAMP DEFAULT 'now'" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContainer.Task.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GeoTasks.db";

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //public static void initDatabase(){
        //If db exists return except db version changes
        //            db.execSQL(SQL_CREATE_ENTRIES);

    //}

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

    public void addTask(Task task){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskContainer.Task.COLUMN_NAME_NAME          , task.getName());
        values.put(TaskContainer.Task.COLUMN_NAME_DESCRIPTION   , task.getDescription());
        values.put(TaskContainer.Task.COLUMN_NAME_LONGITUDE     , task.getLongitude());
        values.put(TaskContainer.Task.COLUMN_NAME_LATITUDE      , task.getLatitude());
        values.put(TaskContainer.Task.COLUMN_NAME_RADIUS        , task.getRadius());
        values.put(TaskContainer.Task.COLUMN_NAME_DUE_DATE      , task.getDueDate().toString());
        values.put(TaskContainer.Task.COLUMN_NAME_TIMESTAMP     , task.getTimestamp().toString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TaskContainer.Task.TABLE_NAME,
                TaskContainer.Task.COLUMN_NAME_NAME,
                values);

        task.setID((int) newRowId);
    }
}
