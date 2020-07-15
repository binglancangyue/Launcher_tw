package com.bixin.launcher_tw.model.tool;

import com.bixin.launcher_tw.model.listener.OnAppUpdateListener;
import com.bixin.launcher_tw.model.listener.OnLocationListener;

/**
 * @author Altair
 * @date :2020.04.01 上午 10:23
 * @description:
 */
public class InterfaceCallBackManagement {
    private OnLocationListener mOnLocationListener;
    private OnAppUpdateListener mOnAppUpdateListener;

    public static InterfaceCallBackManagement getInstance() {
        return SingleHolder.management;
    }

    public static class SingleHolder {
        static InterfaceCallBackManagement management = new InterfaceCallBackManagement();
    }

    public void setOnAppUpdateListener(OnAppUpdateListener onAppUpdateListener) {
        this.mOnAppUpdateListener = onAppUpdateListener;
    }

    public void setOnLocationListener(OnLocationListener listener) {
        this.mOnLocationListener = listener;
    }

    public void gpsSpeedChange(int speed) {
        if (mOnLocationListener == null) {
            return;
        }
        mOnLocationListener.gpsSpeedChanged(speed);
    }

    public void updateAppList() {
        if (mOnAppUpdateListener != null) {
            mOnAppUpdateListener.updateAppList();
        }
    }

}
