/**
 *Park.java
 *classes:com.sunrise.epark.activity.Park
 *李敏 Create at 2015年7月30日 下午6:13:05
 */
package com.sunrise.epark.bean;

import java.io.Serializable;

/**
 * 停车场表 com.sunrise.epark.bean.Park
 * 
 * @author 李敏 create at 2015年7月30日 下午6:13:05
 */
public class Park implements Serializable {
	/**
	 * 创建编号
	 */
	private String creatId;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 白天结束时间
	 */
	private String dayEndTime;
	/**
	 * 一天的价格
	 */
	private String dayPrice;
	/**
	 * 白天的开始时间
	 */
	private String dayStartTime;

	private String deleteFlag;
	/**
	 * 责任人电话
	 */
	private String headMobile;
	/**
	 * 图片编号
	 */
	private String imgId;
	/**
	 * 纬度
	 */
	private String latitude;
	/**
	 * 经度
	 */
	private String longitude;

	private String multipartRequestHandler;
	/**
	 * 晚上结束时间
	 */
	private String nightEndTime;
	/**
	 * 晚上价格
	 */
	private String nightPrice;
	/**
	 * 晚上开始时间
	 */
	private String nightStartTime;
	/**
	 * 地址
	 */
	private String parkAddr;
	/**
	 * 超出计费时间长
	 */
	private String parkBeyondTime;
	/**
	 * 市
	 */
	private String parkCity;
	/**
	 * 停车场编号
	 */
	private String parkId;
	/**
	 * 最小计费时长
	 */
	private String parkMinTime;
	/**
	 * 停车场电话
	 */
	private String parkMobile;
	/**
	 * 停车场名字
	 */
	private String parkName;
	/**
	 * 取消续约金额比例
	 */
	private String parkPer;
	/**
	 * 省
	 */
	private String parkPro;
	/**
	 * 停车场状态
	 */
	private String parkStatus;
	/**
	 * 协议车位数
	 */
	private String parkSum;
	/**
	 * 总车位数
	 */
	private String parkTotal;
	/**
	 * 停车场类型
	 */
	private String parkType;

	private String remainPark;

	private String remark;

	private String servletWrapper;

	private String updateId;

	private String updateTime;

	private Double curr_geoLat;// 当前经度

	private Double curr_geoLng;// 当前纬度
	
	

	public Double getCurr_geoLat() {
		return curr_geoLat;
	}

	public void setCurr_geoLat(Double curr_geoLat) {
		this.curr_geoLat = curr_geoLat;
	}

	public Double getCurr_geoLng() {
		return curr_geoLng;
	}

	public void setCurr_geoLng(Double curr_geoLng) {
		this.curr_geoLng = curr_geoLng;
	}

	public String getCreatId() {
		return creatId;
	}

	public void setCreatId(String creatId) {
		this.creatId = creatId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDayEndTime() {
		return dayEndTime;
	}

	public void setDayEndTime(String dayEndTime) {
		this.dayEndTime = dayEndTime;
	}

	public String getDayPrice() {
		return dayPrice;
	}

	public void setDayPrice(String dayPrice) {
		this.dayPrice = dayPrice;
	}

	public String getDayStartTime() {
		return dayStartTime;
	}

	public void setDayStartTime(String dayStartTime) {
		this.dayStartTime = dayStartTime;
	}

	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getHeadMobile() {
		return headMobile;
	}

	public void setHeadMobile(String headMobile) {
		this.headMobile = headMobile;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMultipartRequestHandler() {
		return multipartRequestHandler;
	}

	public void setMultipartRequestHandler(String multipartRequestHandler) {
		this.multipartRequestHandler = multipartRequestHandler;
	}

	public String getNightEndTime() {
		return nightEndTime;
	}

	public void setNightEndTime(String nightEndTime) {
		this.nightEndTime = nightEndTime;
	}

	public String getNightPrice() {
		return nightPrice;
	}

	public void setNightPrice(String nightPrice) {
		this.nightPrice = nightPrice;
	}

	public String getNightStartTime() {
		return nightStartTime;
	}

	public void setNightStartTime(String nightStartTime) {
		this.nightStartTime = nightStartTime;
	}

	public String getParkAddr() {
		return parkAddr;
	}

	public void setParkAddr(String parkAddr) {
		this.parkAddr = parkAddr;
	}

	public String getParkBeyondTime() {
		return parkBeyondTime;
	}

	public void setParkBeyondTime(String parkBeyondTime) {
		this.parkBeyondTime = parkBeyondTime;
	}

	public String getParkCity() {
		return parkCity;
	}

	public void setParkCity(String parkCity) {
		this.parkCity = parkCity;
	}

	public String getParkId() {
		return parkId;
	}

	public void setParkId(String parkId) {
		this.parkId = parkId;
	}

	public String getParkMinTime() {
		return parkMinTime;
	}

	public void setParkMinTime(String parkMinTime) {
		this.parkMinTime = parkMinTime;
	}

	public String getParkMobile() {
		return parkMobile;
	}

	public void setParkMobile(String parkMobile) {
		this.parkMobile = parkMobile;
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

	public String getParkPro() {
		return parkPro;
	}

	public void setParkPro(String parkPro) {
		this.parkPro = parkPro;
	}

	public String getParkStatus() {
		return parkStatus;
	}

	public void setParkStatus(String parkStatus) {
		this.parkStatus = parkStatus;
	}

	public String getParkSum() {
		return parkSum;
	}

	public void setParkSum(String parkSum) {
		this.parkSum = parkSum;
	}

	public String getParkTotal() {
		return parkTotal;
	}

	public void setParkTotal(String parkTotal) {
		this.parkTotal = parkTotal;
	}

	public String getParkType() {
		return parkType;
	}

	public void setParkType(String parkType) {
		this.parkType = parkType;
	}

	public String getRemainPark() {
		return remainPark;
	}

	public void setRemainPark(String remainPark) {
		this.remainPark = remainPark;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getServletWrapper() {
		return servletWrapper;
	}

	public void setServletWrapper(String servletWrapper) {
		this.servletWrapper = servletWrapper;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
