package com.sunrise.epark.activity;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.epark.R;
import com.sunrise.epark.wheel.AbWheelUtil;
import com.sunrise.epark.wheel.AbWheelUtil.DateCallBack;
import com.sunrise.epark.wheel.AbWheelView;

public class TimePicker extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_three);
		if ("hour".equals(getIntent().getStringExtra("type"))) {
			initWheelDate();
		} else {
			initWheelDate1();
		}

	}

	private void initWheelDate() {
		// 年月日时间选择器
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		final AbWheelView mWheelViewY = (AbWheelView) findViewById(R.id.wheelView1);
		final AbWheelView mWheelViewM = (AbWheelView) findViewById(R.id.wheelView2);
		final AbWheelView mWheelViewD = (AbWheelView) findViewById(R.id.wheelView3);
		final AbWheelView mWheelViewHH = (AbWheelView) findViewById(R.id.wheelView4);
		final AbWheelView mWheelViewMM = (AbWheelView) findViewById(R.id.wheelView5);
		Button okBtn = (Button) findViewById(R.id.okBtn);
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewD.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		// AbWheelUtil.initWheelDatePicker(this, mText, mWheelViewY,
		// mWheelViewM, mWheelViewD,okBtn,cancelBtn, year,month,day, year, 120,
		// false);
		AbWheelUtil
				.initWheelDateTimePicker(this, new DateCallBack() {

					@Override
					public void returnResult(String str) {
						// TODO Auto-generated method stub
						if (!"".equals(str)) {
							Intent intent = new Intent();
							intent.putExtra("data", str);
							setResult(1, intent);
						}
						finish();
					}

				}, mWheelViewY, mWheelViewM, mWheelViewD, mWheelViewHH,
						mWheelViewMM, okBtn, cancelBtn, year, month, day, hour,
						min, year, 120, false);
	}

	private void initWheelDate1() {
		// 年月日时间选择器
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);

		final AbWheelView mWheelViewY = (AbWheelView) findViewById(R.id.wheelView1);
		final AbWheelView mWheelViewM = (AbWheelView) findViewById(R.id.wheelView2);
		final AbWheelView mWheelViewD = (AbWheelView) findViewById(R.id.wheelView3);
		final AbWheelView mWheelViewHH = (AbWheelView) findViewById(R.id.wheelView4);
		final AbWheelView mWheelViewMM = (AbWheelView) findViewById(R.id.wheelView5);

		mWheelViewHH.setVisibility(View.GONE);
		mWheelViewMM.setVisibility(View.GONE);

		Button okBtn = (Button) findViewById(R.id.okBtn);
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
		mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));
		mWheelViewD.setCenterSelectDrawable(this.getResources().getDrawable(
				R.drawable.wheel_select));

		// AbWheelUtil.initWheelDatePicker(this, mText, mWheelViewY,
		// mWheelViewM, mWheelViewD,okBtn,cancelBtn, year,month,day, year, 120,
		// false);
		AbWheelUtil.initWheelDatePicker(this, new DateCallBack() {

			@Override
			public void returnResult(String str) {
				// TODO Auto-generated method stub
				if (!"".equals(str)) {
					Intent intent = new Intent();
					intent.putExtra("data", str);
					setResult(1, intent);
				}
				finish();
			}

		}, mWheelViewY, mWheelViewM, mWheelViewD, okBtn, cancelBtn, year,
				month, day, year, 120, false);
	}
}
