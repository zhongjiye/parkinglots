package com.sunrise.epark.bean;

/**
 * 描述:账单实体类
 * 
 * @author zhongjy
 * @date 2015年8月7日
 * @version 1.0
 */
public class Bill {
	/** 帐单类型 */
	private String type;
	/** 帐单类型 */
	private String typeName;

	/** 账单日期 */
	private String date;
	/** 订单号 */
	private String num;
	/** 金额 */
	private String money;
	/** 备注 */
	private String remark;
	/** 正负标识 */
	private String flag;

	private String payId;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getType() {
		return type;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Bill() {
		super();
	}

	public Bill(String type, String typeName, String date, String num,
			String money, String remark, String flag, String payId) {
		super();
		this.type = type;
		this.typeName = typeName;
		this.date = date;
		this.num = num;
		this.money = money;
		this.remark = remark;
		this.flag = flag;
		this.payId = payId;
	}

}
