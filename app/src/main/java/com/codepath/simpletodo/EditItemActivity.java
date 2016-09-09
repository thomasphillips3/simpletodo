package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    EditText etEditItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(getIntent().getExtras().getString("itemValue"));
        etEditItem.setSelection(etEditItem.getText().length());
    }

    public void saveItem(View v) {
        Intent data = new Intent();
        Log.d("SAVE", "saved");
        data.putExtra("itemValue", etEditItem.getText());
        data.putExtra("itemPosition", getIntent().getExtras().getInt("itemPosition"));
        setResult(RESULT_OK, data);
        finish();
    }
}
