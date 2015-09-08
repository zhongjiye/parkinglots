package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
* 描述:意见反馈 
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class ComplainAdviseActivity extends BaseActivity implements
		OnClickListener, TextWatcher {
	/** loading */
	private Dialog loading;
	/** 返回 */
	private RelativeLayout back;
	/** 提交 */
	private Button confrim;
	/** 投诉建议编辑框 */
	private EditText et1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complain_advise);
		inidView();
		bindEvent();
	}

	/**
	 * 初始化组件
	 */
	private void inidView() {
		loading = DialogUtil.createLoadingDialog(ComplainAdviseActivity.this,
				"正在递交请稍后...");
		et1 = (EditText) findViewById(R.id.advise);
		back = (RelativeLayout) findViewById(R.id.back);
		confrim = (Button) findViewById(R.id.confirm);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		back.setOnClickListener(this);
		confrim.setOnClickListener(this);
		et1.addTextChangedListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (!"".equals(et1.getText().toString().trim())) {
			confrim.setBackgroundResource(R.drawable.button_round_sure_green);
		} else {
			confrim.setBackgroundResource(R.drawable.button_round_sure_gray);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:// 返回
			finish();
			break;
		case R.id.confirm:// 意见反馈
			String content = et1.getText().toString().trim();
			if (!"".equals(content)) {
				advise(content);
			} else {
				//ToastUtil.showShort(this, "意见反馈内容不能为空");
//				DialogUtil.showToast(getApplicationContext(),"意见反馈内容不能为空");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 意见反馈
	 * 
	 * @param content
	 *            反馈内容
	 */
	private void advise(final String content) {
		loading.show();
		StringRequest complain = new StringRequest(Method.POST,
				Urls.INSERT_FEED_BACK, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("意见反馈:" + Urls.INSERT_FEED_BACK, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								DialogUtil.showToast(getApplicationContext(),
										getString(R.string.advise_success));
								et1.setText("");
								finish();
								break;
							case "010":
								DialogUtil
										.loginAgain(ComplainAdviseActivity.this);
								break;
							default:
								DialogUtil.showToast(getApplicationContext(),
										getString(R.string.advise_fail));
								break;
							}
						} catch (JSONException e) {
							DialogUtil.showToast(getApplicationContext(),
									getString(R.string.advise_fail));
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
						ToastUtil.showLong(ComplainAdviseActivity.this,
								VolleyErrorUtil.getMessage(error,
										ComplainAdviseActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				if (UserInfo.userId==null) {
					map.put("userId", "");
				}else {
					map.put("userId", UserInfo.userId);
				}
				if (UserInfo.uuid==null) {
					map.put("uuid", "");
				}else {
					map.put("uuid", UserInfo.uuid);
				}
				map.put("fbContent", content);
				LogUtil.i("意见反馈:" + Urls.INSERT_FEED_BACK,
						StringUtils.getMap(map));
				return map;
			}
		};
		complain.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(complain);
	}

}
