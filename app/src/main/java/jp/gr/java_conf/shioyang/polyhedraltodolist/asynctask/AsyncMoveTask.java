package jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;

import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyUtil;

public class AsyncMoveTask extends AsyncTask<Void, Void, Boolean> {
    Tasks tasksService;
    String listId;
    Task task;
    String previousTaskId;

    public AsyncMoveTask(Tasks tasksService, String listId, Task task, String previousTaskId) {
        this.tasksService = tasksService;
        this.listId = listId;
        this.task = task;
        this.previousTaskId = previousTaskId;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isSuccess = false;
        try {
            synchronized (task) {
                Task result = tasksService.tasks().move(listId, task.getId()).setPrevious(previousTaskId).execute();
//                task.setPosition(result.getPosition());
//                isSuccess = true;
                isSuccess = PolyUtil.copyTaskValues(result, task);
            }
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

    public static void run(Tasks tasksService, String listId, Task task, String previousTaskId) {
        Log.d("AsyncMoveTask", "listId: " + listId + " task: " + task.getId() + " previousTaskId: " + previousTaskId);
        new AsyncMoveTask(tasksService, listId, task, previousTaskId).execute();
    }
}
