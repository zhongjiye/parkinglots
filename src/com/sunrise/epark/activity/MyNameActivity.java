package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
 * this class is used for... 个人中心-我的信息-昵称
 * 
 * @wangyingjie create
 * @time 2015年7月22日上午9:34:21
 */
public class MyNameActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout back;
	private EditText nameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_name);
		nameEditText = (EditText) findViewById(R.id.name);
		nameEditText.setText(UserInfo.userName);
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			changeName();
			break;
		default:
			break;
		}
	}

	private void changeName() {
		final String name = nameEditText.getText().toString().trim();
		StringRequest reg = new StringRequest(Method.POST, Urls.CHANGE_NAME,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("修改姓名", response);
						try {
							JSONObject object = new JSONObject(response);
							if ("000".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"修改成功");
								StatusUtil.SaveName(getApplicationContext(),
										name);
								finish();
							} else if ("001".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查找不到数据，请重新输入");
							} else if ("002".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"超时");
							} else if ("003".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"请求数据不正确");
							} else if ("004".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("005".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("006".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"操作失败");
							} else if ("010".equals(object.getString("code"))) {
								DialogUtil.loginAgain(
										MyNameActivity.this);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							finish();
						}
						
					//	ToastUtil.showLong(MyNameActivity.this, response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(MyNameActivity.this, VolleyErrorUtil
								.getMessage(error, MyNameActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("userName", name);
				map.put("uuid", UserInfo.uuid);
				LogUtil.i("修改姓名", StringUtils.getMap(map));
				return map;
			}
		};
		reg.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(reg);
	}

}
