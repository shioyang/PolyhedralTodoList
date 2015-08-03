package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * "Facade" class for PolyTodoList and PolyTodoItem classes.
 *    - Should not access PolyTodoList and PolyTodoItem directly
 */
public class PolyMainListImpl implements PolyMainList {
    static Comparator<PolyTodoItem> comparator;
    static {
        comparator = new PolyTodoItemComparator();
    }

    private Tasks tasksService = null;
    private static PolyMainList polyMainList = null;

    OnMainListChangedListener onMainListChangedListener = null;

    List<PolyTodoList> todoLists;
    List<PolyTodoItem> globalTodoItems;

    private PolyMainListImpl() {
        reset();
    }

    public static synchronized PolyMainList getInstance() {
        if (polyMainList == null) {
            polyMainList = new PolyMainListImpl();
        }
        return polyMainList;
    }

    @Override
    public void setTasksService(Tasks tasksService) {
        this.tasksService = tasksService;
    }

    @Override
    public Tasks getTasksService() {
        return tasksService;
    }

    @Override
    public void reset() {
        todoLists = new ArrayList<>();
        globalTodoItems = new ArrayList<>();
    }

    @Override
    public boolean isLoaded() {
        return todoLists.size() > 0;
    }

    @Override
    public List<PolyTodoItem> getGlobalTodoItems() {
        return globalTodoItems;
    }

    @Override
    public void addTodoList(TaskList taskList) {
        // TODO: addTodoList(TaskList taskList) not implemented
    }

    @Override
    public void addTodoList(TaskList taskList, List<Task> tasks, int color) throws Exception {
        if (taskList == null && tasks == null)
            throw new Exception();

        PolyTodoList polyTodoList = new PolyTodoListImpl(taskList, tasks, color);

        if (polyTodoList.isPolyTodoList()) {
            todoLists.add(polyTodoList);

            List<PolyTodoItem> polyTodoItems = polyTodoList.getLocalList();
            mergePolyTodoItemsLast(polyTodoItems);
        }
    }

    @Override
    public void addTodoItem(String listId) {
        Task task = null;
        PolyTodoList polyTodoList = getPolyTodoList(listId);
        if (polyTodoList != null) {
            String previousTaskId = polyTodoList.getTaskId(polyTodoList.getLocalList().size() - 1);
            task = polyTodoList.addNewTodoLast(globalTodoItems.size());
            if (task != null) {
                PolyTodoItemExecutor.add(tasksService, task, listId, previousTaskId);

                List<PolyTodoItem> polyTodoItems = polyTodoList.getLocalList();
                globalTodoItems.add(polyTodoItems.get(polyTodoItems.size() - 1));
            }
        }
    }

    @Override
    public void insertTodoItem(String listId, int position) {

    }

    @Override
    public void setOnListChanged(OnMainListChangedListener onMainListChangedListener) {
        this.onMainListChangedListener = onMainListChangedListener;
    }

    @Override
    public String getPolyTodoListId(int num) throws IndexOutOfBoundsException {
        return todoLists.get(num).getId();
    }

    @Override
    public PolyTodoList getPolyTodoList(String listId) {
        if (listId.isEmpty())
            return null;
        PolyTodoList polyTodoList = null;
        for (PolyTodoList todoList : todoLists) {
            if (listId.equals(todoList.getId())) {
                polyTodoList = todoList;
                break;
            }
        }
        return polyTodoList;
    }

    // ===========================================
    // PRIVATE
    // ===========================================
    private void listChanged() {
        if (onMainListChangedListener != null)
            onMainListChangedListener.mainListChanged();
    }

    private void mergePolyTodoItemsLast(List<PolyTodoItem> polyTodoItems) {
        globalTodoItems.addAll(polyTodoItems);
        Collections.sort(globalTodoItems, comparator); // Sort by global position
    }
}
