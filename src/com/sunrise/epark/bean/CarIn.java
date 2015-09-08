package com.sunrise.epark.bean;

public class CarIn {

	private String orderId;
	private String carName;
	private String startTime;

	private String creatTime;
	private String endTime;
	private String imgUrl;
	private String orderAmount;
	private String parkAddr;
	private String parkId;
	private String parkName;
	private String parkPer;
	private String parkType;
	private String subList;
	private String updateTime;
	private String orderStatu;

	public String getOrderId() {
		return orderId;
	}

	public String getOrderStatu() {
		return orderStatu;
	}

	public void setOrderStatu(String orderStatu) {
		this.orderStatu = orderStatu;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getParkAddr() {
		return parkAddr;
	}

	public void setParkAddr(String parkAddr) {
		this.parkAddr = parkAddr;
	}

	public String getParkId() {
		return parkId;
	}

	public void setParkId(String parkId) {
		this.parkId = parkId;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getParkPer() {
		return parkPer;
	}

	public void setParkPer(String parkPer) {
		this.parkPer = parkPer;
	}

	public String getParkType() {
		return parkType;
	}

	public void setParkType(String parkType) {
		this.parkType = parkType;
	}

	public String getSubList() {
		return subList;
	}

	public void setSubList(String subList) {
		this.subList = subList;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public CarIn(String orderId, String carName, String startTime,String orderStatu) {
		super();
		this.orderId = orderId;
		this.carName = carName;
		this.startTime = startTime;
		this.orderStatu=orderStatu;
	}
	
	public CarIn(String orderId, String carName, String startTime) {
		super();
		this.orderId = orderId;
		this.carName = carName;
		this.startTime = startTime;
		
	}

}
