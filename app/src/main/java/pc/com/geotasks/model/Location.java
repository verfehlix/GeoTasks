package pc.com.geotasks.model;

/**
 * Created by totto on 05.05.2016.
 */
public class Location {
    private String      locationName;
    private String      locationAddress;
    private double      longitude;
    private double      latitude;

    private int         radius;

    public Location(String locationName, String locationAddress, double longitude, double latitude, int radius) {
        this.locationName = locationName;
        this.locationAddress = locationAddress;

        this.longitude = longitude;
        this.latitude = latitude;

        this.radius = radius;

    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
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

}
