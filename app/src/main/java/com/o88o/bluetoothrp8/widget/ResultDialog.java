package com.o88o.bluetoothrp8.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.o88o.bluetoothrp8.R;

public class ResultDialog extends Dialog {

    private TextView okBtn;
    private TextView resultTextView;

    public ResultDialog(Context context, boolean cancelable,
                        OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // Auto-generated constructor stub
        onCreate();
    }

    public ResultDialog(Context context, int themeResId) {
        super(context, themeResId);
        // Auto-generated constructor stub
        onCreate();
    }

    public ResultDialog(Context context) {
        super(context);
        // Auto-generated constructor stub
        onCreate();
    }

    public void setResult(String result) {
        this.resultTextView.setText(result);
        this.show();
    }

    private void onCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_result);
        okBtn = (TextView) findViewById(R.id.dialog_button);
        resultTextView = (TextView) findViewById(R.id.dialog_result);
        setTitle("提示");

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResultDialog.this.dismiss();
            }
        });
    }

}
