package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;

public class AsyncUpdateTask extends AsyncTask<Void, Void, Boolean> {
    Tasks tasksService;
    Task task;
    String listId;
    String previousTaskId;

    public AsyncUpdateTask(Tasks tasksService, Task task, String listId) {
        this.tasksService = tasksService;
        this.task = task;
        this.listId = listId;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccess = false;
        try {
            Task result = tasksService.tasks().update(listId, task.getId(), task).execute();
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

    public static void run(Tasks tasksService, Task task, String listId) {
        new AsyncUpdateTask(tasksService, task, listId).execute();
    }
}
