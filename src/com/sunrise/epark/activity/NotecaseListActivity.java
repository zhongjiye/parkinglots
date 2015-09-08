package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.Bill;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;

/***
 * 
 * this class is used for...个人中心-钱包余额-账单明细
 * 
 * @wangyingjie create
 * @time 2015年7月27日上午9:37:25
 */
public class NotecaseListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnRefreshListener<ListView> {
	private RelativeLayout back;
	private PullToRefreshListView listView;
	private QuickAdapter<Bill> adapter;
	private ArrayList<Bill> list;
	private Dialog loading;
	private ImageView no_pay_detail;
	private int currentPage = 1;
	private int pageSize = 10;
	/** 灰色 */
	private int green = android.graphics.Color.parseColor("#4DC060");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notecase_list);
		initView();
		bindEvent();
		initData();
	}

	private void initView() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		back = (RelativeLayout) findViewById(R.id.back);
		listView = (PullToRefreshListView) findViewById(R.id.bill_list);
		no_pay_detail=(ImageView)findViewById(R.id.no_pay_detail);
	}

	private void bindEvent() {
		back.setOnClickListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(this);
	}

	private void initData() {
		list = new ArrayList<Bill>();
		adapter = new QuickAdapter<Bill>(NotecaseListActivity.this,
				R.layout.item_bill_list, list) {
			@Override
			protected void convert(BaseAdapterHelper arg0, Bill bill) {
				arg0.setText(R.id.order_type, bill.getTypeName());
				arg0.setText(R.id.order_date, bill.getDate());
				arg0.setText(R.id.order_num, bill.getNum());

				if ("00".equals(bill.getFlag())) {
					arg0.setText(R.id.order_money, "+" + bill.getMoney());
					((TextView) arg0.getView(R.id.order_money))
							.setTextColor(green);
					((TextView) arg0.getView(R.id.order_yuan_text))
							.setTextColor(green);
				} else {
					arg0.setText(R.id.order_money, "-" + bill.getMoney());
					((TextView) arg0.getView(R.id.order_money))
							.setTextColor(Color.RED);
					((TextView) arg0.getView(R.id.order_yuan_text))
							.setTextColor(Color.RED);
				}

				if (!"".equals(bill.getRemark())) {
					arg0.getView(R.id.order_remark).setVisibility(View.VISIBLE);
					arg0.setText(R.id.order_remark, bill.getRemark());
				} else {
					arg0.getView(R.id.order_remark).setVisibility(View.GONE);
				}
			}
		};
		listView.setAdapter(adapter);
		loading.show();
		getData();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			getData();
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(NotecaseListActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			currentPage = 1;
			getData();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LogUtil.i("账单列表-->账单详情", "payId:" + list.get(position - 1).getPayId());
		Bundle bundle = new Bundle();
		bundle.putString("payId", list.get(position - 1).getPayId());
		bundle.putString("payType", list.get(position - 1).getType());
		bundle.putString("payAmount", list.get(position - 1).getMoney());
		bundle.putString("payLogo", list.get(position - 1).getFlag());
		Intent intent = new Intent(this, NotecaseDetailActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void getData() {
		StringRequest request = new StringRequest(Method.POST,
				Urls.LIST_PAYDETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("账单明细"+Urls.LIST_PAYDETAIL, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								LogUtil.d("dddd", result);
								JSONArray array = new JSONArray(result);
								if (currentPage == 1) {
									list.clear();
								}
								if (array.length() == pageSize) {
									listView.setMode(Mode.BOTH);
								}
								if (array.length()==0) {
									no_pay_detail.setVisibility(View.VISIBLE);
									listView.setVisibility(View.GONE);
								}else {
									no_pay_detail.setVisibility(View.GONE);
									listView.setVisibility(View.VISIBLE);
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject temp = array.getJSONObject(i);
									String type = temp.getString("payType");
									switch (type) {
									case "00":
										type = "预约套餐支付";
										break;
									case "01":
										type = "停车出场支付";
										break;
									case "02":
										type = "取消预约套餐支付";
										break;
									case "03":
										type = "延长出场支付";
										break;
									case "04":
										type = "充值";
										break;
									default:
										type = "套餐选配";
										break;
									}
									list.add(new Bill(
											temp.getString("payType"), type,
											temp.getString("creatTimeString"),
											temp.getString("orderId"), temp
													.getString("payAmount"),
											temp.getString("remark"), temp
													.getString("payLogo"), temp
													.getString("payId")));
								}
							}
							if("010".equals(code)){
								DialogUtil.loginAgain(NotecaseListActivity.this);
							}
						} catch (JSONException e) {

						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
							adapter.replaceAll(list);
							adapter.notifyDataSetChanged();
							listView.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
						adapter.replaceAll(list);
						adapter.notifyDataSetChanged();
						listView.onRefreshComplete();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("账单明细", StringUtils.getMap(map));
				return map;
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(request);
	}
}
