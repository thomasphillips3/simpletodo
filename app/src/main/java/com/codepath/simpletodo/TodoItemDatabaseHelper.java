package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by briasullivan on 9/10/16.
 */
public class TodoItemDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "todoItemDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TODO_SQLite";

    // Table Names
    private static final String TABLE_TODOS = "todos";

    // Todo Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_DUE_DATE = "dueDate";
    private static final String KEY_TODO_URGENCY = "urgency";
    private static final String KEY_TODO_REPEAT = "repeat";
    private static final String KEY_TODO_STATUS = "status";

    private static TodoItemDatabaseHelper sInstance;

    public static synchronized TodoItemDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoItemDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODOS +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_TEXT + " TEXT," +
                KEY_TODO_DUE_DATE + " LONG," +
                KEY_TODO_URGENCY + " INTEGER," +
                KEY_TODO_REPEAT + " INTEGER," +
                KEY_TODO_STATUS + " INTEGER" +
                ")";
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
            onCreate(db);
        }
    }

    public long addOrUpdateTodoItem(TodoItem todoItem) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long todoItemId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, todoItem.itemText);
            values.put(KEY_TODO_DUE_DATE, todoItem.dueDate.getTime());
            values.put(KEY_TODO_URGENCY, todoItem.urgency.getUrgency());
            values.put(KEY_TODO_REPEAT, todoItem.repeat.getRepeat());
            values.put(KEY_TODO_STATUS, todoItem.status.getStatus());

            // First try to update the todo item in case the user already exists in the database
            // This assumes todo items are unique
            int rows =
                    db.update(
                            TABLE_TODOS,
                            values,
                            KEY_TODO_ID + "= ?",
                            new String[]{String.valueOf(todoItem.id)});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the todo item we just updated
                todoItemId = todoItem.id;
            } else {
                // user with this todoItem did not already exist, so insert new user
                todoItemId = db.insertOrThrow(TABLE_TODOS, null, values);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
        }
        db.close();
        return todoItemId;
    }

    public TodoItem getTodoItem(int id) {
        TodoItem todoItem = null;
        String TODOS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_TODOS, KEY_TODO_ID, String.valueOf(id));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if(cursor.moveToFirst()){
                todoItem = new TodoItem();
                todoItem.id = cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID));
                todoItem.itemText = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));
                Date dueDate = new Date();
                dueDate.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TODO_DUE_DATE)));
                todoItem.dueDate = dueDate;
                todoItem.urgency = TodoItem.Urgency.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_URGENCY))];
                todoItem.repeat = TodoItem.Repeat.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_REPEAT))];
                todoItem.status = TodoItem.Status.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_STATUS))];
            }
        } catch (Exception e){
            Log.d(TAG, "Error while trying to get posts from database");

        }
        return todoItem;
    }

    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todoItems = new ArrayList<>();

        // SELECT * FROM TODOS;
        String TODOS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TODOS);
        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    TodoItem todoItem = new TodoItem();
                    todoItem.id = cursor.getLong(cursor.getColumnIndex(KEY_TODO_ID));
                    todoItem.itemText = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));
                    Date dueDate = new Date();
                    dueDate.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TODO_DUE_DATE)));
                    todoItem.dueDate = dueDate;
                    todoItem.urgency = TodoItem.Urgency.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_URGENCY))];
                    todoItem.repeat = TodoItem.Repeat.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_REPEAT))];
                    todoItem.status = TodoItem.Status.values()[cursor.getInt(cursor.getColumnIndex(KEY_TODO_STATUS))];
                    todoItems.add(todoItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todoItems;
    }

    // Returns -1 if there is a database error.
    public int deleteTodoItem(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        int deletedRows = -1;
        try {
            deletedRows =
                    db.delete(
                            TABLE_TODOS, KEY_TODO_ID + "= ?",
                            new String[]{String.valueOf(itemId)});
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete user");
        } finally {
        }
        db.close();
        return deletedRows;
    }
}
