package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DaoHangUtils;
import com.sunrise.epark.util.DateTool;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.TTSController;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubscribeDetailActivity extends BaseActivity implements
		OnClickListener, AMapNaviListener, AMapNaviViewListener {

	private ImageView back;
	// 展示信息
	private TextView parkName;
	private TextView parkType;
	private TextView parkAddress;
	private TextView carLicence;
	private TextView money;
	private TextView startDate;
	private TextView endDate;
	private TextView tip;
	private ImageView imgView;
	// 头像url
	private String headUrl;
	// 扣除的钱
	private String discount;
	//
	private String totelMoney;
	// 订单号
	private String orderId;
	// 停车场id
	private String parkid;
	// 按钮
	private Button sureEnter;
	private Button cancelOrder;
	// 导航按钮
	private ImageButton guide;
	// 弹出对话框
	private Dialog dialog;
	// 返给客户的钱
	private String amount;

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
		setContentView(R.layout.subscribe_detail);
		// 语音播报开始
		TTSController.getInstance(this).startSpeaking();
		orderId = getIntent().getStringExtra("orderId");
		initCom();
	}

	private void initCom() {
		// 导航
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
		mRouteCalculatorProgressDialog = new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);
		AMapNavi.getInstance(SubscribeDetailActivity.this).setAMapNaviListener(
				this);

		back = (ImageView) findViewById(R.id.sub_d_back);
		back.setOnClickListener(this);
		parkName = (TextView) findViewById(R.id.sub_detail_name);
		parkType = (TextView) findViewById(R.id.sub_detail_type);
		parkAddress = (TextView) findViewById(R.id.sub_detail_address);
		carLicence = (TextView) findViewById(R.id.sub_detail_licence);
		money = (TextView) findViewById(R.id.sub_detail_money);
		startDate = (TextView) findViewById(R.id.sub_detail_start);
		endDate = (TextView) findViewById(R.id.sub_detail_end);
		tip = (TextView) findViewById(R.id.sub_detail_tip);
		imgView = (ImageView) findViewById(R.id.sub_detail_img);

		guide = (ImageButton) findViewById(R.id.sub_detail_daohang);
		sureEnter = (Button) findViewById(R.id.sub_detail_ok);
		cancelOrder = (Button) findViewById(R.id.sub_detail_cancel);
		// 注册舰艇事件
		guide.setOnClickListener(this);
		sureEnter.setOnClickListener(this);
		cancelOrder.setOnClickListener(this);

		// 设置提示信息
		discount = "30%";// 要扣除的百分比

		initDate();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();

		switch (id) {
		case R.id.sub_d_back:
			finish();
			break;
		case R.id.sub_detail_daohang:// 导航
			ToastUtil.show(SubscribeDetailActivity.this, mNaviEnd + "---"
					+ UserInfo.curr_geoLat + "---" + UserInfo.curr_geoLng);
			mNaviStart = new NaviLatLng(UserInfo.curr_geoLat,
					UserInfo.curr_geoLng);
			mStartPoints.add(mNaviStart);
			mEndPoints.add(mNaviEnd);
			AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
					mEndPoints, null, AMapNavi.DrivingDefault);
			mRouteCalculatorProgressDialog.show();
			break;
		case R.id.sub_detail_ok:// 确认进场
			makeAppointmentParkIn();
			break;
		case R.id.sub_detail_cancel:// 取消预约
			if (DateTool.XiaoShiCha(
					DateTool.string2date(startDate.getText().toString()),
					new Date()) <= 2) {
				dialog = cancelDialog(this);
				dialog.show();
			} else {
				cancleSub();
			}
			break;
		case R.id.sub_canpop_yes:// 确认取消
			cancleSub();
			dialog.dismiss();
			break;
		case R.id.sub_canpop_no:// 取消
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

	/**
	 * 确认取消
	 */
	private void cancleSub() {
		showLoadingDialog();

		StringRequest register = new StringRequest(Method.POST,
				Urls.CANCEL_MAKEAPPOINTMENT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("取消预约", response);

						try {
							JSONObject jObject = new JSONObject(response);
							LogUtil.e("取消预约", jObject.toString());
							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								DialogUtil.showDialog(SubscribeDetailActivity.this,
										"取消成功", true);
								finish();
							} else if ("010".equals(code.trim())) {
								// 失败
								DialogUtil
										.loginAgain(SubscribeDetailActivity.this);

							} else {
								DialogUtil.showToast(getApplicationContext(),
										"操作失败");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("orderId", orderId);
				map.put("payAmount", amount);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}

	/**
	 * 预约进场
	 */
	private void makeAppointmentParkIn() {
		showLoadingDialog();

		StringRequest register = new StringRequest(Method.POST,
				Urls.MAKEAPPOINTMENT_PARKIN, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("预约进场", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								String id = jObject.getJSONObject("result")
										.getString("orderId");
								Intent intent = new Intent(
										SubscribeDetailActivity.this,
										CoverLayer.class);
								intent.putExtra("title",
										getString(R.string.cover_order));
								intent.putExtra("flag", "enter");
								intent.putExtra("order", id);
								startActivity(intent);
							} else {
								// 失败
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderId);
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("parkId", parkid);
				map.put("carName", UserInfo.carName);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	/**
	 * 获取数据
	 */
	private void initDate() {
		showLoadingDialog();

		StringRequest register = new StringRequest(Method.POST,
				Urls.SUBSCRIBE_DETAIL, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("预约详情", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								updateData(jObject);
							} else {
								// 失败
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
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
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	/**
	 * 加载页面数据
	 * 
	 * @throws JSONException
	 */
	private void updateData(JSONObject jObject) throws JSONException {
		JSONObject results = jObject.getJSONObject("result");
		parkName.setText(results.getString("parkName"));
		parkAddress.setText(results.getString("parkAddr"));
		carLicence.setText(results.getString("carName"));

		totelMoney = results.getString("orderAmount");
		// 初始值为总金额
		amount = totelMoney;
		money.setText(getString(R.string.how_yuan, totelMoney));
		parkType.setText("00".equals(results.getString("parkType")) ? getString(R.string.parkType_shinei)
				: getString(R.string.parkType_shiwai));
		startDate.setText(results.getString("startTime"));
		endDate.setText(results.getString("endTime"));
		discount = results.getString("parkPer") + "%";
		parkid = results.getString("parkId");

		tip.setText(Html.fromHtml(String.format(
				getResources().getString(R.string.sub_detail_tip),
				discount,
				"<font color=\"#999999\">"
						+ String.format(
								getResources().getString(
										R.string.sub_detail_money), discount)
						+ "</font>")));
		headUrl = results.getString("imgUrl");
	//	getImage(headUrl);
		if (StringUtils.isNotNull(headUrl)) {
			@SuppressWarnings("deprecation")
			ImageRequest imageRequest = new ImageRequest(
					headUrl,
					new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(
								Bitmap response) {
							imgView.setImageBitmap(response);
						}
					}, 0, 0, Config.RGB_565,
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(
								VolleyError error) {
						}
					});
			BaseApplication.mQueue.add(imageRequest);
			// ImageLoader imageLoader = new
			// ImageLoader(
			// BaseApplication.mQueue, new
			// BitmapCache());
			// ImageListener listener = ImageLoader
			// .getImageListener(
			// parkImg,
			// R.drawable.parkinglots_example,
			// R.drawable.parkinglots_example);
			// imageLoader.get(imgUrl, listener);
		}

		getLatLng(results.getString("parkId"));
	}

	/**
	 * 
	 */
	private void getLatLng(final String parkId) {
		StringRequest register = new StringRequest(Method.POST,
				Urls.STOPCAR_MINGXI, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("停车场详情", response);
						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");

							switch (code) {
							case "000":
								// 成功
								JSONObject results = jObject
										.getJSONObject("result");
								String lat = results.getString("latitude");
								String lng = results.getString("longitude");
								mNaviEnd = new NaviLatLng(Double.valueOf(lat),
										Double.valueOf(lng));
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
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						} 
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						showLoadingDialog("请求失败");
						ToastUtil.showLong(SubscribeDetailActivity.this,
								VolleyErrorUtil.getMessage(error,
										SubscribeDetailActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("parkId",parkId);
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	/**
	 * 弹出确认取消对话框
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog cancelDialog(Context context) {
		Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.subscribe_cancel, null);
		// 获取对象
		TextView tip = (TextView) popView.findViewById(R.id.sub_canpop_tip);
		TextView totle = (TextView) popView.findViewById(R.id.sub_canpop_t);
		TextView fuwu = (TextView) popView.findViewById(R.id.sub_canpop_f);
		TextView act = (TextView) popView.findViewById(R.id.sub_canpop_a);
		Button cancel = (Button) popView.findViewById(R.id.sub_canpop_no);
		Button sure = (Button) popView.findViewById(R.id.sub_canpop_yes);

		cancel.setOnClickListener(this);
		sure.setOnClickListener(this);
		// 赋值
		tip.setText(getString(R.string.sub_cancel_tip, discount));
		totle.setText(getString(R.string.how_yuan, totelMoney));
		// 费用
		String[] fy = delMoney(totelMoney, discount);
		totle.setText(Html.fromHtml(getString(R.string.how_yuan,
				"<font color=\"#4DC060\">" + totelMoney + "</font>")));
		fuwu.setText(Html.fromHtml(getString(R.string.how_yuan,
				"<font color=\"#FF0000\">" + fy[0] + "</font>")));
		act.setText(Html.fromHtml(getString(R.string.how_yuan,
				"<font color=\"#4DC060\">" + fy[1] + "</font>")));
		// 折扣后返给客户的
		amount = fy[1];
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}

	/**
	 * 处理取消扣费
	 * 
	 * @param totle
	 * @param dis
	 * @return
	 */
	private String[] delMoney(String totle, String dis) {
		String[] str = new String[2];
		// 计算服务费
		int d = Integer.parseInt(dis.split("%")[0]);
		String temp = totle.split("元")[0];
		float t = Float.parseFloat("".equals(temp) ? "0" : temp);
		float fuw = (float) Math.rint(t * d / 100.0);
		str[0] = fuw + "";
		str[1] = (t - fuw) + "";
		return str;
	}

	/**
	 * 获取图片，如果缓存中存在则读缓存
	 * 
	 * @param url
	 *//*
	private void getImage(String url) {
		url = "http://wanzao2.b0.upaiyun.com/system/pictures/7950220/original/20140417000103.png";
		ImageLoader imageLoader = new ImageLoader(mQueue, new MyImageCache());
		ImageListener listener = ImageLoader
				.getImageListener(imgView, android.R.drawable.ic_menu_rotate,
						android.R.drawable.ic_delete);
		imageLoader.get(url, listener);
	}

	class MyImageCache implements ImageCache {

		private LruCache<String, Bitmap> mCache;

		public MyImageCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}

			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}

	}
*/
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
		Intent intent = new Intent(SubscribeDetailActivity.this,
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
	}
}
