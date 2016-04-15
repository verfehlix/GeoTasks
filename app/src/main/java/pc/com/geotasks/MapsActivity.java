package pc.com.geotasks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        ArrayList<Task> tasks = MainActivity.db.getTasks("");
//
//        for(int i=0; i<tasks.size(); i++){
//            double lat = tasks.get(i).getLatitude();
//            double lng = tasks.get(i).getLongitude();
//            String title = tasks.get(i).getName();
//            String desc = tasks.get(i).getDescription();
//            String addr = tasks.get(i).getLocationAddress();
//            String loc = tasks.get(i).getLocationName();
//            int radius = tasks.get(i).getRadius();
//
//            LatLng tmp = new LatLng(lat, lng);
//            mMap.addMarker(new MarkerOptions().position(tmp).title(title).snippet(desc + "\n" + loc + ", " + addr));
//
//            // Instantiates a new CircleOptions object and defines the center and radius
//            CircleOptions circleOptions = new CircleOptions()
//                    .center(new LatLng(lat, lng))
//                    .radius(radius); // In meters
//
//            // Get back the mutable Circle
//            Circle circle = mMap.addCircle(circleOptions);
//        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        int color = android.graphics.Color.argb(50, 33, 150, 243);
        int colorBorder = android.graphics.Color.argb(100, 33, 150, 243);

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(-34, 151))
                .radius(1000) //in meters
                .strokeColor(colorBorder)
                .fillColor(color)
                .strokeWidth(5f);

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        //show users location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
