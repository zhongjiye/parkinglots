package com.sunrise.epark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunrise.epark.R;
import com.sunrise.epark.util.DateTool;
/***
 * 
 *this class is used for...个人中心-停车记录-详情-离开优惠后价格
 * @wangyingjie  create
 *@time 2015年7月27日上午9:40:36
 */
public class StopcarDetailAwayPriceActivity extends BaseActivity {
	private RelativeLayout back;
	private TextView stopcaradd;
	private TextView  stop_money;
	private TextView  stop_time;
	private TextView  stop_starttime;
	private TextView  stop_endtime;
	private TextView  money;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stopcar_detail_away_price);
		intent = getIntent();
		initview();
		initdata();
		initbind();
	}
	private void initview() {
		back=(RelativeLayout)findViewById(R.id.back);
		stopcaradd=(TextView)findViewById(R.id.stopcar);
		stop_money=(TextView)findViewById(R.id.stop_money);
		stop_time=(TextView)findViewById(R.id.stop_time);
		stop_starttime=(TextView)findViewById(R.id.stop_starttime);
		stop_endtime=(TextView)findViewById(R.id.stop_endtime);
		money=(TextView)findViewById(R.id.money);
	}

	private void initdata() {
		stopcaradd.setText(intent.getStringExtra("name"));
		stop_money.setText(getString(R.string.how_yuan,intent.getStringExtra("money")));
		stop_starttime.setText(intent.getStringExtra("startTime"));
		String end = intent.getStringExtra("endTime");
		
		stop_endtime.setText("".equals(end)?"待定":end);
		String time = DateTool.getTime(DateTool.string2date(intent.getStringExtra("startTime")), DateTool.string2date(intent.getStringExtra("endTime")));
		stop_time.setText(time);
		money.setText(getString(R.string.how_yuan,intent.getStringExtra("money")));
		
	}
	private void initbind() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
	}

}
