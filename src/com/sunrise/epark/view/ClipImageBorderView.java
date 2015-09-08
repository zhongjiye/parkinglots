package com.sunrise.epark.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ClipImageBorderView extends View{
	/**
	 * ˮƽ������View�ı߾�
	 */
	private int mHorizontalPadding;
	/**
	 * �߿�Ŀ�� ��λdp
	 */
	private int mBorderWidth = 2;
	
	private Paint mPaint;//Բ��������Բ��paint
	private Paint mPaintCirle;//Բ��ʵ��Բ��paint
	private Paint mPaintRect;//��Ӱ��paint
	
	private Canvas mCanvas;//��Ӱ�㻭��
	private RectF mRect;//������Ļ
	
	private Bitmap mBgBitmap;//���ڻ�����Ӱ���bitmap

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());

		//������Ӱ��
		mPaintRect = new Paint();
		mPaintRect.setARGB(185, 0,0, 0);
		
		// ����ʵ��Բ
		mPaintCirle = new Paint();
		mPaintCirle.setStrokeWidth((getWidth()-2*mHorizontalPadding)/2);  //ʵ��Բ�뾶
        mPaintCirle.setARGB(255, 0,0, 0);
        mPaintCirle.setXfermode(new PorterDuffXfermode(Mode.XOR));//XOR设置实心圆画笔原色
        
		// ����Բ��
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true); //�����
		mPaint.setColor(Color.WHITE);//�߿���ɫ��ɫ
		mPaint.setStrokeWidth(mBorderWidth);//���ʿ��
		
	}
	//当阴影层和其上部的的实心圆重叠时，则实心圆部分即被抠出，剩下一个中间空洞的阴影层了，然后再将该画布的bitmap画在自定义View的画布上合成，最后画出圆形裁剪区白色边框
	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas)   {  
        super.onDraw(canvas);  
        if(mBgBitmap==null){  
            mBgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);    
            mCanvas = new  Canvas(mBgBitmap);  
            mRect = new RectF(0, 0, getWidth(), getHeight());  
        }  
        //绘制阴影层  
        mCanvas.drawRect(mRect, mPaintRect);  
         //绘制实心圆 ，绘制完后，在mCanvas画布中，mPaintRect和mPaintCirle相交部分即被掏空  
        mCanvas.drawCircle( getWidth()/2, getHeight()/2, getWidth()/2-mHorizontalPadding, mPaintCirle);  
         //将阴影层画进本View的画布中  
        canvas.drawBitmap(mBgBitmap, null, mRect, new Paint());  
        //绘制圆环  
        canvas.drawCircle( getWidth()/2, getHeight()/2, getWidth()/2-mHorizontalPadding, mPaint);        
    } 

	public void setHorizontalPadding(int mHorizontalPadding){
		this.mHorizontalPadding = mHorizontalPadding;	
	}

}
