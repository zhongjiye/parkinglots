package com.sunrise.epark.datedialog;

import java.util.Calendar;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.sunrise.epark.R;

public class DateTimePicker extends FrameLayout
{
	private final NumberPicker mDateSpinner;
	private final NumberPicker mHourSpinner;
	private final NumberPicker mMinuteSpinner;
	private Calendar mDate;
    private int mHour,mMinute; 
    private String[] mDateDisplayValues = new String[7];
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    private String[] m={"0","15","30","45"};
    
    public DateTimePicker(Context context)
	{
    	super(context);
    	 mDate = Calendar.getInstance();
    	 
         mHour=mDate.get(Calendar.HOUR_OF_DAY);
         mMinute=mDate.get(Calendar.MINUTE);
    	 
    	 inflate(context, R.layout.datedialog, this);
    	 
    	 mDateSpinner=(NumberPicker)this.findViewById(R.id.np_date);
    	 mDateSpinner.setMinValue(0);
         mDateSpinner.setMaxValue(6);
         updateDateControl();
    	 mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
    	 
    	 mHourSpinner=(NumberPicker)this.findViewById(R.id.np_hour);
    	 mHourSpinner.setMaxValue(23);
    	 mHourSpinner.setMinValue(0);
    	 mHourSpinner.setValue(mHour);
    	 mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);
    	 
    	 mMinuteSpinner=(NumberPicker)this.findViewById(R.id.np_minute);
    	 mMinuteSpinner.setMaxValue(m.length-1);
    	 mMinuteSpinner.setMinValue(0);
    	 mMinuteSpinner.setDisplayedValues(m);
    	 for (int i = 0; i < m.length; i++) {
    		 mMinuteSpinner.setValue(i);
		}
    	 
    	 mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);
	}
    
    private NumberPicker.OnValueChangeListener mOnDateChangedListener=new OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
			updateDateControl();
			onDateTimeChanged();
		}
	};
    
    private NumberPicker.OnValueChangeListener mOnHourChangedListener=new OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mHour=mHourSpinner.getValue();
			onDateTimeChanged();
		}
	};
	
	  private NumberPicker.OnValueChangeListener mOnMinuteChangedListener=new OnValueChangeListener()
		{
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal)
			{
				mMinute=mMinuteSpinner.getValue();
				switch (mMinute) {
				case 00:
					mMinute=00;
					break;
				case 01:
					mMinute=15;
					break;
				case 02:
					mMinute=30;
					break;
				case 03:
					mMinute=45;
					break;

				default:
					break;
				}
				onDateTimeChanged();
			}
		};
	
	private void updateDateControl() 
    {
	 	Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mDate.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, -7 / 2 - 1);
        mDateSpinner.setDisplayedValues(null);
        for (int i = 0; i < 7; ++i) 
        {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            mDateDisplayValues[i] = (String) DateFormat.format("MM.dd EEEE", cal);
        }
        mDateSpinner.setDisplayedValues(mDateDisplayValues);
        mDateSpinner.setValue(7 / 2);
        mDateSpinner.invalidate();
    }
	
	  public interface OnDateTimeChangedListener 
	  {
	        void onDateTimeChanged(DateTimePicker view, int year, int month, int day,int hour,int minute);
	  }
	
	  public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) 
	  {
	        mOnDateTimeChangedListener = callback;
	   }
	  private void onDateTimeChanged() 
	  
	  {
	        if (mOnDateTimeChangedListener != null)
	        {
	            mOnDateTimeChangedListener.onDateTimeChanged(this, mDate.get(Calendar.YEAR),
	            		mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH),mHour, mMinute);
	        }
	    }
}
