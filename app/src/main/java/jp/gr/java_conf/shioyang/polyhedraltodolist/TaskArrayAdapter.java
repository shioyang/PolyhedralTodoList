package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskArrayAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater = null;

    public TaskArrayAdapter(Context context, int resource, List<String> tasksList) {
        super(context, resource, tasksList);
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);
//            convertView = inflater.inflate(R.layout.list_item2, null);

        String task = getItem(position);

        TextView textView = (TextView)convertView.findViewById(R.id.textView);
        textView.setText(task);

//        convertView.setBackgroundColor(task.getColor());

        return convertView;
    }
}
