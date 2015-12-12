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
    private View formElementsView;

    private String oldTask = null;
    private String oldPriority = null;
    private String newTask = null;
    private String priority = null;
    private String task = null;

    private EditText nameEditText;
    private EditText taskEditView;

    private int selectedId;

    private TextView taskTextView;
    private TextView staticPriorityTextView;
    private TextView priorityTextView;

    private RadioButton selectedRadioButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton highPriority;
    private RadioButton mediumPriority;
    private RadioButton lowPriority;

    private ImageButton editButton;
    private ImageButton updateButton;

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
                formElementsView = inflater.inflate(R.layout.dialog_form,
                        null, false);

                priorityRadioGroup = (RadioGroup) formElementsView
                        .findViewById(R.id.priorityRadioGroup);

                nameEditText = (EditText) formElementsView
                        .findViewById(R.id.taskEditText);

                new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                        .setTitle("Add a task")
                        .setMessage("What do you want to do?")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                task = nameEditText.getText().toString();

                                selectedId = priorityRadioGroup
                                        .getCheckedRadioButtonId();
                                selectedRadioButton = (RadioButton) formElementsView
                                        .findViewById(selectedId);

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
        taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        task = taskTextView.getText().toString();

        helper = new TaskHelper(MainActivity.this);
        helper.onDelete(task);

        updateUI();
    }

    public void onEditClick(View view) {
        View v = (View) view.getParent();

        taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        staticPriorityTextView = (TextView) v.findViewById(R.id.staticPriority);
        priorityTextView = (TextView) v.findViewById(R.id.priorityTextView);
        taskEditView = (EditText) v.findViewById(R.id.taskEditText);
        priorityRadioGroup = (RadioGroup) v.findViewById(R.id.priorityRadioGroup);

        highPriority = (RadioButton) v.findViewById(R.id.highRadioButton);
        mediumPriority = (RadioButton) v.findViewById(R.id.mediumRadioButton);
        lowPriority = (RadioButton) v.findViewById(R.id.lowRadioButton);

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

        priorityRadioGroup.setVisibility(View.VISIBLE);
        taskEditView.setText(oldTask, TextView.BufferType.EDITABLE);

        editButton = (ImageButton) v.findViewById(R.id.editButton);
        updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        editButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.VISIBLE);

    }

    public void onupdateClick(View view) {

        View v = (View) view.getParent();

        priorityRadioGroup = (RadioGroup) v.findViewById(R.id.priorityRadioGroup);
        selectedId = priorityRadioGroup
                .getCheckedRadioButtonId();
        selectedRadioButton = (RadioButton) v.findViewById(selectedId);

        priority = null;

        if (selectedRadioButton.getText().toString() != null)
            priority = selectedRadioButton.getText().toString();

        taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        taskEditView = (EditText) v.findViewById(R.id.taskEditText);

        editButton = (ImageButton) v.findViewById(R.id.editButton);
        updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        newTask = taskEditView.getText().toString();

        helper = new TaskHelper(MainActivity.this);
        helper.onUpdate(oldTask, newTask, priority);

        taskTextView.setVisibility(View.VISIBLE);
        taskEditView.setVisibility(View.GONE);

        editButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        updateUI();
    }
}
