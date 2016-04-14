package pc.com.geotasks;

/**
 * Created by Stefan on 12.04.2016.
 */
public class ListObject {
    private String name;
    private String description;
    private String location;
    private String dateAdded;

    public ListObject(String name, String location){
        this.name = name;
        this.location = location;
    }

    public String getName(){
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
