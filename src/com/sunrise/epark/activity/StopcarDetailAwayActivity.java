package com.sunrise.epark.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sunrise.epark.R;
/***
 * 
 *this class is used for... 个人中心-停车记录-详情离开
 * @wangyingjie  create
 *@time 2015年7月27日上午9:40:19
 */
public class StopcarDetailAwayActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stopcar_detail_away);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stopcar_detail_away, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
