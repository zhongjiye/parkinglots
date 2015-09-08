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
 * this class is used for...个人资料-修改邮箱
 * 
 * @wangyingjie create
 * @time 2015年7月30日上午12:40:14
 */
public class MyEmailActivity extends BaseActivity implements OnClickListener {
	/** Volley请求队列 */
	private RelativeLayout back;
	private EditText emailEditText;
	private Dialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_email);
		emailEditText = (EditText) findViewById(R.id.email);
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		loadingDialog = DialogUtil.createLoadingDialog(MyEmailActivity.this,
				"正在保存请稍后...");
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			changeEmail();
			break;

		default:
			break;
		}

	}

	private void changeEmail() {
		final String email = emailEditText.getText().toString().trim();
		if (StringUtils.isEmail(email)) {
			loadingDialog.show();
			StringRequest reg = new StringRequest(Method.POST, Urls.ADD_EMAIL,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							LogUtil.i("修改邮箱", response);
							try {
								JSONObject object = new JSONObject(response);
								if ("000".equals(object.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "修改成功");
									StatusUtil.SaveEmail(
											getApplicationContext(), email);
									finish();
								} else if ("001".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(),
											"查找不到数据，请重新输入");
								} else if ("002".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "超时");
								} else if ("003".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "请求数据不正确");
								} else if ("004".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "程序执行异常");
								} else if ("005".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "其他");
								} else if ("006".equals(object
										.getString("code"))) {
									DialogUtil.showToast(
											getApplicationContext(), "操作失败");
								} else if ("010".equals(object
										.getString("code"))) {
									DialogUtil.loginAgain(
											MyEmailActivity.this);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								if (loadingDialog != null
										&& loadingDialog.isShowing()) {
									loadingDialog.dismiss();
								}
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							ToastUtil.showLong(MyEmailActivity.this,
									VolleyErrorUtil.getMessage(error,
											MyEmailActivity.this));
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
					map.put("userId", UserInfo.userId);
					map.put("userMail", email);
					map.put("uuid", UserInfo.uuid);
					LogUtil.i("修改邮箱", StringUtils.getMap(map));
					return map;
				}
			};
			reg.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
			BaseApplication.mQueue.add(reg);
		} else {
			DialogUtil.showToast(getApplicationContext(),
					getString(R.string.text_email));
			finish();
		}

	}

}
