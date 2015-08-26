package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.util.Log;

import com.google.api.services.tasks.model.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolyTodoItemImpl implements PolyTodoItem {
    static String regex = "^poly:global(\\d+):local(\\d+):(.*)";
    static Pattern pattern;

    static {
        pattern = Pattern.compile(regex);
    }

    /*
    String id
    String title
    String parent
    String position
    String status
     */
    Task task;

    String listId;
    int color;

    int globalPosition = -1;
    int localPosition = -1;
    String justTitle = null;

    boolean isSaveNeeded = false;

    public PolyTodoItemImpl(Task task, String listId, int color) {
        this.task = task;
        this.listId = listId;
        this.color = color;
        parseTitle();
    }

    @Override
    public boolean isPolyTodoItem() {
        return globalPosition >= 0 ? true : false;
    }

    private Matcher matchTaskTitle() {
        return pattern.matcher(task.getTitle());
    }

    private boolean parseTitle() {
        Matcher matcher = matchTaskTitle();
        if(matcher.find() && matcher.groupCount() == 3) {
            globalPosition = Integer.parseInt(matcher.group(1));
            localPosition = Integer.parseInt(matcher.group(2));
            justTitle = matcher.group(3);
            return true;
        }
        return false;
    }

    @Override
    public String makeTaskTitle() {
        return PolyUtil.formatTaskTitle(globalPosition, localPosition, justTitle);
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public Task getTask() {
        return this.task;
    }

    @Override
    public int getGlobalPosition() {
        return globalPosition;
    }

    @Override
    public void setGlobalPosition(int globalPosition) {
        this.globalPosition = globalPosition;
        String newTitle = makeTaskTitle();
        task.setTitle(newTitle);
        Log.d("PolyTodoItemImpl", "setGlobalPosition| New title: " + newTitle);
        this.isSaveNeeded = true;
    }

    @Override
    public int getLocalPosition() {
        return localPosition;
    }

    @Override
    public void setLocalPosition(int localPosition) {
        this.localPosition = localPosition;
        String newTitle = makeTaskTitle();
        task.setTitle(newTitle);
        Log.d("PolyTodoItemImpl", "setLocalPosition| New title: " + newTitle);
        this.isSaveNeeded = true;
    }

    @Override
    public String getJustTitle() {
        return justTitle;
    }

    @Override
    public void setJustTitle(String justTitle) {
        this.justTitle = justTitle;
        String newTitle = makeTaskTitle();
        task.setTitle(newTitle);
        Log.d("PolyTodoItemImpl", "setJustTitle| New title: " + newTitle);
        this.isSaveNeeded = true;
    }

    @Override
    public String getListId() {
        return listId;
    }

    @Override
    public String getId() {
        return task.getId();
    }

    @Override
    public String getTitle() {
        return task.getTitle();
    }

    @Override
    public String getParent() {
        return task.getParent();
    }

    @Override
    public String getPosition() {
        return task.getPosition();
    }

    @Override
    public String getStatus() {
        return task.getStatus();
    }

    @Override
    public boolean isSaveNeeded() {
        return this.isSaveNeeded;
    }

    @Override
    public void saveCompleted() {
        this.isSaveNeeded = false;
    }
}
