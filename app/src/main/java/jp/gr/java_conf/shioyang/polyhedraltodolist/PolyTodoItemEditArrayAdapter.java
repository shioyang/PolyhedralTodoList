package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

public class PolyTodoItemEditArrayAdapter extends ArrayAdapter<PolyTodoItem> {
    protected LayoutInflater inflater = null;

    public PolyTodoItemEditArrayAdapter(Context context, int resource, List<PolyTodoItem> polyTodoItems) {
        super(context, resource, polyTodoItems);
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_edit, null);

        PolyTodoItem polyTodoItem = getItem(position);

        // Color
        convertView.setBackgroundColor(polyTodoItem.getColor());

        // Text
        EditText editText = (EditText)convertView.findViewById(R.id.editView);
        editText.setText(polyTodoItem.getJustTitle());

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
