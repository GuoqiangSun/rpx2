package cn.com.startai.kp8.util;

/**
 * Created by SEELE on 2018/1/16.
 */

public interface Rp86MCommond extends BaseCommond{
    String HEX_COMMOND_START = "55AA";
    String HEX_COMMOND_TYPE_RESERVE = "44";
    String HEX_COMMOND_TYPE_STARTUP = "45";
    String HEX_COMMOND_TYPE_SHUTDOWN = "46";
    String HEX_COMMOND_TYPE_GET = "13"; // 向蓝牙设备发出请求
    String HEX_COMMOND_TYPE_NOTIFICATION = "12";//Notification 设备蓝牙主动推送报文类型
    String HEX_COMMOND_TYPE_NOTIFICATION_17 = "17";
    //String HEX_COMMOND_TYPE_NOTIFICATION_14 = "14";	//Response	蓝牙设备返回的报文请求

    String HEX_COMMOND_DATA_LENGTH = "03";//此协议get / notification 命令data 长度都是3个字节

    String HEX_RESPONSE_0000 = "0000";//	通用错误	成功。
    String HEX_RESPONSE_00F1 = "00F1";//		未知error类型。
    String HEX_RESPONSE_00F2 = "00F2";//		不支持该请求。
    String HEX_RESPONSE_00F3 = "00F3";//		请求格式错误。
    String HEX_RESPONSE_00F4 = "00F4";//		参数错误
    String HEX_RESPONSE_00F5 = "00F5";//		响应超时

    String HEX_RESPONSE_00E1 = "00E1";//	控制继电器	打开继电器失败
    String HEX_RESPONSE_00E2 = "00E2";//		处理超时
    String HEX_RESPONSE_00E3 = "00E3";//		查询开关状态错误

    String HEX_REQUEST_DEVICE0 = "00";//power
    //z1 - z8
    String HEX_REQUEST_DEVICE1 = "01";//
    String HEX_REQUEST_DEVICE2 = "02";//
    String HEX_REQUEST_DEVICE3 = "03";//
    String HEX_REQUEST_DEVICE4 = "04";//
    String HEX_REQUEST_DEVICE5 = "05";//
    String HEX_REQUEST_DEVICE6 = "06";//
    String HEX_REQUEST_DEVICE7 = "07";//
    String HEX_REQUEST_DEVICE8 = "08";//
    //m1 - m6
    String HEX_REQUEST_DEVICE9 = "00";//
    String HEX_REQUEST_DEVICE10 = "01";//
    String HEX_REQUEST_DEVICE11 = "02";//
    String HEX_REQUEST_DEVICE12 = "03";//
    String HEX_REQUEST_DEVICE13 = "04";//
    String HEX_REQUEST_DEVICE14 = "05";//

    String HEX_REQUEST_FIRST = HEX_COMMOND_START + "01" + "11" ;
    String HEX_REQUEST_VERSION = "55AA021600";

    String HEX_REQUEST_FUNC_DATA = "00";// 初始数据
    String HEX_REQUEST_FUNC_OPEN_1 = "01";//	继电器闭合
    String HEX_REQUEST_FUNC_CLOSE = "00";//	继电器断开


    String HEX_COMMOND_NOTIFY_START = HEX_COMMOND_START + HEX_COMMOND_DATA_LENGTH + HEX_COMMOND_TYPE_NOTIFICATION;
    //z1 - z8 ,power
    int LOW_BIT_0 = 1;
    int LOW_BIT_1 = 2;
    int LOW_BIT_2 = 3;
    int LOW_BIT_3 = 4;
    int LOW_BIT_4 = 5;
    int LOW_BIT_5 = 6;
    int LOW_BIT_6 = 7;
    int LOW_BIT_7 = 8;
    int LOW_BIT_8 = 9;

    int WIFI_LOW_BIT_13 = 13;
    int BT_LOW_BIT_12 = 12;

    //Wifi是否存在	0：不存在，1：存在
    int WIFI_EXIST_LOW_BIT_15 = 15;
    // Wifi配网	0：没有正在配网，1：正在配网
    int WIFI_DISTRIBUTION_LOW_BIT_14 = 14;

    //String HEX_COMMOND_RESPONSE_START = HEX_COMMOND_START + HEX_COMMOND_DATA_LENGTH + HEX_COMMOND_TYPE_RESPONSE;
    int DEVICE_RUN_DIRECT_LOW_BIT = 1;
    int DEVICE_RUN_ABLE_LOW_BIT = 2;
    int DEVICE_RUN_STATUS_LOW_BIT = 5;
}
