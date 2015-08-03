package jp.gr.java_conf.shioyang.polyhedraltodolist;

public class TaskAlreadyHasException extends Exception {
    public TaskAlreadyHasException(String msg) {
        super(msg);
    }
}
