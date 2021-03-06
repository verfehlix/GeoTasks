package pc.com.geotasks;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pc.com.geotasks.database.SQLHelper;
import pc.com.geotasks.model.Favourite;
import pc.com.geotasks.model.Task;

/*
*   Add New Task Activity - used to add new tasks into the database
*   author: fmagnus
* */
public class AddNewTaskActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //variables
    SQLHelper db;

    Dialog dialog;

    Toolbar toolbar;
    Button exitButton;
    Button saveButton;
    EditText editTextTaskName;
    EditText editTextTaskDescription;
    EditText editTextLocationAutocomplete;
    EditText datePickerEditText;
    EditText meterEditText;
    EditText timePickerEditText;
    EditText categoryEditText;
    EditText editTextFavourite;
    CheckBox checkBoxFavourite;
    TextView textViewLngLtd;
    TextView orTextView;
    TextView or2TextView;
    Spinner favouriteSpinner;
    Switch useCurrentLocationSwitch;
    SeekBar radiusSeekBar;

    Button buttonAddLocation;

    Calendar cal = Calendar.getInstance();
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH);
    int year = cal.get(Calendar.YEAR);
    int hour = cal.get(Calendar.HOUR);
    int minute = cal.get(Calendar.MINUTE);

    Place currentPlace;
    Location lastKnownLocation;

    GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final String TAG = TaskListFragment.class.getSimpleName();

    public static final int MODE_NEW = 1;
    public static final int MODE_VIEW = 2;
    public static final int MODE_EDIT = 3;

    private int currentMode;


    private int taskId;
    private String taskName;
    private String taskDescription;
    private String taskTag;
    private String taskLocationName;
    private String taskLocationAddress;
    private double taskLatitude;
    private double taskLongitude;
    private int taskRadius;
    private Date taskDueDate;

    private ArrayList<pc.com.geotasks.model.Location> selectedLocations = new ArrayList<pc.com.geotasks.model.Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        //get dialog
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.location_layout);
        dialog.setTitle("Select Location");
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_location_on_black_24dp);

        //handle extras
        Bundle extras = getIntent().getExtras();
        this.currentMode = extras.getInt("mode");

        if(this.currentMode == MODE_VIEW){
            this.taskId = extras.getInt("taskId");
            this.taskName = extras.getString("taskName");
            this.taskDescription = extras.getString("taskDescription");
            this.taskTag = extras.getString("taskTag");
            this.taskLocationName = extras.getString("taskLocationName");
            this.taskLocationAddress = extras.getString("taskLocationAddress");
            this.taskLatitude = extras.getDouble("taskLatitude");
            this.taskLongitude = extras.getDouble("taskLongitude");
            this.taskDueDate = new Date (); this.taskDueDate.setTime(extras.getLong("taskDueDate"));
            this.taskRadius = extras.getInt("taskRadius");

            this.selectedLocations = extras.getParcelableArrayList("locations");
        }

        //setup connection to database
        this.db = new SQLHelper(this.getApplicationContext());

        //init location Manager
        setUpGPSService();

        //clear place
        this.currentPlace = null;

        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //hide the title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get buttons
        exitButton = (Button) findViewById(R.id.exitButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        buttonAddLocation = (Button) findViewById(R.id.buttonAddLocation);

        //get inputs
        editTextTaskName = (EditText) findViewById(R.id.editTextTaskName);
        editTextTaskDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);
        editTextLocationAutocomplete = (EditText) dialog.findViewById(R.id.editTextLocationAutocomplete);
        meterEditText = (EditText) dialog.findViewById(R.id.meterEditText);
//        datePickerEditText = (EditText) findViewById(R.id.datePickerEditText);
//        timePickerEditText = (EditText) findViewById(R.id.timePickerEditText);

        //get text views
        textViewLngLtd = (TextView) dialog.findViewById(R.id.textViewLngLtd);
//        orTextView = (TextView) dialog.findViewById(R.id.orTextView);
        or2TextView = (TextView) dialog.findViewById(R.id.or2TextView);

        favouriteSpinner = (Spinner) dialog.findViewById(R.id.spinnerFavourites);

        editTextFavourite = (EditText) dialog.findViewById(R.id.editTextFavourite);
        editTextFavourite.setVisibility(View.GONE);
        checkBoxFavourite = (CheckBox) dialog.findViewById(R.id.checkBoxFavourite);

        checkBoxFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editTextFavourite.setVisibility(View.VISIBLE);
                } else {
                    editTextFavourite.setVisibility(View.GONE);
                }
            }
        });

        //get switch
//        useCurrentLocationSwitch = (Switch) dialog.findViewById(R.id.useCurrentLocationSwitch);

        //get radius seekbar
        radiusSeekBar = (SeekBar) dialog.findViewById(R.id.radiusSeekBar);

        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int defaultValue = 150;
        long defaultRadius = sharedPref.getInt("radiusDefault", defaultValue);
        radiusSeekBar.setProgress((int)defaultRadius);
        meterEditText.setText(defaultRadius+"");

        editTextTaskName.setTag(editTextTaskName.getKeyListener());
        editTextTaskDescription.setTag(editTextTaskDescription.getKeyListener());
        categoryEditText.setTag(categoryEditText.getKeyListener());
        editTextLocationAutocomplete.setTag(editTextLocationAutocomplete.getKeyListener());
//        datePickerEditText.setTag(datePickerEditText.getKeyListener());
        meterEditText.setTag(meterEditText.getKeyListener());
//        timePickerEditText.setTag(timePickerEditText.getKeyListener());

        if(currentMode == MODE_NEW){
            enableControls();
        }
        if(currentMode == MODE_VIEW){
            disableControls();
            setEditButton();
            fillTextEditsWithTaskData();

//            for (pc.com.geotasks.model.Location loc: selectedLocations) {
//                Log.d("testing",loc.getLocationAddress());
//                Log.d("testing",loc.getLocationName());
//                Log.d("testing",loc.getLongitude()+"");
//                Log.d("testing",loc.getLatitude()+"");
//                Log.d("testing","------------------------------");
//            }
        }

        //init google maps api component
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

    }

    //method that fills controls with data when a task is recieved in the extras
    private void fillTextEditsWithTaskData() {

        //task data
        editTextTaskName.setText(taskName);
        editTextTaskDescription.setText(taskDescription);
        categoryEditText.setText(taskTag);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String dateString = dateFormat.format(taskDueDate);
        String timeString = timeFormat.format(taskDueDate);
//        datePickerEditText.setText(dateString);
//        timePickerEditText.setText(timeString);

        //location data
        TextView locAddrName = (TextView) findViewById(R.id.location1AddrName);
        TextView locLongLat = (TextView) findViewById(R.id.location1LongLat);

        int count = 0;
        for (pc.com.geotasks.model.Location loc : selectedLocations) {
            if(count == 0){
                locAddrName.setText(loc.getLocationName());
                locLongLat.setText("(" + loc.getRadius() + "m) " +loc.getLocationAddress());
            } else {
                LinearLayout locationHolderLayout = (LinearLayout) findViewById(R.id.locationHolderLayout);

                TextView tv1 = new TextView(locationHolderLayout.getContext());
                locationHolderLayout.addView(tv1);
                tv1.setTextAppearance(AddNewTaskActivity.this, android.R.style.TextAppearance_DeviceDefault_Medium);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv1.getLayoutParams();
                params.setMargins(0, 25, 0, 0);
                tv1.setLayoutParams(params);
                tv1.setText(loc.getLocationName());

                TextView tv2 = new TextView(locationHolderLayout.getContext());
                locationHolderLayout.addView(tv2);
                tv2.setText("(" + loc.getRadius() + "m) " +loc.getLocationAddress());
            }

//            String locName = editTextLocationAutocomplete.getText().toString().split(",", 2)[0].trim();
//            String locAddress = editTextLocationAutocomplete.getText().toString().split(",", 2)[1].trim();
//            double locLong = Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[0].trim());
//            double locLat  = Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[1].trim());
//            int locRadius = radiusSeekBar.getProgress();

            count++;
        }
    }

    //handling of the edit/save button #1
    private void setEditButton() {
        //change save button to edit button
        saveButton.setText("Edit");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Toast.makeText(AddNewTaskActivity.this, "Editing started!", Toast.LENGTH_SHORT).show();
                currentMode = MODE_EDIT;
                enableControls();
                setEditSaveButton();
            }
        });
    }

    //handling of the edit/save button #2
    private void setEditSaveButton() {
        saveButton.setText("Save");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddNewTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();

                try {
                    updateTask();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                currentMode = MODE_VIEW;
                disableControls();
                setEditButton();
            }
        });
    }

    //updates a task in the db when the save button is pressed
    private void updateTask() throws ParseException {
        //task data
        Calendar calendar   = Calendar.getInstance();
        Date taskTimestamp  = calendar.getTime();

        String taskName = editTextTaskName.getText().toString();
        String taskDescription = editTextTaskDescription.getText().toString();
        String tag = categoryEditText.getText().toString();

//        String inputDate = datePickerEditText.getText().toString();
//        String inputTime = timePickerEditText.getText().toString();
//        String dateString;
//        if(inputDate.length() != 0 && inputTime.length() != 0){
//            dateString = inputDate + " " + inputTime + ":00";
//        } else if (inputDate.length() != 0 && inputTime.length() == 0) {
//            dateString = inputDate + " 12:00:00";
//        } else {
//            dateString = "1900-01-01 00:00:00";
//        }
//        Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
          Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
//        String locationName = this.currentPlace != null? this.currentPlace.getName().toString(): "";
//        String locationAddress = this.currentPlace != null? this.currentPlace.getAddress().toString(): "";
//
//        double longitude = useCurrentLocationSwitch.isChecked() ? lastKnownLocation != null ? lastKnownLocation.getLongitude() : 0 : this.currentPlace != null ? this.currentPlace.getLatLng().longitude : 0;
//        double latitude = useCurrentLocationSwitch.isChecked() ? lastKnownLocation != null ? lastKnownLocation.getLatitude() : 0 : this.currentPlace != null ? this.currentPlace.getLatLng().latitude : 0;
//        int radius = radiusSeekBar.getProgress();

        String locationName = selectedLocations.get(0).getLocationName();
        String locationAddress = selectedLocations.get(0).getLocationAddress();

        double longitude = selectedLocations.get(0).getLongitude();
        double latitude = selectedLocations.get(0).getLatitude();
        int radius = selectedLocations.get(0).getRadius();

//        String locationName = "";
//        String locationAddress = "";
//
//        double longitude = 0;
//        double latitude = 0;
//        int radius = 0;

        db.updateTask(  taskId, taskName, taskTimestamp,  taskDescription, tag, locationName, locationAddress, latitude, longitude, radius, dueDate, selectedLocations);

    }

    //disables all controls when in view mode
    private void disableControls() {
        editTextTaskName.setKeyListener(null);

        editTextTaskDescription.setKeyListener(null);

        categoryEditText.setKeyListener(null);


//        useCurrentLocationSwitch.setEnabled(false);

        editTextLocationAutocomplete.setKeyListener(null);
        editTextLocationAutocomplete.setOnClickListener(null);
        editTextLocationAutocomplete.setOnFocusChangeListener(null);

        radiusSeekBar.setEnabled(false);

//        datePickerEditText.setKeyListener(null);
//        datePickerEditText.setOnClickListener(null);
//        datePickerEditText.setOnFocusChangeListener(null);


        meterEditText.setKeyListener(null);

//        timePickerEditText.setKeyListener(null);
//        timePickerEditText.setOnClickListener(null);
//        timePickerEditText.setOnFocusChangeListener(null);

        buttonAddLocation.setVisibility(View.GONE);
    }

    //enables all controls when in edit (or add new) mode
    private void enableControls() {

        buttonAddLocation.setVisibility(View.VISIBLE);

        editTextTaskName.setKeyListener((KeyListener) editTextTaskName.getTag());

        editTextTaskDescription.setKeyListener((KeyListener) editTextTaskDescription.getTag());

        categoryEditText.setKeyListener((KeyListener) categoryEditText.getTag());

        editTextLocationAutocomplete.setKeyListener((KeyListener) editTextLocationAutocomplete.getTag());

//        datePickerEditText.setKeyListener((KeyListener) datePickerEditText.getTag());

        meterEditText.setKeyListener((KeyListener) meterEditText.getTag());

//        timePickerEditText.setKeyListener((KeyListener) timePickerEditText.getTag());

//        useCurrentLocationSwitch.setEnabled(true);
//        useCurrentLocationSwitch.setChecked(false);

        radiusSeekBar.setEnabled(true);

//         onchange listener to autocomplete edit text
        editTextLocationAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectFromMapButtonPressed(findViewById(android.R.id.content));
                }
            }
        });
        editTextLocationAutocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromMapButtonPressed(findViewById(android.R.id.content));
            }
        });

//        add onchange listener to switch for use current location
//        useCurrentLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    editTextLocationAutocomplete.setVisibility(View.GONE);
//                    orTextView.setVisibility(View.GONE);
//                    favouriteSpinner.setVisibility(View.GONE);
//                    or2TextView.setVisibility(View.GONE);
//
//                    //also set place for writing to the db to current position (use last known location)
//                    if(lastKnownLocation != null){
//                        double longtitude = lastKnownLocation.getLongitude();
//                        double latitiude = lastKnownLocation.getLatitude();
//
//                        textViewLngLtd.setText(latitiude + ", " + longtitude);
//                    } else {
//                        Toast.makeText(AddNewTaskActivity.this, "Currnent location could not be recieved!", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } else {
//                    editTextLocationAutocomplete.setVisibility(View.VISIBLE);
//                    editTextLocationAutocomplete.setText("");
//                    textViewLngLtd.setText("");
//                    orTextView.setVisibility(View.VISIBLE);
//                    favouriteSpinner.setSelection(0);
//                    favouriteSpinner.setVisibility(View.VISIBLE);
//                    or2TextView.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        //add change listener to radius slider
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                meterEditText.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //add change listener to meter input
        meterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    return;
                }

                int inputValue = Integer.parseInt(s.toString());

                if (inputValue > 5000) {
                    meterEditText.setText("" + 5000);
                    radiusSeekBar.setProgress(5000);
                } else {
                    radiusSeekBar.setProgress(inputValue);
                }

                meterEditText.setSelection(meterEditText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        //add date / time picker click listeners
//        datePickerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    datePicker();
//                }
//            }
//        });
//        datePickerEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datePicker();
//            }
//        });
//
//        //add date / time picker click listeners
//        timePickerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    timePicker();
//                }
//            }
//        });
//        timePickerEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timePicker();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null)
            googleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AddNewTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pc.com.geotasks/http/host/path")
        );
        AppIndex.AppIndexApi.start(googleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AddNewTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pc.com.geotasks/http/host/path")
        );
        AppIndex.AppIndexApi.end(googleApiClient, viewAction);

    }

    @Override
    public void onBackPressed() {
        exitButtonPressed(findViewById(android.R.id.content));
    }

    //opens the pick location popup
    public void openPopup(View view){
        dialog.setTitle("Select Place:");


        Button dialogButton = (Button) dialog.findViewById(R.id.buttonCancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                editTextLocationAutocomplete.setText("");
                textViewLngLtd.setText("");
                editTextFavourite.setText("");
                checkBoxFavourite.setChecked(false);
//                useCurrentLocationSwitch.setChecked(false);

                SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
                int defaultValue = 150;
                long defaultRadius = sharedPref.getInt("radiusDefault", defaultValue);
                radiusSeekBar.setProgress((int) defaultRadius);
                meterEditText.setText(defaultRadius+"");
                Spinner sItems = (Spinner) dialog.findViewById(R.id.spinnerFavourites);
                sItems.setSelection(0);

            }
        });

        Button okButton = (Button) dialog.findViewById(R.id.buttonOk);
        // if button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView locAddrName = (TextView) findViewById(R.id.location1AddrName);
                TextView locLongLat = (TextView) findViewById(R.id.location1LongLat);

                if(editTextLocationAutocomplete.getText().length() == 0 && textViewLngLtd.getText().length() == 0){
                    Toast.makeText(AddNewTaskActivity.this, "Please select a location first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(locAddrName.getText().length() == 0) {
                    locAddrName.setText(editTextLocationAutocomplete.getText());
                    locLongLat.setText("(" + radiusSeekBar.getProgress() + "m) " + textViewLngLtd.getText());
                } else {
                    LinearLayout locationHolderLayout = (LinearLayout) findViewById(R.id.locationHolderLayout);

//                    LinearLayout containerVertical = new LinearLayout(locationHolderLayout.getContext());
//                    locationHolderLayout.addView(containerVertical);
//                    containerVertical.setOrientation(LinearLayout.VERTICAL);
//                    LinearLayout.LayoutParams containerVParams = (LinearLayout.LayoutParams) containerVertical.getLayoutParams();
//                    containerVParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                    containerVParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    containerVertical.setLayoutParams(containerVParams);
//
//
//                    LinearLayout containerHorizontal = new LinearLayout(locationHolderLayout.getContext());
//                    containerVertical.addView(containerHorizontal);
//                    containerHorizontal.setOrientation(LinearLayout.HORIZONTAL);
//                    LinearLayout.LayoutParams containerHParams = (LinearLayout.LayoutParams) containerHorizontal.getLayoutParams();
//                    containerHParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                    containerHParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                    containerHorizontal.setLayoutParams(containerHParams);

                    TextView tv1 = new TextView(locationHolderLayout.getContext());
//                    containerHorizontal.addView(tv1);
                    locationHolderLayout.addView(tv1);
                    tv1.setText(editTextLocationAutocomplete.getText());
                    tv1.setTextAppearance(AddNewTaskActivity.this, android.R.style.TextAppearance_DeviceDefault_Medium);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv1.getLayoutParams();
                    params.setMargins(0, 25, 0, 0);
//                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                    params.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                    params.weight = 8;
                    tv1.setLayoutParams(params);


//                    Button delButton = new Button(locationHolderLayout.getContext());
//                    containerHorizontal.addView(delButton);
//                    LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) delButton.getLayoutParams();
//                    float pixelsWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
//                    float pixelsHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
//                    buttonParams.width = (int) pixelsWidth;
//                    buttonParams.height = (int) pixelsHeight;
//                    buttonParams.weight = 2;
//                    delButton.setText("DEL");
//                    containerHorizontal.setLayoutParams(buttonParams);

                    TextView tv2 = new TextView(locationHolderLayout.getContext());
//                    containerVertical.addView(tv2);
                    locationHolderLayout.addView(tv2);
//                    LinearLayout.LayoutParams paramstv2 = (LinearLayout.LayoutParams) tv1.getLayoutParams();
//                    paramstv2.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    paramstv2.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    tv2.setLayoutParams(paramstv2);
                    tv2.setText("(" + radiusSeekBar.getProgress() + "m) " + textViewLngLtd.getText());

                }

                String locName = editTextLocationAutocomplete.getText().toString().split(",", 2)[0].trim();
                String locAddress = editTextLocationAutocomplete.getText().toString().split(",", 2)[1].trim();
                double locLat  = Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[0].trim());
                double locLong = Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[1].trim());
                int locRadius = radiusSeekBar.getProgress();

                pc.com.geotasks.model.Location loc = new pc.com.geotasks.model.Location(locName, locAddress, locLat, locLong, locRadius);
                selectedLocations.add(loc);

                if(checkBoxFavourite.isChecked()){
                    Favourite newFav = new Favourite(editTextFavourite.getText().toString(),
                            editTextLocationAutocomplete.getText().toString().split(",", 2)[0].trim(),
                            editTextLocationAutocomplete.getText().toString().split(",", 2)[1].trim(),
                            Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[0].trim()),
                            Double.parseDouble(textViewLngLtd.getText().toString().split(",", 2)[1].trim()));

                    db.addFavourite(newFav);
                }

                dialog.dismiss();

                editTextLocationAutocomplete.setText("");
                textViewLngLtd.setText("");
                editTextFavourite.setText("");
                checkBoxFavourite.setChecked(false);
//                useCurrentLocationSwitch.setChecked(false);
                SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
                int defaultValue = 150;
                long defaultRadius = sharedPref.getInt("radiusDefault", defaultValue);
                radiusSeekBar.setProgress((int)defaultRadius);
                meterEditText.setText(defaultRadius+"");
                Spinner sItems = (Spinner) dialog.findViewById(R.id.spinnerFavourites);
                sItems.setSelection(0);

            }
        });

        ArrayList<Favourite> favs = db.getFavourites("");

        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.add("Select from favourites:");

        for (Favourite fav:favs) {
            spinnerArray.add(fav.getID() + " - " + fav.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) dialog.findViewById(R.id.spinnerFavourites);
        sItems.setAdapter(adapter);

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItemName = adapterView.getItemAtPosition(i).toString();
                if(selectedItemName != "Select from favourites:"){
                    ArrayList<Favourite> favs = db.getFavourites("");
                    for (Favourite fav : favs) {
                        int idFromDropdown = Integer.parseInt(selectedItemName.split("-")[0].trim());
                        if(fav.getID() == idFromDropdown){
                            editTextLocationAutocomplete.setText(fav.getLocationName() + ", " + fav.getLocationAddress());
                            textViewLngLtd.setText(fav.getLatitude() + ", " + fav.getLongitude());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
    }

    //handles the press of the button top left ("really?" dialog etc.)
    public void exitButtonPressed(View view){
        if(currentMode == MODE_NEW || currentMode == MODE_EDIT){
            //check if any data was entered
            if(anyDataEntered()){
                //prompt user if he really wants to exit
                exitPrompt();
            } else {
                //no data was entered, just exit this activity
                finish();
                ((CustomListView)TaskListFragment.mAdapter).update();
            }
        }  else {
            finish();
            if(TaskListFragment.mAdapter != null){
                ((CustomListView)TaskListFragment.mAdapter).update();
            }
        }

    }

    //saves a new task into the db
    public void saveButtonPressed(View view) throws ParseException {

        String taskName = editTextTaskName.getText().toString();
        String taskDescription = editTextTaskDescription.getText().toString();
        String tag = categoryEditText.getText().toString();

//        OLD LOGIC
//        String locationName = this.currentPlace != null? this.currentPlace.getName().toString(): "";
//        String locationAddress = this.currentPlace != null? this.currentPlace.getAddress().toString(): "";
//        double longitude = useCurrentLocationSwitch.isChecked() ? lastKnownLocation.getLongitude() : this.currentPlace != null ? this.currentPlace.getLatLng().longitude : 0;
//        double latitude = useCurrentLocationSwitch.isChecked() ? lastKnownLocation.getLatitude() : this.currentPlace != null ? this.currentPlace.getLatLng().latitude : 0;
//        int radius = radiusSeekBar.getProgress();

//      NEW LOGIC
        String locationName = "";
        String locationAddress = "";
        double longitude = 0;
        double latitude = 0;
        int radius = 0;

        if(selectedLocations != null && selectedLocations.size() != 0){
            locationName = selectedLocations.get(0).getLocationName();
            locationAddress = selectedLocations.get(0).getLocationName();
            longitude = selectedLocations.get(0).getLongitude();
            latitude = selectedLocations.get(0).getLatitude();
            radius = selectedLocations.get(0).getRadius();
        }


        //date logic
//        String inputDate = datePickerEditText.getText().toString();
//        String inputTime = timePickerEditText.getText().toString();
//        String dateString;
//        if(inputDate.length() != 0 && inputTime.length() != 0){
//            dateString = inputDate + " " + inputTime + ":00";
//        } else if (inputDate.length() != 0 && inputTime.length() == 0) {
//            dateString = inputDate + " 12:00:00";
//        } else {
//            dateString = "1900-01-01 00:00:00";
//        }
//        Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);"1900-01-01 00:00:00"
        Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1900-01-01 00:00:00");
        Task t = new Task(taskName, taskDescription, tag, locationName, locationAddress, latitude, longitude, radius, dueDate);

        if(selectedLocations != null && selectedLocations.size() != 0) {
            t.setLocations(selectedLocations);
        }

        db.addTask(t);

        ((CustomListView)TaskListFragment.mAdapter).update();

        finish();
    }

    //opens the google map picker fragment
    public void selectFromMapButtonPressed(View view){
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

        }  catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

    public void selectFromFavoritesButtonPressed(View view){
        Toast.makeText(this, "Favorite List Missing!", Toast.LENGTH_SHORT).show();
    }

    private void datePicker() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String monthString = (""+monthOfYear).length() == 1 ? "0" + monthOfYear : monthOfYear+"";
                String dayString = (""+dayOfMonth).length() == 1 ? "0" + dayOfMonth : dayOfMonth+"";

                datePickerEditText.setText(year + "-" + monthString + "-" + dayString);

            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);

        dpDialog.show();
    }

    private void timePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                String hourString = (""+hourOfDay).length() == 1 ? "0" + hourOfDay : hourOfDay+"";
                String minuteString = (""+minute).length() == 1 ? "0" + minute : minute+"";

                timePickerEditText.setText(hourString + ":" + minuteString);
            }

        };

        TimePickerDialog tpDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);

        tpDialog.show();
    }

    //"Really?" exit prompt
    private void exitPrompt() {
        AlertDialog exitDialog = new AlertDialog.Builder(AddNewTaskActivity.this).create();
        if(currentMode == MODE_NEW){
            exitDialog.setMessage("Do you really want to exit and discard this task?");
        } else if (currentMode == MODE_EDIT){
            exitDialog.setMessage("Do you really want to exit and discard the changes?");
        }

        exitDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                });
        exitDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay & Keep Editing",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        exitDialog.show();
    }

    //checks if any controls have entered data
    private boolean anyDataEntered() {
        boolean anyDataEnterd = false;

        if(textEditFilled(editTextTaskName)) {
            anyDataEnterd = true;
        }

        if(textEditFilled(editTextTaskDescription)) {
            anyDataEnterd = true;
        }

        if(textEditFilled(editTextLocationAutocomplete)) {
            anyDataEnterd = true;
        }

//        if(textEditFilled(datePickerEditText)) {
//            anyDataEnterd = true;
//        }
//
//        if(textEditFilled(timePickerEditText)) {
//            anyDataEnterd = true;
//        }

        if (textEditFilled(categoryEditText)) {
            anyDataEnterd = true;
        }

        return anyDataEnterd;
    }

    private boolean textEditFilled(EditText editText){
        return editText.getText().toString().trim().length() > 0 ? true : false;
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());

                this.currentPlace = place;

                String placeText = place.getName() + ", " + place.getAddress();

                editTextLocationAutocomplete.setText(placeText);
                textViewLngLtd.setText(place.getLatLng().latitude+ ", " + place.getLatLng().longitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("api",connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //sets up the gps service
    public void setUpGPSService() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //use last known location
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        if(lastKnownLocation != null) {
            handleLocation(lastKnownLocation);
        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "(addnewtaskactivity - Location changed");
                handleLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                //Toast.makeText(getBaseContext(), "addnewtaskactivity - Gps turned on", Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String provider) {
               // Toast.makeText(getBaseContext(), "addnewtaskactivity - Gps turned off", Toast.LENGTH_LONG).show();
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
        //Toast toast = Toast.makeText(AddNewTaskActivity.this, "lat: " + location.getLatitude() + "\nlong" + location.getLongitude(), Toast.LENGTH_LONG);
        //toast.show();

        lastKnownLocation = location;
    }
}
