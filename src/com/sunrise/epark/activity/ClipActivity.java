package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.ImageTools;
import com.sunrise.epark.util.ImageUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
import com.sunrise.epark.view.ClipImageLayout;

public class ClipActivity extends BaseActivity implements OnClickListener {
	private ClipImageLayout mClipImageLayout;
	private String path;
	private Dialog loading;
	private String base64;
	private Bitmap bitmap;
	private Button cancel;
	private Button clip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clipimage);
		initView();
		bindEvent();
		initData();
	}

	private void initView() {
		loading = DialogUtil.createLoadingDialog(ClipActivity.this,
				"正在上传照片请稍后...");
		path = getIntent().getStringExtra("path");
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		cancel = (Button) findViewById(R.id.id_action_cancel);
		clip = (Button) findViewById(R.id.id_action_clip);
	}

	private void initData() {
		bitmap = ImageTools.convertToBitmap(path, 600, 600);// 裁剪区域大小
		mClipImageLayout.setBitmap(bitmap);
	}

	private void bindEvent() {
		cancel.setOnClickListener(this);
		clip.setOnClickListener(this);
	}

	/**
	 * 照片递交接口,并在用户信息界面显示照片
	 */
	protected void changeImg() {
		StringRequest register = new StringRequest(Method.POST,
				Urls.CHANGE_IMG, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("递交照片:" + Urls.CHANGE_IMG, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String imgUrl = object.getString("result");
								StatusUtil.SaveImgUrl(getApplicationContext(),
										imgUrl);
								Intent intent = new Intent();
								intent.putExtra("path", path);
								setResult(RESULT_OK, intent);
								finish();
								break;
							case "010":
								DialogUtil.loginAgain(ClipActivity.this);
								break;
							default:
								DialogUtil.showToast(getApplicationContext(),
										"修改头像失败,请稍后重试");
								break;
							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							if (loading != null && loading.isShowing()) {
								loading.dismiss();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(ClipActivity.this, VolleyErrorUtil
								.getMessage(error, ClipActivity.this));
						if (loading != null && loading.isShowing()) {
							loading.dismiss();
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);// "402881054ed3f899014ed403eb110002");
				map.put("imgContent", base64);
				map.put("uuid", UserInfo.uuid);
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_action_cancel:
			finish();
			break;
		case R.id.id_action_clip:
			loading.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap newBitmap = ImageUtil.decodeBitmap(mClipImageLayout.clip(), 100);
					base64 = ImageTools.bitmapToBase64(newBitmap);
					changeImg();
				}
			}).start();
			break;

		default:
			break;
		}

	}

}
