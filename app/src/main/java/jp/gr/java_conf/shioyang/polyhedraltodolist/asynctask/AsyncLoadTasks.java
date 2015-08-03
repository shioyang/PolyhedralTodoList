package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.MainActivity;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMainList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMainListImpl;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class AsyncLoadTasks extends AsyncTask<Void, Void, Boolean> {
    final MainActivity activity;
    final PolyMainList polyMainList;
    final Tasks client;
    private final ProgressBar progressBar;

    public AsyncLoadTasks(MainActivity tasksActivity, PolyMainList polyMainList) {
        super();
        this.activity = tasksActivity;
        this.polyMainList = polyMainList;
        client = tasksActivity.getService();
//        client = tasksActivity.service;
        progressBar = (ProgressBar) tasksActivity.findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        polyMainList.reset();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<String> result = new ArrayList<>();
        try {
            //test
            String id01 = activity.getResources().getString(R.string.list_id_01); //PolyTest01
            TaskList taskList = client.tasklists().get(id01).setFields("id, title").execute();
            List<Task> tasks = client.tasks().list(id01).setFields("items(id,title,parent,position,status)").execute().getItems();
            if (tasks != null) {
                polyMainList.addTodoList(taskList, tasks, R.color.cherry);
            }

            String id02 = activity.getResources().getString(R.string.list_id_02); //PolyTest02
            TaskList taskList2 = client.tasklists().get(id02).setFields("id, title").execute();
            List<Task> tasks2 = client.tasks().list(id02).setFields("items(id,title,parent,position,status)").execute().getItems();
            if (tasks2 != null) {
                polyMainList.addTodoList(taskList2, tasks2, R.color.skyBlue);
            }
            //test

            List<PolyTodoItem> globalTodoItems = polyMainList.getGlobalTodoItems();
            activity.setPolyTodoItems(globalTodoItems);
//            if (globalTodoItems != null) {
//                for (PolyTodoItem polyTodoItem : globalTodoItems) {
//                    result.add(polyTodoItem.getTitle());
////                    result.add(polyTodoItem.getJustTitle());
//                }
//            } else {
//                result.add("No tasks...");
//            }
//            activity.setTasksList(result);

//            TaskList taskList = client.tasklists().get("@default").setFields("id").execute();
//            List<Task> tasks = client.tasks().list(listId).setFields("items(id,title,parent,position,status)").execute().getItems();

//            List<Task> tasks = client.tasks().list("@default").setFields("items(title)").execute().getItems();
//            if (tasks != null) {
//                for (Task task : tasks) {
//                    result.add(task.getTitle());
//                }
//            } else {
//                result.add("No tasks...");
//            }
//            activity.tasksList = result;
            return true;
        } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
            activity.startActivityForResult(userRecoverableAuthIOException.getIntent(), activity.REQUEST_AUTHORIZATION);
            // The result is handled in MainActivity.onActivityResult().
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) { //from polyMainList.addTodoList(taskList, tasks);
            e.printStackTrace();
        }
        return false; // fail
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        progressBar.setVisibility(View.GONE);
        if (isSuccess)
            activity.refreshView();
    }

    public static void run(MainActivity tasksActivity, PolyMainList polyMainList) {
        new AsyncLoadTasks(tasksActivity, polyMainList).execute();
    }
}

