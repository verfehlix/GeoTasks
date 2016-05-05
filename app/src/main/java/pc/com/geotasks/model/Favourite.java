package pc.com.geotasks.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by totto on 08.04.2016.
 */
public class Favourite {
    private int         ID;
    private String      name;
    private String      locationName;
    private String      locationAddress;
    private double      longitude;
    private double      latitude;

    public Favourite(){

    }

    public Favourite(String name, String locationName, String locationAddress, double latitude, double longitude){
        this.name               = name;
        this.locationName       = locationName;
        this.locationAddress    = locationAddress;
        this.longitude          = longitude;
        this.latitude           = latitude;

        Calendar calendar   = Calendar.getInstance();
        Date now            = calendar.getTime();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationName() { return locationName; }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress;}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
