package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.util.Log;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

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
        Task task;
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

    @Override
    public Boolean moveUpTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMainListImpl", "moveUpTask 1 [not implemented]");
        // call task.move needs:
        //     1: task list ID
        //     2: task ID
        //     3: previous ID (higher sibling task ID)
        String previousId = list.getPreviousTaskId(item);
        Log.d("PolyMainListImpl", "Retrieved previous task ID: " + previousId);
        if (!previousId.isEmpty()) {
            // call task.move
            // TODO: Call PolyTodoItemExecutor.move()

            // Update global
            try {
                PolyTodoItem previous = list.getPreviousTask(item);
                while (isHigherPriority(previous, item)) {   // while (previous.position > item.position)
                    moveUpTaskInGlobalTodoItems(item);
                }
            } catch (TaskMismatchPositionsException e) {
                Log.e("PolyMainListImpl", "Position mismatch in moveUpTask().");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean moveUpTask(String taskId, String listId) {
        Log.d("PolyMainListImpl", "moveUpTask 2 [not implemented]");
        return null;
    }

    @Override
    public Boolean moveDownTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMainListImpl", "moveDownTask 1 [not implemented]");
        // call task.move needs:
        //     1: task list ID
        //     2: task ID
        //     3: previous ID (lower sibling task ID)

        return null;
    }

    @Override
    public Boolean moveDownTask(String taskId, String listId) {
        Log.d("PolyMainListImpl", "moveDownTask 2 [not implemented]");
        return null;
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

    private boolean isHigherPriority(PolyTodoItem previous, PolyTodoItem item) throws TaskMismatchPositionsException {
        boolean isHigher = false;
        if (previous != null && item != null) {
            int previousPosition = previous.getGlobalPosition();
            int itemPosition = item.getGlobalPosition();
            if (!verifyPosition(previousPosition, previous) || !verifyPosition(itemPosition, item)) {
                throw new TaskMismatchPositionsException("Position mismatch.");
            }
            if (previousPosition > itemPosition)
                return true;
        }
        return false;
    }

    private boolean verifyPosition(int position, PolyTodoItem item) {
        int index = globalTodoItems.indexOf(item);
        return (index == position) ? true : false;
    }

    private void moveUpTaskInGlobalTodoItems(PolyTodoItem item) {
        // Swap in globalTodoItems
        int index = globalTodoItems.indexOf(item);
        if (index >= 0) {
            return;
        }
        PolyTodoItem target = globalTodoItems.get(index - 1);
        globalTodoItems.set(index - 1, item);
        globalTodoItems.set(index, target);
        // Update global position in each PolyTodoItem
        item.setGlobalPosition(index - 1);
        target.setGlobalPosition(index);
    }
}
