package com.sunrise.epark.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
/**
 * 图片帮助类
 * @author  钟继业 E-mail:zhongjy@camelotchina.com.cn
 * @version 创建时间：2015年7月10日 下午3:11:22
 * 类说明
 */
public class ImageUtil {
	private static final int STROKE_WIDTH = 4;
	
	/**
	 * 压缩资源文件中的图片
	 * @param res
	 * @param resId
	 * @param reqWidth  压缩后的图片宽度
	 * @param reqHeight 压缩后的图片高度
	 * @return
	 */
	public static Bitmap decodeBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	 *  压缩sd卡中的图片  
	 * @param pathName  图片路径
	 * @param reqWidth  压缩后的图片宽度
	 * @param reqHeight 压缩后的图片高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeBitmapFromSd(String pathName, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName,options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}
	
	/**
	 * 压缩从网络中获取的图片  
	 * @param data  是要进行decode的资源数据
	 * @param length decode的数据长度一般为data数组的长度
	 * @param reqWidth  压缩后的图片宽度
	 * @param reqHeight 压缩后的图片高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeBitmapFromNet(byte[] data, int length,int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, length, options);
	}
	/**
	 * 压缩从Bitmap 
	 * @param bitmap  是要进行压缩bitmap
	 * @param reqWidth  压缩后的图片宽度
	 * @param reqHeight 压缩后的图片高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeBitmapFromNet(Bitmap bitmap,int reqWidth, int reqHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		byte[] data=baos.toByteArray();
		int length=baos.toByteArray().length;
		BitmapFactory.decodeByteArray(data, 0, length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, length, options);
	}
	/**
	 * 压缩从Bitmap 
	 * @param bitmap  是要进行压缩bitmap
	 * @param maxLength  最大容量
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeBitmap(Bitmap image,int maxLength) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>maxLength) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


	
	/**
	 * 获取压缩比
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;	// 源图片的高度
		final int width = options.outWidth;     // 源图片的宽度
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);// 计算出实际高和目标高的比率
			final int widthRatio = Math.round((float) width / (float) reqWidth);// 计算出实际宽和目标宽的比率
			// 选择宽和高中最小的比率作为inSampleSize的值,这样可以保证最终图片的宽和高,一定都会大于等于目标的宽和高
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 从文件中获取Bitmap
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmap(String filename){
		Bitmap bitmap=null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(filename);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}
	/**
	 * 从asset中获取Bitmap
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String filename) {
		Bitmap bitmap = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(filename);
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 从Drawable中获取Bitmap
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmap(Context context, int resourceId) {
		Resources r = context.getResources();  
		//以数据流的方式读取资源  
		InputStream is = r.openRawResource(resourceId);  
		@SuppressWarnings("deprecation")
		BitmapDrawable  bmpDraw = new BitmapDrawable(is);  
		Bitmap bmp = bmpDraw.getBitmap(); 
		return bmp;
	}
	

	/**
	 * 从asset文件中获取圆形图片
	 * @param context
	 * @param filename
	 * @return
	 */
    public static Bitmap getCircuarImg(Context context,String filename){
    	Bitmap bitmap=getBitmap(context, filename);
    	return getCircuarImg(context, bitmap);
    }
    /**
     * 从资源文件中获去圆形图片
     * @param context
     * @param resourceId
     * @return
     */
    public static Bitmap getCircuarImg(Context context,int resourceId){
    	Bitmap bitmap=getBitmap(context, resourceId);
    	return getCircuarImg(context, bitmap);
    }
    /**
     * 从文件中获去圆形图片
     * @param context
     * @param resourceId
     * @return
     */
    public static Bitmap getCircuarImg(String filename,Context context){
    	Bitmap bitmap=getBitmap(filename);
    	return getCircuarImg(context, bitmap);
    }
    /**
     * 将图片转为圆形
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap getCircuarImg(Context context, Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			left = 0;
			bottom = width;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		@SuppressWarnings("unused")
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(4);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		paint.reset();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
		paint.setAntiAlias(true);
		canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2,
				paint);
		return output;
	}
    
    /**
     * 将图片转为base64编码字符串
     * @param bitmap 
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {  
		  
	    String result = null;  
	    ByteArrayOutputStream baos = null;  
	    try {  
	        if (bitmap != null) {  
	            baos = new ByteArrayOutputStream();  
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	  
	            baos.flush();  
	            baos.close();  
	  
	            byte[] bitmapBytes = baos.toByteArray();  
	            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try {  
	            if (baos != null) {  
	                baos.flush();  
	                baos.close();  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return result;  
	} 	
    /**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		if (checkSDCardAvailable()) {
			File photoFile = new File(path, photoName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
			} catch (Exception e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void savePhotoToSDCard(Bitmap photoBitmap, String path) {
		if (checkSDCardAvailable()) {
			File photoFile = new File(path);
			FileOutputStream fileOutputStream = null;
			try {
				// 1111
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
			} catch (Exception e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					// 1111
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check the SD card
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 转换图片bitmap 根据路径加载bitmap
	 * 
	 * @param path
	 *            路径
	 * @param w
	 *            宽
	 * @param h
	 *            长
	 * @return
	 */
	public static final Bitmap convertToBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置为ture只获取图片大小
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 返回为空
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			float scaleWidth = 0.f, scaleHeight = 0.f;
			if (width > w || height > h) {
				// 缩放
				scaleWidth = ((float) width) / w;
				scaleHeight = ((float) height) / h;
			}
			opts.inJustDecodeBounds = false;
			float scale = Math.max(scaleWidth, scaleHeight);
			opts.inSampleSize = (int) scale;
			WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
					BitmapFactory.decodeFile(path, opts));
			Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak
					.get().getWidth(), weak.get().getHeight(), null, true);
			if (bMapRotate != null) {
				return bMapRotate;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
