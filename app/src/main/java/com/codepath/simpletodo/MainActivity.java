package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 20;
    ArrayList<TodoItem> items;
    TodoItemAdapter itemsAdapter;
    ListView lvItems;
    TodoItemDatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        items = new ArrayList<>(databaseHelper.getAllTodoItems());
        itemsAdapter = new TodoItemAdapter(this, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);
        refreshItems();
        setupListViewListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addItem:
                addItem();
                break;
            default:
                break;
        }
        return true;
    }

    private void setupListViewListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, TodoDisplayActivity.class);
                TodoItem todoItem = itemsAdapter.getItem(position);
                i.putExtra("id", todoItem.id);
                i.putExtra("text", todoItem.itemText);
                i.putExtra("dueDate", todoItem.dueDate.getTime());
                i.putExtra("repeat", todoItem.repeat.getRepeat());
                i.putExtra("urgency", todoItem.urgency.getUrgency());
                i.putExtra("status", todoItem.status.getStatus());
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("ADDED", "Item was added to list of tasks");
        }
    }

    public void addItem() {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    private void refreshItems() {
        itemsAdapter.clear();
        List<TodoItem> todoItems = databaseHelper.getAllTodoItems();
        final Calendar currentCalendar = Calendar.getInstance();
            Collections.sort(todoItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem t1, TodoItem t2) {
                if (t1.status == TodoItem.Status.DONE || t2.status == TodoItem.Status.DONE) {
                    return t1.status.getStatus() - t2.status.getStatus();
                }
                if (t1.dueDate.compareTo(currentCalendar.getTime()) < 0) {
                    return -100;
                }
                return t1.urgency.getUrgency() - t2.urgency.getUrgency();
            }
        });
        itemsAdapter.addAll(todoItems);
    }
}
