package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.model.Task;

public interface PolyTodoItem {
    /*
    String listId

    google.task task
//    String id
//    String title
//    String parent
//    String position
//    String status

    PolyTodoItem parentItem
     */

//    polyTodoItem(String listId, Task task);

//    void setTask(Task task) throws TaskAlreadyHasException;

    boolean isPolyTodoItem();

    String makeTaskTitle();

    int getColor();
    void setColor(int color);

    int getGlobalPosition();
    void setGlobalPosition(int globalPosition);
    int getLocalPosition();
    String getJustTitle();

    String getListId();
    String getId();
    String getTitle();
    String getParent();
    String getPosition();
    String getStatus();

    PolyTodoItem getParentItem();
}
