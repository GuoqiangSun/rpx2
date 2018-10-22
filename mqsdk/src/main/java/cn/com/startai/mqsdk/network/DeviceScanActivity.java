package cn.com.startai.mqsdk.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.com.startai.mqsdk.R;

/**
 * author: Guoqiang_Sun
 * date: 2018-03-12
 * description:
 */

public class DeviceScanActivity extends AppCompatActivity implements BroadcastDiscoveryUtil.IBroadcastResult {

    private String TAG = NetworkManager.TAG;
    private ProgressBar progress;
    private BleScanAdapter mLstvAdapter;
    private TextView mTxtBleState;

    private static final int MSG_WHAT_HIDE = 0x01;
    private static final int MSG_WHAT_SHOW = 0x02;

    private Handler h;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);


        progress = (ProgressBar) findViewById(R.id.progressBar1);
        mTxtBleState = (TextView) findViewById(R.id.bt_state);
        ListView mLstv = (ListView) findViewById(R.id.scanLstv);
        mLstvAdapter = new BleScanAdapter(this);
        mLstv.setAdapter(mLstvAdapter);
        mLstv.setOnItemClickListener(new BleItemClick());

        h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_WHAT_HIDE) {
                    progress.setVisibility(ProgressBar.INVISIBLE);
                } else if (msg.what == MSG_WHAT_SHOW) {
                    progress.setVisibility(ProgressBar.VISIBLE);
                }
            }
        };

        NetworkManager.getInstance().addDiscoveryCallBack(this);
        NetworkManager.getInstance().discoveryLanDevice(12);

    }

    public void forceScan(View v) {
        mLstvAdapter.clearData();
        NetworkManager.getInstance().discoveryLanDevice(12);
        if (h != null) {
            h.sendEmptyMessage(MSG_WHAT_SHOW);
            h.sendEmptyMessageDelayed(MSG_WHAT_HIDE, 1500);
        }
    }


    public void stopScan() {
        progress.setVisibility(ProgressBar.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLstvAdapter = null;
        NetworkManager.getInstance().removeDiscoveryCallBack(this);
        super.onDestroy();
    }

    @Override
    public void onBroadcastStart(int remainingTimes) {
        if (h != null) {
            h.sendEmptyMessage(MSG_WHAT_SHOW);
            h.sendEmptyMessageDelayed(MSG_WHAT_HIDE, 1500);
        }
    }

    @Override
    public void onDiscoveryDevice(LanDeviceInfo mLanDeviceInfo) {
        if (mLstvAdapter != null) {
            mLstvAdapter.onBleScan(mLanDeviceInfo);
        }
    }


    private class BleItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            if (mLstvAdapter.getCount() >= position) {
                LanDeviceInfo item = mLstvAdapter.getItem(position);

                if (item != null) {

                    NetworkManager.getInstance().bindDevice(item.getMac(),item.getIp());

                }
            }
        }
    }


}
