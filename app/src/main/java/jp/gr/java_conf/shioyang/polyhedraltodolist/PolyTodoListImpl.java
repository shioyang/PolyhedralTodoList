package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PolyTodoListImpl implements PolyTodoList {
    static Comparator<PolyTodoItem> comparator;

    static {
        comparator = new PolyTodoItemComparator();
    }

    /*
    String id
    String title
    */
    TaskList taskList;
    List<PolyTodoItem> localList;
    int color;
    boolean isPolyTodoList = true;

    public PolyTodoListImpl(TaskList taskList) {
        this.taskList = taskList;
        this.localList = new ArrayList<>();
        if (taskList != null) {
            // TODO
        }
    }

    public PolyTodoListImpl(TaskList taskList, List<Task> tasks, int color) {
        this.taskList = taskList;
        this.localList = new ArrayList<>();
        // TODO: Set color
        this.color = color;
        if (taskList != null && tasks != null) {
            String listId = taskList.getId();
            for (Task task : tasks) {
                if (task.getTitle().isEmpty())
                    break;
                PolyTodoItem polyTodoItem = new PolyTodoItemImpl(task, listId, color);
                localList.add(polyTodoItem);
                isPolyTodoList = isPolyTodoList && polyTodoItem.isPolyTodoItem(); // If there is a non PolyTodoItem, isPolyTodoList is false.
            }
        }
    }

    @Override
    public  boolean isPolyTodoList() {
        return isPolyTodoList;
    }

    @Override
    public void loadTodoItems() {

    }

    @Override
    public List<PolyTodoItem> getLocalList() {
        return localList;
    }

    @Override
    public void addNewTodo(int previousId) {

    }

    @Override
    public void addNewTodoPosition(int position) {

    }

    @Override
    public void addNewTodoFirst() {

    }

    @Override
    public Task addNewTodoLast(int globalPosition) {
        return createTodoItem(globalPosition, localList.size(), "");
    }

    @Override
    public String getId() {
        return taskList.getId();
    }

    @Override
    public String getTaskId(int position) {
        String taskId = "";
        PolyTodoItem polyTodoItem = localList.get(position);
        if (polyTodoItem != null) {
            taskId = polyTodoItem.getId();
        }
        return taskId;
    }

    @Override
    public PolyTodoItem getPreviousTask(PolyTodoItem item) {
        PolyTodoItem rtnTodoItem = null;
        if (item != null) {
            String itemId = item.getId();
            for (int i = 1; i < localList.size(); i++) {
                PolyTodoItem previous = localList.get(i - 1);
                String currentId = localList.get(i).getId();
                if (currentId.equals(itemId)) {
                    rtnTodoItem = previous;
                    break;
                }
            }
        }
        return rtnTodoItem;
    }

    @Override
    public String getPreviousTaskId(PolyTodoItem item) {
        PolyTodoItem previous = getPreviousTask(item);
        return (previous != null) ? previous.getId() : "";
    }

    @Override
    public String getTitle() {
        return taskList.getTitle();
    }

    @Override
    public void done(int id) {

    }

    @Override
    public void updateTitle(int id) {

    }

    @Override
    public void moveUpTask(String id) {

    }

    @Override
    public void moveDownTask(String id) {

    }

    @Override
    public void delete(int id) {

    }

    private Task createTodoItem(int globalPosition, int localPosition, String justTitle) {
        Task task = new Task();
        task.setTitle(PolyUtil.formatTaskTitle(globalPosition, localPosition, justTitle));
        PolyTodoItem polyTodoItem = new PolyTodoItemImpl(task, getId(), color);
        localList.add(polyTodoItem);
        return task;
    }
}
