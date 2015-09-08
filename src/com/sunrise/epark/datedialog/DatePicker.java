package com.sunrise.epark.datedialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.sunrise.epark.R;

public class DatePicker extends FrameLayout
{
	private final NumberPicker mYearSpinner;
	private final NumberPicker mMonthSpinner;
	private final NumberPicker mDaySpinner;
	private Calendar mDate;
    private int year,month,day; 
    private String[] mDateDisplayValues = new String[7];
    private OnDateChangedListener mOnDateTimeChangedListener;
    
    private boolean first = true;
    public DatePicker(Context context)
	{
    	super(context);
    	 mDate = Calendar.getInstance();
    	 
    	 year = mDate.get(Calendar.YEAR);
         month=mDate.get(Calendar.MONTH);
         
         
         day=mDate.get(Calendar.DATE);
    	 
    	 inflate(context, R.layout.datedialog, this);
    	 TextView t = (TextView) this.findViewById(R.id.diandian);
    	 t.setVisibility(View.INVISIBLE);
    	 mYearSpinner=(NumberPicker)this.findViewById(R.id.np_date);
    	 mYearSpinner.setMinValue(0);
    	 mYearSpinner.setMaxValue(6);
         updateDateControl();
         mYearSpinner.setOnValueChangedListener(mOnDateChangedListener);
    	 
         mMonthSpinner=(NumberPicker)this.findViewById(R.id.np_hour);
         mMonthSpinner.setMaxValue(12);
         mMonthSpinner.setMinValue(1);
         mMonthSpinner.setValue(month+1);
         mMonthSpinner.setOnValueChangedListener(mOnHourChangedListener);
    	 
         mDaySpinner=(NumberPicker)this.findViewById(R.id.np_minute);
         mDaySpinner.setMaxValue(31);
         mDaySpinner.setMinValue(1);
         mDaySpinner.setValue(day);
    	 
    	 
    	 mDaySpinner.setOnValueChangedListener(mOnMinuteChangedListener);
	}
    
    private NumberPicker.OnValueChangeListener mOnDateChangedListener=new OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mDate.add(Calendar.YEAR, newVal - oldVal);
			
			year = mDate.get(Calendar.YEAR);
			System.out.println("year---------"+year);
			onDateTimeChanged();
		}
	};
    
    private NumberPicker.OnValueChangeListener mOnHourChangedListener=new OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			first = false;
			// 添加大小月月份并将其转换为list,方便之后的判断
			String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
			String[] months_little = { "4", "6", "9", "11" };
			month=mMonthSpinner.getValue();
			System.out.println("month-----"+month);
			List<String> list_big = Arrays.asList(months_big);
			List<String> list_little = Arrays.asList(months_little);
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month))) {
				mDaySpinner.setMaxValue(31);
			} else if (list_little.contains(String.valueOf(month))) {
				mDaySpinner.setMaxValue(30);
			} else {
				System.out.println("============="+year);
				// 闰年
				if (isLeapYear(year)) {
					mDaySpinner.setMaxValue(29);
				} else {
					mDaySpinner.setMaxValue(28);
				}
			}
			
			onDateTimeChanged();
		}
	};
	
	  private NumberPicker.OnValueChangeListener mOnMinuteChangedListener=new OnValueChangeListener()
		{
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal)
			{
				if(first)
				{
					month++;
					first = false;
				}
				day=mDaySpinner.getValue();
				mDate.set(Calendar.DATE, day);
				onDateTimeChanged();
			}
		};
	
	private void updateDateControl() 
    {
	 	Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mDate.getTimeInMillis());
        cal.add(Calendar.YEAR,-1);
        mYearSpinner.setDisplayedValues(null);
        for (int i = 0; i < 7; ++i) 
        {
            cal.add(Calendar.YEAR, 1);
            mDateDisplayValues[i] = (String) DateFormat.format("yyyy", cal);
        }
        mYearSpinner.setDisplayedValues(mDateDisplayValues);
        mYearSpinner.setValue(0);
        mYearSpinner.invalidate();
    }
	
	  public interface OnDateChangedListener 
	  {
	        void onDateTimeChanged(DatePicker view, int year, int month, int day);
	  }
	
	  public void setOnDateChangedListener(OnDateChangedListener callback) 
	  {
	        mOnDateTimeChangedListener = callback;
	   }
	  private void onDateTimeChanged() 
	  
	  {
	        if (mOnDateTimeChangedListener != null)
	        {
	            mOnDateTimeChangedListener.onDateTimeChanged(this, mDate.get(Calendar.YEAR),
	            		month-1, day);
	        }
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
}
