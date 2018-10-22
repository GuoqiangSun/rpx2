package com.o88o.bluetoothrp8.util;

import android.content.Context;

/**
 * Created by SEELE on 2018/2/9.
 */

public class Rp8DeviceUtils {
    public static void saveDeviceInfo(Context context, String uuid, String mac, String macInfo, String name ){
        if(null != uuid && !uuid.isEmpty()) {
            SharedPreferencesUtil.putRp8StringValue(context, SharedPreferencesUtil.DEVICE_UUID, uuid);
        }
        if(null != mac && !mac.isEmpty()) {
            SharedPreferencesUtil.putRp8StringValue(context, SharedPreferencesUtil.DEVICE_MAC, mac);
        }
        if(null != macInfo && !macInfo.isEmpty()) {
            SharedPreferencesUtil.putRp8StringValue(context, SharedPreferencesUtil.DEVICE_MAC_INFO, macInfo);
        }
        if(null != name && !name.isEmpty()) {
            SharedPreferencesUtil.putRp8StringValue(context, SharedPreferencesUtil.DEVICE_NAME, name);
        }
    }
    public static void saveDeviceInfo(Context context, String macInfo, String name ){
        saveDeviceInfo(context,null,null,macInfo,name);
    }
    public static void saveDeviceUuid(Context context, String uuid ){
        saveDeviceInfo(context,uuid,null,null,null);
    }
    public static void saveDeviceMac(Context context, String mac ){
        if(null != mac && !mac.isEmpty()) {
            SharedPreferencesUtil.putRp8StringValue(context, SharedPreferencesUtil.DEVICE_MAC, mac);
        }
    }
    public static String getDeviceUuid(Context context){
        return SharedPreferencesUtil.getRp8StringValue(context,SharedPreferencesUtil.DEVICE_UUID);
    }
    public static String getDeviceMac(Context context){
        return SharedPreferencesUtil.getRp8StringValue(context,SharedPreferencesUtil.DEVICE_MAC);
    }
    public static String getDeviceMacInfo(Context context){
        return SharedPreferencesUtil.getRp8StringValue(context,SharedPreferencesUtil.DEVICE_MAC_INFO);
    }
    public static String getDeviceName(Context context){
        return SharedPreferencesUtil.getRp8StringValue(context,SharedPreferencesUtil.DEVICE_NAME);
    }

    public static void saveRpType(Context context, String RpType) {
        SharedPreferencesUtil.putRp8StringValue(context,SharedPreferencesUtil.RP_TYPE,RpType);
    }

    public static String getRpType(Context context, String defaultValue) {
        return SharedPreferencesUtil.getRp8StringValue(context,SharedPreferencesUtil.RP_TYPE,defaultValue);
    }
    public static String getRpXStringValue(Context context, String key,String defaultValue) {

        return SharedPreferencesUtil.getRpXStringValue(context,SharedPreferencesUtil.RP_TYPE, key,defaultValue);
    }

    public static void putRpXStringValue(Context context, String key, String value) {
        SharedPreferencesUtil.putRpXStringValue(context,SharedPreferencesUtil.RP_TYPE, key,value);
    }

}
