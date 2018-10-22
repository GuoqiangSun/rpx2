package cn.com.startai.mqsdk.network;

import android.content.Context;

import cn.com.swain.baselib.sp.BaseSpTool;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/28 0028
 * desc :
 */
public class UserIDXml extends BaseSpTool {

    private static final String NAME = "userID";

    private UserIDXml(Context mCtx) {
        super(mCtx, NAME);
    }

    private static UserIDXml mUserIDXml;

    public static final UserIDXml getInstance(Context mCtx) {

        if (mUserIDXml == null) {
            synchronized (UserIDXml.class) {
                if (mUserIDXml == null) {
                    mUserIDXml = new UserIDXml(mCtx);
                }
            }
        }
        return mUserIDXml;
    }

    private String key = "ui";

    public void setUserID(String userID) {
        putString(key, userID);
    }

    private static final String DEFAULT_UID = "1234567890ABCDEF1234567890";

    public String getUserID() {
        String string = getString(key, "");
        if (string == null || "".equals(string)) {

            synchronized (UserIDXml.this) {
                string = getRandomUserID();

                if (string == null || "".equals(string)) {
                    int random = (int) ((Math.random() * 9 + 1) * 100000);
                    string = DEFAULT_UID + String.valueOf(random);
                    setRandomUserID(string);
                }

            }

        }
        return string;
    }

    private String rkey = "rui";

    private void setRandomUserID(String userID) {
        putString(rkey, userID);
    }

    public String getRandomUserID() {
        return getString(rkey, "");
    }

}
