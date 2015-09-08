package com.sunrise.epark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sunrise.epark.R;
/**
 * 离开停车场
 * @author 毛卫军
 *
 */
public class LeavePark extends BaseActivity implements OnClickListener{

	private Button leave;
	private RelativeLayout back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_park);
		
		leave = (Button)findViewById(R.id.leave_ok);
		leave.setOnClickListener(this);
		
		back = (RelativeLayout)findViewById(R.id.back);
		back.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
//		Intent intent = new Intent(this,CoverLayer.class);
//		intent.putExtra("title", getString(R.string.cover_order));
//		intent.putExtra("order", "A-000112");
//		intent.putExtra("flag", "leave");
//		startActivity(intent);
		

//			showPayingDialog("付款中 请稍候");
//			hidePayDialog();
//			showPaySuccessDialog("付款成功");
		int id = arg0.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;
		case R.id.leave_ok:
			startActivity(new Intent(this, PayMainActivity.class));
			break;
		default:
			break;
		}
		
	}
}
