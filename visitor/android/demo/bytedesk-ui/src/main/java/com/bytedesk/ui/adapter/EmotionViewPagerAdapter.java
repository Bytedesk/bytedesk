package com.bytedesk.ui.adapter;

import android.view.View;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class EmotionViewPagerAdapter extends PagerAdapter {

	private ArrayList<GridView> m_gridViewArrayList;

	public EmotionViewPagerAdapter(ArrayList<GridView> gridViewArrayList) {
		m_gridViewArrayList = gridViewArrayList;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return m_gridViewArrayList.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(m_gridViewArrayList.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(m_gridViewArrayList.get(position));
		return m_gridViewArrayList.get(position);
	}

}
