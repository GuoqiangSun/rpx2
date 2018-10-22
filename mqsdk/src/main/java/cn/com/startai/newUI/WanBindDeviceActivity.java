package cn.com.startai.newUI;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.com.startai.kp8.activity.Rp86MMasterActivity;
import cn.com.startai.kp8.fragment.Rp86MMenuFragment;
import cn.com.startai.kp8.util.BaseCommondUtils;
import cn.com.startai.kp8.util.HexUtil;
import cn.com.startai.kp8.util.Rp86MCommond;
import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.network.BroadcastDiscoveryUtil;
import cn.com.startai.mqsdk.network.LanBindingDevice;
import cn.com.startai.mqsdk.network.LanDeviceInfo;
import cn.com.startai.mqsdk.network.LooperManager;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8002_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8004_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8005_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8020_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8024_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8200_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_Device_Connect_Status;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;
import cn.com.startai.newUI.addDevice.AddDeviceActivity;
import cn.com.startai.newUI.db.bean.WanBindingDevice;
import cn.com.startai.newUI.db.gen.WanBindingDeviceDao;
import cn.com.startai.newUI.db.manager.DBManager;
import cn.com.startai.newUI.login.MyImageView;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/16 0016
 * Desc:
 */
public class WanBindDeviceActivity extends BaseActivity
        implements NetworkManager.ILanPassThroughResult
        , BroadcastDiscoveryUtil.IBroadcastResult
        , NetworkManager.ILanBindResult {


    private String TAG = "abc";

    private Handler mWorkHandler;


    private LanScanAdapter mLstvAdapter;
    private ProgressDialog progressDialog;

    private String mSelectMac; // 选中的设备
    private String mControlMac; // 控制的设备


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newui_wanlogincon);


        ListView mScanLstv = findViewById(R.id.scanLstvNewUI);
        mLstvAdapter = new LanScanAdapter(this, true);
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
                TAndL.T(WanBindDeviceActivity.this, " rename not impl ");
                return false;
            }
        });

        progressDialog = new ProgressDialog(WanBindDeviceActivity.this);//1.创建一个ProgressDialog的实例
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
                    }


                    StartAI.getInstance().getBaseBusiManager().getBindList(1, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {

                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                            Tlog.v(TAG, " getBindList" + startaiError.getErrorMsg());
                            TAndL.T(getApplicationContext(), "getBindList msg send fail");
                            mFlushHandler.sendEmptyMessage(MSG_STOP_ANIM);
                        }

                        @Override
                        public boolean needUISafety() {
                            return false;
                        }
                    });

                    NetworkManager.getInstance().discoveryLanDevice(6);

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

                            WanBindingDeviceDao wanBindingDeviceDao =
                                    DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
                            List<WanBindingDevice> list = wanBindingDeviceDao.queryBuilder().where(WanBindingDeviceDao.Properties.Mid.eq(NetworkManager.getInstance().getMqttUserID())
                                    , WanBindingDeviceDao.Properties.Mac.eq(deviceModel.toID)).list();

                            Tlog.d(TAG, "List<WanBindingDevice> size " + list.size());

                            if (list.size() > 0) {

                                WanBindingDevice wanBindingDevice = list.get(0);
                                String oid = wanBindingDevice.getOid();

                                Tlog.d(TAG, " controlMac oid " + oid + String.valueOf(deviceModel));

                                StartAI.getInstance().getBaseBusiManager()
                                        .passthrough(oid, deviceModel.data, new IOnCallListener() {
                                            @Override
                                            public void onSuccess(MqttPublishRequest request) {

                                            }

                                            @Override
                                            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                                            }

                                            @Override
                                            public boolean needUISafety() {
                                                return false;
                                            }
                                        });
                            }


                            if (mFlushHandler != null) {
                                mFlushHandler.sendEmptyMessageDelayed(MSG_GET_VERSION_REPEAT, 1000 * 3);
                            }

                        }


                    }

                } else if (msg.what == MSG_SCAN) {
                    NetworkManager.getInstance().discoveryLanDevice(msg.arg1);
                } else if (msg.what == MSG_SHOW_DEVICE) {

                    if (mLstvAdapter != null && mBindSize != null) {
                        if (mLstvAdapter.getCount() > 0) {
                            String msgD = mLstvAdapter.getCount() + " devices";
                            mBindSize.setText(msgD);
                        } else {
                            mBindSize.setText("");
                        }
                    }

                }

            }
        };


        mWorkHandler = new Handler(LooperManager.getInstance().getWorkLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case MSG_CHECK_LAN_BIND:

                        WanBindingDeviceDao wanBindingDeviceDao0 =
                                DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();

                        String randomUserID0 = NetworkManager.getInstance().getRandomUserID();
                        Tlog.v(TAG, "MSG_CHECK_LAN_BIND randomUserID:" + randomUserID0);
                        List<WanBindingDevice> listBind = wanBindingDeviceDao0.queryBuilder()
                                .where(WanBindingDeviceDao.Properties.Mid.eq(randomUserID0)).list();
                        Tlog.v(TAG, "MSG_CHECK_LAN_BIND bind listBind " + listBind.size());


                        String mqttUserID0 = NetworkManager.getInstance().getMqttUserID();
                        Tlog.v(TAG, "MSG_CHECK_LAN_BIND mqttUserID:" + mqttUserID0);

                        List<WanBindingDevice> listBindM = wanBindingDeviceDao0.queryBuilder()
                                .where(WanBindingDeviceDao.Properties.Mid.eq(mqttUserID0)).list();
                        Tlog.v(TAG, "MSG_CHECK_LAN_BIND bind listBind " + listBindM.size());


                        for (WanBindingDevice mWanBindingDevice : listBind) {

                            boolean needWanBind = true;
                            for (WanBindingDevice mWanBindingDevice2 : listBindM) {
                                if (mWanBindingDevice.getOid().equals(mWanBindingDevice2.getOid())) {

                                    needWanBind = !mWanBindingDevice2.getHasBindingByWan();
                                    Tlog.d(TAG, "for r binds " + String.valueOf(mWanBindingDevice2));
                                    break;
                                }
                            }

                            if (needWanBind) {
                                String oid = mWanBindingDevice.getOid();
                                Tlog.d(TAG, "bind r " + String.valueOf(mWanBindingDevice));
                                wanbindDevice(oid);
                            }

                        }


                        for (WanBindingDevice mWanBindingDevice2 : listBindM) {

                            boolean needWanBind = false;

                            if (!mWanBindingDevice2.getHasBindingByWan()) {
                                needWanBind = true;

                                for (WanBindingDevice mWanBindingDevice : listBind) {
                                    if (mWanBindingDevice2.getOid().equals(mWanBindingDevice.getOid())) {
                                        needWanBind = false;
                                        Tlog.d(TAG, "for m binds " + String.valueOf(mWanBindingDevice2));
                                        break;
                                    }
                                }

                            }

                            if (needWanBind) {
                                String oid = mWanBindingDevice2.getOid();
                                Tlog.d(TAG, "bind m " + String.valueOf(mWanBindingDevice2));
                                wanbindDevice(oid);
                            }

                        }


                        mWorkHandler.sendEmptyMessage(MSG_DISPLAY_WAN_BIND_LIST);

                        break;
                    case MSG_WAN_BIND_SUCCESS: // lan bind success

                        String id = (String) msg.obj;

                        WanBindingDeviceDao wanBindingDeviceDao =
                                DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();


                        String randomUserID = NetworkManager.getInstance().getRandomUserID();


                        List<WanBindingDevice> listBind3 = wanBindingDeviceDao.queryBuilder()
                                .where(WanBindingDeviceDao.Properties.Mid.eq(randomUserID)
                                        , WanBindingDeviceDao.Properties.Oid.eq(id)).list();

                        WanBindingDevice mtmpWanBindingDevice = null;

                        if (listBind3.size() > 0) {
                            for (WanBindingDevice mWanBindingDevice : listBind3) {
                                if (!mWanBindingDevice.getHasBindingByWan()) {
                                    mWanBindingDevice.setHasBindingByWan(true);
                                    wanBindingDeviceDao.update(mWanBindingDevice);
                                } else {
                                    mtmpWanBindingDevice = mWanBindingDevice;
                                }
                            }
                        }


                        String mqttUserID = NetworkManager.getInstance().getMqttUserID();

                        List<WanBindingDevice> listBind2 = wanBindingDeviceDao.queryBuilder()
                                .where(WanBindingDeviceDao.Properties.Mid.eq(mqttUserID)
                                        , WanBindingDeviceDao.Properties.Oid.eq(id)).list();

                        if (listBind2.size() > 0) {
                            for (WanBindingDevice mWanBindingDevice : listBind2) {
                                if (!mWanBindingDevice.getHasBindingByWan()) {
                                    mWanBindingDevice.setHasBindingByWan(true);
                                    wanBindingDeviceDao.update(mWanBindingDevice);
                                }
                            }
                        } else {
                            if (mtmpWanBindingDevice != null) {
                                mtmpWanBindingDevice.setGid(null);
                                wanBindingDeviceDao.insert(mtmpWanBindingDevice);
                            }
                        }


                        break;

                    case MSG_GET_WAN_BIND_LIST_SUCCESS:


                        E_0x8005_Resp resp = (E_0x8005_Resp) msg.obj;


                        ArrayList<C_0x8005.Resp.ContentBean> bindList = null;


                        if (resp.getResult() == 1) {
                            bindList = resp.getBindList();

                        }

                        if (bindList != null) {
                            String mqttUserID1 = NetworkManager.getInstance().getMqttUserID();

                            onGetBindListResult(bindList, mqttUserID1);

                        }

                        mWorkHandler.sendEmptyMessage(MSG_DISPLAY_WAN_BIND_LIST);
                        mFlushHandler.sendEmptyMessage(MSG_STOP_ANIM);
                        break;

                    case MSG_DISPLAY_WAN_BIND_LIST:

                        String mid = NetworkManager.getInstance().getMqttUserID();
                        WanBindingDeviceDao bindingDeviceDao =
                                DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
                        List<WanBindingDevice> listWan = bindingDeviceDao.queryBuilder()
                                .where(WanBindingDeviceDao.Properties.Mid.eq(mid)).list();

                        for (WanBindingDevice mWanBindingDevice : listWan) {
                            displayWanBindDevice(mWanBindingDevice.getMac(), mWanBindingDevice.getOid(), mWanBindingDevice.getConnstatus() == 1);
                        }

                        break;
                }

            }
        };

        mWorkHandler.sendEmptyMessage(MSG_CHECK_LAN_BIND);
        mFlushHandler.sendEmptyMessage(MSG_FLUSH);


        mNickName = findViewById(R.id.nick_name);
        mImgView = findViewById(R.id.head_pic);

        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WanBindDeviceActivity.this, UserInfoActivity.class);
                intent.putExtra("name", mNickName.getText().toString());
                if (headPicPath != null) {
                    intent.putExtra("headPicPath", headPicPath);
                }

                startActivityForResult(intent, REQUEST_CODE_USERINFO);

            }
        });

        mBindSize = findViewById(R.id.bind_wandevice);


        StartAI.getInstance().getBaseBusiManager().getUserInfo(new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {

            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

            }

            @Override
            public boolean needUISafety() {
                return false;
            }
        });

        NetworkManager.getInstance().addDiscoveryCallBack(this);
        NetworkManager.getInstance().addLanBindCallBack(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().removeDiscoveryCallBack(this);
        NetworkManager.getInstance().removeLanBindCallBack(this);

        if (mFlushHandler != null) {
            mFlushHandler.removeCallbacksAndMessages(null);
        }
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacksAndMessages(null);
        }
    }

    private static final int MSG_CHECK_LAN_BIND = 0x00;
    private static final int MSG_WAN_BIND_SUCCESS = 0x01;
    private static final int MSG_GET_WAN_BIND_LIST_SUCCESS = 0x02;
    private static final int MSG_DISPLAY_WAN_BIND_LIST = 0x03;


    private TextView mNickName;
    private MyImageView mImgView;
    TextView mBindSize;

    @Override
    public void onUpdateUserInfoResult(E_0x8020_Resp resp) {
        super.onUpdateUserInfoResult(resp);

        Tlog.v(TAG, " wanBindActivity onUpdateUserInfoResult " + String.valueOf(resp));

        if (resp.getResult() == 1) {

            String nickName = resp.getMessage().getNickName();

            if (null != nickName && null != mNickName) {
                mNickName.setText(nickName);
            }

            final String headPic = resp.getMessage().getHeadPic();

            if (null != headPic) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        setImageURL(headPic);
                    }
                }.start();
            }
        }

    }

    @Override
    public void onGetUserInfoResult(E_0x8024_Resp resp) {
        super.onGetUserInfoResult(resp);

        Tlog.v(TAG, " wanBindActivity onGetUserInfoResult " + String.valueOf(resp));

        if (resp.getResult() == 1) {

            String nickName = resp.getMessage().getNickName();

            if (null != nickName && null != mNickName) {
                mNickName.setText(nickName);
            }

            final String headPic = resp.getMessage().getHeadPic();

            if (null != headPic) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        setImageURL(headPic);
                    }
                }.start();
            }
        }

    }


    @Override
    public void onDeviceConnectStatusChange(E_Device_Connect_Status e_device_connect_status) {
        super.onDeviceConnectStatusChange(e_device_connect_status);


        String sn = e_device_connect_status.sn;
        int status = e_device_connect_status.status;
        Tlog.v(TAG, " onDeviceConnectStatusChange " + sn + " status:" + status);

        mLstvAdapter.onStateChange(sn, status == 1);


    }


    synchronized void onGetBindListResult(ArrayList<C_0x8005.Resp.ContentBean> bindList, String mid) {


        WanBindingDeviceDao bindingDeviceDao =
                DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();

        List<WanBindingDevice> listWan = bindingDeviceDao.queryBuilder()
                .where(WanBindingDeviceDao.Properties.Mid.eq(mid)).list();

        for (WanBindingDevice mBindingDevice : listWan) {

            boolean serverHas = false;
            C_0x8005.Resp.ContentBean tContentBean = null;
            for (C_0x8005.Resp.ContentBean mContentBean : bindList) {
                if (mContentBean.getId().equals(mBindingDevice.getOid())) {
                    serverHas = true;
                    tContentBean = mContentBean;
                    break;
                }
            }

            if (!serverHas) { // 我有，服务器没有
                if (mBindingDevice.getHasBindingByWan()) {
                    bindingDeviceDao.deleteByKey(mBindingDevice.getGid());
                    Tlog.e(TAG, "onGetBindListResult() delete WanBindDevice:" + mBindingDevice.toString());
                }
            } else { // 我有，服务器也有
                mBindingDevice.setConnstatus(tContentBean.getConnstatus());// 连接状态是实时刷新的
                mBindingDevice.setHasBindingByWan(true);
                bindingDeviceDao.update(mBindingDevice);
                Tlog.e(TAG, "onGetBindListResult() bindingDeviceDao update:" + mBindingDevice.toString());
            }

        }

        // 我没有，服务器有
        for (C_0x8005.Resp.ContentBean mBean : bindList) {

            Tlog.e(TAG, "onGetBindListResult mid:" + mid + " oid:" + mBean.getId());

            boolean myHas = false;
            for (WanBindingDevice mBindingDevice : listWan) {
                if (mBean.getId().equals(mBindingDevice.getOid())) {
                    myHas = true;
                    break;
                }
            }

            if (!myHas) {
                WanBindingDevice memor = WanBindingDevice.memor(mBean);
                memor.setHasBindingByWan(true);
                memor.setMid(mid);
                bindingDeviceDao.insert(memor);
                Tlog.e(TAG, "onGetBindListResult() bindingDeviceDao insert:" + memor.toString());
            }

        }


    }

    @Override
    public void onUnBindResult(E_0x8004_Resp resp) {
        super.onUnBindResult(resp);

        Tlog.e(TAG, "onUnBindResult():" + String.valueOf(resp));


        if (resp.getResult() == 1) {
            if (mLstvAdapter != null) {
//                resp.getBeUnbindid()
                mLstvAdapter.removeDevice(resp.getBeUnbindid());
            }
        }

    }

    @Override
    public void onBindResult(E_0x8002_Resp resp) {
        super.onBindResult(resp);

        Tlog.d(TAG, " bind result " + String.valueOf(resp));

        if (resp.getResult() == 1
//                || resp.getErrorCode().equalsIgnoreCase("0x800205")//绑定失败，重复绑定
                ) {

            String id = resp.getBebinding().getId();

            Tlog.e(TAG, " bind success " + id);

            mWorkHandler.obtainMessage(MSG_WAN_BIND_SUCCESS, id).sendToTarget();
            displayWanBindDevice(resp.getBebinding().getMac(), id, resp.getBebinding().getConnstatus() == 1);

        }


    }


    @Override
    public void onBindListResult(E_0x8005_Resp resp) {
        super.onBindListResult(resp);

        Tlog.v(TAG, " onBindListResult " + String.valueOf(resp));

        mWorkHandler.obtainMessage(MSG_GET_WAN_BIND_LIST_SUCCESS, resp).sendToTarget();

    }

    private void displayWanBindDevice(String mac, String deviceID, boolean con) {

        if (mLstvAdapter != null) {

            LanDeviceInfo lanDeviceInfoByMac = NetworkManager.getInstance().getLanDeviceInfoByMac(mac);

            if (lanDeviceInfoByMac == null) {
                lanDeviceInfoByMac = new LanDeviceInfo();
                lanDeviceInfoByMac.mac = mac;
                lanDeviceInfoByMac.checkName();
            }
            lanDeviceInfoByMac.state = con;
            lanDeviceInfoByMac.deviceID = deviceID;
            mLstvAdapter.onLanScan(lanDeviceInfoByMac);

        }

        mFlushHandler.sendEmptyMessageDelayed(MSG_SHOW_DEVICE, 1000);

    }

    public static final int REQUEST_CODE_CONFIG_MAC = 0x6392;

    public void addDevice(View v) {
        startActivityForResult(new Intent(WanBindDeviceActivity.this, AddDeviceActivity.class), REQUEST_CODE_CONFIG_MAC);
    }

    private String mLastConfigMac;

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
                    TAndL.T(WanBindDeviceActivity.this, " mac not matches ");
                }
                mFlushHandler.obtainMessage(MSG_SCAN, 6, 6).sendToTarget();
            } else {
                mLastConfigMac = null;
            }
        } else if (requestCode == REQUEST_CODE_USERINFO) {

            if (resultCode == RESULT_OK) {

                boolean logout = data.getBooleanExtra("logout", false);
                if (logout) {
                    finish();
                }
            }

        }
    }

    public static final int REQUEST_CODE_USERINFO = 0x6396;


    public void flushDevice(View v) {
        if (mLstvAdapter != null) {
            mLstvAdapter.clearData();
            mLstvAdapter.onSelect(null);
        }

        mSelectMac = null;

        mFlushHandler.sendEmptyMessage(MSG_FLUSH);

//        test();

    }


//    private String getRandom() {
//        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
//    }
//
//    private void test() {
//        for (int i = 0; i < 6; i++) {
//            LanDeviceInfo mLanDeviceInfo = new LanDeviceInfo();
//            mLanDeviceInfo.mac = getRandom();
//            mLanDeviceInfo.name = getRandom();
//            mLstvAdapter.onLanScan(mLanDeviceInfo);
//        }
//    }


    public void connect(View v) {
        String tmp = mSelectMac;
        if (tmp == null) {
            TAndL.T(WanBindDeviceActivity.this, "please select one device");
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


        WanBindingDeviceDao wanBindingDeviceDao =
                DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
        List<WanBindingDevice> list = wanBindingDeviceDao.queryBuilder().where(WanBindingDeviceDao.Properties.Mid.eq(NetworkManager.getInstance().getMqttUserID())
                , WanBindingDeviceDao.Properties.Mac.eq(mControlMac)).list();

        Tlog.d(TAG, "List<WanBindingDevice> size " + list.size());

        String oid = null;

        if (list.size() > 0) {

            WanBindingDevice wanBindingDevice = list.get(0);
            oid = wanBindingDevice.getOid();

            Tlog.d(TAG, " controlMac oid " + oid);

            StartAI.getInstance().getBaseBusiManager()
                    .passthrough(oid, hexStr, new IOnCallListener() {
                        @Override
                        public void onSuccess(MqttPublishRequest request) {

                        }

                        @Override
                        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

                        }

                        @Override
                        public boolean needUISafety() {
                            return false;
                        }
                    });
        }

        NetworkManager.getInstance().getDeviceModelByLan(mControlMac, bytes);
        NetworkManager.getInstance().onLanPassThrough(mControlMac, bytes);

        if (mFlushHandler != null) {
            mFlushHandler.sendEmptyMessageDelayed(MSG_GET_VERSION_REPEAT, 1000 * 3);
        }

    }

    public void skipLocal(View v) {
        this.finish();
    }


    private void removeConMsg() {
        if (mFlushHandler != null) {
            mFlushHandler.removeMessages(MSG_GET_VERSION_REPEAT);
        }
        if (mControlMac != null) {
            String mac = mControlMac;
            NetworkManager.getInstance().removeGetDeviceModelMsg(mac);
        }
        NetworkManager.getInstance().removePassThroughCallBack(WanBindDeviceActivity.this);
    }

    private Handler mFlushHandler;

    private static final int MSG_FLUSH = 0x01;
    private static final int MSG_STOP_ANIM = 0x02;
    private static final int MSG_GET_VERSION_REPEAT = 0x03;
    private static final int MSG_SCAN = 0x04;
    private static final int MSG_SHOW_DEVICE = 0x05;

    private static final int MAX_RETYR_GET_MODEL_TIMES = 6;
    private int getModelTimes;

    private ImageView mFlushDeviceImg;
    private Animation operatingAnim;

    @Override
    public void onReceiveLanPassThrough(String mac, byte[] data) {

        if (mac != null && mac.equalsIgnoreCase(mControlMac)) {
            parseVersion(data);
        }

    }

    @Override
    public void onPassthrouthResult(E_0x8200_Resp resp) {
        super.onPassthrouthResult(resp);

        Tlog.e(TAG, " onPassthrouthResult " + String.valueOf(resp));


        parseVersion(resp.getDataByteArray());

    }

    private synchronized void parseVersion(byte[] data) {

        String version = BaseCommondUtils.resolveVersion(data);
        Tlog.v(TAG, "version:" + version);

        String mac = mControlMac;

        if (version.length() == 0 || null == mac) {
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
            intent = new Intent(WanBindDeviceActivity.this, Rp86MMasterActivity.class);
        } else {
            TAndL.T(WanBindDeviceActivity.this, " version:" + version + " [activity not impl]");
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


            WanBindingDeviceDao wanBindingDeviceDao =
                    DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
            List<WanBindingDevice> list = wanBindingDeviceDao.queryBuilder().where(WanBindingDeviceDao.Properties.Mid.eq(NetworkManager.getInstance().getMqttUserID())
                    , WanBindingDeviceDao.Properties.Mac.eq(mac)).list();
            if (list.size() > 0) {

                WanBindingDevice wanBindingDevice = list.get(0);
                String oid = wanBindingDevice.getOid();

                C_0x8005.Resp.ContentBean controlMac = new C_0x8005.Resp.ContentBean();
                controlMac.setMac(mac);
                controlMac.setId(oid);
                intent.putExtra("device", controlMac);

                Tlog.d(TAG, " putExtra oid " + oid);

            }
            Tlog.d(TAG, " putExtra mac " + mac);
            intent.putExtra("mac", mac);
            startActivity(intent);
        }
    }

    private String headPicPath;

    private void setImageURL(final String path) {
        //开启一个线程用于联网
        headPicPath = path;
        //把传过来的路径转成URL
        try {
            URL url = null;
            url = new URL(path);
            Tlog.e(TAG, " setImageURL path:" + path);
            //获取连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //使用GET方法访问网络
            connection.setRequestMethod("GET");
            //超时时间为10秒
            connection.setConnectTimeout(10000);
            //获取返回码
            int code = connection.getResponseCode();

            Tlog.e(TAG, " setImageURL bitmap0 code" + code);

            if (code == 200) {
                InputStream inputStream = connection.getInputStream();
                //使用工厂把网络的输入流生产Bitmap
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //利用Message把图片发给Handler

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mImgView != null) {
                            mImgView.setBitmap(bitmap);
                            mImgView.setmOuterRing(56);
                            mImgView.setColor(Color.RED);
                            mImgView.setOuterRingAlpha(50);
//                            mImgView.setImageBitmap(bitmap);
                            mImgView.invalidate();
                        }
                    }
                });
                inputStream.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        } catch (IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        }

    }


    @Override
    public void onBroadcastStart(int remainingTimes) {

    }

    @Override
    public void onDiscoveryDevice(LanDeviceInfo mLanDeviceInfo) {
        Tlog.d(TAG, " wanActivity onDiscoveryDevice: mac:" + mLanDeviceInfo.mac
                + " isLanBind:" + mLanDeviceInfo.isLanBind
                + " isWanBind:" + mLanDeviceInfo.isWanBind);
        if (!mLanDeviceInfo.isLanBind
//&& mLanDeviceInfo.mac.equalsIgnoreCase(mLastConfigMac)
                ) {
            NetworkManager.getInstance().bindDevice(mLanDeviceInfo.mac, mLanDeviceInfo.ip);
        } else if (!mLanDeviceInfo.isWanBind) {

        }

        if (mLstvAdapter != null) {
            mLstvAdapter.onLanScanByWan(mLanDeviceInfo);
        }

    }

    @Override
    public void onDeviceLanBindResult(LanBindingDevice mLanBindingDevice) {
        wanbindDevice(mLanBindingDevice.getOid());

    }

    private void wanbindDevice(String oid) {
        Tlog.v(TAG, "wanbindDevice :" + oid);
        StartAI.getInstance().getBaseBusiManager().bind(oid, callListener);
    }


    final IOnCallListener callListener = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest request) {

        }

        @Override
        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {

            TAndL.T(getApplicationContext(), "wan bind fail " + startaiError.getErrorCode());

        }

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

}
