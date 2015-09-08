package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/***
 * 
 * this class is used for..登陆
 * 
 * @wangyingjie create
 * @time 2015年7月21日上午9:30:20
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnTouchListener {

	private Dialog loading;
	private RelativeLayout back;
	/** 返回 */
	private TextView register;
	/** 注册 */
	private TextView forgetpwd;
	/** 忘记密码 */
	private EditText username;
	/** 用户名 */
	private EditText password;
	/** 密码 */
	private Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		bindEvent();
	}

	private void initView() {
		loading = DialogUtil.createLoadingDialog(LoginActivity.this,
				"正在登陆请稍后...");
		back = (RelativeLayout) findViewById(R.id.back);
		register = (TextView) findViewById(R.id.tv_register);
		forgetpwd = (TextView) findViewById(R.id.tv_forgetpsw);
		login = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
	}

	private void bindEvent() {
		back.setOnClickListener(this);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		forgetpwd.setOnClickListener(this);
		username.setOnClickListener(this);
		password.setOnTouchListener(this);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			if (BaseApplication.getInstance().getSize() == 1) {
				startActivity(new Intent(this, LocationActivity.class));
			} else {
				finish();
			}
			break;
		case R.id.tv_register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_forgetpsw:
			Intent intent1 = new Intent(this, ForgetPwdActivity.class);
			startActivity(intent1);
			break;
		case R.id.login:
			String userName = username.getText().toString().trim();
			if(checkUsername(userName)){
				String pass=password.getText().toString().trim();
				if(checkPwd(pass)){
					login(userName, pass);
				}else{
					password.setFocusable(true);
					password.setFocusableInTouchMode(true);
				}
			}else{
				username.setFocusable(true);
				username.setFocusableInTouchMode(true);
				username.requestFocus();
			}
			break;
		}
	}

	private void login(final String userName, final String password) {
		loading.show();
		StringRequest register = new StringRequest(Method.POST,
				Urls.LOGIN_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("登陆", response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								object = object.getJSONObject("result");
								if (StatusUtil.Login(object,
										getApplicationContext())) {
									String userRole = object
											.getString("userRole");
									Intent intent = null;
									if ("00".equals(userRole)) {
										if (loading != null
												&& loading.isShowing()) {
											loading.dismiss();
										}
										intent = new Intent(LoginActivity.this,
												LocationActivity.class);
										startActivity(intent);
										checkClock();
										finish();
									} else if ("01".equals(userRole)) {
										if (loading != null
												&& loading.isShowing()) {
											loading.dismiss();
										}
										intent = new Intent(LoginActivity.this,
												CarManageActivity.class);
										startActivity(intent);
										finish();
									}
								}

								break;
							case "001":
								DialogUtil.showDialog(LoginActivity.this,
										"用户不存在", true,username);
								break;
							case "002":

								break;
							case "003":

								break;
							case "006":
								if (Integer.parseInt(object
										.getString("errorCount")) > 0) {
									DialogUtil
											.showDialog(
													LoginActivity.this,
													object.getString("resultMsg"),
													true,LoginActivity.this.password);
								} else {
									DialogUtil.showDialog(LoginActivity.this,
											"您今日密码已错误3次,请联系管理员", true);
								}
								break;

							default:
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(LoginActivity.this, VolleyErrorUtil
								.getMessage(error, LoginActivity.this));
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userAcount", userName);
				map.put("userPassword", password);
				LogUtil.i("登陆", StringUtils.getMap(map));
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	@Override
	public void onBackPressed() {
		if (BaseApplication.getInstance().getSize() == 1) {
			startActivity(new Intent(this, LocationActivity.class));
		} else {
			finish();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.password:
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				String name = username.getText().toString().trim();
				checkUsername(name);
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	private boolean checkUsername(String name){
		if (StringUtils.isNotNull(name)) {
			if (!StringUtils.isuser(name)) {
				DialogUtil.showDialog(LoginActivity.this,
						getString(R.string.text_username), true,username);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			}else{
				return true;
			}
		} else {
			DialogUtil.showDialog(LoginActivity.this, "用户名不能为空", true,username);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}
	
	private boolean checkPwd(String pass){
		if (StringUtils.isNotNull(pass)) {
			if (!StringUtils.ispwd(pass)) {
				DialogUtil.showDialog(LoginActivity.this,
						getString(R.string.text_pwd), true,password);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			} else {
				return true;
			}
		} else {
			DialogUtil.showDialog(LoginActivity.this, "密码不能为空",
					true,password);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}

	private void checkClock()
	{
		
		StringRequest register = new StringRequest(Method.POST,
				Urls.CHECK_CLOCK, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
					
						LogUtil.e("获取申请的订单", response);
						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								JSONObject result = jObject.getJSONObject("result");
								String status = result.getString("orderStatus");
								String flag = "";
								String title = getString(R.string.cover_order);
								switch (status) {
								case "01"://申请进场
									flag = "enter";
									break;
								case "05"://补单出场
									flag = "budan";
									title = getString(R.string.cover_budan);
									break;
								case "09"://申请出场
									flag = "leave";
									break;
								default:
									break;
								}
								Intent intent = new Intent("com.cover.receiver");
								intent.putExtra("title",title);
								intent.putExtra("order", result.getString("orderId"));
								intent.putExtra("flag",flag);
								sendBroadcast(intent);
							} else {
							}

						} catch (JSONException e) {
							
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				LogUtil.e("获取申请的订单"+Urls.CHECK_CLOCK, StringUtils.getMap(map));
				return map;
			}
		};
		BaseApplication.mQueue.add(register);
	}
	
}