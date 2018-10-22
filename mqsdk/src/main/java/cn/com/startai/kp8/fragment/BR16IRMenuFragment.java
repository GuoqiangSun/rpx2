package cn.com.startai.kp8.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

import cn.com.startai.kp8.activity.BR16IRMasterActivity;
import cn.com.startai.kp8.util.MetricsUtils;
import cn.com.startai.kp8.util.Rp86MCommond;
import cn.com.startai.kp8.util.Rp86MCommondUtils;
import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.widget.LongClickButton;
import cn.com.startai.mqttsdk.StartAI;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BR16IRMenuFragment extends Fragment {

    public  static final String FW_ID = "BR16IR21";
    private ColorStateList color_gray;
    private ColorStateList color_red;
    private ColorStateList color_green;
    private ColorStateList color_white;
    private View rootView;
    private RelativeLayout btnLayout;

    private TextView panel_tv_z1,panel_tv_z2,panel_tv_z3,panel_tv_z4,panel_tv_z5,panel_tv_z6,panel_tv_z7,panel_tv_z8,panel_tv_power;
    private TextView panel_tv_m1,panel_tv_m2,panel_tv_m3,panel_tv_m4,panel_tv_m5,panel_tv_m6;
    private LongClickButton panel_btn_m1,panel_btn_m2,panel_btn_m3,panel_btn_m4,panel_btn_m5,panel_btn_m6;
    private LongClickButton btn_m1,btn_m2,btn_m3,btn_m4,btn_m5,btn_m6;
    private LongClickButton btn_m1_1,btn_m2_1,btn_m3_1,btn_m4_1,btn_m5_1,btn_m6_1;
    private Button btn_z1,panel_btn_z1;
    private Button btn_z2,panel_btn_z2;
    private Button btn_z4,panel_btn_z4;
    private Button btn_z5,panel_btn_z5;
    private Button btn_on_off,panel_btn_power;
    private Button btn_z6,panel_btn_z6;
    private Button btn_z7,panel_btn_z7;
    private Button btn_z8,panel_btn_z8;
    private Button btn_z3,panel_btn_z3;
    private Button panel_btn_anchor;
    private TextView btn_brand;

    private Button rp8_gear;
    private ImageView layout_logo;


    private Integer boolBtn_z1 = 0,boolBtn_m1 = 0,boolRun_m1 = 0;
    private Integer boolBtn_z2 = 0,boolBtn_m2 = 0,boolRun_m2 = 0;
    private Integer boolBtn_z3 = 0,boolBtn_m3 = 0,boolRun_m3 = 0;
    private Integer boolBtn_z4 = 0,boolBtn_m4 = 0,boolRun_m4 = 0;
    private Integer boolBtn_z5 = 0,boolBtn_m5 = 0,boolRun_m5 = 0;
    private Integer boolBtn_z6 = 0,boolBtn_m6 = 0,boolRun_m6 = 0;
    private Integer boolBtn_z7 = 0;
    private Integer boolBtn_z8 = 0;
    private Integer boolBtn_on_off = 0;
    private Integer blink_wifi_rate = 0;


    private final int OPEN_BREATH_INTERVAL_TIME = 200; //设置呼吸灯时间间隔
    private final int CLOSE_BREATH_INTERVAL_TIME = 200; //设置呼吸灯时间间隔
    private AlphaAnimation animationFadeIn;
    private AlphaAnimation animationFadeOut;
    private boolean powerChange = false;
    private ImageView layout_wifi;
    private ImageView layout_bt;

    MyLongClickUpListener longClickUpListener_9;
    MyLongClickUpListener longClickUpListener_10;
    MyLongClickUpListener longClickUpListener_11;
    MyLongClickUpListener longClickUpListener_12;
    MyLongClickUpListener longClickUpListener_13;
    MyLongClickUpListener longClickUpListener_14;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_br16ir_menu, null, false);
        bindView();
        return rootView;
    }
    private void bindView(){

        color_gray = AppCompatResources.getColorStateList(getContext(), R.color.color_gray);
        color_red = AppCompatResources.getColorStateList(getContext(), R.color.color_red);
        color_green = AppCompatResources.getColorStateList(getContext(), R.color.color_green);
        color_white = AppCompatResources.getColorStateList(getContext(), R.color.color_white);
        btnLayout = (RelativeLayout)rootView.findViewById(R.id.btn_layout);

        btn_on_off = (Button)rootView.findViewById(R.id.btn_on_off);
        btn_z1 = (Button)rootView.findViewById(R.id.btn_z1);
        btn_z2 = (Button)rootView.findViewById(R.id.btn_z2);
        btn_z6 = (Button)rootView.findViewById(R.id.btn_z6);
        btn_z5 = (Button)rootView.findViewById(R.id.btn_z5);
        btn_z4 = (Button)rootView.findViewById(R.id.btn_z4);
        btn_z3 = (Button)rootView.findViewById(R.id.btn_z3);
        btn_z7 = (Button)rootView.findViewById(R.id.btn_z7);
        btn_z8 = (Button)rootView.findViewById(R.id.btn_z8);
        btn_brand = (TextView)rootView.findViewById(R.id.btn_brand);
        btn_m1 = (LongClickButton)rootView.findViewById(R.id.btn_m1);
        btn_m2 = (LongClickButton)rootView.findViewById(R.id.btn_m2);
        btn_m6 = (LongClickButton)rootView.findViewById(R.id.btn_m6);
        btn_m5 = (LongClickButton)rootView.findViewById(R.id.btn_m5);
        btn_m4 = (LongClickButton)rootView.findViewById(R.id.btn_m4);
        btn_m3 = (LongClickButton)rootView.findViewById(R.id.btn_m3);

        btn_m1_1 = (LongClickButton)rootView.findViewById(R.id.btn_m1_1);
        btn_m2_1 = (LongClickButton)rootView.findViewById(R.id.btn_m2_1);
        btn_m6_1 = (LongClickButton)rootView.findViewById(R.id.btn_m6_1);
        btn_m5_1 = (LongClickButton)rootView.findViewById(R.id.btn_m5_1);
        btn_m4_1 = (LongClickButton)rootView.findViewById(R.id.btn_m4_1);
        btn_m3_1 = (LongClickButton)rootView.findViewById(R.id.btn_m3_1);

        layout_wifi = (ImageView)rootView.findViewById(R.id.layout_wifi);
        layout_bt = (ImageView)rootView.findViewById(R.id.layout_bt);
        layout_logo = (ImageView)rootView.findViewById(R.id.layout_logo);

        //down
        //panel_btn_anchor = (Button)rootView.findViewById(R.id.panel_btn_anchor);
        panel_btn_z1 = (Button)rootView.findViewById(R.id.panel_btn_z1);
        panel_btn_z2 = (Button)rootView.findViewById(R.id.panel_btn_z2);
        panel_btn_z6 = (Button)rootView.findViewById(R.id.panel_btn_z6);
        panel_btn_z5 = (Button)rootView.findViewById(R.id.panel_btn_z5);
        panel_btn_z4 = (Button)rootView.findViewById(R.id.panel_btn_z4);
        panel_btn_z3 = (Button)rootView.findViewById(R.id.panel_btn_z3);
        panel_btn_z7 = (Button)rootView.findViewById(R.id.panel_btn_z7);
        panel_btn_z8 = (Button)rootView.findViewById(R.id.panel_btn_z8);
        //panel_logo = (ImageView)rootView.findViewById(R.id.panel_logo);
        panel_btn_power = (Button)rootView.findViewById(R.id.panel_btn_power);
        rp8_gear = (Button)rootView.findViewById(R.id.rp8_gear);
        panel_btn_m1 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m1);
        panel_btn_m2 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m2);
        panel_btn_m6 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m6);
        panel_btn_m5 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m5);
        panel_btn_m4 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m4);
        panel_btn_m3 = (LongClickButton)rootView.findViewById(R.id.panel_btn_m3);
        //panel_tv_z1,panel_tv_z2,panel_tv_z3,panel_tv_z4,panel_tv_b,panel_tv_z5,panel_tv_power,panel_tv_nav;
        panel_tv_z1 = (TextView) rootView.findViewById(R.id.panel_tv_z1);
        panel_tv_z2 = (TextView)rootView.findViewById(R.id.panel_tv_z2);
        panel_tv_z6 = (TextView)rootView.findViewById(R.id.panel_tv_z6);
        panel_tv_z5 = (TextView)rootView.findViewById(R.id.panel_tv_z5);
        panel_tv_z4 = (TextView)rootView.findViewById(R.id.panel_tv_z4);
        panel_tv_z3 = (TextView)rootView.findViewById(R.id.panel_tv_z3);
        panel_tv_z7 = (TextView)rootView.findViewById(R.id.panel_tv_z7);
        panel_tv_z8 = (TextView)rootView.findViewById(R.id.panel_tv_z8);
        panel_tv_power = (TextView)rootView.findViewById(R.id.panel_tv_power);
        panel_tv_m1 = (TextView)rootView.findViewById(R.id.panel_tv_m1);
        panel_tv_m2 = (TextView)rootView.findViewById(R.id.panel_tv_m2);
        panel_tv_m6 = (TextView)rootView.findViewById(R.id.panel_tv_m6);
        panel_tv_m5 = (TextView)rootView.findViewById(R.id.panel_tv_m5);
        panel_tv_m4 = (TextView)rootView.findViewById(R.id.panel_tv_m4);
        panel_tv_m3 = (TextView)rootView.findViewById(R.id.panel_tv_m3);
        autoAdatper();


        View.OnClickListener btnOnClickListener = getBtnOnClickListener();
        rp8_gear.setOnClickListener(btnOnClickListener);

        btn_z1.setOnClickListener(btnOnClickListener);
        btn_z2.setOnClickListener(btnOnClickListener);
        btn_z6.setOnClickListener(btnOnClickListener);
        btn_z5.setOnClickListener(btnOnClickListener);
        btn_z4.setOnClickListener(btnOnClickListener);
        btn_on_off.setOnClickListener(btnOnClickListener);
        btn_z3.setOnClickListener(btnOnClickListener);
        btn_z7.setOnClickListener(btnOnClickListener);
        btn_z8.setOnClickListener(btnOnClickListener);
        //down
        panel_btn_z1.setOnClickListener(btnOnClickListener);
        panel_btn_z2.setOnClickListener(btnOnClickListener);
        panel_btn_z6.setOnClickListener(btnOnClickListener);
        panel_btn_z5.setOnClickListener(btnOnClickListener);
        panel_btn_z4.setOnClickListener(btnOnClickListener);
        //panel_btn_anchor.setOnClickListener(btnOnClickListener);
        panel_btn_z3.setOnClickListener(btnOnClickListener);
        panel_btn_z7.setOnClickListener(btnOnClickListener);
        panel_btn_z8.setOnClickListener(btnOnClickListener);
        panel_btn_power.setOnClickListener(btnOnClickListener);

        btn_m2.setOnClickListener(btnOnClickListener);
        panel_btn_m2.setOnClickListener(btnOnClickListener);
        btn_m3.setOnClickListener(btnOnClickListener);
        panel_btn_m3.setOnClickListener(btnOnClickListener);
        btn_m4.setOnClickListener(btnOnClickListener);
        panel_btn_m4.setOnClickListener(btnOnClickListener);
        btn_m5.setOnClickListener(btnOnClickListener);
        panel_btn_m5.setOnClickListener(btnOnClickListener);
        btn_m6.setOnClickListener(btnOnClickListener);
        panel_btn_m6.setOnClickListener(btnOnClickListener);
        btn_m1.setOnClickListener(btnOnClickListener);
        panel_btn_m1.setOnClickListener(btnOnClickListener);
        //长按 配网 //指令
        //panel_btn_z8.setOnLongClickListener();
        // 长按 和 抬起
        longClickUpListener_9 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE9);
        longClickUpListener_10 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE10);
        longClickUpListener_11 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE11);
        longClickUpListener_12 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE12);
        longClickUpListener_13 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE13);
        longClickUpListener_14 = new MyLongClickUpListener(Rp86MCommond.HEX_REQUEST_DEVICE14);

        btn_m1.setLongClickUpListener(longClickUpListener_9);
        panel_btn_m1.setLongClickUpListener(longClickUpListener_9);
        btn_m2.setLongClickUpListener(longClickUpListener_10);
        panel_btn_m2.setLongClickUpListener(longClickUpListener_10);
        btn_m3.setLongClickUpListener(longClickUpListener_11);
        panel_btn_m3.setLongClickUpListener(longClickUpListener_11);
        btn_m4.setLongClickUpListener(longClickUpListener_12);
        panel_btn_m4.setLongClickUpListener(longClickUpListener_12);
        btn_m5.setLongClickUpListener(longClickUpListener_13);
        panel_btn_m5.setLongClickUpListener(longClickUpListener_13);
        btn_m6.setLongClickUpListener(longClickUpListener_14);
        panel_btn_m6.setLongClickUpListener(longClickUpListener_14);
        //// TODO: 2018/4/6  禁止按钮点击
        //disableButtons(false);

        animationFadeIn = new AlphaAnimation(0.3f, 1.0f);
        animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeOut = new AlphaAnimation(1.0f, 0.3f);
        animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
//        animationFadeIn.setStartOffset(100);

        animationFadeIn.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                startAnimation(boolRun_m1,animationFadeOut,btn_m1,panel_btn_m1);//,panel_tv_m1);
                startAnimation(boolRun_m2,animationFadeOut,btn_m2,panel_btn_m2);//,panel_tv_m2);
                startAnimation(boolRun_m3,animationFadeOut,btn_m3,panel_btn_m3);//,panel_tv_m3);
                startAnimation(boolRun_m4,animationFadeOut,btn_m4,panel_btn_m4);//,panel_tv_m4);
                startAnimation(boolRun_m5,animationFadeOut,btn_m5,panel_btn_m5);//,panel_tv_m5);
                startAnimation(boolRun_m6,animationFadeOut,btn_m6,panel_btn_m6);//,panel_tv_m6);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

        animationFadeOut.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationEnd(Animation arg0) {
                startAnimation(boolRun_m1,animationFadeIn,btn_m1,panel_btn_m1);//,panel_tv_m1);
                startAnimation(boolRun_m2,animationFadeIn,btn_m2,panel_btn_m2);//,panel_tv_m2);
                startAnimation(boolRun_m3,animationFadeIn,btn_m3,panel_btn_m3);//,panel_tv_m3);
                startAnimation(boolRun_m4,animationFadeIn,btn_m4,panel_btn_m4);//,panel_tv_m4);
                startAnimation(boolRun_m5,animationFadeIn,btn_m5,panel_btn_m5);//,panel_tv_m5);
                startAnimation(boolRun_m6,animationFadeIn,btn_m6,panel_btn_m6);//,panel_tv_m6);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });
    }
    private void startAnimation(Integer isStart, Animation animation, View ... views) {
        if (1 == isStart) {
            for (int i = 0; i < views.length; i++ ) {
                views[i].startAnimation(animation);
            }
        }
    }
    private void autoAdatper() {
        int widthWin = MetricsUtils.getWindowWidth(getActivity());
        int heightWin = MetricsUtils.getWindowHeigh(getActivity());
        int height = heightWin *2/ 5 - MetricsUtils.dip2px(getActivity(),40);
        int width = widthWin;
        //up
        ViewGroup.LayoutParams layoutParams = btnLayout.getLayoutParams();
        double scaleRatio = 1;
        if(height *layoutParams.width / layoutParams.height  > width){
            height = width * layoutParams.height / layoutParams.width;
            scaleRatio =  (width * 1.0 / layoutParams.width);
        }else{
            width = height * layoutParams.width / layoutParams.height ;
            scaleRatio =  (height * 1.0 / layoutParams.height);
        }

        layoutParams.height = (int)(layoutParams.height * scaleRatio + 0.5);
        layoutParams.width = (int)(layoutParams.width * scaleRatio + 0.5);
        btnLayout.setLayoutParams(layoutParams);

        scaleRelativeLayout(btn_on_off,scaleRatio);
        scaleRelativeLayout(btn_z6,scaleRatio);
        scaleRelativeLayout(btn_z4,scaleRatio);
        scaleRelativeLayout(btn_z5,scaleRatio);
        scaleRelativeLayout(btn_z1,scaleRatio);
        scaleRelativeLayout(btn_z2,scaleRatio);
        scaleRelativeLayout(btn_z3,scaleRatio);
        scaleRelativeLayout(btn_z7,scaleRatio);
        scaleRelativeLayout(btn_z8,scaleRatio);
        scaleRelativeLayout(btn_brand,scaleRatio);
        scaleRelativeLayout(btn_m6,scaleRatio);
        scaleRelativeLayout(btn_m4,scaleRatio);
        scaleRelativeLayout(btn_m5,scaleRatio);
        scaleRelativeLayout(btn_m1,scaleRatio);
        scaleRelativeLayout(btn_m2,scaleRatio);
        scaleRelativeLayout(btn_m3,scaleRatio);

        MetricsUtils.setTextSize((float) scaleRatio * MetricsUtils.sp2px(getContext(),10),
                btn_z1,btn_z2,btn_z3,btn_z4,btn_z5,btn_z6,btn_z7,btn_z8,
                btn_m1,btn_m2,btn_m3,btn_m4,btn_m5,btn_m6);
        MetricsUtils.setTextSize((float) scaleRatio * MetricsUtils.sp2px(getContext(),8),
                btn_brand);

        scaleRelativeLayout(btn_m6_1,scaleRatio);
        scaleRelativeLayout(btn_m4_1,scaleRatio);
        scaleRelativeLayout(btn_m5_1,scaleRatio);
        scaleRelativeLayout(btn_m1_1,scaleRatio);
        scaleRelativeLayout(btn_m2_1,scaleRatio);
        scaleRelativeLayout(btn_m3_1,scaleRatio);
        /*layoutParams = layout_logo.getLayoutParams();
        layoutParams.width = (int) (layoutParams.width * scaleRatio + 0.5);
        layoutParams.height = (int) (layoutParams.height * scaleRatio + 0.5);
        layout_logo.setLayoutParams(layoutParams);*/
        scaleRelativeLayout(layout_logo,scaleRatio);

        scaleRelativeLayout(layout_bt,scaleRatio);
        scaleRelativeLayout(layout_wifi,scaleRatio);
        //down

        int heightDown = heightWin *3/5 - 185;
        int perWidth  = heightDown/8 - MetricsUtils.dip2px(getActivity(),6);
        MetricsUtils.setBtnRadis(panel_btn_z1,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z2,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z3,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z4,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z5,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z6,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z7,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_z8,perWidth);

        MetricsUtils.setBtnRadis(panel_btn_m1,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_m2,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_m3,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_m4,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_m5,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_m6,perWidth);
        MetricsUtils.setBtnRadis(panel_btn_power,perWidth);
        scaleRatio = perWidth/MetricsUtils.dip2px(getActivity(),30);

        MetricsUtils.setTextSize((float) scaleRatio* MetricsUtils.sp2px(getContext(),8),
                panel_btn_z1,panel_btn_z2,panel_btn_z3,panel_btn_z4,panel_btn_z5,panel_btn_z6,panel_btn_z7,panel_btn_z8,
                panel_btn_m1,panel_btn_m2,panel_btn_m3,panel_btn_m4,panel_btn_m5,panel_btn_m6);
        MetricsUtils.setTextSize((float) scaleRatio* MetricsUtils.sp2px(getContext(),12),
                panel_tv_z1,panel_tv_z2,panel_tv_z3,panel_tv_z4,panel_tv_z5,panel_tv_z6,panel_tv_z7,panel_tv_z8,
                panel_tv_m1,panel_tv_m2,panel_tv_m3,panel_tv_m4,panel_tv_m5,panel_tv_m6,panel_tv_power);
        /*
        height = heightWin / 2;
        width = (int)(widthWin * 0.9 + 0.5);

        int radis = width /8 - MetricsUtils.dip2px(getActivity(),4);
        int linearLayout_marginBottom = 0;
        int btn_margin = MetricsUtils.dip2px(getActivity(),2);
        if(height * 0.35 > radis){
            linearLayout_marginBottom = (int)((height * 0.35 - radis)/2 + 0.5);
        }else{
            radis = (int)(height * 0.35 + 0.5);
            btn_margin = (int)(width /8 - radis + 0.5)/2;
        }
        RelativeLayout.LayoutParams relativeLayoutParams  = (RelativeLayout.LayoutParams)linearLayoutDown.getLayoutParams();
        scaleRatio = radis * 1.0 / relativeLayoutParams.height;
        relativeLayoutParams.height = radis;
        relativeLayoutParams.width = width;
        relativeLayoutParams.setMargins(0,0,0,linearLayout_marginBottom);
        linearLayoutDown.setLayoutParams(relativeLayoutParams);

        relativeLayoutParams = (RelativeLayout.LayoutParams)panel_logo.getLayoutParams();
        relativeLayoutParams.width = (int) (relativeLayoutParams.width * scaleRatio + 0.5);
        relativeLayoutParams.height = (int) (relativeLayoutParams.height * scaleRatio + 0.5);
        relativeLayoutParams.setMargins(0,0,0,(int)(height * 0.35));//height * 0.7 / 2
        panel_logo.setLayoutParams(relativeLayoutParams);

        setBtnLayoutDown(panel_btn_b,radis,btn_margin);
        setBtnLayoutDown(panel_btn_z4,radis,btn_margin);
        setBtnLayoutDown(panel_btn_z5,radis,btn_margin);
        setBtnLayoutDown(panel_btn_z1,radis,btn_margin);
        setBtnLayoutDown(panel_btn_z2,radis,btn_margin);
        setBtnLayoutDown(panel_btn_z3,radis,btn_margin);
        setBtnLayoutDown(panel_btn_nav,radis,btn_margin);
        setBtnLayoutDown(panel_btn_anchor,radis,btn_margin);*/
    }

    private void setBtnLayoutDown(View btn, int radis, int btn_margin) {
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = radis;
        relativeLayoutParams.height = radis;
        relativeLayoutParams.setMargins(btn_margin,
                relativeLayoutParams.topMargin,
                btn_margin,
                relativeLayoutParams.bottomMargin);
        btn.setLayoutParams(relativeLayoutParams);
    }

    private void scaleRelativeLayout(View btn, double scaleRatio) {
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams)btn.getLayoutParams();
        relativeLayoutParams.width = (int) (relativeLayoutParams.width * scaleRatio + 0.5);
        relativeLayoutParams.height = (int) (relativeLayoutParams.height * scaleRatio + 0.5);
        relativeLayoutParams.setMargins((int)(relativeLayoutParams.leftMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.topMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.rightMargin * scaleRatio + 0.5),
                (int)(relativeLayoutParams.bottomMargin * scaleRatio + 0.5));
        btn.setLayoutParams(relativeLayoutParams);
    }

    private View.OnClickListener getBtnOnClickListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommond(genCommond(view.getId()));

            }
        };
    }
    public String genCommond(int viewId){
        StringBuffer hexCommond = new StringBuffer();
        hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
        hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
        // 只发键值，状态由继电器控制切换
        if(viewId == R.id.panel_btn_z1 ||  viewId == R.id.btn_z1) {
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE1);
        }
        if(viewId ==  R.id.panel_btn_z2|| viewId ==  R.id.btn_z2){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE2);
        }
        if(viewId ==  R.id.panel_btn_z6 || viewId == R.id.btn_z6){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE6);
        }
        if(viewId ==  R.id.panel_btn_z5 || viewId == R.id.btn_z5){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE5);
        }
        if(viewId ==  R.id.panel_btn_z4 || viewId == R.id.btn_z4){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE4);
        }
        if(viewId ==  R.id.btn_on_off || viewId == R.id.panel_btn_power){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE0);
        }
        if(viewId ==  R.id.panel_btn_z3 ||viewId == R.id.btn_z3){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE3);
        }
            /*case R.id.panel_btn_nav){
                hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE8);
        }
            case R.id.panel_btn_anchor){
                hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE9);
        }*/
        if(viewId ==  R.id.panel_btn_z7 || viewId == R.id.btn_z7){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE7);
        }
        if(viewId ==  R.id.panel_btn_z8 || viewId == R.id.btn_z8){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_GET);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE8);
        }
        if(viewId ==  R.id.panel_btn_m1 || viewId == R.id.btn_m1){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE9);
        }
        if(viewId ==  R.id.panel_btn_m2 || viewId == R.id.btn_m2){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE10);
        }
        if(viewId ==  R.id.panel_btn_m3 || viewId == R.id.btn_m3){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE11);
        }
        if(viewId ==  R.id.panel_btn_m4 || viewId == R.id.btn_m4){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE12);
        }
        if(viewId ==  R.id.panel_btn_m5 || viewId == R.id.btn_m5){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE13);
        }
        if(viewId ==  R.id.panel_btn_m6 ||viewId == R.id.btn_m6){
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_RESERVE);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_DEVICE14);

        }
        hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
        hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
        return  hexCommond.toString();
    }


    public void sendCommond(String hexCommond){
        StartAI.getInstance().getBaseBusiManager().passthrough(((BR16IRMasterActivity)getActivity()).getDevice().getId(), hexCommond, ((BaseActivity)getActivity()).onCallListener);
    }
    public  void  switchBtnStatus(Map<Integer,Integer> commondMap){
        if(null == commondMap || commondMap.size() == 0 ){
            return;
        }
        if(null != commondMap.get(R.id.btn_on_off) && commondMap.get(R.id.btn_on_off) != boolBtn_on_off){
            powerChange = true;
        }

        for (Map.Entry<Integer, Integer> en : commondMap.entrySet()) {

            if(en.getKey() == R.id.btn_on_off) {
                if (1 == en.getValue()) {
                    //btn_on_off.setBackgroundResource(R.mipmap.btn_on_red);//红色
                    btn_on_off.setBackgroundResource(R.mipmap.brand_innovator); // 白色
                    layout_logo.setBackgroundResource(R.mipmap.by_layout_logo);
                    panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off);
                    panel_tv_power.setText(R.string.menu_power_on);
                    panel_tv_power.setTextColor(color_gray);
                    boolBtn_on_off = 1;
                } else {
                    btn_on_off.setBackgroundResource(R.mipmap.brand_innovator_gray); //灰色
                    layout_logo.setBackgroundResource(R.mipmap.by_layout_logo_gray);
                    panel_btn_power.setBackgroundResource(R.mipmap.rp8_btn_off_gray);
                    panel_tv_power.setText(R.string.menu_power_off);
                    panel_tv_power.setTextColor(color_gray);
                    boolBtn_on_off = 0;
                }
            }
            if(en.getKey() == R.id.btn_z3) {
                if (1 == en.getValue()) {
                    //btn_on_off.setBackgroundResource(R.mipmap.btn_on_red);//红色
                    //btn_z3.setBackgroundResource(R.mipmap.oem_live_red);//
                    btn_z3.setTextColor(color_red);
                    panel_btn_z3.setTextColor(color_red);
                    panel_tv_z3.setText(R.string.menu_bdr_lgt_on);
                    panel_tv_z3.setTextColor(color_gray);
                    //panel_btn_z3.setBackgroundResource(R.mipmap.relay_panel_live_red);//
                    boolBtn_z3 = 1;
                } else {
                    //btn_z3.setBackgroundResource(R.mipmap.oem_live);
                    //panel_btn_z3.setBackgroundResource(R.mipmap.relay_panel_live);//白色
                    btn_z3.setTextColor(color_white);
                    panel_btn_z3.setTextColor(color_white);//白色
                    panel_tv_z3.setText(R.string.menu_bdr_lgt_off);
                    panel_tv_z3.setTextColor(color_gray);
                    boolBtn_z3 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z2) {
                if (1 == en.getValue()) {
                    btn_z2.setTextColor(color_red);
                    panel_btn_z2.setTextColor(color_red);//
                    panel_tv_z2.setText(R.string.menu_main_lgt_on);
                    panel_tv_z2.setTextColor(color_gray);
                    boolBtn_z2 = 1;
                } else {
                    btn_z2.setTextColor(color_white);
                    panel_btn_z2.setTextColor(color_white);//白色
                    panel_tv_z2.setText(R.string.menu_main_lgt_off);
                    panel_tv_z2.setTextColor(color_gray);
                    boolBtn_z2 = 0;
                }
            }

            if(en.getKey() == R.id.btn_z1) {
                if (1 == en.getValue()) {/*
                        btn_z1.setBackgroundResource(R.mipmap.oem_acc2_red);
                        panel_btn_z1.setBackgroundResource(R.mipmap.relay_panel_ac2_red);*///
                    btn_z1.setTextColor(color_red);
                    panel_btn_z1.setTextColor(color_red);
                    panel_tv_z1.setText(R.string.menu_zone1_on);
                    panel_tv_z1.setTextColor(color_gray);
                    boolBtn_z1 = 1;
                } else {
                        /*btn_z1.setBackgroundResource(R.mipmap.oem_acc2);
                        panel_btn_z1.setBackgroundResource(R.mipmap.relay_panel_ac2);//白色*/
                    btn_z1.setTextColor(color_white);
                    panel_btn_z1.setTextColor(color_white);
                    panel_tv_z1.setText(R.string.menu_zone1_off);
                    panel_tv_z1.setTextColor(color_gray);
                    boolBtn_z1 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z6) {
                if (1 == en.getValue()) {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg_red);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg_red);*///
                    btn_z6.setTextColor(color_red);
                    panel_btn_z6.setTextColor(color_red);
                    panel_tv_z6.setText(R.string.menu_road_lgt_on);
                    panel_tv_z6.setTextColor(color_gray);
                    boolBtn_z6 = 1;
                } else {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg);*/
                    btn_z6.setTextColor(color_white);
                    panel_btn_z6.setTextColor(color_white);
                    panel_tv_z6.setText(R.string.menu_road_lgt_off);
                    panel_tv_z6.setTextColor(color_gray);
                    boolBtn_z6 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z7) {
                if (1 == en.getValue()) {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg_red);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg_red);*///
                    btn_z7.setTextColor(color_red);
                    panel_btn_z7.setTextColor(color_red);
                    panel_tv_z7.setText(R.string.menu_pch_lgt_on);
                    panel_tv_z7.setTextColor(color_gray);
                    boolBtn_z7 = 1;
                } else {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg);*/
                    btn_z7.setTextColor(color_white);
                    panel_btn_z7.setTextColor(color_white);
                    panel_tv_z7.setText(R.string.menu_pch_lgt_off);
                    panel_tv_z7.setTextColor(color_gray);
                    boolBtn_z7 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z8) {
                if (1 == en.getValue()) {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg_red);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg_red);*///
                    btn_z8.setTextColor(color_red);
                    panel_btn_z8.setTextColor(color_red);
                    panel_tv_z8.setText(R.string.menu_bed_lgt_on);
                    panel_tv_z8.setTextColor(color_gray);
                    boolBtn_z8 = 1;
                } else {
                        /*btn_b.setBackgroundResource(R.mipmap.oem_blg);
                        panel_btn_b.setBackgroundResource(R.mipmap.relay_panel_blg);*/
                    btn_z8.setTextColor(color_white);
                    panel_btn_z8.setTextColor(color_white);
                    panel_tv_z8.setText(R.string.menu_bed_lgt_off);
                    panel_tv_z8.setTextColor(color_gray);
                    boolBtn_z8 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z4) {
                if (1 == en.getValue()) {
                        /*btn_z4.setBackgroundResource(R.mipmap.oem_int_red);
                        panel_btn_z4.setBackgroundResource(R.mipmap.relay_panel_int_red);*/
                    btn_z4.setTextColor(color_red);
                    panel_btn_z4.setTextColor(color_red);
                    panel_tv_z4.setText(R.string.menu_bath_lgt_on);
                    panel_tv_z4.setTextColor(color_gray);
                    boolBtn_z4 = 1;
                } else {
                        /*btn_z4.setBackgroundResource(R.mipmap.oem_int);
                        panel_btn_z4.setBackgroundResource(R.mipmap.relay_panel_int);*/
                    btn_z4.setTextColor(color_white);
                    panel_btn_z4.setTextColor(color_white);
                    panel_tv_z4.setText(R.string.menu_bath_lgt_off);
                    panel_tv_z4.setTextColor(color_gray);
                    boolBtn_z4 = 0;
                }
            }
            if(en.getKey() == R.id.btn_z5) {
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_z5.setTextColor(color_red);
                    panel_btn_z5.setTextColor(color_red);
                    panel_tv_z5.setText(R.string.menu_ext_lgt_on);
                    panel_tv_z5.setTextColor(color_gray);
                    boolBtn_z5 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_z5.setTextColor(color_white);
                    panel_btn_z5.setTextColor(color_white);
                    panel_tv_z5.setText(R.string.menu_ext_lgt_off);
                    panel_tv_z5.setTextColor(color_gray);
                    boolBtn_z5 = 0;
                }
            }
            if(en.getKey() == R.id.btn_m1) {
                if (-1 == en.getValue()) {
                    btn_m1.setTextColor(color_gray);
                    panel_btn_m1.setTextColor(color_gray);
                    panel_tv_m1.setText(R.string.menu_jack1_off);
                    panel_tv_m1.setTextColor(color_gray);
                    boolBtn_m1 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m1.setTextColor(color_green);
                    panel_btn_m1.setTextColor(color_green);
                    panel_tv_m1.setText(R.string.menu_jack1_on);
                    panel_tv_m1.setTextColor(color_gray);
                    boolBtn_m1 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m1.setTextColor(color_red);
                    panel_btn_m1.setTextColor(color_red);
                    panel_tv_m1.setText(R.string.menu_jack1_off);
                    panel_tv_m1.setTextColor(color_gray);
                    boolBtn_m1 = 0;
                }
            }
            if(en.getKey() == R.id.btn_m2) {
                if (-1 == en.getValue()) {
                    btn_m2.setTextColor(color_gray);
                    panel_btn_m2.setTextColor(color_gray);
                    panel_tv_m2.setText(R.string.menu_jack2_off);
                    panel_tv_m2.setTextColor(color_gray);
                    boolBtn_m2 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m2.setTextColor(color_green);
                    panel_btn_m2.setTextColor(color_green);
                    panel_tv_m2.setText(R.string.menu_jack2_on);
                    panel_tv_m2.setTextColor(color_gray);
                    boolBtn_m2 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m2.setTextColor(color_red);
                    panel_btn_m2.setTextColor(color_red);
                    panel_tv_m2.setText(R.string.menu_jack2_off);
                    panel_tv_m2.setTextColor(color_gray);
                    boolBtn_m2 = 0;
                }
            }
            if(en.getKey() == R.id.btn_m3) {
                if (-1 == en.getValue()) {
                    btn_m3.setTextColor(color_gray);
                    panel_btn_m3.setTextColor(color_gray);
                    panel_tv_m3.setText(R.string.menu_slide1_off);
                    panel_tv_m3.setTextColor(color_gray);
                    boolBtn_m3 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m3.setTextColor(color_green);
                    panel_btn_m3.setTextColor(color_green);
                    panel_tv_m3.setText(R.string.menu_slide1_on);
                    panel_tv_m3.setTextColor(color_gray);
                    boolBtn_m3 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m3.setTextColor(color_red);
                    panel_btn_m3.setTextColor(color_red);
                    panel_tv_m3.setText(R.string.menu_slide1_off);
                    panel_tv_m3.setTextColor(color_gray);
                    boolBtn_m3 = 0;
                }
            }

            if(en.getKey() == R.id.btn_m4) {
                if (-1 == en.getValue()) {
                    btn_m4.setTextColor(color_gray);
                    panel_btn_m4.setTextColor(color_gray);
                    panel_tv_m4.setText(R.string.menu_m1_off);
                    panel_tv_m4.setTextColor(color_gray);
                    boolBtn_m4 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m4.setTextColor(color_green);
                    panel_btn_m4.setTextColor(color_green);
                    panel_tv_m4.setText(R.string.menu_m1_on);
                    panel_tv_m4.setTextColor(color_gray);
                    boolBtn_m4 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m4.setTextColor(color_red);
                    panel_btn_m4.setTextColor(color_red);
                    panel_tv_m4.setText(R.string.menu_m1_off);
                    panel_tv_m4.setTextColor(color_gray);
                    boolBtn_m4 = 0;
                }
            }
            if(en.getKey() == R.id.btn_m5) {
                if (-1 == en.getValue()) {
                    btn_m5.setTextColor(color_gray);
                    panel_btn_m5.setTextColor(color_gray);
                    panel_tv_m5.setText(R.string.menu_m2_off);
                    panel_tv_m5.setTextColor(color_gray);
                    boolBtn_m5 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m5.setTextColor(color_green);
                    panel_btn_m5.setTextColor(color_green);
                    panel_tv_m5.setText(R.string.menu_m2_on);
                    panel_tv_m5.setTextColor(color_gray);
                    boolBtn_m5 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m5.setTextColor(color_red);
                    panel_btn_m5.setTextColor(color_red);
                    panel_tv_m5.setText(R.string.menu_m2_off);
                    panel_tv_m5.setTextColor(color_gray);
                    boolBtn_m5 = 0;
                }
            }
            if(en.getKey() == R.id.btn_m6) {
                if (-1 == en.getValue()) {
                    btn_m6.setTextColor(color_gray);
                    panel_btn_m6.setTextColor(color_gray);
                    panel_tv_m6.setText(R.string.menu_m3_off);
                    panel_tv_m6.setTextColor(color_gray);
                    boolBtn_m6 = -1;
                }
                if (1 == en.getValue()) {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1_red);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1_red);*/
                    btn_m6.setTextColor(color_green);
                    panel_btn_m6.setTextColor(color_green);
                    panel_tv_m6.setText(R.string.menu_m3_on);
                    panel_tv_m6.setTextColor(color_gray);
                    boolBtn_m6 = 1;
                } else {
                        /*btn_z5.setBackgroundResource(R.mipmap.oem_acc1);
                        panel_btn_z5.setBackgroundResource(R.mipmap.relay_panel_ac1);*/
                    btn_m6.setTextColor(color_red);
                    panel_btn_m6.setTextColor(color_red);
                    panel_tv_m6.setText(R.string.menu_m3_off);
                    panel_tv_m6.setTextColor(color_gray);
                    boolBtn_m6 = 0;
                }
            }
            if(en.getKey() == R.id.run_m3) {

            }
            if(en.getKey() == R.id.layout_bt) {
                if (1 == en.getValue()) {
                    layout_bt.setBackgroundResource(R.mipmap.bt_on);
                } else {
                    layout_bt.setBackgroundResource(R.mipmap.bt_off);
                }
            }
            if(en.getKey() == R.id.layout_wifi) {
                if (1 == en.getValue()) {
                    layout_wifi.setBackgroundResource(R.mipmap.wifi_on);
                } else {
                    layout_wifi.setBackgroundResource(R.mipmap.wifi_off);
                }
            }

        }

        if( null != commondMap.get(R.id.blink_wifi_rate) && commondMap.get(R.id.blink_wifi_rate) != blink_wifi_rate){
            if(0 != commondMap.get(R.id.blink_wifi_rate)){
                int time = OPEN_BREATH_INTERVAL_TIME /commondMap.get(R.id.blink_wifi_rate);

            } else{
                layout_wifi.clearAnimation();
            }
            blink_wifi_rate = commondMap.get(R.id.blink_wifi_rate);
        }
        if( null != commondMap.get(R.id.run_m6) && commondMap.get(R.id.run_m6) != boolRun_m6){
            if(1 == commondMap.get(R.id.run_m6)){
                boolRun_m6 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                panel_tv_z6.setText(R.string.menu_z6_on);
                btn_m6.startAnimation(animationFadeOut);
            } else{
                btn_m6.clearAnimation();
                boolRun_m6 = 0;
            }

        }
        if( null != commondMap.get(R.id.run_m5) && commondMap.get(R.id.run_m5) != boolRun_m5){
            if(1 == commondMap.get(R.id.run_m5)){
                boolRun_m5 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                btn_m5.startAnimation(animationFadeOut);
            } else{
                btn_m5.clearAnimation();
                boolRun_m5 = 0;
            }

        }
        if(null != commondMap.get(R.id.run_m4) && commondMap.get(R.id.run_m4) != boolRun_m4){
            if(1 == commondMap.get(R.id.run_m4)){
                boolRun_m4 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                btn_m4.startAnimation(animationFadeOut);
            } else{
                btn_m4.clearAnimation();
                boolRun_m4 = 0;
            }

        }
        if(null != commondMap.get(R.id.run_m3) && commondMap.get(R.id.run_m3) != boolRun_m3){
            if(1 == commondMap.get(R.id.run_m3)){
                boolRun_m3 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                panel_tv_z3.setText(R.string.menu_z3_on);
                btn_m3.startAnimation(animationFadeOut);
            } else{
                btn_m3.clearAnimation();
                boolRun_m3 = 0;
            }

        }
        if(null != commondMap.get(R.id.run_m2) && commondMap.get(R.id.run_m2) != boolRun_m2){
            if(1 == commondMap.get(R.id.run_m2)){
                boolRun_m2 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                btn_m2.startAnimation(animationFadeOut);
            } else{
                btn_m2.clearAnimation();
                boolRun_m2 = 0;
            }

        }
        if(null != commondMap.get(R.id.run_m1) && commondMap.get(R.id.run_m1) != boolRun_m1){
            if(1 == commondMap.get(R.id.run_m1)){
                boolRun_m1 = 1;
                animationFadeOut.setDuration(OPEN_BREATH_INTERVAL_TIME);
                animationFadeIn.setDuration(OPEN_BREATH_INTERVAL_TIME);
                btn_m1.startAnimation(animationFadeOut);
            } else{
                btn_m1.clearAnimation();
                boolRun_m1 = 0;
            }

        }
        if(powerChange) {
            disableButtons(1 == boolBtn_on_off? true:false);
            powerChange = false;
        }
        if(0 == boolBtn_on_off) {
            disableButtons(1 == boolBtn_on_off ? true : false);
        }
        btn_on_off.setEnabled(true);
    }

    public void disableButtons(boolean isEnabled){
        btn_z3.setEnabled(isEnabled);
        btn_z2.setEnabled(isEnabled);
        btn_z1.setEnabled(isEnabled);
        btn_z6.setEnabled(isEnabled);
        btn_z5.setEnabled(isEnabled);
        btn_z4.setEnabled(isEnabled);
        btn_on_off.setEnabled(isEnabled);
        btn_z7.setEnabled(isEnabled);
        btn_z8.setEnabled(isEnabled);

        panel_btn_z3.setEnabled(isEnabled);
        panel_btn_z2.setEnabled(isEnabled);
        panel_btn_z1.setEnabled(isEnabled);
        panel_btn_z6.setEnabled(isEnabled);
        panel_btn_z5.setEnabled(isEnabled);
        panel_btn_z4.setEnabled(isEnabled);
        panel_btn_z7.setEnabled(isEnabled);
        panel_btn_z8.setEnabled(isEnabled);

        btn_m3_1.setEnabled(isEnabled);
        btn_m2_1.setEnabled(isEnabled);
        btn_m1_1.setEnabled(isEnabled);
        btn_m6_1.setEnabled(isEnabled);
        btn_m5_1.setEnabled(isEnabled);
        btn_m4_1.setEnabled(isEnabled);
        btn_m3.setEnabled(isEnabled);
        btn_m2.setEnabled(isEnabled);
        btn_m1.setEnabled(isEnabled);
        btn_m6.setEnabled(isEnabled);
        btn_m5.setEnabled(isEnabled);
        btn_m4.setEnabled(isEnabled);

        panel_btn_m3.setEnabled(isEnabled);
        panel_btn_m2.setEnabled(isEnabled);
        panel_btn_m1.setEnabled(isEnabled);
        panel_btn_m6.setEnabled(isEnabled);
        panel_btn_m5.setEnabled(isEnabled);
        panel_btn_m4.setEnabled(isEnabled);
        if(!isEnabled){
            grayButtons();
            stopAnimation();
        }
    }

    private void stopAnimation() {
        //layout_wifi.clearAnimation();
        btn_m1.clearAnimation();
        btn_m2.clearAnimation();
        btn_m3.clearAnimation();
        btn_m4.clearAnimation();
        btn_m5.clearAnimation();
        btn_m6.clearAnimation();

        //blink_wifi_rate = 0;
        boolRun_m1 = 0;
        boolRun_m2 = 0;
        boolRun_m3 = 0;
        boolRun_m4 = 0;
        boolRun_m5 = 0;
        boolRun_m6 = 0;
    }
    private void grayButtons(){
        btn_z3.setTextColor(color_gray);//.setBackgroundResource(R.mipmap.oem_live_gray);
        btn_z2.setTextColor(color_gray);
        btn_on_off.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_off);
        btn_z1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc2_gray);
        btn_z6.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_blg_gray);
        btn_z4.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_int_gray);
        btn_z5.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc1_gray);
        btn_z7.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_nav_gray);
        btn_z8.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_nav_gray);

        panel_btn_z3.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_live_gray);
        panel_btn_z2.setTextColor(color_gray);
        panel_btn_z1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac2_gray);
        panel_btn_z6.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_blg_gray);
        panel_btn_z5.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac1_gray);
        panel_btn_z4.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_int_gray);
        panel_btn_z7.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_nav_gray);
        panel_btn_z8.setTextColor(color_gray);
        panel_btn_power.setTextColor(color_gray);

        btn_m3.setTextColor(color_gray);//.setBackgroundResource(R.mipmap.oem_live_gray);
        btn_m2.setTextColor(color_gray);
        btn_on_off.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_off);
        btn_m1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc2_gray);
        btn_m6.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_blg_gray);
        btn_m4.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_int_gray);
        btn_m5.setTextColor(color_gray);//setBackgroundResource(R.mipmap.oem_acc1_gray);
        panel_btn_m3.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_live_gray);
        panel_btn_m2.setTextColor(color_gray);
        panel_btn_m1.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac2_gray);
        panel_btn_m6.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_blg_gray);
        panel_btn_m5.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_ac1_gray);
        panel_btn_m4.setTextColor(color_gray);//setBackgroundResource(R.mipmap.relay_panel_int_gray);
        //layout_logo.setBackgroundResource(R.mipmap.oem_layout_logo_gray);

    }

    public class MyLongClickUpListener implements LongClickButton.LongClickUpListener {
        String deviceHexCode;
        public MyLongClickUpListener(String deviceHexCode) {
            this.deviceHexCode = deviceHexCode;
        }

        @Override
        public void upAction() {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_SHUTDOWN);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());

        }

        @Override
        public void downAction() {
            StringBuffer hexCommond = new StringBuffer();
            hexCommond.append(Rp86MCommond.HEX_COMMOND_START);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_DATA_LENGTH);
            hexCommond.append(Rp86MCommond.HEX_COMMOND_TYPE_STARTUP);
            hexCommond.append(deviceHexCode);
            hexCommond.append(Rp86MCommond.HEX_REQUEST_FUNC_DATA);
            hexCommond.append(Rp86MCommondUtils.crc8(hexCommond.toString()));
            sendCommond(hexCommond.toString());

        }

        @Override
        public void downing() {
            //Looper.prepare();
            downAction();
            //Looper.loop();
        }


        public void sendCommond(String hexCommond){
            StartAI.getInstance().getBaseBusiManager().passthrough(((BR16IRMasterActivity)getActivity()).getDevice().getId(), hexCommond, ((BaseActivity)getActivity()).onCallListener);
        }
    }
}
