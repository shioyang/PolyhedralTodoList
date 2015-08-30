package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.model.Task;

import java.util.List;

public interface PolyTodoList {
    /*
    Color color
    List<PolyTodoItem> localList

    google.taskList taskList
//    String id
//    String title
     */

    boolean isPolyTodoList();

    void loadTodoItems();

    /* LIST */
    List<PolyTodoItem> getLocalList();
    /* CREATE */
    void addNewTodo(int previousId);
    void addNewTodoPosition(int position);
    void addNewTodoFirst();
    Task addNewTodoLast(int globalPosition);
    /* READ */
    String getId();
    String getTitle();
    String getTaskId(int position);
    PolyTodoItem getPreviousTask(PolyTodoItem item);
    PolyTodoItem getNextTask(PolyTodoItem item);
    String getPreviousTaskId(PolyTodoItem item);
    List<PolyTodoItem> getSaveNeededTasks();
    /* UPDATE */
    void done(int id);
    void updateTitle(int id);
    void moveUpTask(String id);
    void moveDownTask(String id);
    /* DELETE */
    void delete(int id);
}
