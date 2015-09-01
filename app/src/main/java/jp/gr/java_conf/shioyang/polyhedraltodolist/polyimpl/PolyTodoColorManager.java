package jp.gr.java_conf.shioyang.polyhedraltodolist.polyimpl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import jp.gr.java_conf.shioyang.polyhedraltodolist.R;

public class PolyTodoColorManager {
    final static String PREF_NAME = "Pref_ColorManager";
    final static String PREF_KEY_COLOR_COUNT = "PrefKey_colorCount";

    public static int getColor(String listId, Context context) {
        if (listId.isEmpty())
            return -1;

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        int color = pref.getInt(listId, -1);
        if (color == -1) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(listId, generateNewColor(pref));
        }
        return color;
    }

    private static int generateNewColor(SharedPreferences pref) {
        int color;
        int count = pref.getInt(PREF_KEY_COLOR_COUNT, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_KEY_COLOR_COUNT, ++count);
        // TODO: Generate a new color
        // temporally
        switch(count % 5) {
            case 0:
                color = R.color.cherry;
                break;
            case 1:
                color = R.color.skyBlue;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.MAGENTA;
                break;
            case 4:
                color = Color.CYAN;
                break;
            default:
                color = Color.LTGRAY;
                break;
        }
        // temporally

        return color;
    }
}
