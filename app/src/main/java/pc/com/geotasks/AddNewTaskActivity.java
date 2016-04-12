package pc.com.geotasks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewTaskActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button exitButton;
    Button saveButton;
    EditText editTextTaskName;
    EditText editTextTaskDescription;

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
}
