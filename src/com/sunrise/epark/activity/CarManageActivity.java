package com.sunrise.epark.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.fragment.CarListFragment;
import com.sunrise.epark.fragment.OrderSearchFragment;
import com.sunrise.epark.fragment.ParkingLotsFragment;

/**
 * 
* 描述:停车场管理员主页 
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class CarManageActivity extends BaseFragmentActivity implements
		OnClickListener {

	/**标题文字*/
	private TextView title;
	/** 进出车辆 */
	private LinearLayout carManageLinearLayout;
	/** 订单搜索 */
	private ImageButton orderSearch;
	/** 停车场 */
	private LinearLayout parkinglotsLinearLayout;
	/**停车场图标*/
	private ImageView parkinglotsImg;
	/**车辆管理图标*/
	private ImageView carManageImageView;

	private CarListFragment carListFragment;
	private ParkingLotsFragment parkingLotsFragment;
	private OrderSearchFragment orderSearchFragment;
	private FragmentManager fragmentManager;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_main);
		initView();
		bindEvent();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		fragmentManager = getSupportFragmentManager();
		title=(TextView)this.findViewById(R.id.car_manage_title);
		carManageLinearLayout = (LinearLayout) this
				.findViewById(R.id.linear_car_manage);
		parkinglotsLinearLayout = (LinearLayout) this
				.findViewById(R.id.linear_parkinglots);
		orderSearch = (ImageButton) this.findViewById(R.id.btn_search);
		parkinglotsImg=(ImageView)this.findViewById(R.id.parkinglots_img);
		carManageImageView=(ImageView)this.findViewById(R.id.car_manage_img);
	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		carManageLinearLayout.setOnClickListener(this);
		parkinglotsLinearLayout.setOnClickListener(this);
		orderSearch.setOnClickListener(this);
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (carListFragment != null) {
			transaction.hide(carListFragment);
		}
		if (parkingLotsFragment != null) {
			transaction.hide(parkingLotsFragment);
		}
		if(orderSearchFragment!=null){
			transaction.hide(orderSearchFragment);
		}
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (v.getId()) {
		case R.id.linear_car_manage://车辆管理
			title.setText(getResources().getString(R.string.car_manager));
			if(carListFragment==null){
				carListFragment = new CarListFragment();
				transaction.add(R.id.car_manage_main, carListFragment);
			}else{
				transaction.show(carListFragment);
			}
			parkinglotsImg.setImageResource(R.drawable.parkinglots); 
			carManageImageView.setImageResource(R.drawable.car_in_out_press);
			break;
		case R.id.btn_search://订单搜索
			title.setText(getResources().getString(R.string.order_search));
			if(orderSearchFragment==null){
				orderSearchFragment=new OrderSearchFragment();
				transaction.add(R.id.car_manage_main,orderSearchFragment);
			}else{
				transaction.show(orderSearchFragment);
			}
			break;
		case R.id.linear_parkinglots://停车场详情
			title.setText(getResources().getString(R.string.parkinglots));
			if(parkingLotsFragment==null){
				parkingLotsFragment = new ParkingLotsFragment();
				transaction.add(R.id.car_manage_main, parkingLotsFragment);
			}else{
				transaction.show(parkingLotsFragment);
				parkingLotsFragment.getParkingDetail();
			}
			 parkinglotsImg.setImageResource(R.drawable.parkinglots_press); 
			 carManageImageView.setImageResource(R.drawable.car_in_out);
			break;
		default:
			break;
		}
		transaction.commit();  
	}

	/**
	 * 重写退出事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Builder builder = new Builder(this);
			builder.setTitle(getString(R.string.tip));
			builder.setMessage(getString(R.string.exit_app_sure));
			builder.setPositiveButton(getString(R.string.yes),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							BaseApplication.getInstance().finishAllActivity();
						}
					});
			builder.setNegativeButton(getString(R.string.cancle), null);
			builder.show();
		}
		return true;
	}

}
