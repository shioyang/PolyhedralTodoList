package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.model.Task;

import java.util.List;

public class PolyTasksImpl implements PolyTasks {
    private List<Task> tasks = null;

    public PolyTasksImpl(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public int size() {
        return tasks != null ? tasks.size() : 0;
    }

    @Override
    public Task getTask(int position) {
        return tasks != null ? tasks.get(position) : null;
    }

    @Override
    public void save() {
        // no longer needed?
    }

    @Override
    public void load() {
        // no longer needed?
    }

    @Override
    public void addNewTask(int position) {
        // insert
    }

    @Override
    public void addNewTaskToLast() {
        // instert
    }

    @Override
    public void addTask(int position, Task task) {
        // instert
    }

    @Override
    public void removeTask(String taskId) {
        // delete
    }

    @Override
    public void doneTask(String taskId) {
        // update status and completed datetime
    }

    @Override
    public void undoneTask(String taskId) {
        // update status and completed datetime
    }

    @Override
    public void moveTask(String previousTaskId) {

    }
}
