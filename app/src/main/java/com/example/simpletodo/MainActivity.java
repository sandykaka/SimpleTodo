package com.example.simpletodo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ITEMTEXT_REQUEST = 1;
    private static final int ITEMNEW_REQUEST = 2;
    ListView listViewItems;
    int itemPosition;
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    CustomAdapter customAdapter;
    ItemsList itemsList;
    List<ItemsList> itemsListList;
    String itemToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemsListList = new ArrayList<>();
        mDbHelper = new FeedReaderDbHelper(getApplicationContext());
        readItems();
        listViewItems = (ListView) findViewById(R.id.listViewItems);
        customAdapter = new CustomAdapter(this, R.layout.activity_custom_adapter, itemsListList) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textViewPriority = (TextView) view.findViewById(R.id.priority);
                String priority = textViewPriority.getText().toString();
                if (priority.equals("High")) {
                    textViewPriority.setTextColor(Color.RED);
                } else if (priority.equals("Medium")) {
                    textViewPriority.setTextColor(Color.BLUE);
                } else {
                    textViewPriority.setTextColor(Color.YELLOW);
                }
                return view;
            }
        };

        listViewItems.setAdapter(customAdapter);
        setupListViewListener();
    }

    private void readItems() {
        db = mDbHelper.getReadableDatabase();
        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PRIORITY,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DUEDATE,
        };

        String sortOrder = "(case when priority = \"High\" then 1 when priority=\"Medium\" then 2 else 3 end), priority";
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String itemText = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM));
            String priority = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_PRIORITY));
            String dueDate = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_DUEDATE));
            itemsList = new ItemsList(itemText, priority, dueDate);
            Log.d("items", String.valueOf(itemsList));
            itemsListList.add(itemsList);
            cursor.moveToNext();
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

    }

    private void deleteItems(String itemText) {
        db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {itemText};
        // Issue SQL statement.
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);

    }

    private void updateItems(ItemsList itemsList, String itemText) {
        db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM, itemsList.getItems());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PRIORITY, itemsList.getPriority());

        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM + " LIKE ?";
        String[] selectionArgs = {itemText};

        int count = db.update(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    private void writeItems(String itemText, String priority, String dueDate) {
        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ITEM, itemText);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PRIORITY, priority);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DUEDATE, dueDate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NULLABLE,
                values);
    }

    private void setupListViewListener() {
        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                TextView text = (TextView) view.findViewById(R.id.item);
                String itemText = text.getText().toString();
                alertWindow(itemText, position);
                return true;
            }
        });
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditItemActivity.class);
                TextView text = (TextView) view.findViewById(R.id.item);
                TextView priority = (TextView) view.findViewById(R.id.priority);
                itemToUpdate = text.getText().toString();
                String itemPriority = priority.getText().toString();
                itemPosition = position;
                Toast.makeText(getApplicationContext(), String.valueOf(itemPosition), Toast.LENGTH_SHORT).show();
                ArrayList<String> itemDetails = new ArrayList<String>();
                itemDetails.add(itemToUpdate);
                itemDetails.add(itemPriority);
                itemDetails.add(itemsListList.get(position).getDate());
                intent.putExtra("ITEMDETAILS", itemDetails);
                startActivityForResult(intent, ITEMTEXT_REQUEST);
            }
        });
    }

    private void alertWindow(final String itemText, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this entry?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItems(itemText);
                        itemsListList.remove(position);
                        customAdapter.notifyDataSetChanged();
                        //return true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("request code", String.valueOf(requestCode));
        if (requestCode == ITEMTEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textDetails = data.getStringArrayListExtra("ITEMNEW");
                Log.d("itemNew", textDetails.toString());
                itemsList = new ItemsList(textDetails.get(0), textDetails.get(1), textDetails.get(2));
                long itemId = customAdapter.getItemId(itemPosition);
                Log.d("itemId", String.valueOf(itemId));
                Log.d("itemText", itemsList.getItems());
                itemsListList.remove(itemPosition);
                customAdapter.notifyDataSetChanged();
                customAdapter.insert(itemsList, itemPosition);
                customAdapter.notifyDataSetChanged();
                updateItems(itemsList, itemToUpdate);
            }
        }
        if (requestCode == ITEMNEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textDetails = data.getStringArrayListExtra("ITEMNEW");
                itemsList = new ItemsList(textDetails.get(0), textDetails.get(1), textDetails.get(2));
                long itemId = customAdapter.getItemId(itemPosition);
                customAdapter.add(itemsList);
                customAdapter.notifyDataSetChanged();
                writeItems(textDetails.get(0), textDetails.get(1), textDetails.get(2));
            }
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
        if (id == R.id.action_addItem) {
            Intent intent = new Intent(getApplicationContext(), EditItemActivity.class);
            startActivityForResult(intent, ITEMNEW_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
