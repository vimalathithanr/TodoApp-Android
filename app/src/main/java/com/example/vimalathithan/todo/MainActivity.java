package com.example.vimalathithan.todo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private TaskHelper helper;
    private ListView mainListView;
    private boolean normalFlow = true;
    private String oldTask = null;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a task");
                builder.setMessage("What do you want to do?");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = inputField.getText().toString();

                        helper = new TaskHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(TaskContract.Columns.TASK,task);

                        db.insertWithOnConflict(TaskContract.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.create().show();
                return true;

            default:
                return false;
        }
    }

    private void updateUI() {
        helper = new TaskHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK},
                null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[]{TaskContract.Columns.TASK},
                new int[]{R.id.taskTextView},
                0
        );

        ListView lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(listAdapter);
    }

    public void onDeleteClick (View view){
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        String task = taskTextView.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);


        helper = new TaskHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }

    public void onEditClick (View view) {
        View v = (View) view.getParent();

        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        EditText taskEditView = (EditText) v.findViewById(R.id.taskEditText);

        oldTask = taskTextView.getText().toString();

        taskTextView.setVisibility(View.GONE);
        taskEditView.setVisibility(View.VISIBLE);
        taskEditView.setText(oldTask, TextView.BufferType.EDITABLE);

        ImageButton editButton = (ImageButton) v.findViewById(R.id.editButton);
        ImageButton updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        editButton.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.VISIBLE);

    }

    public void onupdateClick(View view){

        View v = (View) view.getParent();

        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        EditText taskEditView = (EditText) v.findViewById(R.id.taskEditText);

        ImageButton editButton = (ImageButton) v.findViewById(R.id.editButton);
        ImageButton updateButton = (ImageButton) v.findViewById(R.id.updateButton);

        String newTask = taskEditView.getText().toString();

        helper = new TaskHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.clear();
        values.put(TaskContract.Columns.TASK, newTask);

        String[] args = new String[]{oldTask};

        db.update(TaskContract.TABLE, values, "task=?", args);

        taskTextView.setVisibility(View.VISIBLE);
        taskEditView.setVisibility(View.GONE);

        editButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
        updateUI();
    }
}
