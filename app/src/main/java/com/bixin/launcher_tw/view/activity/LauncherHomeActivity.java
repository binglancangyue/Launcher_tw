package com.bixin.launcher_tw.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.bean.Customer;
import com.bixin.launcher_tw.model.listener.OnLocationListener;
import com.bixin.launcher_tw.model.receiver.APPReceiver;
import com.bixin.launcher_tw.model.tool.DialogTool;
import com.bixin.launcher_tw.model.tool.FileOperationTool;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.LocationManagerTool;
import com.bixin.launcher_tw.model.tool.RequestPermissionTool;
import com.bixin.launcher_tw.model.tool.SettingsFunctionTool;
import com.bixin.launcher_tw.model.tool.SharePreferencesTool;
import com.bixin.launcher_tw.model.tool.SharedPreferencesTool;
import com.bixin.launcher_tw.model.tool.StartActivityTool;
import com.bixin.launcher_tw.model.tool.StoragePaTool;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.bixin.launcher_tw.view.activity.SettingsScreenActivity.SCREEN_TYPE;

/**
 * @author Altair
 * @date :2020.03.31 下午 06:19
 * @description: main Activity
 */
public class LauncherHomeActivity extends RxAppCompatActivity implements View.OnClickListener,
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
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,};

    private int speeds = -1;
    private RelativeLayout rlBT;
    private RelativeLayout rlVideo;
    private RelativeLayout rlApp;
    private StartActivityTool mStartActivityTool;
    public static final int REQUEST_CAMERA_CODE = 1000;
    public LocationManagerTool mLocationManager;
    private RequestPermissionTool requestPermissionTool;
    private long lastTime = 0;
    private SettingsFunctionTool mSettingsUtils;
    private CompositeDisposable compositeDisposable;
    private SharedPreferencesTool mSharedPreferencesTool;
    private DialogTool mDialogTool;

    @SuppressLint("InflateParams")
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
        Log.d(TAG, "onCreate: ");
        this.mContext = this;
        myHandle = new MyHandle(this);
        initView();
        initData();
    }

    private void sendBroadcast(String name, Boolean value, String action) {
        Intent intent = new Intent(action);
        intent.putExtra(name, value);
        mContext.sendBroadcast(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_camera:
                if (Customer.IS_KD003) {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                } else {
                    sendBroadcast("dvr_camera_front", true, Customer.ACTION_OPEN_DVR_CAMERA);
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR);
//                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR3_TW);
                }
                break;
            case R.id.rl_wifi:
//                mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_AUTONAVI);
                if (Customer.IS_DOUBLE_ROWS) {
                    mStartActivityTool.openAPUI();
                } else if (Customer.IS_KD003) {
                    showSettingWindow();
//                    Intent intent = new Intent(this, LauncherSettingsActivity.class);
//                    startActivity(intent);
                } else {
                    mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_ViDEO_PLAY_BACK);
                }
                break;
            case R.id.rl_file:
                Log.d(TAG, "onClick:rl_file ");
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
        hideOrShowNav(false);
    }

    private void hideOrShowNav(boolean isHide) {
        Intent intent = new Intent(Customer.ACTION_HIDE_NAVIGATION);
        intent.putExtra("KEY_HIDE", isHide);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyDown: keyCode " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class MyHandle extends Handler {
        private final LauncherHomeActivity mActivity;

        MyHandle(LauncherHomeActivity activity) {
            WeakReference<LauncherHomeActivity> reference =
                    new WeakReference<>(activity);
            this.mActivity = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mActivity.updateGpsSpeed(String.valueOf(msg.arg1));
                    break;
                case 2:
                    mActivity.startDVRService();
                    break;
                case 3:
                    mActivity.mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR3_TW);
                    mActivity.startUploadService();
                    mActivity.checkPhoneNumber();
                    mActivity.initMaxScreenTime();
                    break;
                case 4:
                    mActivity.sendStateCode();
                    break;
                case 5:
                    mActivity.mSettingsUtils.setDataEnabled(true);
                    break;
                case 6:
                    mActivity.mStartActivityTool.startValidationTools();
                    mActivity.checkDVR003File();
                    break;
            }
            removeMessages(msg.what);
        }
    }

    private void sendStateCode() {
        Intent it = new Intent(Customer.ACTION_TW_STATE);
        it.putExtra("machineState", 0);
        sendBroadcast(it);
//        Intent intent = new Intent("com.transiot.kardidvr003.healthcheck");
//        sendBroadcast(intent);
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
    public void gpsSpeedChanged(int speed) {
        if (speeds == speed) {
            return;
        }
        Log.d(TAG, "gpsSpeedChanged: " + speed);
        if (speed <= 5) {
            speed = 0;
        } else {
            speed += 3;
        }
        Message message = Message.obtain();
        message.what = 1;
        message.arg1 = speed;
        speeds = speed;
        myHandle.sendMessage(message);
    }

    @Override
    public void changeAirMode() {
        if (isRestartAirMode()) {
            mSettingsUtils.setDataEnabled(false);
            myHandle.sendEmptyMessageAtTime(5, 2000);
        }
    }

    private boolean isRestartAirMode() {
        long currentTime = SystemClock.uptimeMillis();
        if ((currentTime - lastTime) >= 1800000) {
            lastTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    private void registerAppReceiver() {
//        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
//        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
//        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Customer.ACTION_TXZ_CUSTOM_COMMAND);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addDataScheme("package");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Customer.ACTION_TW_STATE);
        registerReceiver(mReceiver, filter);
    }

    private void registerMobileDataReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @SuppressLint("NewApi")
    private void initData() {
        getWindow().getDecorView().post(() -> myHandle.post(() -> {
            InterfaceCallBackManagement.getInstance().setOnLocationListener(this);
            mStartActivityTool = new StartActivityTool();
            mSettingsUtils = new SettingsFunctionTool();
            mLocationManager = new LocationManagerTool();
            mDialogTool = new DialogTool(this);
            compositeDisposable = new CompositeDisposable();
            mSharedPreferencesTool = new SharedPreferencesTool(this);
            mReceiver = new APPReceiver();
            requestPermissionTool = new RequestPermissionTool(mContext);
            requestPermissionTool.initPermission(permissions, this);
            registerAppReceiver();
            myHandle.sendEmptyMessageDelayed(2, 4000);
            myHandle.sendEmptyMessageDelayed(3, 10000);
            myHandle.sendEmptyMessageDelayed(4, 12000);
            myHandle.sendEmptyMessageDelayed(6, 14000);
            setSelectedApnKey("23");
        }));
    }

    private void initMaxScreenTime() {
        if (mSharedPreferencesTool.isFirstStart()) {
            mSettingsUtils.setScreenOffTimeOut(Integer.MAX_VALUE);
            Settings.Global.putInt(getContentResolver(), SCREEN_TYPE, 0);
            mSettingsUtils.setVolume(12, AudioManager.STREAM_MUSIC);
            mSettingsUtils.setVolume(mSettingsUtils.getMaxValue(AudioManager.STREAM_SYSTEM),
                    AudioManager.STREAM_SYSTEM);
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
//                mLocationManager.getLocation();
                mLocationManager.startGaoDe();
            }
        }
    }

    @SuppressLint("NewApi")
    private void startDVRService() {
        Intent intent = new Intent();
//        String packageName = "com.bx.carDVR";
//        String className = "com.bx.carDVR.DVRService";
//        intent.setComponent(new ComponentName(packageName, className));
//        intent.setPackage(packageName);
//        mContext.startService(intent);
        Intent launchIntent = mContext.getPackageManager()
                .getLaunchIntentForPackage(Customer.PACKAGE_NAME_DVR);
        if (launchIntent == null) {
            myHandle.sendEmptyMessageDelayed(2, 1000);
        } else {
            mContext.startActivity(launchIntent);
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

    @SuppressLint("NewApi")
    private void startUploadService() {
        Log.d(TAG, "startUploadService: ");
        Intent intent = new Intent();
        String packageName = "com.zsi.powervideo";
        String className = "com.zsi.powervideo.service.AwakenService";
        intent.setComponent(new ComponentName(packageName, className));
        mContext.startForegroundService(intent);
//        mStartActivityTool.launchAppByPackageName(Customer.PACKAGE_NAME_DVR3_TW);
    }

    private void showSettingWindow() {
        int dvrState = Settings.Global.getInt(getContentResolver(), Customer.CAMERA_RECORD_STATUS, 0);
        if (dvrState == 1) {
            mDialogTool.showStopRecordDialog(mContext);
        } else {
            Intent intent = new Intent(this, LauncherSettingsActivity.class);
            startActivity(intent);
//            Intent intent = new Intent(Customer.ACTION_SHOW_SETTING_WINDOW);
//            intent.putExtra("isLauncher", false);
//            sendBroadcast(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @SuppressLint("CheckResult")
    private void checkPhoneNumber() {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mStartActivityTool.getAllContacts();
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object aBoolean) throws Exception {

                    }
                });
    }

    private long startTime;

    private void copyDVR003File(String copyPath, String targetPath) {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                startTime = SystemClock.uptimeMillis();
                boolean is = FileOperationTool.copyAll(copyPath, targetPath);
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        int time = (int) ((SystemClock.uptimeMillis() - startTime) / 1000);
                        Log.d(TAG, "copyDVR003File: " + aBoolean + " time " + time);
                    }
                }));
    }

    private void checkDVR003File() {
        if (!Customer.IS_SUPPORT_COPY_FILE) {
            return;
        }
        String storagePath = StoragePaTool.getStoragePath(false);
        Log.d(TAG, "checkDVR003File: " + storagePath);
        String path = storagePath + "/Download/here_offline_cache";
        File file = new File(path);
        if (!file.exists()) {
            String targetPath = storagePath + "/Download/here_offline_cache";
            String copyPath = "/system/etc/KD003_info/here_offline_cache";
            Log.d(TAG, "checkDVR003File: copyPath " + copyPath + "\n targetPath " + targetPath);
            copyDVR003File(copyPath, targetPath);
        } else {
            Log.d(TAG, "checkDVR003File: the file exists");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        SharePreferencesTool.getInstance().saveBoolean("isFirstStart", true);
        if (myHandle != null) {
            myHandle.removeCallbacksAndMessages(null);
            myHandle = null;
        }
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mLocationManager != null) {
            mLocationManager.stopLocation();
            mLocationManager.stopGaoDeLocation();
        }
        if (mDialogTool != null) {
            mDialogTool.dismissDialog();
        }
    }

    private void test() {
//        MediaPlayer player;
//        player = MediaPlayer.create(this, R.raw.smart_drive_detect);
////            player.prepare();
//        player.start();
    }

    private void setSelectedApnKey(String key) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put("apn_id", key);
        resolver.update(Uri.parse("content://telephony/carriers/preferapn/subId/1"), values, null, null);
    }
}