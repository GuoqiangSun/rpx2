package com.o88o.bluetoothrp8;

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

import com.clj.fastble.data.ScanResult;
import com.o88o.bluetoothrp8.fragment.MenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8BluetoothFragment;
import com.o88o.bluetoothrp8.fragment.Rp8MontiorFragment;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;
import com.o88o.bluetoothrp8.widget.FbPopup;
import com.o88o.bluetoothrp8.widget.ResultAdapter;
import com.o88o.bluetoothrp8.widget.VerticalViewPager;
import com.o88o.bluetoothrp8.widget.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MasterActivity extends BaseActivity {

    private BluetoothService mBluetoothService;
    private VerticalViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FbPopup fbPopup = null;
    String[] names = null,  uuids = null;
    String mac = "";
    Boolean isNeedConnect = false;
    Boolean isAutoConnect = true;
    private ResultAdapter mResultAdapter;
    private Rp8BluetoothFragment bluetoothFragment;
    private MenuFragment menuFragment;
    private Animation operatingAnim;
    private Rp8MontiorFragment montiorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.layout_main);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.closeConnect();
            unbindService();
        }
    }

    private void initView() {
        viewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        menuFragment = new MenuFragment();
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        }, 10);
    }

    public void setViewPagerCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem, false); // MenuFragment
    }

    public void newFbPopup(View rootView) {

        mResultAdapter = new com.o88o.bluetoothrp8.widget.ResultAdapter(this);
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
            /*String uuid = Rp8DeviceUtils.getDeviceUuid(MasterActivity.this);
            if(null != uuid && !uuid.isEmpty()) {
                uuids = new String[2];
                uuids[0] = uuid;
                uuids[1] = "03DACD2B-CC17-4C04-B6E1-8DF3E629203B";
            }*/
            mac = Rp8DeviceUtils.getDeviceMac(MasterActivity.this);

            mBluetoothService.setScanCallback(callback);
            mBluetoothService.setting(names, mac, uuids, isAutoConnect);
            /*if(null != uuid && !uuid.isEmpty()) {
                mBluetoothService.scanDevice(true);//自动连接
            }else {
                mBluetoothService.scanDevice(isNeedConnect);
            }
            uuids = null;*/

            if(null != mac && !mac.isEmpty()) {
                mBluetoothService.scanDevice(true);//自动连接
            }else {
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
            Toast.makeText(MasterActivity.this, "ConnectFail", Toast.LENGTH_LONG).show();
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
            Toast.makeText(MasterActivity.this, "Reconnecting", Toast.LENGTH_LONG).show();
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
                setViewPagerCurrentItem(0);
            }
            Toast.makeText(MasterActivity.this, "ServicesDiscovered", Toast.LENGTH_LONG).show();
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
