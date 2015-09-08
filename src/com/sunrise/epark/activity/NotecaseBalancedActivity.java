package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;

/**
* 描述: 钱包余额
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class NotecaseBalancedActivity extends BaseActivity implements
		OnClickListener {
	/** 账户余额*/
	private TextView money;
	/**返回*/
	private RelativeLayout back;
	/**充值*/
	private RelativeLayout chongzhi;
	/**账单明细*/
	private RelativeLayout mingxi;
	/**loading框*/
	private Dialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notecase_balanced);
		initView();
		bindEvent();
		if(!"exit".equals(UserInfo.loginStatus)){
			initData();
		}
	}

	/**
	 * 初始化组件
	 */
	private void initView(){
		chongzhi = (RelativeLayout) findViewById(R.id.chongqian);
		money = (TextView) findViewById(R.id.yue);
		back = (RelativeLayout) this.findViewById(R.id.back);
		mingxi = (RelativeLayout) findViewById(R.id.mingxi);
		dialog=DialogUtil.createLoadingDialog(this, getString(R.string.loading));
	}
	/**
	 * 绑定事件
	 */
	private void bindEvent(){
		back.setOnClickListener(this);
		chongzhi.setOnClickListener(this);
		mingxi.setOnClickListener(this);
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		money.setText(UserInfo.money);
		getMoney();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.chongqian:
			//if(StatusUtil.StatusCheck(this)){
				startActivity(new Intent(NotecaseBalancedActivity.this,
						NotecaseChongzhiActivity.class));
		//	}
			break;
		case R.id.mingxi:
//			if(StatusUtil.StatusCheck(this)){
				startActivity(new Intent(NotecaseBalancedActivity.this,
						NotecaseListActivity.class));
//			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 获取账户余额
	 */
	private void getMoney(){
			dialog.show();
			StringRequest getMoney = new StringRequest(Method.POST,
					Urls.ACCOUNT_AMOUNT, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							LogUtil.i("账户金额:"+Urls.ACCOUNT_AMOUNT, response);
							try {
								JSONObject object = new JSONObject(response);
								String code = object.getString("code");
								switch (code) {
								case "000":
									String result = object.getString("result");
									StatusUtil.SaveUserMoney(NotecaseBalancedActivity.this, result);
									money.setText(result.trim());
									break;
								case "010":
									DialogUtil.loginAgain(NotecaseBalancedActivity.this);
									break;
								default:
									money.setText("?");
									break;
								}
							} catch (JSONException e) {
								money.setText("?");
							}finally{
								if(dialog.isShowing()){
									dialog.dismiss();
								}
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if(dialog.isShowing()){
								dialog.dismiss();
							}
							money.setText("?");
						}
					}) {
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("userId", UserInfo.userId);
					map.put("uuid", UserInfo.uuid);
					LogUtil.i("账户金额:"+Urls.ACCOUNT_AMOUNT, StringUtils.getMap(map));
					return map;
				}
			};
			getMoney.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
			BaseApplication.mQueue.add(getMoney);
	}
}
