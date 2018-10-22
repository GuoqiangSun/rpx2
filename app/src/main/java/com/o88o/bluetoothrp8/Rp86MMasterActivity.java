package com.o88o.bluetoothrp8;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
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

import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.o88o.bluetoothrp8.fragment.Rp8BluetoothFragment;
import com.o88o.bluetoothrp8.fragment.Rp86MMenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8MontiorFragment;
import com.o88o.bluetoothrp8.util.Rp86MCommond;
import com.o88o.bluetoothrp8.util.Rp86MCommondUtils;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;
import com.o88o.bluetoothrp8.widget.FbPopup;
import com.o88o.bluetoothrp8.widget.ResultAdapter;
import com.o88o.bluetoothrp8.widget.VerticalViewLockablePager;
import com.o88o.bluetoothrp8.widget.VerticalViewPager;
import com.o88o.bluetoothrp8.widget.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Rp86MMasterActivity extends BaseActivity {

    private BluetoothService mBluetoothService;
    private VerticalViewLockablePager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FbPopup fbPopup = null;
    String[] names = new String[]{"rp8"},  uuids = null;
    String mac = "";
    Boolean isNeedConnect = false;
    Boolean isAutoConnect = true;
    private ResultAdapter mResultAdapter;
    private Rp8BluetoothFragment bluetoothFragment;
    private Rp86MMenuFragment menuFragment;
    private Animation operatingAnim;
    private Rp8MontiorFragment montiorFragment;
    private Rp86MCommondUtils commondUtils = new Rp86MCommondUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.rp86m_layout_main);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null){
            mBluetoothService.closeConnect();
            unbindService();
        }
    }

    private void initView() {
        viewPager = (VerticalViewLockablePager) findViewById(R.id.viewPager);
        menuFragment = new Rp86MMenuFragment();
        fragmentList.add(menuFragment);
        bluetoothFragment = new Rp8BluetoothFragment();
        fragmentList.add(bluetoothFragment);
        montiorFragment = new Rp8MontiorFragment();
        fragmentList.add(montiorFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false); // MenuFragment
        viewPager.lock();

        String temp = Rp8DeviceUtils.getDeviceMac(getApplicationContext());
        if(null != temp && !temp.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkPermissions();
                }
            }, 10);
        }
    }
    public void unlock(){
        viewPager.unlock();
    }

    public void lock(){
        viewPager.lock();
    }

    private  boolean half = false;
    private  StringBuffer sb = new StringBuffer();
    public void bindBluetoothNotify(BluetoothGattCharacteristic characteristic) {
        mBluetoothService.notify(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleCharacterCallback() {

                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "receive commend : " + HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
                            }
                        });*/
                        String hexString = HexUtil.formatHexString(characteristic.getValue());
                        if(half){
                            half = false;
                            final String hexStringFinal = sb.toString() + hexString;
                            sb.setLength(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Rp86MMasterActivity.this, "receive commend : " + hexStringFinal, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        if(hexString.length()>10 && hexString.substring(6, 8).toUpperCase().equals(Rp86MCommond.HEX_COMMOND_TYPE_NOTIFICATION_17)) {
                            half = true;
                            sb.append(hexString);
                        }
                        Map commondMap = commondUtils.resolve(characteristic.getValue());
                        if(null != commondMap && !commondMap.isEmpty()) {
                            menuFragment.runSwitchBtnStatus(commondMap);
                            return;
                        }
                        String passthrough = commondUtils.resolvePassthrough(characteristic.getValue());
                        if(null != passthrough && !passthrough.isEmpty()) {
                            montiorFragment.runPassthrough(passthrough);
                            return;
                        }

                        String version = commondUtils.resolveVersion(characteristic.getValue());
                        if(null != version && !version.isEmpty()) {
                            menuFragment.runSwitchVersionRunnable(version);
                            return;
                        }

                        int auth = commondUtils.resolveAuth(Rp86MMasterActivity.this,characteristic.getValue());
                        if(-1 != auth){
                            menuFragment.runSwitchAuthRunnable(auth);
                            if(1 == auth){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Rp86MMasterActivity.this, "收到点击实体按键的数据" + HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Rp86MMasterActivity.this, "收到的数据" + HexUtil.formatHexString(characteristic.getValue()), Toast.LENGTH_LONG).show();
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
    public void setViewPagerCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem, false); // Rp86MMenuFragment
    }

    public void newFbPopup(View rootView) {

        mResultAdapter = new ResultAdapter(this);
        fbPopup = new FbPopup(this,rootView,mBluetoothService,mResultAdapter,operatingAnim);
    }

    private void startScan() {
        if (mBluetoothService == null) {
            bindService();
        } else {
            mBluetoothService.setting(names, mac, uuids, isAutoConnect);
            mBluetoothService.scanDevice(isNeedConnect);
        }
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            if(null != fbPopup){
                fbPopup.setBluetoothService(mBluetoothService);
            }
            /*String uuid = DeviceUtils.getDeviceUuid(MasterActivity.this);
            if(null != uuid && !uuid.isEmpty()) {
                uuids = new String[2];
                uuids[0] = uuid;
                uuids[1] = "03DACD2B-CC17-4C04-B6E1-8DF3E629203B";
            }*/
            mac = Rp8DeviceUtils.getDeviceMac(getApplicationContext());

            mBluetoothService.setScanCallback(callback);
            /*if(null != uuid && !uuid.isEmpty()) {
                mBluetoothService.scanDevice(true);//自动连接
            }else {
                mBluetoothService.scanDevice(isNeedConnect);
            }
            uuids = null;*/

            if(null != mac && !mac.isEmpty()) {
                mBluetoothService.setting(null, mac, uuids, isAutoConnect);
                mBluetoothService.scanDevice(true);//自动连接
            }else {
                mBluetoothService.setting(names, mac, uuids, isAutoConnect);
                mBluetoothService.scanDevice(isNeedConnect);
            }
            mac = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };


    private BluetoothService.Callback callback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {
            if(null != mResultAdapter) {
                mResultAdapter.clear();
                mResultAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScanning(ScanResult result) {
            //result.getDevice().getName().startsWith();
            //todo
            //if (result.getDevice().getName().startsWith(MenuFragment.DEVICE_NAME)) {
                if(null != mResultAdapter) {
                    mResultAdapter.addResult(result);
                    mResultAdapter.notifyDataSetChanged();
                }
                if(null != fbPopup) {
                    fbPopup.hiddenImgLoading();
                }
            //}
        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onConnecting() {
//            progressDialog.show();
        }

        @Override
        public void onConnectFail() {
            Toast.makeText(Rp86MMasterActivity.this, "ConnectFail", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisConnected() {
            //progressDialog.dismiss();
            if(null != mResultAdapter) {
                mResultAdapter.clear();
                mResultAdapter.notifyDataSetChanged();
            }
            if(null != fbPopup){
                fbPopup.hiddenItemLoading();
                fbPopup.dismiss();
                fbPopup = null;
            }
            bluetoothFragment.changeConnectState(false);
            setViewPagerCurrentItem(1);

        }
        @Override
        public void onDisConnected2Reconnect(){
            bluetoothFragment.changeConnectState(false);
            Toast.makeText(Rp86MMasterActivity.this, "Reconnecting", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onServicesDiscovered() {
            bluetoothFragment.changeConnectState(true);
            if(null != fbPopup){
                fbPopup.hiddenItemLoading();
                fbPopup.dismiss();
                fbPopup = null;
                mResultAdapter.clear();
            }
            if(menuFragment.initBluetoothService()) {
                // 需要 auth 认证
                //setViewPagerCurrentItem(0);
            }else{
                bluetoothFragment.changeConnectState(false);
            }
            Toast.makeText(Rp86MMasterActivity.this, "ServicesDiscovered", Toast.LENGTH_LONG).show();
        }
    };

    /*private BluetoothService.Callback2 callback2 = new BluetoothService.Callback2() {

        @Override
        public void onDisConnected() {
            //finish();
            bluetoothFragment.changeConnectState(false);
            setViewPagerCurrentItem(1);
        }
    };*/
    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    public void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            //Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.notifyTitle)
                    .setMessage(R.string.bluetoothNotifyMsg)
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setPositiveButton(R.string.setting,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                    startActivityForResult(intent, 1);
                                }
                            })

                    .setCancelable(false)
                    .show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, 1);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    startScan();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (checkGPSIsOpen()) {
                startScan();
            }
        }
    }
    public BluetoothService getBluetoothService(){
        return  mBluetoothService ;
    }
    public void cancelScan() {

        mBluetoothService.cancelScan();
    }

    public boolean isConnect(){
        return bluetoothFragment.isConnect();
    }

  }
