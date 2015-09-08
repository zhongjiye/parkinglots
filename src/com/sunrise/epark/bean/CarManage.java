package com.sunrise.epark.bean;

/**
 * 车辆管理信息类
 * Title: CarManage.java
 * Copyright: Copyright (c) 2015
 * Company: Camelot
 * @author zhongjy
 * @date 2015年7月28日
 * @version 1.0
 */
public class CarManage {
	/** 订单号 */
	private String orderNum;

	/** 车牌 */
	private String carNum;

	/** 时间 */
	private String date;

	/** 下单人 */
	private String userAcount;

	public CarManage(String orderNum, String carNum, String date,
			String userAcount) {
		super();
		this.orderNum = orderNum;
		this.carNum = carNum;
		this.date = date;
		this.userAcount = userAcount;
	}

	public CarManage() {
		super();
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserAcount() {
		return userAcount;
	}

	public void setUserAcount(String userAcount) {
		this.userAcount = userAcount;
	}
}
