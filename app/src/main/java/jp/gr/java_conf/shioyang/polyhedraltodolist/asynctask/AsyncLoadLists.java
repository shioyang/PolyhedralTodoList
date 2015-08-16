package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.java_conf.shioyang.polyhedraltodolist.MainActivity;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMainList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMainListActivity;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class AsyncLoadLists extends AsyncTask<Void, Void, Boolean> {
    final MainActivity activity;
    final Tasks client;
    private final ProgressBar progressBar;

    static String regex = "^poly:(.*)";
    static Pattern pattern;

    static {
        pattern = Pattern.compile(regex);
    }

    public AsyncLoadLists(MainActivity tasksActivity) {
        super();
        this.activity = tasksActivity;
        client = tasksActivity.getService();
        progressBar = (ProgressBar) tasksActivity.findViewById(R.id.progressBarMain);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<String> result = new ArrayList<>();
        try {
//            TaskLists taskLists = client.tasklists().list().execute();
//            activity.setTaskLists(taskLists.getItems());
            List<TaskList> taskLists = client.tasklists().list().execute().getItems();
            List<TaskList> newTaskLists = new ArrayList<>();
            for (TaskList taskList : taskLists) {
                String title = taskList.getTitle();
                Matcher matcher = pattern.matcher(title);
                if (matcher.find() && matcher.groupCount() == 1) {
                    newTaskLists.add(taskList);
                }
            }
            activity.setTaskLists(newTaskLists);

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

    public static void run(MainActivity tasksActivity) {
        new AsyncLoadLists(tasksActivity).execute();
    }
}

