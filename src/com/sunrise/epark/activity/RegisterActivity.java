package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
 * this class is used for...个人中心设置
 * 
 * @wangyingjie create
 * @time 2015年7月27日上午9:38:51
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
		OnTouchListener {

	private Dialog loadingDialog;
	private RelativeLayout back;
	private EditText userName;
	private EditText pwd1;
	private EditText pwd2;
	private Button register;
	private TextView stopdeal;// 停车协议

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		bindEvent();
	}

	private void initView() {
		loadingDialog = DialogUtil.createLoadingDialog(RegisterActivity.this,
				"正在注册请稍后...");
		back = (RelativeLayout) findViewById(R.id.back);
		register = (Button) findViewById(R.id.register);
		stopdeal = (TextView) findViewById(R.id.stopdeal);
		userName = (EditText) findViewById(R.id.username);
		pwd1 = (EditText) findViewById(R.id.pwd1);
		pwd2 = (EditText) findViewById(R.id.pwd2);
	}

	private void bindEvent() {
		back.setOnClickListener(this);
		register.setOnClickListener(this);
		stopdeal.setOnClickListener(this);
		pwd1.setOnTouchListener(this);
		pwd2.setOnTouchListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.register:
			String name = userName.getText().toString().trim();
			if (checkUsername(name)) {
				String p1 = pwd1.getText().toString().trim();
				if (checkPwd1(p1)) {
					String p2 = pwd2.getText().toString().trim();
					if (checkPwd2(p1, p2)) {
						register(name, p1);
					}
				}
			}
			break;
		case R.id.stopdeal:
			Intent stopdeal = new Intent(this, RegisterDealActivity.class);
			startActivity(stopdeal);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.pwd1:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				String name = userName.getText().toString().trim();
				checkUsername(name);
			}
			break;
		case R.id.pwd2:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				String name = userName.getText().toString().trim();
				if (checkUsername(name)) {
					String pwd = pwd1.getText().toString().trim();
					checkPwd1(pwd);
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean checkUsername(String name) {
		if (StringUtils.isNotNull(name)) {
			if (!StringUtils.isuser(name)) {
				DialogUtil.showDialog(RegisterActivity.this,
						getString(R.string.text_username), true, userName);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			} else {
				return true;
			}
		} else {
			DialogUtil.showDialog(RegisterActivity.this, "用户名不能为空", true,
					userName);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}

	private boolean checkPwd1(String pwd) {
		if (StringUtils.isNotNull(pwd)) {
			if (!StringUtils.ispwd(pwd)) {
				DialogUtil.showDialog(RegisterActivity.this,
						getString(R.string.text_pwd), true, pwd1);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			} else {
				return true;
			}
		} else {
			DialogUtil.showDialog(RegisterActivity.this, "密码不能为空", true, pwd1);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}

	private boolean checkPwd2(String p1, String p2) {
		if (StringUtils.isNotNull(p2)) {
			if (!StringUtils.ispwd(p2)) {
				DialogUtil.showDialog(RegisterActivity.this,
						getString(R.string.text_pwd), true, pwd2);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			} else {
				if (StringUtils.isNotNull(p1)) {
					if (p1.equals(p2)) {
						return true;
					} else {
						DialogUtil.showDialog(RegisterActivity.this,
								"密码和确认密码不一致", true, pwd2);
						return false;
					}
				} else {
					DialogUtil.showDialog(RegisterActivity.this,
							getString(R.string.text_pwd), true, pwd1);
					return false;
				}
			}
		} else {
			DialogUtil.showDialog(RegisterActivity.this, "密码不能为空", true, pwd2);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}

	/*** 注册 */
	private void register(final String name, final String pwd) {

		loadingDialog.show();
		StringRequest reg = new StringRequest(Method.POST,
				Urls.REGISTER_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("用户注册", response);
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						try {
							JSONObject jObject = new JSONObject(response);
							switch (jObject.getString("code")) {
							case "000":
								registerSuccess();
								jObject = jObject.getJSONObject("userResult");
								
								break;
							case "011":
								DialogUtil.showDialog(RegisterActivity.this,
										"该用户名已存在", true, userName);
                               break;
							default:
								DialogUtil.showDialog(RegisterActivity.this,
										"注册失败,请稍后重试", true);
								break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} 
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loadingDialog != null && loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						ToastUtil.showLong(RegisterActivity.this,
								VolleyErrorUtil.getMessage(error,
										RegisterActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("registerName", name);
				map.put("registerPassword", pwd);
				LogUtil.i("用户注册", StringUtils.getMap(map));
				return map;
			}
		};
		reg.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(reg);
	}

	private void registerSuccess() {
		Builder builder = new Builder(this);
		builder.setTitle("注册");
		builder.setMessage("注册成功！");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(RegisterActivity.this,
						LoginActivity.class));
				finish();
			}

		});
		builder.show();
	}
	
}
