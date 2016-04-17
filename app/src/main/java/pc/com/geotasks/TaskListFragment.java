package pc.com.geotasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pc.com.geotasks.model.Task;

/**
 * Created by Stefan
 */

/**
 * A fragment representing a taskList of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TaskListFragment extends Fragment implements AbsListView.OnItemClickListener {

    public static final String TAG = TaskListFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public static ArrayList<Task> taskList = new ArrayList<Task>();
    /**
     * The fragment's ListView/GridView.
     */
    private static AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;
    private static View view;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date tmpDate = new Date();
        tmpDate.setTime(Calendar.getInstance().getTimeInMillis());
        Task task1 = new Task("Einkaufen","", "", "Rewe City", "N1, Mannheim", 0, 0, 0, tmpDate);
        Task task2 = new Task("Team Meeting","", "", "University of Mannheim", "Mannheim", 0, 0, 0, tmpDate);
        Task task3 = new Task("App","", "", "Zuhause", "L14,18 Mannheim", 0, 0, 0, tmpDate);

//        ListObject task1 = new ListObject("Einkaufen", "Rewe City, N 1, Mannheim");
//        ListObject task2 = new ListObject("Team Meeting", "University Mannheim");
//        ListObject task3 = new ListObject("App programmieren","Zuhause");

        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);

        // TODO: Change Adapter to display your content
        //taskList = MainActivity.db.getTasks("");
        mAdapter = new CustomListView(getActivity(), taskList);

        //set title of ActionBar
        AppCompatActivity aca = (AppCompatActivity) getActivity();
        ActionBar actionBar = aca.getSupportActionBar();
        actionBar.setTitle(R.string.task_input);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task_list, container, false);

        view.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        final EditText searchBar = (EditText)view.findViewById(R.id.search);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchBar.getText().toString();
                taskList.clear();
                taskList = MainActivity.db.getTasks(searchText);
//                for debug
//                Date tmpDate = new Date();
//                tmpDate.setTime(Calendar.getInstance().getTimeInMillis());
//                Task task1 = new Task("Einkauf","", "Rewe City", "N1, Mannheim", 0, 0, 0, tmpDate);
//
//                taskList.add(task1);
                ((CustomListView)mAdapter).update();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    /**
     * updates the taskList of activity logs
     * @param mAdapter
     */
    public static void addListItem(ListAdapter mAdapter){
        view.findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the taskList is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
        public void onFragmentInteraction(String id);
    }

    public ArrayList<Task> getTaskList(){
        return taskList;
    }

}
