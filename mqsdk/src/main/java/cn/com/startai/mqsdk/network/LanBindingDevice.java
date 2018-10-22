package cn.com.startai.mqsdk.network;

import cn.com.swain.baselib.util.Bit;

/**
 * author: Guoqiang_Sun
 * date : 2018/7/10 0010
 * desc :
 */

public class LanBindingDevice {


    private String mid; // 我的id

    private String oid; // 对方的id

    private String omac; // 对方的mac

    private boolean isAdmin;// 是否是管理员

    private String cpuInfo;

    private int isBindResult;


    @Override
    public String toString() {
        return " LanBindingDevice{" +
                ", mid='" + mid + '\'' +
                ", oid='" + oid + '\'' +
                ", omac='" + omac + '\'' +
                ", isAdmin=" + isAdmin +
                ", cpuInfo='" + cpuInfo + '\'' +
                ", isBindResult='" + Integer.toBinaryString(isBindResult & 0xFF) + '\'' +
                '}';
    }


    public LanBindingDevice() {
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOmac() {
        return this.omac;
    }

    public void setOmac(String omac) {
        this.omac = omac;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public String getCpuInfo() {
        return this.cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public boolean isBind() {
        return Bit.isOne(isBindResult, 0);
    }

    public void setModel(int isBindResult) {
        this.isBindResult = isBindResult;
    }
}
