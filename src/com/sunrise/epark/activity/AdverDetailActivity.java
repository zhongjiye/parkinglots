package com.sunrise.epark.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.sunrise.epark.R;

public class AdverDetailActivity extends BaseActivity {
	private RelativeLayout back;
	private WebView web;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adver_detail);
		back = (RelativeLayout) findViewById(R.id.back);
		web = (WebView) findViewById(R.id.adver_detail);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		Bundle bundle = getIntent().getExtras();
		web.setWebViewClient(new webViewClient());
		web.loadUrl(bundle.getString("url"));
	}

	class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
