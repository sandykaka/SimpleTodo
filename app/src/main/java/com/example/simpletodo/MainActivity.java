package com.example.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String ITEMTEXT = "";
    private static final int ITEMTEXT_REQUEST = 1;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView listViewItems;
    EditText editTextNewItem;
    int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readItems();
        //items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listViewItems = (ListView) findViewById(R.id.listViewItems);
        listViewItems.setAdapter(itemsAdapter);
        //items.add("First Item");
        //items.add("Second Item");
        setupListViewListener();
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch (IOException e){
            items = new ArrayList<String>();
        }
    }

    private void setupListViewListener() {
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditItemActivity.class);
                String itemText = parent.getItemAtPosition(position).toString();
                itemPosition = position;
                Toast.makeText(getApplicationContext(),String.valueOf(itemPosition),Toast.LENGTH_SHORT).show();
                intent.putExtra(ITEMTEXT, itemText);
                startActivityForResult(intent, ITEMTEXT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ITEMTEXT_REQUEST){
            if (resultCode == RESULT_OK){
                String textNew = data.getStringExtra("ITEMTEXTNEW");
                Toast.makeText(getApplicationContext(), textNew, Toast.LENGTH_SHORT).show();
                String itemText = itemsAdapter.getItem(itemPosition);
                itemsAdapter.remove(itemText);
                itemsAdapter.insert(textNew, itemPosition);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickAddItem(View view) {
        editTextNewItem = (EditText) findViewById(R.id.editTextNewItem);
        String itemText = editTextNewItem.getText().toString().trim();
        if (itemText.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter valid text to add",Toast.LENGTH_SHORT).show();
        }else {
            itemsAdapter.add(itemText);
            editTextNewItem.setText("");
            writeItems();
        }
    }
}
