package cn.com.startai.newUI;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.network.LanDeviceInfo;


public class LanScanAdapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<LanDeviceInfo> data = new ArrayList<LanDeviceInfo>();
    private LeSort mBleSort = new LeSort();
    private Handler mUIHandler;
    private boolean flag;

    public LanScanAdapter(Context mContext, boolean flag) {
        this.flag = flag;
        this.mContext = mContext;
        this.mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_CLEAR_DATA) {
                    data.clear();
                    LanScanAdapter.this.notifyDataSetChanged();
                } else if (msg.what == MSG_OBTAIN_DATA) {
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
                                scan.name = mLanDeviceInfo.name;
                                add = false;
                                break;
                            }
                        }
                    }
                    if (add) {
                        data.add(mLanDeviceInfo);
                    }
                    Collections.sort(data, mBleSort);
                    LanScanAdapter.this.notifyDataSetChanged();
                } else if (msg.what == MSG_FLUSH_DATA) {
                    LanScanAdapter.this.notifyDataSetChanged();
                } else if (msg.what == MSG_REMOVE_DATA) {

                    String unbindID = (String) msg.obj;

                    if (data.size() > 0) {
                        LanDeviceInfo tmp = null;
                        for (LanDeviceInfo scan : data) {
                            if (scan.deviceID != null && scan.deviceID.equalsIgnoreCase(unbindID)) {
                                tmp = scan;
                                break;
                            }
                        }

                        if (tmp != null) {
                            data.remove(tmp);
                            LanScanAdapter.this.notifyDataSetChanged();
                        }
                    }


                } else if (msg.what == MSG_CHANGE_STATE_TRUE) {

                    String snt = (String) msg.obj;

                    for (LanDeviceInfo mLanDeviceInfo : data) {

                        if (mLanDeviceInfo.deviceID != null && mLanDeviceInfo.deviceID.equals(snt)) {
                            mLanDeviceInfo.state = true;
                            break;
                        }

                    }

                    LanScanAdapter.this.notifyDataSetChanged();

                } else if (msg.what == MSG_CHANGE_STATE_FALSE) {
                    String snf = (String) msg.obj;

                    for (LanDeviceInfo mLanDeviceInfo : data) {

                        if (mLanDeviceInfo.deviceID != null && mLanDeviceInfo.deviceID.equals(snf)) {
                            mLanDeviceInfo.state = false;
                            break;
                        }

                    }

                    LanScanAdapter.this.notifyDataSetChanged();
                } else if (msg.what == MSG_OBTAIN_DATA_NO_ADD) {
                    if (msg.obj == null) {
                        return;
                    }
                    LanDeviceInfo mLanDeviceInfo = (LanDeviceInfo) msg.obj;
                    if (data.size() > 0) {
                        for (LanDeviceInfo scan : data) {
                            if (scan.mac.equalsIgnoreCase(mLanDeviceInfo.mac)) {
                                scan.ip = mLanDeviceInfo.ip;
                                scan.isLanBind = mLanDeviceInfo.isLanBind;
                                scan.isWanBind = mLanDeviceInfo.isWanBind;
                                scan.name = mLanDeviceInfo.name;
                                break;
                            }
                        }
                    }
                    Collections.sort(data, mBleSort);
                    LanScanAdapter.this.notifyDataSetChanged();
                }

            }
        };

    }

    private static final int MSG_CLEAR_DATA = 0x00;

    public void clearData() {
        this.selectMac = null;
        if (mUIHandler != null) {
            mUIHandler.sendEmptyMessage(MSG_CLEAR_DATA);
        }
    }

    private static final int MSG_OBTAIN_DATA = 0x01;

    public void onLanScan(LanDeviceInfo mScanBle) {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(MSG_OBTAIN_DATA, mScanBle).sendToTarget();
        }
    }

    private String selectMac;
    private static final int MSG_FLUSH_DATA = 0x02;

    public void onSelect(String mac) {
        this.selectMac = mac;
        if (mUIHandler != null) {
            mUIHandler.sendEmptyMessage(MSG_FLUSH_DATA);
        }
    }


    private static final int MSG_REMOVE_DATA = 0x03;

    public void removeDevice(String beUnbindid) {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(MSG_REMOVE_DATA, beUnbindid).sendToTarget();
        }
    }

    private static final int MSG_CHANGE_STATE_TRUE = 0x04;
    private static final int MSG_CHANGE_STATE_FALSE = 0x05;

    public void onStateChange(String sn, boolean b) {
        if (mUIHandler != null) {
            if (b) {
                mUIHandler.obtainMessage(MSG_CHANGE_STATE_TRUE, sn).sendToTarget();
            } else {
                mUIHandler.obtainMessage(MSG_CHANGE_STATE_FALSE, sn).sendToTarget();
            }
        }
    }

    private static final int MSG_OBTAIN_DATA_NO_ADD = 0x06;

    public void onLanScanByWan(LanDeviceInfo mScanBle) {
        if (mUIHandler != null) {
            mUIHandler.obtainMessage(MSG_OBTAIN_DATA_NO_ADD, mScanBle).sendToTarget();
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
            convertView = View.inflate(mContext, R.layout.item_device_newui, null);
            mHolder.mNameTxt = (TextView) convertView.findViewById(R.id.name);
            mHolder.mSelectImg = (ImageView) convertView.findViewById(R.id.select);
            mHolder.mStateImg = convertView.findViewById(R.id.state_img);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }


        LanDeviceInfo scanBle = data.get(position);
        if (flag) {
            mHolder.mStateImg.setVisibility(View.VISIBLE);
        } else {
            mHolder.mStateImg.setVisibility(View.GONE);
        }

        if (scanBle != null) {
            mHolder.mNameTxt.setText(scanBle.name);

            if (scanBle.mac != null && scanBle.mac.equalsIgnoreCase(selectMac)) {
                mHolder.mSelectImg.setVisibility(View.VISIBLE);
            } else {
                mHolder.mSelectImg.setVisibility(View.INVISIBLE);
            }

            if (flag) {
                mHolder.mStateImg.setBackgroundResource(scanBle.state ? R.mipmap.online_switch : R.mipmap.offline_newui);
            }

        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView mNameTxt;
        public ImageView mSelectImg;
        public ImageView mStateImg;
    }
}
