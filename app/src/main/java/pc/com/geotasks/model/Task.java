package pc.com.geotasks.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by totto on 08.04.2016.
 */
public class Task {
    private int         ID;
    private String      name;
    private String      description;
    private String      tag;
    private String      locationName;
    private String      locationAddress;
    private double      longitude;
    private double      latitude;
    private int         radius;
    private Timestamp   dueDate;
    private Timestamp   timestamp;

    public Task(){

    }

    public Task(String name, String description, String tag, String locationName, String locationAddress, double latitude, double longitude, int radius, Date dueDate){
        this.name               = name;
        this.description        = description;
        this.tag                = tag;
        this.locationName       = locationName;
        this.locationAddress    = locationAddress;
        this.longitude          = longitude;
        this.latitude           = latitude;
        this.radius             = radius;

        Calendar calendar   = Calendar.getInstance();
        Date now            = calendar.getTime();
        this.timestamp      = new java.sql.Timestamp(now.getTime());

        this.dueDate        = new java.sql.Timestamp(dueDate.getTime());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.name = tag;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = new java.sql.Timestamp(dueDate.getTime());
    }
}
