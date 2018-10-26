package cn.com.startai.mqsdk.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/11 0011
 * desc :
 */
public class BroadcastDiscoveryUtil {

    private final IDataProtocolOutput mControlWiFi;
    private final BroadcastHandler mBroadcastHandler;

    public interface IBroadcastResult {
        void onBroadcastStart(int remainingTimes);

        void onDiscoveryDevice(LanDeviceInfo mLanDeviceInfo);
    }

    public BroadcastDiscoveryUtil(Looper mWorkLooper, IDataProtocolOutput mControlWiFi) {
        this.mControlWiFi = mControlWiFi;
        this.mBroadcastHandler = new BroadcastHandler(mWorkLooper, this);
    }

    public void discoveryDevice(String userID) {
        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = ProtocolDataCache.getDiscoveryDevice("255.255.255.255", bytes);
        mControlWiFi.onBroadcastDataToServer(mResponseData);
    }

    private boolean start = false;
    private int mTimes = 0;

    public void startDiscovery(String userID, int times) {
        start = true;
        if (mBroadcastHandler.hasMessages(MSG_WHAT_SEND)) {
            mBroadcastHandler.removeMessages(MSG_WHAT_SEND);
        }
        if (this.mTimes < times) {
            this.mTimes = times;
        }

        Tlog.v(NetworkManager.TAG, " startDiscovery() " + userID + " times:" + mTimes);

        mBroadcastHandler.obtainMessage(MSG_WHAT_SEND, userID).sendToTarget();

    }

    private static final long DELAY = 3000;

    public void stopDiscovery() {
        this.start = false;
        this.mTimes = 0;
        if (mBroadcastHandler.hasMessages(MSG_WHAT_SEND)) {
            mBroadcastHandler.removeMessages(MSG_WHAT_SEND);
        }
        mBroadcastHandler.removeCallbacksAndMessages(null);
    }

    private static final int MSG_WHAT_SEND = 0x01;

    private void handleMessage(Message msg) {

        onStartDiscovery(mTimes);

        String userID = (String) msg.obj;
        discoveryDevice(userID);

        mTimes--;


        if (start && mTimes > 0) {
            Message message = mBroadcastHandler.obtainMessage(MSG_WHAT_SEND, userID);
            mBroadcastHandler.sendMessageDelayed(message, DELAY);
        }

    }

    public void onStartDiscovery(int mTimes) {
    }

    private static class BroadcastHandler extends Handler {
        private final WeakReference<BroadcastDiscoveryUtil> wr;

        public BroadcastHandler(Looper mLooper, BroadcastDiscoveryUtil mBroadcastDiscoveryUtil) {
            super(mLooper);
            wr = new WeakReference<>(mBroadcastDiscoveryUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BroadcastDiscoveryUtil broadcastDiscoveryUtil;

            if (wr != null && (broadcastDiscoveryUtil = wr.get()) != null) {
                broadcastDiscoveryUtil.handleMessage(msg);
            }
        }
    }


}
