package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
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
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 停车场列表 com.camelot.parkinglots.activity.ParkedListActivity
 * 
 * @author 李敏 create at 2015年7月23日 上午9:53:33
 */
public class ParkedListActivity extends BaseActivity implements
		OnClickListener, OnDismissListener, OnRefreshListener<ListView>,
		OnItemClickListener {

	/** 加载框 */
	private Dialog loading;
	/** 停车场列表适配器 */
	private QuickAdapter<JSONObject> parkedListAdapter;
	private QuickAdapter<JSONObject> ListAdapter;
	/** 返回 */
	private ImageView backImg;
	/** 筛选的布局 */
	private RelativeLayout ll_choose;
	/** 筛选文字 */
	private TextView tv_choose;
	/** 筛选图标 */
	private ImageView iv_icon1;
	/** 筛选框 */
	private PopupWindow popWindow;
	/** 距离 */
	private TextView distance;
	/** 价格 */
	private TextView price;
	/** 距离筛选条件布局 */
	private LinearLayout distanceLinear;
	/** 价格筛选条件布局 */
	private LinearLayout priceLinear;
	/** 价格从低到高 */
	private TextView priceAcs;
	/** 价格从高到底 */
	private TextView priceDesc;
	/** 不限距离 */
	private TextView limitNo;
	/** 距离0.5公里 */
	private TextView limit1;
	/** 距离1公里 */
	private TextView limit2;
	/** 距离2公里 */
	private TextView limit3;
	/**
	 * 筛选类型 -1:未初始化 1:距离 2:价格
	 */
	private int selectType = -1;
	/**
	 * selectType=1 1:不限 2:0.5公里 3:1公里 4:2公里 selectType=2 1:从低到高 2：从高到低
	 */
	private int selectValue = -1;
	/** 灰色 */
	private int gray = android.graphics.Color.parseColor("#E7E7E7");
	/** 白色 */
	private int white = android.graphics.Color.parseColor("#FFFFFF");
	/** 停车场列表listview */
	private PullToRefreshListView pullListView;

	private boolean flag = true;

	private ListView sel_listView;
	/** 当前纬度 */
	private Double curr_lat;
	/** 当前经度 */
	private Double curr_lng;
	/** 停车场列表数据 */
	private ArrayList<JSONObject> parkList;

	/** 筛选后的停车场列表数据 */
	private ArrayList<JSONObject> getParkList = new ArrayList<JSONObject>();

	/** 当前页 */
	private int currentPage = 1;

	/** 每页显示的数据 5条 */
	private int pageSize = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parked_list);
		initView();
		initListView();
		initData();
		bindEvent();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		ll_choose = (RelativeLayout) findViewById(R.id.ll_choose);
		iv_icon1 = (ImageView) findViewById(R.id.iv_icon1);
		tv_choose = (TextView) findViewById(R.id.tv_choose);
		backImg = (ImageView) findViewById(R.id.parklist_back);
	}

	/**
	 * 初始化listview
	 */
	private void initListView() {
		sel_listView = (ListView) findViewById(R.id.sel_ListView);
		sel_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(ParkedListActivity.this,
						ParkedDetailsActivity.class);
				try {
					intent.putExtra("parkId",
							getParkList.get(position).getString("parkId"));
					intent.putExtra("curr_Lat", curr_lat);
					intent.putExtra("curr_lng", curr_lng);
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		pullListView = (PullToRefreshListView) findViewById(R.id.pullListView);
		pullListView.setMode(Mode.PULL_FROM_END);
		ILoadingLayout endlabel = pullListView.getLoadingLayoutProxy(false,
				true);
		endlabel.setPullLabel(getResources().getString(R.string.load_more));
		endlabel.setRefreshingLabel(getResources().getString(R.string.loading));
		endlabel.setReleaseLabel(getResources().getString(R.string.preload));
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		ll_choose.setOnClickListener(this);
		pullListView.setOnRefreshListener(this);
		pullListView.setOnItemClickListener(this);
		backImg.setOnClickListener(this);
	}

	/**
	 * 给组件赋值
	 */
	private void initData() {
		Intent intent = getIntent();
		curr_lat = intent.getDoubleExtra("lat", 0.0);
		curr_lng = intent.getDoubleExtra("lng", 0.0);

		parkList = (ArrayList<JSONObject>) LocationActivity.parkList;
		getParkList = parkList;
		if (getParkList == null) {
			ToastUtil.showShort(ParkedListActivity.this, "附近没有停车场");
		} else {
			parkedListAdapter = new QuickAdapter<JSONObject>(this,
					R.layout.park_list_item, getParkList) {
				@Override
				protected void convert(BaseAdapterHelper arg0, JSONObject json) {
					try {
						arg0.setText(R.id.tv_parkAddr,
								json.getString("parkAddr"));
						arg0.setText(R.id.tv_parkCity,
								json.getString("parkCity"));
						arg0.setText(R.id.tv_parkName,
								json.getString("parkName"));
						arg0.setText(R.id.tv_parkId, json.getString("parkId"));
						// 1.将两个经纬度点转换成距离
						LatLng startLatLng = new LatLng(curr_lat, curr_lng);
						LatLng endLatLng = new LatLng(Double.valueOf(json
								.getString("latitude")), Double.valueOf(json
								.getString("longitude")));
						String str = String
								.valueOf(Math.floor(AMapUtils
										.calculateLineDistance(startLatLng,
												endLatLng)));
						String location = str.substring(0, str.indexOf("."));
						arg0.setText(R.id.tv_parkDistance, location + "米");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
		}

		pullListView.setVisibility(View.GONE);
		sel_listView.setAdapter(parkedListAdapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_choose:// 筛选
			showPop();
			break;
		case R.id.parklist_back:// 返回
			Intent goBackIntent = new Intent(ParkedListActivity.this,
					LocationActivity.class);
			goBackIntent.putExtra("isBack", true);
			startActivity(goBackIntent);
			finish();
			break;
		case R.id.pop_sort_distance:// 距离
			priceLinear.setVisibility(View.GONE);
			distanceLinear.setVisibility(View.VISIBLE);
			distance.setBackgroundColor(gray);
			price.setBackgroundColor(white);
			break;
		case R.id.pop_sort_price:// 价格
			distanceLinear.setVisibility(View.GONE);
			priceLinear.setVisibility(View.VISIBLE);
			price.setBackgroundColor(gray);
			distance.setBackgroundColor(white);
			break;
		case R.id.pop_sort_acs:// 从低到高
			priceAcs.setBackgroundColor(gray);
			priceDesc.setBackgroundColor(white);
			tv_choose.setText(priceAcs.getText().toString().trim());
			selectType = 2;
			selectValue = 1;
			popWindow.dismiss();
			pullListView.setVisibility(View.GONE);
			sel_listView.setVisibility(View.VISIBLE);
			getParklist2();
			break;
		case R.id.pop_sort_desc:// 从高到低
			priceDesc.setBackgroundColor(gray);
			priceAcs.setBackgroundColor(white);
			tv_choose.setText(priceDesc.getText().toString().trim());
			selectType = 2;
			selectValue = 2;
			popWindow.dismiss();
			pullListView.setVisibility(View.GONE);
			sel_listView.setVisibility(View.VISIBLE);
			getParklist2();
			break;
		case R.id.distance_limit_no:// 距离不限
			limitNo.setBackgroundColor(gray);
			limit1.setBackgroundColor(white);
			limit2.setBackgroundColor(white);
			limit3.setBackgroundColor(white);
			tv_choose.setText(limitNo.getText().toString().trim());
			selectType = 1;
			selectValue = 1;
			popWindow.dismiss();
			sel_listView.setVisibility(View.GONE);
			pullListView.setVisibility(View.VISIBLE);
			getParkList.clear();
			currentPage=1;
			getParklist();
			ListAdapter = new QuickAdapter<JSONObject>(this,
					R.layout.park_list_item, getParkList) {
				@Override
				protected void convert(BaseAdapterHelper arg0, JSONObject json) {
					try {
						arg0.setText(R.id.tv_parkAddr,
								json.getString("parkAddr"));
						arg0.setText(R.id.tv_parkCity,
								json.getString("parkCity"));
						arg0.setText(R.id.tv_parkName,
								json.getString("parkName"));
						arg0.setText(R.id.tv_parkId, json.getString("parkId"));
						// 1.将两个经纬度点转换成距离
						LatLng startLatLng = new LatLng(curr_lat, curr_lng);
						LatLng endLatLng = new LatLng(Double.valueOf(json
								.getString("latitude")), Double.valueOf(json
								.getString("longitude")));
						String str = String
								.valueOf(Math.floor(AMapUtils
										.calculateLineDistance(startLatLng,
												endLatLng)));
						String location = str.substring(0, str.indexOf("."));
						arg0.setText(R.id.tv_parkDistance, location + "米");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			pullListView.setAdapter(ListAdapter);
			break;
		case R.id.distance_limit_1:// 0.5公里
			limitNo.setBackgroundColor(white);
			limit1.setBackgroundColor(gray);
			limit2.setBackgroundColor(white);
			limit3.setBackgroundColor(white);
			tv_choose.setText(limit1.getText().toString().trim());
			selectType = 1;
			selectValue = 2;
			popWindow.dismiss();
			pullListView.setVisibility(View.GONE);
			sel_listView.setVisibility(View.VISIBLE);
			getParklist2();
			break;
		case R.id.distance_limit_2:// 1公里
			limitNo.setBackgroundColor(white);
			limit1.setBackgroundColor(white);
			limit2.setBackgroundColor(gray);
			limit3.setBackgroundColor(white);
			tv_choose.setText(limit2.getText().toString().trim());
			selectType = 1;
			selectValue = 3;
			popWindow.dismiss();
			pullListView.setVisibility(View.GONE);
			sel_listView.setVisibility(View.VISIBLE);
			getParklist2();
			break;
		case R.id.distance_limit_3:// 2公里
			limitNo.setBackgroundColor(white);
			limit1.setBackgroundColor(white);
			limit2.setBackgroundColor(white);
			limit3.setBackgroundColor(gray);
			tv_choose.setText(limit3.getText().toString().trim());
			selectType = 1;
			selectValue = 4;
			popWindow.dismiss();
			pullListView.setVisibility(View.GONE);
			sel_listView.setVisibility(View.VISIBLE);
			getParklist2();
			break;

		default:
			break;
		}
	}

	/**
	 * 刷新停车场列表 // 上拉 下拉 刷新
	 * 
	 * @param refreshView
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			getParklist();
		}
		/*
		 * if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) { String
		 * label = DateUtils.formatDateTime(ParkedListActivity.this,
		 * System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME |
		 * DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		 * refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		 * currentPage=1; getParklist(); }
		 */
	}

	/**
	 * 跳转到停车场详情
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(ParkedListActivity.this,
				ParkedDetailsActivity.class);
		try {
			intent.putExtra("parkId",
					getParkList.get(position - 1).getString("parkId"));
			intent.putExtra("curr_Lat", curr_lat);
			intent.putExtra("curr_lng", curr_lng);
			startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出筛选框
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showPop() {
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		} else {
			if (popWindow == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.pop_select, null);
				popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT, true);
				distance = (TextView) view.findViewById(R.id.pop_sort_distance);
				price = (TextView) view.findViewById(R.id.pop_sort_price);
				distanceLinear = (LinearLayout) view
						.findViewById(R.id.pop_distance_liner);
				priceLinear = (LinearLayout) view
						.findViewById(R.id.pop_price_liner);
				priceAcs = (TextView) view.findViewById(R.id.pop_sort_acs);
				priceDesc = (TextView) view.findViewById(R.id.pop_sort_desc);
				limitNo = (TextView) view.findViewById(R.id.distance_limit_no);
				limit1 = (TextView) view.findViewById(R.id.distance_limit_1);
				limit2 = (TextView) view.findViewById(R.id.distance_limit_2);
				limit3 = (TextView) view.findViewById(R.id.distance_limit_3);
				if (selectType > 0 && selectValue > 0) {
					switch (selectType) {
					case 1:
						priceLinear.setVisibility(View.GONE);
						distanceLinear.setVisibility(View.VISIBLE);
						distance.setBackgroundColor(gray);
						price.setBackgroundColor(white);
						switch (selectValue) {
						case 1:
							limitNo.setBackgroundColor(gray);
							limit1.setBackgroundColor(white);
							limit2.setBackgroundColor(white);
							limit3.setBackgroundColor(white);
							break;
						case 2:
							limitNo.setBackgroundColor(white);
							limit1.setBackgroundColor(gray);
							limit2.setBackgroundColor(white);
							limit3.setBackgroundColor(white);
							break;
						case 3:
							limitNo.setBackgroundColor(white);
							limit1.setBackgroundColor(white);
							limit2.setBackgroundColor(gray);
							limit3.setBackgroundColor(white);
							break;
						case 4:
							limitNo.setBackgroundColor(white);
							limit1.setBackgroundColor(white);
							limit2.setBackgroundColor(white);
							limit3.setBackgroundColor(gray);
							break;
						}
						break;
					case 2:
						distanceLinear.setVisibility(View.GONE);
						priceLinear.setVisibility(View.VISIBLE);
						price.setBackgroundColor(gray);
						distance.setBackgroundColor(white);
						switch (selectValue) {
						case 1:
							priceAcs.setBackgroundColor(gray);
							priceDesc.setBackgroundColor(white);
							break;
						case 2:
							priceAcs.setBackgroundColor(white);
							priceDesc.setBackgroundColor(gray);
							break;
						}
						break;
					}
				}
				popWindow.setOnDismissListener(this);
				distance.setOnClickListener(this);
				price.setOnClickListener(this);
				distanceLinear.setOnClickListener(this);
				priceLinear.setOnClickListener(this);
				priceAcs.setOnClickListener(this);
				priceDesc.setOnClickListener(this);
				limitNo.setOnClickListener(this);
				limit1.setOnClickListener(this);
				limit2.setOnClickListener(this);
				limit3.setOnClickListener(this);
			}
			popWindow.setFocusable(true);
			popWindow.setBackgroundDrawable(new BitmapDrawable());
			popWindow
					.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popWindow.setOutsideTouchable(true);
			popWindow.showAsDropDown(ll_choose);
			iv_icon1.setImageResource(R.drawable.icon_top_arrow);
			tv_choose
					.setTextColor(android.graphics.Color.parseColor("#4DC060"));
		}
	}

	/**
	 * 筛选框消失后的操作
	 */
	@Override
	public void onDismiss() {
		iv_icon1.setImageResource(R.drawable.icon_down_arrow);
		tv_choose.setTextColor(android.graphics.Color.parseColor("#ff000000"));
	}

	/**
	 * 获取停车场列表
	 */
	private void getParklist() {
		// loading.show();
		StringRequest parks = new StringRequest(Method.POST,
				Urls.PARKLIST_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("停车列表筛选", response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							//getParkList.clear();
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								for (int i = 0; i < array.length(); i++) {
									getParkList.add(array.getJSONObject(i));
									LogUtil.e("添加进去了莫", getParkList.get(i)
											.getString("parkId"));
								}

								ListAdapter.replaceAll(getParkList);
								ListAdapter.notifyDataSetChanged();
								// 页面自增１
								currentPage++;
							}
							if ("001".equals(code)) {
								showShortToast("没有数据");
							}
							if ("010".equals("code")) {
								DialogUtil.loginAgain(ParkedListActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading.isShowing()) {
								loading.dismiss();
							}
							// 刷新完成
							pullListView.onRefreshComplete();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading.isShowing()) {
							loading.dismiss();
						}
						pullListView.onRefreshComplete();
						ToastUtil.showLong(ParkedListActivity.this,
								VolleyErrorUtil.getMessage(error,
										ParkedListActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("longitude", String.valueOf(curr_lng));
				map.put("latitude", String.valueOf(curr_lat));
				map.put("priceSort", "");
				map.put("distance", "0");
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.e("停车列表筛选", StringUtils.getMap(map));
				return map;
			}
		};
		parks.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(parks);
	}

	/**
	 * 获取停车场列表
	 */
	private void getParklist2() {
		// loading.show();
		StringRequest parks = new StringRequest(Method.POST,
				Urls.PARKLIST_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("停车列表筛选", response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							getParkList.clear();
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								for (int i = 0; i < array.length(); i++) {
									getParkList.add(array.getJSONObject(i));
									LogUtil.e("添加进去了莫", getParkList.get(i)
											.getString("parkId"));
								}

								parkedListAdapter.replaceAll(getParkList);
								parkedListAdapter.notifyDataSetChanged();
							}
							if ("001".equals(code)) {
								showShortToast("没有数据!");
							}
							if ("010".equals("code")) {
								DialogUtil.loginAgain(ParkedListActivity.this);
							}
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
						ToastUtil.showLong(ParkedListActivity.this,
								VolleyErrorUtil.getMessage(error,
										ParkedListActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("longitude", String.valueOf(curr_lng));
				map.put("latitude", String.valueOf(curr_lat));
				switch (selectType) {
				case 1:
					map.put("priceSort", "");
					switch (selectValue) {
					case 1:
						map.put("distance", "0");

						break;
					case 2:
						map.put("distance", "0.5");
						break;
					case 3:
						map.put("distance", "1");
						break;
					case 4:
						map.put("distance", "2");
						break;
					}
					break;
				case 2:
					map.put("distance", "3");
					switch (selectValue) {
					case 1:
						map.put("priceSort", "");
						break;
					case 2:
						map.put("priceSort", "desc");
						break;
					}
				}
				LogUtil.e("停车列表筛选", StringUtils.getMap(map));
				return map;
			}
		};
		parks.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(parks);
	}

	/**
	 * 点击返回键,如界面上弹出框还在先隐藏弹出框
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// ToastUtil.showShort(this, curr_lat+"-------------"+curr_lng);
	}
}
