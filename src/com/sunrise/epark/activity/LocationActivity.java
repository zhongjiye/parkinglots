package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.Park;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DaoHangUtils;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.ImageUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.PreferenceUtil;
import com.sunrise.epark.util.ScreenUtils;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.TTSController;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 地图定位的首页 com.camelot.parkinglots.activity.LocationActivity
 * 
 * @author 李敏 create at 2015年7月23日 上午9:24:05
 */
public class LocationActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnMapLoadedListener, OnMarkerClickListener,
		OnClickListener, OnCameraChangeListener, OnGeocodeSearchListener,
		AMapNaviListener, AMapNaviViewListener, OnMapClickListener {

	private static int requestCount = 0;

	private String loginStatus;
	private String imgUrl;
	private String carNameString;
	/** 显示车牌 */
	private DrawerLayout drawerLayout;// 抽屉

	/** 打开抽屉 */
	private ImageView openDrawer;

	/** 停车场列表 */
	private ImageView iv_parkingList;

	private MapView mapView;// 地图视图

	private GeocodeSearch geocoderSearch;

	private AMap aMap;

	private OnLocationChangedListener mListener;

	private LocationManagerProxy mAmapLocationManager;

	private boolean firstLocat = false;
	private Double tempLat;
	private Double tempLng;

	private Double latDouble;// 作为参数经度
	private Double lngDouble;// 作为参数纬度

	private MarkerOptions markerOption;// marker集合

	public String cityCode;// 城市编码

	private CheckBox traffic;// 路况

	public Boolean trafficFlag = false;// 默认没选中

	private ImageView iv_user_image, iv_center;// 个人图像

	private ImageView iv_loction;// 定位按钮
	private ImageView iv_orders;// 订单图标

	private RelativeLayout rl_search;
	private LinearLayout pesoner;// 个人信息

	private LinearLayout money;// 钱包余额
	private LinearLayout preferential;// 优惠推广
	private LinearLayout parkNotes;// 停车记录
	private LinearLayout invoice;// 发票邮寄
	private LinearLayout orderList;// 预约列表
	private LinearLayout choosePackage;// 套餐选配
	private LinearLayout myMessage;// 我的消息
	private LinearLayout systemSetting;// 停车记录

	private RelativeLayout loginLayout, notLoginLayout, rl_apply;
	private TextView carName, userName;

	public static List<JSONObject> parkList = new ArrayList<JSONObject>();

	private LinearLayout parkDetail, include_detail, ll_daohang;

	private TextView tv_daohang, tv_parkNameD, tv_distanceD, tv_parkTypeD,
			tv_parkAddrD, tv_parkidD, tv_parkGZ;

	private LinearLayout rl_daohang, ll_location_detatil, jingchang;

	// 起点终点
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;

	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态

	LatLngBounds bounds = null;
	List<LatLng> latlng = new ArrayList<LatLng>();

	private AMapLocation aLocation;

	/** 广州数据 */
	private Double latDouble_gz = 23.137962;
	private Double lngDouble_gz = 113.272698;

	private static boolean isBack = false;

	/** loading框 */
	private Dialog loading;

	private String orderId;// 在库 订单号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_location);
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		geocoderSearch = new GeocodeSearch(LocationActivity.this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		// 语音播报开始
		TTSController.getInstance(this).startSpeaking();
		drawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
		traffic = (CheckBox) findViewById(R.id.traffic);
		traffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aMap.setTrafficEnabled(((CheckBox) v).isChecked());// 是否显示路况信息
			}
		});
		init();
		initView();
	}

	/**
	 * 初始化组件
	 */
	@SuppressLint("NewApi")
	private void initView() {
		loginStatus = UserInfo.loginStatus;
		loginLayout = (RelativeLayout) findViewById(R.id.ll_login);
		notLoginLayout = (RelativeLayout) findViewById(R.id.ll_notLogin);
		loginLayout.setOnClickListener(this);
		notLoginLayout.setOnClickListener(this);

		carName = (TextView) findViewById(R.id.tv_car);
		userName = (TextView) findViewById(R.id.tv_user_name);
		initImg();
		iv_orders = (ImageView) findViewById(R.id.iv_orders);
		// 打开抽屉
		openDrawer = (ImageView) findViewById(R.id.open);
		openDrawer.setOnClickListener(this);

		// 登陆
		openDrawer = (ImageView) findViewById(R.id.open);
		openDrawer.setOnClickListener(this);

		// 停车场列表
		iv_parkingList = (ImageView) findViewById(R.id.parking_list);
		iv_parkingList.setOnClickListener(this);

		// 订单
		iv_loction = (ImageView) findViewById(R.id.iv_location);
		iv_loction.setOnClickListener(this);
		// 目的地搜索
		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		rl_search.setOnClickListener(this);

		pesoner = (LinearLayout) findViewById(R.id.pesoner);
		pesoner.setOnClickListener(this);

		money = (LinearLayout) findViewById(R.id.money);
		money.setOnClickListener(this);

		preferential = (LinearLayout) findViewById(R.id.preferential);
		preferential.setOnClickListener(this);

		parkNotes = (LinearLayout) findViewById(R.id.parkNotes);
		parkNotes.setOnClickListener(this);

		invoice = (LinearLayout) findViewById(R.id.invoice);
		invoice.setOnClickListener(this);

		orderList = (LinearLayout) findViewById(R.id.orderList);
		orderList.setOnClickListener(this);

		choosePackage = (LinearLayout) findViewById(R.id.choosePackage);
		choosePackage.setOnClickListener(this);

		myMessage = (LinearLayout) findViewById(R.id.myMessage);
		myMessage.setOnClickListener(this);

		systemSetting = (LinearLayout) findViewById(R.id.systemSetting);
		systemSetting.setOnClickListener(this);

		parkDetail = (LinearLayout) findViewById(R.id.parkDetail);
		tv_parkNameD = (TextView) findViewById(R.id.tv_parkNameD);
		tv_distanceD = (TextView) findViewById(R.id.tv_distanceD);
		tv_parkTypeD = (TextView) findViewById(R.id.tv_parkTypeD);
		tv_parkAddrD = (TextView) findViewById(R.id.tv_parkAddrD);
		tv_parkidD = (TextView) findViewById(R.id.tv_parkidD);
		tv_daohang = (TextView) findViewById(R.id.tv_daohang);
		tv_daohang.setOnClickListener(this);
		tv_parkGZ = (TextView) findViewById(R.id.tv_parkGZ);
		tv_parkGZ.setOnClickListener(this);

		rl_daohang = (LinearLayout) findViewById(R.id.daohang);
		rl_daohang.setOnClickListener(this);

		include_detail = (LinearLayout) findViewById(R.id.include_detail);
		ll_location_detatil = (LinearLayout) findViewById(R.id.ll_location_detatil);
		ll_location_detatil.setOnClickListener(this);

		rl_apply = (RelativeLayout) findViewById(R.id.rl_apply);
		rl_apply.setOnClickListener(this);
		jingchang = (LinearLayout) findViewById(R.id.jingchang);
		jingchang.setOnClickListener(this);

		iv_center = (ImageView) findViewById(R.id.iv_center);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ScreenUtils.getScreenWidth(LocationActivity.this) / 2,
				ScreenUtils.getScreenHeight(LocationActivity.this) / 2);
		iv_center
				.setPivotX(ScreenUtils.getScreenWidth(LocationActivity.this) / 2);
		iv_center
				.setPivotY(ScreenUtils.getScreenHeight(LocationActivity.this) / 2);
	}

	/*
	 * 初始化AMap对象
	 */
	private void init() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		loading.show();
		if (aMap == null) {
			aMap = mapView.getMap();// 得到地图
			setUpMap();
			// 导航
			TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
			ttsManager.init();
			AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
			mRouteCalculatorProgressDialog = new ProgressDialog(this);
			mRouteCalculatorProgressDialog.setCancelable(true);
			AMapNavi.getInstance(LocationActivity.this).setAMapNaviListener(
					this);
		}
	}

	/**
	 * 初始化用户头像
	 */
	private void initImg() {
		iv_user_image = (ImageView) findViewById(R.id.iv_user_image);
		iv_user_image.setOnClickListener(this);
		if ("login".equals(UserInfo.loginStatus)
				|| "register".equals(UserInfo.loginStatus)) {
			initUrlImg();
			loginLayout.setVisibility(View.VISIBLE);
			notLoginLayout.setVisibility(View.GONE);
			carName.setText(UserInfo.carName);
			userName.setText(UserInfo.userLogin);
		} else {
			initDefaultImg();
		}
	}

	/**
	 * 初始化图片url
	 */
	@SuppressWarnings("deprecation")
	private void initUrlImg() {
		if (!"".equals(UserInfo.imgUrl)) {
			ImageRequest imageRequest = new ImageRequest(UserInfo.imgUrl,
					new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap response) {
							iv_user_image.setBackground(new BitmapDrawable(
									ImageUtil.getCircuarImg(
											LocationActivity.this, response)));
							iv_user_image.getBackground().setAlpha(0);
							iv_user_image.setImageBitmap(ImageUtil
									.getCircuarImg(LocationActivity.this,
											response));
						}
					}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							initDefaultImg();
						}
					});
			BaseApplication.mQueue.add(imageRequest);
		} else {
			initDefaultImg();
		}
	}

	/**
	 * 初始化默认头像
	 */
	private void initDefaultImg() {
		iv_user_image.setScaleType(ScaleType.FIT_XY);
		iv_user_image.setBackground(new BitmapDrawable(ImageUtil.getCircuarImg(
				LocationActivity.this, R.drawable.icon_head)));
		iv_user_image.getBackground().setAlpha(0);
		iv_user_image.setImageBitmap(ImageUtil.getCircuarImg(
				LocationActivity.this, R.drawable.icon_head));
	}

	/**
	 * 检查是否有在库订单
	 * 
	 * @param type
	 */
	private void checkOrder() {
		StringRequest checkOrder = new StringRequest(Method.POST,
				Urls.LIST_ORDER, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("在库订单:" + Urls.LIST_ORDER, response);
						try {
							JSONObject jObject = new JSONObject(response);
							String code = jObject.getString("code");
							LogUtil.d("....", jObject + "");
							if ("000".equals(code)) {
								String result=jObject.getString("result");
								JSONArray results=new JSONArray(result);
								if(results.length()>0){
									JSONObject temp=(JSONObject)results.get(0);
									if(temp!=null){
										if(StringUtils.isNotNull(temp.getString("orderId"))){
											orderId = ((JSONObject)results.get(0)).getString("orderId");
											LogUtil.d("在库订单 单号:", orderId + "......");
											iv_orders.setVisibility(View.VISIBLE);
											iv_orders.setOnClickListener(LocationActivity.this);
										}
										
									}
									
								}
							}
							if ("001".equals(code)) {
								iv_orders.setVisibility(View.INVISIBLE);
							}
						} catch (JSONException e) {
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
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("orderStatus", "02");
				map.put("type", "3");
				LogUtil.i("在库订单:" + Urls.LIST_ORDER, StringUtils.getMap(map));
				return map;
			}
		};
		checkOrder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(checkOrder);
	}
	
	private void del(String temp){
		
	}

	/**
	 * 给Amap设置对应的属性
	 */
	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.parseColor("#00000000"));// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(0);// 设置圆形的填充颜色
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setZoomGesturesEnabled(true);
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
		// 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器

	}

	/**
	 * 网络请求测试
	 */
	public void parkListLoad() {
		StringRequest parks = new StringRequest(Method.POST,
				Urls.PARKLIST_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						/*
						 * if ((tempLat == latDouble && tempLng == lngDouble) ||
						 * (!firstLocat)) { if (!firstLocat) { firstLocat =
						 * true; }
						 */
						LogUtil.e("得到请求停车场数据", response);
						try {
							LogUtil.i("得到请求停车场数据", (new JSONObject(response))
									.getString("code"));
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								parkList.clear();
								LogUtil.i("得到请求停车场数据数量", array.length() + "");
								for (int i = 0; i < array.length(); i++) {
									parkList.add(array.getJSONObject(i));
								}
							}
							if ("001".equals(code)) {
								parkList.clear();
							}
							if ("010".equals("code")) {
								DialogUtil.loginAgain(LocationActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading.isShowing()) {
								loading.dismiss();
							}
							if (parkList.size() > 0) {
								addMarkersToMap();
							} else {
								ToastUtil.showShort(LocationActivity.this,
										"附近没有停车场!");
							}
						}
					}
					// }
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (loading.isShowing()) {
							loading.dismiss();
						}
						ToastUtil.showLong(LocationActivity.this,
								VolleyErrorUtil.getMessage(error,
										LocationActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("longitude", String.valueOf(lngDouble));
				map.put("latitude", String.valueOf(latDouble));
				map.put("distance", "3");
				map.put("page", "1");
				map.put("priceSort", "");
				return map;
			}
		};
		BaseApplication.mQueue.cancelAll("park" + (requestCount - 1));
		parks.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		parks.setTag("park" + requestCount);
		BaseApplication.mQueue.add(parks);
	}

	// 在地图上添加marker
	private void addMarkersToMap() {
		ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
		if (parkList.size() > 0) {
			for (int i = 0; i < parkList.size(); i++) {
				markerOption = new MarkerOptions();
				try {
					String latDoubles = parkList.get(i).getString("latitude");
					String lngDoubles = parkList.get(i).getString("longitude");
					LatLng latLng = new LatLng(Double.valueOf(latDoubles),
							Double.valueOf(lngDoubles));
					markerOption.position(latLng);
					markerOption.perspective(true);
					markerOption.draggable(true);
					if (Integer
							.valueOf(parkList.get(i).getString("remainPark")) <= 0) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon0));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 1) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon1));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 2) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon2));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 3) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon3));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 4) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon4));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 5) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon5));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 6) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon6));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 7) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon7));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 8) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon8));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) == 9) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon9));
					} else if (Integer.valueOf(parkList.get(i).getString(
							"remainPark")) > 9
							&& Integer.valueOf(parkList.get(i).getString(
									"remainPark")) <= 20) {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.img10));
					} else {
						markerOption.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.img20));
					}
					markerOption.setFlat(true);
					markerOptionlst.add(markerOption);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			if (aMap != null) {
				aMap.addMarkers(markerOptionlst, false);
				// 设置所有maker显示在当前可视区域地图中
				LatLngBounds.Builder bounds = new LatLngBounds.Builder();
				for (int j = 0; j < parkList.size(); j++) {
					try {
						bounds.include(new LatLng(Double.valueOf(parkList
								.get(j).getString("latitude")),
								Double.valueOf(parkList.get(j).getString(
										"longitude"))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				bounds.include(new LatLng(latDouble, lngDouble));
			}
		} else {
			Log.d("停车场", "附近没有停车场");
		}
	}

	/**
	 * 点击Marker事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (aMap != null) {
			Double latitude = marker.getPosition().latitude;
			Double longitude = marker.getPosition().longitude;
			for (int i = 0; i < parkList.size(); i++) {
				try {
					String latDoubless = parkList.get(i).getString("latitude");
					String lngDoubless = parkList.get(i).getString("longitude");
					if (latDoubless.equals(latitude.toString())
							&& lngDoubless.equals(longitude.toString())) {
						tv_parkNameD.setText(parkList.get(i).getString(
								"parkName"));
						String strtype;
						if ("00".equals(parkList.get(i).getString("parkType"))) {
							strtype = "室内停车场";
						} else {
							strtype = "室外停车场";
						}
						tv_parkTypeD.setText(strtype);
						tv_parkAddrD.setText(parkList.get(i).getString(
								"parkAddr"));
						tv_parkidD.setText(parkList.get(i).getString("parkId")
								+ "");
						// 1.将两个经纬度点转换成距离
						LatLng startLatLng = new LatLng(
								Double.valueOf(latDoubless),
								Double.valueOf(lngDoubless));
						// 广州的数据 应该是定位的经纬度
						LatLng endLatLng = new LatLng(
								Double.valueOf(latDouble),
								Double.valueOf(lngDouble));
						String str = String.valueOf(AMapUtils
								.calculateLineDistance(startLatLng, endLatLng));
						String location = str.substring(0, str.indexOf("."));
						tv_distanceD.setText(location + " 米");
						include_detail.setVisibility(View.VISIBLE);
						mNaviEnd = new NaviLatLng(Double.valueOf(latDoubless),
								Double.valueOf(lngDoubless));
					} else {
						/*
						 * Toast.makeText(LocationActivity.this, "您点击的是：" +
						 * marker
						 * .getPosition().latitude+".."+Double.valueOf(latDouble
						 * )
						 * +"---"+Double.valueOf(lngDouble)+".."+marker.getPosition
						 * ().longitude , Toast.LENGTH_LONG).show();
						 */
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		// ToastUtil.show(LocationActivity.this, "地图加载加载");
		// CameraUpdateFactory.zoomTo(18);
		// addMarkersToMap();

	}

	// 定位被激活的时候调用，把provider获取的地点数据关联到View
	// 激活定位
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAmapLocationManager == null) {
			// 激活以后，一旦获取到最新的地点更新数据，就保存起来
			mAmapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			// mAmapLocationManager.setGpsEnable(true);
			// LocationManagerProxy.NETWORK_PROVIDER
			mAmapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, -1, 15, this);
		}
	}

	// 取消定位的时候调用：释放资源
	@Override
	public void deactivate() {
		mListener = null;
		if (mAmapLocationManager != null) {
			mAmapLocationManager.removeUpdates(this);
			mAmapLocationManager.destroy();
		}
		mAmapLocationManager = null;

	}

	/**
	 * OnLocationChangedListener 重写的方法
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	int requestCounts = 0;

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		requestCounts++;
		Intent intent = getIntent();
		isBack = intent.getBooleanExtra("isBack", false);
		if (mListener != null && aLocation != null) {
			this.aLocation = aLocation;
			if (isBack == false) {
				if (aLocation.getAMapException().getErrorCode() == 0) {
					mListener.onLocationChanged(aLocation);// 显示系统小蓝点
					// 获取当前定位的经纬度
					UserInfo.curr_geoLat = aLocation.getLatitude();
					UserInfo.curr_geoLng = aLocation.getLongitude();
					LogUtil.d("...", UserInfo.curr_geoLat + "----"
							+ UserInfo.curr_geoLng);
					// ToastUtil.show(LocationActivity.this,
					// UserInfo.curr_geoLat+"----"+UserInfo.curr_geoLng);
					cityCode = aLocation.getCityCode();
					// 判断是否定位在广州
					if ("020".equals(cityCode)) {
						latDouble = UserInfo.curr_geoLat;
						lngDouble = UserInfo.curr_geoLng;
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(latDouble, lngDouble), 16));
					} else {
						// 创建弹窗实例
						Builder builder = new Builder(this);
						builder.setTitle(getString(R.string.tip));
						builder.setMessage(getString(R.string.service));
						builder.setPositiveButton(getString(R.string.yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 给参数经纬度赋值 定位到广州的指定地点
										latDouble = latDouble_gz;
										lngDouble = lngDouble_gz;
										UserInfo.curr_geoLat = latDouble;
										UserInfo.curr_geoLng = lngDouble;
										aMap.moveCamera(CameraUpdateFactory
												.newLatLngZoom(new LatLng(
														latDouble, lngDouble),
														16));
										parkListLoad();
									}
								});
						// 显示弹窗
						builder.show();
					}
					// 获取数据
					if (latDouble != null && lngDouble != null) {
						parkListLoad();
						stopLocation();
					}

				}
			} else {
				if (aLocation.getAMapException().getErrorCode() == 0) {
					mListener.onLocationChanged(aLocation);// 显示系统小蓝点
					// 获取当前定位的经纬度
					UserInfo.curr_geoLat = aLocation.getLatitude();
					UserInfo.curr_geoLng = aLocation.getLongitude();
					cityCode = aLocation.getCityCode();
					// 判断是否定位在广州
					if ("020".equals(cityCode)) {
						latDouble = UserInfo.curr_geoLat;
						lngDouble = UserInfo.curr_geoLng;
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(latDouble, lngDouble), 16));
					} else {
						// 给参数经纬度赋值 定位到广州的指定地点
						latDouble = latDouble_gz;
						lngDouble = lngDouble_gz;
						UserInfo.curr_geoLat = latDouble;
						UserInfo.curr_geoLng = lngDouble;
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(latDouble, lngDouble), 16));
					}

				}
				// 获取数据
				if (latDouble != null && lngDouble != null) {
					parkListLoad();
					stopLocation();
				}

			}
		}

	}

	/**
	 * 销毁定位
	 */

	private void stopLocation() {
		mListener = null;
		if (mAmapLocationManager != null) {
			mAmapLocationManager.removeUpdates(this);
			mAmapLocationManager.destroy();
		}
		mAmapLocationManager = null;
		aMap.setOnCameraChangeListener(this); // 地图移动事件
	}

	/**
	 * 此方法需存在
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		if (loginStatus == null || (!loginStatus.equals(UserInfo.loginStatus))) {
			loginStatus = UserInfo.loginStatus;
			switch (loginStatus) {
			case "login":
				initUrlImg();
				loginLayout.setVisibility(View.VISIBLE);
				notLoginLayout.setVisibility(View.GONE);
				userName.setText(UserInfo.userLogin);
				carName.setText(UserInfo.carName);
				break;
			case "register":
				initDefaultImg();
				loginLayout.setVisibility(View.VISIBLE);
				notLoginLayout.setVisibility(View.GONE);
				carName.setText(UserInfo.carName);
				userName.setText(UserInfo.userLogin);
				break;
			case "exit":
				initDefaultImg();
				notLoginLayout.setVisibility(View.VISIBLE);
				loginLayout.setVisibility(View.GONE);
				userName.setText(UserInfo.userLogin);
				break;
			default:
				break;
			}
		}
		if (imgUrl == null
				|| (imgUrl != null && !imgUrl.equals(UserInfo.imgUrl))) {
			initUrlImg();
		}
		if (StringUtils.isNullOrEmpty(carNameString)
				|| (StringUtils.isNotNull(carNameString) && carNameString
						.equals(UserInfo.carName))) {
			carName.setText(UserInfo.carName);
		}
		if ("register".equals(UserInfo.loginStatus)
				|| "login".equals(UserInfo.loginStatus)) {
			checkOrder();
		}

	}

	/**
	 * 此方法需存在
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		stopLocation();
		deactivate();
	}

	/**
	 * 此方法需存在
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 此方法需存在
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
		/*
		 * // 这是最后退出页，所以销毁导航和播报资源 AMapNavi.getInstance(this).destroy();// 销毁导航
		 * TTSController.getInstance(this).stopSpeaking();
		 * TTSController.getInstance(this).destroy();
		 */
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.open:// 打开抽屉
			drawerLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.rl_search:// 目的地搜索
			Intent searchIntent = new Intent(LocationActivity.this,
					KeywordSearchActivity.class);
			searchIntent.putExtra("cityCode", cityCode);
			startActivityForResult(searchIntent, 112);
			break;
		case R.id.pesoner:
			StatusUtil.StatusCheck(LocationActivity.this);
			break;
		case R.id.ll_login:
			// StatusUtil.StatusCheck(LocationActivity.this);
			startActivity(new Intent(LocationActivity.this,
					UserInfosActivity.class));
			break;
		case R.id.ll_notLogin:
			StatusUtil.StatusCheck(LocationActivity.this);
			break;
		case R.id.iv_user_image:
			StatusUtil.StatusCheck(LocationActivity.this);
			break;
		case R.id.money:// 钱包余额
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						NotecaseBalancedActivity.class));
			}
			break;
		case R.id.preferential:// 优惠推广
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						AdverListActivity.class));
			}
			break;
		case R.id.parkNotes:// 停车记录
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						StopCarRecordActivity.class));
			}
			break;
		case R.id.invoice:// 发票邮寄
			// if(StatusUtil.StatusCheck(LocationActivity.this)){
			// SendReceipt();
			// }
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				SendReceipt();
			}
			break;
		case R.id.orderList:// 预约列表
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						SubscribeListActivity.class));
			}
			break;

		case R.id.choosePackage:// 套餐选配
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						PackageSelectionActivity.class));
			}
			break;
		case R.id.myMessage:// 我的消息
			if ("exit".equals(UserInfo.loginStatus)) {
				startActivity(new Intent(LocationActivity.this,
						LoginActivity.class));
			} else {
				startActivity(new Intent(LocationActivity.this,
						NewsListActivity.class));
			}
			break;
		case R.id.systemSetting:// 系统设置
			startActivity(new Intent(LocationActivity.this,
					PersonalSettingActivity.class));
			break;
		case R.id.iv_location:// 定位到当前位置
			aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					aLocation.getLatitude(), aLocation.getLongitude())));
			break;
		case R.id.tv_parkGZ:
			aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
					latDouble_gz, lngDouble_gz)));
			break;

		case R.id.parking_list:// 停车场列表
			// 加载的广州数据 应换成当前定位的经纬度
			Intent parkIntent = new Intent(this, ParkedListActivity.class);
			parkIntent.putExtra("lat", latDouble);
			parkIntent.putExtra("lng", lngDouble);
			startActivity(parkIntent);
			break;
		case R.id.daohang:// 导航
			// 加载的广州数据 应换成当前定位的经纬度
			mNaviStart = new NaviLatLng(latDouble, lngDouble);
			mStartPoints.add(mNaviStart);
			mEndPoints.add(mNaviEnd);
			AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
					mEndPoints, null, AMapNavi.DrivingDefault);
			mRouteCalculatorProgressDialog.show();
			break;
		case R.id.iv_orders:// 订单快捷按钮
			Intent oIntent = new Intent(LocationActivity.this,
					StopCarDetailActivity.class);
			oIntent.putExtra("id", orderId);
			LogUtil.d("在库订单 单号:", orderId + "......iiiiii");
			startActivity(oIntent);
			break;

		case R.id.rl_apply:// 申请预约
			if (StatusUtil.StatusCheck(LocationActivity.this)) {
				Intent i = new Intent(this, SubscribeActivity.class);
				i.putExtra("parkId", tv_parkidD.getText().toString());
				startActivity(i);
			}
			break;
		case R.id.jingchang:// 进场
			if (StatusUtil.StatusCheck(LocationActivity.this)) {
				Intent intent = new Intent(this, EnterPark.class);
				intent.putExtra("parkId", tv_parkidD.getText().toString());
				startActivity(intent);

			}
			break;
		default:
			break;
		}

	}

	/**
	 * 邮递发票-掉接口（查该用户最大开发票金额）
	 */
	private void SendReceipt() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.GET_MAXPOST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("邮递发票", response);
						try {
							JSONObject object = new JSONObject(response);
							if ("000".equals(object.getString("code"))) {
								String maxMoney = object.getString("result");
								PreferenceUtil preferenceUtil = PreferenceUtil
										.getInstance(getApplicationContext());
								preferenceUtil.put("maxMoney", maxMoney);
								startActivity(new Intent(LocationActivity.this,
										SendReceiptActivity.class));
							} else if ("001".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查询不到数据");
							} else if ("002".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"超时");
							} else if ("003".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"请求数据不正确");
							} else if ("004".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("005".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("006".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"操作失败");
							} else if ("010".equals("code")) {
								DialogUtil.loginAgain(LocationActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// ToastUtil.showLong(LocationActivity.this, response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(LocationActivity.this,
								VolleyErrorUtil.getMessage(error,
										LocationActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}

	Double geoLats;
	Double geoLngs;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 113) {
			if (requestCode == 112) {
				Bundle bundle = data.getExtras();
				String string = bundle.getString("address");
				geoLats = bundle.getDouble("lat");
				geoLngs = bundle.getDouble("lon");
				Log.e("tag", string + "|||||" + geoLats + geoLngs + ".....");
				LatLonPoint latLonPoint = new LatLonPoint(geoLats, geoLngs);
				getAddress(latLonPoint);
				// 添加标记
				LatLng marker1 = new LatLng(geoLats, geoLngs);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker1, 14));
				aMap.addMarker(new MarkerOptions()
						.perspective(true)
						.position(marker1)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
				// 请求网络数据
				latDouble = geoLats;
				lngDouble = geoLngs;
				if (latDouble != null && lngDouble != null) {
					System.out.println("搜索周边的停车场。。。。。。。。。" + latDouble
							+ lngDouble);
					parkListLoad();
				}
			}
		} else {
			/*
			 * UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
			 * resultCode); if (ssoHandler != null) {
			 * ssoHandler.authorizeCallBack(requestCode, resultCode, data); }
			 * super.onActivityResult(requestCode, resultCode, data);
			 */
		}
	};

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				String addressName = "经纬度值:" + address.getLatLonPoint()
						+ "\n位置描述:" + address.getFormatAddress();
				Log.e("tag", addressName);

			} else {
			}
		} else if (rCode == 27) {
		} else if (rCode == 32) {
		} else {
		}
	}

	/**
	 * 逆地理编码回调
	 */
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				String addressName = result.getRegeocodeAddress()
						.getFormatAddress();
				String province = result.getRegeocodeAddress().getProvince();
			} else {
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_key);
		} else {
			// ToastUtil.show(GeocoderActivity.this,
			// getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 重写退出事件
	 */
	private long mExitTime;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				BaseApplication.getInstance().finishAllActivity();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 地图移动事件
	 */
	@Override
	public void onCameraChange(CameraPosition positions) {

	}

	/**
	 * 地图完成移动后的事件
	 */
	@Override
	public void onCameraChangeFinish(CameraPosition positions) {
		// 加载的广州数据 应换成当前定位的经纬度startLatLng
		// 进行搜索周围的停车场数据
		LatLng startLatLng = new LatLng(latDouble, lngDouble);
		int location = (int) AMapUtils.calculateLineDistance(startLatLng,
				positions.target);
		latDouble = positions.target.latitude;
		lngDouble = positions.target.longitude;
		if (location > 300) {
			if (latDouble != null && lngDouble != null) {
				LogUtil.e("地图移动距离",
						location + "定位的经纬度：" + aLocation.getLatitude() + "---"
								+ aLocation.getLongitude());
				tempLat = latDouble;
				tempLng = lngDouble;
				parkListLoad();
			}
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
		// TODO Auto-generated method stub
		mRouteCalculatorProgressDialog.dismiss();
	}

	@Override
	public void onCalculateRouteSuccess() {
		mRouteCalculatorProgressDialog.dismiss();
		Intent intent = new Intent(LocationActivity.this,
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
	public void onNaviInfoUpdate(NaviInfo arg0) {
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

	/*
	 * 地图的点击事件
	 * 
	 * @see
	 * com.amap.api.maps.AMap.OnMapClickListener#onMapClick(com.amap.api.maps
	 * .model.LatLng)
	 */
	@Override
	public void onMapClick(LatLng arg0) {
		include_detail.setVisibility(View.GONE);
	}

}
