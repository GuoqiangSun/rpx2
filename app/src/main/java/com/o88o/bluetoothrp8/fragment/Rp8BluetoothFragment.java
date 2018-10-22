package com.o88o.bluetoothrp8.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.BaseActivity;
import com.o88o.bluetoothrp8.R;
import com.o88o.bluetoothrp8.util.MetricsUtils;
import com.o88o.bluetoothrp8.widget.ViewPagerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.com.startai.newUI.ScanLanDeviceActivity;

//import cn.com.startai.mqsdk.LoginActivity;


/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class Rp8BluetoothFragment extends Fragment {
    private View rootView;
    private ViewPager viewPager;
    private ImageView connectPhone;
    private TextView bluetooth_panel_down;
    private Button btn_connect,btn_switch_wifi;
    private RelativeLayout connect_layout;
    private BluetoothService mBluetoothService;

    private boolean boolBtn_connect = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rp8_fragment_bluetooth, null, false);
        bindView();
        return rootView;
    }
    private void bindView() {
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        bluetooth_panel_down = (TextView) rootView.findViewById(R.id.bluetooth_panel_down);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View bluetooth = inflater.inflate(R.layout.scala_bluetooth_, null);

        connect_layout = (RelativeLayout)bluetooth.findViewById(R.id.connect_layout);
        connectPhone = (ImageView) bluetooth.findViewById(R.id.connectPhone);
        btn_connect = (Button) bluetooth.findViewById(R.id.btn_connect);

        // wifi 入口
        View wifi = inflater.inflate(R.layout.scala_wifi_, null);

        List<View> pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(bluetooth);
        pageview.add(wifi);
        PagerAdapter mPagerAdapter = new ViewPagerViewAdapter(pageview);
        viewPager.setAdapter(mPagerAdapter);

        btn_switch_wifi = (Button) wifi.findViewById(R.id.btn_switch_wifi);
        btn_switch_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanLanDeviceActivity.class));
            }
        });


        autoAdatper();

        View.OnClickListener btnOnClickListener = getBtnOnClickListener();
        btn_connect.setOnClickListener(btnOnClickListener);
        //connectHeader.setOnClickListener(btnOnClickListener);

        mBluetoothService = ((BaseActivity) getActivity()).getBluetoothService();

        if(null == mBluetoothService || !mBluetoothService.isServiceDiscover()){
            changeConnectState(false);
        }else{
            changeConnectState(true);
        }
    }

    private void autoAdatper() {
        int widthWin = MetricsUtils.getWindowWidth(getActivity());
        int heightWin = MetricsUtils.getWindowHeigh(getActivity());
        int height = heightWin *2/5;
        int width = widthWin;
        double scaleRatio = 1;
        //down
        ViewGroup.LayoutParams layoutParams = bluetooth_panel_down.getLayoutParams();
        if(height *layoutParams.width / layoutParams.height  > width){
            height = width * layoutParams.height / layoutParams.width;
            scaleRatio =  (width * 1.0 / layoutParams.width);
        }else{
            width = height * layoutParams.width / layoutParams.height ;
            scaleRatio =  (height * 1.0 / layoutParams.height);
        }

        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        bluetooth_panel_down.setLayoutParams(layoutParams);


        scaleRelativeLayout(connectPhone,scaleRatio);;
        scaleRelativeLayout(btn_connect,scaleRatio);


        scaleRelativeLayout(btn_switch_wifi,scaleRatio);
    }

    private void setBtnLayoutDown(Button btn, int radis, int btn_margin) {
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
                switch (view.getId()) {
                    case R.id.btn_connect:
                        //
                        mBluetoothService = ((BaseActivity) getActivity()).getBluetoothService();

                        if(null == mBluetoothService || !mBluetoothService.isServiceDiscover()){
                            boolBtn_connect = false;
                        }else{
                            boolBtn_connect = true;
                        }
                        if (!boolBtn_connect) {
                            //loading
                            if (mBluetoothService != null) {
                                mBluetoothService.cancelScan();
                            }
                            //scan
                            ((BaseActivity)getActivity()).checkPermissions();
                            //popup
                            ((BaseActivity)getActivity()).newFbPopup(rootView);

                            //change ui
                            //changeConnectState(true);
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.tips)
                                    .setMessage(R.string.bluetoothCloseConfirmMsg)
                                    .setNegativeButton(R.string.cancel,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                    .setPositiveButton(R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ((BaseActivity)getActivity()).lock();
                                                    ((BaseActivity)getActivity()).cancelScan();
                                                    mBluetoothService.closeConnect();
                                                    mBluetoothService = null;
                                                    changeConnectState(false) ;
                                                }
                                            })

                                    .setCancelable(false)
                                    .show();

                        }
                        break;
                    case R.id.connectHeader :
                        ((BaseActivity)getActivity()).setViewPagerCurrentItem(0);
                        break ;
                    default:
                }
            }
        };
    }
    public void changeConnectState(boolean showConnected){
        if(!showConnected){
            boolBtn_connect = false;
            connectPhone.setBackgroundResource(R.mipmap.rp8_disconnect_pg);
            btn_connect.setBackgroundResource(R.mipmap.rp8_disconnect_btn_pg);
        }else{
            boolBtn_connect = true;
            connectPhone.setBackgroundResource(R.mipmap.rp8_connect_pg);
            btn_connect.setBackgroundResource(R.mipmap.rp8_connect_btn_pg);
        }
    }
    public boolean isConnect(){
        return boolBtn_connect;
    }
}
