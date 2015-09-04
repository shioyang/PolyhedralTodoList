package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.List;

public interface PolyMergedList {
    /*
    List<PolyTodoList> todoLists;
    List<PolyTodoItem> globalTodoItems;
     */

    /*
    no getter/setter
    Should call a method.
     */

    void setTasksService(Tasks tasksService);
    Tasks getTasksService();
    void reset();

    boolean isLoaded();

    List<PolyTodoItem> getGlobalTodoItems();
    void setOnListChanged(OnMergedListChangedListener onMergedListChangedListener);

    /*** LIST *****/
    /*** CREATE ***/
    void addTodoList(TaskList taskList);
    void addTodoList(TaskList taskList, List<Task> tasks, int color);
    void addTodoItem(String listId);
    void insertTodoItem(String listId, int position);
    /*** READ *****/
    String getPolyTodoListId(int num) throws IndexOutOfBoundsException;
    PolyTodoList getPolyTodoList(String listId);
    /*** UPDATE ***/
    boolean moveUpGlobalTask(int globalPosition);
    boolean moveDownGlobalTask(int globalPosition);
    boolean moveUpLocalTask(PolyTodoItem item, PolyTodoList list);
    boolean moveDownLocalTask(PolyTodoItem item, PolyTodoList list);

    void saveChangedTasks();
    /*** DELETE ***/
}
