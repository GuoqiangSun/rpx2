package cn.com.startai.newUI;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class WifiSSIDUtil {

    public static int getConnectedWiFiId(Application applicationContext) {
        if (applicationContext == null) {
            return -1;
        }
        WifiManager mWiFiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
        if (mWiFiManager == null) {
            return -1;
        }
        WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
        return connectionInfo != null ? connectionInfo.getNetworkId() : -1;
    }

    public static String getConnectedWiFiSSID(Application applicationContext) {
        if (applicationContext == null) {
            return "unknown";
        }
        WifiManager mWiFiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
        if (mWiFiManager == null) {
            return "unknown";
        }
        WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();
        String ssid = connectionInfo != null ? connectionInfo.getSSID() : "unknown";
        int conNetworkID = connectionInfo != null ? connectionInfo.getNetworkId() : -1;
        String pwd = null;

        // 有些手机获取不到连接的ssid
        if (conNetworkID != -1) {
            List<WifiConfiguration> configuredNetworks = mWiFiManager.getConfiguredNetworks();
            if (configuredNetworks != null && configuredNetworks.size() > 0) {
                for (WifiConfiguration mWifiConfiguration : configuredNetworks) {
                    if (mWifiConfiguration.networkId == conNetworkID) {
                        pwd = mWifiConfiguration.preSharedKey;
                        ssid = mWifiConfiguration.SSID;
                        break;
                    }
                }
            }
        }

        Tlog.v(NetworkManager.TAG, "connectionInfo ssid: " + ssid + " NetworkId: " + conNetworkID + " pwd: " + pwd);

        if (conNetworkID == -1) {
            return "unknown";
        }

        ssid = ssid.replaceAll("\"", "");
        return ssid;
    }


}
