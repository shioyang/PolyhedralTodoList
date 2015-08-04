package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class PolyListActivity extends AppCompatActivity {
    private String list_id = null;

    // test
    private static final String List_ID = "LIST_ID";
    // test

    PolyMainList polyMainList;
    PolyTodoList polyTodoList;

    List<PolyTodoItem> polyTodoItems;

    ListView listView;
    PolyTodoItemArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly_list);

        // ListView
        listView = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        list_id = intent.getStringExtra(List_ID);
        if (list_id != null) {
            polyMainList = PolyMainListImpl.getInstance();
            polyTodoList = polyMainList.getPolyTodoList(list_id);
            if (polyTodoList != null) {
                polyTodoItems = new ArrayList<>();
                polyTodoItems = polyTodoList.getLocalList();
                refreshView();
            }
        }
    }

    private void refreshView() {
        adapter = new PolyTodoItemArrayAdapter(this, 0, polyTodoItems);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poly_list, menu);
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
}