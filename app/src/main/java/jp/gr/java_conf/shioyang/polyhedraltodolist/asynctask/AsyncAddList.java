package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;

import jp.gr.java_conf.shioyang.polyhedraltodolist.MainActivity;
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class AsyncAddList extends AsyncTask<Void, Void, Boolean> {
    final MainActivity activity;
    final Tasks tasksService;
    private final ProgressBar progressBar;
    private TaskList result = null;

    public AsyncAddList(MainActivity tasksActivity) {
        super();
        this.activity = tasksActivity;
        tasksService = tasksActivity.getService();
        progressBar = (ProgressBar) tasksActivity.findViewById(R.id.progressBarMain);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {

            TaskList taskList = new TaskList();
            taskList.setTitle("Poly:New Task List");
            TaskList result = tasksService.tasklists().insert(taskList).execute();
            this.result = result;

            return true;

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
            activity.completeAddNewList(result);
    }

    public static void run(MainActivity tasksActivity) {
        new AsyncAddList(tasksActivity).execute();
    }
}

