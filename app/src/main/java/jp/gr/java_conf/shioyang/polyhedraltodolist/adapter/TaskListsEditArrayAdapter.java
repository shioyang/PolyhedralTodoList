package jp.gr.java_conf.shioyang.polyhedraltodolist.adapter;

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

import com.google.api.services.tasks.model.TaskList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class TaskListsEditArrayAdapter extends ArrayAdapter<TaskList> {
    private LayoutInflater inflater = null;
    private View selectedView = null;

    static String regex = "^poly:(.*)";
    static Pattern pattern;

    static {
        pattern = Pattern.compile(regex);
    }

    public TaskListsEditArrayAdapter(Context context, int resource, List<TaskList> tasklits) {
        super(context, resource, tasklits);
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_list_item_edit, null);
        }

        final TaskList taskList = getItem(position);

        // Color
//        convertView.setBackgroundColor(polyTodoItem.getColor());

        // Text
        String justTitle = "no title";
        Matcher matcher = pattern.matcher(taskList.getTitle());
        if (matcher.find() && matcher.groupCount() == 1) {
            justTitle = matcher.group(1);
        }
        EditText editText = (EditText)convertView.findViewById(R.id.editTextListItemEdit);
        editText.setText(justTitle);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    taskList.setTitle("poly:" + textView.getText().toString());
                }
                return false;
            }
        });

        return convertView;
    }

}