package com.sunrise.epark.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.activity.CarInConfirmActivity;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.CarIn;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 描述: 停车场管理员-未进场列表
 * 
 * @author zhongjy
 * @date 2015年8月17日
 * @version 1.0
 */
public class CarInFragment extends Fragment implements
		OnRefreshListener<ListView>, OnItemClickListener {

	/** 适配器 */
	private QuickAdapter<CarIn> carInAdapter;
	/** listview */
	private PullToRefreshListView carInListview;
	/** 数据集合 */
	private ArrayList<CarIn> carInList;
	/** loading框 */
	private Dialog loading;
	/** 每页请求数据量 */
	private static int pageSize = 20;
	/** 当前页 */
	private static int currentPage = 1;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_car_in, container, false);
		initListView(view);
		bindEvent();
		initData();
		return view;
	}

	/**
	 * 当前fragment可见时刷新数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (isVisible()) {
			currentPage = 1;
			getCarInList();
		}
	}

	/**
	 * 初始化listview
	 * 
	 * @param view
	 */
	private void initListView(View view) {
		loading = DialogUtil.createLoadingDialog(getActivity(),
				getString(R.string.loading));

		carInListview = (PullToRefreshListView) view
				.findViewById(R.id.car_in_list);

		carInListview.setMode(Mode.PULL_FROM_START);

		ILoadingLayout endlabel = carInListview.getLoadingLayoutProxy(false,
				true);
		endlabel.setPullLabel(getString(R.string.load_more));
		endlabel.setRefreshingLabel(getString(R.string.loading));
		endlabel.setReleaseLabel(getString(R.string.preload));

		ILoadingLayout startlable = carInListview.getLoadingLayoutProxy(true,
				false);
		startlable.setPullLabel(getString(R.string.refresh));
		startlable.setRefreshingLabel(getString(R.string.refreshing));
		startlable.setReleaseLabel(getString(R.string.prerefresh));

		carInListview.setRefreshing(false);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		carInListview.setOnRefreshListener(this);
		carInListview.setOnItemClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		carInList = new ArrayList<CarIn>();
		carInAdapter = new QuickAdapter<CarIn>(getActivity(),
				R.layout.item_car_in, carInList) {
			@Override
			protected void convert(BaseAdapterHelper arg0, CarIn bean) {
				arg0.setText(R.id.car_order_num, bean.getOrderId());
				arg0.setText(R.id.car_num, bean.getCarName());
				arg0.setText(R.id.order_date, bean.getStartTime());
			}
		};
		carInListview.setAdapter(carInAdapter);
	}

	/**
	 * 下拉刷新与上拉加载更多
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			getCarInList();
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			currentPage = 1;
			getCarInList();
		}
	}

	/**
	 * 未进场列表-->详情
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LogUtil.i("未进场列表-->详情", "订单号:"
				+ carInList.get(position - 1).getOrderId());
		GetOrderDetail(carInList.get(position - 1).getOrderId());
	}

	/**
	 * 获取未进场列表
	 */
	public void getCarInList() {
		StringRequest getOrder = new StringRequest(Method.POST,
				Urls.ORDER_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("未进场列表:"+Urls.ORDER_LIST, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								if (array.length() == 0) {
									carInListview.setMode(Mode.PULL_FROM_START);
								} else {
									if (currentPage == 1) {
										carInList.clear();
									}
									if (array.length() == pageSize) {
										carInListview.setMode(Mode.BOTH);
									}
									for (int i = 0; i < array.length(); i++) {
										JSONObject temp = array
												.getJSONObject(i);
										carInList.add(new CarIn(temp
												.getString("orderId"), temp
												.getString("carName"), temp
												.getString("startTime")));
									}
								}
							}
							if ("001".equals(code)) {
								carInListview.setMode(Mode.PULL_FROM_START);
								carInList.clear();
							}
							if ("010".equals(code)) {
								DialogUtil.loginAgain(getActivity());
							}
						} catch (JSONException e) {
							carInListview.setMode(Mode.PULL_FROM_START);
							carInList.clear();
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
							carInAdapter.replaceAll(carInList);
							carInAdapter.notifyDataSetChanged();
							carInListview.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showShort(getActivity(), VolleyErrorUtil
								.getMessage(error, getActivity()));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("parkId", UserInfo.parkId);
				map.put("orderStatus", "01");
				map.put("orderType", "02");
				map.put("type", "5");
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("未进场列表:"+Urls.ORDER_LIST, StringUtils.getMap(map));
				return map;
			}
		};
		getOrder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getOrder);
	}

	/**
	 * 获取订单详情
	 * 
	 * @param orderId
	 */
	private void GetOrderDetail(final String orderId) {
		loading.show();
		StringRequest getOrderDetail = new StringRequest(Method.POST,
				Urls.ORDER_DETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							LogUtil.i("未进订单详情", response);
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String result = object.getString("result");
								JSONObject object2 = new JSONObject(result);
								String orderId = object2.getString("orderId");
								String carName = object2.getString("carName");
								String userName = object2.getString("userName");
								String userMobile = object2
										.getString("userTel");
								Bundle bundle = new Bundle();
								if (StringUtils.isNotNull(orderId)
										&& StringUtils.isNotNull(userName)
										&& StringUtils.isNotNull(carName)
										&& StringUtils.isNotNull(userMobile))

								{
									bundle.putString("orderNum", orderId);
									bundle.putString("userName", userName);
									bundle.putString("userCarNum", carName);
									bundle.putString("userMobile", userMobile);
									Intent intent = new Intent(getActivity(),
											CarInConfirmActivity.class);
									intent.putExtras(bundle);
									getActivity().startActivity(intent);
								} else {
									ToastUtil.showShort(getActivity(),
											"未进订单数据不完整");
								}
								break;

							case "001":
								NoDetail();
								break;
							case "010":
								DialogUtil.loginAgain(getActivity());
								break;
							default:
								NoDetail();
								break;
							}
						} catch (JSONException e) {
							NoDetail();
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
						ToastUtil.show(getActivity(), VolleyErrorUtil
								.getMessage(error, getActivity()));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				LogUtil.i("未进订单详情", StringUtils.getMap(map));
				return map;
			}
		};
		getOrderDetail
				.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getOrderDetail);
	}

	/**
	 * 无订单详情
	 */
	private void NoDetail() {
		ToastUtil.showLong(getActivity(), getString(R.string.no_order_detail));
	}

}
