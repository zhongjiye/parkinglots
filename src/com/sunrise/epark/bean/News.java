package com.sunrise.epark.bean;

/**
 * 
* 描述:消息推送实体类 
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class News {
	/**消息日期*/
	private String messageDate;
	/**消息内容*/
	private String messageContent;
	/**消息类型*/
	private String messageType;

	public String getMessageDate() {
		return messageDate;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public String getMessageType() {
		return messageType;
	}

	public News(String messageDate, String messageContent,
			String messageType) {
		super();
		this.messageDate = messageDate;
		this.messageContent = messageContent;
		this.messageType = messageType;
	}
}
