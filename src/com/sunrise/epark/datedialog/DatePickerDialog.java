package com.sunrise.epark.datedialog;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.TextView;

import com.sunrise.epark.R;
import com.sunrise.epark.datedialog.DatePicker.OnDateChangedListener;

public class DatePickerDialog extends AlertDialog implements OnClickListener
{
    private DatePicker mDateTimePicker;
    private Calendar mDate = Calendar.getInstance();
    private OnDateSetListener mOnDateTimeSetListener;
    private TextView titlev;
	@SuppressWarnings("deprecation")
	public DatePickerDialog(Context context, long date) 
	{
		super(context);
		mDateTimePicker = new DatePicker(context);
	    setView(mDateTimePicker);
	   
	    mDateTimePicker.setOnDateChangedListener(new OnDateChangedListener()
		{
			@Override
			public void onDateTimeChanged(DatePicker view, int year, int month, int day)
			{
				mDate.set(Calendar.YEAR, year);
                mDate.set(Calendar.MONTH, month);
                mDate.set(Calendar.DAY_OF_MONTH, day);
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
	
	public interface OnDateSetListener 
    {
        void OnDateTimeSet(AlertDialog dialog, long date);
    }
	
	private void updateTitle(long date) 
    {
       Calendar c = Calendar.getInstance();
       c.setTime(new Date(date));
       String title = c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONTH)+1)+"月"+c.get(Calendar.DATE)+"日";
       
       
       titlev.setText(title);
    }
	
	public void setOnDateSetListener(OnDateSetListener callBack)
    {
        mOnDateTimeSetListener = callBack;
    }
	 
	public void onClick(DialogInterface arg0, int arg1)
    {
        if (mOnDateTimeSetListener != null) 
        {
        	
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
        }
    }
}
