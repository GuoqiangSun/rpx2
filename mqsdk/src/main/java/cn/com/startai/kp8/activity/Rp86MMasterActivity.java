package cn.com.startai.kp8.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.startai.kp8.fragment.Rp86MMenuFragment;
import cn.com.startai.kp8.fragment.Rp8MontiorFragment;
import cn.com.startai.kp8.util.HexUtil;
import cn.com.startai.kp8.util.Rp86MCommondUtils;
import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.adapter.ViewPagerAdapter;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8001_Resp_;
import cn.com.startai.mqsdk.util.eventbus.E_0x8101_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8200_Resp;
import cn.com.startai.mqsdk.widget.VerticalViewPager;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;
import cn.com.swain169.log.Tlog;

public class Rp86MMasterActivity extends BaseActivity implements NetworkManager.ILanPassThroughResult {

    private C_0x8005.Resp.ContentBean device;

    public String getDeviceMac() {
        if (mac != null && !"".equals(mac)) {
            return mac;
        }
        return device != null ? device.getMac() : null;
    }

    public String getDeviceID() {
        return device != null ? device.getId() : null;
    }

    private String mac;

    private VerticalViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Rp8MontiorFragment montiorFragment;
    private Rp86MMenuFragment menuFragment;
    private Rp86MCommondUtils commondUtils = new Rp86MCommondUtils();
    private Handler mUIHander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_kp8_layout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.include4);
        toolbar.setTitle("设备控制");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mUIHander = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                resolve((String) msg.obj);
            }
        };


        mac = getIntent().getStringExtra("mac");
        Serializable device = getIntent().getSerializableExtra("device");
        if (device != null) {
            this.device = (C_0x8005.Resp.ContentBean) device;
        }

        initView();

        NetworkManager.getInstance().addPassThroughCallBack(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUIHander != null) {
            mUIHander.removeCallbacksAndMessages(null);
            mUIHander = null;
        }
        NetworkManager.getInstance().removePassThroughCallBack(this);
        NetworkManager.getInstance().disconnectWiFiDevice(getDeviceMac());
    }

    private void initView() {
        viewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        menuFragment = new Rp86MMenuFragment();
        fragmentList.add(menuFragment);
        montiorFragment = new Rp8MontiorFragment();
        fragmentList.add(montiorFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, false); // MenuFragment
    }

    @Override
    public void onPassthrouthResult(E_0x8200_Resp resp) {
        super.onPassthrouthResult(resp);
//        TAndL.TL(getApplicationContext(), "透传 result = " + resp.getResult() + "fromid = " + resp.getResp().getFromid() + " errorMsg = " + resp.getErrorMsg() + " data = " + resp.getDataString() + " dataArr = " + HexUtil.formatHexString(resp.getDataByteArray()));
//        TAndL.TL(getApplicationContext(), "WanReceiveData: " + resp.getDataString());
//        TAndL.TL(getApplicationContext(), String.valueOf(resp));
        Tlog.v(NetworkManager.TAG, " rp8Activity onWanPassthrouthResult :" + String.valueOf(resp));

        String dataString = resp.getDataString();
        resolve(dataString);
    }

    @Override
    public void onReceiveLanPassThrough(String mac, byte[] data) {
        if (mac != null && mac.equals(getDeviceMac())) {
//            String dataString = toString(data);
            String dataString = HexUtil.formatHexString(data);
            Tlog.v(NetworkManager.TAG, " rp8Activity onReceiveLanPassThrough mac:" + mac + " [" + dataString);

//            TAndL.TL(getApplicationContext(), "LanReceiveData: " + dataString);

            if (mUIHander != null) {
                mUIHander.obtainMessage(0, dataString).sendToTarget();
            }
        }

    }


    private void resolve(String dataString) {

        if (dataString.length() > 10) {
            //TAndL.TL(getApplicationContext(), "透传 result = " + resp.getResult() + " data = " + resp.getDataString() + " errorMsg = " + resp.getErrorMsg());
            String temp = dataString.toUpperCase();
            if (!temp.startsWith("55AA")) {
                temp = "55AA" + temp.substring(10);
                //TAndL.TL(getApplicationContext(), "修复data = " + temp + " errorMsg = " + resp.getErrorMsg());
            }

            Map map = commondUtils.resolve(HexUtil.hexStringToBytes(temp));
            if (null != menuFragment && null != map && !map.isEmpty()) {
                try {
                    menuFragment.switchBtnStatus(map);

                } catch (Exception e) {

                }
                return;
            }
            String passthrough = commondUtils.resolvePassthrough(HexUtil.hexStringToBytes(temp));
            if (null != montiorFragment && null != passthrough && !passthrough.isEmpty()) {
                montiorFragment.runPassthrough(passthrough);
                return;
            }

        }
    }

    @Override
    public void on_0x8101_resp(E_0x8101_Resp resp) {
        super.on_0x8101_resp(resp);
        TAndL.TL(getApplicationContext(), "设置音量结果 " + resp.getResult() + " value = " + resp.getResp().getContent().getValue());
    }

    @Override
    public void onHardwareActivateResult(E_0x8001_Resp_ resp_) {
        super.onHardwareActivateResult(resp_);

        TAndL.TL(getApplicationContext(), "代激活结果 result = " + resp_.getResult() + " errorMsg = " + resp_.getErrorMsg());
    }


//    public C_0x8005.Resp.ContentBean getDevice() {
//        return device;
//    }




    /*
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_device_control_passthrough) {//消息透传
            String hexStr = etHexStr.getText().toString();
            StartAI.getInstance().getBaseBusiManager().passthrough(device.getId(), hexStr, onCallListener);
        } else if (i == R.id.bt_normal_msg) {//待发送的消息
            String msg = etNormal.getText().toString();
            MqttPublishRequest request = new MqttPublishRequest();
            request.topic = device.getTopic();
            request.message = msg;
            StartAI.getInstance().send(request, onCallListener);
        } else if (i == R.id.bt_device_control_miof) {
            C_0x8101.m_0x8101_req(device.getTopic(), device.getId(), 4, onCallListener);
        } else if (i == R.id.bt_hardware_act) {
            C_0x8001.Req.ContentBean contentBean = new C_0x8001.Req.ContentBean();

            contentBean.setAppid("ae6529f2fc52782a6d75db3259257084");
            contentBean.setApptype("smartOlWifi");
            contentBean.setClientid("SNSNSNSNSNSNSNSNSNSNSNSNSNSNSNSN");
            contentBean.setDomain("startai");
            contentBean.setSn("SNSNSNSNSNSNSNSNSNSNSNSNSNSNSNSN");
            contentBean.setM_ver("Json_1.2.9_9.2.1");

            C_0x8001.Req.ContentBean.FirmwareParamBean firmwareParamBean = new C_0x8001.Req.ContentBean.FirmwareParamBean();
            firmwareParamBean.setBluetoothMac("AA:AA:AA:AA:AA:AA");
            firmwareParamBean.setFirmwareVersion("abc");
            contentBean.setFirmwareParam(firmwareParamBean);
            //代智能硬件激活
            StartAI.getInstance().getBaseBusiManager().hardwareActivate(contentBean, onCallListener);
        }
    }*/
}
