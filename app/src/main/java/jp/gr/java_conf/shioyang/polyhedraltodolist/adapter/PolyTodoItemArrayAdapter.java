package jp.gr.java_conf.shioyang.polyhedraltodolist.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.gr.java_conf.shioyang.polyhedraltodolist.PolyTodoItem;
import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class PolyTodoItemArrayAdapter extends ArrayAdapter<PolyTodoItem> {
    protected LayoutInflater inflater = null;

    public PolyTodoItemArrayAdapter(Context context, int resource, List<PolyTodoItem> polyTodoItems) {
        super(context, resource, polyTodoItems);
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);

        PolyTodoItem polyTodoItem = getItem(position);

        // Color
        convertView.setBackgroundColor(polyTodoItem.getColor());

        // Text
        TextView textView = (TextView)convertView.findViewById(R.id.textView);
        textView.setText(polyTodoItem.getJustTitle());

        // Add icon
//        ImageButton addButton = (ImageButton)convertView.findViewById(R.id.addButton);
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });

        return convertView;
    }
}
