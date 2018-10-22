package com.o88o.bluetoothrp8.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.o88o.bluetoothrp8.BaseActivity;
import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.R;
import com.o88o.bluetoothrp8.Rp8MasterActivity;
import com.o88o.bluetoothrp8.util.BaseCommond;
import com.o88o.bluetoothrp8.util.BaseCommondUtils;
import com.o88o.bluetoothrp8.util.MetricsUtils;
import com.o88o.bluetoothrp8.widget.HistogramView;
import com.o88o.bluetoothrp8.widget.ViewPagerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class Rp8MontiorFragment extends Fragment {
    private View rootView;
    private ViewPager viewPager;
    private BluetoothService mBluetoothService;

    private RelativeLayout montior_water_relationLayout;
    private LinearLayout rp8_dsi_layout;
    private HistogramView histogramView;
    private Button montior_water_sync,rp8_dsi;
    private ImageView montior_water_indicator,montior_water_botton,montior_water_up;
    private ImageView monitorHeader;
    private TextView montior_water_percent;
    private float initAngle = 180;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rp8_fragment_montior, null, false);
        bindView();
        return rootView;
    }
    private void bindView() {
        montior_water_relationLayout = (RelativeLayout) rootView.findViewById(R.id.montior_water_relationLayout);
        rp8_dsi_layout = (LinearLayout) rootView.findViewById(R.id.rp8_dsi_layout);
        histogramView = (HistogramView)rootView.findViewById(R.id.montior_water_histogram);
        montior_water_sync = (Button) rootView.findViewById(R.id.montior_water_sync);
        rp8_dsi = (Button) rootView.findViewById(R.id.rp8_dsi);
        montior_water_indicator = (ImageView)  rootView.findViewById(R.id.montior_water_indicator);
        montior_water_botton = (ImageView)  rootView.findViewById(R.id.montior_water_botton);
        montior_water_up = (ImageView)  rootView.findViewById(R.id.montior_water_up);
        monitorHeader = (ImageView)  rootView.findViewById(R.id.monitorHeader);
        montior_water_percent = (TextView)  rootView.findViewById(R.id.montior_water_percent);
        autoAdatper();
        //montior_water_indicator
        montior_water_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommond(BaseCommond.HEX_REQUEST_PASSTHROUGH_CD41_11);
            }
        });

    }
    public void sendCommond(String hexCommond){
        hexCommond += BaseCommondUtils.crc8(hexCommond);

        if(null == mBluetoothService) {
            mBluetoothService = ((BaseActivity) getActivity()).getBluetoothService();
        }
        if(null == mBluetoothService) {
            ((BaseActivity) getActivity()).setViewPagerCurrentItem(1);
            return;
        }
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
                );
    }
    private void autoAdatper() {
        int widthWin = MetricsUtils.getWindowWidth(getActivity());
        int heightWin = MetricsUtils.getWindowHeigh(getActivity());

        int height = heightWin *3/ 7 ;//- monitorHeader.getHeight();// - MetricsUtils.dip2px(getContext(),24);
        int width = widthWin;

        ViewGroup.LayoutParams layoutParams = montior_water_botton.getLayoutParams();
        double scaleRatio = 1;
        if(height   > width){
            scaleRatio =  (width * 1.0 / layoutParams.width);
        }else{
            scaleRatio =  (height * 1.0 / layoutParams.height);
        }
/*
        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        montior_water_relationLayout.setLayoutParams(layoutParams);*/

        layoutParams = montior_water_botton.getLayoutParams();
        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        montior_water_botton.setLayoutParams(layoutParams);

        layoutParams = montior_water_indicator.getLayoutParams();
        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        montior_water_indicator.setLayoutParams(layoutParams);

        layoutParams = montior_water_up.getLayoutParams();
        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        montior_water_up.setLayoutParams(layoutParams);

        scaleRelativeLayout(rp8_dsi_layout,scaleRatio);

    }

    private void scaleRelativeLayoutAndWH(View btn, double scaleRatio) {
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = (int) (relativeLayoutParams.width * scaleRatio + 0.5);
        relativeLayoutParams.height = (int) (relativeLayoutParams.height * scaleRatio + 0.5);
        relativeLayoutParams.setMargins((int)(relativeLayoutParams.leftMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.topMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.rightMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.bottomMargin * scaleRatio + 0.5));
        btn.setLayoutParams(relativeLayoutParams);
    }
    private void scaleRelativeLayout(View btn, double scaleRatio) {
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.setMargins((int)(relativeLayoutParams.leftMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.topMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.rightMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.bottomMargin * scaleRatio + 0.5));
        btn.setLayoutParams(relativeLayoutParams);
    }


    public void runPassthrough(final String passthrough) {
        if(null == passthrough || passthrough.isEmpty() || !passthrough.startsWith("4118")|| passthrough.length() != 24 ){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "透传的数据" + passthrough, Toast.LENGTH_LONG).show();
                /*Random random = new Random();
                random.setSeed(SystemClock.currentThreadTimeMillis());
                histogramView.upData(new int[]{random.nextInt(100),random.nextInt(100),random.nextInt(100),random.nextInt(100),random.nextInt(100)});
                montior_water_indicator.setRotation(initAngle + random.nextInt(122 - 100)*225/(122f - 100));*/
                int aD_voltage = Integer.parseInt(passthrough.substring(18,20),16);
                float voltage = aD_voltage * 0.74352319f;//AD_voltage *10 * 3260 * 362 / 62 / 256 /1000 ;
                voltage = voltage > 122? 122 : voltage;
                voltage = voltage < 100? 100 : voltage;
                montior_water_indicator.setRotation(initAngle + (voltage - 100 )*225/(122f - 100));
                int percent = ((int)voltage - 100 )* 100/(122 - 100);
                montior_water_percent.setText(String.valueOf(percent));

                int dsi = Integer.parseInt(passthrough.substring(22,24),16);
                switchDsi(dsi);
                int[] data = new int[]{
                        Integer.parseInt(passthrough.substring(4,6),16),
                        Integer.parseInt(passthrough.substring(6,8),16),
                        Integer.parseInt(passthrough.substring(8,10),16),
                        Integer.parseInt(passthrough.substring(10,12),16),
                        Integer.parseInt(passthrough.substring(12,14),16),
                        //Integer.parseInt(passthrough.substring(14,16),16),
                        //Integer.parseInt(passthrough.substring(16,18),16),
                };
                histogramView.upData(convertLevel(data));
            }
        });
    }

    private int[] convertLevel(int[] data) {
        for(int index = 0; index < data.length; index++){
            if(data[index]<95) {
                data[index] = 99;
            }else if(data[index]<174 && data[index]>127){
                data[index] = 66;
            }else if(data[index]<219 && data[index]>169){
                data[index] = 33;
            }else if(data[index]>233){
                data[index] = 11;
            }else {
                data[index] = 1;
            }
        }
        return data;
    }

    private void switchDsi(int dsi) {
        if(dsi == 1) {
            rp8_dsi.setBackgroundResource(R.mipmap.dsi_on);
        }else{
            rp8_dsi.setBackgroundResource(R.mipmap.dsi_off);
        }
    }
}
