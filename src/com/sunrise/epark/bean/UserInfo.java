package com.sunrise.epark.bean;

import java.util.List;

/**
 * 用户信息实体类
 * 
 * @author 钟继业 E-mail:zhongjy@camelotchina.com.cn
 * @version 创建时间：2015年7月23日 下午3:43:14 类说明
 */
public class UserInfo {
	/**
	 *login:用户已登录
	 *exit:用户已退出登录
	 *register:用户注册但未维护个人信息 
	 */
	public static String loginStatus;
	/**用户UUID,单人登录使用*/
	public static String uuid;
	/**用户角色*/
	public static String userRole;
	/**用户ID*/
	public static String userId;
	/**头像url地址*/
	public static String imgUrl;
	/**用户姓名*/
	public static String userName;
	/**用户手机号码*/
	public static String userTel;
	/**用户邮箱*/
	public static String userMail;
	/**用户默认车牌号*/
	public static String carName;
	/**用户地址*/
	public static String userAddr;
	/**用户账户*/
	public static String userLogin;
	/**停车场ID*/
	public static String parkId;
	/**账户名字*/
	public static String loginName;	
	/**账户余额*/
	public static String money;
	/***/
	public static String userPassWord;
	
	public static double curr_geoLat;// 当前经度

	public static double curr_geoLng;// 当前纬度
}
