package com.example.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity{
    EditText editTextEditItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Intent intent = getIntent();
        String itemText = intent.getStringExtra(MainActivity.ITEMTEXT);
        editTextEditItem = (EditText) findViewById(R.id.editTextEditItem);
        editTextEditItem.setText(itemText);
    }

    public void onClickSave(View view) {
        Intent intent = new Intent();
        String itemText = editTextEditItem.getText().toString().trim();
        if (itemText.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter valid text to add", Toast.LENGTH_SHORT).show();
        }else {
            intent.putExtra("ITEMTEXTNEW", itemText);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }
}
