package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.menulistview.SwipeMenu;
import com.sunrise.epark.menulistview.SwipeMenuCreator;
import com.sunrise.epark.menulistview.SwipeMenuItem;
import com.sunrise.epark.menulistview.SwipeMenuListView;
import com.sunrise.epark.menulistview.SwipeMenuListView.IXListViewListener;
import com.sunrise.epark.menulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.PreferenceUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/***
 * 
 * this class is used for...个人中心-我的信息-车牌
 * 
 * @wangyingjie create
 * @time 2015年7月21日上午9:32:56
 */
public class MyCarActivity extends BaseActivity implements OnClickListener,
		IXListViewListener {

	private RelativeLayout back1;
	/** 返回 */
	private TextView add;// 新增车牌
	private List<ImageView> rdbs = new ArrayList<ImageView>();

	private List<String[]> listitem;
	private boolean status = false;// 判断是否选择车牌

	private String chepai;
	private String deleteCarId, deleteCarName;
	private Dialog loading1, loading3, loading2, dialog3;

	private SwipeMenuListView lv_location;
	private ad_local_location adapter;
	private SharedPreferences time;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 666) {
				onLoad();
				adapter.notifyDataSetChanged();
			}
		}

	};

	private SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
			// set item background
			// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
			// 0xCE)));
			openItem.setBackground(R.color.red);
			// set item width
			openItem.setWidth(dp2px(90));
			// set item title
			openItem.setTitle("删除");
			// set item title fontsize
			openItem.setTitleSize(18);
			// set item title font color
			openItem.setTitleColor(Color.WHITE);
			// add to menu
			menu.addMenuItem(openItem);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_car);
		initView();
		initBind();
		findCarId();
	}

	private void initView() {
		// dialog3=DialogUtil.pleaseSelectCarIdDialog(MyCarActivity.this,"请选择默认车牌");
		lv_location = (SwipeMenuListView) findViewById(R.id.listview);
		loading1 = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		loading3 = DialogUtil.createLoadingDialog(this, "正在删除数据，请稍后");
		loading2 = DialogUtil.createLoadingDialog(this, "正在保存数据,请稍后");
		back1 = (RelativeLayout) findViewById(R.id.back1);
		add = (TextView) findViewById(R.id.add);
		listitem = new ArrayList<String[]>();
		adapter = new ad_local_location(listitem);
		time = getSharedPreferences("time", 1);
		lv_location.setAdapter(adapter);
		lv_location.setPullLoadEnable(true);
		lv_location.setPullRefreshEnable(true);
		lv_location.setXListViewListener(this);
		lv_location.setMenuCreator(creator);
		lv_location.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					// 删除数据
					deleteCarId = listitem.get(position)[2];
					deleteCarName = listitem.get(position)[0];
					deleteCarId(deleteCarId, deleteCarName);
					adapter.notifyDataSetChanged();
					break;
				}
				return false;
			}
		});
		onRefresh();

		lv_location.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				for (int i = 0; i < rdbs.size(); i++) {
					if (i != arg3) {
						rdbs.get(i).setBackgroundResource(
								R.drawable.btn_pay_normal);
					} else {
						rdbs.get(i).setBackgroundResource(
								R.drawable.btn_pay_click);
						status = true;
					}
				}
				chepai = listitem.get((int) arg3)[2];
			}
		});
	}

	/** 删除车牌 */
	protected void deleteCarId(final String carId, final String carName) {
		loading3.show();
		StringRequest register = new StringRequest(Method.POST,
				Urls.DELETE_CAR, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("删除车牌", response);
						try {
							JSONObject jsonObject = new JSONObject(response);
							if ("000".equals(jsonObject.getString("code"))) {
								// DialogUtil.showToast(getApplicationContext(),jsonObject.getString("result"));
								StatusUtil.AddCar(getApplicationContext(), "");
								findCarId();
								/** 删除成功以后，重新刷新一下页面 */
							} else if ("001".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查找不到数据，请重新输入");
							} else if ("004".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("006".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										jsonObject.getString("result"));
							} else if ("010".equals(jsonObject
									.getString("code"))) {
								DialogUtil.loginAgain(MyCarActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading3 != null && loading3.isShowing()) {
								loading3.dismiss();
							}
							// finish();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(MyCarActivity.this, VolleyErrorUtil
								.getMessage(error, MyCarActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("carId", carId);
				map.put("carName", carName);
				LogUtil.i("删除车牌", StringUtils.getMap(map));
				return map;
			}
		};
		BaseApplication.mQueue.add(register);

	}

	private void initBind() {
		back1.setOnClickListener(this);
		add.setOnClickListener(this);
	}

	public class ad_local_location extends BaseAdapter {

		List<String[]> list;

		public ad_local_location(List<String[]> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
			rdbs = new ArrayList<ImageView>();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View v, ViewGroup arg2) {
			v = View.inflate(MyCarActivity.this, R.layout.item_my_car, null);
			if (list.size() == 1) {
				TextView carName = (TextView) v.findViewById(R.id.carName);
				ImageView rdb = (ImageView) v.findViewById(R.id.radiobtn);
				carName.setText(list.get(arg0)[0]);
				// if ("00".equals(list.get(arg0)[1])) {
				rdb.setBackgroundResource(R.drawable.btn_pay_click);
				status = true;
				// } else {
				// rdb.setBackgroundResource(R.drawable.btn_pay_normal);
				// }
				// status = false;
				rdbs.add(rdb);
			} else {
				TextView carName = (TextView) v.findViewById(R.id.carName);
				ImageView rdb = (ImageView) v.findViewById(R.id.radiobtn);
				carName.setText(list.get(arg0)[0]);
				if ("00".equals(list.get(arg0)[1])) {
					rdb.setBackgroundResource(R.drawable.btn_pay_click);
					status = true;
				} else {
					rdb.setBackgroundResource(R.drawable.btn_pay_normal);
				}
				status = false;
				rdbs.add(rdb);
			}
			return v;
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back1:
			if (status) {
				changecarid(chepai);
			} else {
				finish();
				// dialog3.show();
			}
			break;
		case R.id.add:
			/*
			 * Intent intent = new Intent(MyCarActivity.this,
			 * MyCarAddActivity.class); startActivity(intent);
			 */
			Intent intent = new Intent(MyCarActivity.this,
					MyCarAddActivity.class);
			startActivityForResult(intent, 112);
			finish();
			break;
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		ToastUtil.showLong(MyCarActivity.this, "回调。。。");
		if (resultCode == 113) {
			if (requestCode == 112) {
				ToastUtil.showLong(MyCarActivity.this, "回调。。。");
				findCarId();
			}
		}
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { super.onActivityResult(requestCode,
	 * resultCode, data); //String result =
	 * data.getExtras().getString("carName");// 得到新Activity // 关闭后返回的数据
	 * //Log.i("TAG111111111111111d", result);
	 * ToastUtil.showLong(MyCarActivity.this, "回调。。。"); if (resultCode==113) {
	 * if (requestCode==112) { ToastUtil.showLong(MyCarActivity.this, "回调。。。");
	 * findCarId(); } } }
	 */

	/** 查找车辆 */
	private void findCarId() {
		loading1.show();
		StringRequest register = new StringRequest(Method.POST,
				Urls.FIND_CARID, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jObject = new JSONObject(response);
							if ("000".equals(jObject.getString("code"))) {
								JSONArray jObject1 = jObject
										.getJSONArray("result");
								listitem.clear();
								for (int i = 0; i < jObject1.length(); i++) {
									JSONObject jsonObject = jObject1
											.getJSONObject(i);
									PreferenceUtil preferenceUtil = PreferenceUtil
											.getInstance(getApplicationContext());
									preferenceUtil.put("carId",
											jsonObject.getString("carId"));
									preferenceUtil.put("carName",
											jsonObject.getString("carName"));
									listitem.add(new String[] {
											jsonObject.getString("carName"),
											jsonObject.getString("carStatus"),
											jsonObject.getString("carId") });
								}

								adapter.notifyDataSetChanged();
							} else if ("001".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查找不到数据，请重新输入");
							} else if ("002".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"超时");
							} else if ("003".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"请求数据不正确");
							} else if ("004".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("005".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("006".equals(jObject.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("010".equals(jObject.getString("code"))) {
								DialogUtil.loginAgain(MyCarActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							finish();
						} finally {
							if (loading1 != null && loading1.isShowing()) {
								loading1.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(MyCarActivity.this, VolleyErrorUtil
								.getMessage(error, MyCarActivity.this));
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

	/** 修改车牌 */
	private void changecarid(final String carId) {
		loading2.show();
		StringRequest register = new StringRequest(Method.POST,
				Urls.CHANGE_CARID, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("修改车牌", response);
						try {
							JSONObject jsonObject = new JSONObject(response);
							if ("000".equals(jsonObject.getString("code"))) {
								String carName = jsonObject
										.getString("carName");
								StatusUtil.AddCar(getApplicationContext(),
										carName);
							} else if ("001".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查找不到数据，请重新输入");
							} else if ("002".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"超时");
							} else if ("003".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"请求数据不正确");
							} else if ("004".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("005".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("006".equals(jsonObject
									.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"操作失败");
							} else if ("010".equals(jsonObject
									.getString("code"))) {
								DialogUtil.loginAgain(MyCarActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading2 != null && loading2.isShowing()) {
								loading2.dismiss();
							}
							finish();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(MyCarActivity.this, VolleyErrorUtil
								.getMessage(error, MyCarActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("carId", carId);
				LogUtil.i("chelianglieb", StringUtils.getMap(map));
				return map;
			}
		};
		BaseApplication.mQueue.add(register);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	public void onRefresh() {
		// TODO Auto-generated method stub
		// listitem.clear();
		findCarId();
		adapter.notifyDataSetChanged();
		lv_location.setRefreshTime(time.getString("time4", ""));
		handler.sendEmptyMessageDelayed(666, 1000);
	}

	@Override
	public void onLoadMore() {
		handler.sendEmptyMessageDelayed(666, 1000);
	}

	private void onLoad() { // 停止头部和脚部进度条的转动
		lv_location.resetHeaderHeight();
		lv_location.stopRefresh();
		lv_location.stopLoadMore();
		lv_location.setRefreshTime(time.getString("time4", ""));
	}
}
