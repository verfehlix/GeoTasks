package pc.com.geotasks.model;

import java.sql.Timestamp;

import pc.com.geotasks.database.SQLHelper;
import pc.com.geotasks.database.TaskContainer;

/**
 * Created by totto on 08.04.2016.
 */
public class Task {
    private int         ID;
    private String      name;
    private String      description;
    private String      longitude;
    private String      latitude;
    private String      radius;
    private Timestamp   dueDate;
    private Timestamp   timestamp;

    public Task(){

    }

    public Task(String name, String description, String longitude, String latitude, String radius, Timestamp dueDate){
        this.name           = name;
        this.description    = description;
        this.longitude      = longitude;
        this.latitude       = latitude;
        this.radius         = radius;
        this.dueDate        = dueDate;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
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

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
