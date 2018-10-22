package cn.com.startai.mqsdk.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.com.startai.mqsdk.R;


public class BleScanAdapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<LanDeviceInfo> data = new ArrayList<LanDeviceInfo>();
    private LeSort mBleSort = new LeSort();
    private Handler mUIHandler;

    public BleScanAdapter(Context mContext) {

        this.mContext = mContext;
        this.mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_CLEAR_DATA) {
                    data.clear();
                    BleScanAdapter.this.notifyDataSetChanged();
                } else {
                    if (msg.obj == null) {
                        return;
                    }
                    LanDeviceInfo mLanDeviceInfo = (LanDeviceInfo) msg.obj;
                    boolean add = true;
                    if (data.size() > 0) {
                        for (LanDeviceInfo scan : data) {
                            if (scan.mac.equalsIgnoreCase(mLanDeviceInfo.mac)) {
                                scan.ip = mLanDeviceInfo.ip;
                                scan.isLanBind = mLanDeviceInfo.isLanBind;
                                scan.isWanBind = mLanDeviceInfo.isWanBind;
                                add = false;
                                break;
                            }
                        }
                    }
                    if (add) {
                        data.add(mLanDeviceInfo);
                    }
                    Collections.sort(data, mBleSort);
                    BleScanAdapter.this.notifyDataSetChanged();
                }

            }
        };

    }

    private static final int MSG_CLEAR_DATA = 0x00;

    public void clearData() {

        if (mUIHandler != null) {
            mUIHandler.sendEmptyMessage(MSG_CLEAR_DATA);
        }
    }

    private static final int MSG_OBTAIN_DATA = 0x01;

    public void onBleScan(LanDeviceInfo mScanBle) {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(MSG_OBTAIN_DATA, mScanBle).sendToTarget();
        }
    }

    private class LeSort implements Comparator<LanDeviceInfo> {

        @Override
        public int compare(LanDeviceInfo lhs, LanDeviceInfo rhs) {
            // TODO Auto-generated method stub

            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else if (lhs.rssi < rhs.rssi) {
                return 1;
            }

            return 0;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public LanDeviceInfo getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder mHolder = null;

        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_ble, null);
            mHolder.mNameTxt = (TextView) convertView.findViewById(R.id.name);
            mHolder.mAddressTxt = (TextView) convertView.findViewById(R.id.address);
            mHolder.mRssiTxt = (TextView) convertView.findViewById(R.id.rssi);
            mHolder.mIpTxt = (TextView) convertView.findViewById(R.id.ip);
            mHolder.mBindTxt = (TextView) convertView.findViewById(R.id.bind);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        LanDeviceInfo scanBle = data.get(position);

        if (scanBle != null) {
            mHolder.mNameTxt.setText(scanBle.name);
            mHolder.mAddressTxt.setText(scanBle.mac);
            mHolder.mRssiTxt.setText(String.valueOf(scanBle.rssi));
            mHolder.mIpTxt.setText(scanBle.ip + ":" + String.valueOf(scanBle.port)
            );
            mHolder.mBindTxt.setText("Binding:"+ scanBle.isWanBind);
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView mNameTxt;
        public TextView mAddressTxt;
        public TextView mRssiTxt;
        public TextView mIpTxt;
        public TextView mBindTxt;
    }
}
