package com.sunrise.epark.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.sunrise.epark.R;

/**
* 描述: 关于我们
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class AboutUsActivity extends BaseActivity {
	private RelativeLayout back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		GifView myGifView = (GifView) findViewById(R.id.imageView1);  
		myGifView.setGifImage(R.drawable.logo_car);  
		myGifView.setGifImageType(GifImageType.COVER);
		back = (RelativeLayout) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
