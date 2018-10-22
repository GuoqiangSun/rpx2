package com.o88o.bluetoothrp8.widget;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.o88o.bluetoothrp8.BaseActivity;
import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.util.Rp86MCommond;
import com.o88o.bluetoothrp8.util.Rp86MCommondUtils;

import java.lang.ref.WeakReference;

public class MyLongClickUpListener implements LongClickButton.LongClickUpListener {
    BluetoothService mBluetoothService;
    String deviceHexCode;
    public MyLongClickUpListener(BluetoothService mBluetoothService,String deviceHexCode) {
        this.mBluetoothService = mBluetoothService;
        this.deviceHexCode = deviceHexCode;
    }

    public void setmBluetoothService(BluetoothService mBluetoothService) {
        this.mBluetoothService = mBluetoothService;
    }

    @Override
    public void upAction() {
        if(null != mBluetoothService) {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_SHUTDOWN);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());
        }
    }

    @Override
    public void downAction() {
        if(null != mBluetoothService) {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_STARTUP);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());
        }
    }

    @Override
    public void downing() {
        //Looper.prepare();
        downAction();
        //Looper.loop();
    }


    public void sendCommond(String hexCommond){
        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        if(null == characteristic){
            mBluetoothService.closeConnect();
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
                        //Map commondMap = Rp86MCommondUtils.resolveResponseCommond(HexUtil.formatHexString(characteristic.getValue()));

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
}