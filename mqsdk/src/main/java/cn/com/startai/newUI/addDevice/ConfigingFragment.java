package cn.com.startai.newUI.addDevice;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.com.startai.mqsdk.R;

/**
 * author: Guoqiang_Sun
 * date : 2018/10/9 0009
 * desc :
 */
public class ConfigingFragment extends BaseFragment {


    @Override
    protected View inflateView() {

        View inflate = View.inflate(getActivity(), R.layout.fragment_newui_configing,
                null);
        ImageView mLoadingImg = inflate.findViewById(R.id.loading_img);
        AnimationDrawable mAniDraw = (AnimationDrawable) mLoadingImg.getBackground();
        mAniDraw.start();
        return inflate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;


    }
}
