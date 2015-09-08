package com.sunrise.epark.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.sunrise.epark.util.ToastUtil;

/**
* 描述: 订单详情之确定进场 
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
@SuppressLint("SimpleDateFormat")
public class CarInConfirmActivity extends BaseActivity implements OnClickListener {

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
	/** 确定进场 */
	private Button confirm;
	/**loading框*/
	private Dialog dialog;
	
	private Bundle bundle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carin_confirm);
		initView();
		bindEvent();
		initData();
	}
	
	/**
	 * 初始化组件
	 */
	private void initView() {
		dialog=DialogUtil.createLoadingDialog(this, getString(R.string.loading));
		back = (LinearLayout) this.findViewById(R.id.order_detail_back);
		orderNum = (TextView) this.findViewById(R.id.order_detail_num);
		userName = (TextView) this.findViewById(R.id.order_detail_username);
		userCarNum = (TextView) this.findViewById(R.id.order_detail_usercarnum);
		userMobile = (TextView) this.findViewById(R.id.order_detail_usermobile);
		orderStartDate = (TextView) this.findViewById(R.id.order_detail_starttime);
		confirm = (Button) this.findViewById(R.id.confirm_in);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		back.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		bundle = getIntent().getExtras();  
		orderNum.setText(bundle.getString("orderNum"));
		userName.setText(bundle.getString("userName"));
		userCarNum.setText(bundle.getString("userCarNum"));
		userMobile.setText(bundle.getString("userMobile"));
		orderStartDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm_in://确定进场
			ConfirmIn(bundle.getString("orderNum"), orderStartDate.getText().toString().trim());
			break;
		case R.id.order_detail_back://返回
			this.finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 确定进场
	 * @param orderId 订单号
	 * @param startTime 开始时间
	 */
	public void ConfirmIn(final String orderId,final String startTime){
		dialog.show();
		StringRequest confirmIn = new StringRequest(Method.POST,
				Urls.MANAGER_CONFIRM_IN, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("确定进场："+Urls.MANAGER_CONFIRM_IN, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								finish();
								break;
							case "010":
								DialogUtil.loginAgain(CarInConfirmActivity.this);
								break;
							default:
								ToastUtil.showShort(CarInConfirmActivity.this, orderId+"进场失败");
								break;
							}
						} catch (JSONException e) {
							ToastUtil.showShort(CarInConfirmActivity.this, orderId+"进场失败");
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
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("orderId", orderId);
				map.put("startTime", startTime);
				LogUtil.i("确定进场："+Urls.MANAGER_CONFIRM_IN, StringUtils.getMap(map));
				return map;
			}
		};
		confirmIn.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(confirmIn);
	}

}
