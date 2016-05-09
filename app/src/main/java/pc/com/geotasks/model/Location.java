package pc.com.geotasks.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by totto on 05.05.2016.
 */
public class Location implements Parcelable {
    private String      locationName;
    private String      locationAddress;
    private double      longitude;
    private double      latitude;

    private int         radius;

    public Location(String locationName, String locationAddress, double latitude, double longitude, int radius) {
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

    public android.location.Location getLocation(){
        android.location.Location loc = new android.location.Location("");
        loc.setLatitude(getLatitude());
        loc.setLongitude(getLongitude());
        return loc;
    }

    protected Location(Parcel in) {
        locationName = in.readString();
        locationAddress = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        radius = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
        dest.writeString(locationAddress);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(radius);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}