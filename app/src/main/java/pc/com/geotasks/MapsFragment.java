package pc.com.geotasks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import pc.com.geotasks.model.Task;

/**
 * created by Stefan 16.04.2016
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set title of actionbar
        AppCompatActivity aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.task_map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<Marker> markers = new ArrayList<>();
        int color = android.graphics.Color.argb(50, 33, 150, 243);
        int colorBorder = android.graphics.Color.argb(100, 33, 150, 243);

        ArrayList<Task> tasks = MainActivity.db.getTasks("");

        for(int i=0; i<tasks.size(); i++){
            double lat = tasks.get(i).getLatitude();
            double lng = tasks.get(i).getLongitude();
            String title = tasks.get(i).getName();
            String desc = tasks.get(i).getDescription();
            String addr = tasks.get(i).getLocationAddress();
            String loc = tasks.get(i).getLocationName();
            int radius = tasks.get(i).getRadius();

            LatLng tmp = new LatLng(lat, lng);
            Marker tmpMarker = mMap.addMarker(new MarkerOptions().position(tmp).title(title).snippet(desc + "\n" + loc + ", " + addr));
            markers.add(tmpMarker);

            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(tmp)
                    .radius(radius)// In meters
                    .strokeColor(colorBorder)
                    .fillColor(color)
                    .strokeWidth(5f);

            // Get back the mutable Circle
            Circle circle = mMap.addCircle(circleOptions);
        }

//        // Add a marker in Sydney and move the camera
//        LatLng ma1 = new LatLng(49, 8);
//        LatLng ma2 = new LatLng(49.487528, 8.469828);
//        LatLng ma3 = new LatLng(49.487619, 8.466223);
//        Marker markSyd = mMap.addMarker(new MarkerOptions().position(ma1).title("Marker in Sydney"));
//        Marker markSyd1 = mMap.addMarker(new MarkerOptions().position(ma2).title("Marker in Sydney"));
//        Marker markSyd2 = mMap.addMarker(new MarkerOptions().position(ma3).title("Marker in Sydney"));
//
//        markers.add(markSyd);
//        markers.add(markSyd1);
//        markers.add(markSyd2);
//
//        // Instantiates a new CircleOptions object and defines the center and radius
//        CircleOptions circleOptions = new CircleOptions()
//                .center(ma1)
//                .radius(5000) //in meters
//                .strokeColor(colorBorder)
//                .fillColor(color)
//                .strokeWidth(5f);
//
//        // Get back the mutable Circle
//        Circle circle = mMap.addCircle(circleOptions);

        //calc bounds of all markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        //obtain a movement description objec
        int padding = convertDpToPixel(100); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);
//        mMap.animateCamera(cu);

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        //show users location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * returns value in db from px value
     * @param dp
     * @return dp
     */
    public int convertDpToPixel(float dp){
        Resources r = getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
