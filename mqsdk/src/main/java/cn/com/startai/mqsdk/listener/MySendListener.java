package cn.com.startai.mqsdk.listener;

import cn.com.startai.mqsdk.MyApp;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;

/**
 * Created by Robin on 2018/7/10.
 * qq: 419109715 彬影
 */

public class MySendListener implements IOnCallListener {
    @Override
    public void onSuccess(MqttPublishRequest request) {
        TAndL.TL(MyApp.getContext(), "消息发送成功");
    }

    @Override
    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
        TAndL.TL(MyApp.getContext(), "消息发送失败 " + startaiError.getErrorMsg());
    }

    @Override
    public boolean needUISafety() {
        return true;
    }
}
