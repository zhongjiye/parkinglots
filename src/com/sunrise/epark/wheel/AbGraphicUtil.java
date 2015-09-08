/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunrise.epark.wheel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.text.Layout;
import android.text.TextPaint;
import android.util.DisplayMetrics;


// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn
 * 名称：AbGraphic.java 
 * 描述：图形处理类.
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-01-17 下午11:52:13
 */
public final class AbGraphicUtil {
	/**  UI设计的基准宽度. */
	public static int UI_WIDTH = 720;
	
	/**  UI设计的基准高度. */
	public static int UI_HEIGHT = 1080;
     /**
      * 描述：获取字符的所在位置（按像素获取最大能容纳的）.
      *
      * @param str 指定的字符串
      * @param maxPix 要取到的位置（像素）
      * @param paint the paint
      * @return 字符的所在位置
      */
     public static int subStringLength(String str,int maxPix,TextPaint paint) {
    	 if(isEmpty(str)){
    		 return 0;
    	 }
    	 int currentIndex = 0;
         for (int i = 0; i < str.length(); i++) {
             //获取一个字符 
             String temp = str.substring(0, i + 1);
             float valueLength  = paint.measureText(temp);
             if(valueLength > maxPix){
            	 currentIndex = i-1;
            	 break;
             }else if(valueLength == maxPix){
            	 currentIndex = i;
            	 break;
             }
         }
         //短于最大像素返回最后一个字符位置
         if(currentIndex==0){
        	 currentIndex = str.length()-1 ;
         }
         return currentIndex;
     }
     
     /**
      * 描述：获取文字的像素宽.
      *
      * @param str the str
      * @param paint the paint
      * @return the string width
      */
     public static float getStringWidth(String str,TextPaint paint) {
    	 float strWidth  = paint.measureText(str);
         return strWidth;
     }
     
     /**
      * 获得文字的宽度
      * @param str the str
      * @param paint the paint
      * @return the string width
      */
     public static float getDesiredWidth(String str,TextPaint paint) {
    	 float strWidth = Layout.getDesiredWidth(str, paint);
         return strWidth;
     }
     
     /**
      * 获取文字的高度
      * @param paint the textPaint
      * @return the string height
      */
     public static float getDesiredHeight(TextPaint paint) {
    	 FontMetrics  fm  = paint.getFontMetrics();
         return (float)Math.ceil(fm.descent - fm.ascent);
     }

     /**
      * 解析成行.
      * @param text the text
      * @param maxWPix the max w pix
      * @param paint the paint
      * @return the draw row count
      */
     public static List<String> getDrawRowStr(String text,int maxWPix,TextPaint paint) {
     	String [] texts = null;
     	if(text.indexOf("\n")!=-1){
     		 texts = text.split("\n");
     	}else{
     		 texts  = new String [1];
     		 texts[0] = text;
     	}
     	//共多少行
     	List<String> mStrList  = new ArrayList<String>();
     	
     	for(int i=0;i<texts.length;i++){
   		    String textLine = texts[i];
   		    //计算这个文本显示为几行
            while(true){
	           	 //可容纳的最后一个字的位置
	           	 int endIndex = subStringLength(textLine,maxWPix,paint);
	           	 if(endIndex<=0){
	           		mStrList.add(textLine);
	           	 }else{
	           		if(endIndex == textLine.length()-1){
	           			mStrList.add(textLine);
	           		}else{
	           			mStrList.add(textLine.substring(0,endIndex+1));
	           		}
	           		 
	           	 }
	           	 //获取剩下的
	           	 if(textLine.length()>endIndex+1){
	           		 //还有剩下的
	           		 textLine = textLine.substring(endIndex+1);
	           	 }else{
	           		 break;
	           	 }
            }
   	     }
        
         return mStrList;
     }
     
     /**
      * 
      * 描述：获取这段文本多少行.
      * @param text
      * @param textSize
      * @param maxWPix
      * @return
      */
     public static int getDrawRowCount(String text,int maxWPix,TextPaint paint) {
    	
    	String [] texts = null;
     	if(text.indexOf("\n")!=-1){
     		 texts = text.split("\n");
     	}else{
     		 texts  = new String [1];
     		 texts[0] = text;
     	}
     	//共多少行
     	List<String> mStrList  = new ArrayList<String>();
     	
     	for(int i=0;i<texts.length;i++){
   		    String textLine = texts[i];
   		    //计算这个文本显示为几行
            while(true){
	           	 //可容纳的最后一个字的位置
	           	 int endIndex = subStringLength(textLine,maxWPix,paint);
	           	 if(endIndex<=0){
	           		mStrList.add(textLine);
	           	 }else{
	           		if(endIndex == textLine.length()-1){
	           			mStrList.add(textLine);
	           		}else{
	           			mStrList.add(textLine.substring(0,endIndex+1));
	           		}
	           		 
	           	 }
	           	 //获取剩下的
	           	 if(textLine.length()>endIndex+1){
	           		 //还有剩下的
	           		 textLine = textLine.substring(endIndex+1);
	           	 }else{
	           		 break;
	           	 }
            }
   	     }
        
         return mStrList.size();
     }
     
     /**
      * 描述：绘制文本，支持换行.
      *
      * @param canvas the canvas
      * @param text the text
      * @param maxWPix the max w pix
      * @param paint the paint
      * @param left the left
      * @param top the top
      * @return the int
      */
     public static int drawText(Canvas canvas,String text,int maxWPix,TextPaint paint,int left,int top) {
    	if(isEmpty(text)){
    		return 1;
    	}
    	//需要根据文字长度控制换行
        //测量文字的长度
    	List<String> mStrList  = getDrawRowStr(text,maxWPix,paint);
         
        int hSize = (int)getDesiredHeight(paint);
         
        for(int i=0;i<mStrList.size();i++){
        	 //计算坐标
        	 int x = left;
             int y = top+hSize/2+hSize*i;
    		 String textLine = mStrList.get(i);
             canvas.drawText(textLine,x,y, paint); 
             
        }
        return mStrList.size();
     }
     
     /**
      * 描述：获取字符串的长度.
      *
      * @param str 指定的字符串
      * @return  字符串的长度（中文字符计2个）
      */
      public static int strLength(String str) {
          int valueLength = 0;
          String chinese = "[\u0391-\uFFE5]";
          if(!isEmpty(str)){
 	         //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
 	         for (int i = 0; i < str.length(); i++) {
 	             //获取一个字符
 	             String temp = str.substring(i, i + 1);
 	             //判断是否为中文字符
 	             if (temp.matches(chinese)) {
 	                 //中文字符长度为2
 	                 valueLength += 2;
 	             } else {
 	                 //其他字符长度为1
 	                 valueLength += 1;
 	             }
 	         }
          }
          return valueLength;
      }

   	/**
  	  * 描述：标准化日期时间类型的数据，不足两位的补0.
  	  *
  	  * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
  	  * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
  	  */
   	public static String dateTimeFormat(String dateTime) {
  		StringBuilder sb = new StringBuilder();
  		try {
  			if(isEmpty(dateTime)){
  				return null;
  			}
  			String[] dateAndTime = dateTime.split(" ");
  			if(dateAndTime.length>0){
  				  for(String str : dateAndTime){
  					if(str.indexOf("-")!=-1){
  						String[] date =  str.split("-");
  						for(int i=0;i<date.length;i++){
  						  String str1 = date[i];
  						  sb.append(strFormat2(str1));
  						  if(i< date.length-1){
  							  sb.append("-");
  						  }
  						}
  					}else if(str.indexOf(":")!=-1){
  						sb.append(" ");
  						String[] date =  str.split(":");
  						for(int i=0;i<date.length;i++){
  						  String str1 = date[i];
  						  sb.append(strFormat2(str1));
  						  if(i< date.length-1){
  							  sb.append(":");
  						  }
  						}
  					}
  				}
  			}
  		} catch (Exception e) {
  			e.printStackTrace();
  			return null;
  		} 
  		return sb.toString();
  	}
    /**
     * 描述：判断一个字符串是否为null或空值.
     *
     * @param str 指定的字符串
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
 	/**
	  * 描述：不足2个字符的在前面补“0”.
	  *
	  * @param str 指定的字符串
	  * @return 至少2个字符的字符串
	  */
    public static String strFormat2(String str) {
		try {
			if(str.length()<=1){
				str = "0"+str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return str;
	}
    /**
	 * 描述：判断是否是闰年()
	 * <p>(year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
	 *
	 * @param year 年代（如2012）
	 * @return boolean 是否为闰年
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 描述：根据屏幕大小缩放.
	 *
	 * @param context the context
	 * @param pxValue the px value
	 * @return the int
	 */
	public static int scale(Context context, float value) {
		DisplayMetrics mDisplayMetrics = getDisplayMetrics(context);
		return scale(mDisplayMetrics.widthPixels,
				mDisplayMetrics.heightPixels, value);
	}
	 /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null){
            mResources = Resources.getSystem();
            
        }else{
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }
    /**
	 * 描述：根据屏幕大小缩放.
	 *
	 * @param displayWidth the display width
	 * @param displayHeight the display height
	 * @param pxValue the px value
	 * @return the int
	 */
	public static int scale(int displayWidth, int displayHeight, float pxValue) {
		if(pxValue == 0 ){
			return 0;
		}
		float scale = 1;
		try {
			float scaleWidth = (float) displayWidth / UI_WIDTH;
			float scaleHeight = (float) displayHeight / UI_HEIGHT;
			scale = Math.min(scaleWidth, scaleHeight);
		} catch (Exception e) {
		}
		return Math.round(pxValue * scale + 0.5f);
	}
	
}
