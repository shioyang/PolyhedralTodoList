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
import jp.gr.java_conf.shioyang.polyhedraltodolist.asynctask.AsyncUpdateList;
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
    public void addTodoList(TaskList taskList, List<Task> tasks, int color) {
        if (taskList != null) {
            PolyTodoList polyTodoList = new PolyTodoListImpl(taskList, tasks != null ? tasks : new ArrayList<Task>(), color);

            if (polyTodoList.isPolyTodoList()) {
                todoLists.add(polyTodoList);
                List<PolyTodoItem> polyTodoItems = polyTodoList.getLocalList();
                mergePolyTodoItemsLast(polyTodoItems);
            }
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
    public boolean moveUpGlobalTask(int globalPosition) {
        Log.d("PolyMergedListImpl", "Start moveUpGlobalTask");
        if (globalPosition <= 0 || globalTodoItems.size() <= globalPosition) {
            Log.d("PolyMergedListImpl", "End moveUpGlobalTask with false");
            return false;
        }

        PolyTodoItem item = globalTodoItems.get(globalPosition);

        // Update local
        //   If different list, do nothing.
        //   If same list, swap them.
        PolyTodoItem previous = globalTodoItems.get(globalPosition - 1);
        if (isSameListTasks(item, previous)) {
            PolyTodoList list = getPolyTodoList(item.getListId());
            moveUpTaskForLocal(list, item, previous);
        }

        // Update global
        moveUpTaskForGlobal(item);

        // Save
        saveChangedTasks();

        Log.d("PolyMergedListImpl", "End moveUpGlobalTask with true");
        return true;
    }

    @Override
    public boolean moveDownGlobalTask(int globalPosition) {
        Log.d("PolyMergedListImpl", "Start moveDownGlobalTask");
        if (globalPosition < 0 || (globalTodoItems.size() - 1) <= globalPosition) {
            Log.d("PolyMergedListImpl", "End moveDownGlobalTask with false");
            return false;
        }

        PolyTodoItem item = globalTodoItems.get(globalPosition);

        // Update local
        //   If different list, do nothing.
        //   If same list, swap them.
        PolyTodoItem next = globalTodoItems.get(globalPosition + 1);
        if (isSameListTasks(item, next)) {
            PolyTodoList list = getPolyTodoList(item.getListId());
            moveDownTaskForLocal(list, item, next);
        }

        // Update global
        moveDownTaskForGlobal(item);

        // Save
        saveChangedTasks();

        Log.d("PolyMergedListImpl", "End moveDownGlobalTask with true");
        return true;
    }

    @Override
    public boolean moveUpLocalTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMergedListImpl", "Start moveUpLocalTask");
        PolyTodoItem previous = list.getPreviousTask(item);
        Log.d("PolyMergedListImpl", "Retrieved previous task ID: " + (previous != null ? previous.getId() : "(previous is null)"));
        if (previous != null) {
            try {
                // Update global
                while (isHigherPriority(previous, item)) {   // while (previous.position > item.position)
                    moveUpTaskForGlobal(item);
                }

                // Update local
                moveUpTaskForLocal(list, item, previous);

                // Save
                // for task in list
                //    find task which isNeedSave is true
                //    async title update task
                saveChangedTasks();

            } catch (TaskMismatchPositionsException e) {
                Log.e("PolyMergedListImpl", "Position mismatch in moveUpTask().");
                e.printStackTrace();
                Log.d("PolyMergedListImpl", "End moveUpTask with false");
                return false;
            }
        }
        Log.d("PolyMergedListImpl", "End moveUpLocalTask with true");
        return true;
    }

    @Override
    public boolean moveDownLocalTask(PolyTodoItem item, PolyTodoList list) {
        Log.d("PolyMergedListImpl", "Start moveDownLocalTask");
        PolyTodoItem next = list.getNextTask(item);
        Log.d("PolyMergedListImpl", "Retrieved next task ID: " + (next != null ? next.getId() : "(next is null)"));
        if (next != null) {
            try {
                // Update global
                while (isHigherPriority(item, next)) {   // while (item.position > next.position)
                    moveDownTaskForGlobal(item);
                }

                // Update local
                moveDownTaskForLocal(list, item, next);

                // Save
                // for task in list
                //    find task which isNeedSave is true
                //    async title update task
                saveChangedTasks();

            } catch (TaskMismatchPositionsException e) {
                Log.e("PolyMergedListImpl", "Position mismatch in moveUpTask().");
                e.printStackTrace();
                Log.d("PolyMergedListImpl", "End moveDownTask with false");
                return false;
            }
        }
        Log.d("PolyMergedListImpl", "End moveDownLocalTask with true");
        return true;
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

    @Override
    public void saveChangedLists() {
        for (PolyTodoList list : todoLists) {
            if (list.isSaveNeeded()) {
                AsyncUpdateList.run(tasksService, list.getTaskList());
//                PolyTodoListExecutor.update(tasksService, list); // TODO: Prepare PolyTodoListExecutor class
                list.saveCompleted();
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
        Log.d("PolyMergedListImpl", "verifyPosition todoItemPosition:" + todoItemPosition + " vs index:" + index);
        return (todoItemPosition == index);
    }

    private void moveUpTaskForGlobal(PolyTodoItem item) {
        Log.d("PolyMergedListImpl", "Start moveUpTaskForGlobal()");
        // Swap in globalTodoItems
        int index = globalTodoItems.indexOf(item);
        if (index > 0) {
            Log.d("PolyMergedListImpl", "Move up the item which index is " + index + ".");
            PolyTodoItem previous = globalTodoItems.get(index - 1);
            // Update globalTodItems list
            globalTodoItems.set(index - 1, item);
            globalTodoItems.set(index, previous);
            // Update global position in each PolyTodoItem
            item.setGlobalPosition(index - 1);
            previous.setGlobalPosition(index);
        }
        Log.d("PolyMergedListImpl", "End moveUpTaskForGlobal()");
    }

    private void moveDownTaskForGlobal(PolyTodoItem item) {
        Log.d("PolyMergedListImpl", "Start moveDownTaskForGlobal()");
        // Swap in globalTodoItems
        int index = globalTodoItems.indexOf(item);
        if (index > 0) {
            Log.d("PolyMergedListImpl", "Move down the item which index is " + index + ".");
            PolyTodoItem next = globalTodoItems.get(index + 1);
            // Update globalTodItems list
            globalTodoItems.set(index, next);
            globalTodoItems.set(index + 1, item);
            // Update global position in each PolyTodoItem
            next.setGlobalPosition(index);
            item.setGlobalPosition(index + 1);
        }
        Log.d("PolyMergedListImpl", "End moveDownTaskForGlobal()");
    }

    private void moveUpTaskForLocal(PolyTodoList list, PolyTodoItem item, PolyTodoItem previous) {
        // Call task.move: task list ID, task ID, new previous ID (higher sibling task ID)
        // The current previous task moves down, and the current item becomes the new previous one.
        //    FROM:              TO:
        //          previous         item       <<- "previous"'s previous
        //          item             previous
        PolyTodoItemExecutor.move(tasksService, list.getId(), previous.getTask(), item.getId());

        // Update local position in PolyTodoList
        list.moveUpTask(item.getTask().getId());
    }

//    private void moveDownTaskForLocal(PolyTodoList list, Task task, String nextId) {
    private void moveDownTaskForLocal(PolyTodoList list, PolyTodoItem item, PolyTodoItem next) {
        // Call task.move: task list ID, task ID, previous ID (higher sibling task ID)
        // The current task moves up, and the current next task becomes the new previous one.
        //    FROM:              TO:
        //          item             next   <<- "item"'s previous
        //          next             item
        PolyTodoItemExecutor.move(tasksService, list.getId(), item.getTask(), next.getId());

        // Update local position in PolyTodoList
        list.moveDownTask(item.getTask().getId());
    }

    private boolean isSameListTasks(PolyTodoItem item, PolyTodoItem previous) {
        return item.getListId().equals(previous.getListId());
    }


}
