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
    private OnLocationListener.OnUpdateViewListener onUpdateViewListener;

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

    public void setOnUpdateViewListener(OnLocationListener.OnUpdateViewListener listener) {
        this.onUpdateViewListener = listener;
    }

    public void updateView(int type, boolean isCheck) {
        if (onUpdateViewListener != null) {
            onUpdateViewListener.updateView(type, isCheck);
        }
    }

    public void changeAirMode() {
        if (mOnLocationListener != null) {
            mOnLocationListener.changeAirMode();
        }
    }

}
