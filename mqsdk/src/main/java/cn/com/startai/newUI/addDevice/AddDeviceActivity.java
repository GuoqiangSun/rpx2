package cn.com.startai.newUI.addDevice;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.List;

import cn.com.startai.esptouchsender.IEsptouchResult;
import cn.com.startai.esptouchsender.customer.EsptouchAsyncTask;
import cn.com.startai.esptouchsender.customer.MyEsptouchListener;
import cn.com.startai.mqsdk.R;
import cn.com.startai.newUI.AirkissHelper;
import cn.com.startai.newUI.NewUIBaseActivity;
import cn.com.startai.newUI.WifiSSIDUtil;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class AddDeviceActivity extends NewUIBaseActivity
        implements MainFragment.IYesBtnCallBack, ConfigRouteFragment.IConfimBtnCallBack
        , NoNetworkIDFragment.IOkBtnCallBack, TimeoutFragment.IRetryBtnCallBack {


    private final SparseArray<BaseFragment> mFragments = new SparseArray<>(5);
    private static final int ID_HOME = 0x00; // 首页

    private void showHomePage() {

        if (mInfoImg != null) {
            mInfoImg.setBackgroundResource(R.mipmap.yakpower_press);
        }
        if (mInfoTxt != null) {
            mInfoTxt.setText(R.string.add_device_info);
        }
        replace(ID_HOME);
    }

    private static final int ID_CONFIG_ROUTE = 0x01; // 配置路由

    private void showConfigRouterPage() {

        if (mInfoImg != null) {
            mInfoImg.setBackgroundResource(R.mipmap.yakpower_none);
        }
        if (mInfoTxt != null) {
            mInfoTxt.setText(R.string.add_device_info_input_pwd);
        }
        replace(ID_CONFIG_ROUTE);
    }


    private static final int ID_CONFIG_ROUTE_ING = 0x02;// 配网中...

    private void showConfigingPage() {

        if (mInfoImg != null) {
            mInfoImg.setBackgroundResource(R.mipmap.yakpower_flash);
        }
        if (mInfoTxt != null) {
            mInfoTxt.setText(R.string.add_device_info_writing);
        }
        replace(ID_CONFIG_ROUTE_ING);
    }

    private static final int ID_CONFIG_NO_ID = 0x03;// 配网失败,无wifiAp

    private void showConfigFailPage() {

        if (mInfoImg != null) {
            mInfoImg.setBackgroundResource(R.mipmap.yakpower_flash);
        }
        if (mInfoTxt != null) {
            mInfoTxt.setText(R.string.add_device_info);
        }
        replace(ID_CONFIG_NO_ID);

    }


    private static final int ID_CONFIG_TIME_OUT = 0x04;// 配网失败,超时

    private void showConfigTimeoutPage() {
        if (mInfoImg != null) {
            mInfoImg.setBackgroundResource(R.mipmap.yakpower_none);
        }
        if (mInfoTxt != null) {
            mInfoTxt.setText(R.string.add_device_info_fail);
        }
        replace(ID_CONFIG_TIME_OUT);
    }

    private int mCurPage;

    private void replace(int page) {

        if (mFragments.get(page) == null) {
            switch (page) {
                case ID_HOME:
                    mFragments.put(page, new MainFragment());
                    break;
                case ID_CONFIG_ROUTE:
                    mFragments.put(page, new ConfigRouteFragment());
                    break;
                case ID_CONFIG_ROUTE_ING:
                    mFragments.put(page, new ConfigingFragment());
                    break;
                case ID_CONFIG_NO_ID:
                    mFragments.put(page, new NoNetworkIDFragment());
                    break;
                case ID_CONFIG_TIME_OUT:
                    mFragments.put(page, new TimeoutFragment());
                    break;
                default:
                    page = ID_HOME;
                    if (mFragments.get(page) == null) {
                        mFragments.put(page, new MainFragment());
                    }
                    break;
            }

        }
        mCurPage = page;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, mFragments.get(page), String.valueOf(page));
//        fragmentTransaction.show(mFragments.get(page));
        fragmentTransaction.commit();

    }


    private ImageView mInfoImg;
    private WifiManager mWiFiManager;
    private TextView mInfoTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_newui_home_add);

        mInfoImg = findViewById(R.id.yakpower_img);
        mInfoTxt = findViewById(R.id.info_txt);

        Application application = (Application) this.getApplicationContext();
        mWiFiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);

        showHomePage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mCurPage == ID_CONFIG_ROUTE_ING) {
            stopConfigureWiFi();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragments.clear();
    }

    @Override
    public void onYes() {

        int connectedWiFiId = WifiSSIDUtil.getConnectedWiFiId(this.getApplication());

        if (connectedWiFiId != -1) {
            showConfigRouterPage();
        } else {
            showConfigFailPage();
        }

    }


    @Override
    public void onOk() {
        showHomePage();
    }

    @Override
    public void onRetry() {
        showHomePage();
    }

    private String TAG = "abc";

    @Override
    public void onConfirm(String ssid, String pwd) {
        boolean b = configureWiFi(ssid, pwd);
        if (b) {
            showConfigingPage();
        } else {
            showConfigFailPage();
        }
    }

    private long startT;
    private final long AIR_KISS_TIME_OUT = 1000 * 90;
    //    private final String ARI_KISS_ASE_KEY = "";
    private EsptouchAsyncTask mTask;

    public boolean configureWiFi(String ssid1, final String pwd) {

        if (ssid1 != null) {
            ssid1 = ssid1.replaceAll("\"", "");
        }
        final String ssid = ssid1;

        startT = System.currentTimeMillis();


        final String conSSID = "\"" + ssid + "\"";
        String bssid = "";

        WifiInfo connectionInfo = mWiFiManager.getConnectionInfo();

        if (connectionInfo.getNetworkId() == -1) {
            Tlog.e(TAG, " networkID=-1");
            return false;
        }

        bssid = connectionInfo.getBSSID();
        Tlog.v(TAG, "connectionInfo bssid " + bssid + " id:" + connectionInfo.getNetworkId()
                + " conSSID:" + connectionInfo.getSSID() + " configSSID:" + conSSID);

        if (bssid == null || !connectionInfo.getSSID().equalsIgnoreCase(conSSID)) {
            List<WifiConfiguration> configuredNetworks = mWiFiManager.getConfiguredNetworks();

            if (configuredNetworks != null) {
                for (WifiConfiguration mWiFiConfig : configuredNetworks) {
//                    Tlog.v(TAG, "WifiConfiguration bssid " + mWiFiConfig.BSSID + " ssid:" + mWiFiConfig.SSID + " id:" + mWiFiConfig.networkId);

                    if (mWiFiConfig.networkId == connectionInfo.getNetworkId()) {
                        if (mWiFiConfig.BSSID != null) {
                            bssid = mWiFiConfig.BSSID;
                        }
                        Tlog.v(TAG, "WifiConfiguration.BSSID " + mWiFiConfig.BSSID);
                        break;
                    }
                }
            }
        }

        if (!MacUtil.macMatches(bssid)) {
            Tlog.e(TAG, " bssid unInvalid " + bssid);
            return false;
        }

        Tlog.v(TAG, "startAirKiss() " + ssid + " pwd:" + pwd);

        if (mTask != null) {
            mTask.cancelEsptouch();
        }

        final EsptouchAsyncTask mTmpTask = new EsptouchAsyncTask(AddDeviceActivity.this.getApplication(), bssid, ssid, pwd, 0, (int) AIR_KISS_TIME_OUT, mEspLsn);
        mTmpTask.execute();
        mTask = mTmpTask;

        AirkissHelper.getInstance().start(AIR_KISS_TIME_OUT, mAirKissHelperLsn);

//                C2JavaExDevice.getInstance().setAirKissListener(airKissLsn);
//                Java2CExDevice.startAirKissWithInter(pwd, ssid, ARI_KISS_ASE_KEY.getBytes(), AIR_KISS_TIME_OUT, processPeroid, datePeroid);
//                Java2CExDevice.startAirKiss(pwd, ssid, ARI_KISS_ASE_KEY.getBytes(), AIR_KISS_TIME_OUT);


        Tlog.v(TAG, "startAirKissWithInter() finish ");
        return true;
    }

    public void stopConfigureWiFi() {
        Tlog.v(TAG, " stopConfigureWiFi() ");

        if (mTask != null) {
            mTask.cancelEsptouch();
            mTask = null;
        }

        AirkissHelper.getInstance().stop();

//                    C2JavaExDevice.getInstance().setAirKissListener(null);
//                    Java2CExDevice.stopAirKiss();

        Tlog.v(TAG, "stopConfigureWiFi() finish ");
    }


    private final AirkissHelper.AirkissHelperListener mAirKissHelperLsn = new AirkissHelper.AirkissHelperListener() {
        @Override
        public void onAirkissSuccess(InetAddress inetAddress) {
            String ip = inetAddress != null ? inetAddress.getHostAddress() : "0.0.0.0";
            Tlog.v(TAG, "配置成功 用时 " + ((System.currentTimeMillis() - startT) / 1000) + " s " + ip + "\n");
            finishForResult(null);
        }
    };


    private final MyEsptouchListener mEspLsn = new MyEsptouchListener() {
        @Override
        public void onEspTouchResultFailed(String errorMsg, String errorCode) {
            Tlog.e(TAG, "配置失败 " + errorMsg + " errorCode = " + errorCode);
            showConfigTimeoutPage();
        }

        @Override
        public void onEsptouchResultAdded(IEsptouchResult iEsptouchResult) {
            Tlog.v(TAG, "配置成功 用时 " + ((System.currentTimeMillis() - startT) / 1000) + " s " + iEsptouchResult.getBssid() + "\n");
            finishForResult(iEsptouchResult.getBssid());
        }
    };

    private void finishForResult(String mac) {

        if (mac == null) {
            mac = "00:00:00:00:00:00";
        }

        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("mac", mac);
        //设置返回数据
        AddDeviceActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        AddDeviceActivity.this.finish();
    }


}
