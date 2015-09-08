package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DaoHangUtils;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.TTSController;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 停车场详情 com.sunrise.epark.activity.ParkedDetailsActivity
 * 
 * @author 李敏 create at 2015年8月25日 上午11:30:16
 */
public class ParkedDetailsActivity extends BaseActivity implements
		OnClickListener, AMapNaviListener, AMapNaviViewListener {

	private RelativeLayout rl_back;

	private LinearLayout ll_enter, ll_yuyue;


	public String parkId;

	private TextView tv_parkName, tv_parkType, tv_parkPrice, tv_parkCount,
			tv_parkAddress, tv_remainPark;

	private ImageView iv_img;

	private Button btn_daohang;

	private Double curr_Lat;

	private Double curr_lng;

	// 起点终点
	private NaviLatLng mNaviStart;
	private NaviLatLng mNaviEnd;

	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

	private ProgressDialog mRouteCalculatorProgressDialog;// 路径规划过程显示状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parked_details);
		// 语音播报开始
		TTSController.getInstance(this).startSpeaking();
		Intent intent = getIntent();
		parkId = intent.getStringExtra("parkId");
		curr_Lat = intent.getDoubleExtra("curr_Lat", 0.0);
		curr_lng = intent.getDoubleExtra("curr_lng", 0.0);
		initView();
		initData();
	}

	/**
	 * 加载数据
	 */
	private void initData() {
		// 导航
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		AMapNavi.getInstance(this).setAMapNaviListener(ttsManager);// 设置语音模块播报
		mRouteCalculatorProgressDialog = new ProgressDialog(this);
		mRouteCalculatorProgressDialog.setCancelable(true);
		AMapNavi.getInstance(ParkedDetailsActivity.this).setAMapNaviListener(
				this);
		getParkDetail();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		rl_back = (RelativeLayout) findViewById(R.id.back);
		rl_back.setOnClickListener(this);
		tv_parkName = (TextView) findViewById(R.id.parkName);
		tv_parkType = (TextView) findViewById(R.id.parkType);
		tv_parkPrice = (TextView) findViewById(R.id.parkPrice);
		tv_parkCount = (TextView) findViewById(R.id.parkCount);
		tv_parkAddress = (TextView) findViewById(R.id.parkAddress);
		tv_remainPark = (TextView) findViewById(R.id.remainPark);

		iv_img = (ImageView) findViewById(R.id.iv_img);
		btn_daohang = (Button) findViewById(R.id.btn_daohang);
		btn_daohang.setOnClickListener(this);
		ll_enter = (LinearLayout) findViewById(R.id.ll_enter);
		ll_enter.setOnClickListener(this);
		ll_yuyue = (LinearLayout) findViewById(R.id.ll_yuyue);
		ll_yuyue.setOnClickListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		// 导航
		case R.id.btn_daohang:
			mNaviStart = new NaviLatLng(curr_Lat, curr_lng);
			mStartPoints.add(mNaviStart);
			mEndPoints.add(mNaviEnd);
			AMapNavi.getInstance(this).calculateDriveRoute(mStartPoints,
					mEndPoints, null, AMapNavi.DrivingDefault);
			mRouteCalculatorProgressDialog.show();
			break;
		// 预约
		case R.id.ll_yuyue:
			if (StatusUtil.StatusCheck(ParkedDetailsActivity.this)) {
				Intent i = new Intent(this, SubscribeActivity.class);
				i.putExtra("parkId", parkId);
				startActivity(i);
			}
			break;
		// 进场
		case R.id.ll_enter:
			if (StatusUtil.StatusCheck(ParkedDetailsActivity.this)) {
				Intent intent = new Intent(this, EnterPark.class);
				intent.putExtra("parkId", parkId);
				startActivity(intent);

			}

			break;
		default:
			break;
		}
	}

	private void getParkDetail() {
		StringRequest parks = new StringRequest(Method.POST,
				Urls.STOPCAR_MINGXI, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// closeLoadingDialog();
						LogUtil.e("停车场明细", response);
						try {
							JSONObject jsonObject = new JSONObject(response);
							String code = jsonObject.getString("code");
							if ("000".equals(code.trim())) {
								updateData(jsonObject);
							}
							if ("001".equals(code)) {
							}
							if ("010".equals("code")) {
								DialogUtil
										.loginAgain(ParkedDetailsActivity.this);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(ParkedDetailsActivity.this,
								VolleyErrorUtil.getMessage(error,
										ParkedDetailsActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("parkId", parkId);
				return map;
			}
		};
		parks.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(parks);

	}

	/**
	 * @param jsonObject
	 */
	protected void updateData(JSONObject jsonObject) {

		try {
			jsonObject = jsonObject.getJSONObject("result");
			tv_parkName.setText(jsonObject.getString("parkName"));
			String str;
			if ("00".equals(jsonObject.getString("parkType"))) {
				str = "室内停车场";
			} else {
				str = "室外停车场";
			}
			tv_parkType.setText(str);
			tv_parkCount.setText(jsonObject.getString("parkSum"));
			tv_parkAddress.setText(jsonObject.getString("parkAddr"));
			tv_parkPrice.setText(jsonObject.getString("dayPrice"));
			int remainPark = Integer.parseInt(jsonObject
					.getString("remainPark"));
			if (remainPark <= 0) {
				tv_remainPark.setText("0个");
				ll_yuyue.setVisibility(View.GONE);
			} else {
				tv_remainPark.setText(jsonObject.getString("remainPark") + "个");
			}
			String imgId = jsonObject.getString("imgUrl");
			if (StringUtils.isNotNull(imgId)) {
				@SuppressWarnings("deprecation")
				ImageRequest imageRequest = new ImageRequest(
						imgId,
						new Response.Listener<Bitmap>() {
							@Override
							public void onResponse(
									Bitmap response) {
								iv_img.setImageBitmap(response);
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

			//getImage(imgId);
			mNaviEnd = new NaviLatLng(Double.valueOf(jsonObject
					.getString("latitude")), Double.valueOf(jsonObject
					.getString("longitude")));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(jsonObject);

	}

	/**
	 * @param imgId
	 *//*
	private void getImage(String imgId) {
		imgId = "http://wanzao2.b0.upaiyun.com/system/pictures/7950220/original/20140417000103.png";
		ImageLoader imageLoader = new ImageLoader(mQueue, new MyImageCache());
		ImageListener listener = ImageLoader
				.getImageListener(iv_img, android.R.drawable.ic_menu_rotate,
						android.R.drawable.ic_delete);
		imageLoader.get(imgId, listener);
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

	}*/

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
		Intent intent = new Intent(ParkedDetailsActivity.this,
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 删除导航监听
		AMapNavi.getInstance(this).removeAMapNaviListener(this);
	}

}
