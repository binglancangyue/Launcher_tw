package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;

import java.lang.reflect.Field;

/**
 * @author Altair
 * @date :2020.05.29 下午 04:47
 * @description:
 */
public class DialogTool {
    private Context mContext;
    private AlertDialog closeGPSDialog;
    private AlertDialog formatSDCardDialog;
    private AlertDialog close4GDialog;

    public DialogTool(Context mContext) {
        this.mContext = mContext;
    }

    public void showCloseGPSDialog(Context context) {
        if (closeGPSDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = View.inflate(context, R.layout.dialog_layout, null);
            builder.setView(view);
            TextView title = view.findViewById(R.id.tv_dialog_title);
            TextView message = view.findViewById(R.id.tv_dialog_message);
            TextView negativeButton = view.findViewById(R.id.tv_dialog_cancel);
            TextView positiveButton = view.findViewById(R.id.tv_dialog_ok);
            title.setText(R.string.close_gps);
            message.setText(R.string.close_gps_message);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissCloseGPSDialog();
                    sendBroadcast("CLOSE_GPS", true);
                    InterfaceCallBackManagement.getInstance().updateView(1,true);
                }
            });
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissCloseGPSDialog();
                    InterfaceCallBackManagement.getInstance().updateView(1,false);
                }
            });
            closeGPSDialog = builder.create();
        }
        showAlertDialog(closeGPSDialog);
    }

    public void showFormatDialog(Context context) {
        if (formatSDCardDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = View.inflate(context, R.layout.dialog_layout, null);
          /*  LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.width = 280;*/
            builder.setView(view);
            TextView title = view.findViewById(R.id.tv_dialog_title);
            TextView message = view.findViewById(R.id.tv_dialog_message);
            TextView negativeButton = view.findViewById(R.id.tv_dialog_cancel);
            TextView positiveButton = view.findViewById(R.id.tv_dialog_ok);
            title.setText(R.string.settings_other_format_sd_card);
            message.setText(R.string.format_message);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissFormatDialog();
//                    InterfaceCallBackManagement.getInstance().updateView(1,false);
                }
            });
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissFormatDialog();
                    sendBroadcast("FORMAT_SD", true);

                }
            });
            formatSDCardDialog = builder.create();
        }
        showAlertDialog(formatSDCardDialog);
    }

    private void showAlertDialog(AlertDialog alertDialog) {
        alertDialog.show();
        WindowManager.LayoutParams params =
                alertDialog.getWindow().getAttributes();
        params.width = (int) mContext.getResources().getDimension(R.dimen.kd003DialogWidth);
        alertDialog.getWindow().setAttributes(params);
    }

    public void showClose4GDialog(Context context) {
        if (close4GDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = View.inflate(context, R.layout.dialog_layout, null);
            builder.setView(view);
            TextView title = view.findViewById(R.id.tv_dialog_title);
            TextView message = view.findViewById(R.id.tv_dialog_message);
            TextView negativeButton = view.findViewById(R.id.tv_dialog_cancel);
            TextView positiveButton = view.findViewById(R.id.tv_dialog_ok);
            title.setText(R.string.close_4g);
            message.setText(R.string.close_4g_message);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissClose4GDialog();
                    sendBroadcast("CLOSE_4G", true);
                    InterfaceCallBackManagement.getInstance().updateView(2,true);
                }
            });
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissClose4GDialog();
                    InterfaceCallBackManagement.getInstance().updateView(2,false);
                }
            });
            close4GDialog = builder.create();
        }
        showAlertDialog(close4GDialog);
    }

    private void showDialog(AlertDialog alertDialog) {
        if (!alertDialog.isShowing()) {
            focusNotAle(alertDialog.getWindow());
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            setDialogTextSize(alertDialog);
//            hideNavigationBar(alertDialog.getWindow());
//            clearFocusNotAle(alertDialog.getWindow());
        }
    }


    public void dismissCloseGPSDialog() {
        if (closeGPSDialog != null) {
            closeGPSDialog.dismiss();
        }
    }

    public void dismissClose4GDialog() {
        if (close4GDialog != null) {
            close4GDialog.dismiss();
        }
    }

    public void dismissFormatDialog() {
        if (formatSDCardDialog != null) {
            formatSDCardDialog.dismiss();
        }
    }

    public void dismissDialog() {
        dismissCloseGPSDialog();
        dismissFormatDialog();
        dismissClose4GDialog();
        close4GDialog = null;
        closeGPSDialog = null;
        formatSDCardDialog = null;
    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public void hideNavigationBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                window.getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setDialogTextSize(AlertDialog builder) {
        Button button_negative = builder.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button button_positive = builder.getButton(AlertDialog.BUTTON_POSITIVE);
        button_negative.setTextSize(22);
        button_positive.setTextSize(22);
        builder.getWindow().setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button_positive.getLayoutParams();
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) button_positive.getLayoutParams();
        layoutParams.height = 40;
        layoutParams.width = 45;
        layoutParams.setMargins(0, 0, 5, 0);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.height = 80;
        layoutParams1.width = 90;
        button_negative.setLayoutParams(layoutParams);
        button_positive.setLayoutParams(layoutParams1);
        try {
            //获取mAlert对象
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(builder);

            //获取mTitleView并设置大小颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setPadding(30, 30, 0, 30);
            mTitleView.setTextSize(24);

            //获取mMessageView并设置大小颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setPadding(30, 0, 0, 0);
            mMessageView.setTextSize(20);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast(String keyType, boolean keyState) {
        Intent intent = new Intent(Customer.ACTION_SETTINGS_FUNCTION);
        intent.putExtra("key_type", keyType);
        intent.putExtra("key_state", keyState);
        mContext.sendBroadcast(intent);
    }

}
