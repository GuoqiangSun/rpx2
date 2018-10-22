package com.o88o.bluetoothrp8.util;

import com.clj.fastble.utils.HexUtil;
import com.o88o.bluetoothrp8.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SEELE on 2018/1/16.
 */

public class Rp8OEMRCommondUtils extends BaseCommondUtils{
    @Override
    protected Map resolveNotify(byte[] notifyByte) {
        Map commondMap = new HashMap();
        //data0
        //String btnStatus = notify.substring(notify.length()-4,notify.length()-2);
        byte data0 = notifyByte[notifyByte.length -1 -1];
        //String bit8 = HexUtil.getBit(btnStatus );
        int[] btnStatus8 = HexUtil.getBit(data0 );
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_1]) {
            commondMap.put(R.id.btn_on_off,1);
        }else
        {
            commondMap.put(R.id.btn_on_off,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_2]) {
            commondMap.put(R.id.panel_btn_nav,1);
        }else
        {
            commondMap.put(R.id.panel_btn_nav,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_3]) {
            commondMap.put(R.id.panel_btn_anchor,1);
        }else
        {
            commondMap.put(R.id.panel_btn_anchor,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_4]) {
            commondMap.put(R.id.btn_b,1);
        }else
        {
            commondMap.put(R.id.btn_b,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_5]) {
            commondMap.put(R.id.btn_m,1);
        }else
        {
            commondMap.put(R.id.btn_m,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_6]) {
            commondMap.put(R.id.btn_s,1);
        }else
        {
            commondMap.put(R.id.btn_s,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_7]) {
            commondMap.put(R.id.btn_1,1);
        }else
        {
            commondMap.put(R.id.btn_1,0);
        }
        if (1 == btnStatus8[8 - Rp8OEMRCommond.LOW_BIT_8]) {
            commondMap.put(R.id.btn_2,1);
        }else
        {
            commondMap.put(R.id.btn_2,0);
        }

        //data1
        byte data1 = notifyByte[notifyByte.length -1 -1 -1];
        //String bit8 = HexUtil.getBit(btnStatus );
        btnStatus8 = HexUtil.getBit(data1 );
        if (1 == btnStatus8[8 + 8 - Rp8OEMRCommond.LOW_BIT_9]) {
            commondMap.put(R.id.btn_3,1);
        }else
        {
            commondMap.put(R.id.btn_3,0);
        }

        if (1 == btnStatus8[8 + 8 - Rp8OEMRCommond.LOW_BIT_10]) {
            commondMap.put(R.id.blink_live2,1);
        }else
        {
            commondMap.put(R.id.blink_live2,0);
        }
        if (1 == btnStatus8[8 + 8 - Rp8OEMRCommond.LOW_BIT_11]) {
            commondMap.put(R.id.blink_live,1);
        }else
        {
            commondMap.put(R.id.blink_live,0);
        }
        if (1 == btnStatus8[8 + 8 - Rp86MCommond.BT_LOW_BIT_12]) {
            commondMap.put(R.id.layout_bt, 1);
        } else {
            commondMap.put(R.id.layout_bt, 0);
        }
        if (1 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_EXIST_LOW_BIT_15]) {
            commondMap.put(R.id.layout_wifi, 1);
        } else {
            commondMap.put(R.id.layout_wifi, 0);
        }
        if (1 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_EXIST_LOW_BIT_15]){
            if (1 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_LOW_BIT_13]) {
                commondMap.put(R.id.blink_wifi_rate, 0);
            } else{
                if(0 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_DISTRIBUTION_LOW_BIT_14]) {
                    commondMap.put(R.id.blink_wifi_rate, 1);
                }else{
                    commondMap.put(R.id.blink_wifi_rate, 2);
                }
            }
        }
        return commondMap;
    }

    /*private static boolean checkResponseCommond(String notify) {

        if(!notify.startsWith(Rp8OEMRCommond.HEX_COMMOND_RESPONSE_START)){
            return false;
        }
        return true;
    }*/

    @Override
    protected boolean check(String notify) {

        if (null == notify || notify.isEmpty() || notify.length() != 14) {
            return false;
        }
        if (!notify.toUpperCase().startsWith(Rp8OEMRCommond.HEX_COMMOND_START)) {
            return false;
        }
        if (!crc8(notify.substring(0, notify.length() - 2)).equals(notify.substring(notify.length() - 2).toUpperCase())) {
            return false;
        }
        return true;
    }
    @Override
    protected boolean checkNotfiy(String notify) {

        if(!notify.toUpperCase().startsWith(Rp8OEMRCommond.HEX_COMMOND_NOTIFY_START)){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(crc8("55AA03120001"));  // 98
        System.out.println(crc8("55AA03120002"));  // 7A
        System.out.println(crc8("55AA03120004"));  // A7
        System.out.println(crc8("55AA03120008"));  // 04
        System.out.println(crc8("55AA03120010"));  // 5B
        System.out.println(crc8("55AA03120020"));  // E5
        System.out.println(crc8("55AA031201FF"));  // 37
        System.out.println(crc8("55AA031203FF"));  // A6
        System.out.println(crc8("55AA031205FF"));  // 0C
        System.out.println(crc8("55AA031207FF"));  // 9D 不存在  长暗

        System.out.println(crc8("55AA03124FFF"));  // 70 //存在 慢闪
        System.out.println(crc8("55AA03126FFF"));  // B1 // 存在 快闪
        System.out.println(crc8("55AA03127FFF"));  // 5D // 存在 常亮
    }
}
