package com.sunrise.epark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.sunrise.epark.R;
import com.sunrise.epark.util.PreferenceUtil;

public class ShowActivity extends BaseActivity implements OnPageChangeListener {
	/**
	 * ViewPager
	 */
	private ViewPager viewPager;

	/**
	 * 装ImageView数组
	 */
	private View[] mImageViews;

	/**
	 * 图片资源id
	 */
	private int[] imgIdArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_display);

		viewPager = (ViewPager) findViewById(R.id.viewPager);

		// 载入图片资源ID
		imgIdArray = new int[] { R.drawable.welcome_1, R.drawable.welcome_2,
				R.layout.last_display };

		// 将图片装载到数组中
		mImageViews = new View[imgIdArray.length];
		for (int i = 0; i < mImageViews.length; i++) {
			if (i == mImageViews.length - 1) {
				mImageViews[i] = View.inflate(this, imgIdArray[i], null);
			} else {
				ImageView imageView = new ImageView(this);
				mImageViews[i] = imageView;
				imageView.setBackgroundResource(imgIdArray[i]);
			}
		}

		// 设置Adapter
		viewPager.setAdapter(new MyAdapter(mImageViews));
		// 设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(this);
		// 设置默认显示的选项
		viewPager.setCurrentItem(0);

	}

	class MyAdapter extends PagerAdapter {

		private View[] mImageViews;

		public MyAdapter(View[] mImageViews) {
			this.mImageViews = mImageViews;
		}

		@Override
		public int getCount() {
			return this.mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mImageViews[position]);
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {

			View v = mImageViews[position];
			if (position == mImageViews.length - 1) {
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						PreferenceUtil SPUtils = PreferenceUtil
								.getInstance(getApplicationContext());
						String status = (String) SPUtils.get("loginStatus", "");
						if ("login".equals(status)) {
							String type = (String) SPUtils.get("userRole", "");
							switch (type) {
							case "00":
								startActivity(new Intent(ShowActivity.this,
										LocationActivity.class));
								break;
							case "01":
								startActivity(new Intent(ShowActivity.this,
										CarManageActivity.class));
								break;
							default:
								startActivity(new Intent(ShowActivity.this,
										LocationActivity.class));
								break;
							}
						} else {
							startActivity(new Intent(ShowActivity.this,
									LocationActivity.class));
						}
						finish();
					}
				});
			}
			((ViewPager) container).addView(v, 0);
			return v;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
	}

}
