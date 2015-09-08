package com.sunrise.epark.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunrise.epark.R;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.util.AppUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.UpdateUtil;

/***
 * 
 * this class is used for...个人中心设置
 * 
 * @wangyingjie create
 * @time 2015年7月27日上午9:38:11
 */
public class PersonalSettingActivity extends BaseActivity implements
		OnClickListener {
	private RelativeLayout back;
	private LinearLayout about;
	private LinearLayout checking;
	private LinearLayout fankui;
	private LinearLayout changepwd;
	private TextView version;
	private Button quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_setting);
		initview();
		bindEvent();
		initData();
	}

	private void initview() {
		back = (RelativeLayout) findViewById(R.id.back);
		about = (LinearLayout) findViewById(R.id.about);
		version = (TextView) findViewById(R.id.banben);
		changepwd = (LinearLayout) findViewById(R.id.changepwd);
		checking = (LinearLayout) findViewById(R.id.checking);
		fankui = (LinearLayout) findViewById(R.id.fankui);
		quit = (Button) findViewById(R.id.quit);
	}
	
	private void bindEvent(){
		about.setOnClickListener(this);
		back.setOnClickListener(this);
		changepwd.setOnClickListener(this);
		checking.setOnClickListener(this);
		fankui.setOnClickListener(this);
		quit.setOnClickListener(this);
		
	}
	
	private void initData(){
		version.setText("V"+AppUtil.getVersionName(this));
		if ("login".equals(UserInfo.loginStatus)
				|| "register".equals(UserInfo.loginStatus)) {
			fankui.setVisibility(View.VISIBLE);
			changepwd.setVisibility(View.VISIBLE);
		} else {
			fankui.setVisibility(View.VISIBLE);
			changepwd.setVisibility(View.GONE);
			quit.setText(getString(R.string.login));
			quit.setBackgroundResource(R.drawable.btn_selector);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.about:
			startActivity(new Intent(PersonalSettingActivity.this,
					AboutUsActivity.class));
			break;
		case R.id.checking:
			UpdateUtil updateUtil = new UpdateUtil(this);
			updateUtil.checkVersion();
			break;
		case R.id.fankui:
//			if(StatusUtil.StatusCheck(PersonalSettingActivity.this)){
			startActivity(new Intent(PersonalSettingActivity.this,
					ComplainAdviseActivity.class));
//			}
			break;
		case R.id.changepwd:
			startActivity(new Intent(PersonalSettingActivity.this,
					ChangePwdActivity.class));
			break;
		case R.id.quit:
			switch (UserInfo.loginStatus) {
			case "login":
				quit();
				break;
			case "register":
				quit();
				break;
			case "exit":
				startActivity(new Intent(this, LoginActivity.class));
				finish();
				break;
			}
			break;
		default:
			break;
		}
	}

	private void quit() {
		Builder builder = new Builder(this);
		builder.setTitle("确认");
		builder.setMessage("确认退出登录吗？");
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StatusUtil.exit(getApplicationContext());
				startActivity(new Intent(PersonalSettingActivity.this,
						LoginActivity.class));
				finish();
			}

		});
		builder.setNegativeButton("否", null);
		builder.show();
	}
	
	
	

}
