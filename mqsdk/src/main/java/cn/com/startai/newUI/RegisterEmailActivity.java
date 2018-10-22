package cn.com.startai.newUI;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8017_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8020_Resp;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;

public class RegisterEmailActivity extends BaseActivity2 {

    private EditText etLastName;
    private EditText etFirstName;
    private EditText etPwd;
    private EditText etEmail;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);


        Toolbar toolbar = (Toolbar) findViewById(R.id.include3);
        toolbar.setTitle("Register ID with your email");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#262626"));
        toolbar.setNavigationIcon(R.mipmap.minimize2_newui);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initview();
        initListener();

    }


    @Override
    public void onRegisterResult(E_0x8017_Resp resp) {
        super.onRegisterResult(resp);
        int result = resp.getResult();
        if (result == 1) {

            TAndL.TL(getApplicationContext(), "register success");

            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
            contentBean.setFirstName(firstName);
            contentBean.setLastName(lastName);

            // 登录后才能修改用户信息

//            StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, new IOnCallListener() {
//                @Override
//                public void onSuccess(MqttPublishRequest request) {
//                    RegisterEmailActivity.this.finish();
//                }
//
//                @Override
//                public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
//                    TAndL.TL(getApplicationContext(), "update name fail [" + startaiError.getErrorMsg() + "]");
//                    RegisterEmailActivity.this.finish();
//                }
//
//                @Override
//                public boolean needUISafety() {
//                    return false;
//                }
//            });

        } else if (result == 0) {
//            TAndL.TL(getApplicationContext(), "register fail [" + resp.getErrorCode() + "]");
            TAndL.TL(getApplicationContext(), "register fail [" + resp.getErrorMsg() + "]");
        }

    }

    @Override
    public void onUpdateUserInfoResult(E_0x8020_Resp resp) {
        super.onUpdateUserInfoResult(resp);
        if (resp.getResult() == 1) {
            TAndL.TL(getApplicationContext(), "update user info success");
        } else {
            TAndL.TL(getApplicationContext(), "update user info fail [" + resp.getErrorMsg() + "]");
        }
        RegisterEmailActivity.this.finish();
    }

    private void initListener() {

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = etEmail.getText().toString();
                String pwd = etPwd.getText().toString();


                StartAI.getInstance().getBaseBusiManager().register(uname, pwd, new IOnCallListener() {
                    @Override
                    public void onSuccess(MqttPublishRequest request) {

                    }

                    @Override
                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
//                        TAndL.TL(getApplicationContext(), "register fail [" + startaiError.getErrorCode() + "]");
                        TAndL.TL(getApplicationContext(), "register fail [" + startaiError.getErrorMsg() + "]");
                    }

                    @Override
                    public boolean needUISafety() {
                        return false;
                    }
                });

            }
        });

    }

    private void initview() {

        etEmail = (EditText) findViewById(R.id.et_register_email_email);
        etPwd = (EditText) findViewById(R.id.et_register_email_pwd);
        etFirstName = (EditText) findViewById(R.id.et_register_email_firstname);
        etLastName = (EditText) findViewById(R.id.et_register_email_lastname);
        btRegister = (Button) findViewById(R.id.bt_register_email_register);

    }

}
