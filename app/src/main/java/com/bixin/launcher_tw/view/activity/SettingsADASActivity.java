package com.bixin.launcher_tw.view.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.view.base.BaseAppCompatActivity;

public class SettingsADASActivity extends BaseAppCompatActivity {
    private static final String TAG = "SettingsADASActivity";
    private RadioButton rbLimitClose;
    private RadioButton rbLimitOriginal;
    private RadioButton rbLimit5;
    private RadioButton rbLimit10;
    private RadioButton rbWarningClose;
    private RadioButton rbWarning5;
    private RadioButton rbWarning10;
    private RadioButton rbWarningModeAuto;
    private RadioButton rbWarningModeBeep;
    private RadioButton rbWarningModeFlickers;
    private Switch switchWarning;
    private Switch switchDrivingDetection;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void init() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_adas_test;
    }

    @Override
    protected void initView() {
        rbLimitClose = findViewById(R.id.rb_limit_close);
        rbLimitOriginal = findViewById(R.id.rb_limit_0);
        rbLimit5 = findViewById(R.id.rb_limit_5);
        rbLimit10 = findViewById(R.id.rb_limit_10);
        rbWarningClose = findViewById(R.id.rb_warning_interval_close);
        rbWarning5 = findViewById(R.id.rb_warning_interval_5);
        rbWarning10 = findViewById(R.id.rb_warning_interval_10);
        rbWarningModeAuto = findViewById(R.id.rb_warning_mode_wisdom);
        rbWarningModeBeep = findViewById(R.id.rb_warning_mode_beep);
        rbWarningModeFlickers = findViewById(R.id.rb_warning_mode_picture_flickers);
        switchWarning = findViewById(R.id.switch_speed);
        switchDrivingDetection = findViewById(R.id.switch_driving_detection);
//        tvDrivingDetectionState = findViewById(R.id.tv_driving_detection_state);
//        tvWarningState = findViewById(R.id.tv_adas_state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }
}
