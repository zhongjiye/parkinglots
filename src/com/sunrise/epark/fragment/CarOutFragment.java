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
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
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
import com.sunrise.epark.activity.CarOutAddConfirmActivity;
import com.sunrise.epark.activity.CarOutConfirmActivity;
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
 * 描述: 停车场管理员-在场列表
 * 
 * @author zhongjy
 * @date 2015年8月17日
 * @version 1.0
 */
public class CarOutFragment extends Fragment implements
		OnRefreshListener<ListView>, OnItemClickListener {
	/** 适配器 */
	private QuickAdapter<CarIn> carOutAdapter;
	/** listview */
	private PullToRefreshListView carOutListview;
	/** 数据集合 */
	private ArrayList<CarIn> carOutList;
	/** loading框 */
	private Dialog loading;
	/** 每页请求数据量 */
	private static int pageSize = 20;
	/** 当前页 */
	private static int currentPage = 1;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_car_not, container,
				false);
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
			getCarOutList();
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
		carOutListview = (PullToRefreshListView) view
				.findViewById(R.id.car_not_list);
		carOutListview.setMode(Mode.PULL_FROM_START);
		ILoadingLayout endlabel = carOutListview.getLoadingLayoutProxy(false,
				true);
		endlabel.setPullLabel(getString(R.string.load_more));
		endlabel.setRefreshingLabel(getString(R.string.loading));
		endlabel.setReleaseLabel(getString(R.string.preload));

		ILoadingLayout startlable = carOutListview.getLoadingLayoutProxy(true,
				false);
		startlable.setPullLabel(getString(R.string.refresh));
		startlable.setRefreshingLabel(getString(R.string.refreshing));
		startlable.setReleaseLabel(getString(R.string.prerefresh));

		carOutListview.setRefreshing(false);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		carOutListview.setOnRefreshListener(this);
		carOutListview.setOnItemClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		carOutList = new ArrayList<CarIn>();
		carOutAdapter = new QuickAdapter<CarIn>(getActivity(),
				R.layout.item_car_out, carOutList) {
			@Override
			protected void convert(BaseAdapterHelper arg0, CarIn bean) {
				arg0.setText(R.id.car_order_num, bean.getOrderId());
				arg0.setText(R.id.car_num, bean.getCarName());
				String start = bean.getStartTime();
				String starts = start.substring(start.indexOf("-") + 1);
				arg0.setText(R.id.order_date, starts);
				String orderType = "";
				if ("02".equals(bean.getOrderStatu())) {
					orderType = "进行中";
				}
				if ("05".equals(bean.getOrderStatu())) {
					orderType = "补单出场";
				}
				if ("09".equals(bean.getOrderStatu())) {
					orderType = "申请出场";
				}
				arg0.setText(R.id.car_order_statu, orderType);
			}
		};
		carOutListview.setAdapter(carOutAdapter);
	}

	/**
	 * 跳转到补单出场或正常出场确定
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LogUtil.i("在库订单-->订单详情", "订单ID："
				+ carOutList.get(position - 1).getOrderId());
		GetOrderDetail(carOutList.get(position - 1).getOrderId());
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			getCarOutList();
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			currentPage = 1;
			getCarOutList();
		}
	}

	/**
	 * 获取在库订单列表
	 */
	public void getCarOutList() {
		StringRequest getOrder = new StringRequest(Method.POST,
				Urls.ORDER_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("在场列表", response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								if (array.length() == 0) {
									carOutListview
											.setMode(Mode.PULL_FROM_START);
								} else {
									if (array.length() == pageSize) {
										carOutListview.setMode(Mode.BOTH);
									}
									if (currentPage == 1) {
										carOutList.clear();
									}
									for (int i = 0; i < array.length(); i++) {
										JSONObject temp = array
												.getJSONObject(i);
										carOutList.add(new CarIn(temp
												.getString("orderId"), temp
												.getString("carName"), temp
												.getString("startTime"), temp
												.getString("orderStatus")));
									}
									carOutAdapter.replaceAll(carOutList);
								}

								break;
							case "001":
								carOutListview.setMode(Mode.PULL_FROM_START);
								carOutList.clear();
								break;
							case "010":
								DialogUtil.loginAgain(getActivity());
								break;
							default:
								break;
							}
						} catch (JSONException e) {
							carOutListview.setMode(Mode.PULL_FROM_START);
							carOutList.clear();
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
							carOutAdapter.notifyDataSetChanged();
							carOutListview.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
						carOutAdapter.notifyDataSetChanged();
						carOutListview.onRefreshComplete();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("parkId", UserInfo.parkId);
				map.put("orderStatus", "02");
				map.put("orderType", "02");
				map.put("type", "6");
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("在场列表", StringUtils.getMap(map));
				return map;
			}
		};
		getOrder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getOrder);
	}

	private void GetOrderDetail(final String orderId) {
		loading.show();
		StringRequest getOrderDetail = new StringRequest(Method.POST,
				Urls.ORDER_DETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							LogUtil.i("订单详情", response);
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONObject object2 = new JSONObject(result);
								String orderId = object2.getString("orderId");
								String carName = object2.getString("carName");
								String orderType = object2
										.getString("orderStatus");
								String userName = object2.getString("userName");
								String userMobile = object2
										.getString("userTel");
								String start = object2.getString("startTime");

								Bundle bundle = new Bundle();
								bundle.putString("orderNum", orderId);
								bundle.putString("userName", userName);
								bundle.putString("userCarNum", carName);
								bundle.putString("userMobile", userMobile);
								bundle.putString("orderStartDate", start);
								Intent intent = null;
								switch (orderType) {
								case "05":
									intent = new Intent(getActivity(),
											CarOutAddConfirmActivity.class);
									break;
								case "09":
									intent = new Intent(getActivity(),
											CarOutConfirmActivity.class);
									break;
								case "02":
									bundle.putString("orderType", "running");
									intent = new Intent(getActivity(),
											CarOutConfirmActivity.class);
									break;
								}
								intent.putExtras(bundle);
								getActivity().startActivity(intent);
							}
							if ("010".equals(code)) {
								DialogUtil.loginAgain(getActivity());
							} /*
							 * else { NoDetail(); }
							 */
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
						ToastUtil.showShort(getActivity(), VolleyErrorUtil
								.getMessage(error, getActivity()));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				LogUtil.i("订单详情", StringUtils.getMap(map));
				return map;
			}
		};
		BaseApplication.mQueue.add(getOrderDetail);
	}

	/**
	 * 无订单详情
	 */
	private void NoDetail() {
		ToastUtil.showLong(getActivity(),
				getResources().getString(R.string.no_order_detail));
	}

}
