package com.o88o.bluetoothrp8.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.o88o.bluetoothrp8.R;

public class FullScrreenDialog extends Dialog {
    public FullScrreenDialog(Context context) {
        super(context);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意顺序
        //<!--关键点1-->
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_full_screen, null);
        //<!--关键点2-->
        setContentView(view);
        //<!--关键点3-->  透明
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        //<!--关键点4-->
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}