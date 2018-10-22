package com.o88o.bluetoothrp8.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.o88o.bluetoothrp8.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputDialog extends Dialog {

    private TextView okBtn;
    private EditText editMsg;
    private TextView error;
    private TextView tip;
    private TextView title;
    private String type;

    public InputDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // Auto-generated constructor stub
        onCreate();
    }

    public InputDialog(Context context, int themeResId) {
        super(context, themeResId);
        // Auto-generated constructor stub
        onCreate();
    }

    public InputDialog(Context context,String type) {
        super(context);
        this.type = type;
        // Auto-generated constructor stub
        onCreate();
    }

    public void setDialog( String title,String tip) {
        this.title.setText(title);
        this.tip.setText(tip);
        this.show();
    }

    private void onCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_result);
        okBtn = (TextView) findViewById(R.id.dialog_button);
        editMsg = (EditText) findViewById(R.id.dialog_edit_msg);
        tip = (TextView) findViewById(R.id.dialog_tips);
        error = (TextView) findViewById(R.id.dialog_error);
        title = (TextView) findViewById(R.id.dialog_title);
        //setTitle("提示");
        if("email".equals(type)) {
            okBtn.setBackgroundResource(R.mipmap.dialog_submit);
        }else{
            okBtn.setBackgroundResource(R.mipmap.dialog_ok);
        }
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //校验邮箱
                CharSequence text = editMsg.getText();
                if(null!= text ){
                    /*String[] email = {"longxiuzhai@126.com"}; // 需要注意，email必须以数组形式传入
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822"); // 设置邮件格式
                    intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
                    intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                    intent.putExtra(Intent.EXTRA_SUBJECT, "客户订阅邮件"); // 主题
                    intent.putExtra(Intent.EXTRA_TEXT, text.toString()); // 正文
                    getContext().startActivity(Intent.createChooser(intent, "请选择邮件类应用"));*/


                    // 必须明确使用mailto前缀来修饰邮件地址,如果使用
                    // intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
                    Uri uri = Uri.parse("mailto:longxiuzhai@126.com");
                    String[] email = {"longxiuzhai@126.com"};
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                    if("email".equals(type)){
                        if(!isEmail(text.toString())){
                            error.setText("please enter correct mail format");
                            return;
                        }

                        intent.putExtra(Intent.EXTRA_SUBJECT, "客户订阅邮件,邮箱"); // 主题
                    }else if("other".equals(type)){

                        intent.putExtra(Intent.EXTRA_SUBJECT, "客户订阅邮件,品牌名"); // 主题
                    }

                    intent.putExtra(Intent.EXTRA_TEXT, text.toString()); // 正文
                    getContext().startActivity(Intent.createChooser(intent, "Please choose the mail application"));
                }
                InputDialog.this.dismiss();
            }
        });
    }
    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }
}
