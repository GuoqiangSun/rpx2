package com.o88o.bluetoothrp8.fragment;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.o88o.bluetoothrp8.BR16IRMasterActivity;
import com.o88o.bluetoothrp8.BR16PUMasterActivity;
import com.o88o.bluetoothrp8.BR16WPMasterActivity;
import com.o88o.bluetoothrp8.BaseActivity;
import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.MasterActivity;
import com.o88o.bluetoothrp8.R;
import com.o88o.bluetoothrp8.Rp86MMasterActivity;
import com.o88o.bluetoothrp8.Rp8GTMasterActivity;
import com.o88o.bluetoothrp8.Rp8MasterActivity;
import com.o88o.bluetoothrp8.Rp8OEMRMasterActivity;
import com.o88o.bluetoothrp8.Rp8OEMRXPMasterActivity;
import com.o88o.bluetoothrp8.util.BaseCommond;
import com.o88o.bluetoothrp8.util.BaseCommondUtils;
import com.o88o.bluetoothrp8.util.MetricsUtils;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;
import com.o88o.bluetoothrp8.util.Rp8OEMRCommond;
import com.o88o.bluetoothrp8.util.Rp8OEMRCommondUtils;
import com.o88o.bluetoothrp8.widget.FullScrreenDialog;

import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class Rp8OEMRXPMenuFragment extends Fragment {

    public  static final String FW_ID = "BR08XP11";
    private ColorStateList color_gray;
    private ColorStateList color_red;
    private ColorStateList color_green;
    private ColorStateList color_white;
    private View rootView;
    private RelativeLayout btnLayout;

    private TextView panel_tv_1,panel_tv_2,panel_tv_3,panel_tv_m,panel_tv_b,panel_tv_s,panel_tv_power,panel_tv_nav;
    private Button btn_1,panel_btn_1;
    private Button btn_2,panel_btn_2;
    private Button btn_b,panel_btn_b;
    private Button btn_m,panel_btn_m;
    private Button btn_s,panel_btn_s;
    private Button btn_on_off,panel_btn_power;

    private Button btn_nav,panel_btn_nav;
    private Button btn_3,panel_btn_3;
    private Button panel_btn_anchor;
    private Button btn_brand;

    private Button rp8_gear;
    private ImageView layout_logo;

    private BluetoothService mBluetoothService;
    public  static final String UUID_DEVICE = "49535343";
    public  static final String UUID_DEVICE_CHARACTERISTIC = "49535343";
    public  static final String DEVICE_NAME = "RP-8";

    private Integer boolBtn_1 = 0;
    private Integer boolBtn_2 = 0;
    private Integer boolBtn_b = 0;
    private Integer boolBtn_m = 0;
    private Integer boolBtn_s = 0;
    private Integer boolBtn_on_off = 0;

    private Integer boolBtn_3 = 0;
    private Integer boolPanel_nav = 0;
    private Integer boolPanel_anchor = 0;
    private Integer bollblink_live = 0,bollblink_live2 = 0;;

    private Integer blink_wifi_rate = 0;

    private final int OPEN_BREATH_INTERVAL_TIME = 200; //设置呼吸灯时间间隔
    private final int CLOSE_BREATH_INTERVAL_TIME = 200; //设置呼吸灯时间间隔
    private AlphaAnimation animationFadeIn;
    private AlphaAnimation animationFadeOut;
    private AlphaAnimation animationFadeInVariable;
    private AlphaAnimation animationFadeOutVariable;

    private boolean powerChange = false;
    private ImageView layout_wifi;
    private ImageView layout_bt;
    private Rp8OEMRCommondUtils commondUtils= new Rp8OEMRCommondUtils();
    private FullScrreenDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rp8oemrxp_fragment_menu, null, false);
        bindView();
        return rootView;
    }

    public boolean initBluetoothService() {
        mBluetoothService = ((BaseActivity) getActivity()).getBluetoothService();
        if(null == mBluetoothService){
            ((BaseActivity) getActivity()).setViewPagerCurrentItem(1);
            return false;
        }
        BluetoothGatt gatt = mBluetoothService.getGatt();
        BluetoothGattService service = null;
        for (final BluetoothGattService service1 : gatt.getServices()) {
            if(service1.getUuid().toString().startsWith(UUID_DEVICE)){
                mBluetoothService.setService(service1);
                service = service1;
            }
        }
        if(null == service){
            mBluetoothService.closeConnect();
            mBluetoothService = null;
            ((BaseActivity) getActivity()).setViewPagerCurrentItem(1);
            return false;
        }
        //BluetoothGattService service = mBluetoothService.getService();
        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            int charaProp = characteristic.getProperties();
            //if(UUID_DEVICE_CHARACTERISTIC.equals(characteristic.getUuid().toString())) {
            if(characteristic.getUuid().toString().startsWith(UUID_DEVICE_CHARACTERISTIC)) {
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0&& (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {//) {
                    mBluetoothService.setCharacteristic(characteristic);
                    mBluetoothService.setCharaProp(charaProp);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    bindBluetoothNotify(characteristic);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {

                }
            }
        }
        Rp8DeviceUtils.saveDeviceUuid(getActivity(), service.getUuid().toString());
        Rp8DeviceUtils.saveDeviceMac(getActivity(), Rp8DeviceUtils.getDeviceMacInfo(getActivity()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommond(Rp8OEMRCommond.HEX_REQUEST_VERSION + Rp8OEMRCommondUtils.crc8(Rp8OEMRCommond.HEX_REQUEST_VERSION));

            }
        }, 100);

        Toast.makeText(getActivity(), "send firt commend : "+Rp8OEMRCommond.HEX_REQUEST_VERSION + Rp8OEMRCommondUtils.crc8(Rp8OEMRCommond.HEX_REQUEST_VERSION), Toast.LENGTH_LONG).show();
        return true;
    }

    private void bindBluetoothNotify(BluetoothGattCharacteristic characteristic) {
        mBluetoothService.notify(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleCharacterCallback() {

                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "receive commend : "+HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
                            }
                        });*/

                        Map commondMap = commondUtils.resolve(characteristic.getValue());
                        if(null != commondMap && !commondMap.isEmpty()) {
                            getActivity().runOnUiThread(new MyRunnable(commondMap));
                        }
                        String version = commondUtils.resolveVersion(characteristic.getValue());
                        if(null != version && !version.isEmpty()) {
                            getActivity().runOnUiThread(new SwitchVersionRunnable(version));
                            return;
                        }

                        int auth = commondUtils.resolveAuth(getActivity(),characteristic.getValue());
                        if(-1 != auth){
                            getActivity().runOnUiThread(new SwitchAuthRunnable(auth));
                            if(1 == auth){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "收到点击实体按键的数据" + HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "收到的数据" + HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            return;
                        }
                    }

                    @Override
                    public void onFailure(final BleException exception) {
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {

                    }

                });
    }


    private void bindView(){

        color_gray = AppCompatResources.getColorStateList(getContext(), R.color.color_gray);
        color_red = AppCompatResources.getColorStateList(getContext(), R.color.color_red);
        color_green = AppCompatResources.getColorStateList(getContext(), R.color.color_green);
        color_white = AppCompatResources.getColorStateList(getContext(), R.color.color_white);
        btnLayout = (RelativeLayout)rootView.findViewById(R.id.btn_layout);

        dialog = new FullScrreenDialog(getContext());
        btn_on_off = (Button)rootView.findViewById(R.id.btn_on_off);
        btn_1 = (Button)rootView.findViewById(R.id.btn_1);
        btn_2 = (Button)rootView.findViewById(R.id.btn_2);
        btn_b = (Button)rootView.findViewById(R.id.btn_b);
        btn_s = (Button)rootView.findViewById(R.id.btn_s);
        btn_m = (Button)rootView.findViewById(R.id.btn_m);
        btn_3 = (Button)rootView.findViewById(R.id.btn_3);
        btn_nav = (Button)rootView.findViewById(R.id.btn_nav);
        btn_brand = (Button)rootView.findViewById(R.id.btn_brand);

        layout_wifi = (ImageView)rootView.findViewById(R.id.layout_wifi);
        layout_bt = (ImageView)rootView.findViewById(R.id.layout_bt);
        layout_logo = (ImageView)rootView.findViewById(R.id.layout_logo);

        //down
        panel_btn_anchor = (Button)rootView.findViewById(R.id.panel_btn_anchor);
        panel_btn_1 = (Button)rootView.findViewById(R.id.panel_btn_1);
        panel_btn_2 = (Button)rootView.findViewById(R.id.panel_btn_2);
        panel_btn_b = (Button)rootView.findViewById(R.id.panel_btn_b);
        panel_btn_s = (Button)rootView.findViewById(R.id.panel_btn_s);
        panel_btn_m = (Button)rootView.findViewById(R.id.panel_btn_m);
        panel_btn_3 = (Button)rootView.findViewById(R.id.panel_btn_3);
        panel_btn_nav = (Button)rootView.findViewById(R.id.panel_btn_nav);
        //panel_logo = (ImageView)rootView.findViewById(R.id.panel_logo);
        panel_btn_power = (Button)rootView.findViewById(R.id.panel_btn_power);
        rp8_gear = (Button)rootView.findViewById(R.id.rp8_gear);

        //panel_tv_1,panel_tv_2,panel_tv_3,panel_tv_m,panel_tv_b,panel_tv_s,panel_tv_power,panel_tv_nav;
        panel_tv_1 = (TextView) rootView.findViewById(R.id.panel_tv_1);
        panel_tv_2 = (TextView)rootView.findViewById(R.id.panel_tv_2);
        panel_tv_b = (TextView)rootView.findViewById(R.id.panel_tv_b);
        panel_tv_s = (TextView)rootView.findViewById(R.id.panel_tv_s);
        panel_tv_m = (TextView)rootView.findViewById(R.id.panel_tv_m);
        panel_tv_3 = (TextView)rootView.findViewById(R.id.panel_tv_3);
        panel_tv_nav = (TextView)rootView.findViewById(R.id.panel_tv_nav);
        panel_tv_power = (TextView)rootView.findViewById(R.id.panel_tv_power);
        autoAdatper();


        View.OnClickListener btnOnClickListener = getBtnOnClickListener();
        rp8_gear.setOnClickListener(btnOnClickListener);

        btn_1.setOnClickListener(btnOnClickListener);
        //btn_2.setOnClickListener(btnOnClickListener);
        btn_b.setOnClickListener(btnOnClickListener);
        btn_s.setOnClickListener(btnOnClickListener);
        btn_m.setOnClickListener(btnOnClickListener);
        btn_on_off.setOnClickListener(btnOnClickListener);
        btn_3.setOnClickListener(btnOnClickListener);
        btn_nav.setOnClickListener(btnOnClickListener);

        //down
        panel_btn_1.setOnClickListener(btnOnClickListener);
        //panel_btn_2.setOnClickListener(btnOnClickListener);
        panel_btn_b.setOnClickListener(btnOnClickListener);
        panel_btn_s.setOnClickListener(btnOnClickListener);
        panel_btn_m.setOnClickListener(btnOnClickListener);
        //panel_btn_anchor.setOnClickListener(btnOnClickListener);
        panel_btn_3.setOnClickListener(btnOnClickListener);
        panel_btn_nav.setOnClickListener(btnOnClickListener);
        panel_btn_power.setOnClickListener(btnOnClickListener);
        // 长按 和 抬起
        btn_2.setOnClickListener(btnOnClickListener);
        panel_btn_2.setOnClickListener(btnOnClickListener);
        //// TODO: 2018/4/6  禁止按钮点击
        //disableButtons(false);

        animationFadeIn = new AlphaAnimation(0.3f, 1.0f);
        animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeOut = new AlphaAnimation(1.0f, 0.3f);
        animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeIn.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(1 == bollblink_live) {
                    btn_3.startAnimation(animationFadeOut);
                    panel_btn_3.startAnimation(animationFadeOut);
                    //panel_tv_3.startAnimation(animationFadeOut);
                }
                if(1 == bollblink_live2) {
                    btn_2.startAnimation(animationFadeOut);
                    panel_btn_2.startAnimation(animationFadeOut);
                    //panel_tv_2.startAnimation(animationFadeOut);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

        animationFadeOut.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(1 == bollblink_live) {
                    btn_3.startAnimation(animationFadeIn);
                    panel_btn_3.startAnimation(animationFadeIn);
                    //panel_tv_3.startAnimation(animationFadeIn);
                }
                if(1 == bollblink_live2) {
                    btn_2.startAnimation(animationFadeIn);
                    panel_btn_2.startAnimation(animationFadeIn);
                    //panel_tv_2.startAnimation(animationFadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });
        animationFadeInVariable = new AlphaAnimation(0.3f, 1.0f);
        animationFadeOutVariable = new AlphaAnimation(1.0f, 0.3f);
        animationFadeInVariable.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(0 != blink_wifi_rate){
                    layout_wifi.startAnimation(animationFadeOutVariable);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationFadeOutVariable.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(0 != blink_wifi_rate){
                    layout_wifi.startAnimation(animationFadeInVariable);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void autoAdatper() {
        int widthWin = MetricsUtils.getWindowWidth(getActivity());
        int heightWin = MetricsUtils.getWindowHeigh(getActivity());
        int height = heightWin / 2 - MetricsUtils.dip2px(getActivity(),40);
        int width = widthWin;
        //up
        ViewGroup.LayoutParams layoutParams = btnLayout.getLayoutParams();
        double scaleRatio = 1;
        if(height *layoutParams.width / layoutParams.height  > width){
            height = width * layoutParams.height / layoutParams.width;
            scaleRatio =  (width * 1.0 / layoutParams.width);
        }else{
            width = height * layoutParams.width / layoutParams.height ;
            scaleRatio =  (height * 1.0 / layoutParams.height);
        }

        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        btnLayout.setLayoutParams(layoutParams);

        scaleRelativeLayout(btn_on_off,scaleRatio);
        scaleRelativeLayout(btn_b,scaleRatio);
        scaleRelativeLayout(btn_m,scaleRatio);
        scaleRelativeLayout(btn_s,scaleRatio);
        scaleRelativeLayout(btn_1,scaleRatio);
        scaleRelativeLayout(btn_2,scaleRatio);
        scaleRelativeLayout(btn_3,scaleRatio);
        scaleRelativeLayout(btn_nav,scaleRatio);
        scaleRelativeLayout(btn_brand,scaleRatio);

        MetricsUtils.setTextSize((float) scaleRatio * MetricsUtils.sp2px(getContext(),16),
                btn_1,btn_2,btn_3,btn_b,btn_m,btn_s,btn_nav);
        MetricsUtils.setTextSize((float) scaleRatio * MetricsUtils.sp2px(getContext(),8),
                btn_brand);

        /*layoutParams = layout_logo.getLayoutParams();
        layoutParams.width = (int) (layoutParams.width * scaleRatio + 0.5);
        layoutParams.height = (int) (layoutParams.height * scaleRatio + 0.5);
        layout_logo.setLayoutParams(layoutParams);*/
        scaleRelativeLayout(layout_logo,scaleRatio);

        scaleRelativeLayout(layout_bt,scaleRatio);
        scaleRelativeLayout(layout_wifi,scaleRatio);
        //down
        /*
        height = heightWin / 2;
        width = (int)(widthWin * 0.9 + 0.5);

        int radis = width /8 - MetricsUtils.dip2px(getActivity(),4);
        int linearLayout_marginBottom = 0;
        int btn_margin = MetricsUtils.dip2px(getActivity(),2);
        if(height * 0.35 > radis){
            linearLayout_marginBottom = (int)((height * 0.35 - radis)/2 + 0.5);
        }else{
            radis = (int)(height * 0.35 + 0.5);
            btn_margin = (int)(width /8 - radis + 0.5)/2;
        }
        RelativeLayout.LayoutParams relativeLayoutParams  = (RelativeLayout.LayoutParams)linearLayoutDown.getLayoutParams();
        scaleRatio = radis * 1.0 / relativeLayoutParams.height;
        relativeLayoutParams.height = radis;
        relativeLayoutParams.width = width;
        relativeLayoutParams.setMargins(0,0,0,linearLayout_marginBottom);
        linearLayoutDown.setLayoutParams(relativeLayoutParams);

        relativeLayoutParams = (RelativeLayout.LayoutParams)panel_logo.getLayoutParams();
        relativeLayoutParams.width = (int) (relativeLayoutParams.width * scaleRatio + 0.5);
        relativeLayoutParams.height = (int) (relativeLayoutParams.height * scaleRatio + 0.5);
        relativeLayoutParams.setMargins(0,0,0,(int)(height * 0.35));//height * 0.7 / 2
        panel_logo.setLayoutParams(relativeLayoutParams);

        setBtnLayoutDown(panel_btn_b,radis,btn_margin);
        setBtnLayoutDown(panel_btn_m,radis,btn_margin);
        setBtnLayoutDown(panel_btn_s,radis,btn_margin);
        setBtnLayoutDown(panel_btn_1,radis,btn_margin);
        setBtnLayoutDown(panel_btn_2,radis,btn_margin);
        setBtnLayoutDown(panel_btn_3,radis,btn_margin);
        setBtnLayoutDown(panel_btn_nav,radis,btn_margin);
        setBtnLayoutDown(panel_btn_anchor,radis,btn_margin);*/
    }

    private void setBtnLayoutDown(View btn, int radis, int btn_margin) {
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = radis;
        relativeLayoutParams.height = radis;
        relativeLayoutParams.setMargins(btn_margin,
                relativeLayoutParams.topMargin,
                btn_margin,
                relativeLayoutParams.bottomMargin);
        btn.setLayoutParams(relativeLayoutParams);
    }

    private void scaleRelativeLayout(View btn, double scaleRatio) {
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = (int) (relativeLayoutParams.width * scaleRatio + 0.5);
        relativeLayoutParams.height = (int) (relativeLayoutParams.height * scaleRatio + 0.5);
        relativeLayoutParams.setMargins((int)(relativeLayoutParams.leftMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.topMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.rightMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.bottomMargin * scaleRatio + 0.5));
        btn.setLayoutParams(relativeLayoutParams);
    }
    private View.OnClickListener getBtnOnClickListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mBluetoothService || !((BaseActivity) getActivity()).isConnect()) {
                    ((BaseActivity) getActivity()).setViewPagerCurrentItem(1);
                    return;
                }
                sendCommond(genCommond(view.getId()));

            }
        };
    }
    public String genCommond(int viewId){
        StringBuffer hexCommond = new StringBuffer();
        hexCommond.append(Rp8OEMRCommond.HEX_COMMOND_START);
        hexCommond.append(Rp8OEMRCommond.HEX_COMMOND_DATA_LENGTH);
        hexCommond.append(Rp8OEMRCommond.HEX_COMMOND_TYPE_GET);
        // 只发键值，状态由继电器控制切换
        switch (viewId) {
            case R.id.panel_btn_1:
            case R.id.btn_1:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE7);
                break;
            case R.id.panel_btn_2:
            case R.id.btn_2:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE8);
                break;
            case R.id.panel_btn_b:
            case R.id.btn_b:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE4);
                break;
            case R.id.panel_btn_s:
            case R.id.btn_s:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE6);
                break;
            case R.id.panel_btn_m:
            case R.id.btn_m:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE5);
                break;
            case R.id.btn_on_off:
            case R.id.panel_btn_power:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE1);
                break;
            case R.id.panel_btn_3:
            case R.id.btn_3:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE9);
                break;
            /*case R.id.panel_btn_nav:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE2);
                break;
            case R.id.panel_btn_anchor:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE3);
                break;*/
            case R.id.panel_btn_nav:
            case R.id.btn_nav:
                hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_DEVICE10);
                break;
            default:
        }
        hexCommond.append(Rp8OEMRCommond.HEX_REQUEST_FUNC_DATA);
        hexCommond.append(Rp8OEMRCommondUtils.crc8(hexCommond.toString()));
        return  hexCommond.toString();
    }

    public void sendCommond(String hexCommond){
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        if(null == characteristic){
            mBluetoothService.closeConnect();
            ((BaseActivity) getActivity()).setViewPagerCurrentItem(1);
            return;
        }
        final int charaProp = mBluetoothService.getCharaProp();
        mBluetoothService.write(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                hexCommond,
                null
                /*new BleCharacterCallback() {
                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        //Map commondMap = Rp8OEMRCommondUtils.resolveResponseCommond(HexUtil.formatHexString(characteristic.getValue()));

                        //switchBtnStatus(commondMap);
                    }

                    @Override
                    public void onFailure(final BleException exception) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "send Fail", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {

                    }
                }*/);
    }
    public  void  switchBtnStatus(Map<Integer,Integer> commondMap){
        if(null != commondMap && commondMap.size() > 0 && commondMap.get(R.id.btn_on_off) != boolBtn_on_off){
            powerChange = true;
        }

        for (Map.Entry<Integer, Integer> en : commondMap.entrySet()) {
            switch (en.getKey()) {
                case R.id.btn_on_off:
                    if (1 == en.getValue()) {
                        //btn_on_off.setBackgroundResource(R.mipmap.btn_on_red);//红色
                        btn_on_off.setBackgroundResource(R.mipmap.rp8_btn_off); // 白色
                        layout_logo.setBackgroundResource(R.mipmap.rp8_layout_logo);
                        panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off);
                        panel_tv_power.setText(R.string.menu_power_on);
                        panel_tv_power.setTextColor(color_gray);
                        boolBtn_on_off = 1;
                    } else {
                        btn_on_off.setBackgroundResource(R.mipmap.rp8_btn_off_gray); //灰色
                        layout_logo.setBackgroundResource(R.mipmap.rp8_layout_logo_gray);
                        panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off_gray);
                        panel_tv_power.setText(R.string.menu_power_off);
                        panel_tv_power.setTextColor(color_gray);
                        boolBtn_on_off = 0;
                    }
                    break;
                case R.id.btn_3:
                    if (1 == en.getValue()) {
                        //btn_on_off.setBackgroundResource(R.mipmap.btn_on_red);//红色
                        //btn_3.setBackgroundResource(R.mipmap.oem_live_red);//
                        btn_3.setTextColor(color_red);
                        panel_btn_3.setTextColor(color_red);
                        panel_tv_3.setText(R.string.menu_live_on);
                        panel_tv_3.setTextColor(color_gray);
                        //panel_btn_3.setBackgroundResource(R.mipmap.relay_panel_live_red);//
                        boolBtn_3 = 1;
                    } else {
                        //btn_3.setBackgroundResource(R.mipmap.oem_live);
                        //panel_btn_3.setBackgroundResource(R.mipmap.relay_panel_live);//白色
                        btn_3.setTextColor(color_white);
                        panel_btn_3.setTextColor(color_white);//白色
                        panel_tv_3.setText(R.string.menu_live_off);
                        panel_tv_3.setTextColor(color_gray);
                        boolBtn_3 = 0;
                    }
                    break;
                case R.id.btn_2:
                    if (1 == en.getValue()) {
                        btn_2.setTextColor(color_red);
                        panel_btn_2.setTextColor(color_red);//.setBackgroundResource(R.mipmap.rp8r_panel_horn_red);//
                        panel_tv_2.setText(R.string.menu_live_on);
                        panel_tv_2.setTextColor(color_gray);
                        boolBtn_2 = 1;
                    } else {
                        btn_2.setTextColor(color_white);
                        panel_btn_2.setTextColor(color_white);//白色
                        panel_tv_2.setText(R.string.menu_live_off);
                        panel_tv_2.setTextColor(color_gray);
                        boolBtn_2 = 0;
                    }
                    break;

                case R.id.btn_1:
                    if (1 == en.getValue()) {/*
                        btn_1.setBackgroundResource(R.mipmap.oem_acc2_red);
                        panel_btn_1.setBackgroundResource(R.mipmap.relay_panel_ac2_red);*///
                        btn_1.setTextColor(color_red);
                        panel_btn_1.setTextColor(color_red);
                        panel_tv_1.setText(R.string.menu_acc2_on);
                        panel_tv_1.setTextColor(color_gray);
                        boolBtn_1 = 1;
                    } else {
                        /*btn_1.setBackgroundResource(R.mipmap.oem_acc2);
                        panel_btn_1.setBackgroundResource(R.mipmap.relay_panel_ac2);//白色*/
                        btn_1.setTextColor(color_white);
                        panel_btn_1.setTextColor(color_white);
                        panel_tv_1.setText(R.string.menu_acc2_off);
                        panel_tv_1.setTextColor(color_gray);
                        boolBtn_1 = 0;
                    }
                    break;
                case R.id.btn_b:
                    if (1 == en.getValue()) {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg_red);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg_red);*///
                        btn_b.setTextColor(color_red);
                        panel_btn_b.setTextColor(color_red);
                        panel_tv_b.setText(R.string.menu_blg_on);
                        panel_tv_b.setTextColor(color_gray);
                        boolBtn_b = 1;
                    } else {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg);*/
                        btn_b.setTextColor(color_white);
                        panel_btn_b.setTextColor(color_white);
                        panel_tv_b.setText(R.string.menu_blg_off);
                        panel_tv_b.setTextColor(color_gray);
                        boolBtn_b = 0;
                    }
                    break;
                case R.id.btn_m:
                    if (1 == en.getValue()) {
                        /*btn_m.setBackgroundResource(R.mipmap.oem_int_red);
                        panel_btn_m.setBackgroundResource(R.mipmap.relay_panel_int_red);*/
                        btn_m.setTextColor(color_red);
                        panel_btn_m.setTextColor(color_red);
                        panel_tv_m.setText(R.string.menu_int_on);
                        panel_tv_m.setTextColor(color_gray);
                        boolBtn_m = 1;
                    } else {
                        /*btn_m.setBackgroundResource(R.mipmap.oem_int);
                        panel_btn_m.setBackgroundResource(R.mipmap.relay_panel_int);*/
                        btn_m.setTextColor(color_white);
                        panel_btn_m.setTextColor(color_white);
                        panel_tv_m.setText(R.string.menu_int_off);
                        panel_tv_m.setTextColor(color_gray);
                        boolBtn_m = 0;
                    }
                    break;
                case R.id.btn_s:
                    if (1 == en.getValue()) {
                        /*btn_s.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_s.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                        btn_s.setTextColor(color_red);
                        panel_btn_s.setTextColor(color_red);
                        panel_tv_s.setText(R.string.menu_acc1_on);
                        panel_tv_s.setTextColor(color_gray);
                        boolBtn_s = 1;
                    } else {
                        /*btn_s.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_s.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                        btn_s.setTextColor(color_white);
                        panel_btn_s.setTextColor(color_white);
                        panel_tv_s.setText(R.string.menu_acc1_off);
                        panel_tv_s.setTextColor(color_gray);
                        boolBtn_s = 0;
                    }
                    break;

                case R.id.panel_btn_anchor:
                    if (1 == en.getValue()) {
                        //panel_btn_anchor.setBackgroundResource(R.mipmap.relay_panel_anchor_red);
                        boolPanel_anchor = 1;
                    } else {
                        //panel_btn_anchor.setBackgroundResource(R.mipmap.relay_panel_anchor);
                        boolPanel_anchor = 0;
                    }
                    break;
                case R.id.panel_btn_nav:
                    if (1 == en.getValue()) {
                        //panel_btn_nav.setBackgroundResource(R.mipmap.relay_panel_nav_red);
                        boolPanel_nav = 1;
                    } else {
                        //panel_btn_nav.setBackgroundResource(R.mipmap.relay_panel_nav);
                        boolPanel_nav = 0;
                    }
                    break;
                case R.id.blink_live:

                    break;
                case R.id.layout_bt:
                    if (1 == en.getValue()) {
                        layout_bt.setBackgroundResource(R.mipmap.bt_on);
                    }else{
                        layout_bt.setBackgroundResource(R.mipmap.bt_off);
                    }

                    break;
                case R.id.layout_wifi:
                    if (1 == en.getValue()) {
                        layout_wifi.setBackgroundResource(R.mipmap.wifi_on);
                    }else{
                        layout_wifi.setBackgroundResource(R.mipmap.wifi_off);
                    }
                    break;
                default:

            }

        }
        //根据 boolPanel_anchor 和 boolPanel_nav 判断 白红绿
        if(1 == boolPanel_nav && 1 == boolPanel_anchor){
            btn_nav.setTextColor(color_green);
            panel_btn_nav.setTextColor(color_green);
            panel_tv_nav.setText(R.string.menu_nav_on);
            panel_tv_nav.setTextColor(color_gray);
        }else if(0 == boolPanel_nav && 1 == boolPanel_anchor){
            btn_nav.setTextColor(color_red);
            panel_btn_nav.setTextColor(color_red);
            panel_tv_nav.setText(R.string.menu_nav_on);
            panel_tv_nav.setTextColor(color_gray);
        }if(0 == boolPanel_nav && 0 == boolPanel_anchor){
            btn_nav.setTextColor(color_white);
            panel_btn_nav.setTextColor(color_white);
            panel_tv_nav.setText(R.string.menu_nav_off);
            panel_tv_nav.setTextColor(color_gray);
        }
        if( null != commondMap.get(R.id.blink_wifi_rate) && commondMap.get(R.id.blink_wifi_rate) != blink_wifi_rate){
            if(0 != commondMap.get(R.id.blink_wifi_rate)){
                int time = OPEN_BREATH_INTERVAL_TIME /commondMap.get(R.id.blink_wifi_rate);

                animationFadeOutVariable.setDuration(time);
                animationFadeInVariable.setDuration(time);
                layout_wifi.startAnimation(animationFadeOutVariable);
            } else{
                layout_wifi.clearAnimation();
            }
            blink_wifi_rate = commondMap.get(R.id.blink_wifi_rate);
        }
        if(null != commondMap && commondMap.size() > 0 && null != commondMap.get(R.id.blink_live) && commondMap.get(R.id.blink_live) != bollblink_live){
            if(1 == commondMap.get(R.id.blink_live )){
                bollblink_live = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                panel_tv_3.setText(R.string.menu_live_interval);
                btn_3.startAnimation(animationFadeOut);
            } else{
                btn_3.clearAnimation();
                bollblink_live = 0;
                    /*animationFadeOutIn.setDuration(CLOSE_BREATH_INTERVAL_TIME);
                    btn_3.startAnimation(animationFadeOutIn);*/
            }

        }
        if(null != commondMap && commondMap.size() > 0 && null != commondMap.get(R.id.blink_live2) && commondMap.get(R.id.blink_live2) != bollblink_live2){
            if(1 == commondMap.get(R.id.blink_live2 )){
                bollblink_live2 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                panel_tv_2.setText(R.string.menu_live_interval);
                btn_2.startAnimation(animationFadeOut);
            } else{
                btn_2.clearAnimation();
                bollblink_live2 = 0;
                    /*animationFadeOutIn.setDuration(CLOSE_BREATH_INTERVAL_TIME);
                    btn_3.startAnimation(animationFadeOutIn);*/
            }

        }
        if(powerChange) {
            disableButtons(1 == boolBtn_on_off);
            powerChange = false;
        }
        if(0 == boolBtn_on_off) {
            disableButtons(1 == boolBtn_on_off ? true : false);
        }
        btn_on_off.setEnabled(true);
    }

    public void disableButtons(boolean isEnabled){
        btn_3.setEnabled(isEnabled);
        btn_2.setEnabled(isEnabled);
        btn_1.setEnabled(isEnabled);
        btn_b.setEnabled(isEnabled);
        btn_s.setEnabled(isEnabled);
        btn_m.setEnabled(isEnabled);
        btn_on_off.setEnabled(isEnabled);

        btn_m.setEnabled(isEnabled);
        btn_nav.setEnabled(isEnabled);

        panel_btn_3.setEnabled(isEnabled);
        panel_btn_2.setEnabled(isEnabled);
        panel_btn_1.setEnabled(isEnabled);
        panel_btn_b.setEnabled(isEnabled);
        panel_btn_s.setEnabled(isEnabled);
        panel_btn_m.setEnabled(isEnabled);
        panel_btn_nav.setEnabled(isEnabled);
        panel_btn_anchor.setEnabled(isEnabled);

        if(!isEnabled){
            grayButtons();
            stopAnimation();
        }
    }

    private void stopAnimation() {
        layout_wifi.clearAnimation();
        btn_3.clearAnimation();
        btn_2.clearAnimation();

        blink_wifi_rate = 0;
        bollblink_live = 0;
        bollblink_live2 = 0;
    }
    private void grayButtons(){
        btn_3.setTextColor(color_gray);//.setBackgroundResource(R.mipmap.oem_live_gray);
        btn_2.setTextColor(color_gray);
        btn_on_off.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_off);
        btn_1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc2_gray);
        btn_b.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_blg_gray);
        btn_m.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_int_gray);
        btn_s.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc1_gray);
        btn_nav.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_nav_gray);

        panel_btn_3.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_live_gray);
        panel_btn_2.setTextColor(color_gray);
        panel_btn_1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac2_gray);
        panel_btn_b.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_blg_gray);
        panel_btn_s.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac1_gray);
        panel_btn_m.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_int_gray);
        panel_btn_power.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_nav_gray);
        //panel_btn_anchor.setBackgroundResource(R.mipmap.relay_panel_anchor_gray);

        //layout_logo.setBackgroundResource(R.mipmap.oem_layout_logo_gray);

    }

    private class MyRunnable implements Runnable {
        private  Map commondMap;
        public MyRunnable(Map commondMap) {
            this.commondMap = commondMap;
        }

        @Override
        public void run() {
            switchBtnStatus(commondMap);
        }
    }

    private class SwitchVersionRunnable implements Runnable {
        private  String version;
        public SwitchVersionRunnable(String version) {
            this.version = version;
        }

        @Override
        public void run() {

            if(FW_ID.equals(version) || null == version || version.isEmpty()){//发送 请求 是否可以连接；取本地的uid，为空是默认0；
                //commondUtils.isAuth = false;
                String uid = Rp8DeviceUtils.getRpXStringValue(getActivity(), BaseCommond.HEX_COMMOND_DT_BR08,"0000000000");
                String hexCommond = BaseCommond.HEX_COMMOND_START
                        + String.format("%02d",uid.length()/2 + 2)
                        + BaseCommond.HEX_COMMOND_TYPE_NOTIFICATION_18
                        + BaseCommond.HEX_COMMOND_DT_BR08
                        + uid;
                hexCommond = hexCommond + BaseCommondUtils.crc8(hexCommond);
                sendCommond(hexCommond);
                Toast.makeText(getActivity(),"解锁请求主机"+hexCommond,Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(),version,Toast.LENGTH_LONG).show();
            Rp8DeviceUtils.saveRpType(getActivity(), version);
            Intent intent = new Intent(getActivity(), Rp8MasterActivity.class);
            switch (version){
                case Rp8MenuFragment.FW_ID:
                    intent = new Intent(getActivity(), Rp8MasterActivity.class);
                    break;
                case MenuFragment.FW_ID:
                    intent = new Intent(getActivity(), MasterActivity.class);
                    break;
                case Rp8GTMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), Rp8GTMasterActivity.class);
                    break;
                case Rp8OEMRMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), Rp8OEMRMasterActivity.class);
                    break;
                case Rp8OEMRXPMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), Rp8OEMRXPMasterActivity.class);
                    break;
                case Rp86MMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), Rp86MMasterActivity.class);
                    break;
                case BR16PUMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), BR16PUMasterActivity.class);
                    break;
                case BR16IRMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), BR16IRMasterActivity.class);
                    break;
                case BR16WPMenuFragment.FW_ID:
                    intent = new Intent(getActivity(), BR16WPMasterActivity.class);
                    break;
            }

            startActivity(intent);
            getActivity().finish();
        }
    }
    private class SwitchAuthRunnable implements Runnable {
        private int auth;
        public SwitchAuthRunnable(int auth) {
            this.auth = auth;
        }

        @Override
        public void run() {
            if(0 == auth){
                // 显示提示
                if(null != dialog) {
                    dialog.show();

                }

            }else if (1 == auth){
                // 隐藏提示
                if(null != dialog) {
                    dialog.dismiss();
                }
                ((BaseActivity)getActivity()).unlock();
                ((BaseActivity)getActivity()).setViewPagerCurrentItem(0);
            } else {
                // timeout
                if(null != dialog) {
                    dialog.dismiss();
                }
                Toast.makeText(getActivity(),R.string.bluetooth_click_timeout,Toast.LENGTH_LONG).show();
            }
        }
    }
}
