package com.sunrise.epark.bean;
/**
 * 
* 描述:停车推广实体类 
* @author zhongjy
* @date 2015年8月7日
* @version 1.0
 */
public class Adver {

	/** 是否头条 */
	private boolean adverFlag;
	/** 推广图片地址 */
	private String adverImg;
	/** 推广内容 */
	private String adverCon;
	/** 推广链接地址 */
	private String adverUrl;

	public boolean isAdverFlag() {
		return adverFlag;
	}

	public void setAdverFlag(boolean adverFlag) {
		this.adverFlag = adverFlag;
	}

	public String getAdverImg() {
		return adverImg;
	}

	public Adver(boolean adverFlag, String adverImg, String adverCon,
			String adverUrl) {
		super();
		this.adverFlag = adverFlag;
		this.adverImg = adverImg;
		this.adverCon = adverCon;
		this.adverUrl = adverUrl;
	}

	public void setAdverImg(String adverImg) {
		this.adverImg = adverImg;
	}

	public String getAdverCon() {
		return adverCon;
	}

	public void setAdverCon(String adverCon) {
		this.adverCon = adverCon;
	}

	public String getAdverUrl() {
		return adverUrl;
	}

	public void setAdverUrl(String adverUrl) {
		this.adverUrl = adverUrl;
	}
}
