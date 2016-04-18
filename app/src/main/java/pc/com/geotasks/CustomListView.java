package pc.com.geotasks;

/**
 * Created by Stefan on 10.01.2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import pc.com.geotasks.model.Task;

public class CustomListView extends ArrayAdapter<Task>{

    private final Activity context;
    private final ArrayList<Task> files;
    private View rowView;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private int highlight = Color.TRANSPARENT;


    /**
     * Constructor.
     * @param context context activity
     * @param files ArrayList<String> with the items that are added to the taskList
     */
    public CustomListView(Activity context, ArrayList<Task> files) {
        super(context, R.layout.task_list_item, files);
        this.context = context;
        this.files = files;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //layout of a single taskList item
        rowView= inflater.inflate(R.layout.task_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView txtSubTitle = (TextView) rowView.findViewById(R.id.subtxt);
        final GridLayout taskListItem = (GridLayout)rowView.findViewById(R.id.task_list_itm);

        //set title text to the String in the ArrayList at position "position"
        txtTitle.setText(files.get(position).getName());
        txtSubTitle.setText(files.get(position).getLocationName() + files.get(position).getLocationAddress());

        taskListItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //store temporary backgroud color of list item
                        Drawable background = taskListItem.getBackground();
                        if (background instanceof ColorDrawable) {
                            highlight = ((ColorDrawable) background).getColor();
                        }

                        //highlight list item
                        taskListItem.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight));

                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //set list item color to the default color
                        taskListItem.setBackgroundColor(highlight);

                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {

                            //--------------------------click-recognized----------------------------

                            //----------------------------------------------------------------------
                        }
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        //set list item color to the default color
                        taskListItem.setBackgroundColor(highlight);
                    }
                }
                return false;
            }
        });

        taskListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrate(10);
                createDeleteDialog(position);
                return false;
            }
        });

        //set the date
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date date =  new Date();
//        txtSubTitle.setText("  Added: " + dateFormat.format(date));
//        txtSubTitle.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

        //create delete icon and adds onClickListener to it
        ImageView deleteIcon = (ImageView) rowView.findViewById(R.id.task_icon_to_describtion);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure you want to Delete?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                remove(files.get(position));
                                //delete from database
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

               /* if(files.size() == 0){
                    context.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }*/
            }
        });

        return rowView;
    }

    /**
     * creates a dialog that asks if the user really wants to delete the task
     * @param index in the list, the user clicked at
     */
    public void createDeleteDialog(final int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //set text
        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_title);
        // Add the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                files.remove(index);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * performs a vibrate
     *
     * @param millis  milliseconds to vibrate
     */
    private void vibrate(int millis) {
        Vibrator vibr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        vibr.vibrate(millis);
    }
}
