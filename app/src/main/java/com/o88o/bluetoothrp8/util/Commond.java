package com.o88o.bluetoothrp8.util;

/**
 * Created by SEELE on 2018/1/16.
 */

public interface Commond {
    String HEX_COMMOND_START = "55";
    String HEX_COMMOND_TYPE_GET = "01"; // 向蓝牙设备发出请求
    String HEX_COMMOND_TYPE_NOTIFICATION = "00";//Notification 设备蓝牙主动推送报文类型
    String HEX_COMMOND_TYPE_RESPONSE = "02";	//Response	蓝牙设备返回的报文请求

    String HEX_COMMOND_DATA_LENGTH = "02";//此协议命令data 长度都是2个字节

    String HEX_RESPONSE_0000 = "0000";//	通用错误	成功。
    String HEX_RESPONSE_00F1 = "00F1";//		未知error类型。
    String HEX_RESPONSE_00F2 = "00F2";//		不支持该请求。
    String HEX_RESPONSE_00F3 = "00F3";//		请求格式错误。
    String HEX_RESPONSE_00F4 = "00F4";//		参数错误
    String HEX_RESPONSE_00F5 = "00F5";//		响应超时

    String HEX_RESPONSE_00E1 = "00E1";//	控制继电器	打开继电器失败
    String HEX_RESPONSE_00E2 = "00E2";//		处理超时
    String HEX_RESPONSE_00E3 = "00E3";//		查询开关状态错误

    String HEX_REQUEST_DEVICE1 = "01";//	开关一（按钮1）
    String HEX_REQUEST_DEVICE2 = "05";//	开关二（按钮2）
    String HEX_REQUEST_DEVICE3 = "02";//	开关三（按钮B）
    String HEX_REQUEST_DEVICE4 = "04";//	开关四（按钮S）
    String HEX_REQUEST_DEVICE5 = "03";//	开关五（按钮M）
    String HEX_REQUEST_DEVICE6 = "00";//	开关六（电源按钮）

    String HEX_REQUEST_FIRST = HEX_COMMOND_START + HEX_COMMOND_TYPE_GET + HEX_COMMOND_DATA_LENGTH + "F5F5";
    String HEX_REQUEST_VERSION = "55AA021600";
    String HEX_REQUEST_FUNC_DATA = "00";
    String HEX_REQUEST_FUNC_OPEN = "00";//	继电器闭合
    String HEX_REQUEST_FUNC_CLOSE = "00";//	继电器断开
    String HEX_REQUEST_FUNC_REVERSE = "02";//	继电器状态取反。


    java.lang.String HEX_COMMOND_NOTIFY_START = HEX_COMMOND_START + HEX_COMMOND_TYPE_NOTIFICATION + HEX_COMMOND_DATA_LENGTH;
    int LOW_BIT_1 = 2;
    int LOW_BIT_2 = 6;
    int LOW_BIT_3 = 3;
    int LOW_BIT_4 = 5;
    int LOW_BIT_5 = 4;
    int LOW_BIT_6 = 1;


    int WIFI_LOW_BIT_8 = 13;
    int BT_LOW_BIT_8 = 12;
    java.lang.String HEX_COMMOND_RESPONSE_START = HEX_COMMOND_START + HEX_COMMOND_TYPE_RESPONSE + HEX_COMMOND_DATA_LENGTH;;
}
