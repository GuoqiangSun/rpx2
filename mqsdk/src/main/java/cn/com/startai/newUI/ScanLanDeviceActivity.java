package cn.com.startai.newUI;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import cn.com.startai.kp8.activity.Rp86MMasterActivity;
import cn.com.startai.kp8.fragment.Rp86MMenuFragment;
import cn.com.startai.kp8.util.BaseCommondUtils;
import cn.com.startai.kp8.util.HexUtil;
import cn.com.startai.kp8.util.Rp86MCommond;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.network.BroadcastDiscoveryUtil;
import cn.com.startai.mqsdk.network.LanDeviceInfo;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.newUI.addDevice.AddDeviceActivity;
import cn.com.startai.newUI.login.LoginActivity;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/8 0008
 * desc :
 */
public class ScanLanDeviceActivity extends NewUIBaseActivity
        implements BroadcastDiscoveryUtil.IBroadcastResult, NetworkManager.ILanPassThroughResult {

    public static final String TAG = "abc";

    private LanScanAdapter mLstvAdapter;
    private ProgressDialog progressDialog;

    private String mLastConfigMac;// 刚刚配完网络的设备
    private String mSelectMac; // 选中的设备
    private String mControlMac; // 控制的设备

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newui_lancon);

        NetworkManager.getInstance().activityCreate();
        NetworkManager.getInstance().registerReceiver();
        NetworkManager.getInstance().addDiscoveryCallBack(this);


        ListView mScanLstv = findViewById(R.id.scanLstvNewUI);
        mLstvAdapter = new LanScanAdapter(this, false);
        mScanLstv.setAdapter(mLstvAdapter);
        mScanLstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mLstvAdapter.getCount() >= position) {
                    LanDeviceInfo item = mLstvAdapter.getItem(position);
                    if (item != null) {
                        String tmp = item.getMac();
                        if (tmp != null && tmp.equalsIgnoreCase(mSelectMac)) {
                            mSelectMac = null;
                            mLstvAdapter.onSelect(null);
                        } else {
                            mSelectMac = tmp;
                            mLstvAdapter.onSelect(tmp);
                        }
                    }
                }
            }
        });

        mScanLstv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TAndL.T(ScanLanDeviceActivity.this, " rename not impl ");
                return false;
            }
        });

        progressDialog = new ProgressDialog(ScanLanDeviceActivity.this);//1.创建一个ProgressDialog的实例
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Tlog.w(TAG, " progressDialog onCancel disconnectWiFiDevice " + mControlMac);
                removeConMsg();
                NetworkManager.getInstance().disconnectWiFiDevice(mControlMac);
            }
        });

        mFlushDeviceImg = findViewById(R.id.flush_device);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_scan);
        operatingAnim.setInterpolator(new LinearInterpolator());

        mFlushHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_FLUSH) {
                    if (mFlushDeviceImg != null) {
                        mFlushDeviceImg.startAnimation(operatingAnim);
                        mFlushHandler.sendEmptyMessageDelayed(MSG_STOP_ANIM, msg.arg1 * 3000);
                    }
                    NetworkManager.getInstance().discoveryLanDevice(msg.arg1);
                } else if (msg.what == MSG_STOP_ANIM) {
                    if (mFlushDeviceImg != null) {
                        mFlushDeviceImg.clearAnimation();
                    }
                } else if (msg.what == MSG_GET_VERSION_REPEAT) {

                    Tlog.d(TAG, " MSG_GET_VERSION_REPEAT ");

                    if (mControlMac != null) {
                        ResponseData deviceModel = NetworkManager.getInstance().getDeviceModelInCache(mControlMac);

                        Tlog.v(TAG, " MSG_GET_VERSION_REPEAT " + getModelTimes
                                + " mControlMac:" + mControlMac + " (deviceModel=null)?" + (deviceModel == null));

                        if (deviceModel != null && ++getModelTimes <= MAX_RETYR_GET_MODEL_TIMES) {

                            NetworkManager.getInstance().onLanPassThrough(deviceModel.toID, deviceModel.data);
                            if (mFlushHandler != null) {
                                mFlushHandler.sendEmptyMessageDelayed(MSG_GET_VERSION_REPEAT, 1000 * 3);
                            }

                        }


                    }

                }
            }
        };

        mFlushHandler.obtainMessage(MSG_FLUSH, 6, 6).sendToTarget();

    }

    private void removeConMsg() {
        if (mFlushHandler != null) {
            mFlushHandler.removeMessages(MSG_GET_VERSION_REPEAT);
        }
        if (mControlMac != null) {
            String mac = mControlMac;
            NetworkManager.getInstance().removeGetDeviceModelMsg(mac);
        }
        NetworkManager.getInstance().removePassThroughCallBack(ScanLanDeviceActivity.this);
    }

    private Handler mFlushHandler;

    private static final int MSG_FLUSH = 0x01;
    private static final int MSG_STOP_ANIM = 0x02;
    private static final int MSG_GET_VERSION_REPEAT = 0x03;

    private static final int MAX_RETYR_GET_MODEL_TIMES = 6;
    private int getModelTimes;

    private ImageView mFlushDeviceImg;
    private Animation operatingAnim;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().unregisterReceiver();
        NetworkManager.getInstance().removeDiscoveryCallBack(this);
    }

    public void flushDevice(View v) {
        if (mLstvAdapter != null) {
            mLstvAdapter.clearData();
            mLstvAdapter.onSelect(null);
        }

        mSelectMac = null;

        mFlushHandler.obtainMessage(MSG_FLUSH, 6, 6).sendToTarget();
//        test();
    }

    @Override
    public void onBroadcastStart(int remainingTimes) {

    }

    @Override
    public void onDiscoveryDevice(LanDeviceInfo mLanDeviceInfo) {
        if (mLstvAdapter != null) {
            mLstvAdapter.onLanScan(mLanDeviceInfo);
        }

        Tlog.d(TAG, "onDiscoveryDevice: mac:" + mLanDeviceInfo.mac
                + " isLanBind:" + mLanDeviceInfo.isLanBind
                + " isWanBind:" + mLanDeviceInfo.isWanBind);
        if (!mLanDeviceInfo.isLanBind
//&& mLanDeviceInfo.mac.equalsIgnoreCase(mLastConfigMac)
                ) {
            NetworkManager.getInstance().bindDevice(mLanDeviceInfo.mac, mLanDeviceInfo.ip);
        }
    }

    private String getRandom() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    private void test() {
        for (int i = 0; i < 6; i++) {
            LanDeviceInfo mLanDeviceInfo = new LanDeviceInfo();
            mLanDeviceInfo.mac = getRandom();
            mLanDeviceInfo.name = getRandom();
            mLstvAdapter.onLanScan(mLanDeviceInfo);
        }
    }

    public static final int REQUEST_CODE_SKIP_LOGIN = 0x2796;

    public void skipCloud(View v) {

        if (mFlushDeviceImg != null) {
            mFlushDeviceImg.clearAnimation();
        }

        String mqttUserID = NetworkManager.getInstance().getMqttUserID();
        if (mqttUserID == null || "".equals(mqttUserID)) {
            //login;

            startActivityForResult(new Intent(ScanLanDeviceActivity.this, LoginActivity.class), REQUEST_CODE_SKIP_LOGIN);

        } else {
//            Toast.makeText(getApplication(), " userID:" + mqttUserID, Toast.LENGTH_SHORT).show();

            startActivity(new Intent(ScanLanDeviceActivity.this, WanBindDeviceActivity.class));

        }

    }

    public void connect(View v) {
        String tmp = mSelectMac;
        if (tmp == null) {
            TAndL.T(ScanLanDeviceActivity.this, "please select one device");
            return;
        }
        mControlMac = tmp;

        mFlushHandler.removeMessages(MSG_GET_VERSION_REPEAT);
        getModelTimes = 0;
        NetworkManager.getInstance().addPassThroughCallBack(this);

        progressDialog.setTitle(R.string.get_device_version);//2.设置标题
        progressDialog.setMessage(getResources().getString(R.string.get_device_version_loading));//3.设置显示内容
        progressDialog.setCancelable(true);//4.设置可否用back键关闭对话框
        progressDialog.show();//5.将ProgessDialog显示出来


        String hexStr = Rp86MCommond.HEX_REQUEST_VERSION + BaseCommondUtils.crc8(Rp86MCommond.HEX_REQUEST_VERSION);
        byte[] bytes = HexUtil.hexStringToBytes(hexStr);

        Tlog.d(TAG, " controlMac:" + mControlMac);
        NetworkManager.getInstance().connectWiFiDevice(mControlMac);
        NetworkManager.getInstance().getDeviceModelByLan(mControlMac, bytes);
        NetworkManager.getInstance().onLanPassThrough(mControlMac, bytes);

        if (mFlushHandler != null) {
            mFlushHandler.sendEmptyMessageDelayed(MSG_GET_VERSION_REPEAT, 1000 * 3);
        }

    }

    public static final int REQUEST_CODE_CONFIG_MAC = 0x2369;

    public void addDevice(View view) {
//        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(ScanLanDeviceActivity.this, AddDeviceActivity.class), REQUEST_CODE_CONFIG_MAC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tlog.d(TAG, "onActivityResult  requestCode:" + requestCode + " resultCode:" + resultCode);
        if (requestCode == REQUEST_CODE_CONFIG_MAC) {
            if (resultCode == RESULT_OK) {
                String mac = data.getStringExtra("mac");

                StringBuilder sb = new StringBuilder();
                int j = mac.length() / 2;
                for (int i = 0; i < j; i++) {
                    String substring = mac.substring(i * 2, i * 2 + 2);
                    sb.append(substring);
                    if (i != (j - 1)) {
                        sb.append(":");
                    }
                }

                String tmp = sb.toString().toUpperCase();
                Tlog.d(TAG, "onActivityResult  mac:" + mac + " tmp:" + tmp);
                if (MacUtil.macMatches(tmp)) {
                    mLastConfigMac = tmp;
                } else {
                    TAndL.T(ScanLanDeviceActivity.this, " mac not matches ");
                }
                mFlushHandler.obtainMessage(MSG_FLUSH, 6, 6).sendToTarget();
            } else {
                mLastConfigMac = null;
            }
        } else if (requestCode == REQUEST_CODE_SKIP_LOGIN) {

            if (resultCode == RESULT_OK) {
                skipCloud(null);
            }

        }
    }


    @Override
    public void onReceiveLanPassThrough(String mac, byte[] data) {

        if (mac != null && mac.equalsIgnoreCase(mControlMac)) {
            parseVersion(data);
        }

    }

    private synchronized void parseVersion(byte[] data) {

        String version = BaseCommondUtils.resolveVersion(data);
        Tlog.v(TAG, "version:" + version);

        if (version.length() == 0 || null == mControlMac) {
            //TAndL.TL(getApplicationContext(), "version = " +version);
            //TAndL.TL(getApplicationContext(), "获取设备的型号:" + version);
            return;
        }
        removeConMsg();
        if (!progressDialog.isShowing()) {
            Tlog.e(TAG, "获取设备的型号:" + version + " !progressDialog.isShowing()");
            return;
        }

        progressDialog.dismiss();
        TAndL.TL(getApplicationContext(), " device version:" + version);
        Tlog.v(TAG, "获取设备的型号:" + version);


        //如果识别出版本号，按版本号跳转
        Intent intent = null;
        if (Rp86MMenuFragment.FW_ID.equals(version)) {
            intent = new Intent(ScanLanDeviceActivity.this, Rp86MMasterActivity.class);
        } else {
            TAndL.T(ScanLanDeviceActivity.this, " version:" + version + " [activity not impl]");
        }

//        if (BR16IRMenuFragment.FW_ID.equals(version)) {
//            intent = new Intent(ScanLanDeviceActivity.this, BR16IRMasterActivity.class);
//        }
//        if (BR16PUMenuFragment.FW_ID.equals(version)) {
//            intent = new Intent(ScanLanDeviceActivity.this, BR16PUMasterActivity.class);
//        }
//        if (BR16WPMenuFragment.FW_ID.equals(version)) {
//            intent = new Intent(ScanLanDeviceActivity.this, BR16WPMasterActivity.class);
//        }

        if (null != intent) {
//            intent.putExtra("device", controlMac);
            intent.putExtra("mac", mSelectMac);
            startActivity(intent);
        }
    }

}
