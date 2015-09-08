package com.sunrise.epark.configure;

public class Configure {
	
	/*** 保存在手机中SharePrefrenc的名称 */
	public static String SP_NANE="parkingLots";
	
	/**接口超时时间*/
	public static int TimeOut=20*1000;
	/**接口超时重试次数*/
	public static int Retry=1;
	/**接口超时因子*/
	public static float Multiplier=1.0f;
	
	
	/**UserAgent信息*/
	public static String USERAGENT;
	/**日志文件存储路径*/
	public static String LOG_PATH;
	/**数据库名称*/
	public static String DATABASE_NAME;
	/**数据库版本*/
	public static int DB_VERSION;
	
	/**无网络*/
	public static  int NETWORK_OFF=-1;
	/**网络状态为2G*/
	public static  int NETWORK_2G=2;
	/**网络状态为3G*/
	public static  int NETWORK_3G=3;
	/**网络状态为4G*/
	public static  int NETWORK_4G=4;
	/**网络状态为其它*/
	public static  int NETWORK_OTHER=5;
	/**网络状态为wifi*/
	public static  int NETWORK_WIFI=6;

}
