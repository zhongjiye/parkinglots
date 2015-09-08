package com.sunrise.epark.activity;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
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
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
/***
 * 
 *this class is used for...登陆-忘记密码-修改
 * @wangyingjie  create
 *@time 2015年7月21日上午9:31:09
 */
public class ChangePwdActivity extends BaseActivity implements OnClickListener,TextWatcher,OnTouchListener{
	   private Dialog loadingDialog;
	   private RelativeLayout back;  
	   private EditText pwdever;
	   private EditText pwd1;
	   private EditText pwd2;
	   private Button confrim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		back=(RelativeLayout) findViewById(R.id.back);
		pwd1=(EditText)findViewById(R.id.pwd1);
		pwd2=(EditText)findViewById(R.id.pwd2);
		pwdever=(EditText)findViewById(R.id.pwd_ever);
		confrim=(Button) findViewById(R.id.confrim);
		loadingDialog=DialogUtil.createLoadingDialog(ChangePwdActivity.this,"正在修改请稍后...");
		initview();
	}
	
	private void initview() {
			pwd1.addTextChangedListener(this);
			back.setOnClickListener(this);
			pwd2.addTextChangedListener(this);
			confrim.setOnClickListener(this);
			pwd1.setOnTouchListener(this);
			pwd2.setOnTouchListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.confrim:
			//ChangePwd();
			String pwdEver=pwdever.getText().toString().trim();
			if (checkUsername(pwdEver)) {
				String p1=pwd1.getText().toString().trim();
				if (checkPwd1(p1)) {
					String p2=pwd2.getText().toString().trim();
					if (checkPwd2(p1, p2)) {
						ChangePwd(p1);
					}
				}
			}
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
				String pwdEver=pwdever.getText().toString().trim();
				checkUsername(pwdEver);
			}
			break;
		case R.id.pwd2:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				String pwdEver=pwdever.getText().toString().trim();
				if (checkUsername(pwdEver)) {
				String  p1=pwd1.getText().toString().trim();
					checkPwd1(p1);
				}
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	
	
	
	private boolean checkUsername(String pwdEver) {
		if (!"".equals(pwdEver)) {
			System.out.println("pwdEver++pwdEver++++"+pwdEver);
			System.out.println("UserInfo.userPassWord++++++"+UserInfo.userPassWord);
			if (pwdEver.equals(UserInfo.userPassWord)) {
				return true;
			} else {
				DialogUtil.showDialog(ChangePwdActivity.this,
						"您的原密码不正确,请重新填写", true, pwdever);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			}
		} else {
			DialogUtil.showDialog(ChangePwdActivity.this, "原始密码不能为空", true,
					pwdever);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}
	
	private boolean checkPwd1(String p1) {
		if (StringUtils.isNotNull(p1)) {
			if (!StringUtils.ispwd(p1)) {
				DialogUtil.showDialog(ChangePwdActivity.this,
						getString(R.string.text_pwd), true, pwd1);
				if (getCurrentFocus() != null) {
					getCurrentFocus().clearFocus();
				}
				return false;
			} else {
				return true;
			}
		} else {
			DialogUtil.showDialog(ChangePwdActivity.this, "新密码不能为空", true, pwd1);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}
	
	private boolean checkPwd2(String p1, String p2) {
		if (StringUtils.isNotNull(p2)) {
			if (!StringUtils.ispwd(p2)) {
				DialogUtil.showDialog(ChangePwdActivity.this,
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
						DialogUtil.showDialog(ChangePwdActivity.this,
								"密码和确认密码不一致", true, pwd2);
						return false;
					}
				} else {
					DialogUtil.showDialog(ChangePwdActivity.this,
							getString(R.string.text_pwd), true, pwd1);
					return false;
				}
			}
		} else {
			DialogUtil.showDialog(ChangePwdActivity.this, "密码不能为空", true, pwd2);
			if (getCurrentFocus() != null) {
				getCurrentFocus().clearFocus();
			}
			return false;
		}
	}

	
	
	private void ChangePwd(final String p1) {
//		final String pwdEverString=pwdever.getText().toString().trim();
//		final String pwd1String=pwd1.getText().toString().trim();
//		final String pwd2String=pwd2.getText().toString().trim();
//		System.out.println("UserInfo.userPassWord+++++"+UserInfo.userPassWord);
//		System.out.println("pwdEverString111111111"+pwdEverString);
//		if (!"".equals(pwdEverString)) {
//	if (pwdEverString.equals(UserInfo.userPassWord)) {
//		if(pwd1String.length()>0&&pwd2String.length()>0){
//			if(pwd1String.equals(pwd2String)){
//				if(StringUtils.ispwd(pwd1String)){
					loadingDialog.show();
		StringRequest register = new StringRequest(Method.POST, Urls.CHANGE_PWD ,new Response.Listener<String>() {  
			@Override  
            public void onResponse(String response) {
            	LogUtil.e("重置密码", response);
    			try {
					JSONObject object=new JSONObject(response);
			if ("000".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(), "修改成功");
				Intent intent = new Intent(ChangePwdActivity.this,LoginActivity.class);
			    	startActivity(intent);
			}else if ("001".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(),"查找不到数据，请重新输入");
			} else if ("002".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(),"超时");
			} else if ("003".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(),	"请求数据不正确");
			} else if ("004".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(),"程序执行异常");
			} else if ("005".equals(object.getString("code"))) {
					DialogUtil.showToast(getApplicationContext(),"其他");
			} else if ("006".equals(object.getString("code"))) {
				DialogUtil.showToast(getApplicationContext(),"操作失败");
			}else if("010".equals(object.getString("code"))){
				DialogUtil.loginAgain(ChangePwdActivity.this);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}finally {
				if (loadingDialog != null&&loadingDialog.isShowing()) {
					loadingDialog.dismiss();}
			}	
//            	ToastUtil.showLong(ChangePwdActivity.this, response);
            }  
        }, new Response.ErrorListener() {  
            @Override  
            public void onErrorResponse(VolleyError error) {
            	ToastUtil.showLong(ChangePwdActivity.this, 
            			VolleyErrorUtil.getMessage(error, ChangePwdActivity.this));
				if (loadingDialog != null&&loadingDialog.isShowing()) {
					loadingDialog.dismiss();}
            }  
        }) {  
		    @Override  
		    protected Map<String, String> getParams() throws AuthFailureError {  
		        Map<String, String> map = new HashMap<String, String>();  
		        map.put("password", p1);
		        map.put("userId",UserInfo.userId);  
		        map.put("uuid",UserInfo.uuid); 
		        LogUtil.i("修改密码",StringUtils.getMap(map));
		        return map;  
		    }  
		};  
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
	   BaseApplication.mQueue.add(register);
//				}else{
//					DialogUtil.showToast(getApplicationContext(),getString(R.string.text_pwd));
//				}
//			}else{
//				DialogUtil.showToast(getApplicationContext(),getString(R.string.text_pwd_add));
//			}
//		}else{
//			DialogUtil.showToast(getApplicationContext(),getString(R.string.text_pwd_null));
//		}
//	}else {
//		DialogUtil.showToast(getApplicationContext(),"您的原密码不正确,请重新填写");
//	}
//		}else {
//			DialogUtil.showToast(getApplicationContext(),"请填写原始密码！");
//		}
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
		if (!"".equals(pwd1.getText().toString().trim())&&!"".equals(pwd2.getText().toString().trim())) {
			confrim.setBackgroundResource(R.drawable.button_round_sure_green);
		} else {
			confrim.setBackgroundResource(R.drawable.button_round_sure_gray);
		}
		
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}


}
