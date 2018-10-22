package com.o88o.bluetoothrp8;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void unlock(){
    }

    public void lock() {
    }

    public void bindBluetoothNotify(BluetoothGattCharacteristic characteristic) {

    }

    public void setViewPagerCurrentItem(int currentItem) {

    }

    public  void newFbPopup(View rootView) {

    }

    @Override
    public  void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void checkPermissions() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    public BluetoothService getBluetoothService(){
        return  null ;
    }
    public void cancelScan() {

    }

    public boolean isConnect(){
        return false;
    }
}
