package com.sunrise.epark.database;

/**
 * 以下存放db的字段 建表语句 /查询字段 将字段定义成static便于 批量修改字段
 * 
 * @author wenjian
 * 
 */
public interface DataBaseConstDefine {

	/**
	 * 版本
	 */
	public static final int VERSION = 1;
	/**
	 * *****************************24,日志信息表****************
	 */
	/**
	 * 24,日志信息表
	 */
	public static final String TABLE_LOGS = "LOGS";
	/**
	 * id
	 */
	public static final String LOGS_ID = "ID";
	/**
	 * 调用者账号
	 */
	public static final String LOGS_SAPACCOUNT = "SAPACCOUNT";
	/**
	 * 发送系统
	 */
	public static final String LOGS_SENDSYS = "SENDSYS";
	/**
	 * 接收系统
	 */
	public static final String LOGS_RECEIVESYS = "RECEIVESYS";
	/**
	 * 接口名称
	 */
	public static final String LOGS_APINAME = "APINAME";
	/**
	 * 开始时间
	 */
	public static final String LOGS_STARTTIME = "STARTTIME";
	/**
	 * 结束时间
	 */
	public static final String LOGS_ENDTIME = "ENDTIME";
	/**
	 * 传输数据
	 */
	public static final String LOGS_DATA = "DATA";
	/**
	 * 处理结果
	 */
	public static final String LOGS_RES = "RES";
	/**
	 * 异常信息
	 */
	public static final String LOGS_ERRORINFO = "ERRORINFO";
	/**
	 * 提交状态
	 */
	public static final String LOGS_UPLOADSTATUS = "UPLOADSTATUS";
	/**
	 * 日志信息表的字段集合
	 */
	public static final String[] LOGS_COLUMNS = { LOGS_ID, LOGS_SAPACCOUNT, LOGS_SENDSYS, LOGS_RECEIVESYS,
			LOGS_APINAME, LOGS_STARTTIME, LOGS_ENDTIME, LOGS_DATA, LOGS_RES, LOGS_ERRORINFO, LOGS_UPLOADSTATUS };
	/**
	 * 日志信息表的字段集合(不含ID)
	 */
	public static final String[] LOGS_COLUMNS_SHORT = { LOGS_SAPACCOUNT, LOGS_SENDSYS, LOGS_RECEIVESYS, LOGS_APINAME,
			LOGS_STARTTIME, LOGS_ENDTIME, LOGS_DATA, LOGS_RES, LOGS_ERRORINFO, LOGS_UPLOADSTATUS };
	/**
	 * 日志信息表建表语句
	 */
	public static final String LOGS_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGS + " (" + LOGS_ID
			+ "  INTEGER PRIMARY KEY AUTOINCREMENT," + LOGS_SAPACCOUNT + " TEXT," + LOGS_SENDSYS + " TEXT,"
			+ LOGS_RECEIVESYS + " TEXT," + LOGS_APINAME + " TEXT," + LOGS_STARTTIME + " TEXT," + LOGS_ENDTIME
			+ " TEXT," + LOGS_DATA + " TEXT," + LOGS_RES + " TEXT," + LOGS_ERRORINFO + " TEXT," + LOGS_UPLOADSTATUS
			+ "  TEXT) ";
	/**
	 * *****************************25,配置记录表****************
	 */
	/**
	 * 25,配置记录表
	 */
	public static final String TABLE_PROFILEINFO = "PROFILEINFO";
	/**
	 * id
	 */
	public static final String PROFILEINFO_ID = "ID";
	/**
	 * 初始页配置
	 */
	public static final String PROFILEINFO_MAINPAGE = "MAINPAGE";
	/**
	 * 优先业务配置
	 */
	public static final String PROFILEINFO_AUART = "AUART";
	/**
	 * 当前APP版本
	 */
	public static final String PROFILEINFO_CURRENTVER = "CURRENTVER";
	/**
	 * 最新版本号
	 */
	public static final String PROFILEINFO_NEWVER = "NEWVER";
	/**
	 * 平台账号
	 */
	public static final String PROFILEINFO_PLATFORMACCOUNT = "PLATFORMACCOUNT";
	/**
	 * 平台密码
	 */
	public static final String PROFILEINFO_PLATFORMPASS = "PLATFORMPASS";
	/**
	 * sap账号
	 */
	public static final String PROFILEINFO_SAPACCOUNT = "SAPACCOUNT";
	/**
	 * sap密码
	 */
	public static final String PROFILEINFO_SAPPASS = "SAPPASS";
	/**
	 * 图案密码
	 */
	public static final String PROFILEINFO_PATTERNSPASS = "PATTERNSPASS";
	/**
	 * 用户姓名
	 */
	public static final String PROFILEINFO_USERNAME = "USERNAME";
	/**
	 * 用户电话
	 */
	public static final String PROFILEINFO_USERTEL = "USERTEL";
	/**
	 * 皮肤选择
	 */
	public static final String PROFILEINFO_SKINS = "SKINS";
	/**
	 * 轮询时间1
	 */
	public static final String PROFILEINFO_SEARTIME = "SEARTIME";
	/**
	 * 消息保留时间2
	 */
	public static final String PROFILEINFO_POOLTIME = "POOLTIME";
	/**
	 * 定时时间3
	 */
	public static final String PROFILEINFO_SCHEDULETIME = "SCHEDULETIME";
	/**
	 * 消息上一次获取时间
	 */
	public static final String PROFILEINFO_LASTSYNCDATE = "LASTSYNCDATE";
	/**
	 * 预留字段1
	 */
	public static final String PROFILEINFO_EXT1 = "EXT1";
	/**
	 * 预留字段2
	 */
	public static final String PROFILEINFO_EXT2 = "EXT2";
	/**
	 * 预留字段3
	 */
	public static final String PROFILEINFO_EXT3 = "EXT3";
	/**
	 * 配置记录表的字段集合
	 */
	public static final String[] PROFILEINFO_COLUMNS = { PROFILEINFO_ID, PROFILEINFO_MAINPAGE, PROFILEINFO_AUART,
			PROFILEINFO_CURRENTVER, PROFILEINFO_NEWVER, PROFILEINFO_PLATFORMACCOUNT, PROFILEINFO_PLATFORMPASS,
			PROFILEINFO_SAPACCOUNT, PROFILEINFO_SAPPASS, PROFILEINFO_PATTERNSPASS, PROFILEINFO_USERNAME,
			PROFILEINFO_USERTEL, PROFILEINFO_SKINS, PROFILEINFO_SEARTIME, PROFILEINFO_POOLTIME,
			PROFILEINFO_SCHEDULETIME, PROFILEINFO_LASTSYNCDATE, PROFILEINFO_EXT1, PROFILEINFO_EXT2, PROFILEINFO_EXT3 };
	/**
	 * 配置记录表的字段集合(不含ID)
	 */
	public static final String[] PROFILEINFO_COLUMNS_SHORT = { PROFILEINFO_MAINPAGE, PROFILEINFO_AUART,
			PROFILEINFO_CURRENTVER, PROFILEINFO_NEWVER, PROFILEINFO_PLATFORMACCOUNT, PROFILEINFO_PLATFORMPASS,
			PROFILEINFO_SAPACCOUNT, PROFILEINFO_SAPPASS, PROFILEINFO_PATTERNSPASS, PROFILEINFO_USERNAME,
			PROFILEINFO_USERTEL, PROFILEINFO_SKINS, PROFILEINFO_SEARTIME, PROFILEINFO_POOLTIME,
			PROFILEINFO_SCHEDULETIME, PROFILEINFO_LASTSYNCDATE, PROFILEINFO_EXT1, PROFILEINFO_EXT2, PROFILEINFO_EXT3 };
	/**
	 * 配置记录表建表语句
	 */
	public static final String PROFILEINFO_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILEINFO + " ("
			+ PROFILEINFO_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT," + PROFILEINFO_MAINPAGE + " TEXT,"
			+ PROFILEINFO_AUART + " TEXT," + PROFILEINFO_CURRENTVER + " TEXT," + PROFILEINFO_NEWVER + " TEXT,"
			+ PROFILEINFO_PLATFORMACCOUNT + " TEXT," + PROFILEINFO_PLATFORMPASS + " TEXT," + PROFILEINFO_SAPACCOUNT
			+ " TEXT," + PROFILEINFO_SAPPASS + " TEXT," + PROFILEINFO_PATTERNSPASS + " TEXT," + PROFILEINFO_USERNAME
			+ " TEXT," + PROFILEINFO_USERTEL + " TEXT," + PROFILEINFO_SKINS + " TEXT," + PROFILEINFO_SEARTIME
			+ " TEXT," + PROFILEINFO_POOLTIME + " TEXT," + PROFILEINFO_SCHEDULETIME + " TEXT,"
			+ PROFILEINFO_LASTSYNCDATE + " TEXT," + PROFILEINFO_EXT1 + " TEXT," + PROFILEINFO_EXT2 + " TEXT,"
			+ PROFILEINFO_EXT3 + "  TEXT) ";
	/**
	 * *****************************26,消息记录表****************
	 */
	/**
	 * 26,消息记录表
	 */
	public static final String TABLE_MESSAGEINFO = "MESSAGEINFO";
	/**
	 * id
	 */
	public static final String MESSAGEINFO_ID = "ID";
	/**
	 * 消息编号
	 */
	public static final String MESSAGEINFO_MSGCODE = "MSGCODE";
	/**
	 * 消息日期 yyyy-MM-dd HH:mm
	 */
	public static final String MESSAGEINFO_MSGDATE = "MSGDATE";
	/**
	 * 消息内容
	 */
	public static final String MESSAGEINFO_MSGCONTENT = "MSGCONTENT";
	/**
	 * 接收者 员工编号
	 */
	public static final String MESSAGEINFO_MSGRECEIVER = "MSGRECEIVER";
	/**
	 * 消息类型描述
	 * 任务取消(A004)/任务改派(A002)/派工通知(A001)/基础数据更新通知(B001)/系统通知(C001)/派工单更新(A003)
	 */
	public static final String MESSAGEINFO_MSGTYPEDESC = "MSGTYPEDESC";
	/**
	 * 消息类型 A001/A002/A003/A004/B001/C001
	 */
	public static final String MESSAGEINFO_MSGTYPE = "MSGTYPE";
	/**
	 * 查看状态
	 */
	public static final String MESSAGEINFO_VIEWSTATUS = "VIEWSTATUS";
	/**
	 * 单号 工单单号/通知单号
	 */
	public static final String MESSAGEINFO_TASKNO = "TASKNO";
	/**
	 * 单据类型 工单/通知单
	 */
	public static final String MESSAGEINFO_TASKTYPE = "TASKTYPE";
	/**
	 * 用户编码 一个sap账号可能会有多个员工编号
	 */
	public static final String MESSAGEINFO_PERNR = "PERNR";
	/**
	 * (基础下载)更新的表名 改一个表产生一条消息
	 */
	public static final String MESSAGEINFO_UPDATETABLE = "UPDATETABLE";
	/**
	 * 消息表的字段集合
	 */
	public static final String[] MESSAGEINFO_COLUMNS = { MESSAGEINFO_ID, MESSAGEINFO_MSGCODE, MESSAGEINFO_MSGDATE,
			MESSAGEINFO_MSGCONTENT, MESSAGEINFO_MSGRECEIVER, MESSAGEINFO_MSGTYPEDESC, MESSAGEINFO_MSGTYPE,
			MESSAGEINFO_VIEWSTATUS, MESSAGEINFO_TASKNO, MESSAGEINFO_TASKTYPE, MESSAGEINFO_PERNR,
			MESSAGEINFO_UPDATETABLE };
	/**
	 * 消息表的字段集合(不含ID)
	 */
	public static final String[] MESSAGEINFO_COLUMNS_SHORT = { MESSAGEINFO_MSGCODE, MESSAGEINFO_MSGDATE,
			MESSAGEINFO_MSGCONTENT, MESSAGEINFO_MSGRECEIVER, MESSAGEINFO_MSGTYPEDESC, MESSAGEINFO_MSGTYPE,
			MESSAGEINFO_VIEWSTATUS, MESSAGEINFO_TASKNO, MESSAGEINFO_TASKTYPE, MESSAGEINFO_PERNR,
			MESSAGEINFO_UPDATETABLE };
	/**
	 * 消息表建表语句
	 */
	public static final String MESSAGEINFO_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGEINFO + " ("
			+ MESSAGEINFO_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT," + MESSAGEINFO_MSGCODE + " TEXT,"
			+ MESSAGEINFO_MSGDATE + " TEXT," + MESSAGEINFO_MSGCONTENT + " TEXT," + MESSAGEINFO_MSGRECEIVER + " TEXT,"
			+ MESSAGEINFO_MSGTYPEDESC + " TEXT," + MESSAGEINFO_MSGTYPE + " TEXT," + MESSAGEINFO_VIEWSTATUS + " TEXT,"
			+ MESSAGEINFO_TASKNO + " TEXT," + MESSAGEINFO_TASKTYPE + " TEXT," + MESSAGEINFO_PERNR + " TEXT,"
			+ MESSAGEINFO_UPDATETABLE + "  TEXT) ";
}
