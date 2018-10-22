package cn.com.startai.newUI;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8023;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;

public class ForgetActivity extends BaseActivity2 {

    private EditText mEmailTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include2);
        toolbar.setTitle("Forgot password");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#262626"));
        toolbar.setNavigationIcon(R.mipmap.minimize2_newui);
//        ic_chevron_left_white_24dp
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEmailTxt = findViewById(R.id.et_register_email_email);
        mMsgSend = findViewById(R.id.msg_send_email_lay);
        Button mOKBtn = findViewById(R.id.ok_btn);
        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mInfoTxt = findViewById(R.id.forgot_info_txt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(" forget password ");//2.设置标题
        progressDialog.setMessage("Please wait");//3.设置显示内容
        progressDialog.setCancelable(true);//4.设置可否用back键关闭对话框
    }

    ProgressDialog progressDialog;//1.创建一个ProgressDialog的实例

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private TextView mInfoTxt;
    private LinearLayout mMsgSend;

    private String email;

    public void submit(View v) {

        if (mMsgSend.getVisibility() == View.VISIBLE) {
            mMsgSend.setVisibility(View.INVISIBLE);
        }

        email = mEmailTxt.getText().toString();


        StartAI.getInstance().getBaseBusiManager().sendEmail(email, 2, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {
//                TAndL.T(getApplicationContext(), " msg send success");

                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                TAndL.T(getApplicationContext(), " msg send fail " + startaiError.getErrorMsg());
            }

            @Override
            public boolean needUISafety() {
                return true;
            }
        });

    }

    @Override
    public void onEmilSend(C_0x8023.Resp resp) {
        super.onEmilSend(resp);

        if (progressDialog.isShowing()) {
            progressDialog.cancel();
        }

        if (resp.getResult() == 1) {

            String text = getResources().getText(R.string.forgot_send_email).toString();
            text = text.replace("$email", String.valueOf(email));
            mInfoTxt.setText(text);
            mMsgSend.setVisibility(View.VISIBLE);

        } else {
            TAndL.T(getApplicationContext(), String.valueOf(resp.getContent().getErrmsg()));
            mMsgSend.setVisibility(View.INVISIBLE);
        }

    }
}
