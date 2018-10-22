package cn.com.startai.newUI.addDevice;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import cn.com.startai.mqsdk.R;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class MainFragment extends BaseFragment {

    @Override
    protected View inflateView() {
        View inflate = View.inflate(getActivity(), R.layout.fragment_newui_main,
                null);

        Button mYesBtn = inflate.findViewById(R.id.yes_btn);
        mYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentActivity activity = getActivity();

                if (activity instanceof IYesBtnCallBack) {
                    ((IYesBtnCallBack) activity).onYes();
                }

            }
        });

        return inflate;
    }

    public interface IYesBtnCallBack {
        void onYes();
    }


}
