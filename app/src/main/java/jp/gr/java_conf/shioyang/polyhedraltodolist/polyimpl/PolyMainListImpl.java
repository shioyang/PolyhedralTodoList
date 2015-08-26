package jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl;

import android.util.Log;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.OnMainListChangedListener;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMainList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItemComparator;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItemExecutor;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.TaskMismatchPositionsException;

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
        Log.d("PolyMainListImpl", "Start moveUpTask 1");
        String previousId = list.getPreviousTaskId(item);
        Log.d("PolyMainListImpl", "Retrieved previous task ID: " + previousId);
        if (!previousId.isEmpty()) {
            // Call task.move: task list ID, task ID, previous ID (higher sibling task ID)
//            PolyTodoItemExecutor.move(tasksService, item.getId(), list.getId(), previousId);

            try {
                PolyTodoItem previous = list.getPreviousTask(item);
                // Update global
                while (isHigherPriority(previous, item)) {   // while (previous.position > item.position)
                    moveUpTaskForGlobal(item);
                }
                // Update local
                moveUpTaskForLocal(list, item.getId());

                // Save
                // for task in list
                //    find task which isNeedSave is true
                //    async title update task
                saveChangedTasks();

            } catch (TaskMismatchPositionsException e) {
                Log.e("PolyMainListImpl", "Position mismatch in moveUpTask().");
                e.printStackTrace();
                Log.d("PolyMainListImpl", "End moveUpTask 1 with false");
                return false;
            }
        }
        Log.d("PolyMainListImpl", "End moveUpTask 1 with true");
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
            if (previousPosition < itemPosition) // Small digit is high priority.
                return true;
        }
        return false;
    }

    private boolean verifyPosition(int todoItemPosition, PolyTodoItem item) {
        int index = globalTodoItems.indexOf(item);
        Log.d("PolyMainListImpl", "verifyPosition todoItemPosition:" + todoItemPosition + " vs index:" + index);
        return (todoItemPosition == index);
    }

    private void moveUpTaskForGlobal(PolyTodoItem item) {
        Log.d("PolyMainListImpl", "Start moveUpTaskForGlobal()");
        // Swap in globalTodoItems
        int index = globalTodoItems.indexOf(item);
        if (index > 0) {
            Log.d("PolyMainListImpl", "Move up the item which index is " + index + ".");
            PolyTodoItem previous = globalTodoItems.get(index - 1);
            // Update globalTodItems list
            globalTodoItems.set(index - 1, item);
            globalTodoItems.set(index, previous);
            // Update global position in each PolyTodoItem
            item.setGlobalPosition(index - 1);
            previous.setGlobalPosition(index);
        }
        Log.d("PolyMainListImpl", "End moveUpTaskForGlobal()");
    }

    private void moveUpTaskForLocal(PolyTodoList list, String id) {
        // Update local position in PolyTodoList
        list.moveUpTask(id);
    }

    private void saveChangedTasks() {
        for (PolyTodoList list : todoLists) {
            List<PolyTodoItem> savedTasks = list.getSaveNeededTasks();
            for (PolyTodoItem saveItem : savedTasks) {
                PolyTodoItemExecutor.update(tasksService, saveItem.getTask(), list.getId());
                saveItem.saveCompleted(); // TODO: If success, saveCompleted. If not, try again.
            }
        }
    }

}
