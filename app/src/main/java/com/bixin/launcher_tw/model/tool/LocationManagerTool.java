package com.bixin.launcher_tw.model.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.bixin.launcher_tw.model.LauncherApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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
        mLocationManager.requestLocationUpdates(getProviderName(), 2000, 0,
                mLocationListener);
        Log.d(TAG, "getLocation: " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        return mLocation;
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: ");
            if (location == null) return;
            String strResult = "\r\n"
                    + "准确度:" + location.getAccuracy() + "\r\n"
                    + "海拔:" + location.getAltitude() + "\r\n"
                    + "方位:" + location.getBearing() + "\r\n"
                    + "getElapsedRealtimeNanos:" + String.valueOf(location.getElapsedRealtimeNanos()) + "\r\n"
                    + "纬度:" + location.getLatitude() + "\r\n"
                    + "经度:" + location.getLongitude() + "\r\n"
                    + "供应商:" + location.getProvider() + "\r\n"
                    + "速度:" + location.getSpeed() + "\r\n"
                    + "时间:" + getTimeByGPS(location.getTime()) + "\r\n";
            Log.d("LocationManagerTool", strResult);
            sendToActivity((int) location.getSpeed());
//            getDetailedAddress(location);
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

    private void sendToActivity(int speed) {
        InterfaceCallBackManagement.getInstance().gpsSpeedChange(speed);
    }


    @SuppressLint("SimpleDateFormat")
    private static String getTimeByGPS(long gpsTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gpsTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return df.format(calendar.getTime());
    }

    /**
     * 获取 Location Provider
     *
     * @return
     */
    private String getProviderName() {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(true);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(true);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
        // provider
        return mLocationManager.getBestProvider(criteria, true);
    }


    private String parseAddress(Address address) {
        return address.getAddressLine(0) + address.getAddressLine(1)
                + address.getAddressLine(2) + address.getFeatureName();
    }

    // 获取地址信息
    private List<Address> getAddressByGeoPoint(Location location) {
        List<Address> result = null;
        // 先将 Location 转换为 GeoPoint
        // GeoPoint gp =getGeoByLocation(location);
        try {
            if (location != null) {
                // 获取 Geocoder ，通过 Geocoder 就可以拿到地址信息
                Geocoder gc = new Geocoder(LauncherApp.getInstance(), Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getDetailedAddress(Location location) {
        List<Address> addressList = getAddressByGeoPoint(location);
        String address = "当前详细地址：";
        if (addressList != null && !addressList.isEmpty()) {
            address += parseAddress(addressList.get(0));
            Log.d("LocationManagerTool", "当前详细地址: " + address);
        }
    }

}
