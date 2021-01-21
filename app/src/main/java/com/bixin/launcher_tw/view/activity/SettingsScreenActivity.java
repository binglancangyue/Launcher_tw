package com.bixin.launcher_tw.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.tool.SettingsFunctionTool;
import com.bixin.launcher_tw.model.tool.SharedPreferencesTool;
import com.bixin.launcher_tw.view.base.BaseAppCompatActivity;

import java.lang.ref.WeakReference;

public class SettingsScreenActivity extends BaseAppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "SettingsScreenActivity";
    private SeekBar skbScreen;
    private SeekBar skbVolume;
    private RadioButton rbtClose;
    private RadioButton rbtSaver;
    private RadioButton rbtLight;
    private RadioButton rbtTime1;
    private RadioButton rbtTime3;
    private RadioButton rbtTime5;
    private SettingsFunctionTool mSettingsUtils;
    private SharedPreferencesTool mSharedPreferencesTool;
    private TextView tvVolume;
    private TextView tvScreen;
    private int colorWhite;
    private int colorDisEnable;
    private MyHandle myHandle;
    private TextView tvTimeTitle;
    public static final String SCREEN_TYPE = "bx_screen_type";
    public static final String SCREEN_TIME = "bx_screen_time";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        getWindow().getDecorView().post(() -> myHandle.post(() -> {
            tvTimeTitle = findViewById(R.id.tv_standby_time_tile);
            skbScreen = findViewById(R.id.skb_screen_brightness);
            skbVolume = findViewById(R.id.skb_volume_adjustment);
            rbtSaver = findViewById(R.id.rb_screen_mode_saver);
            rbtLight = findViewById(R.id.rb_screen_mode_light);
            rbtClose = findViewById(R.id.rb_screen_mode_close);
            rbtTime1 = findViewById(R.id.rb_standby_time_1);
            rbtTime3 = findViewById(R.id.rb_standby_time_3);
            rbtTime5 = findViewById(R.id.rb_standby_time_5);
            tvScreen = findViewById(R.id.tv_screen_value);
            tvVolume = findViewById(R.id.tv_volume_value);
            skbScreen.setOnSeekBarChangeListener(this);
            skbVolume.setOnSeekBarChangeListener(this);
            rbtSaver.setOnClickListener(this);
            rbtLight.setOnClickListener(this);
            rbtClose.setOnClickListener(this);
            rbtTime1.setOnClickListener(this);
            rbtTime3.setOnClickListener(this);
            rbtTime5.setOnClickListener(this);
            initData();
        }));
    }

    @Override
    protected void init() {
        myHandle = new MyHandle(this);
        mSettingsUtils = new SettingsFunctionTool();
        mSharedPreferencesTool = new SharedPreferencesTool(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_screen;
    }

    private void initData() {
        skbVolume.setMax(mSettingsUtils.getMaxValue(SettingsFunctionTool.STREAM_TYPE));
        colorWhite = getResources().getColor(R.color.colorWhite);
        colorDisEnable = getResources().getColor(R.color.colorEnable);
        updateVolume();
        updateBrightness();
        initScreenOutTime();
    }

    private void clearTimeSelected() {
        rbtTime1.setChecked(false);
        rbtTime3.setChecked(false);
        rbtTime5.setChecked(false);
    }

    private void clearScreenMode() {
        rbtClose.setChecked(false);
        rbtSaver.setChecked(false);
        rbtLight.setChecked(false);
    }

    private void initScreenOutTime() {
        int timeType = mSettingsUtils.getScreenOutTime();
        // max ScreenOffTimeOut
        if (mSharedPreferencesTool.isFirstStart()) {
            setMaxScreenOffTimeOut();
            rbtTimeEnable(false);
            mSharedPreferencesTool.saveFirstStart();
            Settings.Global.putInt(getContentResolver(), SCREEN_TYPE, 0);
            return;
        }
        Log.d(TAG, "initScreenOutTime: " + timeType);
        clearScreenMode();
        clearTimeSelected();
        updateRbtTime(timeType);
        if (timeType != 4) {
            int screenType = getScreenType();
            Log.d(TAG, "initScreenType " + screenType);
            if (screenType == 0) {
                rbtClose.setChecked(true);
            } else {
                rbtSaver.setChecked(true);
            }
        }
    }

    private void updateRbtTime(int timeType) {
        if (timeType == 1) {
            rbtTime1.setChecked(true);
            mSettingsUtils.setScreenOffTimeOut(60 * 1000);
        } else if (timeType == 2) {
            mSettingsUtils.setScreenOffTimeOut(180 * 1000);
            rbtTime3.setChecked(true);
        } else if (timeType == 3) {
            mSettingsUtils.setScreenOffTimeOut(300 * 1000);
            rbtTime5.setChecked(true);
        } else {
            mSettingsUtils.setScreenOffTimeOut(Integer.MAX_VALUE);
            rbtLight.setChecked(true);
            rbtTimeEnable(false);
        }
    }

    /**
     * 根据系统音量更新声音进度条
     */
    private void updateVolume() {
        int volume = mSettingsUtils.getCurrentVolume();
        tvVolume.setText(String.valueOf(volume));
        skbVolume.setProgress(volume);
        Log.d(TAG, "updateVolume: " + volume);
    }

    private void updateVolumeByProgress(int value) {
        mSettingsUtils.setVolume(value);
        tvVolume.setText(String.valueOf(value));
    }

    /**
     * 根据系统屏幕亮度值更新亮度进度条
     */
    private void updateBrightness() {
        int brightness = mSettingsUtils.getScreenBrightnessPercentageValue();
        Log.d(TAG, "updateBrightness: brightness " + brightness);
        skbScreen.setProgress(brightness);
        tvScreen.setText(String.valueOf(brightness));
    }

    /**
     * @param value 屏幕亮度值
     */
    private void updateBrightnessByProgress(int value) {
        String progressValue = String.valueOf(value);
        mSettingsUtils.progressChangeToBrightness(value);
        tvScreen.setText(progressValue);
    }

    private void setMaxScreenOffTimeOut() {
//        clearTimeSelected();
        clearScreenMode();
        rbtLight.setChecked(true);
        mSettingsUtils.setScreenOffTimeOut(Integer.MAX_VALUE);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: ");
        int value = seekBar.getProgress();
        if (seekBar.getId() == R.id.skb_screen_brightness) {
            updateBrightnessByProgress(value);
        } else {
            updateVolumeByProgress(value);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewID = v.getId();
        switch (viewID) {
            case R.id.rb_standby_time_1:
                clearTimeSelected();
                Settings.Global.putInt(getContentResolver(), SCREEN_TIME, 1);
                mSettingsUtils.setScreenOffTimeOut(60 * 1000);
                rbtTime1.setChecked(true);
                break;
            case R.id.rb_standby_time_3:
                clearTimeSelected();
                Settings.Global.putInt(getContentResolver(), SCREEN_TIME, 2);
                mSettingsUtils.setScreenOffTimeOut(180 * 1000);
                rbtTime3.setChecked(true);
                break;
            case R.id.rb_standby_time_5:
                clearTimeSelected();
                Settings.Global.putInt(getContentResolver(), SCREEN_TIME, 3);
                mSettingsUtils.setScreenOffTimeOut(300 * 1000);
                rbtTime5.setChecked(true);
                break;
            case R.id.rb_screen_mode_close:
                Log.d(TAG, "onClick: rb_screen_mode_close");
                myHandle.sendEmptyMessage(0);
                clearScreenMode();
                rbtClose.setChecked(true);
                Settings.Global.putInt(getContentResolver(), SCREEN_TYPE, 0);
                clickScreenTypeSetTime();
                break;
            case R.id.rb_screen_mode_light:
                clearScreenMode();
                myHandle.sendEmptyMessage(1);
                setMaxScreenOffTimeOut();
                rbtLight.setChecked(true);
                Settings.Global.putInt(getContentResolver(), SCREEN_TYPE, 0);
                break;
            case R.id.rb_screen_mode_saver:
                myHandle.sendEmptyMessage(0);
                clearScreenMode();
                Settings.Global.putInt(getContentResolver(), SCREEN_TYPE, 1);
                rbtSaver.setChecked(true);
                clickScreenTypeSetTime();
                break;
        }
    }

    private void clickScreenTypeSetTime() {
        try {
            updateRbtTime(Settings.Global.getInt(getContentResolver(), SCREEN_TIME));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getScreenType() {
        return Settings.Global.getInt(getContentResolver(), SCREEN_TYPE, 1);
    }

    private void rbtTimeEnable(boolean enable) {
        rbtTime5.setEnabled(enable);
        rbtTime3.setEnabled(enable);
        rbtTime1.setEnabled(enable);
        int color;
        if (enable) {
            color = colorWhite;
            int type = Settings.Global.getInt(getContentResolver(), SCREEN_TIME, -1);
            if (type == -1) {
                updateRbtTime(1);
            }
            Log.d(TAG, "rbtTimeEnable:type " + type);
        } else {
            color = colorDisEnable;
        }
        tvTimeTitle.setTextColor(color);
        rbtTime5.setTextColor(color);
        rbtTime3.setTextColor(color);
        rbtTime1.setTextColor(color);
    }

    private static class MyHandle extends Handler {
        private SettingsScreenActivity mActivity;

        MyHandle(SettingsScreenActivity activity) {
            WeakReference<SettingsScreenActivity> reference =
                    new WeakReference<>(activity);
            this.mActivity = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mActivity.rbtTimeEnable(false);
            }
            if (msg.what == 0) {
                mActivity.rbtTimeEnable(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
            myHandle = null;
        }
    }
}
