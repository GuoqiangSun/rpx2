package cn.com.startai.mqsdk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import cn.com.startai.kp8.util.FileUtil;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.network.UserIDXml;
import cn.com.startai.mqsdk.util.eventbus.E_0x8018_Resp;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.busi.entity.C_0x8018;
import cn.com.startai.mqttsdk.control.SPController;
import cn.com.startai.newUI.RegisterEmailActivity;

/**
 * 账号加密码登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUname;
    private EditText etPwd;
    private Button btLogin;
    private TextView tvForete;
    private TextView tvRegister;
    private String TAG = LoginActivity.class.getSimpleName();
    private TextView tvProtocol;
    private TextView tvRegister2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include);
        toolbar.setTitle("用户登录");
        setSupportActionBar(toolbar);


        initView();
        initListener();

//        File f = new File(FileUtil.getUpdateDir(),"abc.txt");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 自动登录
         */
        checkLoginStatus();
    }

    private void checkLoginStatus() {

        C_0x8018.Resp.ContentBean userInfo = SPController.getUserInfo();
        if (
                userInfo != null && !TextUtils.isEmpty(userInfo.getUserid())  ||
               NetworkManager.getInstance().getMqttUserID() != null
                ) {
            //TODO:还需要判断token是否过期
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setTitle("快捷登录");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            startActivity(new Intent(LoginActivity.this, Login2Activity.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.bt_login2_login) {
            String uname = etUname.getText().toString();
            String pwd = etPwd.getText().toString();

            StartAI.getInstance().getBaseBusiManager().login(uname, pwd, "", onCallListener);

        } else if (i == R.id.tv_main_new_register) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        } else if (i == R.id.tv_main_forget_pwd) {
        } else if (i == R.id.tv_main_new_register2) {
            startActivity(new Intent(LoginActivity.this, RegisterEmailActivity.class));

        }
    }


    private void initView() {

        etUname = (EditText) findViewById(R.id.et_login2_mobile);
        etPwd = (EditText) findViewById(R.id.et_login2_identify);
        btLogin = (Button) findViewById(R.id.bt_login2_login);
        tvForete = (TextView) findViewById(R.id.tv_main_forget_pwd);
        tvForete.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvRegister = (TextView) findViewById(R.id.tv_main_new_register);
        tvRegister.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvRegister2 = (TextView) findViewById(R.id.tv_main_new_register2);
        tvRegister2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        tvProtocol = (TextView) findViewById(R.id.tv_main_protocl);
        tvProtocol.setText("登录则表您同意遵守亓行智能wifi插座的");

    }

    String text1 = "用户协议";
    String and = "和";
    String text2 = "隐私协议";

    private void initListener() {

        btLogin.setOnClickListener(this);
        tvForete.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvRegister2.setOnClickListener(this);

        SpannableString spStr1 = new SpannableString(text1);

        spStr1.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#99cc33"));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                TAndL.TL(getApplicationContext(), text1);
            }
        }, 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProtocol.append(spStr1);
        tvProtocol.append(and);
        SpannableString spStr2 = new SpannableString(text2);
        spStr2.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#99cc33"));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                TAndL.TL(getApplicationContext(), text2);

            }
        }, 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProtocol.append(spStr2);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    public void onLoginResult(E_0x8018_Resp resp) {
        super.onLoginResult(resp);
        int result = resp.getResult();
        C_0x8018.Resp.ContentBean loginInfo = resp.getLoginInfo();

        if (result == 1) {

            NetworkManager.getInstance().setLoginUserID(loginInfo.getUserid());
            UserIDXml.getInstance(this).setUserID(loginInfo.getUserid());

            TAndL.TL(getApplicationContext(), "登录成功 " + loginInfo.getUserid() + " " + loginInfo.getuName() + " " + loginInfo.getType());
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else if (result == 0) {
            String errmsg = resp.getErrorMsg();
            TAndL.TL(getApplicationContext(), "登录失败 " + errmsg + " loginInfo = " + loginInfo);
        }

    }

}
