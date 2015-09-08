package com.sunrise.epark.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sunrise.epark.R;

public class CarListFragment extends Fragment implements OnClickListener,OnPageChangeListener {

	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments;
	private View line1, line2;
	private LinearLayout carInLinearLayout,carNotInLinearLayout;
	private FragmentManager fragmentManager;	
	private Fragment carInFragment,carOutFragment;
	@SuppressWarnings("unused")
	private int currentIndex;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_manager_main, container,
				false);
		
		initView(view);
		
		bindEvent();
		
		mAdapter = new FragmentPagerAdapter(fragmentManager) {
			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mFragments.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);
		return view;
	}

	private void initView(View view) {
		fragmentManager=getChildFragmentManager();
		
		mFragments= new ArrayList<Fragment>();
		mViewPager = (ViewPager) view.findViewById(R.id.car_manager);
		
		line1 = (View) view.findViewById(R.id.car_line1);
		line2 = (View) view.findViewById(R.id.car_line2);
		
		carInLinearLayout = (LinearLayout) view.findViewById(R.id.car_in);
		carNotInLinearLayout = (LinearLayout) view.findViewById(R.id.car_not_in);
		
		carInFragment = new CarInFragment();
		carOutFragment = new CarOutFragment();
		
		mFragments.add(carInFragment);
		mFragments.add(carOutFragment);
	}
	
	private void bindEvent(){
		line1.setOnClickListener(this);
		line2.setOnClickListener(this);
		carInLinearLayout.setOnClickListener(this);
		carNotInLinearLayout.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_in:
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(1);
			break;
		case R.id.car_not_in:
			line2.setVisibility(View.VISIBLE);
			line1.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.INVISIBLE);
			break;
		case 1:
			line2.setVisibility(View.VISIBLE);
			line1.setVisibility(View.INVISIBLE);
			break;
		}
		currentIndex = position;
	}
}
