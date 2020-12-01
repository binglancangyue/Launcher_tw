package com.bixin.launcher_tw.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bixin.launcher_tw.model.bean.Customer;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        this.mContext = this;
        init();
        initView();
    }

    private void hideOrShowNav(boolean isHide) {
        Intent intent = new Intent(Customer.ACTION_HIDE_NAVIGATION);
        intent.putExtra("KEY_HIDE", isHide);
        sendBroadcast(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected abstract void initView();

    protected abstract void init();

    public abstract int getLayoutId();

    public void sendBroadcastToSystemUI(String keyType, boolean state) {
        Intent intent = new Intent(Customer.ACTION_SETTINGS_FUNCTION);
        intent.putExtra("key_type", keyType);
        intent.putExtra("key_state", state);
        sendBroadcast(intent);
    }

    public void sendBroadcastToSystemUI(String keyType, int time) {
        Intent intent = new Intent(Customer.ACTION_SETTINGS_FUNCTION);
        intent.putExtra("key_type", keyType);
        intent.putExtra("key_time", time);
        sendBroadcast(intent);
    }
    public void sendBroadcastToSystemUI(String keyType) {
        Intent intent = new Intent(Customer.ACTION_SETTINGS_FUNCTION);
        intent.putExtra("key_type", keyType);
        sendBroadcast(intent);
    }
    public void sendBroadcastToSystemUI(String keyType, String mode) {
        Intent intent = new Intent(Customer.ACTION_SETTINGS_FUNCTION);
        intent.putExtra("key_type", keyType);
        intent.putExtra("key_mode", mode);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        hideOrShowNav(false);
    }
}
