package com.o88o.bluetoothrp8.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class SharedPreferencesUtil {


    /**
     * 保存是否为第一次启动
     */
    public static final String SP_FIRST_LAUNCH_BOOLEAN = "is_first_launch";

    public static final String KEY_IS_GUIDE_PAGE_SHOW = "first_use";

    public static final String DEVICE_UUID = "DEVICE_UUID";
    public static final String DEVICE_MAC = "DEVICE_MAC";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String IS_NEED_CONNECT_BOOLEAN = "IS_NEED_CONNECT";
    public static final String IS_AUTO_CONNECT_BOOLEAN = "IS_AUTO_CONNECT";

    private static final String SP_NAME_RP5 = "RP5";
    private static final String SP_NAME_RP8 = "RP8";
    public static final String DEVICE_MAC_INFO = "DEVICE_MAC_INFO";
    public static final String BRAND_TYPE = "BRAND_TYPE";
    public static final String RP_TYPE = "RP_TYPE";
    public static final String DT_TYPE = "DT_TYPE";
    public static void putStringValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getStringValue(Context context, String key) {
        if (null == context) {
            return "";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
    public static void putRp8StringValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP8, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getRp8StringValue(Context context, String key, String defaultValue) {
        if (null == context) {
            return defaultValue;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP8, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
    public static String getRp8StringValue(Context context, String key) {
        return SharedPreferencesUtil.getRp8StringValue(context,key,"");
    }
    public static void putRpXStringValue(Context context, String spName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                spName, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getRpXStringValue(Context context, String spName, String key,String defaultValue) {
        if (null == context) {
            return "";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                spName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void putRpXIntValue(Context context, String spName, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                spName, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getRpXIntValue(Context context, String spName, String key) {
        if (null == context) {
            return 0;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                spName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void putIntValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getIntValue(Context context, String key) {
        if (null == context) {
            return 0;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
    public static void putRp8IntValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP8, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getRp8IntValue(Context context, String key) {
        if (null == context) {
            return 0;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP8, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void putLongValue(Context context, String key, Long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static long getLongValue(Context context, String key) {
        if (null == context) {
            return 0L;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0L);
    }

    public static void putBooleanValue(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBooleanValue(Context context, String key) {
        if (null == context) {
            return false;
        }

        return getBooleanValue(context, key, false);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        if (null == context) {
            return defaultValue;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SP_NAME_RP5, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

}
