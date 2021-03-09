package com.bixin.launcher_tw.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.tool.StartActivityTool;

import java.lang.ref.WeakReference;

public class LauncherSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LauncherSettingsActivity.class.getName();
    private LinearLayout llOther;
    private LinearLayout llADAS;
    private LinearLayout llLink;
    private LinearLayout llScreen;
    private StartActivityTool startActivityTool;
    private MyHandle myHandle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        initView();
    }

    private void init() {
        startActivityTool = new StartActivityTool();
        myHandle = new MyHandle(this);
    }

    private void initView() {
        llOther = findViewById(R.id.ll_other);
        llADAS = findViewById(R.id.ll_adas);
        llLink = findViewById(R.id.ll_link);
        llScreen = findViewById(R.id.ll_screen);
        llOther.setOnClickListener(this);
        llADAS.setOnClickListener(this);
        llLink.setOnClickListener(this);
        llScreen.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_screen:
                startActivityTool.jumpToActivity(this, SettingsScreenActivity.class);
                break;
            case R.id.ll_other:
                startActivityTool.jumpToActivity(this, SettingsOtherActivity.class);
                break;
            case R.id.ll_adas:
                Intent it = new Intent("com.transiot.kardidvr003.driving.setting");
                sendBroadcast(it);
//                startActivityTool.jumpToActivity(this, SettingsADASActivity.class);
                break;
            case R.id.ll_link:
                startActivityTool.jumpToActivity(this, SettingsLinkActivity.class);
                break;
        }
    }

    private void hideOrShowNav(boolean isHide) {
        Intent intent = new Intent(Customer.ACTION_HIDE_NAVIGATION);
        intent.putExtra("KEY_HIDE", isHide);
        sendBroadcast(intent);
    }

    private static class MyHandle extends Handler {
        private LauncherSettingsActivity mActivity;

        MyHandle(LauncherSettingsActivity activity) {
            WeakReference<LauncherSettingsActivity> reference =
                    new WeakReference<>(activity);
            this.mActivity = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Customer.HANDLE_MESSAGE_CODE) {
                mActivity.hideOrShowNav(true);
            }
            removeMessages(msg.what);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        hideOrShowNav(true);
        myHandle.sendEmptyMessageDelayed(Customer.HANDLE_MESSAGE_CODE, Customer.HIDE_NAV_DELAY_MILLIS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideOrShowNav(false);
        startActivityTool = null;
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
        }
        Log.d(TAG, "onStop: ");
    }

}
