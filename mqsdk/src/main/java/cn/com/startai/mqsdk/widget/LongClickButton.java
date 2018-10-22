package cn.com.startai.mqsdk.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

public class LongClickButton extends android.support.v7.widget.AppCompatButton {

    private LongClickUpListener longClickListener;

    private long intervalTime;
 
    private MyHandler handler;
 
    public LongClickButton(Context context) {
        super(context);
 
        init();
    }
 
    public LongClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        init();
    }
 
    public LongClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
 
        init();
    }
 
    /**
     * 初始化监听
     */
    private void init() {
        handler = new MyHandler(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.downAction();
                new Thread(new LongClickThread()).start();
                return true;
            }
        });
    }
 
    /**
     * 长按时，该线程将会启动
     */
    private class LongClickThread implements Runnable {
        @Override
        public void run() {
            while (LongClickButton.this.isPressed()) {
                SystemClock.sleep(intervalTime);
                //longClickListener.downing();
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }
            SystemClock.sleep(250);
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }
 
    /**
     * 通过handler，使监听的事件响应在主线程中进行
     */
    private static class MyHandler extends Handler {
        private WeakReference<LongClickButton> ref;
 
        MyHandler(LongClickButton button) {
            ref = new WeakReference<>(button);
        }
 
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            LongClickButton button = ref.get();
            if (button != null && button.longClickListener != null) {
                if(msg.what == 1) {
                    button.longClickListener.upAction();
                }else if(msg.what == 2) {
                    button.longClickListener.downing();
                }
            }
        }
    }

    public void setLongClickUpListener(LongClickUpListener listener, long intervalTime) {
        this.longClickListener = listener;
        this.intervalTime = intervalTime;
    }

    public void setLongClickUpListener(LongClickUpListener listener) {
        setLongClickUpListener(listener, 500);
    }
 
    public interface LongClickUpListener {
        void upAction();
        void downAction();
        void downing();
    }
}