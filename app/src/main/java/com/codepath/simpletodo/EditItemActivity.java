package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

public class EditItemActivity extends AppCompatActivity {
    EditText etEditItem;
    DatePicker dueDatePicker;
    TimePicker dueTimePicker;
    Spinner urgencySpinner;
    Spinner repeatSpinner;
    TodoItem todoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etEditItem = (EditText) findViewById(R.id.etTodoItem);
        dueDatePicker = (DatePicker) findViewById(R.id.dueDatePicker);
        dueTimePicker = (TimePicker) findViewById(R.id.dueTimePicker);
        urgencySpinner = (Spinner) findViewById(R.id.urgencySpinner);
        ArrayAdapter<CharSequence> urgencyAdapter =
                ArrayAdapter.createFromResource(this, R.array.urgency_array, android.R.layout.simple_spinner_item);
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urgencySpinner.setAdapter(urgencyAdapter);
        repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
        ArrayAdapter<CharSequence> repeatAdapter =
                ArrayAdapter.createFromResource(this, R.array.repeat_array, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(repeatAdapter);

        todoItem = new TodoItem();
        todoItem.status = TodoItem.Status.TODO;

        // Get Extras
        Bundle extras = getIntent().getExtras();
        if (extras != null && !extras.isEmpty()) {
            setup(extras);
        }
    }

    private void setup(Bundle extras) {
        todoItem.id = extras.getLong("id");
        todoItem.itemText = extras.getString("text");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(extras.getLong("dueDate"));
        todoItem.dueDate = calendar.getTime();
        todoItem.urgency = TodoItem.Urgency.values()[extras.getInt("urgency")];
        todoItem.repeat = TodoItem.Repeat.values()[extras.getInt("repeat")];
        todoItem.status = TodoItem.Status.values()[extras.getInt("status")];

        etEditItem.setText(todoItem.itemText);
        etEditItem.setSelection(todoItem.itemText.length());
        dueDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dueTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            dueTimePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            dueTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            dueTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
        urgencySpinner.setSelection(todoItem.urgency.getUrgency());
        repeatSpinner.setSelection(todoItem.repeat.getRepeat());
    }
    public void saveItem(View v) {
        todoItem.itemText = etEditItem.getText().toString();

        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(dueDatePicker.getYear(), dueDatePicker.getMonth(), dueDatePicker.getDayOfMonth(), dueTimePicker.getHour(), dueTimePicker.getMinute());
        } else {
            calendar.set(dueDatePicker.getYear(), dueDatePicker.getMonth(), dueDatePicker.getDayOfMonth(), dueTimePicker.getCurrentHour(), dueTimePicker.getCurrentMinute());
        }
        todoItem.dueDate = calendar.getTime();

        // todoItem.repeat = TodoItem.Repeat.values()[repeatSpinner.getSelectedItemPosition()];
        todoItem.repeat = TodoItem.Repeat.NO_REPEAT;
        todoItem.urgency = TodoItem.Urgency.values()[urgencySpinner.getSelectedItemPosition()];
        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        todoItem.id = databaseHelper.addOrUpdateTodoItem(todoItem);

        Intent i = new Intent();
        i.putExtra("id", todoItem.id);
        i.putExtra("text", todoItem.itemText);
        i.putExtra("dueDate", todoItem.dueDate.getTime());
        i.putExtra("repeat", todoItem.repeat.getRepeat());
        i.putExtra("urgency", todoItem.urgency.getUrgency());
        i.putExtra("status", todoItem.status.getStatus());
        setResult(RESULT_OK, i);
        finish();
    }
}
