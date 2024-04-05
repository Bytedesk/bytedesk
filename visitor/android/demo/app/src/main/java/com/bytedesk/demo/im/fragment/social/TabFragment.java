package com.bytedesk.demo.im.fragment.social;

import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.TabEvent;
import com.bytedesk.demo.im.adapter.TabAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关注/粉丝/好友/黑名单
 *
 * @author bytedesk.com on 2018/3/26.
 */
public class TabFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabSegment;
    @BindView(R.id.pager) ViewPager mQMUIViewPager;

    private TabAdapter tabAdapter;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab_social, null);
        ButterKnife.bind(this, rootView);

        initTopBar();
        initTabs();
        initPagers();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i("start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.i("stop");

        mTopBar = null;
        mTabSegment = null;
        mQMUIViewPager = null;
    }

    /**
     *
     */
    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("社交接口");
    }

    /**
     *
     */
    private void initTabs() {
        //
        QMUITabBuilder builder = mTabSegment.tabBuilder();
        mTabSegment.addTab(builder.setText("测试用户").build(getContext()));
        mTabSegment.addTab(builder.setText("关注").build(getContext()));
        mTabSegment.addTab(builder.setText("粉丝").build(getContext()));
        mTabSegment.addTab(builder.setText("好友").build(getContext()));
        mTabSegment.addTab(builder.setText("黑名单").build(getContext()));
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
    }

    /**
     *
     */
    private void initPagers() {
        //
        TestUserFragment testUserFragment = new TestUserFragment();
        FollowFragment followFragment = new FollowFragment();
        FanFragment fanFragment = new FanFragment();
        FriendFragment friendFragment = new FriendFragment();
        BlockFragment blockFragment = new BlockFragment();
        //
        fragmentList.add(testUserFragment);
        fragmentList.add(followFragment);
        fragmentList.add(fanFragment);
        fragmentList.add(friendFragment);
        fragmentList.add(blockFragment);
        //
        tabAdapter = new TabAdapter(this.getFragmentManager(), fragmentList);
        mQMUIViewPager.setAdapter(tabAdapter);
        //
        mTabSegment.setupWithViewPager(mQMUIViewPager, false);
//        mTabSegment.setOnTabClickListener(index -> {
//            Logger.i("tab " + index + " clicked");
//            EventBus.getDefault().post(new TabEvent(index));
//        });
    }



}








