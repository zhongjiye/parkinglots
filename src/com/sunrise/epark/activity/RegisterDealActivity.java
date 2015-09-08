package com.sunrise.epark.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.sunrise.epark.R;
/***
 * 
 *this class is used for... 登陆-注册-停车协议
 * @wangyingjie  create
 *@time 2015年7月27日上午9:38:51
 */
public class RegisterDealActivity extends BaseActivity {
	private RelativeLayout back;
	private WebView web;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_deal);
		back=(RelativeLayout)findViewById(R.id.back);
		web=(WebView)findViewById(R.id.deal);
		init();
	}


	private void init() {
		back.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View view) {
				startActivity(new Intent(RegisterDealActivity.this, RegisterActivity.class));
			}
		});	
		
		web.loadUrl("file:///android_asset/registerdealFile.html");
	}
		


}
