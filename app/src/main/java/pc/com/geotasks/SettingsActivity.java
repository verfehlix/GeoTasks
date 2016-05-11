package pc.com.geotasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import pc.com.geotasks.database.SQLHelper;

/*
*  Settings activity that handles default radius settings, about info and deletion of favourites
* autor: faseitz
* */
public class SettingsActivity extends android.support.v4.app.Fragment {

    SQLHelper db;

    SeekBar seekBar2;
    EditText editText;
    Button saveButton;
    Button deleteFavsButton;

    public SettingsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        AppCompatActivity aca = (AppCompatActivity) getActivity();
        aca.getSupportActionBar().setTitle(R.string.action_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        // Inflate the layout for this fragment

        return view;
    }

    //set up controls and their functions
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.db = new SQLHelper(this.getActivity());

        seekBar2 = (SeekBar) getView().findViewById(R.id.seekBar2);
        editText = (EditText) getView().findViewById(R.id.editText);

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editText.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //add change listener to meter input
        editText.addTextChangedListener(new TextWatcher() {
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
                    editText.setText("" + 5000);
                    seekBar2.setProgress(5000);
                } else {
                    seekBar2.setProgress(inputValue);
                }

                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int defaultValue = 150;
        long defaultRadius = sharedPref.getInt("radiusDefault", defaultValue);

        seekBar2.setProgress((int) defaultRadius);
        editText.setText(defaultRadius+"");

        saveButton = (Button) this.getView().findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingsButtonPressed();
            }
        });

        deleteFavsButton = (Button) this.getView().findViewById(R.id.button2);
        deleteFavsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteFavsButtonPressed();
            }
        });
    }

    //write default radius to shared preferences
    public void onSettingsButtonPressed(){
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("radiusDefault", seekBar2.getProgress());
        editor.commit();

        int defaultValue = 150;
        long defaultRadius = sharedPref.getInt("radiusDefault", defaultValue);

        Toast.makeText(getActivity(), "Default Radius saved! \n Value: " + defaultRadius, Toast.LENGTH_SHORT).show();
    }

    //delete all favourites from db
    public void onDeleteFavsButtonPressed(){
        db.deleteAllFavourites();
        Toast.makeText(getActivity(), "Favourites successfully deleted!", Toast.LENGTH_SHORT).show();
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

}
