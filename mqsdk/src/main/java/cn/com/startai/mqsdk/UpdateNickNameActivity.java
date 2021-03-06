package cn.com.startai.mqsdk;

import android.accounts.Account;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import cn.com.startai.mqsdk.busi.C_0x8101;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8020_Resp;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.busi.entity.C_0x8024;
import cn.com.startai.mqttsdk.control.SPController;

public class UpdateNickNameActivity extends BaseActivity {

    private EditText etNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.include2);
        toolbar.setTitle("修改昵称");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initview();
        initData();
    }

    private void initData() {

        C_0x8024.Resp.ContentBean userInfo = AccountActivity.userInfo;
        if (userInfo == null) {

            etNickName.setText("");
        } else {
            etNickName.setText(userInfo.getNickName());

        }

    }

    private void initview() {

        etNickName = (EditText) findViewById(R.id.et_update_nickname);

    }

    @Override
    public void onUpdateUserInfoResult(E_0x8020_Resp resp) {
        super.onUpdateUserInfoResult(resp);
        int result = resp.getResult();
        if (result == 1) {
            TAndL.TL(getApplicationContext(), "昵称修改成功");
            AccountActivity.userInfo.setNickName(resp.getMessage().getNickName());
            finish();
        } else {
            TAndL.TL(getApplicationContext(), "昵称修改失败");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setTitle("保存");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String nickName = etNickName.getText().toString();

            C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
            contentBean.setNickName(nickName);
            contentBean.setUserid(SPController.getUserInfo().getUserid());

            StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, onCallListener);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }


}

