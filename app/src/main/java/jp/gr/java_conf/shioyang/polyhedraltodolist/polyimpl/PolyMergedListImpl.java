package jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl;

import android.util.Log;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.OnMergedListChangedListener;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyMergedList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoList;
import jp.gr.java_conf.shioyang.polyhedraltodolist.exception.TaskMismatchPositionsException;

/*
 * "Facade" class for PolyTodoList and PolyTodoItem classes.
 *    - Should not access PolyTodoList and PolyTodoItem directly
 */
public class PolyMergedListImpl implements PolyMergedList {
    static Comparator<PolyTodoItem> comparator;
    static {
        comparator = new PolyTodoItemComparator();
    }

    private Tasks tasksService = null;
    private static PolyMergedList polyMergedList = null;

    OnMergedListChangedListener onMergedListChangedListener = null;

    List<PolyTodoList> todoLists;
    List<PolyTodoItem> globalTodoItems;

    private PolyMergedListImpl() {
        reset();
    }

    public static synchronized PolyMergedList getInstance() {
        if (polyMergedList == null) {
            polyMergedList = new PolyMergedListImpl();
        }
        return polyMergedList;
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
    public void setOnListChanged(OnMergedListChangedListener onMergedListChangedListener) {
        this.onMergedListChangedListener = onMergedListChangedListener;
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
    public boolean moveUpTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMainListImpl", "Start moveUpTask 1");
        PolyTodoItem previous = list.getPreviousTask(item);
        Log.d("PolyMainListImpl", "Retrieved previous task ID: " + (previous != null ? previous.getId() : "(previous is null)"));
        if (previous != null) {
            try {
                // Update global
                while (isHigherPriority(previous, item)) {   // while (previous.position > item.position)
                    moveUpTaskForGlobal(item);
                }

                // Update local
                moveUpTaskForLocal(list, item.getTask(), previous.getId());

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
    public boolean moveUpTask(String taskId, String listId) {
        Log.d("PolyMainListImpl", "moveUpTask 2 [not implemented]");
        return true;
    }

    @Override
    public boolean moveDownTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMainListImpl", "moveDownTask 1");
        PolyTodoItem next = list.getNextTask(item);
        Log.d("PolyMainListImpl", "Retrieved next task ID: " + (next != null ? next.getId() : "(next is null)"));
        if (next != null) {
            try {
                // Update global
                while (isHigherPriority(item, next)) {   // while (item.position > next.position)
                    moveDownTaskForGlobal(item);
                }

                // Update local
                moveDownTaskForLocal(list, item.getTask(), next.getId());

                // Save
                // for task in list
                //    find task which isNeedSave is true
                //    async title update task
                saveChangedTasks();

            } catch (TaskMismatchPositionsException e) {
                Log.e("PolyMainListImpl", "Position mismatch in moveUpTask().");
                e.printStackTrace();
                Log.d("PolyMainListImpl", "End moveDownTask 1 with false");
                return false;
            }
        }
        Log.d("PolyMainListImpl", "End moveDownTask 1 with true");
        return true;
    }

    @Override
    public boolean moveDownTask(String taskId, String listId) {
        Log.d("PolyMainListImpl", "moveDownTask 2 [not implemented]");
        return false;
    }

    @Override
    public void saveChangedTasks() {
        for (PolyTodoList list : todoLists) {
            List<PolyTodoItem> savedTasks = list.getSaveNeededTasks();
            for (PolyTodoItem saveItem : savedTasks) {
                PolyTodoItemExecutor.update(tasksService, saveItem.getTask(), list.getId());
                saveItem.saveCompleted(); // TODO: If success, saveCompleted. If not, try again.
            }
        }
    }

    // ===========================================
    // PRIVATE
    // ===========================================
    private void listChanged() {
        if (onMergedListChangedListener != null)
            onMergedListChangedListener.mainListChanged();
    }

    private void mergePolyTodoItemsLast(List<PolyTodoItem> polyTodoItems) {
        globalTodoItems.addAll(polyTodoItems);
        Collections.sort(globalTodoItems, comparator); // Sort by global position
    }

    private boolean isHigherPriority(PolyTodoItem mayHigh, PolyTodoItem mayLow) throws TaskMismatchPositionsException {
        boolean isHigher = false;
        if (mayHigh != null && mayLow != null) {
            int mayHighPosition = mayHigh.getGlobalPosition();
            int mayLowPosition = mayLow.getGlobalPosition();
            if (!verifyPosition(mayHighPosition, mayHigh) || !verifyPosition(mayLowPosition, mayLow)) {
                throw new TaskMismatchPositionsException("Position mismatch.");
            }
            if (mayHighPosition < mayLowPosition) // Small digit is high priority.
                isHigher = true;
        }
        return isHigher;
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

    private void moveDownTaskForGlobal(PolyTodoItem item) {
        Log.d("PolyMainListImpl", "Start moveDownTaskForGlobal()");
        // Swap in globalTodoItems
        int index = globalTodoItems.indexOf(item);
        if (index > 0) {
            Log.d("PolyMainListImpl", "Move down the item which index is " + index + ".");
            PolyTodoItem next = globalTodoItems.get(index + 1);
            // Update globalTodItems list
            globalTodoItems.set(index, next);
            globalTodoItems.set(index + 1, item);
            // Update global position in each PolyTodoItem
            next.setGlobalPosition(index);
            item.setGlobalPosition(index + 1);
        }
        Log.d("PolyMainListImpl", "End moveDownTaskForGlobal()");
    }

    private void moveUpTaskForLocal(PolyTodoList list, Task task, String previousId) {
        // Call task.move: task list ID, task ID, previous ID (higher sibling task ID)
        PolyTodoItemExecutor.move(tasksService, list.getId(), task, previousId);

        // Update local position in PolyTodoList
        list.moveUpTask(task.getId());
    }

    private void moveDownTaskForLocal(PolyTodoList list, Task task, String nextId) {
        // Call task.move: task list ID, task ID, previous ID (higher sibling task ID)
        PolyTodoItemExecutor.move(tasksService, list.getId(), task, nextId);

        // Update local position in PolyTodoList
        list.moveDownTask(task.getId());
    }


}
