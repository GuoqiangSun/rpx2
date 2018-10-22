package com.o88o.bluetoothrp8;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.o88o.bluetoothrp8.fragment.BR16IRMenuFragment;
import com.o88o.bluetoothrp8.fragment.BR16PUMenuFragment;
import com.o88o.bluetoothrp8.fragment.BR16WPMenuFragment;
import com.o88o.bluetoothrp8.fragment.MenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp86MMenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8GTMenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8MenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8OEMRMenuFragment;
import com.o88o.bluetoothrp8.fragment.Rp8OEMRXPMenuFragment;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;

import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.swain.baselib.util.PermissionRequest;
import cn.com.swain169.log.Tlog;

public class SplushActivity extends FragmentActivity {

    private PermissionRequest mPermissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_splush);

        initView();

        Tlog.v("SplushActivity  requestPermission() ");
        mPermissionRequest = new PermissionRequest(this,
                new PermissionRequest.OnPermissionResult() {
                    @Override
                    public void onAllPermissionRequestFinish() {

                    }

                    @Override
                    public void onPermissionRequestResult(String permission, boolean granted) {

                    }
                });
        mPermissionRequest.requestAllPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mPermissionRequest != null) {
            mPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionRequest != null) {
            mPermissionRequest.release();
        }
    }

    private void initView() {
        TextView mVersion = findViewById(R.id.version);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersion.setText("v" + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initActivity();
            }
        }, 1000);
    }

    private void initActivity() {
        String FW_ID = Rp8DeviceUtils.getRpType(getApplicationContext(), Rp8MenuFragment.FW_ID);
        Intent intent = new Intent(SplushActivity.this, Rp8MasterActivity.class);

        Tlog.v(NetworkManager.TAG, "initActivity  " + FW_ID);

        switch (FW_ID) {

            case MenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, MasterActivity.class);
                break;
            case Rp8GTMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, Rp8GTMasterActivity.class);
                break;
            /*case Rp8MenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, Rp8MasterActivity.class);
                break;*/
            case Rp8OEMRMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, Rp8OEMRMasterActivity.class);
                break;
            case Rp8OEMRXPMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, Rp8OEMRXPMasterActivity.class);
                break;
            case Rp86MMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, Rp86MMasterActivity.class);
                break;
            case BR16PUMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, BR16PUMasterActivity.class);
                break;
            case BR16IRMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, BR16IRMasterActivity.class);
                break;
            case BR16WPMenuFragment.FW_ID:
                intent = new Intent(SplushActivity.this, BR16WPMasterActivity.class);
                break;

            default:
                intent = new Intent(SplushActivity.this, Rp86MMasterActivity.class);

        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //only splush
    }
}
