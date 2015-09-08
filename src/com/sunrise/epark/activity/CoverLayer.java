package com.sunrise.epark.activity;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
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

/**
 * 覆盖层activity
 * 
 * @author 毛卫军
 *
 */
public class CoverLayer extends BaseActivity implements OnClickListener {

	private Button cancel;
	private TextView titleView;
	private String title;
	private String order;
	private TextView orderView;
	private String flag;
	
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_park_cover);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		order = intent.getStringExtra("order");
		flag = intent.getStringExtra("flag");
		initCom();

	}

	private void initCom() {
		titleView = (TextView) findViewById(R.id.cover_title);
		orderView = (TextView) findViewById(R.id.order_num);
		titleView.setText(title);
		orderView.setText(order);
		cancel = (Button) findViewById(R.id.enter_cancel);
		if("leave".equals(flag))
		{
			cancel.setVisibility(View.INVISIBLE);
		}else if("enter".equals(flag)){
			//进场
			cancel.setVisibility(View.VISIBLE);
			cancel.setOnClickListener(this);
			url = Urls.ENTER_PARK_CANCEL;
		}else{
			//补单
			cancel.setVisibility(View.VISIBLE);
			cancel.setOnClickListener(this);
			url = Urls.ENTER_PARK_CANCEL;
		}
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// 统计次数
		cancelOrder();
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		return;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	private void cancelOrder()
	{
		
		StringRequest register = new StringRequest(Method.POST,
				url, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("取消进场", response);

						try {
							JSONObject object = new JSONObject(response);

							String code = object.getString("code");
						
							switch (code) {
							case "000":
								//成功
								finish();
								break;
							case "010":
								DialogUtil.loginAgain(CoverLayer.this);
							default:
								showShortToast(object.getString("result"));
								break;
							}
//							if ("000".equals(code.trim())) {
//								
//								
//							} else {
//								// 失败
//								showLoadingDialog("取消失败");
//								
//							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// 失败
							showLoadingDialog("取消失败");
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("orderId",order);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}
	
}
