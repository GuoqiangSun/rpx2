package cn.com.startai.mqsdk.network;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.SparseArray;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.startai.mqsdk.util.eventbus.E_0x8002_Resp;
import cn.com.startai.mqsdk.util.udp.ISocketResult;
import cn.com.startai.mqsdk.util.udp.UdpLanCom;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.localbusi.UserBusi;
import cn.com.startai.newUI.ScanLanDeviceActivity;
import cn.com.startai.newUI.db.bean.WanBindingDevice;
import cn.com.startai.newUI.db.gen.WanBindingDeviceDao;
import cn.com.startai.newUI.db.manager.DBManager;
import cn.com.swain.baselib.util.Bit;
import cn.com.swain.baselib.util.IpUtil;
import cn.com.swain.baselib.util.MacUtil;
import cn.com.swain.baselib.util.StrUtil;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolInput;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.ProtocolBuild;
import cn.com.swain.support.protocolEngine.ProtocolProcessor;
import cn.com.swain.support.protocolEngine.ProtocolProcessorFactory;
import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.pack.ReceivesData;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.result.SimpleProtocolResult;
import cn.com.swain.support.protocolEngine.task.FailTaskResult;
import cn.com.swain169.log.Tlog;

import static cn.com.startai.mqsdk.network.BroadcastDiscoveryUtil.IBroadcastResult;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/28 0028
 * desc :
 */
public class NetworkManager implements IDataProtocolOutput, IDataProtocolInput, ControlDevice.IControlDeviceCallBack {

    public interface ILanPassThroughResult {
        void onReceiveLanPassThrough(String mac, byte[] data);
    }

    public interface ILanBindResult {
        void onDeviceLanBindResult(LanBindingDevice mLanBindingDevice);
    }

    private NetworkManager() {
    }

    /**
     * 透传
     */
    public void onLanPassThrough(String mac, byte[] data) {
        ResponseData mResponseData = new ResponseData(mac, data);
        mResponseData.arg = 12432;
        onOutputDataToServer(mResponseData);
    }

    /**
     * 透传
     */
    @Override
    public void onOutputDataToServer(ResponseData mResponseData) {
        if (mUdpCom != null) {
            if (mResponseData.obj == null) {
                mResponseData.obj = getIpByMac(mResponseData.toID);
            }
            if (mResponseData.arg == 0) {
                mResponseData.arg = 12432;
            }

            if (Tlog.isDebug()) {
                if (mResponseData.arg == 12432) {
                    Tlog.e(TAG, " onLanPassThrough data: " + mResponseData.toString());
                } else {
                    Tlog.v(TAG, " onOutputDataToServer data: " + mResponseData.toString());
                }
            }


            mUdpCom.write(mResponseData);
        } else {
            Tlog.e(TAG, " onOutputDataToServer mUdpCom=null ");
        }
    }

    private InetAddress getBroadcastAddress() {
        InetAddress address = IpUtil.getWiFiBroadcastAddress(app);
        if (address == null || address.getHostAddress().equalsIgnoreCase(IpUtil.BROAD_IP_BOUND)) {
            address = IpUtil.getLocalBroadcastAddress();
            if (address == null) {
                address = IpUtil.getBoundBroadcast();
            }
        }
        return address;
    }

    @Override
    public void onBroadcastDataToServer(ResponseData mResponseData) {

        if (mUdpCom != null) {
            if (mResponseData.data != null) {

                mResponseData.obj = getBroadcastAddress();
                mResponseData.arg = 9222;

                Tlog.w(TAG, "onBroadcastDataToServer() :" + mResponseData.toString());

                mUdpCom.broadcast(mResponseData);
            }
        }
    }

    @Override
    public void onInputServerData(ReceivesData mReceivesData) {
        if (pm != null) {
            pm.onInReceiveData(mReceivesData);
        } else {
            Tlog.e(TAG, " onInputServerData pm = null ");
        }
    }

    public String getRandomUserID() {
        return UserIDXml.getInstance(app).getRandomUserID();
    }

    public String getMqttUserID() {
//        if (Debuger.isDebug) {
//            return "3a5f4bafe52943e286c7ee7240d52a42";
//        }
        UserBusi userBusi = new UserBusi();
        C_0x8018.Resp.ContentBean currUser = userBusi.getCurrUser();
        if (currUser != null) {
            return currUser.getUserid();
        }
        return null;
    }

    public void activityCreate() {
        if (userID == null || "".equals(userID)) {
            String userID1 = getMqttUserID();
            if (userID1 == null || "".equals(userID1)) {
                userID1 = UserIDXml.getInstance(app).getUserID();
            }
            setLoginUserID(userID1);
        }
    }

    public void onBindResult(E_0x8002_Resp resp) {
        Tlog.v(TAG, " onBindResult " + String.valueOf(resp));
    }


    private static final class ClassHolder {
        private static final NetworkManager NM = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return ClassHolder.NM;
    }

    private String userID;

    public void setLoginUserID(String userID) {
        this.userID = userID;
    }

    public static final String TAG = "NetworkManager";
    private UdpLanCom mUdpCom;
    private BroadcastDiscoveryUtil mDiscoveryUtil;
    private Application app;
    private ProtocolProcessor pm;
    private ScmDeviceUtils mScmDeviceUtils;
    private final ControlDeviceUtil mControlDeviceUtil = new ControlDeviceUtil();

    public void init(Application app) {

        this.app = app;

        int version = ProtocolBuild.VERSION.VERSION_SEQ;
        ProtocolDataCache.BuildParams mParams = new ProtocolDataCache.BuildParams();
        mParams.setCustom(SocketSecureKey.Custom.CUSTOM_WAN);
        mParams.setProduct(SocketSecureKey.Custom.PRODUCT_WIFI_SOCKET);
        mParams.setProtocolVersion((byte) version);
        ProtocolDataCache.getInstance().init(mParams);
        pm = ProtocolProcessorFactory.newSingleThreadAnalysisMutilTask(
                LooperManager.getInstance().getProtocolLooper(),
                new SimpleProtocolResult() {
                    @Override
                    public void onFail(FailTaskResult failTaskResult) {

                        Tlog.e(TAG, "receive error data:" + String.valueOf(failTaskResult));

                    }

                    @Override
                    public void onSuccess(SocketDataArray mSocketDataArray) {

                        final byte protocolType = mSocketDataArray.getProtocolType();
                        final byte protocolCmd = mSocketDataArray.getProtocolCmd();

                        switch (protocolType) {

                            case SocketSecureKey.Type.TYPE_ERROR:
                                switch (protocolCmd) {
                                    case SocketSecureKey.Cmd.CMD_ERROR:
                                        doMyErrorTask(mSocketDataArray);
                                        break;
                                }
                                break;

                            case SocketSecureKey.Type.TYPE_SYSTEM:

                                switch (protocolCmd) {
                                    case SocketSecureKey.Cmd.CMD_HEARTBEAT_RESPONSE:
                                        doHeartbeatTask(mSocketDataArray);
                                        break;
                                    case SocketSecureKey.Cmd.CMD_DISCOVERY_DEVICE_RESPONSE:
                                        doDiscoveryTask(mSocketDataArray);
                                        break;
                                    case SocketSecureKey.Cmd.CMD_BIND_DEVICE_RESPONSE:
                                        doBindTask(mSocketDataArray);
                                        break;
                                    case SocketSecureKey.Cmd.CMD_REQUEST_TOKEN_RESPONSE:
                                        doRequestTokenTask(mSocketDataArray);
                                        break;
                                    case SocketSecureKey.Cmd.CMD_CONTROL_TOKEN_RESPONSE:
                                        doControlTask(mSocketDataArray);
                                        break;
                                    case SocketSecureKey.Cmd.CMD_SLEEP_TOKEN_RESPONSE:

                                        break;
                                    case SocketSecureKey.Cmd.CMD_DISCONTROL_TOKEN_RESPONSE:

                                        break;
                                }

                                break;
                        }

//                        ps: 重要，不要遗漏,回收资源
                        mSocketDataArray.setISUnUsed();


                    }
                },
                version);


        Looper workLooper = LooperManager.getInstance().getWorkLooper();
        mDiscoveryUtil = new BroadcastDiscoveryUtil(workLooper, this) {
            @Override
            public void onStartDiscovery(int mTimes) {
                super.onStartDiscovery(mTimes);
                callBroadcastResultStartDiscovery(mTimes);
            }
        };

        mUdpCom = new UdpLanCom(workLooper, new ISocketResult() {
            @Override
            public void onSocketInitResult(boolean result, String ip, int port) {
                Tlog.v(TAG, " UdpLanCom init result:" + result + " ip:" + ip + " port:" + port);
            }

            @Override
            public void onSocketReceiveData(String ip, int port, byte[] data) {
                String mac = getMacByIp(ip);


//                if (data.length > 1 && data[0] == 0x55 && data[1] == (byte) 0xaa) {
//                    callReceiveLanPassThrough(mac, data);
//                    return;
//                }

                if (port == 9222) { // 和模块交互的端口
                    if (Tlog.isDebug()) {
                        Tlog.v(TAG, " onSocketReceiveData:" + " ip:" + ip + ":" + port + " mac:" + mac + StrUtil.toString(data));
                    }
                    ReceivesData mReceiveData = new ReceivesData(mac, data);
                    mReceiveData.obj = ip;
                    mReceiveData.arg = port;
                    onInputServerData(mReceiveData);

                } else if (port == 12432) { // 透传的端口
                    if (Tlog.isDebug()) {
                        Tlog.i(TAG, " onSocketReceiveData:" + " ip:" + ip + ":" + port + " mac:" + mac + StrUtil.toString(data));
                    }
                    callReceiveLanPassThrough(mac, data);

                }

            }
        });
        mUdpCom.init();

        mScmDeviceUtils = new ScmDeviceUtils(this, new ScmDevice.OnHeartbeatCallBack() {

            @Override
            public void onStartSendHeartbeat(ScmDevice mScmDevice) {
                Tlog.v(TAG, " onStartSendHeartbeat ");
            }

            @Override
            public void onHeartbeatLose(String mac, int diff) {
                Tlog.v(TAG, " onHeartbeatLose  mac:" + mac + " diff:" + diff);

                ControlDevice controlDevice = mControlDeviceUtil.get(mac);
                if (controlDevice != null) {
                    controlDevice.heartbeatLose(diff);
                }

            }
        });
    }


    private String getMacByIp(String ip) {
        LanDeviceInfo lanDeviceInfo = discoveryMapByIp.get(ip);
        String mac = null;
        if (lanDeviceInfo != null) {
            mac = lanDeviceInfo.getMac();
        }

        if (mac == null) {
            mac = "00:00:00:00:00:00";
        }
        return mac;
    }

    public boolean canLanControl(String mac) {
        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        if (controlDevice != null) {
            return controlDevice.canLanCom();
        }
        return false;
    }

    public LanDeviceInfo getLanDeviceInfoByMac(String mac) {
        return discoveryMapByMac.get(mac);
    }

    public String getIpByMac(String mac) {
        LanDeviceInfo lanDeviceInfo = discoveryMapByMac.get(mac);
        String ip = null;
        if (lanDeviceInfo != null) {
            ip = lanDeviceInfo.getIp();
        }
        return ip;
    }

    private Map<String, LanDeviceInfo> discoveryMapByMac = Collections.synchronizedMap(new HashMap<String, LanDeviceInfo>());
    private Map<String, LanDeviceInfo> discoveryMapByIp = Collections.synchronizedMap(new HashMap<String, LanDeviceInfo>());

    private void clearDiscovery() {
        Tlog.e(TAG, " clearDiscovery ");
        discoveryMapByIp.clear();
        discoveryMapByMac.clear();
    }

    //绑定设备，设备返回的信息
    private void doBindTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams.length < 77) {
            Tlog.e(TAG, " param length error : " + protocolParams.length);
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        LanBindingDevice mLanBindingDevice = new LanBindingDevice();

        boolean admin = protocolParams[1] == 0x01; // 管理员
        mLanBindingDevice.setIsAdmin(admin);

        String deviceUserID = new String(protocolParams, 2, 32); // userID
        mLanBindingDevice.setOid(deviceUserID);

        String userID = new String(protocolParams, 2 + 32, 32); // userID
        mLanBindingDevice.setMid(userID);

        String mac = MacUtil.byteToMacStr(protocolParams, 2 + 32 + 32);
        mLanBindingDevice.setOmac(mac);

        byte[] CPU_BUF = new byte[4];
        CPU_BUF[3] = protocolParams[2 + 32 + 32 + 6];
        CPU_BUF[2] = protocolParams[2 + 32 + 32 + 6 + 1];
        CPU_BUF[1] = protocolParams[2 + 32 + 32 + 6 + 2];
        CPU_BUF[0] = protocolParams[2 + 32 + 32 + 6 + 3];

        String cpuInfo = String.format("%x%x%x%x", CPU_BUF[0], CPU_BUF[1], CPU_BUF[2], CPU_BUF[3]);
        mLanBindingDevice.setCpuInfo(cpuInfo);

        int isBindResult = protocolParams[2 + 32 + 32 + 6 + 4];
        mLanBindingDevice.setModel(isBindResult);

        Tlog.v(TAG, " bind result:" + result + String.valueOf(mLanBindingDevice));

        onDeviceResponseLanBind(mLanBindingDevice);

        callLanBind(mLanBindingDevice);

    }


    /**
     * 局域内设备被绑定
     */
    synchronized void onDeviceResponseLanBind(LanBindingDevice mLanBindingDevice) {

        Tlog.e(TAG, " onDeviceResponseLanBind() success " + mLanBindingDevice.getOmac());
        WanBindingDeviceDao bindingDeviceDao = DBManager.getInstance().getDaoSession().getWanBindingDeviceDao();
        List<WanBindingDevice> listBind = bindingDeviceDao.queryBuilder()
                .where(WanBindingDeviceDao.Properties.Mid.eq(mLanBindingDevice.getMid()),
                        WanBindingDeviceDao.Properties.Oid.eq(mLanBindingDevice.getOid())).list();

        WanBindingDevice mWanBindingDevice = null;

        if (listBind != null && listBind.size() > 0) {
            mWanBindingDevice = listBind.get(0);
        }

        if (mWanBindingDevice == null) {
            mWanBindingDevice = new WanBindingDevice();
            mWanBindingDevice.setMac(mLanBindingDevice.getOmac());
            mWanBindingDevice.setOid(mLanBindingDevice.getOid());
            mWanBindingDevice.setMid(mLanBindingDevice.getMid());
            mWanBindingDevice.setIsAdmin(mLanBindingDevice.getIsAdmin());
            mWanBindingDevice.setCpuInfo(mLanBindingDevice.getCpuInfo());

            long insert = bindingDeviceDao.insert(mWanBindingDevice);
            Tlog.d(TAG, " getWanBindingDeviceDao insert:" + insert);
        } else {
            mWanBindingDevice.setHasBindingByLan(true);
            mWanBindingDevice.setIsAdmin(mLanBindingDevice.getIsAdmin());
            mWanBindingDevice.setCpuInfo(mLanBindingDevice.getCpuInfo());
            bindingDeviceDao.update(mWanBindingDevice);
            Tlog.d(TAG, " getWanBindingDeviceDao has this device:" + mWanBindingDevice.getGid());
        }

    }


    private void doRequestTokenTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 9) {
            Tlog.e(TAG, " protocolParams error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        int random = (protocolParams[1] & 0xFF) << 24 | (protocolParams[2] & 0xFF) << 16 |
                (protocolParams[3] & 0xFF) << 8 | (protocolParams[4] & 0xFF);

        int token = (protocolParams[5] & 0xFF) << 24 | (protocolParams[6] & 0xFF) << 16 |
                (protocolParams[7] & 0xFF) << 8 | (protocolParams[8] & 0xFF);

        String mac = mSocketDataArray.getID();

        Tlog.e(TAG, " result :" + result + " id:" + mac + " random : " + random + " token: 0x" + Integer.toHexString(token));

        if (result) {
            ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
            int requestRandom = scmDevice.getRequestRandom();
            Tlog.e(TAG, " requestRandom:" + requestRandom + " " + random);
            if (requestRandom == random - 1) {
                scmDevice.setToken(token);

                if (mControlDeviceUtil.containsKey(mac)) {
                    ControlDevice controlDevice = mControlDeviceUtil.get(mac);
                    if (controlDevice != null) {
                        controlDevice.onResponseToken(userID, token);
                    }
                }
            }
        }
    }

    private void doControlTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length < 1) {
            Tlog.e(TAG, " ControlReceiveTask error:" + mSocketDataArray.toString());
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);
        Tlog.e(TAG, " ControlReceiveTask result:" + result + " params:" + protocolParams[0]);

        String mac = mSocketDataArray.getID();
        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        scmDevice.setConResult(result);
        scmDevice.connected();

        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        if (controlDevice != null) {
            controlDevice.responseConnected(result);
            if (result) {
                controlDevice.canLanCom();
                ResponseData responseData = getDeviceModelInCache(mac);
                if (responseData != null) {
                    onOutputDataToServer(responseData);
                }

            } else {
                controlDevice.responseConnectedFail(userID);
            }
        }


    }

    private void onTokenInvalid(String mac) {
        ControlDevice controlDevice = mControlDeviceUtil.get(mac);
        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        int token = scmDevice.getToken();
        if (controlDevice != null) {
            controlDevice.onTokenInvalid(token, userID);
        }
    }

    private void doMyErrorTask(SocketDataArray mSocketDataArray) {

        if (Tlog.isDebug()) {
            Tlog.e(TAG, mSocketDataArray.toString());
        }

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null || protocolParams.length <= 0) {
            Tlog.e(TAG, " new MyErrorTask() params is null ");
            return;
        }

        byte protocolParam = protocolParams[0];

        if (protocolParams.length >= 3) {
            byte paramType = protocolParams[1];
            byte paramCmd = protocolParams[2];
            Tlog.e(TAG, " myType:" + Integer.toHexString(paramType) + " myCmd:" + Integer.toHexString(paramCmd));
        }

        switch (protocolParam) {

            case 0x02:
//                crc错误
                Tlog.e(TAG, " CRC error ");
                break;
            case 0x03:
//                type错误
                Tlog.e(TAG, " type error ");
                break;
            case 0x04:
//                cmd错误
                Tlog.e(TAG, " cmd error ");
                break;
            case 0x05:
//                length错误
                Tlog.e(TAG, " length error ");
                break;
            case 0x06:
                Tlog.e(TAG, "  token invalid  ");
                String id = mSocketDataArray.getID();
                onTokenInvalid(id);
                break;
            default:
                Tlog.e(TAG, " default error ");
                break;

        }


    }

    private void doHeartbeatTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams == null) {
            Tlog.e(TAG, " protocolParams == null");
            return;
        }

        Tlog.d(TAG, "Heartbeat result:" + SocketSecureKey.Util.resultIsOk(protocolParams[0]) + "--value:" + protocolParams[0]);

        ControlDevice controlDevice = mControlDeviceUtil.get(mSocketDataArray.getID());
        if (controlDevice != null) {
            controlDevice.receiveHeartbeat(SocketSecureKey.Util.resultIsOk(protocolParams[0]));
        }

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mSocketDataArray.getID());
        scmDevice.onReceiveHeartbeat(SocketSecureKey.Util.resultIsOk(protocolParams[0]));

        if (SocketSecureKey.Util.resultTokenInvalid(protocolParams[0])) {
            onTokenInvalid(mSocketDataArray.getID());
        }

    }

    // 发现设备，设备返回的消息
    private void doDiscoveryTask(SocketDataArray mSocketDataArray) {

        byte[] protocolParams = mSocketDataArray.getProtocolParams();

        if (protocolParams.length < 46) {
            Tlog.v(TAG, " length error : " + protocolParams.length);
            return;
        }

        boolean result = SocketSecureKey.Util.resultIsOk(protocolParams[0]);

        LanDeviceInfo mWiFiDevice = new LanDeviceInfo();
        mWiFiDevice.ip = (String) mSocketDataArray.getObj();
        mWiFiDevice.state = true;

        mWiFiDevice.model = protocolParams[1];
        mWiFiDevice.mac = MacUtil.byteToMacStr(protocolParams, 2);
        mWiFiDevice.port = mSocketDataArray.getArg();

        String name = new String(protocolParams, 8, 32);
        mWiFiDevice.name = name.trim().replaceAll("\\s*", "");
        if (StrUtil.isSpecialName(mWiFiDevice.name)) {
            mWiFiDevice.name = null;
            mWiFiDevice.checkName();

//            int random = (int) ((Math.random() * 9 + 1) * 100000);
//            mWiFiDevice.name = "UNKNOWN" + random;

        }

        int pointMainVersion = 8 + 32;
        mWiFiDevice.mainVersion = protocolParams[pointMainVersion] & 0xFF;
        mWiFiDevice.subVersion = protocolParams[pointMainVersion + 1] & 0xFF;
//        protocolParams[pointMainVersion + 2] ; 语言

        byte protocolParam;

        protocolParam = protocolParams[pointMainVersion + 3];
        mWiFiDevice.hasAdmin = SocketSecureKey.Util.isTrue((byte) (protocolParam & 0x01));
        mWiFiDevice.bindNeedPwd = SocketSecureKey.Util.isTrue((byte) ((protocolParam >> 1) & 0x01));
        mWiFiDevice.isAdmin = SocketSecureKey.Util.isTrue((byte) ((protocolParam >> 2) & 0x01));

//        mWiFiDevice.isBind = (((protocolParam >> 4) & 0x01) >= 0x01);

        boolean wanBindTrue = SocketSecureKey.Util.isTrue((byte) ((protocolParam >> 5) & 0x01));
        mWiFiDevice.isWanBind = wanBindTrue;
        mWiFiDevice.isLanBind = wanBindTrue || SocketSecureKey.Util.isTrue((byte) ((protocolParam >> 4) & 0x01));

//          bit 4  1 局域网绑定
//        bit 5    1 广域网绑定
        protocolParam = protocolParams[pointMainVersion + 4];
        mWiFiDevice.hasRemote = SocketSecureKey.Util.isTrue((byte) ((protocolParam >> 1) & 0x01));
        mWiFiDevice.hasActivate = SocketSecureKey.Util.isTrue((byte) (protocolParam & 0x01));

        int rssi = protocolParams[pointMainVersion + 5];
        if (rssi > 0) {
            rssi -= 100;
        }
        mWiFiDevice.rssi = rssi;

        Tlog.v(TAG, "discovery: result:" + result + String.valueOf(mWiFiDevice));

        discoveryMapByMac.put(mWiFiDevice.mac, mWiFiDevice);
        discoveryMapByIp.put(mWiFiDevice.ip, mWiFiDevice);

        ScmDevice scmDevice = mScmDeviceUtils.containScmDevice(mWiFiDevice.getMac());

        if (scmDevice != null) {
            ControlDevice controlDevice = mControlDeviceUtil.get(mWiFiDevice.getMac());
            if (controlDevice != null) {
                controlDevice.lanDeviceDiscovery(scmDevice.getToken(), userID);
            }
        }

        callBroadcastResultDiscoveryOne(mWiFiDevice);

    }

    /**
     * 绑定设备
     */
    public void bindDevice(String mac, String ip) {
        Tlog.v(TAG, " bindDevice  " + mac + " userID:" + userID);
        byte[] bytes = userID != null ? userID.getBytes() : null;
        // bit0=1 绑定
        // bit1=1 真实userID
        int info = new Bit().add(0).reserve(1, (bytes != null && bytes.length > 0)).getDevice();
        ResponseData mResponseData = ProtocolDataCache.getBindDevice(mac, bytes, (byte) info, null);
        mResponseData.obj = ip;
        mResponseData.arg = 9222;
        if (Tlog.isDebug()) {
            Tlog.v(TAG, " bindDevice data: " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }

    /**
     * 请求token
     */
    private void requestToken(String mac, String userID) {
        int random = (int) ((Math.random() * 9 + 1) * 100000);
        Tlog.v(TAG, " requestToken  " + userID + " random:" + random);

        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        scmDevice.putRequestTokenRandom(random);

        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = ProtocolDataCache.getRequestToken(mac, bytes, random);
        mResponseData.arg = 9222;
        if (Tlog.isDebug()) {
            Tlog.v(TAG, " requestToken data: " + mResponseData.toString());
        }
        onOutputDataToServer(mResponseData);
    }


    private void controlDevice(String mac, String userID, int token) {
        Tlog.v(TAG, " controlDevice  " + userID + " token:" + Integer.toHexString(token));
        mScmDeviceUtils.getScmDevice(mac).setToken(token);
        byte[] bytes = userID != null ? userID.getBytes() : null;
        ResponseData mResponseData = ProtocolDataCache.getControlDevice(mac, bytes, token);
        mResponseData.arg = 9222;
        onOutputDataToServer(mResponseData);
    }

    private final Map<String, ResponseData> mGetDeviceModel = Collections.synchronizedMap(new HashMap<String, ResponseData>());

    public ResponseData getDeviceModelInCache(String mac) {

        Tlog.v(ScanLanDeviceActivity.TAG, " getDeviceModel: " + mac + " size:" + mGetDeviceModel.size());
//
//        for (Map.Entry<String, ResponseData> e : mGetDeviceModel.entrySet()) {
//            ResponseData value = e.getValue();
//            String key = e.getKey();
//            Tlog.v(ScanLanDeviceActivity.TAG, " for key:" + key + " value:" + (value == null));
//        }

        return mGetDeviceModel.get(mac);
    }

    public void getDeviceModelByLan(String mac, byte[] bytes) {
        String ipByMac = getIpByMac(mac);
        Tlog.w(ScanLanDeviceActivity.TAG, "getDeviceModel mac:" + mac + " ipByMac:" + ipByMac);
        ResponseData mResponseData = new ResponseData(mac, bytes);
        if (ipByMac != null) {
            mResponseData.obj = ipByMac;
        } else {
            mResponseData.obj = null;
        }
        mResponseData.arg = 12432;
        Tlog.w(ScanLanDeviceActivity.TAG, "getDeviceModel put mac:" + mac);
        mGetDeviceModel.put(mac, mResponseData);
    }

    public void removeGetDeviceModelMsg(String mac) {
        Tlog.w(ScanLanDeviceActivity.TAG, "removeGetDeviceModelMsg mac:");
        mGetDeviceModel.remove(mac);
    }

    public void connectWiFiDevice(String mac) {

        Tlog.e(TAG, " controlWiFiDevice()  mac:" + mac);

        ControlDevice mControlDevice = mControlDeviceUtil.get(mac);
        if (mControlDevice == null) {
            mControlDevice = new ControlDevice(mac, this);
            mControlDeviceUtil.put(mac, mControlDevice);
        } else {
            mControlDevice.removeCallJsCon();
        }

        LanDeviceInfo mLanDiscoveryDevice = discoveryMapByMac.get(mac);
        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        int token = 0;
        if (scmDevice != null) {
            token = scmDevice.getToken();
        }
        mControlDevice.controlWiFiDevice(token, userID, mLanDiscoveryDevice != null);

    }

    public void disconnectWiFiDevice(String mac) {

        Tlog.e(TAG, " disconnectWiFiDevice()  mac:" + mac);

        ControlDevice mControlDevice = mControlDeviceUtil.get(mac);
        if (mControlDevice != null) {
            mControlDevice.disControl();
        }
        ScmDevice scmDevice = mScmDeviceUtils.getScmDevice(mac);
        scmDevice.disconnected();

        byte[] bytes = null;
        if (userID != null) {
            bytes = userID.getBytes();
        }

        ResponseData mResponseData = ProtocolDataCache.getAppSleep(mac, bytes, scmDevice.getToken());
        onOutputDataToServer(mResponseData);

    }

    @Override
    public void onResultNeedRequestToken(String mac, String loginUserID) {
        requestToken(mac, loginUserID);
    }

    @Override
    public void onResultCanControlDevice(String mac, String loginUserID, int token) {
        controlDevice(mac, loginUserID, token);
    }


    /***********/


    private final SparseArray<ILanBindResult> mILanBindResults = new SparseArray<>();


    public synchronized void addLanBindCallBack(ILanBindResult mILanBindResult) {
        mILanBindResults.put(mILanBindResult.hashCode(), mILanBindResult);
    }


    public synchronized void removeLanBindCallBack(ILanBindResult mILanBindResult) {
        mILanBindResults.remove(mILanBindResult.hashCode());
    }


    private synchronized void callLanBind(LanBindingDevice mLanBindingDevice) {

        for (int i = 0; i < mILanBindResults.size(); i++) {
            ILanBindResult iILanBindResult = mILanBindResults.valueAt(i);
            iILanBindResult.onDeviceLanBindResult(mLanBindingDevice);
        }

    }


    public void discoveryLanDevice(int times) {
        mDiscoveryUtil.startDiscovery(userID, times);
    }

    private final SparseArray<IBroadcastResult> mIBroadcastResults = new SparseArray<>();

    private synchronized void callBroadcastResultStartDiscovery(int times) {

        for (int i = 0; i < mIBroadcastResults.size(); i++) {
            IBroadcastResult iBroadcastResult = mIBroadcastResults.valueAt(i);
            iBroadcastResult.onBroadcastStart(times);
        }

    }

    private synchronized void callBroadcastResultDiscoveryOne(LanDeviceInfo mWiFiDevice) {

        for (int i = 0; i < mIBroadcastResults.size(); i++) {

            IBroadcastResult iBroadcastResult = mIBroadcastResults.valueAt(i);
            iBroadcastResult.onDiscoveryDevice(mWiFiDevice);
        }
    }

    public synchronized void addDiscoveryCallBack(IBroadcastResult mBroadcast) {
        mIBroadcastResults.put(mBroadcast.hashCode(), mBroadcast);
    }


    public synchronized void removeDiscoveryCallBack(IBroadcastResult mBroadcast) {
        mIBroadcastResults.remove(mBroadcast.hashCode());
    }

    private final SparseArray<ILanPassThroughResult> mIPassThroughResults = new SparseArray<>();


    public synchronized void addPassThroughCallBack(ILanPassThroughResult mBroadcast) {
        mIPassThroughResults.put(mBroadcast.hashCode(), mBroadcast);
    }


    public synchronized void removePassThroughCallBack(ILanPassThroughResult mPassThrough) {
        mIPassThroughResults.remove(mPassThrough.hashCode());
    }


    private synchronized void callReceiveLanPassThrough(String mac, byte[] data) {

        for (int i = 0; i < mIPassThroughResults.size(); i++) {
            ILanPassThroughResult iBroadcastResult = mIPassThroughResults.valueAt(i);
            iBroadcastResult.onReceiveLanPassThrough(mac, data);
        }
    }

    private int regRecTimes = 0;

    public void registerReceiver() {

        if (regRecTimes <= 0) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            try {
                app.registerReceiver(mNetWorkStateReceiver, filter);
            } catch (Exception e) {

            }
        }

        ++regRecTimes;
    }

    public void unregisterReceiver() {

        --regRecTimes;

        if (regRecTimes <= 0) {
            regRecTimes = 0;
            try {
                app.unregisterReceiver(mNetWorkStateReceiver);
            } catch (Exception e) {

            }

        }
    }

    private final BroadcastReceiver mNetWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Tlog.v(TAG, " android.net.conn.CONNECTIVITY_CHANGE " + intent.getAction());

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr == null) {
                Tlog.e(TAG, "ConnectivityManager==null");
                return;
            }

            boolean wifiConnected = false;

            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiNetworkInfo.isConnected()) {

                Tlog.v(TAG, "WIFI isConnected ");
                wifiConnected = true;

            } else if (wifiNetworkInfo.isConnectedOrConnecting()) {

                Tlog.v(TAG, "WIFI isConnecting");

            } else {

                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (dataNetworkInfo.isConnected()) {

                    Tlog.v(TAG, "mobile isConnected ");

                } else if (dataNetworkInfo.isConnectedOrConnecting()) {

                    Tlog.v(TAG, "mobile isConnecting");

                } else {

                    Tlog.v(TAG, " unknown network ");

                }
            }

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            //获取所有网络连接的信息
//            Network[] networks = connMgr.getAllNetworks();
//
//            //通过循环将网络信息逐个取出来
//            for (Network network : networks) {
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//                ｝
//        }

            if (!WifiManager.NETWORK_STATE_CHANGED_ACTION.equalsIgnoreCase(intent.getAction())) {
                clearDiscovery();
                mControlDeviceUtil.onNetworkStateChange();
            }

            if (wifiConnected) {
                discoveryLanDevice(6);
            }

        }
    };


}
