package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
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
 * 进场
 * 
 * @author mwj
 *
 */
public class EnterPark extends BaseActivity implements OnClickListener {

	private LinearLayout enter;
	private LinearLayout budan;

	private TextView name;
	private TextView type;
	private TextView address;
	private TextView price;
	private TextView shengyu;
	private TextView chepai;
	// 停车场id
	private String parkId;

	private RelativeLayout back;
	
	private ImageView img_center;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_park);
		parkId = getIntent().getStringExtra("parkId");
		initCom();
		initData();
	}

	// 初始化组建
	private void initCom() {
		enter = (LinearLayout) findViewById(R.id.enter_ok);
		budan = (LinearLayout) findViewById(R.id.enter_budan);

		name = (TextView) findViewById(R.id.enter_name);
		type = (TextView) findViewById(R.id.enter_type);
		address = (TextView) findViewById(R.id.enter_address);
		price = (TextView) findViewById(R.id.enter_price);
		shengyu = (TextView) findViewById(R.id.enter_shengyu);
		chepai = (TextView) findViewById(R.id.enter_chepai);
		chepai.setText(UserInfo.carName);

		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		enter.setOnClickListener(this);
		budan.setOnClickListener(this);
		img_center=(ImageView) findViewById(R.id.img_center);
	}

	private void initData() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.STOPCAR_MINGXI, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("进场信息", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								updateData(jObject);
							} else {
								// 失败
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				map.put("parkId", parkId);
				return map;
			}
		};

		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));

		BaseApplication.mQueue.add(register);

	}

	// 更新ui
	private void updateData(JSONObject jObject) throws JSONException {
		jObject = jObject.getJSONObject("result");
		name.setText(jObject.getString("parkName"));
		String str;
		if ("00".equals(jObject.getString("parkType"))) {
			str = "室内停车场";
		} else {
			str = "室外停车场";
		}
		type.setText(str);
		address.setText(jObject.getString("parkAddr"));
		price.setText(jObject.getString("nightPrice") + "-"
				+ jObject.getString("dayPrice") + "元");
		int remainPark = Integer.parseInt(jObject.getString("remainPark"));
		if (remainPark <= 0) {
			shengyu.setText("0个");
		} else {
			shengyu.setText(jObject.getString("remainPark") + "个");
		}
		String imgId = jObject.getString("imgUrl");
		if (StringUtils.isNotNull(imgId)) {
			@SuppressWarnings("deprecation")
			ImageRequest imageRequest = new ImageRequest(
					imgId,
					new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(
								Bitmap response) {
							img_center.setImageBitmap(response);
						}
					}, 0, 0, Config.RGB_565,
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(
								VolleyError error) {
						}
					});
			BaseApplication.mQueue.add(imageRequest);
			// ImageLoader imageLoader = new
			// ImageLoader(
			// BaseApplication.mQueue, new
			// BitmapCache());
			// ImageListener listener = ImageLoader
			// .getImageListener(
			// parkImg,
			// R.drawable.parkinglots_example,
			// R.drawable.parkinglots_example);
			// imageLoader.get(imgUrl, listener);
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		showLoadingDialog();
		switch (id) {
		case R.id.enter_ok:
			enterPark();

			break;
		case R.id.enter_budan:
			budan();
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 我要进场，产生订单号
	 */
	private void enterPark() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.ENTER_PARK, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("我要进场", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");

							switch (code) {
							case "000":
								String id = jObject.getJSONObject("result")
										.getString("orderId");
								Intent intent = new Intent(EnterPark.this,
										CoverLayer.class);
								intent.putExtra("title",
										getString(R.string.cover_order));
								intent.putExtra("flag", "enter");
								intent.putExtra("order", id);
								startActivity(intent);
								break;
							case "010":
								DialogUtil.loginAgain(EnterPark.this);
							default:
								showShortToast(jObject.getString("result"));
								break;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				map.put("parkId", parkId);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}

	/**
	 * 补单，产生订单号
	 */
	private void budan() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.BUDAN_SHENQING, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("我要补单", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");

							switch (code) {
							case "000":
								String id = jObject.getJSONObject("result")
										.getString("orderId");
								Intent intent = new Intent(EnterPark.this,
										CoverLayer.class);
								intent.putExtra("title",
										getString(R.string.cover_budan));
								intent.putExtra("flag", "budan");
								intent.putExtra("order", id);
								startActivity(intent);
								break;
							case "010":
								DialogUtil.loginAgain(EnterPark.this);
							default:
								showShortToast(jObject.getString("result"));
								break;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				map.put("parkId", parkId);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}
}
