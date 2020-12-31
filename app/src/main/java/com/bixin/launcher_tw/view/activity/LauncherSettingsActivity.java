package com.bixin.launcher_tw.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.tool.StartActivityTool;

public class LauncherSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LauncherSettingsActivity";
    private LinearLayout llOther;
    private LinearLayout llADAS;
    private LinearLayout llLink;
    private LinearLayout llScreen;
    private StartActivityTool startActivityTool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        initView();
    }

    private void init() {
        startActivityTool = new StartActivityTool();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        hideOrShowNav(true);
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
        Log.d(TAG, "onStop: ");
    }

}
