package com.sunrise.epark.configure;
import android.content.Context;

/**
 * 接口
 * @author  钟继业 E-mail:zhongjy@camelotchina.com.cn
 * @version 创建时间：2015年7月24日 下午2:59:20
 * 类说明
 */
public class Urls {

	public static Context context;
	/**接口IP*/
    public static String SERVER_IP="www.sunrise.network";
	//public static String SERVER_IP="192.168.1.144";
	/**接口IP*/ 
  //  public static String SERVER_IP="192.168.1.79";//"www.sunrise.network";
	/**接口端口*/
	//public static int SERVER_PORT=8080;
    public static int SERVER_PORT=15926;
	//public static int SERVER_PORT=8088;//15926;
	/**接口地址*/
	public static String SERVER_ADDRESS="http://"+SERVER_IP+":"+SERVER_PORT+"/parking/app/user/";
	public static String SUFFIX=".do?";
	/**App更新地址*/
	public static String APK_URL;
	/**注册接口*/
	public static String REGISTER_ACTION=SERVER_ADDRESS+"register"+SUFFIX;//参数:	registerName	registerPassword
	/** 登陆接口*/
	public static String LOGIN_ACTION=SERVER_ADDRESS+"login"+SUFFIX;//参数:	userAcount	userPassword	uuid
	/** 停车场推荐列表*/
	public static String PARKLIST_ACTION=SERVER_ADDRESS+"parkList"+SUFFIX;//	参数:	userId	uuid	longitude	latitude	distance(0:筛选距离不限制)	page	priceSort(“”：为升序 “desc”：为降序)
	/** 忘记密码*/
	public static String FORGET_PWD=SERVER_ADDRESS+"sendEmail"+SUFFIX;//参数:	LoginName		
	/** 查询账户密码*/
	public static String FIND_USERNAME_PWD=SERVER_ADDRESS+"queryPasswoid"+SUFFIX;//	参数:	userId	
	/**重置密码*/
	public static String CHANGE_PWD=SERVER_ADDRESS+"UpdatePwd"+SUFFIX;//	参数:	password	userId	
	/**获取预约列表*/
	public static String SUBSCRIBE_LIST = SERVER_ADDRESS+"listOrder"+SUFFIX;
	/**预约详情*/
	public static String SUBSCRIBE_DETAIL =SERVER_ADDRESS + "getOrderDetail"+SUFFIX;
	/**修改姓名*/
	public static String CHANGE_NAME=SERVER_ADDRESS+"getLoginNameUpdateUserName"+SUFFIX;//	参数:	userId	userName
	/**查询车牌*/
	public static String FIND_CARID=SERVER_ADDRESS+"selectCar"+SUFFIX;//参数:	userId
	/**修改车牌*/
	public static String CHANGE_CARID=SERVER_ADDRESS+"updateDefaultCat"+SUFFIX;//	参数:	userId	carId
	/**修改头像*/
	public static String CHANGE_IMG=SERVER_ADDRESS+"changeFace"+SUFFIX;//		参数:	userId	imgContent
	/**新增车牌号*/
	public static String ADD_CARID=SERVER_ADDRESS+"InsertCarName"+SUFFIX;//	参数:	userId	carName
	/**检查更新*/
	public static String CHECK_VERSION=SERVER_ADDRESS+"getNewVersion"+SUFFIX;//	参数:	userId	carName
	/**查询全部个人信息*/
	public static String FIND_ALL_USERINFO=SERVER_ADDRESS+"getLoginNameByAll"+SUFFIX;//	参数:	userId	
	/**充值请求*/
	public static String CHONGZHI_REQUEST=SERVER_ADDRESS+"paymentRequest"+SUFFIX;//	参数:	payType	payLogo	payAmount	userId	uuid
	/**充值请求结果*/
	public static String CHONGZHI_BACK_RESULT=SERVER_ADDRESS+"paymentResponse"+SUFFIX;//	参数:	trade_status	out_trade_no
	/**新增邮箱*/
	public static String ADD_EMAIL=SERVER_ADDRESS+"insertUserMail"+SUFFIX;//	参数:	userId	userMail
	/**新增手机号*/
	public static String ADD_PHONE=SERVER_ADDRESS+"insertUserTel"+SUFFIX;//	参数:	userId	userTel
	/**账户总金额*/
	public static String ACCOUNT_AMOUNT=SERVER_ADDRESS+"getUserAccountAmount"+SUFFIX;	//	 参数: 	userId	
	/**账单列表*/		
	public static String LIST_PAYDETAIL=SERVER_ADDRESS+"listPayDetail"+SUFFIX;//	参数:	userId 	page 	size		
	/**账单详情-套餐预约，停车记录明细*/
	public static String PAY_DETAIL=SERVER_ADDRESS+"getPayDetail"+SUFFIX;	//		参数:	payId	
	/**账单详情-套餐预约，停车记录明细*/
	public static String PACKAGE_DETAIL=SERVER_ADDRESS+"getPackageDetail"+SUFFIX;	//		参数:	payId	
	/**账单详情-套餐预约，停车记录明细*/
	public static String CHARGE_DETAIL=SERVER_ADDRESS+"getChargeDetail"+SUFFIX;	//		参数:	payId	
	
	/**充值请求*/
	public static String PAYMENT_REQUEST=SERVER_ADDRESS+"paymentRequest"+SUFFIX;//	参数:	payType	payLogo	payAmount	(userId	uuid)		
	/**停车场明细*/
	public static String STOPCAR_MINGXI=SERVER_ADDRESS+"parkDetail"+SUFFIX;//参数:	(userId	uuid)	parkId		
	/**停车记录（订单）-列表*/
	public static String STOPCAR_RECORD_LIST=SERVER_ADDRESS+"listOrder"+SUFFIX;//		参数:	orderType=00	userId	page	size		
	/**停车记录（订单）-明细*/
	public static String STOPCAR_RECORD_MINGXI=SERVER_ADDRESS+"getOrderDetail"+SUFFIX;//		参数:	orderId		
	/**预约列表-预约列表*/
	public static String YUYUE_LIST=SERVER_ADDRESS+"listOrder"+SUFFIX;//	参数:	orderType=01	userId	page	size		
	/**预约列表-预约详情*/
	public static String YUYUE_DETAIL=SERVER_ADDRESS+"getOrderDetail"+SUFFIX;	//	参数:	orderId		
	/**订单列表*/
	public static String ORDER_LIST=SERVER_ADDRESS+"listOrder"+SUFFIX;//orderStatus:01 未进场订单列表	02:在场订单列表   orderType=02 page size
	/**订单详情*/
	public static String ORDER_DETAIL=SERVER_ADDRESS+"getOrderDetail"+SUFFIX;	//	参数:	orderId
	/**我要进场*/
	public static String PARK_IN=SERVER_ADDRESS+"parkIn"+SUFFIX;	//	参数:	userId	uuid	parkId
	/**进场取消*/
	public static String PARK_CANCEL_IN=SERVER_ADDRESS+"parkCancelIn"+SUFFIX;	//	参数:	userId	uuid	parkId
	/**确定进场*/
	public static String CONFIRM_IN=SERVER_ADDRESS+"managerResponse"+SUFFIX;	//	参数:	userId	uuid	orderId	startTime
	/**停车管理员界面-订单记录列表*/
	public static String LIST_ORDER=SERVER_ADDRESS+"listOrder"+SUFFIX;	//	参数:	userId	uuid	parkId	creatTime=yyyy-mm-dd	page
	/**套餐选配-列表*/
	public static String LIST_PACKAGE=SERVER_ADDRESS+"listPackage"+SUFFIX;	//	参数:	page	size
	/**根据用户获取默认车牌*/
	public static String GET_CATNAME_BY_USERID=SERVER_ADDRESS+"getCarNameByUserId"+SUFFIX;	//	参数:	userId	uuid
	/**发票邮寄-维护／提交*/
	public static String INSERT_POST=SERVER_ADDRESS+"insertPost"+SUFFIX;	//	参数:	userId	uuid	postName  postAmount postLook postTel postAddr	
	/**根据用户ID获取用户可开发票最大金额*/
	public static String GET_MAXPOST=SERVER_ADDRESS+"getMaxPost"+SUFFIX;	//	参数:userId	uuid
	/**用户反馈*/
	public static String INSERT_FEED_BACK=SERVER_ADDRESS+"insertFeedback"+SUFFIX;	//	参数:	userId	uuid	fbContent
	/**我的消息-消息列表*/
	public static String LIST_NOTICE=SERVER_ADDRESS+"listNotice"+SUFFIX;	//	参数:	userId	uuid
	/**我的消息-消息明细*/
	public static String GET_NOTICE_DETAIL=SERVER_ADDRESS+"getNoticeDetail"+SUFFIX;	//	参数:userId	uuid	noticeId
	/**消息列表*/
	public static String MESSAGE_LIST=SERVER_ADDRESS+"listNotice"+SUFFIX;
	/**查询是否有申请进场、申请出场和申请补单接口*/
	public static String CHECK_CLOCK=SERVER_ADDRESS+"makeAppointmentCheck"+SUFFIX;
	/**取消进场**/
	public static String ENTER_PARK_CANCEL = SERVER_ADDRESS+"parkCancelIn"+SUFFIX;
	/**进场 产生订单号*/
	public static String ENTER_PARK = SERVER_ADDRESS + "parkIn"+SUFFIX;//
	/**补单申请*/
	public static String BUDAN_SHENQING = SERVER_ADDRESS + "replacementOrder"+SUFFIX;
	/**quxiao补单申请*/
	public static String BUDAN_CANCEL = SERVER_ADDRESS + "replacementCancelIn"+SUFFIX;
	/**停车记录*/
	public static String STOP_LIST = SERVER_ADDRESS + "listOrder"+SUFFIX;
	/**停车记录详情*/
	public static String STOP_LIST_DETAIL =SERVER_ADDRESS +"getOrderDetail"+SUFFIX;
	/**套餐选配 列表**/
	public static String PACKAGE_SELECTION = SERVER_ADDRESS+"listPackage"+SUFFIX;
	/**推广列表*/
	public static String ADVER_LIST = SERVER_ADDRESS + "listAdv"+SUFFIX;//
	/**延长付费*/
	public static String DELAY_PAY=SERVER_ADDRESS+"overTimeCost"+SUFFIX;
	/**确定出场*/
	public static String PARK_OUT = SERVER_ADDRESS + "parkOut"+SUFFIX;//userId	uuid	parkId
	
	/**删除车牌*/
	public static String DELETE_CAR = SERVER_ADDRESS + "deleteCar"+SUFFIX;
	/**停车场管理员确认进场*/
	public static String MANAGER_CONFIRM_IN = SERVER_ADDRESS + "managerResponseForIn"+SUFFIX;
	/**停车场管理员确认出场*/
	public static String MANAGER_CONFIRM_OUT = SERVER_ADDRESS + "managerResponseForOut"+SUFFIX;
	/**停车管理员取消订单*/
	public static String MANAGER_CANCLE_ORDER = SERVER_ADDRESS + "managerCancelIn"+SUFFIX;
	
	
	/**停车场管理员确认出场*/
	public static String MANAGER_CONFIRM_ADD_OUT = SERVER_ADDRESS + "managerResponseForReplacementOut"+SUFFIX;
	/**提交预约*/
	public static String MAKEAPPOINTMENT = SERVER_ADDRESS + "makeAppointment"+SUFFIX;
	/**预约进场*/
	public static String MAKEAPPOINTMENT_PARKIN = SERVER_ADDRESS + "makeAppointmentParkIn"+SUFFIX;
	/**取消预约*/
	public static String CANCEL_MAKEAPPOINTMENT = SERVER_ADDRESS + "CancelMakeAppointment"+SUFFIX;
	/**充值回调*/
	public static String PAY_RESPONS = SERVER_ADDRESS + "paymentResponse"+SUFFIX;
	/**套餐选配*/
	public static String MAKE_PACKAGE = SERVER_ADDRESS+"makePackageAppointment"+SUFFIX;
	/**钱包支付*/
	public static String WELLT_PAY = SERVER_ADDRESS +"payAmount"+SUFFIX;
	/**微信回调*/
	public static String WX_RESPONSE = SERVER_ADDRESS +"wechatResponse"+SUFFIX;
	/**支付宝回调*/
	public static String AL_RESPONSE = SERVER_ADDRESS +"alipayResponse"+SUFFIX;
}