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
import com.sunrise.epark.util.StringUtils;

/**
 * 覆盖层activity
 * 
 * @author 毛卫军
 *
 */
public class ManagerCoverLayer extends BaseActivity implements OnClickListener {

	private Button cancel;
	private TextView titleView;
	private String title;
	private String order;
	private TextView orderView;
	private TextView tv_manager;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_park_cover);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		order = intent.getStringExtra("order");
		initCom();

	}

	private void initCom() {
		titleView = (TextView) findViewById(R.id.cover_title);
		orderView = (TextView) findViewById(R.id.order_num);
		tv_manager=(TextView) findViewById(R.id.tv_manager);
		titleView.setText("提示");
		orderView.setText("支付中，请稍后");
		tv_manager.setVisibility(View.GONE);
		cancel = (Button) findViewById(R.id.enter_cancel);
			//补单
		cancel.setVisibility(View.VISIBLE);
		cancel.setOnClickListener(this);
		url = Urls.MANAGER_CANCLE_ORDER;
		
		
		
	}

	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		switch (id) {
		case R.id.enter_cancel:
			cancelOrder();
			break;

		default:
			break;
		}
		finish();
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
						LogUtil.e("线下支付", response);

						try {
							JSONObject object = new JSONObject(response);

							String code = object.getString("code");
						
							switch (code) {
							case "000":
								//成功
								finish();
								break;
							case "010":
								DialogUtil.loginAgain(ManagerCoverLayer.this);
							default:
								showShortToast(object.getString("result"));
								break;
							}

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
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("orderId", order);
				LogUtil.e("订单已取消：" + Urls.MANAGER_CANCLE_ORDER,
						StringUtils.getMap(map));
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}
	
}
