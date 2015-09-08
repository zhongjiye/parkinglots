package com.sunrise.epark.bean;

/**
 * 订单详情实体类 文件名: OrderDetail.java 描述: 公司: Camelot
 * 
 * @author zhongjy
 * @date 2015年7月30日
 * @version 1.0
 */
public class OrderDetail {
	/** 订单编号 */
	private String orderNum;
	/** 用户名 */
	private String userName;
	/** 用户车牌号 */
	private String userCarNum;
	/** 用户手机号 */
	private String userMobile;
	/** 订单开始时间 */
	private String orderStartDate;
	/**订单类型*/
	private String orderType;

	public String getOrderType() {
		return orderType;
	}

	public OrderDetail(String orderNum, String userName, String userCarNum,
			String userMobile, String orderStartDate, String orderType) {
		super();
		this.orderNum = orderNum;
		this.userName = userName;
		this.userCarNum = userCarNum;
		this.userMobile = userMobile;
		this.orderStartDate = orderStartDate;
		this.orderType = orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCarNum() {
		return userCarNum;
	}

	public void setUserCarNum(String userCarNum) {
		this.userCarNum = userCarNum;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getOrderStartDate() {
		return orderStartDate;
	}

	public void setOrderStartDate(String orderStartDate) {
		this.orderStartDate = orderStartDate;
	}

	public OrderDetail(String orderNum, String userName, String userCarNum,
			String userMobile, String orderStartDate) {
		super();
		this.orderNum = orderNum;
		this.userName = userName;
		this.userCarNum = userCarNum;
		this.userMobile = userMobile;
		this.orderStartDate = orderStartDate;
	}

	public OrderDetail() {
		super();
	}

}
