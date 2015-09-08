package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.sunrise.epark.util.PreferenceUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/***
 * 
 * this class is used for...个人中心-发票邮递
 * 
 * @wangyingjie create
 * @time 2015年7月27日上午9:39:37
 */

public class SendReceiptActivity extends BaseActivity implements
		OnClickListener {
	private Dialog loadingDialog, dialog1;
	private RelativeLayout back;
	private EditText nameTextView;// 联系人
	/** 最大开票金额 */
	private TextView maxMoney;
	private EditText telphoneTextView;// 联系电话
	private EditText jine;// 开票金额
	private EditText comp;// 开票抬头
	private EditText addr;// 详细地址
	private EditText postic;// 邮编
	private Button ok;// 递交
	/** 邮递发票提示 */
	/** 弹出对话框 */
	private Dialog dialog;
	/** 金额 */
	private TextView yuan;
	/** 抬头 */
	private TextView name;
	/** 地址 */
	private TextView address;
	/** 取消 */
	private Button no;
	/** 确定 */
	private Button yes;
	/** 最大金额 */
	private double maxqian;
	private double qian;
	private String compsString = null;
	private String addreString = null;
	private String posticString = null;
	private String jinesString = null;
	private String maxdouble;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_receipt);
		initview();
		initdata();
		bindEvent();
	}

	/** 初始化 */
	private void initview() {
		nameTextView = (EditText) findViewById(R.id.name);
		telphoneTextView = (EditText) findViewById(R.id.telphone);
		maxMoney = (TextView) findViewById(R.id.maxjine);
		jine = (EditText) findViewById(R.id.jine);
		comp = (EditText) findViewById(R.id.taitou1);
		addr = (EditText) findViewById(R.id.addr);
		postic = (EditText) findViewById(R.id.ic);
		loadingDialog = DialogUtil.createLoadingDialog(
				SendReceiptActivity.this, "正在发送，请进邮箱查询...");
		dialog1 = DialogUtil.pleaseInUserInfoDialog(SendReceiptActivity.this,
				"请维护个人信息");

	}

	/** 设置数据 */
	private void initdata() {
		// nameTextView.setText(UserInfo.userName);
		// telphoneTextView.setText(UserInfo.userTel);
		jinesString = jine.getText().toString().trim();
		if (!"".equals(jinesString) || StringUtils.isNotNull(jinesString)) {
			qian = Double.parseDouble(jinesString);
		}
		compsString = comp.getText().toString().trim();
		addreString = addr.getText().toString().trim();
		posticString = postic.getText().toString().trim();
		PreferenceUtil preferenceUtil = PreferenceUtil
				.getInstance(getApplicationContext());
		maxdouble = preferenceUtil.getString("maxMoney", "");
		if (!"".equals(maxdouble) || StringUtils.isNotNull(jinesString)) {
			maxqian = Double.parseDouble(maxdouble);
		}

		maxMoney.setText(maxdouble);
	}

	/** 绑定事件 */
	private void bindEvent() {
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.ok:
			initdata();
			if ("login".equals(UserInfo.loginStatus)) {
				if (!"0".equals(maxdouble)) {
					if (jinesString.length() > 0
							& StringUtils.isNotNull(addreString)
							& StringUtils.isNotNull(compsString)) {
						if (StringUtils.isNumber(jinesString)) {
							if (maxqian - qian > 0) {
								sendtitile();
							} else {
								DialogUtil.showToast(getApplicationContext(),
										getString(R.string.send_max_double));
							}
						} else {
							DialogUtil.showToast(getApplicationContext(),
									getString(R.string.send_double));
						}
					} else {
						DialogUtil.showToast(getApplicationContext(),
								getString(R.string.send_null));
					}
				} else {
					DialogUtil.showToast(getApplicationContext(),
							"可开最大金额为零，没法开票");
				}
			} else {
				dialog1.show();
			}
			break;
		default:
			break;
		}

	}

	/** 发送发票提示 */
	private void sendtitile() {
		dialog = cancelDialog(this);
		dialog.show();
	}

	/**
	 * 弹出确认取消对话框
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog cancelDialog(Context context) {
		initdata();
		final String myname = nameTextView.getText().toString().trim();
		final String myphone = telphoneTextView.getText().toString().trim();
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.send_receipt_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		/** 开票金额 */
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		/** 发票抬头 */
		TextView tv2 = (TextView) popView.findViewById(R.id.tv2);
		/** 邮递地址 */
		TextView tv3 = (TextView) popView.findViewById(R.id.tv3);
		/** 金额 */
		TextView yuan = (TextView) popView.findViewById(R.id.yuan);
		yuan.setText(jinesString);
		/** 抬头 */
		TextView name = (TextView) popView.findViewById(R.id.name);
		name.setText(compsString);
		/** 地址 */
		TextView address = (TextView) popView.findViewById(R.id.address);
		address.setText(addreString);
		/** 取消 */
		no = (Button) popView.findViewById(R.id.cancel);
		/** 确定 */
		yes = (Button) popView.findViewById(R.id.yes);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadingDialog.show();
				StringRequest senp = new StringRequest(Method.POST,
						Urls.INSERT_POST, new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Log.e("邮递发票", response);
								try {
									JSONObject jObject = new JSONObject(
											response);
									if ("000".equals(jObject.getString("code"))) {
										startActivity(new Intent(
												SendReceiptActivity.this,
												LocationActivity.class));
										DialogUtil.showToast(
												getApplicationContext(),
												"发票邮递成功");
										finish();
									} else if ("001".equals(jObject
											.getString("code"))) {
										DialogUtil.showToast(
												getApplicationContext(),
												"查找不到数据，请重新输入");
									} else if ("002".equals(jObject
											.getString("code"))) {
										DialogUtil.showToast(
												getApplicationContext(), "超时");
									} else if ("003".equals(jObject
											.getString("code"))) {
										DialogUtil.showToast(
												getApplicationContext(),
												"请求数据不正确");
									} else if ("004".equals(jObject
											.getString("code"))) {
										DialogUtil.showToast(
												getApplicationContext(),
												"程序执行异常");
									} else if ("005".equals(jObject
											.getString("code"))) {
										DialogUtil.showToast(
												getApplicationContext(), "其他");
									} else if ("006".equals(jObject
											.getString("code"))) {
										DialogUtil
												.showToast(
														getApplicationContext(),
														"操作失败");
									} else if ("010".equals(jObject
											.getString("code"))) {
										DialogUtil
												.loginAgain(SendReceiptActivity.this);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								} finally {
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								}
								// ToastUtil.showLong(SendReceiptActivity.this,
								// response);
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								ToastUtil.showLong(SendReceiptActivity.this,
										VolleyErrorUtil.getMessage(error,
												SendReceiptActivity.this));
								if (loadingDialog != null
										&& loadingDialog.isShowing()) {
									loadingDialog.dismiss();
								}
							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("uuid", UserInfo.uuid);
						map.put("userId", UserInfo.userId);
						map.put("postName", myname);// UserInfo.userName);
						map.put("postAmount", jinesString);
						map.put("postLook", compsString);
						map.put("postTel", myphone);// UserInfo.userTel);
						map.put("postAddr", addreString);
						LogUtil.i("发票邮递", StringUtils.getMap(map));
						return map;
					}
				};
				senp.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
						Configure.Retry, Configure.Multiplier));
				BaseApplication.mQueue.add(senp);
			}
		});

		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}

}
