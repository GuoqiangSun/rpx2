package cn.com.startai.mqsdk.widget;

import cn.com.startai.kp8.util.Rp86MCommond;
import cn.com.startai.kp8.util.Rp86MCommondUtils;

public class MyLongClickUpListener implements LongClickButton.LongClickUpListener {
    String deviceHexCode;
    public MyLongClickUpListener(String deviceHexCode) {
        this.deviceHexCode = deviceHexCode;
    }

    @Override
    public void upAction() {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_SHUTDOWN);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());

    }

    @Override
    public void downAction() {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_STARTUP);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());

    }

    @Override
    public void downing() {
        //Looper.prepare();
        downAction();
        //Looper.loop();
    }


    public void sendCommond(String hexCommond){
        //StartAI.getInstance().getBaseBusiManager().passthrough(((Rp86MMasterActivity)getActivity()).getDevice().getId(), hexCommond, ((NewUIBaseActivity)getActivity()).onCallListener);
    }
}