package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
/***
 * 
 *this class is used for...登陆-忘记密码
 * @wangyingjie  create
 *@time 2015年7月21日上午9:32:01
 */
public class ForgetPwdActivity extends BaseActivity implements OnClickListener{
	 /**Volley请求队列*/
	   private Dialog loadingDialog;
	   private RelativeLayout back;  /**返回*/
	   private EditText username;/**用户名*/
	   private Button confrim;/**确定*/
	   
	   /**弹出框*/
	   private  TextView tv;
	   private  TextView tv1;
	   private TextView tv2;
	   private Button yes;
	   private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		username=(EditText) findViewById(R.id.username);
		confrim=(Button) findViewById(R.id.confrim);
		initview();
	}
	private void initview() {
		back=(RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		confrim=(Button) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.confrim:
			ChangePwdActivity();
			break;
		default:
			break;
		}
		
	}
	@SuppressLint("ResourceAsColor")
	private void ChangePwdActivity() {
		final String LoginName=username.getText().toString().trim();
		StringRequest register = new StringRequest(Method.POST, Urls.FORGET_PWD,new Response.Listener<String>() {  
            @Override  
            public void onResponse(String response) {
            	LogUtil.i("忘记密码", response);
            	try {
					JSONObject object = new JSONObject(response);
					if ("000".equals(object.getString("code"))) {
						dialog = cancelDialog(ForgetPwdActivity.this);
						dialog.show();
					}else if("001".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"查询不到数据");
					}else if("002".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"超时");
					}else if("003".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"请求数据不正确");
					}else if("004".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"程序执行异常");
					}else if("005".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"其他");
					}else if("006".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"操作失败");
					}else if("008".equals(object.getString("code"))){
						DialogUtil.showToast(getApplicationContext(),"未填写用户信息");
					} else if ("007".equals(object.getString("code"))) {
						DialogUtil.showToast(getApplicationContext(),"停车管理员");
						/**让管理员修改密码*/
						dialog = cancelDialog1(ForgetPwdActivity.this);
						dialog.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
//            	ToastUtil.showLong(ForgetPwdActivity.this, response);
            }
            		/**弹出提示发送邮箱框*/
			
        }, new Response.ErrorListener() {  
            @Override  
            public void onErrorResponse(VolleyError error) {  
            	ToastUtil.showLong(ForgetPwdActivity.this, 
            			VolleyErrorUtil.getMessage(error, ForgetPwdActivity.this));
            }  
        }) {  
		    @Override  
		    protected Map<String, String> getParams() throws AuthFailureError {  
		        Map<String, String> map = new HashMap<String, String>();  
		        map.put("LoginName", LoginName ); // "lwl"
		        LogUtil.i("忘记密码",StringUtils.getMap(map));
		        return map;  
		    }  
		};  
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
	   BaseApplication.mQueue.add(register);
	}
	
	/**
	 * 弹出确认取消对话框
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog cancelDialog(Context context) {
		Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.send_email_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) popView.findViewById(R.id.tv2);
		/** 确定 */
		 yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ForgetPwdActivity.this,LoginActivity.class);
    			startActivity(intent);
			}
		});
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}
	
	
	/**
	 * 弹出确认取消对话框
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog cancelDialog1(Context context) {
		Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.send_email_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) popView.findViewById(R.id.tv2);
	// 赋值
		tv1.setText("邮件已发送至后台管理员");
		 tv2.setText("请致电咨询");
		/** 确定 */
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
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
