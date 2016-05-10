package pc.com.geotasks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
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

import pc.com.geotasks.database.SQLHelper;
import pc.com.geotasks.model.Task;

/**
 * created by Stefan 16.04.2016
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    SQLHelper db;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set title of actionbar
        AppCompatActivity aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.task_map);

        //setup connection to database
        this.db = new SQLHelper(this.getContext());
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
            Task t = tasks.get(i);
            for(int j=0; j<t.getLocations().size(); j++){
                pc.com.geotasks.model.Location loc = tasks.get(i).getLocations().get(j);
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();
                int taskId = t.getID();
                String title = t.getName();
                String desc = t.getDescription();
                String addr = loc.getLocationAddress();
                String locName = loc.getLocationName();
                int radius = loc.getRadius();

                LatLng tmp = new LatLng(lat, lng);

                MarkerOptions mOptions = new MarkerOptions().position(tmp).title(taskId + " - " + title).snippet(desc + ", Location: " + locName + ", " + addr);
                Marker tmpMarker = mMap.addMarker(mOptions);
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
        }

        if(tasks.size()>0) {
            //calc bounds of all markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if(builder != null && markers != null && markers.size() != 0){
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                if (tasks.size() != 1) {
                    //obtain a movement description objec
                    int padding = convertDpToPixel(100); // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);


                } else {
                    LatLng latLng = new LatLng(tasks.get(0).getLatitude(), tasks.get(0).getLongitude());
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                    mMap.moveCamera(cu);
                }
            }

        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                int taskId = Integer.parseInt(title.split("-")[0].trim());
                double lat = marker.getPosition().latitude;
                double lng = marker.getPosition().longitude;
                String desc = marker.getSnippet();

                Task task = db.getTaskById(taskId);

                Intent intent = new Intent(getContext(), AddNewTaskActivity.class);
                intent.putExtra("mode",2);

                intent.putExtra("taskId",taskId);
                intent.putExtra("taskName",task.getName());
                intent.putExtra("taskTimestamp",task.getTimestamp());
                intent.putExtra("taskDescription",task.getDescription());
                intent.putExtra("taskTag",task.getTag());
                intent.putExtra("taskLocationName",task.getLocationName());
                intent.putExtra("taskLocationAddress",task.getLocationAddress());
                intent.putExtra("taskLatitude",task.getLatitude());
                intent.putExtra("taskLongitude",task.getLongitude());
                intent.putExtra("taskRadius",task.getRadius());
                intent.putExtra("taskDueDate",task.getDueDate().getTime());

                intent.putParcelableArrayListExtra("locations",  task.getLocations());

                getContext().startActivity(intent);

            }
        });
//        mMap.animateCamera(cu);


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
     * returns the max distance of all tasks
     * @param tasks
     */
    public float getMaxDistance(ArrayList<Task> tasks){
        float maxDistance = 0f;
        for(int i=0; i<tasks.size(); i++){
            for(int j=0; j<tasks.size(); j++){
                if(i != j){
                    for(int k=0; k<tasks.get(i).getLocations().size(); k++) {
                        for(int l=0; l<tasks.get(j).getLocations().size(); l++) {
                            pc.com.geotasks.model.Location l1 = tasks.get(i).getLocations().get(k);
                            pc.com.geotasks.model.Location l2 = tasks.get(j).getLocations().get(l);

                            Location loc1 = new Location("");
                            loc1.setLatitude(l1.getLatitude());
                            loc1.setLongitude(l1.getLongitude());

                            Location loc2 = new Location("");
                            loc2.setLatitude(l2.getLatitude());
                            loc2.setLongitude(l2.getLongitude());

                            float tmpDistance = loc1.distanceTo(loc2);
                            if (tmpDistance > maxDistance) {
                                maxDistance = tmpDistance;
                            }
                        }
                    }
                }
            }
        }
        return maxDistance;
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
