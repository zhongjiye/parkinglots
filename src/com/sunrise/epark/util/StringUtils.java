package com.sunrise.epark.util;

/**
 * 字符串工作包
 * @author jasonwang
 * 
 * 
 * */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StringUtils {
	private final static Pattern input=Pattern.compile("^[A-Za-z0-9]{5,15}$");

	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static Pattern phone = Pattern
			.compile("^(1)\\d{10}$");
	private final static Pattern car = Pattern
			.compile("[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
						
	// private String MobileMatchStr =
	// "^(?<国家代码>(\+86)|(\(\+86\)))?(?<手机号>((13[0-9]{1})|(15[0-9]{1})|(18[0,2,5-9]{1}))+\d{8})$";
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 返回当前系统时间
	 */
	public static String getDataTime(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}

	/**
	 * 返回当前系统时间
	 */
	public static String getDataTime() {
		return getDataTime("HH:mm");
	}

	/**
	 * 毫秒值转换为mm:ss
	 * 
	 * @author kymjs
	 * @param ms
	 */
	public static String timeFormat(int ms) {
		StringBuilder time = new StringBuilder();
		time.delete(0, time.length());
		ms /= 1000;
		int s = ms % 60;
		int min = ms / 60;
		if (min < 10) {
			time.append(0);
		}
		time.append(min).append(":");
		if (s < 10) {
			time.append(0);
		}
		time.append(s);
		return time.toString();
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater2.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		String dataTime = getDataTime("yyyy-MM-dd");
		Date date = toDate(dataTime);
		Log.i("tag", "^^&^&^&^");
		if (time != null) {
			Log.i("tag", "^^&^&%%%%%%%^&^");
			String nowDate = dateFormater2.get().format(date);
			String timeDate = dateFormater2.get().format(time);

			Log.i("tag", nowDate + timeDate + "^^&^&^&^");
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断用户名是否有误
	 * 
	 */
	public static boolean isuser(String userString) {
		if (userString == null || userString.trim().length() == 0)
			return false;
		return input.matcher(userString.toLowerCase()).matches();
	}
	/**
	 * 判断密码是否有误
	 * 
	 */
	public static boolean ispwd(String password) {
		if (password == null || password.trim().length() == 0){
			return false;
		}else{
			if(password.matches("\\w+")){
	            Pattern p1= Pattern.compile("[a-z]+");
	            Pattern p2= Pattern.compile("[A-Z]+");
	            Pattern p3= Pattern.compile("[0-9]+");
	            Matcher m=p1.matcher(password);
	            if(!m.find())
	                return false;
	            else{
	                m.reset().usePattern(p2);
	                if(!m.find())
	                    return false;
	                else{
	                    m.reset().usePattern(p3);
	                    if(!m.find())
	                        return false;
	                    else{
	                        return true;
	                    }
	                }
	            }
	        }else{
	            return false;
	        }
		}
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 判断是不是一个合法的车牌号
	 */
	public static boolean isCar(String carStr) {
    	if (carStr == null || carStr.trim().length() == 0){
    		return false;
    	}
    	return car.matcher(carStr).matches();
    }

	/**
	 * 判断是不是一个合法的手机号码
	 */
	public static boolean isPhone(String phoneNum) {
		if (phoneNum == null || phoneNum.trim().length() == 0)
			return false;
		return phone.matcher(phoneNum).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * String转long
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * String转double
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static double toDouble(String obj) {
		try {
			return Double.parseDouble(obj);
		} catch (Exception e) {
		}
		return 0D;
	}

	/**
	 * 字符串转布尔
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 判断一个字符串是不是数字
	 */
	public static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取AppKey
	 */
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	/**
	 * 获取手机IMEI码
	 */
	public static String getPhoneIMEI(Activity aty) {
		TelephonyManager tm = (TelephonyManager) aty
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * MD5加密
	 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/**
	 * KJ加密
	 */
	public static String KJencrypt(String str) {
		char[] cstr = str.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char c : cstr) {
			hex.append((char) (c + 5));
		}
		return hex.toString();
	}

	/**
	 * KJ解密
	 */
	public static String KJdecipher(String str) {
		char[] cstr = str.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char c : cstr) {
			hex.append((char) (c - 5));
		}
		return hex.toString();
	}

	/**
	 *判断给定字符串是否为null或者为""
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		boolean result = false;
		if (null == str || "".equals(str.trim())) {
			result = true;
		}
		return result;
	}

	/**
	 * 濡傛灉i灏忎簬10锛屾坊锟?鍚庣敓鎴恠tring
	 * 
	 * @param i
	 * @return
	 */
	public static String addZreoIfLessThanTen(int i) {

		String string = "";
		int ballNum = i + 1;
		if (ballNum < 10) {
			string = "0" + ballNum;
		} else {
			string = ballNum + "";
		}
		return string;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotNull(String string) {
		if (null != string && !"".equals(string.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 鍘绘帀锟?锟斤拷瀛楃涓蹭腑鐨勬墍鏈夌殑鍗曚釜绌烘牸" "
	 * 
	 * @param string
	 */
	public static String replaceSpaceCharacter(String string) {
		if (null == string || "".equals(string)) {
			return "";
		}
		return string.replace(" ", "");
	}

	/**
	 * 
	 * 
	 * @param string
	 * @return
	 */
	public static String getPhoneString(String string) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(string);
		StringBuffer replace = buffer.replace(3, 7, "****");

		return replace.toString();

	}
	/** 
     * 把字节数组保存为一个文件 
     *  
     * @param b 
     * @param outputFile 
     * @return 
     */  
    public static File getFileFromBytes(byte[] b, String outputFile) {  
        File ret = null;  
        BufferedOutputStream stream = null;  
        try {  
            ret = new File(outputFile);  
            FileOutputStream fstream = new FileOutputStream(ret);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            // log.error("helper:get file from byte process error!");  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e) {  
                    // log.error("helper:get file from byte process error!");  
                    e.printStackTrace();  
                }  
            }  
        }  
        return ret;  
    }  
    
    public static String getMap(Map<String, String>  map){
    	StringBuffer result = new StringBuffer();
    	for(String obj:map.keySet()){  
    	    result.append(obj+":"+map.get(obj)+"--");
    	}  
    	return result.toString();
    }
    


}
