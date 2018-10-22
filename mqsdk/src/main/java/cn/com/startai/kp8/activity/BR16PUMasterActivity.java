package cn.com.startai.kp8.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.startai.kp8.fragment.BR16PUMenuFragment;
import cn.com.startai.kp8.fragment.Rp8MontiorFragment;
import cn.com.startai.kp8.util.HexUtil;
import cn.com.startai.kp8.util.Rp86MCommondUtils;
import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.adapter.ViewPagerAdapter;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8001_Resp_;
import cn.com.startai.mqsdk.util.eventbus.E_0x8101_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8200_Resp;
import cn.com.startai.mqsdk.widget.VerticalViewPager;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;


public class BR16PUMasterActivity extends BaseActivity {

    private C_0x8005.Resp.ContentBean device;

    private VerticalViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Rp8MontiorFragment montiorFragment;
    private BR16PUMenuFragment menuFragment;
    private Rp86MCommondUtils commondUtils = new Rp86MCommondUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kp8_layout);

        device = (C_0x8005.Resp.ContentBean) getIntent().getSerializableExtra("device");
        initView();

    }

    private void initView() {
        viewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        menuFragment = new BR16PUMenuFragment();
        fragmentList.add(menuFragment);
        montiorFragment = new Rp8MontiorFragment();
        fragmentList.add(montiorFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, false); // MenuFragment
    }

    public void setViewPagerCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem, false); // BR16PUMenuFragment
    }

    @Override
    public void onPassthrouthResult(E_0x8200_Resp resp) {
        super.onPassthrouthResult(resp);
        //TAndL.TL(getApplicationContext(), "透传 result = " + resp.getResult() + "fromid = " + resp.getResp().getFromid() + " errorMsg = " + resp.getErrorMsg() + " data = " + resp.getDataString()+" dataArr = "+ HexUtil.formatHexString(resp.getDataByteArray()));
        TAndL.TL(getApplicationContext(), "透传 result = " + resp.getResult()+ " errorMsg = " + resp.getErrorMsg() + " data = " + resp.getDataString());
        if(null != resp && resp.getDataString().length() > 10) {
            //TAndL.TL(getApplicationContext(), "透传 result = " + resp.getResult() + " data = " + resp.getDataString() + " errorMsg = " + resp.getErrorMsg());
            String temp = resp.getDataString().toUpperCase();
            if(!temp.startsWith("55AA")){
                temp = "55AA" + temp.substring(10);
                //TAndL.TL(getApplicationContext(), "修复data = " + temp + " errorMsg = " + resp.getErrorMsg());
            }

            Map map = commondUtils.resolve(HexUtil.hexStringToBytes(temp));
            if(null != menuFragment && null != map && !map.isEmpty()) {
                menuFragment.switchBtnStatus(map);
                return;
            }
            String passthrough = commondUtils.resolvePassthrough(HexUtil.hexStringToBytes(temp));
            if(null != montiorFragment && null != passthrough && !passthrough.isEmpty()) {
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


    public C_0x8005.Resp.ContentBean getDevice(){
        return device;
    }
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
