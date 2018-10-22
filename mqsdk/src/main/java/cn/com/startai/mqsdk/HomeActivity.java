package cn.com.startai.mqsdk;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.startai.kp8.activity.BR16IRMasterActivity;
import cn.com.startai.kp8.activity.BR16PUMasterActivity;
import cn.com.startai.kp8.activity.BR16WPMasterActivity;
import cn.com.startai.kp8.activity.Rp86MMasterActivity;
import cn.com.startai.kp8.fragment.BR16IRMenuFragment;
import cn.com.startai.kp8.fragment.BR16PUMenuFragment;
import cn.com.startai.kp8.fragment.BR16WPMenuFragment;
import cn.com.startai.kp8.fragment.Rp86MMenuFragment;
import cn.com.startai.kp8.util.BaseCommondUtils;
import cn.com.startai.kp8.util.HexUtil;
import cn.com.startai.kp8.util.Rp86MCommond;
import cn.com.startai.mqsdk.adapter.MyRecyclerViewAdapter;
import cn.com.startai.mqsdk.network.DeviceScanActivity;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.airkiss.AirkissActivity;
import cn.com.startai.mqsdk.util.eventbus.E_0x8002_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8004_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8005_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8200_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Break;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Failed;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Success;
import cn.com.startai.mqsdk.util.eventbus.E_Device_Connect_Status;
import cn.com.startai.mqsdk.util.zxing.DialogHelper;
import cn.com.startai.mqsdk.util.zxing.PermissionHelper;
import cn.com.startai.mqsdk.util.zxing.ScanActivity;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.busi.entity.C_0x8005;
import cn.com.swain169.log.Tlog;

public class HomeActivity extends BaseActivity implements NetworkManager.ILanPassThroughResult {

    private Button btLogout;
    private String TAG = HomeActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;

    ArrayList<C_0x8005.Resp.ContentBean> list = new ArrayList<>();

    private MyRecyclerViewAdapter mAdapter;
    private long t;
    private TextView tvConnect;

    private ProgressDialog progressDialog;
    private C_0x8005.Resp.ContentBean tempDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include2);
        toolbar.setTitle("设备列表");
        setSupportActionBar(toolbar);


        initview();
        initAdapter();
        initListener();

        NetworkManager.getInstance().activityCreate();
        NetworkManager.getInstance().registerReceiver();
        NetworkManager.getInstance().addPassThroughCallBack(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().unregisterReceiver();
        NetworkManager.getInstance().removePassThroughCallBack(this);
    }

    public static final String topicTest = "Q/client/3A410B14F53D9D76D1609E7E204230EA/#";

    @Override
    public void onConnected(E_Conn_Success e_conn_success) {
        super.onConnected(e_conn_success);
        tvConnect.setText("连接成功 ");

//        StartAI.getInstance().subscribe(topicTest, new IOnSubscribeListener() {
//            @Override
//            public void onSuccess(String topic) {
//                TAndL.TL(getApplicationContext(), "订阅成功 " + topic);
//            }
//
//            @Override
//            public void onFailed(String topic, StartaiError error) {
//                TAndL.TL(getApplicationContext(), "订阅失败 " + topic);
//            }
//
//            @Override
//            public boolean needUISafety() {
//                return false;
//            }
//        });

    }


    @Override
    public void onConnectFail(E_Conn_Failed e_conn_failed) {
        super.onConnectFail(e_conn_failed);
        tvConnect.setText("连接失败 " + e_conn_failed.getErrorMsg());
    }

    @Override
    public void onDisconnect(E_Conn_Break e_conn_break) {
        super.onDisconnect(e_conn_break);
        tvConnect.setText("连接断开 " + e_conn_break.getErrorMsg());

    }


//    @Override
//    public void onBackPressed() {
//
//        if (System.currentTimeMillis() - t < 2 * 1000) {
//            finish();
//            System.exit(0);
//        } else {
//            t = System.currentTimeMillis();
//            TAndL.TL(getApplicationContext(), "再次点击返回键退出应用");
//        }
//
//    }


    private void initAdapter() {

        progressDialog = new ProgressDialog(HomeActivity.this);//1.创建一个ProgressDialog的实例
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (tempDevice != null) {
                    String mac = tempDevice.getMac();
                    Tlog.w(NetworkManager.TAG, " progressDialog onCancel disconnectWiFiDevice ");
                    NetworkManager.getInstance().disconnectWiFiDevice(mac);
                }
            }
        });

        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mAdapter = new MyRecyclerViewAdapter(list);
        //设置添加或删除item时的动画，这里使用默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onPassthrouthResult(E_0x8200_Resp resp) {
        super.onPassthrouthResult(resp);
        TAndL.TL(getApplicationContext(), "透传" + " data = " + resp.getDataString() + "fromid = " + resp.getResp().getFromid() + " result = " + resp.getResult() + " errorMsg = " + resp.getErrorMsg());

        Tlog.v(NetworkManager.TAG, "onPassthrouthResult" + String.valueOf(resp));
        parseVersion(resp.getDataByteArray());
    }

    @Override
    public void onReceiveLanPassThrough(String mac, byte[] data) {

        if (mac != null && tempDevice != null && mac.equals(tempDevice.getMac())) {
            parseVersion(data);
        }

    }

    private synchronized void parseVersion(byte[] data) {

        String version = BaseCommondUtils.resolveVersion(data);
        Tlog.v(NetworkManager.TAG, "version:" + version);

        if (null == version || version.length() == 0 || null == tempDevice) {
            //TAndL.TL(getApplicationContext(), "version = " +version);
            //TAndL.TL(getApplicationContext(), "获取设备的型号:" + version);
            return;
        }

        NetworkManager.getInstance().removeGetDeviceModelMsg(tempDevice.getMac());

        if (!progressDialog.isShowing()) {
            Tlog.e(NetworkManager.TAG, "获取设备的型号:" + version + " !progressDialog.isShowing()");
            return;
        }

        progressDialog.dismiss();
        TAndL.TL(getApplicationContext(), "获取设备的型号:" + version);
        Tlog.v(NetworkManager.TAG, "获取设备的型号:" + version);


        //如果识别出版本号，按版本号跳转
        Intent intent = null;
        if (Rp86MMenuFragment.FW_ID.equals(version)) {
            intent = new Intent(HomeActivity.this, Rp86MMasterActivity.class);
        }
        if (BR16IRMenuFragment.FW_ID.equals(version)) {
            intent = new Intent(HomeActivity.this, BR16IRMasterActivity.class);
        }
        if (BR16PUMenuFragment.FW_ID.equals(version)) {
            intent = new Intent(HomeActivity.this, BR16PUMasterActivity.class);
        }
        if (BR16WPMenuFragment.FW_ID.equals(version)) {
            intent = new Intent(HomeActivity.this, BR16WPMasterActivity.class);
        }

        if (null != intent) {
            intent.putExtra("device", tempDevice);
            startActivity(intent);
        }
    }


    private void initListener() {
//        btLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                StartAI.getInstance().getBaseBusiManager().logout();
//
//            }
//        });


        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                progressDialog.setTitle("获取设备的型号");//2.设置标题
                progressDialog.setMessage("正在加载中，请稍等......");//3.设置显示内容
                progressDialog.setCancelable(true);//4.设置可否用back键关闭对话框
                progressDialog.show();//5.将ProgessDialog显示出来
                tempDevice = mAdapter.getItem(position);
                String hexStr = Rp86MCommond.HEX_REQUEST_VERSION + BaseCommondUtils.crc8(Rp86MCommond.HEX_REQUEST_VERSION);


                String mac = tempDevice.getMac();


                byte[] bytes = HexUtil.hexStringToBytes(hexStr);
                NetworkManager.getInstance().connectWiFiDevice(mac);
                NetworkManager.getInstance().getDeviceModelByLan(mac, bytes);

                Tlog.v(NetworkManager.TAG, "setOnItemClickListener passthrough:" + mAdapter.getItem(position).getId() + " " + hexStr);
                StartAI.getInstance().getBaseBusiManager().passthrough(mAdapter.getItem(position).getId(), hexStr, onCallListener);



/*
                Intent intent = new Intent(HomeActivity.this, Rp86MMasterActivity.class);
                intent.putExtra("device", mAdapter.getItem(position));
                startActivity(intent);*/

            }
        });


        mAdapter.setOnItemLongClickListener(new MyRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                final C_0x8005.Resp.ContentBean item = mAdapter.getItem(position);

                //解绑设备
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                StartAI.getInstance().getBaseBusiManager().unBind(item.getId(), onCallListener);

                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();


            }
        });

    }

    @Override
    public void onBindResult(E_0x8002_Resp resp) {
        super.onBindResult(resp);
        TAndL.TL(getApplicationContext(), "添加结果 result = " + resp.getResult() + " errorMsg = " + resp.getErrorMsg() + " id = " + resp.getId());
        if (resp.getResult() == 1) {
            StartAI.getInstance().getBaseBusiManager().getBindList(1, onCallListener);
        }

        NetworkManager.getInstance().onBindResult(resp);
    }

    @Override
    public void onBindListResult(E_0x8005_Resp resp) {
        super.onBindListResult(resp);
        TAndL.TL(getApplicationContext(), "获取好友列表 result = " + resp.getResult() + " errorMsg = " + resp.getErrorMsg()
//                + "id = " + resp.getId() + " list = " + resp.getBindList()
        );

        if (resp.getResult() == 1) {
            list = resp.getBindList();
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onDeviceConnectStatusChange(E_Device_Connect_Status e_device_connect_status) {
        super.onDeviceConnectStatusChange(e_device_connect_status);

        TAndL.TL(getApplicationContext(), e_device_connect_status.userid + " 用户的 " + e_device_connect_status.sn + " " + (e_device_connect_status.status == 1 ? "上线" : "下线" + " 了"));
        if (list != null) {
            for (C_0x8005.Resp.ContentBean contentBean : list) {


                if (contentBean.getId().equals(e_device_connect_status.sn)) {
                    contentBean.setConnstatus(e_device_connect_status.status);
                }
            }
        }
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();

        NetworkManager.getInstance().discoveryLanDevice(2);

    }

    @Override
    public void onUnBindResult(E_0x8004_Resp resp) {
        super.onUnBindResult(resp);
        TAndL.TL(getApplicationContext(), "删除结果 result = " + resp.getResult() + " errorMsg = " + resp.getErrorMsg() + " id = " + resp.getId());

        if (resp.getResult() == 1) {
            StartAI.getInstance().getBaseBusiManager().getBindList(7, onCallListener);
        }
    }


    private void initview() {

        btLogout = (Button) findViewById(R.id.bt_home_logout);
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_devicelist);
        btLogout.setVisibility(View.GONE);
        tvConnect = (TextView) findViewById(R.id.tv_connect);
    }

    private void toScanBarCode() {

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {

            PermissionUtils.permission(PermissionConstants.CAMERA)
                    .rationale(new PermissionUtils.OnRationaleListener() {
                        @Override
                        public void rationale(final ShouldRequest shouldRequest) {
                            DialogHelper.showRationaleDialog(shouldRequest);
                        }
                    })
                    .callback(new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(List<String> permissionsGranted) {

                            toScanActivity();

                        }

                        @Override
                        public void onDenied(List<String> permissionsDeniedForever,
                                             List<String> permissionsDenied) {
                            if (!permissionsDeniedForever.isEmpty()) {
                                DialogHelper.showOpenAppSettingDialog();
                            }
                            LogUtils.d(permissionsDeniedForever, permissionsDenied);
                        }
                    })
                    .request();

        } else {
            toScanActivity();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_BIND_DEVICE:

                    String scanResult = data.getStringExtra("result");
                    Log.i(TAG, "scan result = " + scanResult);

                    String sn = getSnFromQRCodrResult(scanResult);

                    if (TextUtils.isEmpty(sn)) {
                        TAndL.TL(getApplicationContext(), getResources().getString(R.string.scan_no_device_code));
                    } else {
                        StartAI.getInstance().getBaseBusiManager().bind(sn, onCallListener);
                    }

                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    String QR_CODE_INDEX = "http://www.startai.com.cn/qr/?";

    /**
     * 检查 是否是sn的二码维码
     */
    private String getSnFromQRCodrResult(String result) {

        if (result.startsWith(QR_CODE_INDEX)) {
            String[] datas = result.split("\\?");
            if (datas.length >= 2 && !TextUtils.isEmpty(datas[1])) {
                String sn = datas[1];
                return sn;
            }
        }
        return "";
    }


    /**
     * 跳转到绑定设备页面的 请求码
     */
    public static final int REQUEST_CODE_BIND_DEVICE = 1;

    private void toScanActivity() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ScanActivity.showActivityForResult(HomeActivity.this, REQUEST_CODE_BIND_DEVICE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_scan) {

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("添加方式")
                    .setSingleChoiceItems(new String[]{"扫一扫", "局域网发现"}, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    toScanBarCode();
                                    break;
                                case 1:
//                                    TAndL.TL(getApplicationContext(), "开发中...");

                                    HomeActivity.this.startActivity(new Intent(HomeActivity.this, DeviceScanActivity.class));

                                    break;

                            }
                        }
                    })
                    .show();


        } else if (id == R.id.menu_account) {

            startActivity(new Intent(HomeActivity.this, AccountActivity.class));

        } else if (id == R.id.menu_airkiss) {

            /*new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("配网方式")
                    .setSingleChoiceItems(new String[]{"Airkiss", "Esptouch"}, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0:
                                    startActivity(new Intent(HomeActivity.this, AirkissActivity.class));
                                    break;
                                case 1:
                                    startActivity(new Intent(HomeActivity.this, EsptouchActivity.class));
                                    break;
                            }
                        }
                    })
                    .show();*/
            startActivity(new Intent(HomeActivity.this, AirkissActivity.class));

        } else if (id == R.id.menu_refresh) {
            //7 所有的手机  1 智能硬件
            StartAI.getInstance().getBaseBusiManager().getBindList(1, onCallListener);
            //throw new RuntimeException();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //only splush
        if (tempDevice != null) {
            String mac = tempDevice.getMac();
            Tlog.w(NetworkManager.TAG, " onBackPressed disconnectWiFiDevice ");
            NetworkManager.getInstance().disconnectWiFiDevice(mac);
        }
        tempDevice = null;
    }


}
