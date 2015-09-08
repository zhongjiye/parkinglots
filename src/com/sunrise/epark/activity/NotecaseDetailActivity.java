package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;

/***
 * 
 * this class is used for...个人中心-钱包余额-账单明细-详情
 * 
 * @wangyingjie create
 * @time 2015年7月27日上午9:34:48
 */
public class NotecaseDetailActivity extends BaseActivity implements
		OnClickListener {
	private RelativeLayout back;
	private Dialog loading;
	/** 绿色 */
	private int green = android.graphics.Color.parseColor("#4DC060");
	/** 充值布局 */
	private LinearLayout rechargeLinear;
	/** 充值金额 */
	private TextView rechargeMoney;
	/** 充值金额 */
	private TextView recharge1Money;
	/** 充值时间 */
	private TextView rechargeTime;

	/** 预约布局 */
	private LinearLayout subscribeLinear;
	/** 预约停车场 */
	private TextView subscribeParkName;
	/** 预约金额 */
	private TextView subscribeMoney;
	/** 预约金额 */
	private TextView subscribe1Money;
	/** 预约开始时间 */
	private TextView subscribeStartTime;
	/** 预约结束时间 */
	private TextView subscribeEndTime;
	/** 预约时长 */
	private TextView subscribeLastTime;
	/** 预约取消备注 */
	private TextView subscriberemark;

	/** 停车布局 */
	private LinearLayout parkLinear;
	/** 停车停车场 */
	private TextView parkParkName;
	/** 停车金额 */
	private TextView parkMoney;
	/** 停车金额 */
	private TextView park1Money;
	/** 停车开始时间 */
	private TextView parkStartTime;
	/** 停车结束时间 */
	private TextView parkEndTime;
	/** 停车时长 */
	private TextView parkLastTime;
	
	
	/** 套餐选配布局 */
	private LinearLayout packageView;
	/** 套餐选配停车场 */
	private TextView packageName1;
	private TextView packageName2;
	/** 套餐选配总金额 */
	private TextView packageMoney;
	private TextView packageMoney1;
	private TextView packageMoney2;
	/** 套餐选配开始时间 */
	private TextView packageStartTime1;
	private TextView packageStartTime2;
	/** 套餐选配结束时间 */
	private TextView packageEndTime1;
	private TextView packageEndTime2;
	/** 套餐选配时长 */
	private TextView packageLastTime1;
	private TextView packageLastTime2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notecase_detail);
		initView();

		initParkView();
		initSubscribeView();
		initRechargeView();
		initPackageView();
		bindEvent();
		getData();
	}

	private void initView() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		back = (RelativeLayout) findViewById(R.id.back);
	}

	private void initSubscribeView() {
		subscribeLinear = (LinearLayout) findViewById(R.id.nd_subscribe);
		subscribeParkName = (TextView) findViewById(R.id.nds_parkname);
		subscribeMoney = (TextView) findViewById(R.id.nds_money);
		subscribe1Money = (TextView) findViewById(R.id.nds_money1);
		subscribeStartTime = (TextView) findViewById(R.id.nds_start);
		subscribeEndTime = (TextView) findViewById(R.id.nds_end);
		subscribeLastTime = (TextView) findViewById(R.id.nds_hour);
		subscriberemark = (TextView) findViewById(R.id.nd_remark);
	}

	private void initParkView() {
		parkLinear = (LinearLayout) findViewById(R.id.nd_park);
		parkParkName = (TextView) findViewById(R.id.nd_parkname);
		parkMoney = (TextView) findViewById(R.id.nd_money);
		park1Money = (TextView) findViewById(R.id.nd_money1);
		parkStartTime = (TextView) findViewById(R.id.nd_start);
		parkEndTime = (TextView) findViewById(R.id.nd_end);
		parkLastTime = (TextView) findViewById(R.id.nd_hour);
	}

	private void initRechargeView() {
		rechargeLinear = (LinearLayout) findViewById(R.id.nd_recharge);
		rechargeMoney = (TextView) findViewById(R.id.ndr_money);
		recharge1Money = (TextView) findViewById(R.id.ndr_money1);
		rechargeTime = (TextView) findViewById(R.id.ndr_start);
	}
	
	private void initPackageView(){
		packageView=(LinearLayout)findViewById(R.id.nd_package);
		packageName1=(TextView)findViewById(R.id.np1_parkname);
		packageName2=(TextView)findViewById(R.id.np2_parkname);
		packageMoney=(TextView)findViewById(R.id.np_money);
		packageMoney1=(TextView)findViewById(R.id.np1_money);
		packageMoney2=(TextView)findViewById(R.id.np2_money);
		packageStartTime1=(TextView)findViewById(R.id.np1_start);
		packageStartTime2=(TextView)findViewById(R.id.np2_start);
		packageEndTime1 =(TextView)findViewById(R.id.np1_end);
		packageEndTime2=(TextView)findViewById(R.id.np2_end);
		packageLastTime1 =(TextView)findViewById(R.id.np1_hour);
		packageLastTime2 =(TextView)findViewById(R.id.np2_hour);
	}

	private void bindEvent() {
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}

	}

	private void delRes(String res) {
		try {
			if("05".equals((getIntent().getExtras().getString("payType")))){
				initPackageData(res);
			}else{
				JSONObject object=new JSONObject(res);
				String type = object.getString("payType");
				String money = null;
				String logo = null;
				String start = null;
				String park = null;
				String end = null;
				String last = null;

				if ("04".equals(type)) {
					money = object.getString("payAmount");
					logo = object.getString("payLogo");
					start = object.getString("createTime");
					initRechargeData(money, start, logo);
				} else if ("01".equals(type)||"03".equals(type)) {
					park = object.getString("parkName");
					start = object.getString("startTime");
					end = object.getString("endTime");
					money = object.getString("payAmount");
					logo = object.getString("payLogo");
					last = object.getString("orderSumTime");
					initParkData(park, money, logo, start, end, last);
				} else if ("02".equals(type) || "00".equals(type)) {
					park = object.getString("parkName");
					start = object.getString("startTime");
					end = object.getString("endTime");
					money = object.getString("orderAmount");
					logo = object.getString("payLogo");
					last = object.getString("orderSumTime");
					if ("02".equals(type)) {
						String per = object.getString("parkPer");
						String nowMoney = object.getString("payAmount");
						initSubData(park, money, logo, start, end, last, per,
								nowMoney);
					} else {
						initSubData(park, money, logo, start, end, last, "", money);
					}
				} 
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initPackageData(String res) {
		packageView.setVisibility(View.VISIBLE);
		String money=getIntent().getExtras().getString("payAmount");
		String logo=getIntent().getExtras().getString("payLogo");
		if ("00".equals(logo)) {
			money = "+" + money;
			packageMoney.setTextColor(green);
		} else {
			money = "-" + money;
			packageMoney.setTextColor(Color.RED);
		}
		packageMoney.setText(money);
		try {
			JSONArray array=new JSONArray(res);
			String name=null;
			String hour=null;
			
			String start=null;
			String end=null;
			JSONObject object=null;
			
			object=array.getJSONObject(0);
			name = object.getString("parkName");
			start = object.getString("startTime");
			end = object.getString("endTime");
			money = object.getString("orderAmount");
			hour = object.getString("orderSumTime");
			packageName1.setText(name);
			packageStartTime1.setText(start);
		    packageEndTime1.setText(end);
		    packageMoney1.setText(money);
		    packageLastTime1.setText(hour);
		    
		    object=array.getJSONObject(1);
		    name = object.getString("parkName");
		    start = object.getString("startTime");
		    end = object.getString("endTime");
		    money = object.getString("orderAmount");
		    hour = object.getString("orderSumTime");
		    packageName2.setText(name);
		    packageStartTime2.setText(start);
		    packageEndTime2.setText(end);
		    packageMoney2.setText(money);
		    packageLastTime2.setText(hour);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 充值类型
	 * @param money
	 * @param date
	 * @param logo
	 */
	private void initRechargeData(String money, String date, String logo) {
		rechargeLinear.setVisibility(View.VISIBLE);
		rechargeMoney.setText(money);
		if ("00".equals(logo)) {
			money = "+" + money;
			recharge1Money.setTextColor(green);
		} else {
			money = "-" + money;
			recharge1Money.setTextColor(Color.RED);
		}
		recharge1Money.setText(money);
		rechargeTime.setText(date);
	}

	/**
	 * @param money
	 * @param date
	 * @param logo
	 */
	private void initSubData(String park, String money, String logo,
			String start, String end, String last, String per, String nowMoney) {
		subscribeLinear.setVisibility(View.VISIBLE);
		subscribeParkName.setText(park);
		subscribeMoney.setText(money);
		double sm = Math.abs(Math.abs(Double.valueOf(money))
				- Math.abs(Double.valueOf(nowMoney)));
		if ("00".equals(logo)) {
			nowMoney = "+" + nowMoney;
			subscribe1Money.setTextColor(green);
		} else {
			nowMoney = "-" + nowMoney;
			subscribe1Money.setTextColor(Color.RED);
		}
		subscribe1Money.setText(nowMoney);
		subscribeStartTime.setText(start);
		subscribeEndTime.setText(end);
		subscribeLastTime.setText(last);
		if (!"".equals(per)) {
			subscriberemark.setVisibility(View.VISIBLE);
			subscriberemark.setText("预约前2小时内取消,收取服务费" + per + "%,本单服务费" + sm
					+ "元");
		}
	}

	/**
	 * @param money
	 * @param date
	 * @param logo
	 */
	private void initParkData(String park, String money, String logo,
			String start, String end, String last) {
		parkLinear.setVisibility(View.VISIBLE);
		parkParkName.setText(park);
		parkMoney.setText(money);
		if ("00".equals(logo)) {
			money = "+" + money;
			park1Money.setTextColor(green);
		} else {
			money = "-" + money;
			park1Money.setTextColor(Color.RED);
		}
		park1Money.setText(money);
		parkStartTime.setText(start);
		parkEndTime.setText(end);
		parkLastTime.setText(last);
	}

	private void getData() {
		String url = null;
		switch (getIntent().getExtras().getString("payType")) {
		case "04":// 充值类型
			url = Urls.CHARGE_DETAIL;
			break;
		case "05":// 套餐选配
			url = Urls.PACKAGE_DETAIL;
			break;
		default:
			url = Urls.PAY_DETAIL;
			break;
		}
		LogUtil.i("账单详情:", url);
		StringRequest request = new StringRequest(Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("账单详情:", response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String res = object.getString("result");
								delRes(res);
								break;
							case "001":
								break;
							case "010":
								DialogUtil.loginAgain(NotecaseDetailActivity.this);
								break;
							default:
								break;
							}
						} catch (JSONException e) {
							LogUtil.e("JSONException", e);
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("payId", getIntent().getExtras().getString("payId"));
				LogUtil.i("账单详情:", StringUtils.getMap(map));
				return map;
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(request);
	}

}
