package cn.com.startai.mqsdk.util.eventbus;

import cn.com.startai.mqttsdk.busi.entity.C_0x8001;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;

/**
 * Created by Robin on 2018/7/10.
 * qq: 419109715 彬影
 */

public class E_0x8020_Resp {

    private int result;
    private C_0x8020.Resp.ContentBean message;
    private String errorCode;
    private String errorMsg;

    @Override
    public String toString() {
        return "E_0x8020_Resp{" +
                "result=" + result +
                ", message=" + message +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

    public E_0x8020_Resp() {
    }

    public E_0x8020_Resp(int result, C_0x8020.Resp.ContentBean message, String errorCode, String errorMsg) {
        this.result = result;
        this.message = message;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public C_0x8020.Resp.ContentBean getMessage() {
        return message;
    }

    public void setMessage(C_0x8020.Resp.ContentBean message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
