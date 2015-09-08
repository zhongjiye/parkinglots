package com.sunrise.epark.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.configure.Urls;

public class UpdateUtil {
	
	@SuppressLint("SdCardPath")
	private  String savePath = "/sdcard/ParkingLots/apk/";
	private  String saveFileName = savePath+"parking.apk";
	
	private  final static int DOWN_UPDATE = 1;
	private  final static int DOWN_OVER = 2;


	private  boolean cancle = false;
	private  boolean isForceUpdate = false;
	
	private  Context context;
	private  ProgressBar progressBar;
	private  int process;
	
	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case DOWN_OVER:
				installApk();
				break;
			case DOWN_UPDATE:
				progressBar.setProgress(process);
				break;
			}
		}
	};

	public UpdateUtil(Context con){
		this.context=con;
	}
	public  void checkVersion() {
		StringRequest confirmIn = new StringRequest(Method.POST,
				Urls.CHECK_VERSION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("检查更新：" + Urls.CHECK_VERSION, response);
						JSONObject object;
						try {
							object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								JSONObject object2 = object
										.getJSONObject("result");
								int lastVersion = Integer.parseInt(object2
										.getString("verIndex"));
								int nowVersion = AppUtil
										.getVersionCode(context);
								if (lastVersion <= nowVersion) {
									ToastUtil.showLong(context, "当前已是最新版本");
								} else {
									Urls.APK_URL = object2.getString("verUrl");
									forcedUpdate();
								}
							}
							if("009".equals(code)){
								ToastUtil.showLong(context, object.getString("result"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
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
				return map;
			}
		};
		confirmIn.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		BaseApplication.mQueue.add(confirmIn);
	}

	/**
	 * 强制更新提示
	 */
	private  void forcedUpdate() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(context.getString(R.string.update_title));
		String msg = isForceUpdate ? context.getString(R.string.update_imp_msg)
				: context.getString(R.string.update_msg);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
	
		dialog.setPositiveButton(context.getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						excUpdate();
					}
				});
		dialog.setNegativeButton(context.getString(R.string.update_later),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (isForceUpdate) {
						}
					}
				});
		dialog.create().show();
	}
	
	/**
	 * 执行更新操作
	 */
	private  void excUpdate() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(context.getString(R.string.updating));
		dialog.setCancelable(false);
		View v = View.inflate(context, R.layout.download_progress, null);
		progressBar = (ProgressBar) v.findViewById(R.id.download_bar);
		progressBar.setHorizontalScrollBarEnabled(true);
		dialog.setView(v);
		dialog.setNegativeButton(context.getString(R.string.cancle),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						cancle = true;
						if (isForceUpdate) {
							((Activity) context).finish();
						}
					}
				});
		dialog.create().show();
		new Thread(mdownApkRunnable).start();
	}

	
	/**
	 * 下载线程
	 */
	private  Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(Urls.APK_URL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				if (!ApkFile.exists()) {
					ApkFile.createNewFile();
				}else{
					ApkFile.delete();
					ApkFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					process = (int) (((float) count / length) * 100);
					// 更新进度
					handler.sendEmptyMessage(DOWN_UPDATE);
					if (numread < 0) {
						// 下载完成通知安装
						handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!cancle);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private  void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		SharedPreferences share = context.getApplicationContext()
				.getSharedPreferences("yunda", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("first", true);
		editor.commit();
		Log.e("hehs", share.getBoolean("first", true) + "");
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}

}
