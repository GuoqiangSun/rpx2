package cn.com.startai.newUI.login;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.newUI.BaseActivity2;
import cn.com.startai.newUI.ForgetActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.newUI.RegisterEmailActivity;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.network.UserIDXml;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8018_Resp;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/16 0016
 * Desc:
 */
public class LoginActivity extends BaseActivity2 {

    private Application application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newui_login);
        application = getApplication();

        final EditText mIDEdt = findViewById(R.id.ID_edt);
        final EditText mPwdEdt = findViewById(R.id.pwd_edt);

        Button mLoginBtn = findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = mIDEdt.getText().toString();
                String pwd = mPwdEdt.getText().toString();

                StartAI.getInstance().getBaseBusiManager().login(uname, pwd, "", loginCallListener);

            }
        });

        LinearLayout mRegLay = findViewById(R.id.reg_lay);
        mRegLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterEmailActivity.class));
            }
        });

        LinearLayout mForgetPwdLay = findViewById(R.id.forget_pwd_lay);
        mForgetPwdLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });

        Button mFacebookLogin = findViewById(R.id.facebook_login);
        mFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAndL.T(getApplicationContext(), "facebook login Under development");
            }
        });
        Button mGoogleLogin = findViewById(R.id.google_login);
        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAndL.T(getApplicationContext(), "google login Under development");
            }
        });
        Button mTwitterLogin = findViewById(R.id.twitter_login);
        mTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAndL.T(getApplicationContext(), "twitter login Under development");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private final IOnCallListener loginCallListener = new IOnCallListener() {
        @Override
        public void onSuccess(MqttPublishRequest request) {

        }

        @Override
        public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
            TAndL.TL(application, " login msg send fail [" + startaiError.getErrorCode() + "]");
        }

        @Override
        public boolean needUISafety() {
            return true;
        }
    };


    @Override
    public void onLoginResult(E_0x8018_Resp resp) {
        int result = resp.getResult();
        C_0x8018.Resp.ContentBean loginInfo = resp.getLoginInfo();

        if (result == 1) {

            NetworkManager.getInstance().setLoginUserID(loginInfo.getUserid());
            UserIDXml.getInstance(this).setUserID(loginInfo.getUserid());

            TAndL.TL(getApplicationContext(), "login success ");

            Intent intent = new Intent();
            //设置返回数据
            this.setResult(RESULT_OK, intent);

            finish();

        } else if (result == 0) {
            String errmsg = resp.getErrorMsg();
            TAndL.TL(getApplicationContext(), "login fail " + errmsg);
        }

    }


}
