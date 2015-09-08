package com.sunrise.epark.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
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
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 停车场管理员之订单记录
* 文件名: OrderRecordActivity.java
* 描述: 
* 公司: Camelot
* @author zhongjy
* @date 2015年7月29日
* @version 1.0
 */
public class OrderRecordActivity extends BaseActivity implements OnClickListener,OnRefreshListener<ListView>,OnItemClickListener {

	/**返回 */
	private LinearLayout back;
	/**日期*/
	private TextView date;
	/** 日期减 */
	private LinearLayout dateSubtract;
	/** 日期加 */
	private LinearLayout dateAdd;

	private PullToRefreshListView orderListView;

	private ArrayList<OrderRecord> orderList;

	private QuickAdapter<OrderRecord> orderAdapter;
	
	private int pageSize=20;
	
	private int currentPage=1;
	
	private RequestQueue mQueue;
	
	private Dialog loading;

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_record);
		initView();
		initListView();
		bindEvent();
		orderAdapter = new QuickAdapter<OrderRecord>(this,
				R.layout.item_order_record, orderList) {
			@Override
			protected void convert(BaseAdapterHelper arg0, OrderRecord bean) {
				arg0.setText(R.id.order_record_num, bean.getOrderNum());
				arg0.setText(R.id.order_record_date, bean.getOrderDate());
			}
		};
		orderListView.setAdapter(orderAdapter);
		initData();

	}

	/**
	 * 初始化listview
	 */
	private void initListView() {
		loading=DialogUtil.createLoadingDialog(this, getString(R.string.loading));
		mQueue = Volley.newRequestQueue(this);
		orderListView.setMode(Mode.PULL_UP_TO_REFRESH);
		ILoadingLayout endlabel = orderListView.getLoadingLayoutProxy(false,
				true);
		endlabel.setPullLabel(getResources().getString(R.string.load_more));
		endlabel.setRefreshingLabel(getResources().getString(R.string.loading));
		endlabel.setReleaseLabel(getResources().getString(R.string.preload));

		ILoadingLayout startlable = orderListView.getLoadingLayoutProxy(true,
				false);
		startlable.setPullLabel(getResources().getString(R.string.refresh));
		startlable.setRefreshingLabel(getResources().getString(
				R.string.refreshing));
		startlable.setReleaseLabel(getResources()
				.getString(R.string.prerefresh));
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		orderList = new ArrayList<OrderRecord>();
		date.setText(dateFormat.format(new Date()));
		GetOrderList(dateFormat.format(new Date()));
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		back = (LinearLayout) this.findViewById(R.id.order_record_back);
		dateSubtract = (LinearLayout) this
				.findViewById(R.id.order_record_subtract);
		dateAdd = (LinearLayout) this.findViewById(R.id.order_record_add);
		orderListView = (PullToRefreshListView) this
				.findViewById(R.id.order_record_list);
		date = (TextView) this.findViewById(R.id.order_record_date);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		back.setOnClickListener(this);
		dateSubtract.setOnClickListener(this);
		dateAdd.setOnClickListener(this);
		orderListView.setOnRefreshListener(this);
		orderListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_record_back:
			this.finish();
			break;
		case R.id.order_record_add:
			date.setText(getDay(date.getText().toString().trim(), 1));
			currentPage=1;
			GetOrderList(date.getText().toString().trim());
			break;
		case R.id.order_record_subtract:
			date.setText(getDay(date.getText().toString().trim(), 0));
			currentPage=1;
			GetOrderList(date.getText().toString().trim());
			break;
		default:
			break;
		}
	}
	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			GetOrderList(date.getText().toString().trim());
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(
					OrderRecordActivity.this,
					System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					label);
			currentPage=1;
			GetOrderList(date.getText().toString().trim());
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		GetOrderDetail(orderList.get(position-1).getOrderNum());
	}


    public void GetOrderList(final String date){
    	loading.show();
    	StringRequest getOrder = new StringRequest(Method.POST,
				Urls.ORDER_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("订单记录："+Urls.ORDER_LIST, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								if (array.length() == 0) {
									NoDetail();
								} else {
									if (array.length() == pageSize) {
										orderListView.setMode(Mode.BOTH);
									}
									if(currentPage==1){
										orderList.clear();
									}
									for (int i = 0; i < array.length(); i++) {
										JSONObject temp = array.getJSONObject(i);
										orderList.add(new OrderRecord(temp.getString("orderId"), temp.getString("creatTime")));
									}
									orderAdapter.replaceAll(orderList);
									orderAdapter.notifyDataSetChanged();
									orderListView.onRefreshComplete();
								}
							}
							if ("001".equals(code)) {
								NoDetail();
							}
							if("010".equals(code)){
								DialogUtil.loginAgain(OrderRecordActivity.this);
							}
						} catch (JSONException e) {
							NoDetail();
						}finally{
							if(loading.isShowing()){
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						NoDetail();
						if(loading.isShowing()){
							loading.dismiss();
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("parkId",UserInfo.parkId);
				map.put("creatTime", date);
				map.put("type", "7");
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("订单记录："+Urls.ORDER_LIST, StringUtils.getMap(map));
				return map;
			}
		};
		getOrder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		mQueue.add(getOrder);
    }
    
    private void GetOrderDetail(final String orderId) {
		loading.show();
		StringRequest getOrderDetail = new StringRequest(Method.POST,
				Urls.ORDER_DETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							LogUtil.e("订单详情", response);
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONObject object2 = new JSONObject(result);
								String orderId = object2.getString("orderId");
								String carName = object2.getString("carName");
								String userName = object2.getString("userName");
								String userMobile=object2.getString("userTel");
								String start=object2.getString("startTime");
								String end=object2.getString("endTime");

								Bundle bundle = new Bundle();
								bundle.putString("orderNum", orderId);
								bundle.putString("userName", userName);
								bundle.putString("userCarNum", carName);
								bundle.putString("userMobile", userMobile);
								bundle.putString("orderStartDate", start);
								bundle.putString("orderEndDate", end);
								
								Intent intent = new Intent(OrderRecordActivity.this,OrderDetailActivity.class);
								intent.putExtras(bundle);
								OrderRecordActivity.this.startActivity(intent);
							}
							if("010".equals(code)){
								DialogUtil.loginAgain(OrderRecordActivity.this);
							}/*else {
								NoDetail();
							}*/
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading.isShowing()) {
							loading.dismiss();
						}
						ToastUtil.show(OrderRecordActivity.this, VolleyErrorUtil.getMessage(error, OrderRecordActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				LogUtil.e("订单详情", StringUtils.getMap(map));
				return map;
			}
		};
		getOrderDetail.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		mQueue.add(getOrderDetail);
	}

	/**
	 * 订单记录实体类
	* 文件名: OrderRecordActivity.java
	* 描述: 
	* 公司: Camelot
	* @author zhongjy
	* @date 2015年7月29日
	* @version 1.0
	 */
	private class OrderRecord {
		private String orderNum;
		private String orderDate;

		public String getOrderNum() {
			return orderNum;
		}

		@SuppressWarnings("unused")
		public void setOrderNum(String orderNum) {
			this.orderNum = orderNum;
		}

		public String getOrderDate() {
			return orderDate;
		}

		@SuppressWarnings("unused")
		public void setOrderDate(String orderDate) {
			this.orderDate = orderDate;
		}

		@SuppressWarnings("unused")
		public OrderRecord() {
			super();
		}

		public OrderRecord(String orderNum, String orderDate) {
			super();
			this.orderNum = orderNum;
			this.orderDate = orderDate;
		}

	}

	/**
	 * 获得指定日期
	 * 
	 * @param specifiedDay
	 * @param type
	 *            0:获得指定日期前一天 1:获得指定日期后一天
	 * @return
	 */
	public static String getDay(String specifiedDay, int type) {

		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = dateFormat.parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		if (type == 1) {
			c.set(Calendar.DATE, day + 1);
		} else {
			c.set(Calendar.DATE, day - 1);
		}
		return dateFormat.format(c.getTime());
	}

	private void NoDetail(){
		orderListView.setMode(Mode.PULL_FROM_START);
		orderList.clear();
		orderAdapter.replaceAll(orderList);
		orderAdapter.notifyDataSetChanged();
		orderListView.onRefreshComplete();
		if(currentPage==1){
			ToastUtil.showShort(this, R.string.no_order);
		}
	}

	
	

}
