package com.sunrise.epark.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.ImageUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;

/**
 * 描述:正常出场
 * 
 * @author zhongjy
 * @date 2015年7月30日
 * @version 1.0
 */
public class CarOutConfirmActivity extends BaseActivity implements
		OnClickListener, OnLongClickListener {

	private Bundle bundle;
	private Dialog loading;
	/** 返回 */
	private LinearLayout back, ll_cancle,ll_camera;
	/** 订单号 */
	private TextView orderNum;
	/** 用户名 */
	private TextView userName;
	/** 手机号 */
	private TextView userMobile;
	/** 车牌号 */
	private TextView userCarNum;
	/** 开始时间 */
	private TextView orderStartDate;
	/** 确定进场 */
	private Button confirm;

	private RelativeLayout preferentialArea;

	private EditText preferential;

	private ImageView addImg;

	/** 拍照相关事件 */
	private PopupWindow popWindow;
	private TextView photograph, albums;
	private LinearLayout cancel;
	public static final int PHOTOZOOM = 0; // requestcode 打开本地文件
	public static final int PHOTOTAKE = 1; // requestcode 拍照
	public static final int IMAGE_COMPLETE = 2; // 照片裁剪
	private String photoSavePath;// 路径
	private String photoSaveName;// 照片名字

	private boolean imgStatus;
	@SuppressWarnings("unused")
	private String img;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carout_confirm);
		initView();
		bindEvent();
		initData();
	}

	@SuppressLint("SimpleDateFormat")
	private void initData() {
		bundle = getIntent().getExtras();
		orderNum.setText(bundle.getString("orderNum"));
		userName.setText(bundle.getString("userName"));
		userCarNum.setText(bundle.getString("userCarNum"));
		userMobile.setText(bundle.getString("userMobile"));
		orderStartDate.setText(bundle.getString("orderStartDate"));
		String orderType=bundle.getString("orderType","run");
		if ("running".equals(orderType)) {
			confirm.setVisibility(View.GONE);
			ll_cancle.setVisibility(View.GONE);
			ll_camera.setVisibility(View.GONE);
		}
	}

	private void initView() {
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
		back = (LinearLayout) this.findViewById(R.id.car_out_back);
		orderNum = (TextView) this.findViewById(R.id.car_out_num);
		userName = (TextView) this.findViewById(R.id.car_out_username);
		userCarNum = (TextView) this.findViewById(R.id.car_out_usercarnum);
		userMobile = (TextView) this.findViewById(R.id.car_out_usermobile);
		orderStartDate = (TextView) this.findViewById(R.id.car_out_starttime);
		confirm = (Button) this.findViewById(R.id.confirm_out);
		preferentialArea = (RelativeLayout) this
				.findViewById(R.id.car_out_preferential_area);
		preferential = (EditText) this.findViewById(R.id.car_out_preferential);
		addImg = (ImageView) this.findViewById(R.id.car_out_add_img);
		ll_cancle = (LinearLayout) findViewById(R.id.ll_cancle);
		ll_camera=(LinearLayout) findViewById(R.id.ll_camera);

		File file = new File(Environment.getExternalStorageDirectory(),
				"ParkingLots/image");
		if (!file.exists()) {
			file.mkdirs();
		}
		photoSavePath = Environment.getExternalStorageDirectory()
				+ "/ParkingLots/image/";
		photoSaveName = System.currentTimeMillis() + ".png";
	}

	private void bindEvent() {
		back.setOnClickListener(this);
		confirm.setOnClickListener(this);
		addImg.setOnClickListener(this);
		addImg.setOnLongClickListener(this);
		ll_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm_out:
			String hour = null;
			if (preferentialArea.getVisibility() == View.VISIBLE) {
				hour = preferential.getText().toString().trim();
			}
			if (hour == null || "".equals(hour)) {
				hour = "0";
			}
			confirmOut(hour);
			break;
		case R.id.order_detail_back:
			this.finish();
			break;
		case R.id.car_out_add_img:
			addImg();
			break;
		case R.id.photograph:
			popWindow.dismiss();
			photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
			Uri imageUri = null;
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
			openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(openCameraIntent, PHOTOTAKE);
			break;
		case R.id.albums:
			popWindow.dismiss();
			Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			openAlbumIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(openAlbumIntent, PHOTOZOOM);
			break;
		case R.id.cancel:
			popWindow.dismiss();
			break;
		// 取消订单
		case R.id.ll_cancle:
			cancleOrder();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.car_out_add_img:
			if (imgStatus) {
				new AlertDialog.Builder(this)
						.setTitle(getResources().getString(R.string.tip))
						.setMessage(
								getResources().getString(R.string.del_img_sure))
						.setPositiveButton(
								getResources().getString(R.string.yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										addImg.setImageBitmap(ImageUtil
												.getBitmap(
														CarOutConfirmActivity.this,
														R.drawable.icon_add));
										addImg.setScaleType(ScaleType.CENTER);
										preferentialArea
												.setVisibility(View.INVISIBLE);
										imgStatus = false;
										img = null;
									}
								})
						.setNegativeButton(
								getResources().getString(R.string.cancle),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).setCancelable(true).show();
			}
			break;
		default:
			break;
		}

		return false;
	}

	public void cancleOrder(){
		StringRequest cancleOreder=new StringRequest(Method.POST,Urls.MANAGER_CANCLE_ORDER, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				LogUtil.e("取消订单：" + Urls.MANAGER_CANCLE_ORDER,
						response);
				try {
					JSONObject object= new JSONObject(response);
					String code = object.getString("code");
					if ("000".equals(code)) {
						//showPaySuccessDialog("订单已取消，请线下进行收费!");
						ToastUtil.show(CarOutConfirmActivity.this, "订单已取消，请线下进行收费!");
						Intent intent=new Intent(CarOutConfirmActivity.this,CarManageActivity.class);
						startActivity(intent);
					}
					if ("010".equals(code)) {
						DialogUtil
								.loginAgain(CarOutConfirmActivity.this);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//ToastUtil.show(CarOutConfirmActivity.this, "订单已取消，请线下进行收费!");
			}
		}){
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("orderId", bundle.getString("orderNum"));
				LogUtil.e("订单已取消：" + Urls.MANAGER_CANCLE_ORDER,
						StringUtils.getMap(map));
				return map;
			}
		};
		cancleOreder.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(cancleOreder);
	}

	
	private void confirmOut(final String freehour) {
		loading.show();
		StringRequest confirmOut = new StringRequest(Method.POST,
				Urls.MANAGER_CONFIRM_OUT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							LogUtil.e("确定出场：" + Urls.MANAGER_CONFIRM_OUT,
									response);
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								Intent i= new Intent(CarOutConfirmActivity.this,ManagerCoverLayer.class);
								i.putExtra("order", bundle.getString("orderNum"));
								startActivity(i);
							}
							if ("010".equals(code)) {
								DialogUtil
										.loginAgain(CarOutConfirmActivity.this);
							}
						} catch (JSONException e) {
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
					}
				}) {
			@SuppressLint("SimpleDateFormat")
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("orderId", bundle.getString("orderNum"));
				map.put("startTime", bundle.getString("orderStartDate"));
				map.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.format(new Date()));
				map.put("freeHour", freehour);
				if (img != null && !"".equals(img)) {
					map.put("coupon", img);
				}
				LogUtil.e("确定出场：" + Urls.MANAGER_CONFIRM_OUT,
						StringUtils.getMap(map));
				return map;
			}
		};
		confirmOut.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(confirmOut);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void addImg() {
		if (popWindow == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.pop_select_photo, null);// 显示下面的提示框（拍照，相册，取消）
			popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			photograph = (TextView) view.findViewById(R.id.photograph);// 拍照
			albums = (TextView) view.findViewById(R.id.albums);// 本地文件夹
			cancel = (LinearLayout) view.findViewById(R.id.cancel);// 取消
			photograph.setOnClickListener(this);
			albums.setOnClickListener(this);
			cancel.setOnClickListener(this);
		}
		popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);// 弹出
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(addImg, Gravity.CENTER, 0, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Uri uri = null;
		String path;
		switch (requestCode) {
		case PHOTOZOOM:// 文件夹
			if (data == null) {
				return;
			}
			uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
			setImg(path);
			break;
		case PHOTOTAKE:// 相机拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			setImg(path);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setImg(String path) {
		Bitmap bitmap = null;
		bitmap = ImageUtil.convertToBitmap(path, 500, 500);
		bitmap = ImageUtil.decodeBitmap(bitmap, 100);
		addImg.setImageBitmap(bitmap);
		img = ImageUtil.bitmapToBase64(bitmap);
		addImg.setScaleType(ScaleType.FIT_XY);
		preferentialArea.setVisibility(View.VISIBLE);
		imgStatus = true;
	}

}
