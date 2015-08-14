package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.services.tasks.model.TaskList;

import java.util.List;

public class TaskListsArrayAdapter extends ArrayAdapter<TaskList> {
    private LayoutInflater inflater = null;
    private View selectedView = null;

    public TaskListsArrayAdapter(Context context, int resource, List<TaskList> tasklits) {
        super(context, resource, tasklits);
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        TaskList taskList = getItem(position);

        // Color
//        convertView.setBackgroundColor(polyTodoItem.getColor());

        // Text
        TextView textView = (TextView)convertView.findViewById(R.id.textView);
        textView.setText(taskList.getTitle());
//        textView.setText(polyTodoItem.getJustTitle());

        return convertView;
    }

}