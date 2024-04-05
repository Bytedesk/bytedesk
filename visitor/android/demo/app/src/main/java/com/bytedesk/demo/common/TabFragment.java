//package com.bytedesk.demo.common;
//
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.bytedesk.demo.R;
//import com.bytedesk.demo.im.IMController;
//import com.bytedesk.demo.kefu.KeFuController;
//import com.qmuiteam.qmui.util.QMUIResHelper;
//import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
//
//import java.util.HashMap;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//
//public class TabFragment extends BaseFragment {
//
//    @BindView(R.id.pager)
//    ViewPager mViewPager;
//    @BindView(R.id.tabs) QMUITabSegment mTabSegment;
//    private HashMap<Pager, BaseController> mPages;
//
//    private PagerAdapter mPagerAdapter = new PagerAdapter() {
//
//        private int mChildCount = 0;
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public int getCount() {
//            return mPages.size();
//        }
//
//        @Override
//        public Object instantiateItem(final ViewGroup container, int position) {
//            BaseController page = mPages.get(Pager.getPagerFromPosition(position));
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            container.addView(page, params);
//            return page;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            if (mChildCount == 0) {
//                return POSITION_NONE;
//            }
//            return super.getItemPosition(object);
//        }
//
//        @Override
//        public void notifyDataSetChanged() {
//            mChildCount = getCount();
//            super.notifyDataSetChanged();
//        }
//    };
//
//
//    @Override
//    protected View onCreateView() {
//        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab, null);
//        ButterKnife.bind(this, layout);
//        initTabs();
//        initPagers();
//        return layout;
//    }
//
//    private void initTabs() {
//        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_gray_6);
//        int selectColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_blue);
////        mTabSegment.setDefaultNormalColor(normalColor);
////        mTabSegment.setDefaultSelectedColor(selectColor);
////
////        QMUITabSegment.Tab home = new QMUITabSegment.Tab(
////                ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_recent),
////                ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_recent_selected),
////                "客服接口", false
////        );
////
////        QMUITabSegment.Tab social = new QMUITabSegment.Tab(
////                ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_contact),
////                ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_contact_selected),
////                "IM接口", false
////        );
////
////        mTabSegment.addTab(home)
////                .addTab(social);
//    }
//
//    private void initPagers() {
//
//        BaseController.HomeControlListener listener = fragment -> TabFragment.this.startFragment(fragment);
//
//        mPages = new HashMap<>();
//
//        BaseController homeController = new KeFuController(getActivity());
//        homeController.setHomeControlListener(listener);
//        mPages.put(Pager.KEFU, homeController);
//
//        BaseController socialController = new IMController(getActivity());
//        socialController.setHomeControlListener(listener);
//        mPages.put(Pager.IM, socialController);
//
//        mViewPager.setAdapter(mPagerAdapter);
//        mTabSegment.setupWithViewPager(mViewPager, false);
//    }
//
//    enum Pager {
//        KEFU, IM;
//
//        public static Pager getPagerFromPosition(int position) {
//            switch (position) {
//                case 0:
//                    return KEFU;
//                case 1:
//                    return IM;
//                default:
//                    return KEFU;
//            }
//        }
//    }
//
//    @Override
//    protected boolean canDragBack() {
//        return false;
//    }
//
//
//}
