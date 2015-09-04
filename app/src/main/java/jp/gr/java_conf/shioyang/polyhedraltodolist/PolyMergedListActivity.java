package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;

import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.adapter.PolyTodoItemArrayAdapter;
import jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl.PolyMergedListImpl;


public class PolyMergedListActivity extends AppCompatActivity {
    private final static String PREF_ACCOUNT_NAME = "Pref_ColorManager";
    private static final String PREF_KEY_ACCOUNT_NAME = "accountName";
    private static final String APPLICATION_NAME = "PolyhedralTodoList/1.0";

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    public static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final String List_ID = "LIST_ID";

    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    final GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

    GoogleAccountCredential credential;

    PolyMergedList polyMergedList;

    ListView listView;
    PolyTodoItemArrayAdapter adapter;
    List<PolyTodoItem> polyTodoItems;

//    List<MenuItem> disabledMenuItems;

    // ===========================================
    // LIFECYCLE
    // ===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly_main_list);

        // ListView
        listView = (ListView) findViewById(R.id.listViewPolyList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("PolyMergedListActivity", "ListView.onItemClick is called.");
                boolean isSuccess = polyMergedList.moveUpGlobalTask(position);
                if (isSuccess) {
                    polyTodoItems = polyMergedList.getGlobalTodoItems();
                    refreshView();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long idl) {
                Log.d("PolyMergedListActivity", "ListView.onItemLongClick is called.");
                boolean isSuccess = polyMergedList.moveDownGlobalTask(position);
                if (isSuccess) {
                    polyTodoItems = polyMergedList.getGlobalTodoItems();
                    refreshView();
                }
                return true; //true: consume this event here
            }
        });

        // PolyMainList
        polyMergedList = PolyMergedListImpl.getInstance();
        polyMergedList.setOnListChanged(new OnMergedListChangedListener() {
            @Override
            public void mainListChanged() {
//                polyTodoItems = polyMainList.getGlobalTodoItems();
//                adapter.insert();
//                refreshView();
            }
        });

//        disabledMenuItems = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServicesAvailability()) {
            haveGooglePlayServices();
        }
    }

    // ----------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailability();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences pref = getSharedPreferences(PREF_ACCOUNT_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREF_KEY_ACCOUNT_NAME, accountName);
                        editor.apply();
//                        AsyncLoadTasks.run(this, polyMainList);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION: // In AsyncLoadTasks, when access right is needed. UserRecoverableException
                if (resultCode == Activity.RESULT_OK) {
//                    AsyncLoadTasks.run(this, polyMainList);
                } else {
                    chooseAccount();
                }
                break;
        }
    }

    // ----------
    public void refreshView() {
        if (adapter == null) {
            adapter = new PolyTodoItemArrayAdapter(this, 0, polyTodoItems);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

//        for (MenuItem item : disabledMenuItems) {
//            item.setEnabled(true);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_insert:
                // TODO: Add function
                // If an item is not selected, add last.
                // If an item is selected, insert next the item.
//                View selectedView = adapter.getSelectedView();
//                adapter.getPosition(selectedView.getContext());
//                if (selectedView == null) {
//                    polyMainList.addTodoList(selectedView);
//                }
                return true;
            case R.id.action_add:
                // TODO: Ask user which list the new item is added to.
                // show chooser to select list
//                item.setEnabled(false);
//                disabledMenuItems.add(item);
                String listId = polyMergedList.getPolyTodoListId(0); // test
                polyMergedList.addTodoItem(listId);
                setPolyTodoItems(polyMergedList.getGlobalTodoItems());
                adapter.notifyDataSetChanged(); // TODO: work???
                return true;
            case R.id.action_remove:
                // TODO: Remove function
                return true;
            case R.id.action_list_01:
                return startListActivity(0);
            case R.id.action_list_02:
                return startListActivity(1);
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ----------
    public Tasks getService() {
        return polyMergedList.getTasksService();
    }

    public void setPolyTodoItems(List<PolyTodoItem> polyTodoItems) {
        this.polyTodoItems = polyTodoItems;
    }

    // ===========================================
    // PRIVATE
    // ===========================================
    private boolean checkGooglePlayServicesAvailability() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, PolyMergedListActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void haveGooglePlayServices() {
        if (polyMergedList.getTasksService() == null) {
            // TODO: New authentication ?
            Log.e("haveGooglePlayServices", "Not implement new authentication");
            chooseAccount();
        } else {
            if (polyMergedList != null && !polyMergedList.isLoaded()) {
//                AsyncLoadTasks.run(this, polyMainList);
            } else {
                if (polyTodoItems == null) {
                    polyTodoItems = polyMergedList.getGlobalTodoItems();
                }
                refreshView();
            }
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean startListActivity(int num) {
        try {
            Intent intent = new Intent(this, PolyListActivity.class);
            intent.putExtra(List_ID, polyMergedList.getPolyTodoListId(num));
            startActivity(intent);
            overridePendingTransition(R.anim.abc_slide_in_bottom, 0);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false; //???
        }
        return true;
    }
}
