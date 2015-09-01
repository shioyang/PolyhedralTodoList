package jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl;

import com.google.api.services.tasks.model.Task;

public  class PolyUtil {
    public static String formatTaskTitle(int globalPosition, int localPosition, String justTitle) {
        return String.format( "poly:global%d:local%d:%s", globalPosition, localPosition, justTitle);
    }

    public static boolean copyTaskValues(Task from, Task to) {
        if (from == null || to == null || from.isEmpty() || to.isEmpty())
            return false;
        // This copy is needed because not allowing PolyTodoItem to have setter and getter for task.
        to.setId(from.getId());
        to.setTitle(from.getTitle());
        to.setKind(from.getKind());
        to.setEtag(from.getEtag());
        to.setSelfLink(from.getSelfLink());
        to.setPosition(from.getPosition());
        to.setStatus(from.getStatus());
        to.setUpdated(from.getUpdated());
        to.setParent(from.getParent());
        to.setCompleted(from.getCompleted());
        to.setDeleted(from.getDeleted());
        to.setDue(from.getDue());
        to.setHidden(from.getHidden());
        to.setNotes(from.getNotes());
        to.setLinks(from.getLinks());
        return true;
    }
}
