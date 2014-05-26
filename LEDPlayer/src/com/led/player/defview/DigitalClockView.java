package com.led.player.defview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.led.player.aidance.Lunar;
import com.led.player.window.DigitalClockWindow;
import com.led.player.window.TextViewAttr;
/**
 * 数字时钟
 * @author 1231
 *
 */
public class DigitalClockView extends TextView{
	private String str;
	private Handler mHandler;
	private Timer clocktimer;
	private String year;
	private String month;
	private String day;
	private String chdate = null;
	private int currentdate = 0;
	private Calendar calendar;
	private DigitalClockWindow mDigitalClockWindow;
	private TextViewAttr mtextviewattr;
	private String Format;

	public DigitalClockView(Context context,DigitalClockWindow digitalclockwindow) {
		this(context,null,digitalclockwindow);
		// TODO Auto-generated constructor stub
	}
	public DigitalClockView(Context context, AttributeSet attrs,DigitalClockWindow digitalclockwindow) {
	    this(context, attrs, 0,digitalclockwindow);
	   
    }
	

    public DigitalClockView(Context context, AttributeSet attrs,int defStyle,DigitalClockWindow digitalclockwindow) {
        super(context, attrs, defStyle);
        Log.i("DigitalClock", "DigitalClock()");
        mDigitalClockWindow = digitalclockwindow;
        mtextviewattr = mDigitalClockWindow.getTextViewAttr();
        Init();
        
        clocktimer = new Timer("digitalClock_timer");
        clocktimer.schedule(new clocktast(), 1000, 1000);
        mHandler = new Handler() {
        	@Override 
        	public void handleMessage(Message msg) {  
        		if(msg.what == 1) { 
        			Log.i("DigitalClock", "handleMessage ");
        			String text = null;
        			int fixedlen = 0;
        			if(mtextviewattr.getContent()!=null){
        				text = mtextviewattr.getContent() + str;
            			fixedlen = mtextviewattr.getContent().length();
            			SpannableStringBuilder msp = new SpannableStringBuilder(text);
            			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getFixedWordClr()), 0, fixedlen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getWordClr()), fixedlen, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            			setText(msp); 
        			}else{
            			text = str;
            			SpannableStringBuilder msp = new SpannableStringBuilder(text);
            			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getWordClr()), 0, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            			setText(msp); 
        			}
        			
        			invalidate();
        			}   
        		}  
        	};  

    }
    
    public void StopClockTimer(){

		clocktimer.cancel();
		
	}
    
    public void Init(){
    	if(mDigitalClockWindow.getShowDay()){
    		if(mDigitalClockWindow.getTwoBitYear()){
    			if(mtextviewattr.getLanguage()){
    				if(mDigitalClockWindow.getDateTimeStyle()){
        				Format = "yy年MM月dd日";
    				}else{
    					Format = "MM月dd日yy年";
    				}
    			}else{
    				if(mDigitalClockWindow.getDateTimeStyle()){
    					Format = "yy-MM-dd";
    				}else{
    					Format = "MM-dd-yy";
    				}
    				
    			}
        		
        	}else{
        		
        		if(mtextviewattr.getLanguage()){
        			if(mDigitalClockWindow.getDateTimeStyle()){
        				Format = "yyyy年MM月dd日";
    				}else{
    					Format = "MM月dd日yyyy年";
    				}
    			}else{
    				if(mDigitalClockWindow.getDateTimeStyle()){
    					Format = "yyyy-MM-dd";
    				}else{
    					Format = "MM-dd-yyyy";
    				}
    				
    			}
        	}
    	}else{
    		Format = "";
    	}
    	if(mDigitalClockWindow.getShowWeek()){
			Format = Format+" EEEE";
		}
		if(mDigitalClockWindow.getShowSeconds()){
			if(mDigitalClockWindow.getTwelveHour()){
				Format = Format + " hh:mm:ss";
			}else{
				Format = Format + " HH:mm:ss";
			}
		}
		
    	
    }
    
    private class clocktast extends TimerTask{

//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			
//			SimpleDateFormat formatter = new SimpleDateFormat(Format);       
//			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��    
//			str = formatter.format(curDate);
//			
//			calendar = Calendar.getInstance();
//			
//			if(mDigitalClockWindow.getShowCHADays()){
//				
//				String weektimestr;
//				int index = str.indexOf(" ");
//				if(index!=-1){
//					weektimestr = str.substring(index, str.length());
//				}else{
//					weektimestr = "";
//				}
//				
//				Lunar lunar = new Lunar();
//				year = String.valueOf(calendar.get(Calendar.YEAR));
//				month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
//				day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
//				
//				if(currentdate != calendar.get(Calendar.DAY_OF_MONTH)){
//					chdate = lunar.getLunar(year,month,day);
//					
//					currentdate = calendar.get(Calendar.DAY_OF_MONTH);
//				}
//				str = chdate + weektimestr;
//				
//			}else{
//				String week = null ;
//				if(!mtextviewattr.getLanguage()){
//					switch(calendar.get(Calendar.DAY_OF_WEEK)){
//						case 1:
//							week ="Sun";
//							break;
//						case 2:
//							week ="Mon";
//							break;
//						case 3:
//							week ="Tue";
//							break;
//						case 4:
//							week ="Wed";
//							break;
//						case 5:
//							week ="Thu";
//							break;
//						case 6:
//							week ="Fri";
//							break;
//						case 7:
//							week ="Sat";
//							break;
//							default:
//								break;
//					}
//					int i = str.indexOf("星");
//					if(i!=-1){
//						String weekstr= str.substring(i, i+3);
//						str = str.replaceAll(weekstr, week);
//					}
//					
//				}
//			}
//			
//			if(mDigitalClockWindow.getShowAmPm()){
//				if(calendar.get(Calendar.AM_PM)==0){
//					str = str + " AM";
//				}else if(calendar.get(Calendar.AM_PM)==1){
//					str = str + " PM";
//				}
//				
//			}
//			Log.i("DigitalClock", "str "+str);
//			Message msg = mHandler.obtainMessage();  
//			msg.what = 1;  
//			msg.sendToTarget();  
//			
//
//		}
    	


		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			String mclockstr = "";
			String mchaclockstr = "";
			SimpleDateFormat formatter = new SimpleDateFormat(Format);       
			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��    
			mclockstr = formatter.format(curDate);
			
			calendar = Calendar.getInstance();
			
			if(mDigitalClockWindow.getShowCHADays()){
				
				String weektimestr;
				int index = mclockstr.indexOf(" ");
				if(index!=-1){
					weektimestr = mclockstr.substring(index, mclockstr.length());
				}else{
					weektimestr = "";
				}
				
				Lunar lunar = new Lunar();
				year = String.valueOf(calendar.get(Calendar.YEAR));
				month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
				day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
				
				if(currentdate != calendar.get(Calendar.DAY_OF_MONTH)){
					chdate = lunar.getLunar(year,month,day);
					
					currentdate = calendar.get(Calendar.DAY_OF_MONTH);
				}
				mchaclockstr = chdate + weektimestr;
				
				if(mDigitalClockWindow.getShowAmPm()){
					if(mDigitalClockWindow.getShowCHADays()){
						if(calendar.get(Calendar.AM_PM)==0){
							mchaclockstr = mchaclockstr + " 上午";
						}else if(calendar.get(Calendar.AM_PM)==1){
							mchaclockstr = mchaclockstr + " 下午";
						}
					}
					
				}
				
			}

			String week = null ;
			if(!mtextviewattr.getLanguage()){
				switch(calendar.get(Calendar.DAY_OF_WEEK)){
					case 1:
						week ="Sun";
						break;
					case 2:
						week ="Mon";
						break;
					case 3:
						week ="Tue";
						break;
					case 4:
						week ="Wed";
						break;
					case 5:
						week ="Thu";
						break;
					case 6:
						week ="Fri";
						break;
					case 7:
						week ="Sat";
						break;
						default:
							break;
				}
				int i = mclockstr.indexOf("星");
				if(i!=-1){
					String weekstr= mclockstr.substring(i, i+3);
					mclockstr = mclockstr.replaceAll(weekstr, week);
				}
				
			}
			
			if(mDigitalClockWindow.getShowAmPm()){
				if(mtextviewattr.getLanguage()){
					if(calendar.get(Calendar.AM_PM)==0){
						mclockstr = mclockstr + " 上午";
					}else if(calendar.get(Calendar.AM_PM)==1){
						mclockstr = mclockstr + " 下午";
					}
				}else{
					if(calendar.get(Calendar.AM_PM)==0){
						mclockstr = mclockstr + " AM";
					}else if(calendar.get(Calendar.AM_PM)==1){
						mclockstr = mclockstr + " PM";
					}
				}
				
			}
			
			if(mDigitalClockWindow.getShowDay()&&mDigitalClockWindow.getShowCHADays()){
				if(mDigitalClockWindow.getSingleLineShow()){
					str = mclockstr +" "+ mchaclockstr;
				}else{
					str = mclockstr + "\n" + mchaclockstr;
				}
				
			}else if(mDigitalClockWindow.getShowDay()){
				str = mclockstr;
			}else if(mDigitalClockWindow.getShowCHADays()){
				str = mchaclockstr;
			}else{
				str = mclockstr;
			}
			
			Log.i("DigitalClock", "str "+str);
			Message msg = mHandler.obtainMessage();  
			msg.what = 1;  
			msg.sendToTarget();  
			

		}
    	
    	
    }
    


}
