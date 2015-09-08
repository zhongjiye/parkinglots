package com.sunrise.epark.activity;

import java.io.File;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DateTool;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.ImageUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;

/**
* 描述:补单出场
* @author zhongjy
* @date 2015年7月30日
* @version 1.0
 */
public class CarOutAddConfirmActivity extends BaseActivity implements OnClickListener,OnLongClickListener {
    private Bundle bundle ;
	private Dialog loading;
	
	/**相册选取*/
	public static final int PHOTOZOOM = 0; 
	/** 拍照*/
	public static final int PHOTOTAKE = 1; 
	/**照片裁剪*/
	public static final int IMAGE_COMPLETE = 2; 
	
	/** 返回 */
	private LinearLayout back;
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
	/**停车时长-小时*/
	private EditText parkHour;
	/**停车时长-分钟*/
	private EditText parkMinute;
	/** 确定进场 */
	private Button confirm;
	/**维护优惠券信息区域*/
	private RelativeLayout preferentialArea;
	/**优惠时长*/
	private EditText preferential;
	/**添加优惠券*/
	private ImageView addImg;
	/** 拍照相关事件 */
	private PopupWindow popWindow;
	/**拍照*/
	private TextView photograph;
	/**从相册选取*/
	private TextView albums;
	/**取消*/
	private LinearLayout cancel;
	/**照片存取路径*/
	private String photoSavePath;
	/** 照片名字*/
	private String photoSaveName;
	/**是否有优惠券*/
	private boolean imgStatus;
	/**图片的base64字符串*/
	private String img;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caraddout_confirm);
		initView();
		bindEvent();
		initData();
	}

	/***
	 * 初始化组件
	 */
	private void initView() {
		bundle = getIntent().getExtras();
		loading=DialogUtil.createLoadingDialog(this, getString(R.string.loading));
		back = (LinearLayout) this.findViewById(R.id.car_add_out_back);
		orderNum = (TextView) this.findViewById(R.id.car_add_out_num);
		userName = (TextView) this.findViewById(R.id.car_add_out_username);
		userCarNum = (TextView) this.findViewById(R.id.car_add_out_usercarnum);
		userMobile = (TextView) this.findViewById(R.id.car_add_out_usermobile);
		orderStartDate = (TextView) this
				.findViewById(R.id.car_add_out_starttime);
		confirm = (Button) this.findViewById(R.id.confirm_add_out);
		
		preferentialArea = (RelativeLayout) this
				.findViewById(R.id.car_add_out_preferential_area);
		preferential = (EditText) this.findViewById(R.id.car_add_out_preferential);
		addImg = (ImageView) this.findViewById(R.id.car_add_out_add_img);
		
		parkHour=(EditText)findViewById(R.id.car_add_out_parkhour);
		parkMinute=(EditText)findViewById(R.id.car_add_out_parkminute);
		File file = new File(Environment.getExternalStorageDirectory(),
				"ParkingLots/image");
		if (!file.exists()) {
			file.mkdirs();
		}
		photoSavePath = Environment.getExternalStorageDirectory()
				+ "/ParkingLots/image/";
		photoSaveName = System.currentTimeMillis() + ".png";
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		back.setOnClickListener(this);
		confirm.setOnClickListener(this);
		addImg.setOnClickListener(this);
		addImg.setOnLongClickListener(this);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		Bundle bundle = getIntent().getExtras();  
		orderNum.setText(bundle.getString("orderNum"));
		userName.setText(bundle.getString("userName"));
		userCarNum.setText(bundle.getString("userCarNum"));
		userMobile.setText(bundle.getString("userMobile"));
		//orderStartDate.setText(DateTool.getToday());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_add_out_add_img://添加优惠券
			addImg();
			break;
		case R.id.confirm_add_out://确定出场
			String freeHour = null;
			if (preferentialArea.getVisibility() == View.VISIBLE) {
				freeHour = preferential.getText().toString().trim();
			}
			if(freeHour==null||"".equals(freeHour)){
				freeHour="0";
			}
			String hour=parkHour.getText().toString().trim();
			String min=parkMinute.getText().toString().trim();
			hour=(hour==null||"".equals(hour))?"0":hour;
			min=(min==null||"".equals(min))?"0":min;
			if("0".equals(hour)&&"0".equals(min)){
				ToastUtil.showShort(this, "请维护停车时长");
			}else{
				confirmAddOut(freeHour,hour,min);
			}
			break;
		case R.id.order_detail_back://返回
			this.finish();
			break;
		case R.id.photograph://拍照
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
		case R.id.albums://相册选择
			popWindow.dismiss();
			Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			openAlbumIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(openAlbumIntent, PHOTOZOOM);
			break;
		case R.id.cancel://取消
			popWindow.dismiss();
			break;
		default:
			break;
		}

	}
	/**
	 * 补单确定离场
	 * @param freehour 优惠时长
	 * @param hour 停车时长-小时
	 * @param min  停车时长-分钟
	 */
	private void confirmAddOut(final String freehour,final String hour,final String min) {
		loading.show();
		StringRequest confirmOut = new StringRequest(Method.POST,
				Urls.MANAGER_CONFIRM_ADD_OUT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("补单确定出场："+Urls.MANAGER_CONFIRM_ADD_OUT, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							//LogUtil.d("", msg);
							switch (code) {
							case "000":
								finish();
								break;
							case "010":
								DialogUtil.loginAgain(CarOutAddConfirmActivity.this);
								break;
							default:
								ToastUtil.showShort(CarOutAddConfirmActivity.this, "补单出场失败");
								break;
							}
						} catch (JSONException e) {
							ToastUtil.showShort(CarOutAddConfirmActivity.this, "补单出场失败");
						}finally{
							if(loading!=null&&loading.isShowing()){
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(loading!=null&&loading.isShowing()){
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
				map.put("freeHour", freehour);
				map.put("hour", hour);
				map.put("endTime",DateTool.getNextDay4(DateTool.getToday(), hour, min));
				map.put("min", min);
				if(img!=null&&!"".equals(img)){
					map.put("coupon", img);
				}
				LogUtil.i("补单确定出场："+Urls.MANAGER_CONFIRM_ADD_OUT, StringUtils.getMap(map));
				return map;
			}
		};
		confirmOut.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(confirmOut);
	}

	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.car_add_out_add_img:
			if(imgStatus){
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.tip))
				.setMessage(getResources().getString(R.string.del_img_sure))
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								addImg.setImageBitmap(ImageUtil.getBitmap(
										CarOutAddConfirmActivity.this,
										R.drawable.icon_add));
								addImg.setScaleType(ScaleType.CENTER);
								preferentialArea.setVisibility(View.INVISIBLE);
								imgStatus=false;
								img=null;
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancle),
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
		bitmap = ImageUtil.convertToBitmap(path,500,500);
		bitmap = ImageUtil.decodeBitmap(bitmap, 100);
		addImg.setImageBitmap(bitmap);
		img=ImageUtil.bitmapToBase64(bitmap);
		addImg.setScaleType(ScaleType.FIT_XY);
		preferentialArea.setVisibility(View.VISIBLE);
		imgStatus=true;
	}

}
