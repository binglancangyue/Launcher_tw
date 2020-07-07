package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.bixin.launcher_tw.model.LauncherApp;


/**
 * @author Altair
 * @date :2020.03.31 下午 06:01
 * @description:
 */
public class LocationManagerTool {
    private LocationManager mLocationManager;
    private Location mLocation;
    private static final String TAG = "LocationManagerTool";


    @SuppressLint("MissingPermission")
    public Location getLocation() {
        mLocationManager =
                (LocationManager) LauncherApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager == null) {
            return null;
        }
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation == null) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0,
                mLocationListener);
        Log.d(TAG, "getLocation: " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        return mLocation;
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: ");
            if (location == null) return;
            String strResult = "getAccuracy:" + location.getAccuracy() + "\r\n"
                    + "getAltitude:" + location.getAltitude() + "\r\n"
                    + "getBearing:" + location.getBearing() + "\r\n"
                    + "getElapsedRealtimeNanos:" + String.valueOf(location.getElapsedRealtimeNanos()) + "\r\n"
                    + "getLatitude:" + location.getLatitude() + "\r\n"
                    + "getLongitude:" + location.getLongitude() + "\r\n"
                    + "getProvider:" + location.getProvider() + "\r\n"
                    + "getSpeed:" + location.getSpeed() + "\r\n"
                    + "getTime:" + location.getTime() + "\r\n";
            Log.d("LocationManagerTool", "info: " + strResult);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged provider: " + provider + " status: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
    };

    //停止定位
    public void stopLocation() {
        if (mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void sendToActivity() {
        InterfaceCallBackManagement.getInstance().gpsSpeedChange();
    }
}
