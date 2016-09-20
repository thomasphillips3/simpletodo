package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TodoDisplayActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 30;
    TodoItem todoItem;
    TextView todoText;
    TextView dueDateText;
    TextView dueTimeText;
    TextView repeatText;
    TextView urgencyText;
    Switch statusSwitch;
    TodoItemDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_display);
        todoText = (TextView) findViewById(R.id.tvTodoText);
        dueDateText = (TextView) findViewById(R.id.tvDueDateText);
        dueTimeText = (TextView) findViewById(R.id.tvDueTimeText);
        repeatText = (TextView) findViewById(R.id.tvRepeatText);
        urgencyText = (TextView) findViewById(R.id.tvUrgencyText);
        statusSwitch = (Switch) findViewById(R.id.statusSwitch);
        databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        todoItem = new TodoItem();
        // Setting textviews to intent extras
        Bundle todoBundle = getIntent().getExtras();
        updateView(todoBundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.todomenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deleteItem:
                deleteItem();
                break;
            case R.id.action_editItem:
                editItem();
                break;
            default:
                break;
        }
        return true;
    }

    private void updateView(Bundle extras) {
        todoItem.id = extras.getLong("id");
        todoItem.itemText = extras.getString("text");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(extras.getLong("dueDate"));
        todoItem.dueDate = calendar.getTime();
        todoItem.urgency = TodoItem.Urgency.values()[extras.getInt("urgency")];
        todoItem.repeat = TodoItem.Repeat.values()[extras.getInt("repeat")];
        todoItem.status = TodoItem.Status.values()[extras.getInt("status")];

        todoText.setText(todoItem.itemText);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        dueDateText.setText(dateFormat.format(todoItem.dueDate));
        dueTimeText.setText(timeFormat.format(todoItem.dueDate));
        int repeat = todoItem.repeat.getRepeat();
        int urgency = todoItem.urgency.getUrgency();
        int status = todoItem.status.getStatus();
        repeatText.setText(getResources().getStringArray(R.array.repeat_array)[repeat]);
        urgencyText.setText(getResources().getStringArray(R.array.urgency_array)[urgency]);
        statusSwitch.setChecked(TodoItem.Status.values()[status] == TodoItem.Status.DONE);
        setupSwitchListener();
    }

    private void setupSwitchListener() {
        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todoItem.status = (isChecked)? TodoItem.Status.DONE : TodoItem.Status.TODO;
                databaseHelper.addOrUpdateTodoItem(todoItem);
                if (isChecked) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void deleteItem() {
        int result = databaseHelper.deleteTodoItem(todoItem.id);
        if (result == -1) {
            Toast.makeText(this, "Could not delete this task", Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void editItem() {
        Intent i = new Intent(TodoDisplayActivity.this, EditItemActivity.class);
        i.putExtras(getIntent().getExtras());
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Edited Item", Toast.LENGTH_SHORT).show();
            updateView(data.getExtras());
        }
    }
}
