package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
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
import com.sunrise.epark.bean.Park;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DaoHangUtils;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.TTSController;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 预约列表
 * 
 * @author mwj
 *
 */
public class SubscribeListActivity extends BaseActivity implements
		OnClickListener, AMapNaviListener, AMapNaviViewListener {


	private PullToRefreshListView listview;
	private List<SubscribeBean> data;
	private ImageView no_yuyue;
	private QuickAdapter<SubscribeBean> qAdapter;

	private LinearLayout back;

	// 分页请求参数
	private int page = 1;
	private int size = 2;

	private Double curr_lat;
	private Double curr_lng;

	private String lat;
	private String lng;
	// 起点终点
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;

	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe_list);
		// 语音播报开始
		TTSController.getInstance(this).startSpeaking();
		curr_lat = UserInfo.curr_geoLat;
		curr_lng = UserInfo.curr_geoLng;
		initCom();
	}

	private void initCom() {
		// 导航
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
		mRouteCalculatorProgressDialog = new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);
		AMapNavi.getInstance(SubscribeListActivity.this).setAMapNaviListener(
				this);

		data = new ArrayList<SubscribeListActivity.SubscribeBean>();

		back = (LinearLayout) findViewById(R.id.sub_list_back);
		back.setOnClickListener(this);
		no_yuyue = (ImageView) findViewById(R.id.no_yuyue_list);
		listview = (PullToRefreshListView) findViewById(R.id.sub_list);
		listview.setMode(Mode.PULL_FROM_END);
		ILoadingLayout endlabel = listview.getLoadingLayoutProxy(false, true);
		endlabel.setPullLabel(getResources().getString(R.string.load_more));
		endlabel.setRefreshingLabel(getResources().getString(R.string.loading));
		endlabel.setReleaseLabel(getResources().getString(R.string.preload));

		// 上拉 下拉 刷新
		listview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
					 getdata();
				}

			}
		});
		// item点击监听
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 点击跳转到预约详情
				SubscribeBean bean = data.get((int) arg3);
				Intent intent = new Intent(SubscribeListActivity.this,
						SubscribeDetailActivity.class);
				intent.putExtra("orderId", bean.getOrder());
				startActivity(intent);
				startActivityForResult(intent, 113);
			}
		});
		
		
		qAdapter = new QuickAdapter<SubscribeBean>(this,
				R.layout.subscribe_list_item, data) {

			@Override
			protected void convert(BaseAdapterHelper v, final SubscribeBean arg1) {
				// TODO Auto-generated method stub
				v.setText(R.id.sub_item_name, arg1.getName());
				v.setText(R.id.sub_item_start, arg1.getStartdate());
				v.setText(R.id.sub_item_end, arg1.getEnddate());
				v.getView(R.id.sub_item_daohang).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View arg0) {

								Toast.makeText(getApplicationContext(),
										"parkid:" + arg1.getParkid(),
										Toast.LENGTH_LONG).show();
								StringRequest register = new StringRequest(
										Method.POST, Urls.STOPCAR_MINGXI,
										new Response.Listener<String>() {
											@Override
											public void onResponse(
													String response) {
												closeLoadingDialog();
												LogUtil.e("停车场详情", response);
												try {
													JSONObject jObject = new JSONObject(
															response);

													String code = jObject
															.getString("code");

													switch (code) {
													case "000":
														// 成功
														JSONObject results = jObject
																.getJSONObject("result");
														lat = results
																.getString("latitude");
														lng = results
																.getString("longitude");
														mNaviEnd = new NaviLatLng(Double.valueOf(lat),
																Double.valueOf(lng));
														break;
													case "001":
														showShortToast(jObject
																.getString("result"));
														break;
													case "002":
														showShortToast(jObject
																.getString("result"));
														break;
													case "003":
														showShortToast(jObject
																.getString("result"));
														break;
													case "004":
														showShortToast(jObject
																.getString("result"));
														break;
													default:
														showShortToast(jObject
																.getString("result"));
														break;
													}

												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}finally{
													//导航
													mNaviStart = new NaviLatLng(curr_lat, curr_lng);
													mStartPoints.add(mNaviStart);
													mEndPoints.add(mNaviEnd);
													AMapNavi.getInstance(SubscribeListActivity.this).calculateDriveRoute(mStartPoints,
															mEndPoints, null, AMapNavi.DrivingDefault);
													mRouteCalculatorProgressDialog.show();
												}
											}
										}, new Response.ErrorListener() {
											@Override
											public void onErrorResponse(
													VolleyError error) {

												showLoadingDialog("请求失败");
												ToastUtil
														.showLong(
																SubscribeListActivity.this,
																VolleyErrorUtil
																		.getMessage(
																				error,
																				SubscribeListActivity.this));
											}
										}) {
									@Override
									protected Map<String, String> getParams()
											throws AuthFailureError {
										Map<String, String> map = new HashMap<String, String>();
										map.put("parkId", arg1.getParkid());
										map.put("uuid", UserInfo.uuid);
										map.put("userId", UserInfo.userId);
										return map;
									}
								};
								register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
										Configure.Retry, Configure.Multiplier));
								BaseApplication.mQueue.add(register);
								
							}
						});
			}
		};
		listview.setAdapter(qAdapter);
		getdata();
	}

   
	
	@Override
	public void onClick(View arg0) {

		int id = arg0.getId();
		switch (id) {
		case R.id.sub_list_back:
			finish();
			break;

		default:
			break;
		}
	}

	private void getdata() {
		// if(!StatusUtil.StatusCheck(SubscribeListActivity.this))
		// {
		// finish();
		// return;
		// }
		showLoadingDialog();
		StringRequest register = new StringRequest(Method.POST,
				Urls.SUBSCRIBE_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("预约列表", response);
						try {
							JSONObject jObject = new JSONObject(response);
							String code = jObject.getString("code");
							if( "000".equals(code)){
								// 成功
								JSONArray results = jObject
										.getJSONArray("result");
								if (results.length() == 0) {
									no_yuyue.setVisibility(View.VISIBLE);
									listview.setVisibility(View.GONE);
								} else {
									no_yuyue.setVisibility(View.GONE);
									listview.setVisibility(View.VISIBLE);
								}
								for (int i = 0; i < results.length(); i++) {
									JSONObject obj = results.getJSONObject(i);
									data.add(new SubscribeBean(obj
											.getString("parkName"), obj
											.getString("startTime"), obj
											.getString("endTime"), obj
											.getString("parkId"), obj
											.getString("orderId")));
								}
								qAdapter.replaceAll(data);
								qAdapter.notifyDataSetChanged();
								// 页面自增１
								page++;
							}
							if( "010".equals(code)){
								// 失败
								DialogUtil
										.loginAgain(SubscribeListActivity.this);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							// 刷新完成
							listview.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						showLoadingDialog("请求失败");
						ToastUtil.showLong(SubscribeListActivity.this,
								VolleyErrorUtil.getMessage(error,
										SubscribeListActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderType", "01");
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("page", String.valueOf(page));
				map.put("size", String.valueOf(size));
				map.put("type", "2");
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	protected void onResume() {
		if (page != 1) {
			ToastUtil.show(getApplicationContext(), "onresume");
			page = 1;// 从第一页开始
			data.clear();
			getdata();
		}
		super.onResume();
	}

	/**
	 * 预约实体类
	 * 
	 * @author Rocky
	 *
	 */
	class SubscribeBean {
		// 名字
		private String name;
		// 开始时间
		private String startdate;
		// 结束时间
		private String enddate;
		// 停车场id
		private String parkid;
		// 编号
		private String order;

		public SubscribeBean() {
		}

		public SubscribeBean(String name, String startdate, String enddate,
				String parkid, String order) {
			super();
			this.name = name;
			this.startdate = startdate;
			this.enddate = enddate;
			this.parkid = parkid;
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStartdate() {
			return startdate;
		}

		public void setStartdate(String startdate) {
			this.startdate = startdate;
		}

		public String getEnddate() {
			return enddate;
		}

		public void setEnddate(String enddate) {
			this.enddate = enddate;
		}

		public String getParkid() {
			return parkid;
		}

		public void setParkid(String parkid) {
			this.parkid = parkid;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

	}

	// ---------------------导航回调--------------------
	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		mRouteCalculatorProgressDialog.dismiss();

	}

	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		Intent intent = new Intent(SubscribeListActivity.this,
				SimpleNaviActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		Bundle bundle = new Bundle();
		bundle.putInt(DaoHangUtils.ACTIVITYINDEX, DaoHangUtils.SIMPLEGPSNAVI);
		bundle.putBoolean(DaoHangUtils.ISEMULATOR, false);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();

	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	// ---------------------导航View事件回调-----------------------------

	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 删除导航监听
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
		/*
		 * // 这是最后退出页，所以销毁导航和播报资源 AMapNavi.getInstance(this).destroy();// 销毁导航
		 * TTSController.getInstance(this).stopSpeaking();
		 * TTSController.getInstance(this).destroy();
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amap.api.navi.AMapNaviListener#onNaviInfoUpdate(com.amap.api.navi
	 * .model.NaviInfo)
	 */
	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub

	}
}
