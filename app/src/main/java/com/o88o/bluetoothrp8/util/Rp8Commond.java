package com.o88o.bluetoothrp8.util;

/**
 * Created by SEELE on 2018/1/16.
 */

public interface Rp8Commond {
    String HEX_COMMOND_START = "55AA";
    String HEX_COMMOND_TYPE_GET = "13"; // 向蓝牙设备发出请求
    String HEX_COMMOND_TYPE_NOTIFICATION = "12";//Notification 设备蓝牙主动推送报文类型
    //String HEX_COMMOND_TYPE_RESPONSE = "14";	//Response	蓝牙设备返回的报文请求

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

    String HEX_REQUEST_DEVICE1 = "00";//	开关一（按钮1）
    String HEX_REQUEST_DEVICE2 = "01";//	开关二（按钮2）
    String HEX_REQUEST_DEVICE3 = "02";//	开关三（按钮B）
    String HEX_REQUEST_DEVICE4 = "03";//	开关四（按钮S）
    String HEX_REQUEST_DEVICE5 = "04";//	开关五（按钮M）
    String HEX_REQUEST_DEVICE6 = "05";//	开关六（遥控器板电源按钮）
    String HEX_REQUEST_DEVICE7 = "06";//	开关六（按钮3）
    String HEX_REQUEST_DEVICE8 = "07";//	开关六（继电器板按钮nav）
    String HEX_REQUEST_DEVICE9 = "08";//	开关六（继电器板按钮anchor）
    String HEX_REQUEST_DEVICE10 = "09";//开关六（遥控器板按钮nav）

    String HEX_REQUEST_FIRST = HEX_COMMOND_START + "01" + "11" ;
    String HEX_REQUEST_VERSION = "55AA021600";
    String HEX_REQUEST_FUNC_DATA = "00";//	继电器闭合
    String HEX_REQUEST_FUNC_OPEN = "01";//	继电器闭合
    String HEX_REQUEST_FUNC_CLOSE = "00";//	继电器断开
    String HEX_REQUEST_FUNC_REVERSE = "02";//	继电器状态取反。


    String HEX_COMMOND_NOTIFY_START = HEX_COMMOND_START + HEX_COMMOND_DATA_LENGTH + HEX_COMMOND_TYPE_NOTIFICATION;
    int LOW_BIT_1 = 1;
    int LOW_BIT_2 = 2;
    int LOW_BIT_3 = 3;
    int LOW_BIT_4 = 4;
    int LOW_BIT_5 = 5;
    int LOW_BIT_6 = 6;
    int LOW_BIT_7 = 7;
    int LOW_BIT_8 = 8;
    int LOW_BIT_9 = 9;

    int WIFI_LOW_BIT_8 = 13;
    int BT_LOW_BIT_8 = 12;
    //String HEX_COMMOND_RESPONSE_START = HEX_COMMOND_START + HEX_COMMOND_DATA_LENGTH + HEX_COMMOND_TYPE_RESPONSE;
}
