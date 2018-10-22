package cn.com.startai.kp8.util;

import java.util.HashMap;
import java.util.Map;

import cn.com.startai.mqsdk.R;

/**
 * Created by SEELE on 2018/1/16.
 */

public class Rp86MCommondUtils extends BaseCommondUtils {

    private static final StringBuffer sb = new StringBuffer();

    @Override
    protected Map resolveNotify(byte[] notifyByte) {
        Map commondMap = new HashMap();
        //data0
        //String btnStatus = notify.substring(notify.length()-4,notify.length()-2);
        byte data0 = notifyByte[notifyByte.length - 1 - 1];
        //String bit8 = HexUtil.getBit(btnStatus );
        int[] btnStatus8 = HexUtil.getBit(data0);

        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_0]) {
            commondMap.put(R.id.btn_on_off, 1);
        } else {
            commondMap.put(R.id.btn_on_off, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_1]) {
            commondMap.put(R.id.btn_z1, 1);
        } else {
            commondMap.put(R.id.btn_z1, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_2]) {
            commondMap.put(R.id.btn_z2, 1);
        } else {
            commondMap.put(R.id.btn_z2, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_3]) {
            commondMap.put(R.id.btn_z3, 1);
        } else {
            commondMap.put(R.id.btn_z3, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_4]) {
            commondMap.put(R.id.btn_z4, 1);
        } else {
            commondMap.put(R.id.btn_z4, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_5]) {
            commondMap.put(R.id.btn_z5, 1);
        } else {
            commondMap.put(R.id.btn_z5, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_6]) {
            commondMap.put(R.id.btn_z6, 1);
        } else {
            commondMap.put(R.id.btn_z6, 0);
        }
        if (1 == btnStatus8[8 - Rp86MCommond.LOW_BIT_7]) {
            commondMap.put(R.id.btn_z7, 1);
        } else {
            commondMap.put(R.id.btn_z7, 0);
        }
        //data1
        byte data1 = notifyByte[notifyByte.length - 1 - 1 - 1];
        //String bit8 = HexUtil.getBit(btnStatus );
        btnStatus8 = HexUtil.getBit(data1);

        if (1 == btnStatus8[8 + 8 - Rp86MCommond.LOW_BIT_8]) {
            commondMap.put(R.id.btn_z8, 1);
        } else {
            commondMap.put(R.id.btn_z8, 0);
        }

        if (1 == btnStatus8[8 + 8 - Rp86MCommond.BT_LOW_BIT_12]) {
            commondMap.put(R.id.layout_bt, 1);
        } else {
            commondMap.put(R.id.layout_bt, 0);
        }
        if (1 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_EXIST_LOW_BIT_15]
            && 1 == btnStatus8[8 + 8 - Rp86MCommond.WIFI_LOW_BIT_13]) {
            commondMap.put(R.id.layout_wifi, 1);
        } else {
            commondMap.put(R.id.layout_wifi, 0);
        }
        return commondMap;
    }

    public Map resolve(byte[] notifyByte) {
        String notify = HexUtil.formatHexString(notifyByte);
        if (check(notify)) {

            if (checkNotfiy(notify)) {
                return resolveNotify(notifyByte);
            }
        }
        // m1 - m6
        if (checkResponseCommond(notify)) {
            Map responseCommond = resolveResponseCommond(notifyByte, notify);
            sb.setLength(0);
            return responseCommond;
        }
        return new HashMap();
    }

    /*
    Group count，组的数量
        Member count，成员数量
            每个设备都有唯一的SID0
            SID1
            data见下表

            每个设备都有唯一的SID
            SID1
            data见下表

     */
    protected Map resolveResponseCommond(byte[] notifyByte, String notify) {
        notify = sb.toString() + notify;

        //去掉 帧头+长度+功能码 ，crc8
        String notifyHex = notify.substring(8, notify.length() - 2);

        //取第一个字节 -- 组的数量
        Integer segmentSize = Integer.parseInt(notifyHex.substring(0, 2), radix);
        if (segmentSize < 1) {
            return new HashMap();
        }
        int segmentByteSize = 0;
        int deviceBytesize = 3;
        Map<Integer, byte[]> segmentStr = new HashMap(segmentSize);
        int deviceSize = 0;
        for (int i = 0; i < segmentSize; i = i + 1) {
            //每组设备的数量
            deviceSize = Integer.parseInt(notifyHex.substring(2 + 2 * segmentByteSize, 2 + 2 * segmentByteSize + 2), radix);
            //只有一个设备
            //deviceId = Integer.parseInt(notifyHex.substring( 2 + 2*segmentByteSize * i + 2, 2 + 2*segmentByteSize * i + 2 + 4),radix);
            byte[] bytes = HexUtil.hexStringToBytes(notifyHex.substring(2 + 2 * segmentByteSize + 2 + 4, 2 + 2 * segmentByteSize + 2 + 4 + 2));
            segmentStr.put(i + 1, bytes);
            segmentByteSize += deviceSize * 3 + 1;
        }

        Map commondMap = new HashMap();
        int[] btnStatus8;
        byte data1 = 0;
        for (int i = 0; i < segmentStr.size(); i++) {
            data1 = segmentStr.get(i + 1)[0];
            //String bit8 = HexUtil.getBit(btnStatus );
            btnStatus8 = HexUtil.getBit(data1);
            getCommandMap(i + 1, btnStatus8, commondMap);
        }
        return commondMap;
    }

    private void getCommandMap(int i, int[] btnStatus8, Map commondMap) {
        switch (i) {
            case 1:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m1, 0); //反向旋转
                    } else {
                        commondMap.put(R.id.btn_m1, 1); //正向旋转
                    }

                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m1, 1);
                    } else {
                        commondMap.put(R.id.run_m1, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m1, -1); //正向旋转
                }
                break;
            case 2:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m2, 0);
                    } else {
                        commondMap.put(R.id.btn_m2, 1);
                    }

                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m2, 1);
                    } else {
                        commondMap.put(R.id.run_m2, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m2, -1); //正向旋转
                }
                break;
            case 3:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m3, 0);
                    } else {
                        commondMap.put(R.id.btn_m3, 1);
                    }

                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m3, 1);
                    } else {
                        commondMap.put(R.id.run_m3, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m3, -1); //正向旋转
                }
                break;
            case 4:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m4, 0);
                    } else {
                        commondMap.put(R.id.btn_m4, 1);
                    }
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m4, 1);
                    } else {
                        commondMap.put(R.id.run_m4, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m4, -1); //正向旋转
                }

                break;
            case 5:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m5, 0);
                    } else {
                        commondMap.put(R.id.btn_m5, 1);
                    }
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m5, 1);
                    } else {
                        commondMap.put(R.id.run_m5, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m5, -1); //正向旋转
                }
                break;
            case 6:
                if(1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_ABLE_LOW_BIT]) {
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_DIRECT_LOW_BIT]) {
                        commondMap.put(R.id.btn_m6, 0);
                    } else {
                        commondMap.put(R.id.btn_m6, 1);
                    }
                    if (1 == btnStatus8[8 - Rp86MCommond.DEVICE_RUN_STATUS_LOW_BIT]) {
                        commondMap.put(R.id.run_m6, 1);
                    } else {
                        commondMap.put(R.id.run_m6, 0);
                    }
                }else{
                    commondMap.put(R.id.btn_m6, -1); //正向旋转
                }
                break;
        }
    }

    @Override
    protected boolean checkResponseCommond(String notify) {
        if (null == notify || notify.isEmpty()) {
            return false;
        }
        notify = sb.toString() + notify;
        if (!notify.toUpperCase().startsWith(Rp86MCommond.HEX_COMMOND_START ) || notify.length() <10) {
            return false;
        }
        if (!notify.substring(6, 8).toUpperCase().equals(Rp86MCommond.HEX_COMMOND_TYPE_NOTIFICATION_17)) {
            return false;
        }
        int needCommondLength = needCommondLength(notify);
        if (notify.length() < needCommondLength) {
            sb.append(notify);
            return false;
        } else {
            notify = notify.substring(0, needCommondLength);
        }
        if (!crc8(notify.substring(0, notify.length() - 2)).equals(notify.substring(notify.length() - 2).toUpperCase())) {
            sb.setLength(0);
            return false;
        }
        return true;
    }

    @Override
    protected boolean check(String notify) {

        if (null == notify || notify.isEmpty()) {
            return false;
        }
        if (!notify.toUpperCase().startsWith(Rp86MCommond.HEX_COMMOND_START)) {
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

    @Override
    protected boolean checkNotfiy(String notify) {

        if (!notify.substring(6, 8).toUpperCase().equals(Rp86MCommond.HEX_COMMOND_TYPE_NOTIFICATION)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(crc8("55AA0412000001"));  // 18
        System.out.println(crc8("55AA0412000002"));  // FA
        System.out.println(crc8("55AA0412000004"));  // 27
        System.out.println(crc8("55AA0412000008"));  // 84
        System.out.println(crc8("55AA0412000010"));  // DB
        System.out.println(crc8("55AA0412000020"));  // 65
        System.out.println(crc8("55AA04120001FF"));  // B7
        System.out.println(crc8("55AA04120003FF"));  // 26
        System.out.println(crc8("55AA04120005FF"));  // 8C
        System.out.println(crc8("55AA04120007FF"));  // 1D

        System.out.println(crc8("55AA041200FFFF"));  // F2

        System.out.println(crc8("55AA1A1706010000000100000001000000010000030100000301000003"));  // 08

        System.out.println(crc8("55AA1A1706010000110100001101000011010000110100001101000011"));  // 29
    }
}
