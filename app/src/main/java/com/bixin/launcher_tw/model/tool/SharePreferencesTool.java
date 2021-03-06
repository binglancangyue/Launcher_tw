package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bixin.launcher_tw.model.LauncherApp;

import static com.bixin.launcher_tw.model.bean.Customer.SP_NAME;

/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: SharedPreferences 工具
 */
public class SharePreferencesTool {
    private static SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    @SuppressLint("CommitPrefEdits")
    public SharePreferencesTool() {
        this.mContext = LauncherApp.getInstance();
        this.mPreferences = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    private static class SingletonHolder {
        private static final SharePreferencesTool INSTANCE = new SharePreferencesTool();
    }

    public static SharePreferencesTool getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }

    public SharedPreferences getSharePreferences() {
        return mPreferences;
    }

    public void saveString(String name, String value) {
        mEditor.putString(name, value);
        mEditor.apply();
    }

    public void saveInt(String name, int value) {
        mEditor.putInt(name, value);
        mEditor.apply();
    }

    public void saveBoolean(String name, boolean value) {
        mEditor.putBoolean(name, value);
        mEditor.apply();
    }

    public boolean getBoolean(String name, boolean isTrue) {
        return mPreferences.getBoolean(name, isTrue);
    }
}