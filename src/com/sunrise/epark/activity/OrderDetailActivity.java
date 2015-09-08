package com.sunrise.epark.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunrise.epark.R;

/**
 * 
 * 文件名: OrderDetailActivity.java 描述: 订单记录之订单详情  公司: Camelot
 * @author zhongjy
 * @date 2015年7月29日
 * @version 1.0
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener {

	/** 返回 */
	private LinearLayout back;
	/** 订单号 */
	private TextView orderNum;
	/** 用户名 */
	private TextView userName;
	/** 手机号 */
	private TextView userMobile;
	/** 车牌号 */
	private TextView userCarNum;
	/** 开始时间 */
	private TextView orderStartDate;
	/** 结束时间 */
	private TextView orderEndDate;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		initView();
		bindEvent();
		initData();
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		orderNum.setText(bundle.getString("orderNum"));
		userName.setText(bundle.getString("userName"));
		userCarNum.setText(bundle.getString("userCarNum"));
		userMobile.setText(bundle.getString("userMobile"));
		orderStartDate.setText(bundle.getString("orderStartDate"));
		orderEndDate.setText(bundle.getString("orderEndDate"));
	}

	private void initView() {
		back = (LinearLayout) this.findViewById(R.id.order_detail_back);
		orderNum = (TextView) this.findViewById(R.id.order_detail_num);
		userName = (TextView) this.findViewById(R.id.order_detail_username);
		userCarNum = (TextView) this.findViewById(R.id.order_detail_usercarnum);
		userMobile = (TextView) this.findViewById(R.id.order_detail_usermobile);
		orderStartDate = (TextView) this
				.findViewById(R.id.order_detail_starttime);
		orderEndDate = (TextView) this.findViewById(R.id.order_detail_endtime);
	}

	private void bindEvent() {
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_detail_back:
			this.finish();
			break;
		default:
			break;
		}
	}
}
