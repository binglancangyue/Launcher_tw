package com.bixin.launcher_tw.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.tool.DialogTool;
import com.bixin.launcher_tw.model.tool.SharedPreferencesTool;
import com.bixin.launcher_tw.model.tool.StoragePaTool;
import com.bixin.launcher_tw.model.tool.ToastTool;

import java.lang.ref.WeakReference;

public class SettingsOtherActivity extends BaseAppCompatActivity {
    private static final String TAG = "SettingsOtherActivity";
    private Switch switchADAS;
    private TextView tvFormat;
    private TextView tvSystem;
    private DialogTool mDialogTool;
    private RadioButton rbRecordTime1;
    private RadioButton rbRecordTime3;
    private RadioButton rbRecordTime5;
    private TextView tvSensorState;
    private SharedPreferencesTool mSharedPreferencesTool;
    private boolean isOpen = false;
    private int color;
    private MyHandle myHandle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        switchADAS = findViewById(R.id.switch_adas);
        tvFormat = findViewById(R.id.tv_format_sd);
        tvSystem = findViewById(R.id.tv_system_settings);
        rbRecordTime1 = findViewById(R.id.rb_recording_1);
        rbRecordTime3 = findViewById(R.id.rb_recording_3);
        rbRecordTime5 = findViewById(R.id.rb_recording_5);
        tvSensorState = findViewById(R.id.tv_adas_state);
        rbRecordTime1.setOnClickListener(this);
        rbRecordTime3.setOnClickListener(this);
        rbRecordTime5.setOnClickListener(this);
        tvFormat.setOnClickListener(this);
        tvSystem.setOnClickListener(this);
        switchADAS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpen = isChecked;
                myHandle.sendEmptyMessage(1);
                Log.d(TAG, "onCheckedChanged:isChecked " + isChecked);
                sendBroadcastToSystemUI("TYPE_G_SENSOR", isChecked);
                mSharedPreferencesTool.saveBoolean("TYPE_G_SENSOR", isChecked);

            }
        });
        initData();
    }

    @Override
    protected void init() {
        myHandle = new MyHandle(this);
        mDialogTool = new DialogTool(this);
        mSharedPreferencesTool = new SharedPreferencesTool();
        color = getResources().getColor(R.color.colorChecked);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_other;
    }

    private void initData() {
        initRecordTime();
        initGSensorState();
    }

    private void initGSensorState() {
        isOpen = mSharedPreferencesTool.getBoolean("TYPE_G_SENSOR", true);
        switchADAS.setChecked(isOpen);
        updateSenSorState();
    }

    private void initRecordTime() {
        int time = mSharedPreferencesTool.getRecordTime();
        if (time == 1) {
            rbRecordTime1.setChecked(true);
        } else if (time == 3) {
            rbRecordTime3.setChecked(true);
        } else {
            rbRecordTime5.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_format_sd:
                if (StoragePaTool.getStoragePath(true) != null) {
                    mDialogTool.showFormatDialog(mContext);
                } else {
                    ToastTool.showToast(R.string.no_sd_card);
                }
                break;
            case R.id.tv_system_settings:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
            case R.id.rb_recording_1:
                clearRecordingTime();
                mSharedPreferencesTool.saveRecordTime(1);
                rbRecordTime1.setChecked(true);
                sendBroadcastToSystemUI("KEY_RECORD_TIME", 60);
                break;
            case R.id.rb_recording_3:
                clearRecordingTime();
                mSharedPreferencesTool.saveRecordTime(3);
                rbRecordTime3.setChecked(true);
                sendBroadcastToSystemUI("KEY_RECORD_TIME", 180);
                break;
            case R.id.rb_recording_5:
                clearRecordingTime();
                mSharedPreferencesTool.saveRecordTime(5);
                rbRecordTime5.setChecked(true);
                sendBroadcastToSystemUI("KEY_RECORD_TIME", 300);
                break;
        }
    }

    private void clearRecordingTime() {
        rbRecordTime1.setChecked(false);
        rbRecordTime3.setChecked(false);
        rbRecordTime5.setChecked(false);
    }

    private static class MyHandle extends Handler {
        private SettingsOtherActivity mActivity;

        public MyHandle(SettingsOtherActivity activity) {
            WeakReference<SettingsOtherActivity> weakReference = new WeakReference<>(activity);
            mActivity = weakReference.get();
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 1) {
                mActivity.updateSenSorState();
            }
        }
    }

    private void updateSenSorState() {
        if (isOpen) {
            tvSensorState.setText(R.string.state_on);
            tvSensorState.setTextColor(color);
        } else {
            tvSensorState.setText(R.string.state_off);
            tvSensorState.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("activitytest:SettingsOtherActivity", "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("activitytest:SettingsOtherActivity", "onPause: ");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("activitytest:SettingsOtherActivity", "onStop: ");
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
