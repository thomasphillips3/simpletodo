package com.codepath.simpletodo;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by briasullivan on 9/12/16.
 */
public class TodoItemAdapter extends ArrayAdapter<TodoItem> {
    private static final HashMap<TodoItem.Urgency, Integer> urgencyColorMap = new HashMap<>();
    static {
        urgencyColorMap.put(TodoItem.Urgency.HIGH, R.color.colorHigh);
        urgencyColorMap.put(TodoItem.Urgency.MEDIUM, R.color.colorMedium);
        urgencyColorMap.put(TodoItem.Urgency.LOW, R.color.colorLow);
    }

    public TodoItemAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoItem todoItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        TextView todoLabel = (TextView) convertView.findViewById(R.id.tvTodo);
        TextView tvUrgency = (TextView) convertView.findViewById(R.id.urgencyTextView);
        todoLabel.setText(todoItem.itemText);
        if (todoItem.status == TodoItem.Status.DONE) {
            tvUrgency.setText("DONE");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvUrgency.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray, getContext().getTheme()));
                todoLabel.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray, getContext().getTheme()));
            } else {
                tvUrgency.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
                todoLabel.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
            }
        } else {
            tvUrgency.setText(getContext().getResources().getStringArray(R.array.urgency_array)[todoItem.urgency.getUrgency()]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                todoLabel.setTextColor(getContext().getResources().getColor(android.R.color.black, getContext().getTheme()));
                tvUrgency.setTextColor(getContext().getResources().getColor(urgencyColorMap.get(todoItem.urgency), getContext().getTheme()));
            } else {
                tvUrgency.setTextColor(getContext().getResources().getColor(urgencyColorMap.get(todoItem.urgency)));
                todoLabel.setTextColor(getContext().getResources().getColor(android.R.color.black));
            }
        }
        return  convertView;
    }
}
