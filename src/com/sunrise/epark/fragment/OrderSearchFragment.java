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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import com.sunrise.epark.activity.CarOutAddConfirmActivity;
import com.sunrise.epark.activity.CarOutConfirmActivity;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.OrderDetail;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

public class OrderSearchFragment extends Fragment implements OnClickListener,
		OnRefreshListener<ListView>, OnItemClickListener, TextWatcher {

	private PullToRefreshListView orderListView;
	private QuickAdapter<OrderDetail> orderAdapter;
	/**
	 * 搜索按钮
	 */
	private Button orderSearch;
	/**
	 * 搜索框
	 */
	private EditText searchKey;
	/**
	 * 搜索结果集
	 */
	private ArrayList<OrderDetail> orderList;
	/**
	 * loading展示框
	 */
	private Dialog loading;
	/**
	 * 搜索结果暂存
	 */
	private String tempKey;
	/**
	 * 分页请求每页数量
	 */
	private int pageSize = 20;
	/**
	 * 当期页
	 */
	private int currentPage;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order_search, container,
				false);

		initView(view);

		initListView(view);

		bindEvent();

		orderAdapter = new QuickAdapter<OrderDetail>(getActivity(),
				R.layout.item_order_search, orderList) {
			@Override
			protected void convert(BaseAdapterHelper arg0, OrderDetail bean) {
				arg0.setText(R.id.order_search_num, bean.getOrderNum());
			}
		};
		orderListView.setAdapter(orderAdapter);

		return view;
	}

	/**
	 * 初始化View
	 * 
	 * @param view
	 */
	private void initView(View view) {
		orderList = new ArrayList<OrderDetail>();
		loading = DialogUtil.createLoadingDialog(getActivity(),
				getString(R.string.loading));
		orderSearch = (Button) view.findViewById(R.id.order_search);
		searchKey = (EditText) view.findViewById(R.id.order_search_key);
	}

	/**
	 * 初始化Listview
	 * 
	 * @param view
	 */
	private void initListView(View view) {
		orderListView = (PullToRefreshListView) view
				.findViewById(R.id.order_search_list);
		orderListView.setMode(Mode.DISABLED);
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
	 * 绑定事件
	 */
	private void bindEvent() {
		orderSearch.setOnClickListener(this);
		searchKey.addTextChangedListener(this);
		orderListView.setOnRefreshListener(this);
		orderListView.setOnItemClickListener(this);
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_search:
			tempKey = searchKey.getText().toString().trim();
			if (StringUtils.isNotNull(tempKey)) {
				loading.show();
				currentPage = 1;
				SearchOrder(tempKey);
			} else {
				//ToastUtil.showShort(getActivity(), "搜索关键字不能为空");
				loading.show();
				currentPage = 1;
				SearchOrder("");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新事件
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			if (!"".equals(tempKey) && tempKey != null) {
				currentPage++;
				SearchOrder(tempKey);
			} else {
				refreshView.onRefreshComplete();
			}
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			if (!"".equals(tempKey) && tempKey != null) {
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				currentPage = 1;
				SearchOrder(tempKey);
			} else {
				refreshView.onRefreshComplete();
			}
		}
	}

	/**
	 * listview条目点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		loading.show();
		GetOrderDetail(orderList.get(position - 1).getOrderNum());
	}

	/**
	 * 搜索框内容变化监听
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		tempKey = null;
		orderList.clear();
		orderAdapter.clear();
		orderAdapter.notifyDataSetChanged();
	}

	/**
	 * 搜索框内容变化监听
	 */
	@Override
	public void afterTextChanged(Editable s) {
		if ("".equals(searchKey.getText().toString().trim())) {
			orderListView.setMode(Mode.DISABLED);
		}
	}

	/**
	 * 搜索框内容变化监听
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	/**
	 * 搜索订单
	 * 
	 * @param key
	 *            搜索关键字
	 */
	private void SearchOrder(final String key) {
		StringRequest searchOrder = new StringRequest(Method.POST,
				Urls.ORDER_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							LogUtil.e("searchOrder搜索订单", response);
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								if (currentPage == 1) {
									orderList.clear();
								}
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								if (array.length() != 0) {
									for (int i = 0; i < array.length(); i++) {
										OrderDetail ooDetail = new OrderDetail();
										JSONObject object2 = array
												.getJSONObject(i);
										ooDetail.setOrderNum(object2
												.getString("orderId"));
										orderList.add(ooDetail);
									}
								} else {
									NoList();
								}
								orderAdapter.replaceAll(orderList);
								orderAdapter.notifyDataSetChanged();
								orderListView.onRefreshComplete();
								if (orderList.size() == pageSize) {
									orderListView.setMode(Mode.BOTH);
								}
							} 
							if("010".equals(code)){
								DialogUtil.loginAgain(getActivity());
							}
							if ("001".equals(code)) {
								NoList();
							}
						} catch (JSONException e) {
							NoList();
						} finally {
							if (loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						loading.dismiss();
						ToastUtil.showShort(getActivity(), VolleyErrorUtil
								.getMessage(error, getActivity()));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", key);
				map.put("parkId", UserInfo.parkId);
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				map.put("type", "4");
				LogUtil.e("searchOrder搜索订单", StringUtils.getMap(map));
				return map;
			}
		};
		searchOrder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(searchOrder);
	}

	/**
	 * 获取订单详情
	 * 
	 * @param orderId
	 *            订单ID
	 */
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
								String start = object2.getString("creatTime");
								String type = object2.getString("orderStatus");
								String mobile = object2.getString("userTel");

								Bundle bundle = new Bundle();
								bundle.putString("orderNum", orderId);
								bundle.putString("userName", userName);
								bundle.putString("userCarNum", carName);
								bundle.putString("userMobile", mobile);
								bundle.putString("orderStartDate",start);
								if ("05".equals(type) || "09".equals(type)) {
									bundle.putString("orderStartDate", start);
								}
								Intent intent = null;
								switch (type) {
								case "01":
									intent = new Intent(getActivity(),
											CarInConfirmActivity.class);
									intent.putExtras(bundle);
									break;
								case "05":
									intent = new Intent(getActivity(),
											CarOutAddConfirmActivity.class);
									intent.putExtras(bundle);
									break;
								case "09":
									intent = new Intent(getActivity(),
											CarOutConfirmActivity.class);
									intent.putExtras(bundle);
									break;
								default:
                                    ToastUtil.show(getActivity(), "type:"+type+"--无法处理的订单类型");
									break;
								}
								if(intent!=null){
									intent.putExtras(bundle);
									getActivity().startActivity(intent);
								}
							} else {
								NoDetail();
							}
						} catch (JSONException e) {
							NoDetail();
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
		getOrderDetail
				.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getOrderDetail);
	}

	/**
	 * 无搜索结果
	 */
	private void NoList() {
		ToastUtil.showShort(getActivity(),
				getResources().getString(R.string.no_result));
	}

	/**
	 * 无订单详情
	 */
	private void NoDetail() {
		ToastUtil.showLong(getActivity(),
				getResources().getString(R.string.no_order_detail));
	}

}
