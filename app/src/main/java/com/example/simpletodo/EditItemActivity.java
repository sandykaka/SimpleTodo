package com.example.simpletodo;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText editTextEditItem;
    Spinner spinnerPriority;
    TextView textViewDate;
    ArrayList<String> itemDetails, returnItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Intent intent = getIntent();
        itemDetails = intent.getStringArrayListExtra("ITEMDETAILS");
        editTextEditItem = (EditText) findViewById(R.id.editTextEditItem);
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerPriority, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
        spinnerPriority.setOnItemSelectedListener(this);
        textViewDate = (TextView) findViewById(R.id.textViewDate);

        try {
            editTextEditItem.setText(itemDetails.get(0));
            String priority = itemDetails.get(1);
            spinnerPriority.setSelection(1);
            if (priority != null) {
                int spinnerPosition = adapter.getPosition(priority);
                spinnerPriority.setSelection(spinnerPosition);
            }
            textViewDate.setText(itemDetails.get(2));
        } catch (Exception e) {

        }

    }

    public void onClickSave(View view) {
        Intent intent = new Intent();
        String itemText = editTextEditItem.getText().toString().trim();
        //String itemPriority = editTextEditPriority.getText().toString().trim();
        String itemDate = textViewDate.getText().toString().trim();
        if (itemText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter valid text to add", Toast.LENGTH_SHORT).show();
        } else {
            returnItem = new ArrayList<>();
            returnItem.add(itemText);
            String priority = spinnerPriority.getSelectedItem().toString();
            returnItem.add(priority);
            returnItem.add(itemDate);
            intent.putStringArrayListExtra("ITEMNEW", returnItem);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datepicker");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPriority.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
