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
     */

//    polyTodoItem(String listId, Task task);

//    void setTask(Task task) throws TaskAlreadyHasException;

    boolean isPolyTodoItem();

    String makeTaskTitle();

    int getColor();
    void setColor(int color);

    Task getTask();
    int getGlobalPosition();
    void setGlobalPosition(int globalPosition);
    int getLocalPosition();
    void setLocalPosition(int localPosition);
    String getJustTitle();
    void setJustTitle(String justTitle);

    String getListId();
    String getId();
    String getTitle();
    String getParent();
    String getPosition();
    String getStatus();

    boolean isSaveNeeded();
    void saveCompleted();
}
