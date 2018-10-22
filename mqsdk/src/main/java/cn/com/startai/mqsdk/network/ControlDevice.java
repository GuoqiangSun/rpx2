package cn.com.startai.mqsdk.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/10 0010
 * desc :
 */
public class ControlDevice {

    public interface IControlDeviceCallBack {

        void onResultNeedRequestToken(String mac, String loginUserID);

        void onResultCanControlDevice(String mac, String loginUserID, int token);

    }


    private String TAG = NetworkManager.TAG;
    private RepeatHandler mRepeatHandler;
    private IControlDeviceCallBack mResultCallBack;
    private String mac;

    private boolean canLanCom = false;

    private void setCanLanCom() {
        this.canLanCom = true;
    }

    private void setCanNotLanCom() {
        this.canLanCom = false;
    }

    public boolean canLanCom() {
        return canLanCom;
    }

    private boolean canWanCom = false;

    private void setCanWanCom() {
        this.canWanCom = true;
    }

    public boolean canWanCom() {
        return canLanCom;
    }


    public ControlDevice(String mac, IControlDeviceCallBack mResultCallBack) {
        this.mac = mac;
        this.mResultCallBack = mResultCallBack;
        this.mRepeatHandler = new RepeatHandler(this, LooperManager.getInstance().getRepeatLooper());
    }

    private static final int MSG_WHAT_CALL_JS_CON = 0x02;

    private boolean hasCallJsCon = false;

    private void handleMessage(Message msg) {

        if (msg.what == MSG_WHAT_CALL_JS_CON) {
            if (mResultCallBack != null) {
                hasCallJsCon = true;
            }
        }

    }

    public void receiveHeartbeat(boolean result) {
        if (result) {
            if (!canLanCom()) {
                Tlog.e(TAG, " receiveHeartbeat but canNotLanCom");
                resetSendConTokenTimes();
            }
        }
    }

    public void heartbeatLose(int times) {
        Tlog.e(TAG, " heartbeatLose " + times + " setCanNotLanCom() ");
        setCanNotLanCom();
        if (getSendConTokenTimes() > 3) {
            resetSendConTokenTimes();
        }
        resetConnectedTimes();
    }

    private void callJsCon(int delay) {
        if (mRepeatHandler.hasMessages(MSG_WHAT_CALL_JS_CON)) {
            mRepeatHandler.removeMessages(MSG_WHAT_CALL_JS_CON);
        }
        mRepeatHandler.sendEmptyMessageDelayed(MSG_WHAT_CALL_JS_CON, delay);
    }


    public void removeCallJsCon() {

        mRepeatHandler.removeMessages(MSG_WHAT_CALL_JS_CON);

    }

    public void release() {

        if (mRepeatHandler != null) {
            mRepeatHandler.removeCallbacksAndMessages(null);
        }

    }

    private int conTokenTimes = 0;

    private int getSendConTokenTimes() {
        return conTokenTimes;
    }

    private void resetSendConTokenTimes() {
        Tlog.i(TAG, " resetSendConTokenTimes() ");
        this.conTokenTimes = 0;
    }

    public void onNetworkStateChange() {
        Tlog.d(TAG, " onNetworkStateChange  setCanNotLanCom()");
        resetSendConTokenTimes();
        setCanNotLanCom();
    }

    public void onTokenInvalid(int token, String loginUserID) {
        Tlog.d(TAG, " onTokenInvalid() token " + token + " userID:" + loginUserID + " setCanNotLanCom() ");
        if (control) {
            setCanNotLanCom();
            checkComModel(-1, loginUserID);
        }
    }

    public void lanDeviceDiscovery(int token, String loginUserID) {
        Tlog.d(TAG, " lanDeviceDiscovery() token " + token + " userID:" + loginUserID + " canLanCom:" + canLanCom() + " control:" + control);
        if (control && !canLanCom()) {
            checkComModel(token, loginUserID);
        }
    }

    private boolean control;

    public void controlWiFiDevice(int token, String loginUserID, boolean lanDiscovery) {
        Tlog.d(TAG, " controlWiFiDevice() token " + token + " userID:" + loginUserID + " lanDiscovery:" + lanDiscovery);

        hasCallJsCon = false;
        control = true;

        resetSendConTokenTimes();

        if (!lanDiscovery) {
            callJsCon(1000);
        } else {
            checkComModel(token, loginUserID);
            callJsCon(1000 * 10);
        }

    }

    public void disControl() {
        Tlog.e(TAG, " disControl " + mac + " setCanNotLanCom() ");
        control = false;
        removeCallJsCon();
        setCanNotLanCom();
        resetConnectedTimes();
        resetSendConTokenTimes();
    }

    public void onResponseToken(String loginUserID, int token) {

        Tlog.e(TAG, " onResponseToken() loginUserID :" + loginUserID
                + " token:" + Integer.toHexString(token));

        if (control) {
            checkComModel(token, loginUserID);
        }

    }

    private int responseConnectedTimes = 0;

    private void resetConnectedTimes() {
        Tlog.i(TAG, " resetConnectedTimes() ");
        this.responseConnectedTimes = 0;
    }

    public void responseConnected(boolean canLanCom) {
        Tlog.d(TAG, " responseConnected  canLanCom:" + canLanCom + " responseConnectedTimes:" + responseConnectedTimes);

        // 防止一直返回连接成功
        if (++responseConnectedTimes <= 6) {
            resetSendConTokenTimes();
        }

        removeCallJsCon();
        if (canLanCom) {
            setCanLanCom();
        }

        if (!hasCallJsCon) {
            callJsCon(100);
        }
    }

    public void responseConnectedFail(String loginUserID) {
        Tlog.e(TAG, " responseConnected  canLanCom:" + canLanCom);
        checkComModel(-1, loginUserID);
    }

    // 检测通信模式
    private void checkComModel(int token, String loginUserID) {

        if (getSendConTokenTimes() > 3) {
            Tlog.e(TAG, " checkComModel() getSendConTokenTimes " + getSendConTokenTimes()
                    + " responseConnectedTimes:" + responseConnectedTimes + " return;");
            return;
        }


        if (token == -1 || token == 0) {
            Tlog.e(TAG, " checkComModel() NeedRequestToken ");

            if (mResultCallBack != null) {
                mResultCallBack.onResultNeedRequestToken(mac, loginUserID);
            }

        } else {
            Tlog.e(TAG, " checkComModel() canControlDevice token " + Integer.toHexString(token));

            conTokenTimes++;

            if (mResultCallBack != null) {
                mResultCallBack.onResultCanControlDevice(mac, loginUserID, token);
            }
        }

    }


    private static final class RepeatHandler extends Handler {
        private final WeakReference<ControlDevice> wr;

        RepeatHandler(ControlDevice mNetworkManager, Looper mLooper) {
            super(mLooper);
            wr = new WeakReference<>(mNetworkManager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ControlDevice mNetworkManager;
            if ((mNetworkManager = wr.get()) != null) {
                mNetworkManager.handleMessage(msg);
            }

        }
    }


}
