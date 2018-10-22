package com.o88o.bluetoothrp8.util;

import android.content.Context;

import com.clj.fastble.utils.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SEELE on 2018/1/16.
 */

public class BaseCommondUtils {
    private static final StringBuffer sb = new StringBuffer();
    public static int radix = 16;
    public boolean isAuth = false;

    public static String crc8(String hexCommond) {
        int crc8 =  FindCRC(HexUtil.hexStringToBytes(hexCommond));
        return HexUtil.numToHex8(crc8).toUpperCase();
    }
    public static int FindCRC(byte[] data){
        int CRC=0;
        int genPoly = 0x8C;
        for(int i=0;i<data.length; i++){
            CRC ^= data[i];
            CRC &= 0xff;//保证CRC余码输出为1字节。
            for(int j=0;j<8;j++){
                if((CRC & 0x01) != 0){
                    CRC = (CRC >> 1) ^ genPoly;
                    CRC &= 0xff;//保证CRC余码输出为1字节。
                }else{
                    CRC >>= 1;
                    CRC &= 0xff;//保证CRC余码输出为1字节。
                }
            }
        }
        CRC &= 0xff;//保证CRC余码输出为1字节。
        return CRC;
    }
    protected   Map resolveResponseCommond(byte[]  s) {
        return new HashMap();
    }
    public static String resolveVersion(byte[] notifyByte) {
        String notifyHex = HexUtil.formatHexString(notifyByte);
        notifyHex = sb.toString() + notifyHex;
        //BR05RT11 -> 55AA +10+ 16+000500020406074252303552543131+crc8
        if (!notifyHex.toUpperCase().startsWith("55AA1016")) {
            return "";
        }
        int len = needCommondLength(notifyHex);
        if(notifyHex.length() < len){
            sb.append(notifyHex);
            return "";
        }else{
            notifyHex.substring(0,len);
            sb.setLength(0);
        }
        if (!crc8(notifyHex.substring(0, notifyHex.length() - 2)).equals(notifyHex.substring(notifyHex.length() - 2).toUpperCase())) {
            return "";
        }

        sb.setLength(0);
        //数据体结构如下：
        /*0x00：预留。
        0x05：表示版本号由5段组成。
        0x00：第1段起始位置。
        0x02：第2段起始位置。
        0x04：第3段起始位置。
        0x06：第4段起始位置。
        0x07：第5段起始位置。
        “BR08RT11”:字符对应的ASCII十六进制编码*/


        return switchChar(notifyHex.substring(8,notifyHex.length() - 2));
    }

    public static int needCommondLength(String notifyHex) {
        //BR05RT11 -> 55AA +10+ 16+000500020406074252303552543131+crc8
        //数据体结构如下：
        /*0x00：预留。
        0x05：表示版本号由5段组成。
        0x00：第1段起始位置。
        0x02：第2段起始位置。
        0x04：第3段起始位置。
        0x06：第4段起始位置。
        0x07：第5段起始位置。
        “BR08RT11”:字符对应的ASCII十六进制编码*/

        Integer len = Integer.parseInt(notifyHex.substring(4,6),radix);
        return len * 2 + 8;
    }

    /**
     * 数据体结构如下：
     * 0x00：预留。
     0x05：表示版本号由5段组成。
     0x00：第1段起始位置。
     0x02：第2段起始位置。
     0x04：第3段起始位置。
     0x06：第4段起始位置。
     0x07：第5段起始位置。
     “BR08RT11”:字符对应的ASCII十六进制编码
     *
     * @param notifyHex
     */
    public static String switchChar(String notifyHex) {
        Integer segment = Integer.parseInt(notifyHex.substring(2,4),radix);
        if(segment < 1){
            return "";
        }
        StringBuffer segmentStr = new StringBuffer();
        int start = 0,end = 0;
        String index = "";
        for (int i = 0;i < segment - 1; i++){
            start = Integer.parseInt(notifyHex.substring(4 + 2 * i,4 + 2 * i + 2),radix);
            end = Integer.parseInt(notifyHex.substring(4 + 2 * i + 2,4 + 2 * i + 4),radix);
            index = stringHexToString(notifyHex.substring(4 + segment * 2 + start * 2, 4 + segment * 2 + end*2));
            segmentStr.append(index);
        }
        start = Integer.parseInt(notifyHex.substring(4 + 2 * segment -2 ,4 + 2 * segment),radix);
        index = stringHexToString(notifyHex.substring(4 + segment * 2 + start*2));
        segmentStr.append(index);

        return segmentStr.toString();
    }

    /**
     * “BR08RT11”:字符对应的ASCII十六进制编码
     *
     * @param stringHex
     * @return
     */
    private static String stringHexToString(String stringHex) {
        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < stringHex.length() ; i+=2) {
            sbu.append((char)Integer.parseInt(stringHex.substring(i ,i  + 2),radix));
        }
        return sbu.toString();
    }
    public int resolveAuth(Context context, byte[] notifyByte) {
        String notifyHex = HexUtil.formatHexString(notifyByte);
        if(check2(notifyHex)){
            if (!notifyHex.substring(6, 8).toUpperCase().equals(BaseCommond.HEX_COMMOND_TYPE_NOTIFICATION_18)) {
                return -1;
            }
            //0:连接成功；1：UID比对不成功；2：连接超时
            if("00".equals(notifyHex.substring(8,10))){
                // auth success
                isAuth = true;
                String dt = notifyHex.substring(10, 12);
                String uid = notifyHex.substring(12, notifyHex.length() - 2);
                Rp8DeviceUtils.putRpXStringValue(context,dt,uid);
                return 1;
            }else if("01".equals(notifyHex.substring(8,10))){
                // 01  auth fail
                isAuth = false;
                return 0;
            }else {
                // 02  auth timeout
                isAuth = false;
                return 2;
            }
        }
        return -1;
    }
    public String resolvePassthrough( byte[] notifyByte) {
        String notifyHex = HexUtil.formatHexString(notifyByte);
        if(!check2(notifyHex)) {
            return "";
        }
        if (!notifyHex.substring(6, 8).toUpperCase().equals(BaseCommond.HEX_COMMOND_TYPE_NOTIFICATION_19)) {
            return "";
        }

        return notifyHex.substring(8, notifyHex.length()-2);
    }
    public Map resolve(byte[] notifyByte) {
        String notify = HexUtil.formatHexString(notifyByte);
        if (check(notify)) {

            if (checkResponseCommond(notify)) {
                return resolveResponseCommond(notifyByte);
            }
            if (checkNotfiy(notify)) {
                return resolveNotify(notifyByte);
            }
        }
        return new HashMap();
    }
    protected   Map resolveNotify(byte[] notifyByte) {
        return new HashMap();
    }

    protected boolean checkResponseCommond(String notify) {
        return false;
    }
    protected boolean check2(String notify) {

        if (null == notify || notify.isEmpty()) {
            return false;
        }
        if (!notify.toUpperCase().startsWith(Rp86MCommond.HEX_COMMOND_START) || notify.length() <10) {
            return false;
        }
        int needCommondLength = needCommondLength(notify);
        if (notify.length() < needCommondLength) {
            return false;
        } else {
            notify = notify.substring(0, needCommondLength);
        }
        if (!crc8(notify.substring(0, notify.length() - 2)).equals(notify.substring(notify.length() - 2).toUpperCase())) {
            return false;
        }
        return true;
    }
    protected  boolean check(String notify) {
        return false;
    }
    protected boolean checkNotfiy(String notify) {
        return false;
    }

    public static String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }

    public static void main(String[] args){
        /**
         * 数据体结构如下：
         * 0x00：预留。
         0x05：表示版本号由5段组成。
         0x00：第1段起始位置。
         0x02：第2段起始位置。
         0x04：第3段起始位置。
         0x06：第4段起始位置。
         0x07：第5段起始位置。
         “BR08RT11”:字符对应的ASCII十六进制编码
         *
         * @param notifyHex
         */
        String versionHex = convertStringToHex("BR08RT11");//rp8
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252303852543131
        System.out.println(crc8("55AA1016"+"000500020406074252303852543131"));//79
        //55AA101600050002040607425230385254313179

        versionHex = convertStringToHex("BR05RT11");//rp5
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252303552543131
        System.out.println(crc8("55AA1016"+"000500020406074252303552543131"));//95

        versionHex = convertStringToHex("BR08GT11");//rp8gt
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252303847543131
        System.out.println(crc8("55AA1016"+"000500020406074252303847543131"));//C0

        versionHex = convertStringToHex("BR08TR11");//rp8oemr
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252303854523131
        System.out.println(crc8("55AA1016"+"000500020406074252303854523131"));//A1

        versionHex = convertStringToHex("BR08XP11");//rp8oemrxp
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252303858503131
        System.out.println(crc8("55AA1016"+"000500020406074252303858503131"));//FC
        //55AA1016000500020406074252303858503131FC

        versionHex = convertStringToHex("BR16RT21");//rp8-6m
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252313652543231
        System.out.println(crc8("55AA1016"+"000500020406074252313652543231"));//B9
        //55AA1016000500020406074252313652543231B9

        versionHex = convertStringToHex("BR16IR21");//rp8-6m
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252313649523231
        System.out.println(crc8("55AA1016"+"00050002040607" + versionHex));//C4
        //55AA1016000500020406074252313649523231C4

        versionHex = convertStringToHex("BR16PU21");//rp8-6m
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252313650553231
        System.out.println(crc8("55AA1016"+"00050002040607" + versionHex));//15
        //55AA101600050002040607425231365055323115

        versionHex = convertStringToHex("BR16WP21");//rp8-6m
        System.out.println(versionHex);
        System.out.println(switchChar("00050002040607" + versionHex));//000500020406074252313657503231
        System.out.println(crc8("55AA1016"+"00050002040607" + versionHex));//15
        //55AA1016000500020406074252313657503231A6
    }

}
