package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

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
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class AsyncLoadTasks extends AsyncTask<Void, Void, Boolean> {
    final MainActivity activity;
    final PolyMainList polyMainList;
    final Tasks client;
    private final ProgressBar progressBar;

    private List<TaskList> taskLists;

    public AsyncLoadTasks(MainActivity mainActivity, PolyMainList polyMainList, List<TaskList> taskLists) {
        super();
        this.activity = mainActivity;
        this.polyMainList = polyMainList;
        client = mainActivity.getService();
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.progressBarMain);
        this.taskLists = taskLists;
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
            for (TaskList taskList : taskLists) {
                List<Task> tasks = client.tasks().list(taskList.getId()).setFields("items(id,title,parent,position,status)").execute().getItems();
                if (tasks != null) {
                    // TODO: Provide different color
                    polyMainList.addTodoList(taskList, tasks, R.color.cherry);
                }
            }

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

    public static void run(MainActivity mainActivity, PolyMainList polyMainList, List<TaskList> taskLists) {
        new AsyncLoadTasks(mainActivity, polyMainList, taskLists).execute();
    }
}

