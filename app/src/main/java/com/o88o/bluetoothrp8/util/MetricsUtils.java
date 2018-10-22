package com.o88o.bluetoothrp8.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by judge on 2018/2/10.
 */

public class MetricsUtils {
    private static final int DEFLAUT_SIZE_DP = 360;
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int getWindowWidth(Context context){
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        return mScreenWidth;
    }
    public static int getWindowWidthDP(Context context){
        return px2dip(context,getWindowWidth(context));
    }
    public static int getWindowHeigh(Context context){
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        return mScreenHeigh;
    }


    public static boolean isNeedAdapter(Context context){
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        int mScreenWidth = dm.widthPixels;

        if(px2dip(context,mScreenWidth) > DEFLAUT_SIZE_DP && px2dip(context,mScreenHeigh) > DEFLAUT_SIZE_DP){
            return true;
        }
        return false;
    }
    public static void setBtnRadis(View btn, int radis) {
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = radis;
        relativeLayoutParams.height = radis;
        btn.setLayoutParams(relativeLayoutParams);
    }
    private MetricsUtils(){

    }

    public static void setTextSize( float scaleSize, View... btns) {
        for (View btn :btns) {
            if(btn instanceof Button) {
                ((Button)btn).setTextSize(TypedValue.COMPLEX_UNIT_PX, scaleSize);
            }
            if(btn instanceof TextView) {
                ((TextView)btn).setTextSize(TypedValue.COMPLEX_UNIT_PX,scaleSize);
            }
        }
    }

}
