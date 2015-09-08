package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.PreferenceUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash);
		new task().execute();
	}
	
	/**
	 * 异步任务   执行检查更新等。。
	 * @author Rocky
	 *
	 */
	class task extends AsyncTask<Void, Void, Integer>{
		/**
		 * 执行线程 ：更新操作
		 */
		@Override
		protected Integer doInBackground(Void... arg0) {
			int result = 0;
			//判断是否有未取消或者出场的订单
			
			try {
				StatusUtil.init(getApplicationContext());
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			publishProgress();
			return result;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		//执行完成
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			//跳转到主页
			//startActivity(new Intent(MainActivity.this, LocationActivity.class));
			//判断是否有未取消或者出场的订单
			/*Intent intent = new Intent("com.cover.receiver");
			intent.putExtra("title",getString(R.string.cover_order));
			intent.putExtra("order", "2015-08-05 10:53:44");
			
			sendBroadcast(intent);*/
			
			MainActivity.this.finish();

			if(validate()){
				Intent intent = new Intent(MainActivity.this,ShowActivity.class);
				startActivity(intent);
			}else{
				if("login".equals(UserInfo.loginStatus)||"register".equals(UserInfo.loginStatus)){
					switch (UserInfo.userRole) {
					case "00":
						startActivity(new Intent(MainActivity.this, LocationActivity.class));
						checkClock();
						break;
					case "01":
						startActivity(new Intent(MainActivity.this, CarManageActivity.class));
						break;
					default:
						startActivity(new Intent(MainActivity.this, LocationActivity.class));
						checkClock();
						break;
					}
				}else{
					startActivity(new Intent(MainActivity.this, LocationActivity.class));
					
				}
			}
			finish();

		}
	}
	
	/**
	 * 验证是否是第一次打开app
	 * 
	 * @return
	 */
	private boolean validate(){
		PreferenceUtil SPUtils = PreferenceUtil.getInstance(getApplicationContext());
		boolean f = true;
		f =  SPUtils.getFirst();
		if(f){
			SPUtils.setFirst(false);
		}
		return f;
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
				LogUtil.i("获取申请的订单"+Urls.CHECK_CLOCK, StringUtils.getMap(map));
				return map;
			}
		};
		BaseApplication.mQueue.add(register);
	}
}
