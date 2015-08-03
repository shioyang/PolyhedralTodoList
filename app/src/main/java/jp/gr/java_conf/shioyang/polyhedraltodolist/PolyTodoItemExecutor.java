package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask.AsyncAddTask;

public class PolyTodoItemExecutor {
    public static void add(Tasks tasksService, Task task, String listId, String previousTaskId) {
        AsyncAddTask.run(tasksService, task, listId, previousTaskId);
    }
    public static void update(Tasks tasksService, Task task, String listId, String taskId) {
        // TODO: AsyncUpdateTask
//        AsyncUpdateTask.run(tasksService, task, listId, taskId);
    }
}
