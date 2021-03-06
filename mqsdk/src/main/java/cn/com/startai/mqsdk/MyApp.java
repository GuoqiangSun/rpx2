package cn.com.startai.mqsdk;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;

import cn.com.startai.kp8.crash.CrashExceptionHandle;
import cn.com.startai.kp8.util.FileUtil;
import cn.com.startai.mqsdk.listener.MyPushListener;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.network.LooperManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Break;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Failed;
import cn.com.startai.mqsdk.util.eventbus.E_Conn_Success;
import cn.com.startai.mqsdk.util.eventbus.EventBean;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.event.IConnectionStateListener;
import cn.com.startai.mqttsdk.event.PersistentEventDispatcher;
import cn.com.startai.mqttsdk.mqtt.MqttInitParam;
import cn.com.startai.newUI.db.manager.DBManager;
import cn.com.swain169.log.Tlog;

/**
 * Created by Robin on 2018/6/21.
 * qq: 419109715 彬影
 */

public class MyApp extends Application {

    public static String domain = "okaylight"; //开发者平台获取
    public static String appid = "7446b4eaf72aafe4fabc5dac3374fcb8";//开发者平台获取
    public static String apptype = "smartOlWifi/controll/android";//开发者平台获取
    public static String m_ver = "Json_1.2.4_9.2.1";//文档约定

//    public static String domain = "okaylight"; //开发者平台获取
//    public static String appid = "09c1e6662ff3ee7e74b8f429ea4a6850";//开发者平台获取
//    public static String apptype = "smartOlWifi/controll/android";//开发者平台获取
//    public static String m_ver = "Json_1.2.4_9.2.1";//文档约定


//    public static String domain = "etone"; //开发者平台获取
//    public static String appid = "44f0f30da912abb1006f21304c8f713f";//开发者平台获取
//    public static String apptype = "smartAd/controll/android";//开发者平台获取
//    public static String m_ver = "Json_1.2.4_9.2.1";//文档约定


    private static String TAG = MyApp.class.getSimpleName();

    private static Context context;

    public static Context getContext() {

        return MyApp.context;

    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Tlog.setDebug(BuildConfig.DEBUG);
//        Tlog.setPrintStackDebug(BuildConfig.DEBUG);

        LooperManager.getInstance().init(this);
        NetworkManager.getInstance().init(this);
        DBManager.getInstance().init(this);

//        startService(new Intent(this, MyService.class));

        FileUtil.initFileDir();
        CrashExceptionHandle.getIntance().init(this);
        //工具类初始化
        Utils.init(getApplicationContext());
        MqttInitParam initParam = new MqttInitParam(domain, apptype, appid, m_ver);

        StartAI.getInstance().initialization(getApplicationContext(), initParam);

        PersistentEventDispatcher.getInstance().registerOnTunnelStateListener(new IConnectionStateListener() {
            @Override
            public void onConnectFail(int errorCode, String errorMsg) {
                TAndL.TL(getApplicationContext(), "连接失败，" + errorMsg);
                EventBus.getDefault().post(new EventBean(EventBean.S_2_A_CONN_FAILED, new E_Conn_Failed(errorCode, errorMsg)));
            }

            @Override
            public void onConnected() {
                TAndL.TL(getApplicationContext(), "连接成功");
                EventBus.getDefault().post(new EventBean(EventBean.S_2_A_CONN_SUCCESS, new E_Conn_Success()));
            }

            @Override
            public void onDisconnect(int errorCode, String errorMsg) {
                TAndL.TL(getApplicationContext(), "连接断开 " + errorMsg);
                EventBus.getDefault().post(new EventBean(EventBean.S_2_A_CONN_BREAK, new E_Conn_Break(errorCode, errorMsg)));
            }

            @Override
            public boolean needUISafety() {
                return true;
            }
        });


        PersistentEventDispatcher.getInstance().registerOnPushListener(new MyPushListener());
    }
}


