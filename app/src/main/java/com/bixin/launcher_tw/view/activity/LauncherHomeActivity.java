package com.bixin.launcher_tw.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.listener.OnLocationListener;
import com.bixin.launcher_tw.model.receiver.APPReceiver;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.LocationManagerTool;
import com.bixin.launcher_tw.model.tool.RequestPermissionTool;
import com.bixin.launcher_tw.model.tool.StartActivityTool;

import java.lang.ref.WeakReference;

/**
 * @author Altair
 * @date :2020.03.31 下午 06:19
 * @description: main Activity
 */
public class LauncherHomeActivity extends AppCompatActivity implements View.OnClickListener,
        OnLocationListener {
    private Context mContext;
    private MyHandle myHandle;
    private TextView tvSpeed;
    private APPReceiver mReceiver;
    private static final String TAG = "LauncherHomeActivity";
    private RelativeLayout rlCamera;
    private RelativeLayout rlWIFI;
    private RelativeLayout rlFile;
    private TextView tvSpeedLimit;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private RelativeLayout rlBT;
    private RelativeLayout rlVideo;
    private RelativeLayout rlApp;
    private StartActivityTool mStartActivityTool;
    public static final int REQUEST_CAMERA_CODE = 1000;
    public LocationManagerTool mLocationManager;
    private RequestPermissionTool requestPermissionTool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        if (Customer.IS_DOUBLE_ROWS) {
            view = getLayoutInflater().inflate(R.layout.activity_home_double, null);
        } else if (Customer.IS_KD003) {
            view = getLayoutInflater().inflate(R.layout.activity_home_kd003, null);
        } else {
            view = getLayoutInflater().inflate(R.layout.activity_home, null);
        }
        setContentView(view);
        this.mContext = this;
        myHandle = new MyHandle(this);
        InterfaceCallBackManagement.getInstance().setOnLocationListener(this);
        mStartActivityTool = new StartActivityTool();
        mLocationManager = new LocationManagerTool();
        requestPermissionTool = new RequestPermissionTool(mContext);
        initView();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_camera:
//                    sendTest("adb shell input keyevent  26");
//                sendKeyCode(KeyEvent.KEYCODE_POWER);

                if (Customer.IS_KD003) {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                } else {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR);
                }
                break;
            case R.id.rl_wifi:
//                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_AUTONAVI);
                if (Customer.IS_DOUBLE_ROWS) {
                    mStartActivityTool.openAPUI();
                } else if (Customer.IS_KD003) {
                    showSettingWindow();
                } else {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                }
                break;
            case R.id.rl_file:
                if (Customer.IS_DOUBLE_ROWS) {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_FILE_MANAGER);
                } else {
//                    Intent intent = new Intent(Intent.ACTION_DIAL);
//                    startActivity(intent);
                    showSettingWindow();
                }
                break;
            case R.id.rl_bt:
                Intent bt = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(bt);
//                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_BLUETOOTH);
                break;
            case R.id.rl_video:
                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                break;
            case R.id.rl_app:
                Intent intent = new Intent(this, AppListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        return super.onKeyUp(keyCode, event);
//    }

    private static class MyHandle extends Handler {
        private LauncherHomeActivity mActivity;

        MyHandle(LauncherHomeActivity activity) {
            WeakReference<LauncherHomeActivity> reference =
                    new WeakReference<>(activity);
            this.mActivity = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mActivity.updateGpsSpeed((String) msg.obj);
            }
        }
    }


    private void updateGpsSpeed(String s) {
        tvSpeed.setText(s);
    }

    private void initView() {
        tvSpeed = findViewById(R.id.tv_gps_speed);
        rlCamera = findViewById(R.id.rl_camera);
        rlWIFI = findViewById(R.id.rl_wifi);
        rlCamera.setOnClickListener(this);
        rlWIFI.setOnClickListener(this);
//        rlFile = findViewById(R.id.rl_file);
//        rlFile.setOnClickListener(this);
        if (Customer.IS_DOUBLE_ROWS) {
            rlFile = findViewById(R.id.rl_file);
            rlBT = findViewById(R.id.rl_bt);
            rlVideo = findViewById(R.id.rl_video);
            rlApp = findViewById(R.id.rl_app);
            rlFile.setOnClickListener(this);
            rlBT.setOnClickListener(this);
            rlVideo.setOnClickListener(this);
            rlApp.setOnClickListener(this);
        }

        if (Customer.IS_KD003) {
            tvSpeedLimit = findViewById(R.id.tv_speed_limit);
        }

    }

    @Override
    public void gpsSpeedChanged() {
        myHandle.sendEmptyMessage(1);
    }


    private void registerAppReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Customer.ACTION_TXZ_CUSTOM_COMMAND);
        filter.addDataScheme("package");
        registerReceiver(mReceiver, filter);
    }

    @SuppressLint("NewApi")
    private void initData() {
        getWindow().getDecorView().post(() -> myHandle.post(() -> {
//            LauncherApp.getInstance().initAppList();
            mReceiver = new APPReceiver();
            startDVRService();
//            checkPermission();
            requestPermissionTool.initPermission(permissions, this);
            registerAppReceiver();
            startUploadService();
        }));
    }

    private void startDVRService() {
        Intent intent = new Intent();
        String packageName = "com.bx.carDVR";
        String className = "com.bx.carDVR.DVRService";
        intent.setComponent(new ComponentName(packageName, className));
        mContext.startService(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_CODE);
        } else {
            mLocationManager.getLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission Granted
//                try {
//                    mLocationManager.getLocation();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (REQUEST_CAMERA_CODE == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                requestPermissionTool.showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            } else {
                //全部权限通过，可以进行下一步操作。。。
                mLocationManager.getLocation();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startVoiceRecognitionService() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String packageName = "com.bixin.speechrecognitiontool";
        String className = "com.bixin.speechrecognitiontool.SpeechRecognitionService";
        intent.setClassName(packageName, className);
        mContext.startForegroundService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startUploadService() {
        Intent intent = new Intent();
        String packageName = "com.zsi.powervideo";
        String className = "com.zsi.powervideo.service.AwakenService";
        intent.setComponent(new ComponentName(packageName, className));
        mContext.startService(intent);
//        mStartActivityTool.launchAppByPackageName("com.zsi.powervideo");
    }

    private void showSettingWindow() {
        Intent intent = new Intent(Customer.ACTION_SHOW_SETTING_WINDOW);
        intent.putExtra("isLauncher", false);
        sendBroadcast(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
            myHandle = null;
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mLocationManager != null) {
            mLocationManager.stopLocation();
        }
    }


}