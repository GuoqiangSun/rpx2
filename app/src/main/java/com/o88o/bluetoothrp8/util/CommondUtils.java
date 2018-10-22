package com.o88o.bluetoothrp8.util;

import com.clj.fastble.utils.HexUtil;
import com.o88o.bluetoothrp8.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SEELE on 2018/1/16.
 */

public class CommondUtils extends BaseCommondUtils{

    @Override
    protected Map resolveNotify(byte[] notifyByte) {
        Map commondMap = new HashMap();

        //String btnStatus = notify.substring(notify.length()-4,notify.length()-2);
        byte btnStatus = notifyByte[notifyByte.length -1 -1];
        //String bit8 = HexUtil.getBit(btnStatus );
        int[] btnStatus8 = HexUtil.getBit(btnStatus );
        if (1 == btnStatus8[8 - Commond.LOW_BIT_1]) {
            commondMap.put(R.id.btn_1,true);
        }else
        {
            commondMap.put(R.id.btn_1,false);
        }
        if (1 == btnStatus8[8 - Commond.LOW_BIT_2]) {
            commondMap.put(R.id.btn_2,true);
        }else
        {
            commondMap.put(R.id.btn_2,false);
        }
        if (1 == btnStatus8[8 - Commond.LOW_BIT_3]) {
            commondMap.put(R.id.btn_b,true);
        }else
        {
            commondMap.put(R.id.btn_b,false);
        }
        if (1 == btnStatus8[8 - Commond.LOW_BIT_4]) {
            commondMap.put(R.id.btn_s,true);
        }else
        {
            commondMap.put(R.id.btn_s,false);
        }
        if (1 == btnStatus8[8 - Commond.LOW_BIT_5]) {
            commondMap.put(R.id.btn_m,true);
        }else
        {
            commondMap.put(R.id.btn_m,false);
        }
        if (1 == btnStatus8[8 - Commond.LOW_BIT_6]) {
            commondMap.put(R.id.btn_on_off,true);
        }else
        {
            commondMap.put(R.id.btn_on_off,false);
        }
        /*//data1
        byte data1 = notifyByte[notifyByte.length -1 -1 -1];
        //String bit8 = HexUtil.getBit(btnStatus );
        btnStatus8 = HexUtil.getBit(data1 );
        if (1 == btnStatus8[8 + 8 - Rp86MCommond.BT_LOW_BIT_12]) {
            commondMap.put(R.id.layout_bt, true);
        } else {
            commondMap.put(R.id.layout_bt, false);
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
        } */
        return commondMap;
    }

    @Override
    protected boolean checkResponseCommond(String notify) {

        if(!notify.startsWith(Commond.HEX_COMMOND_RESPONSE_START)){
            return false;
        }
        return true;
    }

    @Override
    protected boolean check(String notify) {

        if (null == notify || notify.isEmpty() || notify.length() != 12) {
            return false;
        }
        if (!notify.toUpperCase().startsWith(Commond.HEX_COMMOND_START)) {
            return false;
        }
        if (!crc8(notify.toUpperCase().substring(0, notify.length() - 2)).equals(notify.toUpperCase().substring(notify.length() - 2))) {
            return false;
        }
        return true;
    }
    @Override
    protected boolean checkNotfiy(String notify) {

        if(!notify.toUpperCase().startsWith(Commond.HEX_COMMOND_NOTIFY_START)){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(crc8("5500020001"));  //56
        System.out.println(crc8("5500020003"));  //b4
        System.out.println(crc8("5500020004"));  //69
        System.out.println(crc8("5500020008"));  //ca
        System.out.println(crc8("5500020010"));  //95
        System.out.println(crc8("5500020020"));  //2b
        System.out.println(crc8("550102F5F5"));  // 9F
    }
}
