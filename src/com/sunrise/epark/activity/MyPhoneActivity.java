package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/***
 * 
 * this class is used for...个人资料-修改电话
 * 
 * @wangyingjie create
 * @time 2015年7月30日上午12:40:44
 */
public class MyPhoneActivity extends BaseActivity implements OnClickListener {
	/** Volley请求队列 */
	private RelativeLayout back;
	private EditText phoneEditText;
	private Dialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_phone);
		phoneEditText = (EditText) findViewById(R.id.name);
		loadingDialog = DialogUtil.createLoadingDialog(MyPhoneActivity.this,
				"正在保存请稍后...");
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			changephone();
			break;

		default:
			break;
		}
	}

	private void changephone() {
		final String phonrString = phoneEditText.getText().toString().trim();
		if (StringUtils.isPhone(phonrString)) {
			loadingDialog.show();
			StringRequest register = new StringRequest(Method.POST,
					Urls.ADD_PHONE, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							LogUtil.e("新增电话", response);
							try {
								JSONObject object = new JSONObject(response);
								if ("000".equals(object.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "修改成功");
									StatusUtil.SaveTel(getApplicationContext(),
											phonrString);
									finish();
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("001".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(),
											"查找不到数据，请重新输入");
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("002".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "超时");
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("003".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "请求数据不正确");
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("004".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "程序执行异常");
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("005".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "其他");
									if (loadingDialog != null
											&& loadingDialog.isShowing()) {
										loadingDialog.dismiss();
									}
								} else if ("006".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "操作失败");

								} else if ("010".equals(object
										.getString("code"))) {
									DialogUtil.loginAgain(MyPhoneActivity.this);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								if (loadingDialog != null
										&& loadingDialog.isShowing()) {
									loadingDialog.dismiss();
								}
							}
							// ToastUtil.showLong(MyPhoneActivity.this,
							// response);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							ToastUtil.showLong(MyPhoneActivity.this,
									VolleyErrorUtil.getMessage(error,
											MyPhoneActivity.this));
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
					map.put("userTel", phonrString);
					map.put("userId", UserInfo.userId);
					map.put("uuid", UserInfo.uuid);
					LogUtil.e("新增电话map", StringUtils.getMap(map));
					return map;
				}
			};
			register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
					Configure.Retry, Configure.Multiplier));
			BaseApplication.mQueue.add(register);
		} else {
			DialogUtil.showToast(getApplicationContext(),
					getString(R.string.text_telphone));
			finish();
		}

	}

}
