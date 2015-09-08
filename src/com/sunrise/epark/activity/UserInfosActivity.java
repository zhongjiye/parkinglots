package com.sunrise.epark.activity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.ImageUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.PreferenceUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
/***
 * 
 *this class is used for...个人中心-我的信息
 * @wangyingjie  create
 *@time 2015年7月27日上午9:41:46
 */
public class UserInfosActivity extends BaseActivity implements OnClickListener{
	   /**loading框*/
	   private Dialog loadingDialog;
	   /**显示帐号*/
	   private TextView acountText;
	   /**显示Email*/
	   private TextView emailText;
	   /**显示手机*/
	   private TextView phoneText;
	   /**显示车牌*/
	   private TextView cardText;
	   /**显示姓名*/
	   private TextView nameText;
	   /**返回*/
	   private RelativeLayout back;
	   /**手机号后的图片*/
	   private ImageView phoneid;
	   /**邮箱号后的相片*/
	   private ImageView emailid;
	   /**改变头像*/
	   private LinearLayout changeimg;
	   /**改变名字*/
	   private LinearLayout changename;
	   /**改变电话*/
	   private LinearLayout changephone;
	   /**改变email*/
	   private LinearLayout changeemail;
	   /**改变车牌*/
	   private LinearLayout changecarid;
	   /**拍照相关事件*/
		private ImageView head;
		private PopupWindow popWindow;
		private LayoutInflater layoutInflater;
		private TextView photograph,albums;
		private LinearLayout cancel;	
		public static final int PHOTOZOOM = 0; //requestcode 打开本地文件
		public static final int PHOTOTAKE = 1; // requestcode 拍照
		public static final int IMAGE_COMPLETE = 2; // 照片裁剪
		public static final int CROPREQCODE = 3; // ��ȡ
		private String photoSavePath;//路径
		private String photoSaveName;//照片名字
		private String path;//路径
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);/**设置页面没主题*/
		setContentView(R.layout.activity_user_infos);
		initview();
		bindEvent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.err.println("onresume");
		initphtoto();
		 bindEvent();
		initData();
	}
	
	/**点击头像弹出底部框*/
private void initphtoto() {
	layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	File file = new File(Environment.getExternalStorageDirectory(), "ParkingLots/image");
	if (!file.exists())
		file.mkdirs();
	photoSavePath=Environment.getExternalStorageDirectory()+"/ParkingLots/image/";
	photoSaveName =System.currentTimeMillis()+ ".jpg";
			
	head=(ImageView) findViewById(R.id.head);//点击出发拍照
	head.setOnClickListener(this);
	
}	
	/**初始化数据*/
	private void initview() {
		loadingDialog=DialogUtil.createLoadingDialog(UserInfosActivity.this,"正在保存请稍后...");
		back=(RelativeLayout)findViewById(R.id.back);
		back.setOnClickListener(this);
		changeimg=(LinearLayout)findViewById(R.id.changeimg);
		changecarid=(LinearLayout)findViewById(R.id.carid);
		changeemail=(LinearLayout)findViewById(R.id.email);
		changename=(LinearLayout)findViewById(R.id.name);
		changephone=(LinearLayout)findViewById(R.id.phone);
		
		acountText=(TextView)findViewById(R.id.userName);
		emailText=(TextView)findViewById(R.id.showemail);
		nameText=(TextView)findViewById(R.id.showname);
		phoneText=(TextView)findViewById(R.id.showphone);
		cardText=(TextView)findViewById(R.id.showcarid);
		
		phoneid=(ImageView)findViewById(R.id.phoneid);
		emailid=(ImageView)findViewById(R.id.emailid);
	}
	
	private void bindEvent(){
		if(StringUtils.isNotNull(UserInfo.userTel)){
			phoneid.setVisibility(View.GONE);
		}else{
			changephone.setOnClickListener(this);
		}
		if(StringUtils.isNotNull(UserInfo.userMail)){
			emailid.setVisibility(View.GONE);
		}else{
			changeemail.setOnClickListener(this);
		}
		changename.setOnClickListener(this);
		changecarid.setOnClickListener(this);
		changeimg.setOnClickListener(this);
	}
	/**从缓存中设置数据*/
	private void initData(){
		initUrlImg();
		if(!"".equals(UserInfo.userLogin)){
			acountText.setText(UserInfo.userLogin);
		}
		if(!"".equals(UserInfo.userMail)){
			emailText.setText("****"+UserInfo.userMail.substring(UserInfo.userMail.lastIndexOf("@"), UserInfo.userMail.length()));
		}
		if(!"".equals(UserInfo.userName)){
			String name = null;
			if(UserInfo.userName.length()>=3) {
			 	name=UserInfo.userName.substring(0,1)+"*"+UserInfo.userName.substring(UserInfo.userName.length()-1, UserInfo.userName.length());
			}else {
				name=UserInfo.userName.substring(0,1)+"*";
			}
			nameText.setText(name);
		}
		if(!"".equals(UserInfo.userTel)){
			phoneText.setText(UserInfo.userTel.substring(0, 3)+"****"+UserInfo.userTel.substring(7,UserInfo.userTel.length()));
		}
		if(!"".equals(UserInfo.carName)){
			cardText.setText(UserInfo.carName.substring(0,3)+"**"+UserInfo.carName.substring(5,7));
		}else {
			cardText.setText("");
		}
	}

	/**设置头像，如果缓存中有*/
	@SuppressWarnings("deprecation")
	private void initUrlImg(){
		if (!"".equals(UserInfo.imgUrl)) {
			ImageRequest imageRequest = new ImageRequest(UserInfo.imgUrl,
					new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap response) {
							head.setBackground(new BitmapDrawable(
									ImageUtil.getCircuarImg(UserInfosActivity.this,response)));
							head.getBackground().setAlpha(0);
							head.setImageBitmap(ImageUtil
									.getCircuarImg(UserInfosActivity.this,
											response));
						}
					}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							initDefaultImg();
						}
					});
			BaseApplication.mQueue.add(imageRequest);
		}else{
			initDefaultImg();
		}
	}
		/**如果缓存中没有图片，设置为默认图片*/
		@SuppressWarnings("deprecation")
		private void initDefaultImg() {
			head.setScaleType(ScaleType.FIT_XY);
			head.setBackground(new BitmapDrawable(ImageUtil.getCircuarImg(
					UserInfosActivity.this, R.drawable.icon_head)));
			head.getBackground().setAlpha(0);
			head.setImageBitmap(ImageUtil.getCircuarImg(
					UserInfosActivity.this, R.drawable.icon_head));
		}
		
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.carid:
				findCarId();
			break;
		case R.id.changeimg:
			changeImage(head);
			break;
		case R.id.email:
			Intent intent4 = new Intent(this, MyEmailActivity.class);
			startActivity(intent4);
			break;
		case R.id.phone:
			Intent intent5 = new Intent(this, MyPhoneActivity.class);
			startActivity(intent5);
			break;
		case R.id.name:
			Intent intent6 = new Intent(this, MyNameActivity.class);
			startActivity(intent6);
			break;
		default:
			break;
		}
	}
		/**查找车辆*/
	private void findCarId() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.FIND_CARID, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("查找车牌", response);
						try {
							JSONObject jObject = new JSONObject(response);
					if ("000".equals(jObject.getString("code"))){
						if (!"".equals(jObject.getJSONArray("result"))) {
							JSONArray	jObject1 = jObject.getJSONArray("result");
							for(int i=0;i<jObject1.length();i++){
								JSONObject jsonObject=jObject1.getJSONObject(i);
								PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(getApplicationContext());
								preferenceUtil.put("carId", jsonObject.getString("carId"));
								preferenceUtil.put("carName", jsonObject.getString("carName"));	
							}
						}
						Intent intent = new Intent(UserInfosActivity.this,
								MyCarActivity.class);
						startActivity(intent);
					}else if ("001".equals(jObject.getString("code"))) {
    						DialogUtil.showToast(getApplicationContext(),"查找不到数据，请重新输入");
    				} else if ("002".equals(jObject.getString("code"))) {
							DialogUtil.showToast(getApplicationContext(),"超时");
    				} else if ("003".equals(jObject.getString("code"))) {
							DialogUtil.showToast(getApplicationContext(),	"请求数据不正确");
    				} else if ("004".equals(jObject.getString("code"))) {
							DialogUtil.showToast(getApplicationContext(),"程序执行异常");
    				} else if ("005".equals(jObject.getString("code"))) {
							DialogUtil.showToast(getApplicationContext(),"其他");
    				} else if ("006".equals(jObject.getString("code"))) {
						DialogUtil.showToast(getApplicationContext(),"操作失败");
    				}else if("010".equals(jObject.getString("code"))){
    					DialogUtil.loginAgain(getApplicationContext());
    				}			
						} catch (JSONException e) {
							e.printStackTrace();
						}finally{
							if (loadingDialog != null) {
								loadingDialog.dismiss();}
						}
//						ToastUtil.showLong(UserInfosActivity.this, response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(UserInfosActivity.this,
								VolleyErrorUtil.getMessage(error,
										UserInfosActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				LogUtil.i("查找车辆",StringUtils.getMap(map));
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
		
	}
	
	/**更换头像*/
	private void changeImage(View parent) {
			if (popWindow == null) {		
				View view = layoutInflater.inflate(R.layout.pop_select_photo,null);//显示下面的提示框（拍照，相册，取消）
				popWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
				initPop(view);
			}	
			popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);//弹出
			popWindow.setFocusable(true);
			popWindow.setOutsideTouchable(true);
			popWindow.setBackgroundDrawable(new BitmapDrawable());	
			popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		
	}
	/**点击头像后显示对话框*/
	public void initPop(View view){
		photograph = (TextView) view.findViewById(R.id.photograph);//拍照
		albums = (TextView) view.findViewById(R.id.albums);//本地文件夹
		cancel= (LinearLayout) view.findViewById(R.id.cancel);//取消
		photograph.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popWindow.dismiss();
				photoSaveName =String.valueOf(System.currentTimeMillis()) + ".jpg";
				Uri imageUri = null;
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageUri = Uri.fromFile(new File(photoSavePath,photoSaveName));
				openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, PHOTOTAKE);
			}
		});
		albums.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popWindow.dismiss();
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(openAlbumIntent, PHOTOZOOM);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popWindow.dismiss();
				
			}
		});
	}
	
	/**
	 * ͼƬѡ�����ս��
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode != RESULT_OK) {
				return;
			}
			Uri uri = null;
			switch (requestCode) {
				case PHOTOZOOM://文件夹
					if (data==null) {
						return;
					} 
					uri = data.getData();
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, proj, null, null,null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					path = cursor.getString(column_index);// ͼƬ�ڵ�·��
					Intent intent3=new Intent(UserInfosActivity.this, ClipActivity.class);
					intent3.putExtra("path", path);
					startActivityForResult(intent3, IMAGE_COMPLETE);//
					break;
				case PHOTOTAKE://相机拍照
					path=photoSavePath+photoSaveName;
					uri = Uri.fromFile(new File(path));
					Intent intent2=new Intent(UserInfosActivity.this, ClipActivity.class);
					intent2.putExtra("path", path);
					startActivityForResult(intent2, IMAGE_COMPLETE);
					break;
				case IMAGE_COMPLETE:
					final String temppath = data.getStringExtra("path");
					Bitmap bitmap=getLoacalBitmap(path);
					head.setScaleType(ScaleType.FIT_XY);
					head.setBackground(new BitmapDrawable(ImageUtil.getCircuarImg(
							UserInfosActivity.this, bitmap)));
					head.getBackground().setAlpha(0);
					head.setImageBitmap(ImageUtil.getCircuarImg(
							UserInfosActivity.this, bitmap));
					break;
				default:
					break;
			}
		} catch (Exception e) {
			ToastUtil.show(UserInfosActivity.this, "未能获取到图片,请检查图片路径");
		}	
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		}

}
