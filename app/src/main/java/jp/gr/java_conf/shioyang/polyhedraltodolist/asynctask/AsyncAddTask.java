package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;

import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyUtil;

public class AsyncAddTask extends AsyncTask<Void, Void, Boolean> {
    Tasks tasksService;
    Task task;
    String listId;
    String previousTaskId;

    public AsyncAddTask(Tasks tasksService, Task task, String listId, String previousTaskId) {
        this.tasksService = tasksService;
        this.task = task;
        this.listId = listId;
        this.previousTaskId = previousTaskId;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccess = false;
        try {
            Task result = tasksService.tasks().insert(listId, task).setPrevious(previousTaskId).execute();
            isSuccess = PolyUtil.copyTaskValues(result, task);
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

    public static void run(Tasks tasksService, Task task, String listId, String previousTaskId) {
        new AsyncAddTask(tasksService, task, listId, previousTaskId).execute();
    }
}
