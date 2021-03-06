package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.MainActivity;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMergedList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;
import jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl.PolyTodoColorManager;

public class AsyncLoadTasks extends AsyncTask<Void, Void, Boolean> {
    final MainActivity activity;
    final PolyMergedList polyMergedList;
    final Tasks client;
    final boolean isReset;
    private final ProgressBar progressBar;

    private List<TaskList> taskLists;

    public AsyncLoadTasks(MainActivity mainActivity, PolyMergedList polyMergedList, List<TaskList> taskLists, boolean isReset) {
        super();
        this.activity = mainActivity;
        this.polyMergedList = polyMergedList;
        client = mainActivity.getService();
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.progressBarMain);
        this.taskLists = taskLists;
        this.isReset = isReset;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        if (isReset)
            polyMergedList.reset();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {

            for (TaskList taskList : taskLists) {
                List<Task> tasks = client.tasks().list(taskList.getId()).setFields("items(id,title,parent,position,status)").execute().getItems();
                int color = PolyTodoColorManager.getColor(taskList.getId(), activity);
                polyMergedList.addTodoList(taskList, tasks, color);
            }
            return true;

        } catch (UserRecoverableAuthIOException userRecoverableAuthIOException) {
            activity.startActivityForResult(userRecoverableAuthIOException.getIntent(), activity.REQUEST_AUTHORIZATION);
            // The result is handled in MainActivity.onActivityResult().
        } catch (IOException e) {
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

    public static void run(MainActivity mainActivity, PolyMergedList polyMergedList, List<TaskList> taskLists, boolean isReset) {
        new AsyncLoadTasks(mainActivity, polyMergedList, taskLists, isReset).execute();
    }
}

