package com.sunrise.epark.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateTool {

	/**
	 * 获得当天时间。转化为"yyyy-MM-dd HH-mm-ss"格式
	 */
	public static String getToday() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(date);
		return today;
	}
	
	public static String getday(String dateTime) {
		long long1=Long.parseLong(dateTime);
		Date date = new Date(long1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(date);
		return today;
	}

	/**
	 * 日期相减
	 * 
	 * @param dateString
	 *            日期对象 ，格式如 2012-06-18 11:11:11
	 * 
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	public String getSubtract(String date1, String date2) {
		String so = "0";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long dt1 = formatter.parse(date1).getTime();
			long dt2 = formatter.parse(date2).getTime();
			long l = dt2 - dt1;
			long mSec = l % 1000;
			l /= 1000;
			long year = l / (365 * 24 * 3600);
			l = l % (365 * 24 * 3600);
			long month = l / (30 * 24 * 3600);
			l = l % (30 * 24 * 3600);
			long day = l / (24 * 3600);
			l = l % (24 * 3600);
			long hour = l / 3600;
			l = l % 3600;
			long min = l / 60;
			l = l % 60;
			long sec = l;
			if (year > 0) {
				so = (year + "年" + month + "月" + day + "天" + hour + "小时" + min + "分" + sec + "秒" + "");
			}
			else if (month > 0) {
				so = (month + "月" + day + "天" + hour + "小时" + min + "分" + sec + "秒" + "");
			}
			else if (day > 0) {
				so = (day + "天" + hour + "小时" + min + "分" + sec + "秒" + "");
			}
			else if (hour > 0) {
				so = (hour + "小时" + min + "分" + sec + "秒" + "");
			}
			else if (min > 0) {
				so = (min + "分" + sec + "秒" + "");
			}
			else if (sec > 0) {
				so = (sec + "秒" + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return so;
	}

	/**
	 * 字符串转换成日期类型
	 * 
	 * @param dateString
	 *            日期
	 * @param timeString
	 *            时间
	 * @return
	 */
	public static Date stringTransformDate(String dateString, String timeString) {
		// Date date=new Date();
		if (null == dateString || null == timeString) {
			return null;
		}
		else {
			String stringadd = dateString.trim() + " " + timeString.trim();
			SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			try {
				date = format4.parse(stringadd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return date;
		}
	}

	public static Date stringTransformDate2(String dateString, String timeString) {
		// Date date=new Date();
		if (null == dateString || null == timeString) {
			return null;
		}
		else {
			String stringadd = dateString.trim() + " " + timeString.trim();
			SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date();
			try {
				date = format4.parse(stringadd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return date;
		}
	}

	/**
	 * 从Date中获取年月日 "yyyy-MM-dd"
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFromDate(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 从Date中获取 "HH:mm:ss"
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeFromDate1(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 从Date中获取 "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFromDate2(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 从Date中获取年月
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateToStr(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("MM-dd");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 从日期中得到月日
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateToMonthDay(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			dateString = format.format(date);
		}
		return dateString;
	}
	
	/**
	 * 从日期中得到月日2
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateToMonthDay2(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 从Date中获取时分秒
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeFromDate(Date date) {
		String timeString = "";
		if (null != date) {
			timeString = DateFormat.getTimeInstance().format(date);
		}
		return timeString;
	}

	/**
	 * 从Date中获取年月日时分秒 yyyyMMddHHmmss
	 * 
	 * @param date
	 * @return
	 */
	public static String getFullStrFromDate(Date date) {
		String dateString = "";
		if (null == date) {
		}
		else {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 把时间格式HH：MM：SS字符串转化成Time
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Time strToTime(String str) {
		java.sql.Time time = null;
		try {
			if (str != null && !"".equals(str.trim())) {
				// String转化 Time
				time = Time.valueOf(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 把时间格式HH：MM：SS字符串转化成Time
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Time strToTime2(String str) {
		java.sql.Time time = null;
		try {
			if (str != null && !"".equals(str.trim())) {
				// String转化 Time
				time = Time.valueOf(str + ":00");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 把时间格式HH：MM：SS转化成字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeStr(Time time) {
		String dateString = "";
		if (null != time) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			dateString = formatter.format(time);
		}
		return dateString;
	}

	/**
	 * 把时间格式yyyy-MM-dd转化成字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String getDateStr(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	public static String getDateStrNew(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	public static String getDateStrNew2(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	/**
	 * 把时间格式HH：MM：SS转化成字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String getDateStr3(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	/**
	 * 把时间格式HH：MM转化成字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String getDateStr2(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	/**
	 * Date中获取 时分 字符串 HH:mm
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeStr3(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	/**
	 * Date中获取 时字符串
	 * 
	 * @param date
	 * @return
	 */
	public static int getTimeStr4(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH");
			dateString = formatter.format(date);
			return Integer.parseInt(dateString);
		}
		else {
			return 0;
		}
	}

	/**
	 * 把时间格式HH：MM转化成字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeStr2(Time time) {
		String dateString = "";
		if (null != time) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			dateString = formatter.format(time);
		}
		return dateString;
	}

	/**
	 * 从Date中获取年月日时分秒 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String getFullStrFromDate2(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStringFull(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * "yyyyMMddHHmmss"
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Date strToDate(String str) throws Exception {
		java.sql.Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			// Date转化String
			date = new java.sql.Date(format.parse(str).getTime());
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyyMMdd
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static java.sql.Date strToDateYMD(String str) throws Exception {
		java.sql.Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("");
			// Date转化String
			date = new java.sql.Date(format.parse(str).getTime());
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyy-MM-dd
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date strToDateYMD2(String str) {
		Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				// Date转化String
				date = new Date(format.parse(str).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyy-MM
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date strToDateYMD3(String str) {
		Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyy-MM");
			try {
				// Date转化String
				date = new Date(format.parse(str).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyyMM
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date strToDateYM(String str) {
		Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyyMM");
			try {
				// Date转化String
				date = new Date(format.parse(str).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyyMMdd
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date strToDateYMdd(String str) {
		Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				// Date转化String
				date = new Date(format.parse(str).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * hh:mm:ss
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date strToDateHms(String str) {
		Date date = null;
		if (str != null && !"".equals(str.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("hh24:mm:ss");
			try {
				// Date转化String
				date = new Date(format.parse(str).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println(str);
		return date;
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * 
	 * @param str
	 * @return
	 */
	public static Date strFormatToDate(String str) {
		Date date = null;
		SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format4.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date strFormatToDateNoMiao(String str) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(str);
		buffer.append(":00");
		Date date = null;
		SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format4.parse(buffer.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获得两个时间之间的日期
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	public static List<Date> getTwoDateBetween(Date start, Date end, String type) throws ParseException {
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = new ArrayList<Date>();
		dates.add(start);
		startCalendar.setTime(start);
		endCalendar.setTime(end);
		while (true) {
			startCalendar.add(Calendar.DAY_OF_MONTH, 1);
			if (startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) {
				dates.add(df.parse(df.format(startCalendar.getTime())));
			}
			else {
				break;
			}
		}
		if (start != end) {
			if (type.equalsIgnoreCase("car")) {
				Calendar startCalendar1 = Calendar.getInstance();
				Calendar endCalendar1 = Calendar.getInstance();
				startCalendar1.setTime(start);
				endCalendar1.setTime(end);
				if (startCalendar1.get(Calendar.DAY_OF_YEAR) == endCalendar1.get(Calendar.DAY_OF_YEAR)) {
				}
				else {
					dates.add(end);
				}
			}
		}
		return dates;
	}

	// 求两个日期之间的差值
	public static List<Date> getStartAndEndDateDays(Date start, Date end) {
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = new ArrayList<Date>();
		if (end.before(start)) {// 如果结束时间比开始时间还早
		}
		else if (end.after(start)) {// 结束时间比开始时间晚
			dates.add(start);// 先把开始时间加进去
			startCalendar.setTime(start);
			endCalendar.setTime(end);
			if (startCalendar.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR)) {// 表示同一天
			}
			else {
				while (true) {
					startCalendar.add(Calendar.DAY_OF_MONTH, 1);// 吧开始时间加一天
					if (startCalendar.get(Calendar.DAY_OF_YEAR) <= endCalendar.get(Calendar.DAY_OF_YEAR)) {
						try {
							dates.add(df.parse(df.format(startCalendar.getTime())));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					else {
						break;
					}
				}
			}
		}
		return dates;
	}

	/**
	 * 求两个日期差
	 * 
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 两个日期相差天数
	 */
	public static long GetDateMargin(Date beginDate, Date endDate) {
		long margin = 0;
		margin = endDate.getTime() - beginDate.getTime();
		margin = margin / (1000 * 60 * 60 * 24);
		return margin;
	}

	/*
	 * 得到 日 英文（月） 年
	 * 
	 * return: string param: Date
	 */
	public static String getDayMonthYearFromDate(Date date) {
		if (date == null) {
			return "";
		}
		else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);// 获取年
			int month = calendar.get(Calendar.MONTH);// 获取月
			int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取一月中的第几天
			String month_en = "";
			if (month == 0) {
				month_en = "JAN";
			}
			else if (month == 1) {
				month_en = "FEB";
			}
			else if (month == 2) {
				month_en = "MAR";
			}
			else if (month == 3) {
				month_en = "APR";
			}
			else if (month == 4) {
				month_en = "MAY";
			}
			else if (month == 5) {
				month_en = "JUN";
			}
			else if (month == 6) {
				month_en = "JUL";
			}
			else if (month == 7) {
				month_en = "AUG";
			}
			else if (month == 8) {
				month_en = "SEP";
			}
			else if (month == 9) {
				month_en = "OCT";
			}
			else if (month == 10) {
				month_en = "NOV";
			}
			else if (month == 11) {
				month_en = "DEC";
			}
			StringBuffer enDateStr = new StringBuffer("");
			enDateStr.append(String.valueOf(day));
			enDateStr.append(" ");
			enDateStr.append(month_en);
			enDateStr.append(" ");
			enDateStr.append(String.valueOf(year));
			return enDateStr.toString();
		}
	}

	/**
	 * 从Date中获取年月日 "yyyy-MM-dd"
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateStrFromDate(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
			dateString = format.format(date);
		}
		return dateString;
	}

	// ---------------------------------------------------------------PDA对时间的转换-----------------------------------------------------------
	/**
	 * yyyyMMddHHmmss
	 * 
	 * @author zhaozhaomx
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date pdaFromStrToDate(String strSyncDate) {
		boolean flag = false;
		Date date = new Date();
		if (strSyncDate != null && !"".equals(strSyncDate.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				// Date转化String
				date = format.parse(strSyncDate);
				flag = true;
			} catch (ParseException e) {
				e.printStackTrace();
				flag = false;
			}
		}
		else {
			return null;
		}
		// 如果处理成功返回Date
		if (flag) {
			return date;
		}
		else {// 处理失败返回null
			return null;
		}
	}

	/**
	 * yyyyMMddHHmmss
	 * 
	 * @author zhaozhaomx
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date pdaFromStrToDate2(String strSyncDate) {
		boolean flag = false;
		Date date = new Date();
		if (strSyncDate != null && !"".equals(strSyncDate.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				// Date转化String
				date = format.parse(strSyncDate);
				flag = true;
			} catch (ParseException e) {
				e.printStackTrace();
				flag = false;
			}
		}
		else {
			return null;
		}
		// 如果处理成功返回Date
		if (flag) {
			return date;
		}
		else {// 处理失败返回null
			return null;
		}
	}

	/**
	 * yyyy-MM-dd HH:mm
	 * 
	 * @author zhaozhaomx
	 * @param date
	 * @return
	 */
	public static String pdaFromDateToStr(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			dateString = format.format(date);
		}
		return dateString;
	}

	public static String getTimeStr(Date date) {
		String dateString = "";
		if (null != date) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			dateString = formatter.format(date);
		}
		return dateString;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate1(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay2(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String mdate = "";
			Date d = strToDate2(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	public static Date strToDate1(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	
	public static Date strToDate2(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	
	public static Date getDate(String date,SimpleDateFormat format){
		ParsePosition pos = new ParsePosition(0);
		Date newDate = format.parse(date, pos);
		return newDate;
	}
	/**
	 * 获取当前时间 + 年数
	 * 
	 */
	public static String calculateYear(String strDate,int num) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse(strDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR,num);
			calendar.add(Calendar.DATE, -1);
			
			return sdf.format(calendar.getTime());
			
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	/*--------------------------同步服务器时间begin---------------------------------------*/
	/**
	 * 求两个日期时间差
	 * 
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 两个日期相差的时间
	 */
	public static long GetDateMarginString(String beginDate, String endDate) {
		//String 转date
		Date date1 = new Date();
		Date date2 = new Date();
		if (beginDate != null && !"".equals(beginDate.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				// Date转化String
				date1 = format.parse(beginDate);
				date2 = format.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		long margin = 0;
		margin = date2.getTime() - date1.getTime();
		
		return margin;
	}
	
	/**
	 * 根据时间差 修改时间
	 */
	public static String getNextDay3(String nowdate, long delay) {
		String mdate = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//获取格式化的日期
			ParsePosition pos = new ParsePosition(0);
			Date d = format.parse(nowdate, pos);
			//增加时间差值
			long myTime = d.getTime() + delay;
			d.setTime(myTime);
			mdate = format.format(d);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			//出异常 保持时间不变
			mdate= nowdate;
		}
		return mdate;
	}
	/**
	 * 根据时间差修改时间
	 * yyyy-MM-dd HH:mm:ss
	 * @param nowdate 
	 * @param hour 小时
	 * @param min 分钟
	 * @return
	 */
	public static String getNextDay4(String nowdate, String hour,String min) {
		int h=Integer.parseInt(hour);
		int m=Integer.parseInt(min);
		long delay=h*60*60*1000+m*60*1000;
		String mdate = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			//获取格式化的日期
			ParsePosition pos = new ParsePosition(0);
			Date d = format.parse(nowdate, pos);
			//增加时间差值
			long myTime = d.getTime() + delay;
			d.setTime(myTime);
			mdate = format.format(d);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			//出异常 保持时间不变
			mdate= nowdate;
		}
		return mdate;
	}
	
	/**
	 * 求两个日期时间差
	 * 
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 两个日期相差的时间
	 */
	public static long GetDateMarginString2(String beginDate, String endDate) {
		//String 转date
		Date date1 = new Date();
		Date date2 = new Date();
		if (beginDate != null && !"".equals(beginDate.trim())) {
			// 定义转化格式
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				// Date转化String
				date1 = format.parse(beginDate);
				date2 = format.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		long margin = 0;
		margin = date2.getTime() - date1.getTime();
		
		return margin;
	}
	/**
	 * 根据传入时间返回延迟后的时间
	 * @param start
	 * @param delay
	 * @return
	 */
	public static String GetDelayDate(String start,int delay)
	{
		if("".equals(start))
		{
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(string2date(start));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+delay);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		String mm = "";
		String dd = "";
		if(month<10)
		{
			mm = "0"+month;
		}else{
			mm = String.valueOf(month);
		}
		if(day <10)
		{
			dd = "0"+day;
		}else{
			dd = String.valueOf(day);
		}
		return calendar.get(Calendar.YEAR)+"-"+mm+"-"+dd;
	}
	/**
	 * 日期函数 string ->date
	 * @param time
	 * @return
	 */
		public static Date string2date(String time)
		{
			SimpleDateFormat sdf;
			//去掉分钟和小时前面的〇
			String[] temps = time.split(" ");
			if(temps.length>1)
			{
				//小时
				String hour = temps[1].split(":")[0];
				String min = temps[1].split(":")[1];
				
				if(hour.charAt(0) == '0')
				{
					hour = hour.charAt(1)+"";
				}
				if(min.charAt(0)=='0')
				{
					min = min.charAt(1)+"";
				}
				time = temps[0]+" "+hour+":"+min;
				sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm");
			}else{
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			}
			
			Date da = new Date();
			try {
				da = sdf.parse(time);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return da;
		}
		/**
		 * 判断是日期是否是大于今天
		 * @param date
		 * @return
		 */
		public static boolean beyondTody(Date date){
			boolean f = false;
			Calendar calendar = Calendar.getInstance();
			int tyear = calendar.get(Calendar.YEAR);
			int tmonth = calendar.get(Calendar.MONTH)+1;
			int tday = calendar.get(Calendar.DATE);
			
			Date now = string2date(tyear+"-"+tmonth+"-"+tday+" 23:59");
			if(date.getTime() - now.getTime()>0)
			{
				f = true;
			}
			return f;
		}
		/**
		 * 天数差值
		 * @param start
		 * @param end
		 * @return
		 */
		public  static int TianCha(Date start ,Date end)
		{
			long d = end.getTime() - start.getTime();
			d = d/(24*60*60*1000);
			return (int)d ;
		}
		/**
		 * 向上取整
		 * @param start
		 * @param end
		 * @return
		 */
		public static int XiaoShiCha(Date start,Date end)
		{
			long d = end.getTime() - start.getTime();
			double f  = d/(60*60*1000.0);
			return (int)Math.ceil(f);
		}
		
		/**
		 * 时间
		 * @param start
		 * @param end
		 * @return
		 */
		public static String getTime(Date start,Date end){
			long d = end.getTime() - start.getTime();
			long day = d / (24 * 60 * 60 * 1000); // 天
			long hour = (d / (60 * 60 * 1000) - day * 24);
			long min = ((d / (60 * 1000)) - day * 24 * 60 - hour * 60);
			StringBuffer sbBuffer = new StringBuffer();
			if (day > 0) {
				sbBuffer.append(day).append("天");
			}
			if (min > 0) {
				if (hour > 0) {
					sbBuffer.append(hour).append("小时").append(min).append("分");
				}else {
					sbBuffer.append(min).append("分钟");
				}
			} else {
					sbBuffer.append(hour).append("小时");
			}
			return sbBuffer.toString();
		}
		
		/**
		* 将长时间格式字符串转换为时间 yyyy-MM-dd
		*
		*/
		public static String getStringDate(Long date) 
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(date);
			
			return dateString;
		}
		/**
		* 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
		*
		*/
		public static String getStringTime(Long date) 
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = formatter.format(date);
			
			return dateString;
		}
	/*--------------------------同步服务器时间end---------------------------------------*/
}