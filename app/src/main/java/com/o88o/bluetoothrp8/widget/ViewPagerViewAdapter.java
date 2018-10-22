package com.o88o.bluetoothrp8.widget;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by lujuanjuan on 2017/3/16 0016.
 */

public class ViewPagerViewAdapter extends PagerAdapter{
    List<View> pageview ;

    public ViewPagerViewAdapter(List<View> pageView) {
        super();
        this.pageview = pageView;
    }
    @Override
    public int getCount() {
        //获取当前窗体界面数
        return pageview.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        //判断是否由对象生成界面
        return view == object;
    }

    //使从ViewGroup中移出当前View
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(pageview.get(arg1));
    }

    //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(pageview.get(arg1));
        return pageview.get(arg1);
    }
}
