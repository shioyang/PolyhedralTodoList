package jp.gr.java_conf.shioyang.polyhedraltodolist;

import java.io.Serializable;
import java.util.Comparator;

public class PolyTodoItemComparator implements Comparator<PolyTodoItem>, Serializable {

    @Override
    public int compare(PolyTodoItem item1, PolyTodoItem item2) {
        int globalPosition1 = item1.getGlobalPosition();
        int globalPosition2 = item2.getGlobalPosition();
        if (globalPosition1 > globalPosition2)
            return 1;
        else if (globalPosition1 == globalPosition2)
            return 0;
        return -1;
    }
}
