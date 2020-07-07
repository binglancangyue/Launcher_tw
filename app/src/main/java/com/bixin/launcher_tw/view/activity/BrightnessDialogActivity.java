package com.bixin.launcher_tw.view.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bixin.launcher_tw.R;

import java.lang.ref.WeakReference;

/**
 * @author Altair
 * @date :2020.04.17 上午 09:34
 * @description:
 */
public class BrightnessDialogActivity extends Activity {
    private static final String TAG = "Brightness";
    private SeekBar seekBar;
    private Context mContext;
    private final float baseValue = 2.55f;//0-255
    private InnerHandler mInnerHandler;
    private TextView tvCurrent;

    private static class InnerHandler extends Handler {
        private final WeakReference<BrightnessDialogActivity> activityWeakReference;
        private BrightnessDialogActivity mPopupWindow;

        private InnerHandler(BrightnessDialogActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            mPopupWindow = activityWeakReference.get();

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mPopupWindow.progressChangeToBrightness(msg.what);
                mPopupWindow.tvCurrent.setText(msg.arg1 + "");
            }
            if (msg.what == 2) {
                mPopupWindow.tvCurrent.setText(msg.arg1 + "");
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness_dialog);

        Window window = getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (d.getWidth() * 0.9);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        this.mContext = this;
        mInnerHandler = new InnerHandler(this);
        initView();

    }

    private void initView() {
        seekBar = findViewById(R.id.seek_bar_bra);
        tvCurrent = findViewById(R.id.tv_current);
        int value = getScreenBrightnessPercentageValue();
        seekBar.setProgress(value);
        tvCurrent.setText(value + "");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mInnerHandler.sendMessage(getMessage(2, seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mInnerHandler.sendMessage(getMessage(1, seekBar.getProgress()));
            }
        });
    }

    private Message getMessage(int what, int arg1) {
        Message message = Message.obtain();
        message.what = what;
        message.arg1 = arg1;
        return message;
    }


    /**
     * 获得屏幕亮度值
     *
     * @return 系统屏幕亮度值
     */
    public int getScreenBrightness() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int defValue = 125;
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defValue);
    }

    /**
     * 获得屏百分比制幕亮度值
     *
     * @return 百分比值
     */
    public int getScreenBrightnessPercentageValue() {
        double value = (int) (getScreenBrightness() / baseValue);
        return (int) Math.floor(value);
    }

    /**
     * 将百分比值转成亮度值
     *
     * @return
     */
    public int percentageValueChangeToBrightness(int value) {
        return (int) (value * baseValue);
    }

    /**
     * 关闭光感，设置手动调节背光模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC 自动调节屏幕亮度模式值为1
     * SCREEN_BRIGHTNESS_MODE_MANUAL 手动调节屏幕亮度模式值为0
     **/
    private void setScreenManualMode() {
        ContentResolver contentResolver = mContext.getContentResolver();
        int mode;
        try {
            mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "setScreenManualMode Exception: " + e.toString());
        }
    }

    /**
     * 系统值 0-255
     * 修改Setting 中屏幕亮度值
     * 修改Setting的值需要动态申请权限<uses-permission
     * android:name="android.permission.WRITE_SETTINGS"/>
     **/

    public void modifyScreenBrightness(int brightnessValue) {
        // 首先需要设置为手动调节屏幕亮度模式
        setScreenManualMode();
        ContentResolver contentResolver = mContext.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }

    /**
     * 根据seeBar progress 转换后屏幕亮度
     *
     * @param progress seekBar progress
     */
    public void progressChangeToBrightness(int progress) {
        int brightnessValue = (int) Math.ceil(progress * baseValue);
        Log.d(TAG, "progressChangeToBrightness: brightnessValue " + brightnessValue);
        try {
            modifyScreenBrightness(brightnessValue);
        } catch (Exception e) {
            Log.e(TAG, "progressChangeToBrightness Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInnerHandler != null) {
            mInnerHandler.removeCallbacksAndMessages(null);
            mInnerHandler = null;
        }
    }
}
