package jp.gr.java_conf.shioyang.polyhedraltodolist;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

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

        final PolyTodoItem polyTodoItem = getItem(position);

        // Color
        convertView.setBackgroundColor(polyTodoItem.getColor());

        // Text
        EditText editText = (EditText)convertView.findViewById(R.id.editView);
        editText.setText(polyTodoItem.getJustTitle());
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean done = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    polyTodoItem.setJustTitle(textView.getText().toString());
                    done = true;
                }
                return done;
            }
        });

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
