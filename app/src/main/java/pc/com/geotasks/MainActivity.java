package pc.com.geotasks;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pc.com.geotasks.database.SQLHelper;
import pc.com.geotasks.model.Favourite;
import pc.com.geotasks.model.Task;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static SQLHelper db;
    private Fragment fragment;
    private FloatingActionButton fab;
    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Integer> tasksInRangeIDs = new ArrayList<>();
    private HashMap<Integer, Boolean> notifiedMap = new HashMap<>();
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add floating action button in the bottom right
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
                intent.putExtra("mode",1);
                startActivity(intent);
            }
        });

        //add navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //establish db and add new test task
        this.db = new SQLHelper(this.getApplicationContext());
        Task t1 = null;
        Task t2 = null;
//        try {
//            t1 = new Task("Universität Mannheim", "des 1", "blub", "ln 1", "la 1",  49.487521, 8.458106, 500, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-04-27 13:37:00"));
////            t2 = new Task("Irgendwas", "des 1", "blub", "ln 1", "la 1",  50, 10, 200, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-04-27 13:37:00"));
////            db.addTask(t1);
////            db.addTask(t2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Favourite f = new Favourite("Universität Mannheim", "ln 1", "la 1",  49.487521, 8.458106);
        db.addFavourite(f);

        fragment = new TaskListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();

        //sendNotification("VIRUS", "Ihr Gerät wurde durch einem Virus infiziert. Bitte leiten Sie umgehend alle Schritte zur sofortigen Zerstörung ein.", 0);
        setUpGPSService();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tasks) {
            fragment = new TaskListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        } else if (id == R.id.nav_maps) {
            MapsFragment fragment = new MapsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
//            Intent mapsIntent = new Intent(this, MapsActivity.class);
//            startActivity(mapsIntent);
        }  else if (id == R.id.nav_manage) {
            SettingsActivity fragment = new SettingsActivity();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();

            //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //startActivity(intent);
        }

        //sets visibility of FloatingActionButton
        if (id == R.id.nav_tasks) {
            setFloatingActionButtonVisible(true);
        } else {
            setFloatingActionButtonVisible(false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @author Stefan
     * sets the FloatingActionButton to isVisible
     * @param isVisible
     */
    public void setFloatingActionButtonVisible(boolean isVisible) {
        if (isVisible) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    /**
     * Sends a popup notification to the android system to
     * display in the notification bar
     * @param notification notificaton text
     * @param title title
     */
    public void sendNotification(String title, String notification, int taskID) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_perm_group_location_white)
                            .setContentTitle(title)
                            .setContentText(notification)
                            .setAutoCancel(true);
            // Creates an explicit intent for an Activity in your app

            Task task = db.getTaskById(taskID);
            Intent resultIntent;

            if(task != null){
                resultIntent = new Intent(this, AddNewTaskActivity.class);
                resultIntent.putExtra("mode",2);
                resultIntent.putExtra("taskId",taskID);
                resultIntent.putExtra("taskName",task.getName());
                resultIntent.putExtra("taskTimestamp",task.getTimestamp());
                resultIntent.putExtra("taskDescription",task.getDescription());
                resultIntent.putExtra("taskTag",task.getTag());
                resultIntent.putExtra("taskLocationName",task.getLocationName());
                resultIntent.putExtra("taskLocationAddress",task.getLocationAddress());
                resultIntent.putExtra("taskLatitude",task.getLatitude());
                resultIntent.putExtra("taskLongitude",task.getLongitude());
                resultIntent.putExtra("taskRadius",task.getRadius());
                resultIntent.putExtra("taskDueDate",task.getDueDate().getTime());
            } else {
                resultIntent = new Intent(this, MainActivity.class);
                Toast.makeText(getBaseContext(), "Could not find task from notification in DB!", Toast.LENGTH_LONG).show();
            }

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(taskID, mBuilder.build());
        }

    /**
     * creates the GPS tracking service
     */
    public void setUpGPSService() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//        use last known location
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        location = lastKnownLocation;
        if(lastKnownLocation != null) {
            handleLocation(lastKnownLocation);
        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location changed");
                handleLocation(location);
                MainActivity.location = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
//                Toast.makeText(getBaseContext(), "Gps turned on", Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String provider) {
//                Toast.makeText(getBaseContext(), "Gps turned off", Toast.LENGTH_LONG).show();
            }
        };

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

        Log.d(TAG, "Permission true");
        // Register the listener with the Location Manager to receive location updates
        //
        // second parameters the minimum time interval between notifications
        // and the third is the minimum change in distance (meters) between notifications
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
    }

    /**
     * handles the current location and sends a notification if next to the location of the task
     * @param location
     */
    public void handleLocation(Location location){
//        Toast toast = Toast.makeText(MainActivity.this, "lat: " + location.getLatitude() + "\nlong" + location.getLongitude(), Toast.LENGTH_LONG);
//        toast.show();

        ArrayList<Task> tasksInRange = MainActivity.db.getAllTasksInRange(location);

        for (Map.Entry<Integer, Boolean> entry : notifiedMap.entrySet()) {
            int key = entry.getKey();
            boolean value = entry.getValue();

            boolean inList = false;

            for(int i=0; i<tasksInRange.size();i++){
                if(key == tasksInRange.get(i).getID()){
                    inList = true;
                }
            }

            if(!inList){
                notifiedMap.put(key, false);
            }
        }

        Log.d("MainActivity", "Tasks in Range: " + tasksInRange.size());
        for(int i=0; i<tasksInRange.size(); i++){
            Log.d("MainActivity", "Task in Range: " + tasksInRange.get(i).getName());
            if(notifiedMap.get(tasksInRange.get(i).getID()) == null){
                sendNotification(tasksInRange.get(i).getName() + " reminder", "You are next to the location of the task \"" + tasksInRange.get(i).getName() + "\"", tasksInRange.get(i).getID());
                notifiedMap.put(tasksInRange.get(i).getID(), true);
            }else {
                if (!notifiedMap.get(tasksInRange.get(i).getID())) {
                    sendNotification(tasksInRange.get(i).getName() + " reminder", "You are next to the location of the task \"" + tasksInRange.get(i).getName() + "\"", tasksInRange.get(i).getID());
                    notifiedMap.put(tasksInRange.get(i).getID(), true);
                }
            }
        }
    }
}
