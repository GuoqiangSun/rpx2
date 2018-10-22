package cn.com.startai.newUI.addDevice;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.newUI.WifiSSIDUtil;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class ConfigRouteFragment extends BaseFragment {

    public interface IConfimBtnCallBack {
        void onConfirm(String ssid, String pwd);
    }

    private TextView mTxtSsid;

    @Override
    protected View inflateView() {

        View inflate = View.inflate(getActivity(), R.layout.fragment_newui_config,
                null);

        mTxtSsid = inflate.findViewById(R.id.wifi_ssid);
        final EditText mPwdEdt = inflate.findViewById(R.id.pwd_edt);

        Button mConfirmBtn = inflate.findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentActivity activity = getActivity();
                if (activity instanceof IConfimBtnCallBack) {

                    String ssid = mTxtSsid.getText().toString();
                    String pwd = mPwdEdt.getText().toString();
                    if ("".equals(pwd)) {
                        TAndL.T(activity, "password must not be null");
                        return;
                    }
                    ((IConfimBtnCallBack) activity).onConfirm(ssid, pwd);
                }

            }
        });

        return inflate;
    }

    private final BroadcastReceiver mNetWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Application applicationContext = Objects.requireNonNull(getActivity()).getApplication();
            if (mTxtSsid != null) {
                mTxtSsid.setText(WifiSSIDUtil.getConnectedWiFiSSID(applicationContext));
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Application applicationContext = Objects.requireNonNull(getActivity()).getApplication();
        if (mTxtSsid != null) {
            mTxtSsid.setText(WifiSSIDUtil.getConnectedWiFiSSID(applicationContext));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        applicationContext.registerReceiver(mNetWorkStateReceiver, filter);

        return view;


    }

    @Override
    public void onDestroyView() {
        Application applicationContext = Objects.requireNonNull(getActivity()).getApplication();
        applicationContext.unregisterReceiver(mNetWorkStateReceiver);
        super.onDestroyView();
    }
}
