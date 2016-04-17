package pc.com.geotasks;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.w3c.dom.Text;

public class AddNewTaskActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    Toolbar toolbar;
    Button exitButton;
    Button saveButton;
    EditText editTextTaskName;
    EditText editTextTaskDescription;
    EditText editTextLocationAutocomplete;
    EditText meterEditText;
    TextView textViewLngLtd;
    TextView orTextView;
    Switch useCurrentLocationSwitch;
    SeekBar radiusSeekBar;

    GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final String TAG = TaskListFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //hide the title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //get buttons
        exitButton = (Button) findViewById(R.id.exitButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        //get inputs
        editTextTaskName = (EditText) findViewById(R.id.editTextTaskName);
        editTextTaskDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        editTextLocationAutocomplete = (EditText) findViewById(R.id.editTextLocationAutocomplete);

        //get text views
        textViewLngLtd = (TextView) findViewById(R.id.textViewLngLtd);
        orTextView = (TextView) findViewById(R.id.orTextView);
        meterEditText = (EditText) findViewById(R.id.meterEditText);

        //get switch
        useCurrentLocationSwitch = (Switch) findViewById(R.id.useCurrentLocationSwitch);

        //get radius seekbar
        radiusSeekBar = (SeekBar) findViewById(R.id.radiusSeekBar);

        //add onchange listener to autocomplete edit text
        editTextLocationAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    findPlace(findViewById(android.R.id.content));
                }
            }
        });

        //add onchange listener to switch for use current location
        useCurrentLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTextLocationAutocomplete.setVisibility(View.INVISIBLE);
                    orTextView.setVisibility(View.INVISIBLE);
                } else {
                    editTextLocationAutocomplete.setVisibility(View.VISIBLE);
                    orTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        //add change listener to radius slider
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                meterEditText.setText(""+progress);
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
                if(s.length() == 0){
                    return;
                }

                int inputValue = Integer.parseInt(s.toString());

                if(inputValue > 5000){
                    meterEditText.setText(""+5000);
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


        //init google maps api component
        googleApiClient = new GoogleApiClient
            .Builder(this)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if( googleApiClient != null )
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( googleApiClient != null && googleApiClient.isConnected() ) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        exitButtonPressed(findViewById(android.R.id.content));
    }

    public void exitButtonPressed(View view){
        //check if any data was entered
        if(anyDataEntered()){
            //prompt user if he really wants to exit
            exitPrompt();
        } else {
            //no data was entered, just exit this activity
            finish();
        }
    }

    private void exitPrompt() {
        AlertDialog exitDialog = new AlertDialog.Builder(AddNewTaskActivity.this).create();
        exitDialog.setMessage("Do you really want to exit and discard this task?");
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

    private boolean anyDataEntered() {
        boolean anyDataEnterd = false;

        if(textEditFilled(editTextTaskName)) {
            anyDataEnterd = true;
        }

        if(textEditFilled(editTextTaskDescription)) {
            anyDataEnterd = true;
        }

        return anyDataEnterd;
    }

    private boolean textEditFilled(EditText editText){
        return editText.getText().toString().trim().length() > 0 ? true : false;
    }



    public void findPlace(View view) {
        try {

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);




        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());

                String placeText = place.getName() + ", " + place.getAddress();

                editTextLocationAutocomplete.setText(placeText);
                textViewLngLtd.setText(place.getLatLng().longitude + ", " + place.getLatLng().latitude);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

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
}
