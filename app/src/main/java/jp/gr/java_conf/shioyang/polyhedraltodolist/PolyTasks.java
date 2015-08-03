package jp.gr.java_conf.shioyang.polyhedraltodolist;

import com.google.api.services.tasks.model.Task;

import java.util.List;

public interface PolyTasks {
    void setTasks(List<Task> tasks);
    List<Task> getTasks();
    int size();

    Task getTask(int position);

    void save();
    void load();

    void addNewTask(int position);
    void addNewTaskToLast();

    void addTask(int position, Task task);

    void removeTask(String taskId);

    void doneTask(String taskId);
    void undoneTask(String taskId);

    void moveTask(String previousTaskId);
}
