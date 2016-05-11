package pc.com.geotasks.database;

import android.provider.BaseColumns;

/**
 * Created by totto on 07.04.2016.
 */
public class FavouriteContainer {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FavouriteContainer() {}

    /* Inner class that defines the table contents */
    public static abstract class Favourite implements BaseColumns {
        public static final String TABLE_NAME                    = "favourites";

        public static final String COLUMN_NAME_ID                = "id";

        public static final String COLUMN_NAME_NAME              = "name";

        public static final String COLUMN_NAME_LOCATION_NAME     = "locationName";
        public static final String COLUMN_NAME_LOCATION_ADDRESS  = "locationAddress";
        public static final String COLUMN_NAME_LONGITUDE         = "longitude";
        public static final String COLUMN_NAME_LATITUDE          = "latitude";
    }

}
