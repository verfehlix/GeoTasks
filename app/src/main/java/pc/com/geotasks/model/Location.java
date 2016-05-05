package pc.com.geotasks.model;

/**
 * Created by Ich on 05.05.2016.
 */
public class Location {
    private String      naxme;
    private String      locationName;
    private String      locationAddress;
    private double      longitude;
    private double      latitude;

    public Location() {
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getNaxme() {
        return naxme;
    }

    public void setNaxme(String naxme) {
        this.naxme = naxme;
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
}
