package com.bixin.launcher_tw.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.tool.SettingsFunctionTool;
import com.bixin.launcher_tw.model.tool.SharedPreferencesTool;

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
    private boolean isLight = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
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
    }

    @Override
    protected void init() {
        mSettingsUtils = new SettingsFunctionTool();
        mSharedPreferencesTool = new SharedPreferencesTool();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_screen;
    }

    private void initData() {
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
            mSharedPreferencesTool.saveFirstStart();
            return;
        }
        Log.d(TAG, "initScreenOutTime: " + timeType);
        clearScreenMode();
        clearTimeSelected();
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
            isLight = true;
        }
        if (timeType != 4) {
            if (mSharedPreferencesTool.getInt("SCREEN_TYPE", 1) == 0) {
                rbtClose.setChecked(true);
                sendBroadcastToSystemUI("CLOSE_SCREEN");
            } else {
                rbtSaver.setChecked(true);
                sendBroadcastToSystemUI("SAVER");
            }
        }
    }


    /**
     * 根据系统音量更新声音进度条
     */
    private void updateVolume() {
        int volume = mSettingsUtils.getCurrentVolume();
        tvVolume.setText(String.valueOf(volume));
        skbVolume.setProgress(volume);
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
        clearTimeSelected();
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
                mSettingsUtils.setScreenOffTimeOut(60 * 1000);
                rbtTime1.setChecked(true);
                closeAlwaysBright();
                break;
            case R.id.rb_standby_time_3:
                clearTimeSelected();
                mSettingsUtils.setScreenOffTimeOut(180 * 1000);
                rbtTime3.setChecked(true);
                closeAlwaysBright();
                break;
            case R.id.rb_standby_time_5:
                clearTimeSelected();
                mSettingsUtils.setScreenOffTimeOut(300 * 1000);
                rbtTime5.setChecked(true);
                closeAlwaysBright();
                break;
            case R.id.rb_screen_mode_close:
                aa();
                clearScreenMode();
                rbtClose.setChecked(true);
                sendBroadcastToSystemUI("CLOSE_SCREEN");
                mSharedPreferencesTool.saveInt("SCREEN_TYPE", 0);
                break;
            case R.id.rb_screen_mode_light:
                clearScreenMode();
                isLight = true;
                mSharedPreferencesTool.saveBoolean("ALWAYS_BRIGHT", true);
                setMaxScreenOffTimeOut();
                rbtLight.setChecked(true);
                break;
            case R.id.rb_screen_mode_saver:
                aa();
                clearScreenMode();
                sendBroadcastToSystemUI("SAVER");
                rbtSaver.setChecked(true);
                mSharedPreferencesTool.saveInt("SCREEN_TYPE", 0);
                break;
        }
    }

    private void closeAlwaysBright() {
        if (isLight) {
            isLight = false;
            mSharedPreferencesTool.saveBoolean("ALWAYS_BRIGHT", false);
            clearScreenMode();
            rbtSaver.setChecked(true);
        }
    }

    private void aa() {
        if (isLight) {
            isLight = false;
            clearTimeSelected();
            mSharedPreferencesTool.saveBoolean("ALWAYS_BRIGHT", false);
            mSettingsUtils.setScreenOffTimeOut(60 * 1000);
            rbtTime1.setChecked(true);
        }
    }

}
