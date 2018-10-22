package cn.com.startai.newUI.addDevice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.com.startai.mqsdk.R;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class NoNetworkIDFragment extends BaseFragment {

    public interface IOkBtnCallBack {
        void onOk();
    }

    @Override
    protected View inflateView() {

        View inflate = View.inflate(getActivity(), R.layout.fragment_newui_nonetworkid,
                null);

        Button mOkBtn = inflate.findViewById(R.id.ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof IOkBtnCallBack) {
                    ((IOkBtnCallBack) activity).onOk();
                }
            }
        });
        return inflate;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;


    }
}
