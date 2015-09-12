package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.adapter.PolyTodoItemArrayAdapter;
import jp.gr.java_conf.shioyang.polyhedraltodolist.adapter.PolyTodoItemEditArrayAdapter;
import jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl.PolyMergedListImpl;


public class PolyListActivity extends AppCompatActivity {
    private String list_id = null;

    private static final String List_ID = "LIST_ID";

    PolyMergedList polyMergedList;
    PolyTodoList polyTodoList;

    List<PolyTodoItem> polyTodoItems;

    ListView listView;
    ArrayAdapter<PolyTodoItem> adapter;
    Menu menu;
    boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly_list);

        // ListView
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("PolyListActivity", "ListView.onItemClick is called.");
                PolyTodoItem polyTodoItem = (PolyTodoItem)listView.getItemAtPosition(position);
                boolean isSuccess = polyMergedList.moveUpLocalTask(polyTodoItem, polyTodoList);
                if (isSuccess) {
                    polyTodoItems = polyTodoList.getLocalList();
                    refreshView();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long idl) {
                Log.d("PolyListActivity", "ListView.onItemLongClick is called.");
                PolyTodoItem polyTodoItem = (PolyTodoItem)listView.getItemAtPosition(position);
                boolean isSuccess = polyMergedList.moveDownLocalTask(polyTodoItem, polyTodoList);
                if (isSuccess) {
                    polyTodoItems = polyTodoList.getLocalList();
                    refreshView();
                }
                return true; //true: consume this event here
            }
        });

        Intent intent = getIntent();
        list_id = intent.getStringExtra(List_ID);
        if (list_id != null) {
            polyMergedList = PolyMergedListImpl.getInstance();
            polyTodoList = polyMergedList.getPolyTodoList(list_id);
            if (polyTodoList != null) {
                polyTodoItems = new ArrayList<>();
                polyTodoItems = polyTodoList.getLocalList();
                refreshView();
            }
        }
    }

    private void refreshView() {
        if (editMode) {
            adapter = new PolyTodoItemEditArrayAdapter(this, 0, polyTodoItems);
        } else {
            adapter = new PolyTodoItemArrayAdapter(this, 0, polyTodoItems);
        }
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poly_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit_titles:
                editMode = true;
                manageMenuItems();
                refreshView();
                return true;
            case R.id.action_edit_titles_done:
                polyMergedList.saveChangedTasks();
                editMode = false;
                manageMenuItems();
                refreshView();
                return true;
//            case R.id.action_settings:
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void manageMenuItems() {
        if (menu != null) {
            MenuItem editItem = menu.findItem(R.id.action_edit_titles);
            MenuItem doneItem = menu.findItem(R.id.action_edit_titles_done);
            editItem.setVisible(!editMode);
            doneItem.setVisible(editMode);
        }
    }
}
