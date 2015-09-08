package com.sunrise.epark.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.datedialog.DateTimePickerDialog;
import com.sunrise.epark.datedialog.DateTimePickerDialog.OnDateTimeSetListener;
import com.sunrise.epark.util.DateTool;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

public class PackageSelDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView park1;
	private TextView park2;
	private TextView car1;
	private TextView car2;
	private TextView tip;
	private TextView totel;

	private TextView start;
	private TextView end;
	private TextView start1;
	private TextView end1;
	private ImageView arrow;
	// 返回
	private RelativeLayout back;

	private Button sure;

	private String price1, price2;

	private String parkNames;
	Map<String, String> map = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.package_selection_detail);
		String jsonstr = getIntent().getStringExtra("data");

		initCom(jsonstr);
	}

	private void initCom(String jsonstr) {
		park1 = (TextView) findViewById(R.id.sel_detail_park1);
		park2 = (TextView) findViewById(R.id.sel_detail_park2);
		car1 = (TextView) findViewById(R.id.sel_detail_car1);
		car2 = (TextView) findViewById(R.id.sel_detail_car2);
		arrow = (ImageView) findViewById(R.id.sel_arrow);
		arrow.setOnClickListener(this);
		sure = (Button) findViewById(R.id.pay_sure);
		sure.setOnClickListener(this);

		tip = (TextView) findViewById(R.id.sel_detail_tip);
		totel = (TextView) findViewById(R.id.sel_detail_totle);

		start = (TextView) findViewById(R.id.sel_start);
		start.setOnClickListener(this);
		start1 = (TextView) findViewById(R.id.sel_start1);
		end = (TextView) findViewById(R.id.sel_end);
		end1 = (TextView) findViewById(R.id.sel_end1);

		car1.setText(UserInfo.carName);
		car2.setText(UserInfo.carName);

		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		try {
			JSONObject results = new JSONObject(jsonstr);

			JSONArray objarr = results.getJSONArray("subList");

			park1.setText(objarr.getJSONObject(0).getString("parkName"));
			park2.setText(objarr.getJSONObject(1).getString("parkName"));

			parkNames = objarr.getJSONObject(0).getString("parkName") + "\n"
					+ objarr.getJSONObject(1).getString("parkName");

			String parkids = objarr.getJSONObject(0).getString("parkId") + ","
					+ objarr.getJSONObject(1).getString("parkId");
			map.put("parkIds", parkids);
			car1.setText(UserInfo.carName);
			car2.setText(UserInfo.carName);

			price1 = objarr.getJSONObject(0).getString("packagePrice");
			price2 = objarr.getJSONObject(1).getString("packagePrice");
			String ss = "".equals(results.getString("packagePrice")) ? "0"
					: results.getString("packagePrice");

			map.put("price", ss);

			double sheng = (Double.valueOf(price1) * 5 + Double.valueOf(price2) * 2)
					- Double.valueOf(ss);
			price1 = getString(R.string.yuantian, price1);
			price2 = getString(R.string.yuantian, price2);
			if (sheng<=0.0) {
				sheng =0.0;
			}
			tip.setText(getString(R.string.sel_detail_tip, price1, price2,
					getString(R.string.how_yuan, String.valueOf(sheng))));
			totel.setText(ss);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;
		case R.id.sel_start:
			showTimeDialog(id);
			break;
		case R.id.sel_arrow:
			showTimeDialog(R.id.sel_start);
			break;
		case R.id.pay_sure:
			if ("".equals(start.getText())) {
				ToastUtil.show(getApplicationContext(), "请选择时间");
			} else {
				SurePackage();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 确认选配
	 */
	private void SurePackage() {
		showLoadingDialog("请求中");
		StringRequest getAmount = new StringRequest(Method.POST,
				Urls.MAKE_PACKAGE, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						LogUtil.d("提交套餐", response.toString());
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								JSONObject result = object
										.getJSONObject("result");
								String orderId = result.getString("orderId");
								String money = result.getString("orderAmount");
								Intent intent = new Intent(
										PackageSelDetailActivity.this,
										PayMainActivity.class);
								intent.putExtra("orderId", orderId);
								intent.putExtra("money", money);
								intent.putExtra("name", parkNames);
								intent.putExtra("type","00");
								BaseApplication.className = "yuYue";
								startActivity(intent);
								break;
							case "010":
								DialogUtil
										.loginAgain(PackageSelDetailActivity.this);
							default:
								showShortToast(object.getString("result"));
								break;
							}
							closeLoadingDialog();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showLoadingDialog("请求失败");
						ToastUtil.showLong(PackageSelDetailActivity.this,
								VolleyErrorUtil.getMessage(error,
										PackageSelDetailActivity.this));

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("startTime", start.getText().toString().split(" ")[0]);
				map.put("endTime", end1.getText().toString().split(" ")[0]);
				map.put("diffVal", "7");
				map.put("carName", UserInfo.carName);
				return map;
			}
		};
		getAmount.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getAmount);

	}

	/**
	 * 选择时间
	 * 
	 * @param v
	 */
	public void showTimeDialog(final int v) {
		DateTimePickerDialog dialog = new DateTimePickerDialog(this,
				System.currentTimeMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
			public void OnDateTimeSet(AlertDialog dialog, long date) {

				Calendar c = Calendar.getInstance();
				Date d = new Date(date);
				c.setTime(d);
				int week = c.get(Calendar.DAY_OF_WEEK) - 1;

				// 判断是否为星期一
				if (week != 1) {
					ToastUtil.show(getApplicationContext(), "套餐开始时间必须周一");
				} else if (!DateTool.beyondTody(d)) {// 不为今天
					ToastUtil.show(getApplicationContext(), "套餐开始时间不能小于今天");
				} else {
					start.setText(DateTool.getStringDate(date) + " "
							+ getString(R.string.week1));

					c.add(Calendar.DATE, 4);

					end.setText(DateTool.getStringDate(c.getTimeInMillis())
							+ " " + getString(R.string.week5));

					c.add(Calendar.DATE, 1);
					start1.setText(DateTool.getStringDate(c.getTimeInMillis())
							+ " " + getString(R.string.week6));

					c.add(Calendar.DATE, 1);
					end1.setText(DateTool.getStringDate(c.getTimeInMillis())
							+ " " + getString(R.string.week7));
				}

			}
		});
		dialog.show();
	}
}
