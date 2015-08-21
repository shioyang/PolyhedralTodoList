package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;

import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyUtil;

public class AsyncMoveTask extends AsyncTask<Void, Void, Boolean> {
    Tasks tasksService;
    String listId;
    String taskId;
    String previousTaskId;

    public AsyncMoveTask(Tasks tasksService, String listId, String taskId, String previousTaskId) {
        this.tasksService = tasksService;
        this.listId = listId;
        this.taskId = taskId;
        this.previousTaskId = previousTaskId;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccess = false;
        try {
            Task result = tasksService.tasks().move(listId, taskId).setPrevious(previousTaskId).execute();
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

    public static void run(Tasks tasksService, String listId, String taskId, String previousTaskId) {
        new AsyncMoveTask(tasksService, listId, taskId, previousTaskId).execute();
    }
}
