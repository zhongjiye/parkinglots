package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.R.integer;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
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

public class StopCarDetailActivity extends BaseActivity implements
		OnClickListener {
	private PullToRefreshListView listview;
	private QuickAdapter<HoldList> qAdapter;
	private List<HoldList> data;
	private String orderId;
	private String parkId;
	private TextView parkName;
	private TextView startTime;
	private TextView endTime;
	private TextView money;
	//private String type;

	private String extentPay = "00";//是否可以延长付费 00是 01bushi
	private Button leave_park, delay_pay;

	private JSONObject stopCarObject, payObject;

	private TextView textView1;

	private RelativeLayout back;
	
	/** loading框 */
	private Dialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stopcar_delay_pay);
		orderId = getIntent().getStringExtra("id");
	//	type = getIntent().getStringExtra("type");
		initCom();

	}

	private void initCom() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		loading.show();
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(this);
		parkName = (TextView) findViewById(R.id.stopcar);
		startTime = (TextView) findViewById(R.id.textView3);
		endTime = (TextView) findViewById(R.id.textView2);
		money = (TextView) findViewById(R.id.pay_money);
		leave_park = (Button) findViewById(R.id.leave_park);
		leave_park.setOnClickListener(this);
		delay_pay = (Button) findViewById(R.id.delay_pay);
		delay_pay.setOnClickListener(this);
		//getdata();

		parkName = (TextView) findViewById(R.id.stopcar);
		startTime = (TextView) findViewById(R.id.textView3);
		endTime = (TextView) findViewById(R.id.textView2);
		money = (TextView) findViewById(R.id.pay_money);

		textView1 = (TextView) findViewById(R.id.textView1);

		data = new ArrayList<HoldList>();
		listview = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView1);
		listview.setVisibility(View.VISIBLE);
		listview.setMode(Mode.DISABLED);
		getdata(orderId);
		initList();
	}

	private void initList() {
		// 上拉 下拉 刷新
		listview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
					String label = DateUtils.formatDateTime(
							StopCarDetailActivity.this,
							System.currentTimeMillis(),
							DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
							label);
				}

			}
		});
		// item点击监听
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
		qAdapter = new QuickAdapter<HoldList>(this,
				R.layout.activity_stopcar_detail_item, data) {

			@Override
			protected void convert(BaseAdapterHelper v, final HoldList arg1) {
				// TODO Auto-generated method stub
				v.setText(R.id.time, arg1.startTime);
				v.setText(R.id.time1, arg1.endTime);
				v.setText(R.id.num1, arg1.subPayAmount);
			}
		};
		listview.setAdapter(qAdapter);
	}

	private void getdata(final String orderId) {
		
		StringRequest stopCar = new StringRequest(Method.POST,
				Urls.STOP_LIST_DETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("停车列表明细", response);
						try {
							JSONObject jObject = new JSONObject(response);
							String code = jObject.getString("code");
							switch (code) {
							case "000":
								// 成功
								// 获取所有的数据
								String result = jObject.getString("result");
								stopCarObject = new JSONObject(result);
								setText(stopCarObject);
								// 获取子数据集合
								String subList = stopCarObject
										.getString("subList");
								JSONArray array = new JSONArray(subList);
								LogUtil.d("停车列表详情", array.toString());
								data.clear();
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									HoldList list = new HoldList(obj
											.getString("subId"), obj
											.getString("orderId"), obj
											.getString("startTime"), obj
											.getString("endTime"), obj
											.getString("subPayAmount"), obj
											.getString("subStatus"));
									data.add(list);
								}
								if (data.size() == 0) {
									listview.setVisibility(View.GONE);
									textView1.setVisibility(View.GONE);
								} else {
									qAdapter.replaceAll(data);
									qAdapter.notifyDataSetChanged();
								}
								break;
							case "001":
								showShortToast(jObject.getString("result"));
								break;
							case "002":
								showShortToast(jObject.getString("result"));
								break;
							case "003":
								showShortToast(jObject.getString("result"));
								break;
							case "004":
								showShortToast(jObject.getString("result"));
								break;
							default:
								showShortToast(jObject.getString("result"));
								break;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (loading.isShowing()) {
								loading.dismiss();
							}
							// 刷新完成
							listview.onRefreshComplete();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading.isShowing()) {
							loading.dismiss();
						}
						showLoadingDialog("请求失败");
						ToastUtil.showLong(StopCarDetailActivity.this,
								VolleyErrorUtil.getMessage(error,
										StopCarDetailActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				return map;
			}
		};
		stopCar.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(stopCar);

	}

	private void setText(JSONObject object) {
		try {
			if ("04".equals(object.getString("orderStatus"))) {
				listview.setVisibility(View.GONE);
				leave_park.setVisibility(View.GONE);
				delay_pay.setVisibility(View.GONE);
			}
			parkName.setText(object.getString("parkName"));
			startTime.setText(object.getString("startTime"));
			if ("".equals(object.getString("endTime"))) {
				endTime.setText("待定");
			} else {
				endTime.setText(object.getString("endTime"));
			}
			money.setText(object.getString("orderAmount")+"元");
			extentPay = object.getString("extendPay");
			if("01".equals(extentPay)){//不可延长付费
				delay_pay.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class HoldList {
		String subId;
		String orderId;
		String startTime;
		String endTime;
		String subPayAmount;
		String subStatus;

		public HoldList() {
		}

		public HoldList(String orderId, String startTime, String endTime,
				String subPayAmount, String subStatus) {
			this.orderId = orderId;
			this.startTime = startTime;
			this.endTime = endTime;
			this.subPayAmount = subPayAmount;
			this.subStatus = subStatus;
		}

		public HoldList(String subId, String orderId, String startTime,
				String endTime, String subPayAmount, String subStatus) {
			super();
			this.subId = subId;
			this.orderId = orderId;
			this.startTime = startTime;
			this.endTime = endTime;
			this.subPayAmount = subPayAmount;
			this.subStatus = subStatus;
		}

		public String getSubId() {
			return subId;
		}

		public void setSubId(String subId) {
			this.subId = subId;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getSubPayAmount() {
			return subPayAmount;
		}

		public void setSubPayAmount(String subPayAmount) {
			this.subPayAmount = subPayAmount;
		}

		public String getSubStatus() {
			return subStatus;
		}

		public void setSubStatus(String subStatus) {
			this.subStatus = subStatus;
		}

	}

	public static boolean countStatu=true;
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.leave_park:
			showLoadingDialog();
			LeavePark();
			break;
		case R.id.back:
			finish();
			break;
		// 延长付费
		case R.id.delay_pay:
			try {
				if (countStatu) {
					
					pay();
				}
				countStatu=false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 延长续费
	 */
	private void pay() {
		StringRequest delayPay = new StringRequest(Method.POST, Urls.DELAY_PAY,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						LogUtil.d("延长续费", response.toString());
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String result = object.getString("result");
								payObject = new JSONObject(result);
								LogUtil.d("延长付费。。。", result);
								setPay(payObject);
								break;
							case "001":
								showShortToast(object.getString("result"));
								break;
							case "002":
								showShortToast(object.getString("result"));
								break;
							case "003":
								showShortToast(object.getString("result"));
								break;
							case "004":
								showShortToast(object.getString("result"));
								break;
							default:
								showShortToast(object.getString("result"));
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showLoadingDialog("请求失败");
						ToastUtil.showLong(StopCarDetailActivity.this,
								VolleyErrorUtil.getMessage(error,
										StopCarDetailActivity.this));

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				return map;
			}
		};
		delayPay.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(delayPay);
	}

	private void setPay(JSONObject result) {
		try {
			Intent payIntent = new Intent(StopCarDetailActivity.this,
					PayMainActivity.class);
			payIntent.putExtra("name", result.getString("parkName"));
			String sub = result.getString("subOrderDTO");
			JSONObject obj = new JSONObject(sub);
			payIntent.putExtra("orderId", obj.getString("subId"));
			payIntent.putExtra("mainId", obj.getString("orderId"));
			
			if ("".equals(obj)) {
				String type="00";//主单
				payIntent.putExtra("type",type);
			}else {
				String type="01";//子单
				payIntent.putExtra("type",type);
			}
			payIntent.putExtra("money",
					obj.getString("subPayAmount"));
			BaseApplication.className="delayPay";
			startActivityForResult(payIntent, 112);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/*
	 * 回调加载数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent datas) {
		super.onActivityResult(requestCode, resultCode, datas);
		if (resultCode == 113) {
			if (requestCode == 112) {
				try {
					countStatu=true;
					showPaySuccessDialog("支付成功");
					// 获取值
					Bundle bundle = datas.getExtras();
					//String money = bundle.getString("money");
					String mainId = bundle.getString("orderId");
					/*String subList = payObject.getString("subList");
					JSONArray array = new JSONArray(subList);
					HoldList list = new HoldList(
							array.getJSONObject(0).getString("subId"),
							array.getJSONObject(0).getString("startTime"),
							array.getJSONObject(0).getString("endTime"), 
							money, 
							array.getJSONObject(0).getString("subStatus"));
					data.add(list);
					LogUtil.d("延长付费。。。", data.size() + "......");
					listview.setVisibility(View.VISIBLE);
					textView1.setVisibility(View.VISIBLE);*/
					getdata(mainId);
					initList();
					/*qAdapter.replaceAll(data);
					qAdapter.notifyDataSetChanged();
					listview.onRefreshComplete();*/

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
				//	hidePayDialog();
				}

			}
		}
	}

	/** 确定出场 */
	private void LeavePark() {
		StringRequest register = new StringRequest(Method.POST, Urls.PARK_OUT,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("出场", response);
						try {
							JSONObject jObject = new JSONObject(response);
							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								Intent intent = new Intent(
										StopCarDetailActivity.this,
										CoverLayer.class);
								intent.putExtra("title",
										getString(R.string.cover_order));
								intent.putExtra("flag", "leave");
								intent.putExtra("order", orderId);
								startActivity(intent);
							} else if ("003".equals(code.trim())) {
								showShortToast(jObject.getString("result"));
							} else if ("004".equals(code.trim())) {
								showShortToast(jObject.getString("result"));
							} else if ("010".equals(code.trim())) {
								DialogUtil.loginAgain(getApplicationContext());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();
						ToastUtil.showLong(StopCarDetailActivity.this,
								VolleyErrorUtil.getMessage(error,
										StopCarDetailActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				// map.put("parkId", parkId);
				map.put("orderId", orderId);
				LogUtil.i("我要出场", StringUtils.getMap(map));
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}
}
