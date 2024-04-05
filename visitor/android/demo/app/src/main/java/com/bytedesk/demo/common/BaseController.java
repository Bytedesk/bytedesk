package com.bytedesk.demo.common;

import android.content.Context;

import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;

/**
 * @author cginechen
 * @date 2016-10-20
 */

public abstract class BaseController extends QMUIWindowInsetLayout {

    private HomeControlListener mHomeControlListener;

    public BaseController(Context context) {
        super(context);
    }

    protected void startFragment(BaseFragment fragment) {
        if (mHomeControlListener != null) {
            mHomeControlListener.startFragment(fragment);
        }
    }

    public void setHomeControlListener(HomeControlListener homeControlListener) {
        mHomeControlListener = homeControlListener;
    }

    protected abstract String getTitle();

    public interface HomeControlListener {
        void startFragment(BaseFragment fragment);
    }


}
