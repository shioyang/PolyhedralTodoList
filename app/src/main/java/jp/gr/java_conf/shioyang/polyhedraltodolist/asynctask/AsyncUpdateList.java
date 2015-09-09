package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;

import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoList;

public class AsyncUpdateList extends AsyncTask<Void, Void, Boolean> {
    Tasks tasksService;
    TaskList taskList;

    public AsyncUpdateList(Tasks tasksService, TaskList taskList) {
        this.tasksService = tasksService;
        this.taskList = taskList;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccess = false;
        try {
            synchronized (taskList) {
                TaskList result = tasksService.tasklists().update(taskList.getId(), taskList).execute();
            }
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
//        if (isSuccess) {
//        } else {
//        }
    }

    public static void run(Tasks tasksService, TaskList taskList) {
        Log.d("AsyncUpdateList", "taskList: " + taskList.getId());
        new AsyncUpdateList(tasksService, taskList).execute();
    }
}
