package com.o88o.bluetoothrp8.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.o88o.bluetoothrp8.R;

import java.util.ArrayList;

public class GearDialog extends Dialog {
    private ArrayList<String> volume_sections = new ArrayList<String>();
    private TextView okBtn;
    private TextView resultTextView;
    private CustomSeekbar ontimeCustomSeekBar;
    private CustomSeekbar offtimeCustomSeekBar;
/*    private SeekBar on_time_seekbar;
    private SeekBar off_time_seekbar;*/
    public GearDialog(Context context, boolean cancelable,
                      OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // Auto-generated constructor stub
        onCreate();
    }

    public GearDialog(Context context, int themeResId) {
        super(context, themeResId);
        // Auto-generated constructor stub
        onCreate();
    }

    public GearDialog(Context context) {
        super(context,R.style.gear_dialog);
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

    public void setResult(String result) {/*
        this.resultTextView.setText(result);*/
        this.show();
    }

    private void onCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rp8_oemr_gear);

        /*off_time_seekbar = (SeekBar) findViewById(R.id.off_time_seekbar);
        on_time_seekbar = (SeekBar) findViewById(R.id.on_time_seekbar);

        on_time_seekbar.setEnabled(false);
        off_time_seekbar.setEnabled(false);*/
        ontimeCustomSeekBar = (CustomSeekbar) findViewById(R.id.on_time_CustomSeekBar);
        ontimeCustomSeekBar.initData(volume_sections);
        ontimeCustomSeekBar.setProgress(2);
        ontimeCustomSeekBar.setEnabled(false);

        offtimeCustomSeekBar = (CustomSeekbar) findViewById(R.id.off_time_CustomSeekBar);
        offtimeCustomSeekBar.initData(volume_sections);
        offtimeCustomSeekBar.setProgress(5);
        offtimeCustomSeekBar.setEnabled(false);

        //customSeekBar.setResponseOnTouch();
        /*okBtn = (TextView) findViewById(R.id.dialog_button);
        resultTextView = (TextView) findViewById(R.id.dialog_result);
        setTitle("提示");

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GearDialog.this.dismiss();
            }
        });*/
    }

}
