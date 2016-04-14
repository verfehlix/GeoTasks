package pc.com.geotasks.database;

import android.provider.BaseColumns;

/**
 * Created by Ich on 07.04.2016.
 */
public class TaskContainer {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaskContainer() {}

    /* Inner class that defines the table contents */
    public static abstract class Task implements BaseColumns {
        public static final String TABLE_NAME                    = "tasks";

        public static final String COLUMN_NAME_ID                = "id";

        public static final String COLUMN_NAME_NAME              = "name";
        public static final String COLUMN_NAME_DESCRIPTION       = "description";

        public static final String COLUMN_NAME_LOCATION_NAME     = "locationName";
        public static final String COLUMN_NAME_LOCATION_ADDRESS  = "locationAddress";
        public static final String COLUMN_NAME_LONGITUDE         = "longitude";
        public static final String COLUMN_NAME_LATITUDE          = "latitude";
        public static final String COLUMN_NAME_RADIUS            = "radius";

        public static final String COLUMN_NAME_DUE_DATE          = "dueDate";

        public static final String COLUMN_NAME_TIMESTAMP         = "timestamp";
    }

}
