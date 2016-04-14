package pc.com.geotasks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pc.com.geotasks.model.Task;

/**
 * Created by totto on 07.04.2016.
 */

public class SQLHelper extends SQLiteOpenHelper {
    private static final String SHORT_TEXT_TYPE = " varchar(255)";
    private static final String TEXT_TYPE = " text(255)";
    private static final String DATE_TYPE = " TIMESTAMP(255)";
    private static final String INT_TYPE = " int(8)";
    private static final String DOUBLE_TYPE = " double";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContainer.Task.TABLE_NAME + " (" +
                    TaskContainer.Task.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    TaskContainer.Task.COLUMN_NAME_NAME + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LOCATION_NAME + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LOCATION_ADDRESS + SHORT_TEXT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_RADIUS + INT_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_DUE_DATE + DATE_TYPE + COMMA_SEP +
                    TaskContainer.Task.COLUMN_NAME_TIMESTAMP + " TIMESTAMP DEFAULT 'now'" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContainer.Task.TABLE_NAME;

    public static final int DATABASE_VERSION = 2;
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
        values.put(TaskContainer.Task.COLUMN_NAME_NAME              , task.getName());
        values.put(TaskContainer.Task.COLUMN_NAME_DESCRIPTION       , task.getDescription());
        values.put(TaskContainer.Task.COLUMN_NAME_LOCATION_NAME     , task.getLocationName());
        values.put(TaskContainer.Task.COLUMN_NAME_LOCATION_ADDRESS  , task.getLocationAddress());
        values.put(TaskContainer.Task.COLUMN_NAME_LONGITUDE         , task.getLongitude());
        values.put(TaskContainer.Task.COLUMN_NAME_LATITUDE          , task.getLatitude());
        values.put(TaskContainer.Task.COLUMN_NAME_RADIUS            , task.getRadius());
        values.put(TaskContainer.Task.COLUMN_NAME_DUE_DATE          , task.getDueDate().toString());
        values.put(TaskContainer.Task.COLUMN_NAME_TIMESTAMP         , task.getTimestamp().toString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TaskContainer.Task.TABLE_NAME,
                TaskContainer.Task.COLUMN_NAME_NAME,
                values);

        task.setID((int) newRowId);
    }

    public ArrayList<Task> getTasks(String filter){
        ArrayList<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TaskContainer.Task.COLUMN_NAME_ID,
                TaskContainer.Task.COLUMN_NAME_NAME,
                TaskContainer.Task.COLUMN_NAME_DESCRIPTION,
                TaskContainer.Task.COLUMN_NAME_LOCATION_NAME,
                TaskContainer.Task.COLUMN_NAME_LOCATION_ADDRESS,
                TaskContainer.Task.COLUMN_NAME_LONGITUDE,
                TaskContainer.Task.COLUMN_NAME_LATITUDE,
                TaskContainer.Task.COLUMN_NAME_RADIUS,
                TaskContainer.Task.COLUMN_NAME_DUE_DATE,
                TaskContainer.Task.COLUMN_NAME_TIMESTAMP,
        };

        String selection = TaskContainer.Task.COLUMN_NAME_NAME + " LIKE '%?%' OR "
                + TaskContainer.Task.COLUMN_NAME_LOCATION_NAME + " LIKE '%?%' OR "
                + TaskContainer.Task.COLUMN_NAME_LOCATION_ADDRESS + " LIKE '%?%' ";
        String[] selectionArgs = new String[]{filter, filter};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = TaskContainer.Task.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor c = db.query(
                TaskContainer.Task.TABLE_NAME,            // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        while(!c.isAfterLast()){
            try {
                int       ID               = c.getInt(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_ID));
                String    name             = c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_NAME));
                String    description      = c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_DESCRIPTION));
                String    locationName     = c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_LOCATION_NAME));
                String    locationAddress  = c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_LOCATION_ADDRESS));
                double    longitude        = c.getDouble(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_LONGITUDE));
                double    latitude         = c.getDouble(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_LATITUDE));
                int       radius           = c.getInt(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_RADIUS));
                Date      dueDate          = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_DUE_DATE)));
                Date      timestamp        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_TIMESTAMP)));

                Task task = new Task(name, description, locationName, locationAddress, longitude, latitude, radius, dueDate);
                task.setID(ID);
                task.setTimestamp(timestamp);
                tasks.add(task);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Timestamp timestamp        = c.getInt(c.getColumnIndexOrThrow(TaskContainer.Task.COLUMN_NAME_TIMESTAMP));

        }

        return tasks;
    }
    //Filtern nach taskname like '%%%'
    //OR adresse like
}
