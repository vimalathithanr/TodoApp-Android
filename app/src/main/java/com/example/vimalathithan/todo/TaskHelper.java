package com.example.vimalathithan.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vimalathithan on 11/23/2015.
 */
public class TaskHelper extends SQLiteOpenHelper {

    public TaskHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlQuery =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT," + "%s TEXT)", TaskContract.TABLE,
                        TaskContract.Columns.TASK, TaskContract.Columns.PRIORITY);

        Log.d("TaskDBHelper", "Query to form table: " + sqlQuery);
        sqlDB.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        sqlDB.execSQL("DROP TABLE IF EXISTS " + TaskContract.TABLE);
        onCreate(sqlDB);
    }

    public void onInsert(String task, String priority){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.clear();
        values.put(TaskContract.Columns.TASK, task);
        values.put(TaskContract.Columns.PRIORITY, priority);
        db.insertWithOnConflict(TaskContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void onDelete(String task){
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);

        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public void onUpdate(String oldTask, String newTask, String priority){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.clear();
        values.put(TaskContract.Columns.TASK, newTask);
        values.put(TaskContract.Columns.PRIORITY, priority);

        String[] args = new String[]{oldTask};

        db.update(TaskContract.TABLE, values, "task=?", args);
        db.close();
    }
}
