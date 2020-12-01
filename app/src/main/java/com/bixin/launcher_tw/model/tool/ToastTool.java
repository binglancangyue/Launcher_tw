package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.LauncherApp;

public class ToastTool {
    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(LauncherApp.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /*    @SuppressLint("ShowToast")
        public static void showToast(int text) {
            if (toast == null) {
                toast = Toast.makeText(LauncherApp.getInstance(), text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.show();
        }*/
    public static void showToast(int text) {
        if (toast == null) {
            toast = Toast.makeText(LauncherApp.getInstance(), text, Toast.LENGTH_SHORT);
            LinearLayout linearLayout = (LinearLayout) toast.getView();
            linearLayout.setBackground(LauncherApp.getInstance().getResources().getDrawable(R.drawable.circle_toast_bg));
            TextView messageTextView = (TextView) linearLayout.getChildAt(0);
            messageTextView.setTextSize(24);
            messageTextView.setTextColor(Color.WHITE);
            toast.setGravity(Gravity.BOTTOM, 0, 35);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
