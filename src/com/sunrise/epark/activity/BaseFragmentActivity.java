package com.sunrise.epark.activity;

import com.sunrise.epark.app.BaseApplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		BaseApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从栈中移除该Activity
		BaseApplication.getInstance().finishActivity(this);
	}

}