package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.content.Context;
import android.view.View;

import java.util.List;

public class PolyTodoItemEditArrayAdapter extends PolyTodoItemArrayAdapter {

    public PolyTodoItemEditArrayAdapter(Context context, int resource, List<PolyTodoItem> polyTodoItems) {
        super(context, resource, polyTodoItems);
    }

    @Override
    protected void createConvertView(View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_edit, null);
        }
    }

}
