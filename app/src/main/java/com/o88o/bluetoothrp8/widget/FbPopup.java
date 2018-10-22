package com.o88o.bluetoothrp8.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.clj.fastble.data.ScanResult;
import com.o88o.bluetoothrp8.BluetoothService;
import com.o88o.bluetoothrp8.R;
import com.o88o.bluetoothrp8.util.Rp8DeviceUtils;

public class FbPopup extends PopupWindow {
        private ImageView imgLoading ;
        private ImageView loading;
        private BluetoothService mBluetoothService;
        public void setBluetoothService(BluetoothService mBluetoothService){
            this.mBluetoothService = mBluetoothService;
        }
        public void hiddenImgLoading(){
            imgLoading.clearAnimation();
            imgLoading.setVisibility(View.GONE);
        }
        public void hiddenItemLoading(){
            if(null != loading) {
                loading.clearAnimation();
                loading.setVisibility(View.GONE);
            }
        }

        @Override
        public void dismiss() {
            if(mBluetoothService != null ) {
                mBluetoothService.cancelScan();
            }
            super.dismiss();
        }

        public FbPopup(Context mContext, View parent , BluetoothService mBluetoothService, ResultAdapter mResultAdapter,Animation operatingAnim) {

            View view = View.inflate(mContext, R.layout.lay_popup_select, null);
             final ListView listView_device = (ListView) view.findViewById(R.id.list_device);
            imgLoading = (ImageView) view.findViewById(R.id.img_loading);
            imgLoading.startAnimation(operatingAnim);
            listView_device.setAdapter(mResultAdapter);
            mResultAdapter.notifyDataSetChanged();
            this.mBluetoothService = mBluetoothService;

            listView_device.setOnItemClickListener(new MyOnItemClickListener(mContext,mResultAdapter,operatingAnim));

            setWidth(ViewGroup.LayoutParams.FILL_PARENT);
            setHeight(ViewGroup.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//虚拟按键会挡住popupwindow显示
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();
        }
        private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
            private Context mContext;
            private boolean ishit = false;
            private ResultAdapter mResultAdapter;
            private Animation operatingAnim;
            public MyOnItemClickListener(Context mContext,ResultAdapter mResultAdapter, Animation operatingAnim) {
                this.mResultAdapter = mResultAdapter;
                this.operatingAnim = operatingAnim;
                this.mContext = mContext;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!ishit) {
                    if (mBluetoothService != null) {
                        mBluetoothService.cancelScan();
                        //mBluetoothService.setConnectCallback(callback2);
                        ScanResult item = mResultAdapter.getItem(position);
                        mBluetoothService.connect(item);
                        //mResultAdapter.clear();
                        //mResultAdapter.notifyDataSetChanged();
                        ishit = true;
                        loading = (ImageView) view.findViewById(R.id.loading);
                        loading.startAnimation(operatingAnim);
                        Rp8DeviceUtils.saveDeviceInfo(mContext,item.getDevice().getAddress(),item.getDevice().getName());
                    }
                }
            }
        }
    }