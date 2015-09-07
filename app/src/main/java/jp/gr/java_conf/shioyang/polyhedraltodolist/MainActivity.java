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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.TaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.adapter.TaskListsArrayAdapter;
import jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask.AsyncAddList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask.AsyncLoadLists;
import jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask.AsyncLoadTasks;
import jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl.PolyMergedListImpl;

public class MainActivity extends AppCompatActivity {
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
    TaskListsArrayAdapter adapter;
    List<TaskList> taskLists;
    TextView polyTextView;

    // ===========================================
    // LIFECYCLE
    // ===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        polyTextView = (TextView) findViewById(R.id.textViewMain);
        polyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PolyMergedListActivity.class);
                startActivity(intent);
            }
        });

        // Google Accounts
        credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS));
        SharedPreferences prefs = getSharedPreferences(PREF_ACCOUNT_NAME, Context.MODE_PRIVATE);
        credential.setSelectedAccountName(prefs.getString(PREF_KEY_ACCOUNT_NAME, null));

        // Tasks client
        Tasks service = new Tasks.Builder(httpTransport, gsonFactory, credential).setApplicationName(APPLICATION_NAME).build();

        // ListView
        listView = (ListView) findViewById(R.id.listViewMain);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("MainActivity", "onItemClick() is called");
                TaskList taskList = adapter.getItem(i);
                // TODO: PolyList is not loaded yet...
                Intent intent = new Intent(view.getContext(), PolyListActivity.class);
                intent.putExtra(List_ID, taskList.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.abc_slide_in_bottom, 0);
            }
        });

        // PolyMainList
        polyMergedList = PolyMergedListImpl.getInstance();
        polyMergedList.setTasksService(service);

//        disabledMenuItems = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkGooglePlayServicesAvailability()) {
            haveGooglePlayServices();
        }
    }

    // Just for debug
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MainActivity", "Finally, onTouchEvent() is called.");
        return super.onTouchEvent(event);
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
                        polyTextView.setEnabled(false);
                        AsyncLoadLists.run(this);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION: // In AsyncLoadTasks, when access right is needed. UserRecoverableException
                if (resultCode == Activity.RESULT_OK) {
                    polyTextView.setEnabled(false);
                    AsyncLoadLists.run(this);
                } else {
                    chooseAccount();
                }
                break;
        }
    }

    // ----------
    public void completeLoadLists() {
        AsyncLoadTasks.run(this, polyMergedList, taskLists, /*isReset*/true);
    }

    public void completeAddNewList(TaskList newTaskList) {
        this.taskLists.add(newTaskList);
        List<TaskList> tls = new ArrayList<>();
        tls.add(newTaskList);
        AsyncLoadTasks.run(this, polyMergedList, tls, /*isReset*/false);
    }

    public void refreshView() {
        if (adapter == null) {
            adapter = new TaskListsArrayAdapter(this, 0, taskLists);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        polyTextView.setEnabled(true);
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
            case R.id.action_add_list:
                AsyncAddList.run(this);
                return true;
            case R.id.action_remove_list:
                // TODO: Remove list
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ----------
    public Tasks getService() {
        return polyMergedList.getTasksService();
    }

    public void setTaskLists(List<TaskList> taskLists) {
        this.taskLists = taskLists;
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
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, MainActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void haveGooglePlayServices() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (this.taskLists == null) { // always null???
                polyTextView.setEnabled(false);
                AsyncLoadLists.run(this);
            }
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }
}
