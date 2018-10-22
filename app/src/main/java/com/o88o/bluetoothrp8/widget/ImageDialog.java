package com.o88o.bluetoothrp8.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.o88o.bluetoothrp8.R;

import java.util.ArrayList;

public class ImageDialog extends Dialog {
    private ArrayList<String> volume_sections = new ArrayList<String>();
    private TextView okBtn;
    private TextView resultTextView;
    private CustomSeekbar customSeekBar;
    public ImageDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // Auto-generated constructor stub
        onCreate();
    }

    public ImageDialog(Context context, int themeResId) {
        super(context, themeResId);
        // Auto-generated constructor stub
        onCreate();
    }

    public ImageDialog(Context context) {
        super(context);
        // Auto-generated constructor stub
        onCreate();
        volume_sections.add("1");
        volume_sections.add("2");
        volume_sections.add("3");
        volume_sections.add("4");
        volume_sections.add("5");
        volume_sections.add("6");
        volume_sections.add("7");
        volume_sections.add("8");
        volume_sections.add("9");
        volume_sections.add("10");
    }

    public void setResult(String result) {
        this.resultTextView.setText(result);
        this.show();
    }

    private void onCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rp8_oemr_gear_image);
        /*customSeekBar = (CustomSeekbar) findViewById(R.id.myCustomSeekBar);
        customSeekBar.initData(volume_sections);
        customSeekBar.setProgress(3);*/
        //customSeekBar.setResponseOnTouch();
        /*okBtn = (TextView) findViewById(R.id.dialog_button);
        resultTextView = (TextView) findViewById(R.id.dialog_result);
        setTitle("提示");

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageDialog.this.dismiss();
            }
        });*/
    }

}
