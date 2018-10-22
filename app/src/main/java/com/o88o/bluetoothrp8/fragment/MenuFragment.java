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
import android.widget.ArrayAdapter;
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
import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.MasterActivity;
import com.o88o.bluetoothrp8.R;
import com.o88o.bluetoothrp8.Rp86MMasterActivity;
import com.o88o.bluetoothrp8.Rp8GTMasterActivity;
import com.o88o.bluetoothrp8.Rp8MasterActivity;
import com.o88o.bluetoothrp8.Rp8OEMRMasterActivity;
import com.o88o.bluetoothrp8.Rp8OEMRXPMasterActivity;
import com.o88o.bluetoothrp8.util.BaseCommondUtils;
import com.o88o.bluetoothrp8.util.Commond;
import com.o88o.bluetoothrp8.util.CommondUtils;
import com.o88o.bluetoothrp8.util.MetricsUtils;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;

import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class MenuFragment extends Fragment {
    public  static final String FW_ID = "BR05RT11";
    private View rootView;

    private ColorStateList color_gray;
    private ColorStateList color_red;
    private ColorStateList color_green;
    private ColorStateList color_white;
    private ArrayAdapter adapter = null;

    private ImageView menuBooter;
    private RelativeLayout btnLayout;
    private BluetoothService mBluetoothService;
    private TextView panel_tv_1,panel_tv_2,panel_tv_3,panel_tv_m,panel_tv_b,panel_tv_s,panel_tv_power,panel_tv_nav;
    private Button btn_1,panel_btn_1;
    private Button btn_2,panel_btn_2;
    private Button btn_b,panel_btn_b;
    private Button btn_m,panel_btn_m;
    private Button btn_s,panel_btn_s;
    private Button btn_on_off,panel_btn_power;
    public  static final String UUID_DEVICE = "49535343";
    public  static final String UUID_DEVICE_CHARACTERISTIC = "49535343";
    public  static final String DEVICE_NAME = "RP-5";
    private boolean boolBtn_1 = false;
    private boolean boolBtn_2 = false;
    private boolean boolBtn_b = false;
    private boolean boolBtn_m = false;
    private boolean boolBtn_s = false;
    private boolean boolBtn_on_off = false;

    private boolean powerChange = false;
    private ImageView iv_pg_below;
    private LinearLayout iv_pg_above;

    private CommondUtils commondUtils = new CommondUtils();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, null, false);
        bindView();
        return rootView;
    }

    public boolean initBluetoothService() {
        mBluetoothService = ((MasterActivity) getActivity()).getBluetoothService();
        if(null == mBluetoothService){
            ((MasterActivity) getActivity()).setViewPagerCurrentItem(1);
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
            ((MasterActivity) getActivity()).setViewPagerCurrentItem(1);
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
                sendCommond(null,Commond.HEX_REQUEST_VERSION + CommondUtils.crc8(Commond.HEX_REQUEST_VERSION));

            }
        }, 1000);

        Toast.makeText(getActivity(), "send firt commend : "+Commond.HEX_REQUEST_VERSION + CommondUtils.crc8(Commond.HEX_REQUEST_VERSION), Toast.LENGTH_LONG).show();
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
                        if(FW_ID.equals(version) || null == version || version.isEmpty()){
                            return;
                        }
                        getActivity().runOnUiThread(new SwitchVersionRunnable(version));
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
        iv_pg_below = (ImageView)rootView.findViewById(R.id.iv_pg_below);
        iv_pg_above = (LinearLayout)rootView.findViewById(R.id.iv_pg_above);

        btn_on_off = (Button)rootView.findViewById(R.id.btn_on_off);
        btn_1 = (Button)rootView.findViewById(R.id.btn_1);
        btn_2 = (Button)rootView.findViewById(R.id.btn_2);
        btn_b = (Button)rootView.findViewById(R.id.btn_b);
        btn_s = (Button)rootView.findViewById(R.id.btn_s);
        btn_m = (Button)rootView.findViewById(R.id.btn_m);
        panel_btn_1 = (Button)rootView.findViewById(R.id.panel_btn_1);
        panel_btn_2 = (Button)rootView.findViewById(R.id.panel_btn_2);
        panel_btn_b = (Button)rootView.findViewById(R.id.panel_btn_b);
        panel_btn_s = (Button)rootView.findViewById(R.id.panel_btn_s);
        panel_btn_m = (Button)rootView.findViewById(R.id.panel_btn_m);
        panel_btn_power = (Button)rootView.findViewById(R.id.panel_btn_power);
        //panel_tv_1,panel_tv_2,panel_tv_3,panel_tv_m,panel_tv_b,panel_tv_s,panel_tv_power,panel_tv_nav;
        panel_tv_1 = (TextView) rootView.findViewById(R.id.panel_tv_1);
        panel_tv_2 = (TextView)rootView.findViewById(R.id.panel_tv_2);
        panel_tv_b = (TextView)rootView.findViewById(R.id.panel_tv_b);
        panel_tv_s = (TextView)rootView.findViewById(R.id.panel_tv_s);
        panel_tv_m = (TextView)rootView.findViewById(R.id.panel_tv_m);
        panel_tv_power = (TextView)rootView.findViewById(R.id.panel_tv_power);
        //if(MetricsUtils.isNeedAdapter(getActivity())){  //with>360dp //需要适配
            autoAdatper();
        //}
        //todo
        //disableButtons(false);

        View.OnClickListener btnOnClickListener = getBtnOnClickListener();
        btn_1.setOnClickListener(btnOnClickListener);
        btn_2.setOnClickListener(btnOnClickListener);
        btn_b.setOnClickListener(btnOnClickListener);
        btn_s.setOnClickListener(btnOnClickListener);
        btn_m.setOnClickListener(btnOnClickListener);
        btn_on_off.setOnClickListener(btnOnClickListener);
        //menuBooter.setOnClickListener(btnOnClickListener);
        panel_btn_1.setOnClickListener(btnOnClickListener);
        panel_btn_2.setOnClickListener(btnOnClickListener);
        panel_btn_b.setOnClickListener(btnOnClickListener);
        panel_btn_s.setOnClickListener(btnOnClickListener);
        panel_btn_m.setOnClickListener(btnOnClickListener);
        panel_btn_power.setOnClickListener(btnOnClickListener);
    }

    private void autoAdatper() {
        int widthDp = MetricsUtils.getWindowWidth(getActivity());
        int heightWin = MetricsUtils.getWindowHeigh(getActivity());
        int height = (int)(heightWin * 0.6 + 0.5) - MetricsUtils.dip2px(getActivity(),40);

        ViewGroup.LayoutParams layoutParams = iv_pg_below.getLayoutParams();
        height -= layoutParams.height;

        widthDp = widthDp<height? widthDp:height;
        //widthDp *= 0.8;
        autoAdatpeRadis(btnLayout,(int)(0.88 * widthDp + 0.5));
        layoutParams = iv_pg_above.getLayoutParams();
        layoutParams.height = (int)(0.88 * widthDp + 0.5);
        iv_pg_above.setLayoutParams(layoutParams);

        int btnWidth = (int)(0.155 * widthDp + 0.5);
        int btnRadius = btnWidth/2 ;
        autoAdatpeRadis(btn_1,btnWidth);
        autoAdatpeRadis(btn_2,btnWidth);
        autoAdatpeRadis(btn_b,btnWidth);
        autoAdatpeRadis(btn_s,btnWidth);
        autoAdatpeRadis(btn_m,btnWidth);
        autoAdatpeRadis(btn_on_off,btnWidth);

        double l = 0.19 * widthDp ;
        setLayoutParams(btn_on_off,0,0,0,(int)(l + 0.5 - btnRadius));
        setLayoutParams(btn_1,0,0,(int)(0.5 * 1.732 * l + 0.5 - btnRadius),(int)(0.5 * l + 0.5 - btnRadius));
        setLayoutParams(btn_2,(int)(0.5 * 1.732 * l + 0.5 - btnRadius),0,0,(int)(0.5 * l + 0.5 - btnRadius));
        setLayoutParams(btn_s,(int)(0.5 * 1.732 * l + 0.5 - btnRadius),(int)(0.5 * l + 0.5 - btnRadius),0,0);
        setLayoutParams(btn_b,0,(int)(0.5 * l + 0.5 - btnRadius),(int)(0.5 * 1.732 * l + 0.5 - btnRadius),0);
        setLayoutParams(btn_m,0,(int)(l + 0.5 - btnRadius),0,0);

    }

    private void setLayoutParams(Button btn, int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams relativeLayoutParams  = (RelativeLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.setMargins(left,top,right,bottom);
        btn.setLayoutParams(relativeLayoutParams);
    }

    private void autoAdatpeRadis(View btn, int btnRadis) {
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.height = btnRadis;
        layoutParams.width = btnRadis;
        btn.setLayoutParams(layoutParams);
    }

    private View.OnClickListener getBtnOnClickListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mBluetoothService || !((MasterActivity) getActivity()).isConnect()) {
                    ((MasterActivity) getActivity()).setViewPagerCurrentItem(1);
                    return;
                }
                sendCommond(view, genCommond(view));
            }
        };
    }
    public String genCommond(View view){
        StringBuffer hexCommond = new StringBuffer();
        hexCommond.append(Commond.HEX_COMMOND_START);
        hexCommond.append(Commond.HEX_COMMOND_TYPE_GET);
        hexCommond.append(Commond.HEX_COMMOND_DATA_LENGTH);
        switch (view.getId()) {
            case R.id.panel_btn_1:
            case R.id.btn_1:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE1);
                break;
            case R.id.btn_2:
            case R.id.panel_btn_2:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE2);
                break;
            case R.id.btn_b:
            case R.id.panel_btn_b:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE3);
                break;
            case R.id.btn_s:
            case R.id.panel_btn_s:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE4);
                break;
            case R.id.btn_m:
            case R.id.panel_btn_m:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE5);
                break;
            case R.id.btn_on_off:
            case R.id.panel_btn_power:
                hexCommond.append(Commond.HEX_REQUEST_DEVICE6);
                break;
            default:
        }
        hexCommond.append(Commond.HEX_REQUEST_FUNC_DATA);
        hexCommond.append(CommondUtils.crc8(hexCommond.toString()));
        return  hexCommond.toString();
    }

    public void sendCommond(final View view, String hexCommond){
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        if(null == characteristic){
            mBluetoothService.closeConnect();
            ((MasterActivity) getActivity()).setViewPagerCurrentItem(1);
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
                        //Map commondMap = CommondUtils.resolveResponseCommond(HexUtil.formatHexString(characteristic.getValue()));

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
    public void  switchBtnStatus(Map<Integer,Boolean> commondMap){
        if(null != commondMap && commondMap.size() > 0 && commondMap.get(R.id.btn_on_off) == boolBtn_on_off){
            powerChange = true;
        }
        //commondMap.get("viewId");
        for (Map.Entry<Integer,Boolean> en:commondMap.entrySet()) {

            switch (en.getKey()){
                case R.id.btn_on_off :
                    if(en.getValue()) {
                        //btn_on_off.setBackgroundResource(R.mipmap.btn_on_red);//红色
                        btn_on_off.setBackgroundResource(R.mipmap.btn_off);//白色
                        panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off);
                        panel_tv_power.setText(R.string.menu_power_on);
                        panel_tv_power.setTextColor(color_gray);
                        boolBtn_on_off = true;
                    }else {
                        //btn_on_off.setBackgroundResource(R.mipmap.btn_off_gray);
                        //panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off_gray);
                        panel_tv_power.setText(R.string.menu_power_off);
                        panel_tv_power.setTextColor(color_gray);
                        boolBtn_on_off = false;
                    }
                    break;
                case R.id.btn_2 :
                    if (en.getValue()) {
                        btn_2.setTextColor(color_red);
                        panel_btn_2.setTextColor(color_red);
                        panel_tv_2.setText(R.string.menu_2_on);
                        panel_tv_2.setTextColor(color_gray);
                        boolBtn_2 = true;
                    } else {
                        btn_2.setTextColor(color_white);
                        panel_btn_2.setTextColor(color_white);
                        panel_tv_2.setText(R.string.menu_2_off);
                        panel_tv_2.setTextColor(color_gray);
                        boolBtn_2 = false;
                    }
                    break;

                case R.id.btn_1 :
                    if(en.getValue()) {
                        btn_1.setTextColor(color_red);
                        panel_btn_1.setTextColor(color_red);
                        panel_tv_1.setText(R.string.menu_1_on);
                        panel_tv_1.setTextColor(color_gray);
                        boolBtn_1 = true;
                    } else {
                        btn_1.setTextColor(color_white);
                        panel_btn_1.setTextColor(color_white);
                        panel_tv_1.setText(R.string.menu_1_off);
                        panel_tv_1.setTextColor(color_gray);
                        boolBtn_1 = false;
                    }
                    break;
                case R.id.btn_b :
                    if(en.getValue()) {
                        btn_b.setTextColor(color_red);
                        panel_btn_b.setTextColor(color_red);
                        panel_tv_b.setText(R.string.menu_b_on);
                        panel_tv_b.setTextColor(color_gray);
                        boolBtn_b = true;
                    } else {
                        btn_b.setTextColor(color_white);
                        panel_btn_b.setTextColor(color_white);
                        panel_tv_b.setText(R.string.menu_b_off);
                        panel_tv_b.setTextColor(color_gray);
                        boolBtn_b = false;
                    }
                    break;
                case R.id.btn_m :
                    if(en.getValue()) {
                        btn_m.setTextColor(color_red);
                        panel_btn_m.setTextColor(color_red);
                        panel_tv_m.setText(R.string.menu_m_on);
                        panel_tv_m.setTextColor(color_gray);
                        boolBtn_m = true;
                    } else {
                        btn_m.setTextColor(color_white);
                        panel_btn_m.setTextColor(color_white);
                        panel_tv_m.setText(R.string.menu_m_off);
                        panel_tv_m.setTextColor(color_gray);
                        boolBtn_m = false;
                    }
                    break;
                case R.id.btn_s :
                    if(en.getValue()) {
                        btn_s.setTextColor(color_red);
                        panel_btn_s.setTextColor(color_red);
                        panel_tv_s.setText(R.string.menu_s_on);
                        panel_tv_s.setTextColor(color_gray);
                        boolBtn_s = true;
                    } else {
                        btn_s.setTextColor(color_white);
                        panel_btn_s.setTextColor(color_white);
                        panel_tv_s.setText(R.string.menu_s_off);
                        panel_tv_s.setTextColor(color_gray);
                        boolBtn_s = false;
                    }
                    break;
                /*case R.id.layout_bt:
                    if(en.getValue()){
                        layout_bt.setBackgroundResource(R.mipmap.bt_on);
                    }else{
                        layout_bt.setBackgroundResource(R.mipmap.bt_off);
                    }

                    break;
                case R.id.layout_wifi:
                    if(en.getValue()){
                        layout_wifi.setBackgroundResource(R.mipmap.wifi_on);
                    }else{
                        layout_wifi.setBackgroundResource(R.mipmap.wifi_off);
                    }
                    break;*/
                default:

            }

        }
        /*if(powerChange) {
            disableButtons(boolBtn_on_off);
            powerChange = false;
        }*/
    }

    private void disableButtons(boolean isEnabled){
        btn_2.setEnabled(isEnabled);
        btn_1.setEnabled(isEnabled);
        btn_b.setEnabled(isEnabled);
        btn_s.setEnabled(isEnabled);
        btn_m.setEnabled(isEnabled);
        //btn_on_off.setEnabled(isEnabled);

        panel_btn_2.setEnabled(isEnabled);
        panel_btn_1.setEnabled(isEnabled);
        panel_btn_b.setEnabled(isEnabled);
        panel_btn_s.setEnabled(isEnabled);
        panel_btn_m.setEnabled(isEnabled);
        if(!isEnabled){
            grayButtons();
        }
    }
    private void grayButtons(){
        btn_2.setTextColor(color_gray);
        btn_on_off.setTextColor(color_gray);
        btn_1.setTextColor(color_gray);
        btn_b.setTextColor(color_gray);
        btn_s.setTextColor(color_gray);
        btn_m.setTextColor(color_gray);

        panel_btn_2.setTextColor(color_gray);
        panel_btn_1.setTextColor(color_gray);
        panel_btn_b.setTextColor(color_gray);
        panel_btn_s.setTextColor(color_gray);
        panel_btn_m.setTextColor(color_gray);
        panel_btn_power.setTextColor(color_gray);

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
            if(FW_ID.equals(version) || null == version || version.isEmpty()){
                return;
            }
            Toast.makeText(getActivity(),version,Toast.LENGTH_LONG).show();
            Rp8DeviceUtils.saveRpType(getActivity(), version);
            Intent intent = new Intent(getActivity(), Rp8MasterActivity.class);
            switch (version){

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
            }

            startActivity(intent);
            getActivity().finish();
        }
    }
}
