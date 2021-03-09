package com.bixin.launcher_tw.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.listener.OnLocationListener;
import com.bixin.launcher_tw.model.tool.DialogTool;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.SettingsFunctionTool;
import com.bixin.launcher_tw.model.tool.ToastTool;
import com.bixin.launcher_tw.view.base.BaseAppCompatActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class SettingsLinkActivity extends BaseAppCompatActivity implements OnLocationListener.OnUpdateViewListener {
    private DialogTool mDialogTool;
    private TextView tvQR;
    private Switch switchGPS;
    private Switch switch4G;
    private String IMEI = null;
    private String ICCID = null;
    private String SN = null;
    private SettingsFunctionTool mSettingsUtils;
    private int color;
    private MyHandle myHandle;
    private TextView tv4GState;
    private TextView tvGPSState;
    private boolean isOpen4G;
    private boolean isOpenGPS;
    private static final String TAG = "SettingsLinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init() {
        myHandle = new MyHandle(this);
        mDialogTool = new DialogTool(this);
        mSettingsUtils = new SettingsFunctionTool();
        InterfaceCallBackManagement.getInstance().setOnUpdateViewListener(this);
        color = getResources().getColor(R.color.colorChecked);
        getIMEIAndICCID();
        SN = mSettingsUtils.getSN();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_link;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void initView() {
        tvQR = findViewById(R.id.tv_qr);
        tv4GState = findViewById(R.id.tv_4g_state);
        tvGPSState = findViewById(R.id.tv_gps_state);
        tvQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });
        switchGPS = findViewById(R.id.switch_gps);
        switch4G = findViewById(R.id.switch_4g);
        initData();
        switch4G.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged 4G: " + isChecked);
                if (!mSettingsUtils.isHasSimCard()) {
                    switch4G.setChecked(false);
                    ToastTool.showToast(R.string.no_sim_card);
                    return;
                }
                isOpen4G = isChecked;
                if (!isChecked) {
                    mDialogTool.showClose4GDialog(mContext);
                } else {
                    open4G();
                }
                myHandle.sendEmptyMessage(1);
            }
        });
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged GPS: " + isChecked);
                isOpenGPS = isChecked;
                if (!isChecked) {
                    mDialogTool.showCloseGPSDialog(mContext);
                } else {
//                    sendBroadcastToSystemUI("CLOSE_GPS", false);
                    mSettingsUtils.openGPS(true);
                }
                myHandle.sendEmptyMessage(2);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initData() {
        if (!mSettingsUtils.isHasSimCard()) {
            switch4G.setChecked(false);
            isOpen4G = false;
            Log.d(TAG, "initData:not sim " + isOpen4G);
        } else {
            isOpen4G = mSettingsUtils.getDataEnabled();
            Log.d(TAG, "initData:isOpen4G " + isOpen4G);
        }
        isOpenGPS = mSettingsUtils.isGpsOpen();
        switch4G.setChecked(isOpen4G);
        switchGPS.setChecked(isOpenGPS);
        update4GState();
        updateGPSState();
    }

    private void jump() {
        Intent it = new Intent("com.transiot.kardidvr003.qrcode");
        it.putExtra("guid", "kd003"); // guid  String
        it.putExtra("sn", SN); // DVR String
        it.putExtra("imei", IMEI); // IMEI String
        it.putExtra("iccid", ICCID); // Sim ICCID sendBroadcast(it); String
        sendBroadcast(it);
        Log.d(TAG, "send to kardidvr003: IMEI: " + IMEI + " ICCID: " + ICCID + " SN: " + SN);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void updateView(int type, boolean isChecked) {
        Log.d(TAG, "updateView:isChecked " + isChecked);
        if (type == 1) {
            if (isChecked) {
                switchGPS.setChecked(isChecked);
            } else {
                mSettingsUtils.openGPS(false);
            }

        }
        if (type == 2) {
            if (isChecked) {
                switch4G.setChecked(isChecked);
            } else {
                mSettingsUtils.setDataEnabled(false);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void open4G() {
        if (mSettingsUtils.isHasSimCard()) {
            mSettingsUtils.setDataEnabled(true);
        } else {
            switch4G.setChecked(false);
            ToastTool.showToast(R.string.no_sim_card);
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getIMEIAndICCID() {
        TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        IMEI = tm.getImei(0);
        ICCID = tm.getSimSerialNumber();
    }

    private static class MyHandle extends Handler {
        private final SettingsLinkActivity mActivity;

        public MyHandle(SettingsLinkActivity activity) {
            WeakReference<SettingsLinkActivity> weakReference = new WeakReference<>(activity);
            mActivity = weakReference.get();
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 1) {
                mActivity.update4GState();
            }
            if (msg.what == 2) {
                mActivity.updateGPSState();
            }
            if (msg.what == Customer.HANDLE_MESSAGE_CODE) {
                mActivity.hideOrShowNav(true);
            }
            removeMessages(msg.what);
        }
    }

    private void update4GState() {
        if (isOpen4G) {
            tv4GState.setText(R.string.state_on);
            tv4GState.setTextColor(color);
        } else {
            tv4GState.setText(R.string.state_off);
            tv4GState.setTextColor(Color.WHITE);
        }
    }

    private void updateGPSState() {
        Log.d("tag", "updateGPSState:isOpenGPS " + isOpenGPS);
        if (isOpenGPS) {
            tvGPSState.setText(R.string.state_on);
            tvGPSState.setTextColor(color);
        } else {
            tvGPSState.setText(R.string.state_off);
            tvGPSState.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        myHandle.sendEmptyMessageDelayed(Customer.HANDLE_MESSAGE_CODE, Customer.HIDE_NAV_DELAY_MILLIS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialogTool != null) {
            mDialogTool.dismissDialog();
        }
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
            myHandle = null;
        }
    }
}
