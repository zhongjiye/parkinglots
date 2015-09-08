package com.sunrise.epark.datedialog;
import java.util.Calendar;

import com.sunrise.epark.R;
import com.sunrise.epark.datedialog.DateTimePicker.OnDateTimeChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

public class DateTimePickerDialog extends AlertDialog implements OnClickListener
{
    private DateTimePicker mDateTimePicker;
    private Calendar mDate = Calendar.getInstance();
    private OnDateTimeSetListener mOnDateTimeSetListener;
    private TextView titlev;
	@SuppressWarnings("deprecation")
	public DateTimePickerDialog(Context context, long date) 
	{
		super(context);
		mDateTimePicker = new DateTimePicker(context);
	    setView(mDateTimePicker);
	    mDateTimePicker.setOnDateTimeChangedListener(new OnDateTimeChangedListener()
		{
			@Override
			public void onDateTimeChanged(DateTimePicker view, int year, int month, int day, int hour, int minute)
			{
				mDate.set(Calendar.YEAR, year);
                mDate.set(Calendar.MONTH, month);
                mDate.set(Calendar.DAY_OF_MONTH, day);
                mDate.set(Calendar.HOUR_OF_DAY, hour);
                mDate.set(Calendar.MINUTE, minute);
                mDate.set(Calendar.SECOND, 0);
                updateTitle(mDate.getTimeInMillis());
			}
		});
	    View v = View.inflate(context, R.layout.dialogtitle, null);
	    
		   titlev = (TextView)v.findViewById(R.id.title);
		   setCustomTitle(v);
	    setButton("设置", this);
        setButton2("取消", (OnClickListener)null);
	    mDate.setTimeInMillis(date);
	    updateTitle(mDate.getTimeInMillis()); 
	}
	
	public interface OnDateTimeSetListener 
    {
        void OnDateTimeSet(AlertDialog dialog, long date);
    }
	
	private void updateTitle(long date) 
    {
        int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY| DateUtils.FORMAT_SHOW_TIME;
        String s = DateUtils.formatDateTime(this.getContext(), date, flag);
        
        
        
        titlev.setText(s);
    }
	
	public void setOnDateTimeSetListener(OnDateTimeSetListener callBack)
    {
        mOnDateTimeSetListener = callBack;
    }
	 
	public void onClick(DialogInterface arg0, int arg1)
    {
        if (mOnDateTimeSetListener != null) 
        {
        	int min = mDate.get(Calendar.MINUTE);
        	min = min/15;
        	if(min == 0)
        	{
        		min = 0;
        	}else if(min==1){
        		min = 15;
        	}else if(min == 2){
        		min =30;
        	}else if(min ==3)
        	{
        		min = 45;
        	}
        	mDate.set(Calendar.MINUTE, min);
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
        }
    }
}
