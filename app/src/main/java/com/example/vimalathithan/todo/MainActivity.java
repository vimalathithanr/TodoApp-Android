package com.example.vimalathithan.todo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private TaskHelper helper;
    private ListView mainListView;
    private boolean normalFlow = true;
    private String oldTask = null;
    private String oldPriority = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_task:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.dialog_form,
                        null, false);

                final RadioGroup priorityRadioGroup = (RadioGroup) formElementsView
                        .findViewById(R.id.priorityRadioGroup);

                final EditText nameEditText = (EditText) formElementsView
                        .findViewById(R.id.taskEditText);

                new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                        .setTitle("Add a task")
                        .setMessage("What do you want to do?")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String task = nameEditText.getText().toString();

                                int selectedId = priorityRadioGroup
                                        .getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = (RadioButton) formElementsView
                                        .findViewById(selectedId);


                                String priority = null;

                                if (selectedRadioButton.getText().toString() != null)
                                    priority = selectedRadioButton.getText().toString();

                                helper = new TaskHelper(MainActivity.this);
                                helper.onInsert(task, priority);
                                updateUI();
                            }

                        }).setNegativeButton("Cancel", null).show();

                return true;

            default:
                return false;
        }
    }

    private void updateUI() {
        helper = new TaskHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.PRIORITY},
                null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[]{TaskContract.Columns.TASK, TaskContract.Columns.PRIORITY},
                new int[]{R.id.taskTextView, R.id.priorityTextView},
                0
        );

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(listAdapter);
    }

    public void onDeleteClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        String task = taskTextView.getText().toString();

        helper = new TaskHelper(MainActivity.this);
        helper.onDelete(task);

        updateUI();
    }

    public void onEditClick(View view) {
        View v = (View) view.getParent();

        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        TextView staticPriorityTextView = (TextView) v.findViewById(R.id.staticPriority);
        TextView priorityTextView = (TextView) v.findViewById(R.id.priorityTextView);
        EditText taskEditView = (EditText) v.findViewById(R.id.taskEditText);
        RadioGroup taskRadiogroup = (RadioGroup) v.findViewById(R.id.priorityRadioGroup);

        RadioButton highPriority = (RadioButton) v.findViewById(R.id.highRadioButton);
        RadioButton mediumPriority = (RadioButton) v.findViewById(R.id.mediumRadioButton);
        RadioButton lowPriority = (RadioButton) v.findViewById(R.id.lowRadioButton);

        oldPriority = priorityTextView.getText().toString();

        if (oldPriority.contentEquals("High"))
            highPriority.setChecked(true);
        else if (oldPriority.contentEquals("Medium"))
            mediumPriority.setChecked(true);
        else if (oldPriority.contentEquals("Low"))
            lowPriority.setChecked(true);

        oldTask = taskTextView.getText().toString();

        staticPriorityTextView.setVisibility(View.GONE);
        priorityTextView.setVisibility(View.GONE);

        taskTextView.setVisibility(View.GONE);
        taskEditView.setVisibility(View.VISIBLE);
        taskRadiogroup.setVisibility(View.VISIBLE);
        taskEditView.setText(oldTask, TextView.BufferType.EDITABLE);

        ImageButton editButton = (ImageButton) v.findViewById(R.id.editButton);
        ImageButton updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        editButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.VISIBLE);

    }

    public void onupdateClick(View view) {

        View v = (View) view.getParent();

        final RadioGroup priorityRadioGroup = (RadioGroup) v.findViewById(R.id.priorityRadioGroup);
        int selectedId = priorityRadioGroup
                .getCheckedRadioButtonId();
        RadioButton selectedRadioButton = (RadioButton) v.findViewById(selectedId);

        String priority = null;

        if (selectedRadioButton.getText().toString() != null)
            priority = selectedRadioButton.getText().toString();

        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        EditText taskEditView = (EditText) v.findViewById(R.id.taskEditText);

        ImageButton editButton = (ImageButton) v.findViewById(R.id.editButton);
        ImageButton updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        String newTask = taskEditView.getText().toString();

        helper = new TaskHelper(MainActivity.this);
        helper.onUpdate(oldTask, newTask, priority);

        taskTextView.setVisibility(View.VISIBLE);
        taskEditView.setVisibility(View.GONE);

        editButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        updateUI();
    }
}
