package com.sunrise.epark.bean;

/**
 * 返回结果
 * @author 钟继业 E-mail:zhongjy@camelotchina.com.cn
 * @version 创建时间：2015年7月9日 上午10:47:21 类说明
 */
public class Result {
	/** 错误码 */
	public int msgCode;

	/** 返回结果 */
	public String result;
	
	/**
	 * @param msgCode 错误码
	 * @param result  返回结果
	 */
	public Result(int msgCode, String result) {
		super();
		this.msgCode = msgCode;
		this.result = result;
	}

	public Result() {
		super();
	}

	public int getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
