package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
/**
 * 
 *this class is used for...添加车牌
 * @wangyingjie  create
 *@time 2015年7月30日上午1:00:33
 */
public class MyCarAddActivity extends BaseActivity implements OnClickListener{
	 /**Volley请求队列*/
	   private RelativeLayout back;  /**返回*/
	   private EditText name;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_car_add);
		initview();
	}

	private void initview() {
		name=(EditText)findViewById(R.id.name);
		back=(RelativeLayout) findViewById(R.id.back);
			back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			savecarid();
			Intent data = new Intent();
			setResult(113, data);
			finish();
			break;

		default:
			break;
		}
		
	}
	/**增加车牌*/
	private void savecarid() {
		final String carName=name.getText().toString().trim(); 
		if (StringUtils.isCar(carName)) {
		StringRequest register = new StringRequest(Method.POST,
				Urls.ADD_CARID, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("增加车牌", response);
	        			try {
	    					JSONObject object=new JSONObject(response);
	    				if ("000".equals(object.getString("code"))) {
	    						DialogUtil.showToast(getApplicationContext(), "添加成功");
	    						StatusUtil.AddCar(getApplicationContext(), carName);
	    						
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
	    				} else if ("012".equals(object.getString("code"))) {
	    					DialogUtil.showToast(getApplicationContext(),"车牌号已存在,请重新添加");
	    				} else if ("010".equals(object.getString("code"))) {
	    					DialogUtil.loginAgain(MyCarAddActivity.this);
	    				}
	    				} catch (JSONException e) {
	    					e.printStackTrace();
	    				}		
//			              ToastUtil.showLong(MyCarAddActivity.this, response);			
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(MyCarAddActivity.this,
								VolleyErrorUtil.getMessage(error,
										MyCarAddActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("carName", carName);
				map.put("uuid", UserInfo.uuid);
				LogUtil.i("增加车辆map",StringUtils.getMap(map));
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
		}else{
			DialogUtil.showToast(getApplicationContext(),getString(R.string.text_carid));
		}
	}
}
