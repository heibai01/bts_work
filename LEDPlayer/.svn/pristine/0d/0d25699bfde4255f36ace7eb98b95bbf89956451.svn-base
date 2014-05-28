package com.led.player.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import com.led.player.dao.GPSLoopImageDao;
import com.led.player.dao.GPSLoopTextDao;
import com.led.player.dao.GPSLoopUniversalWindowDao;
import com.led.player.dao.GPSLoopVideoDao;
import com.led.player.meta.MetaData;
import com.led.player.meta.MySqliteOpenHelper;
import com.led.player.receiver.AutoConfigReciver;
import com.led.player.vo.PlanBrightNess;
import com.led.player.vo.PlanOnOff;
import com.led.player.window.BasicPage;
import com.led.player.window.ImageVo;
import com.led.player.window.TextVo;
import com.led.player.window.UniversalWindow;
import com.led.player.window.VideoVo;

/**
 * 用通用业务层
 * @author 1231
 *
 */
public class DisplayerBiz {
	private Context mContext;
	private AlarmManager am;
	private SQLiteDatabase db=null;
	
	private GPSLoopUniversalWindowDao mUniversalWindowDao = null;
	private GPSLoopImageDao mImageDao = null;
	private GPSLoopVideoDao mVideoDao = null;
	private GPSLoopTextDao mTextDao = null;

	public DisplayerBiz(Context mContext) {
		this.mContext = mContext;
		am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		mUniversalWindowDao = new GPSLoopUniversalWindowDao(mContext);
		mImageDao = new GPSLoopImageDao(mContext);
		mVideoDao = new GPSLoopVideoDao(mContext);
		mTextDao = new GPSLoopTextDao(mContext);
	}
	
//	/**
//	 * 
//	 * @param reciever
//	 * @return
//	 */
//	public boolean configOnOffScrn(byte[] reciever){
//		
//	}
	
	/**
	 * 设置定时重启，还是取消已经存在的定时重启。
	 * @param reciveByte
	 * @return  返回值不可靠。
	 */
	public boolean configRebootTime(byte[] reciveByte){
//		Intent intent = new Intent(mContext, AutoConfigReciver.class);
		Intent intent  = new Intent();
		intent.setAction(AutoConfigReciver.AUTOREBOOT);
		PendingIntent operation = PendingIntent.getBroadcast(mContext, 1018, intent, PendingIntent.FLAG_UPDATE_CURRENT);
Log.e("我的地址是", "我的地址1："+operation)		;
		SharedPreferences sp = mContext.getSharedPreferences("autoReboot", Context.MODE_WORLD_READABLE);
		byte isOn = reciveByte[8] ;
		byte hour = reciveByte[9];
		byte minute = reciveByte[10];
		//保存到本地。
		Editor editor = sp.edit();
		editor.putInt("isOn", isOn);
		editor.putString("hour", hour+"");
		editor.putString("minute", minute+"");
		boolean isConfig = editor.commit();
		if (isConfig) {
			if (isOn==0) {
				am.cancel(operation);;
			}else {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
					calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
				}
//				intent.putExtra("setTime",calendar.getTimeInMillis());
//				operation = PendingIntent.getBroadcast(mContext, 1018, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//Log.e("我的地址是", "我的地址2："+operation)				;
				am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
			}
		}else {
			//配置失败
			Log.e("配置失败。。", "配置失败了。。。");
		}
		return isConfig;
	}
	
	/**
	 * 创建一个回复 字节数组，对应自动重启的配置。
	 * @return
	 */
	public byte[] createRespAutoReboot(){
		SharedPreferences sp = mContext.getSharedPreferences("autoReboot", Context.MODE_PRIVATE);
		byte[] autoRebootResp = new byte[12];
		autoRebootResp[0]=(byte) 0xaa;
		autoRebootResp[1] = 0x55;
		autoRebootResp[2] = 0;
		autoRebootResp[3] = 0;
		autoRebootResp[4] = 0;
		autoRebootResp[5] = 12;
		autoRebootResp[6] = 11;
		autoRebootResp[7] = 2;
		autoRebootResp[8] = (byte) sp.getInt("isOn", -1); //从来都没配置过。
		autoRebootResp[9] = Byte.parseByte(sp.getString("hour", "-1"));
		autoRebootResp[10] = Byte.parseByte(sp.getString("minute", "-1"));
		for (int i = 0; i < autoRebootResp.length-1; i++) {
			autoRebootResp[11]+=autoRebootResp[i];
		}
		return autoRebootResp;
		
	}
	
	/**
	 * 返回设置结果的应答信息。
	 * @param isOK
	 * @return 返回isok对应的数组
	 */
	public byte[] createConfigResult(int typeCode ,int funcCode, boolean isOK){
		byte[] result = new byte[10];
		result[0] = (byte) 0xaa;
		result[1] = 0x55;
		result[2] = 0;
		result[3] = 0;
		result[4] = 0;
		result[5] = 10;
		result[6] = (byte) typeCode;
		result [7] = (byte) funcCode;
		result[8] = (byte) (isOK?1:0);
		for (int i = 0; i < result.length-1; i++) {
			result[9]+=result[i];
		}
		return result;
		
				
	}

	/**
	 * 定时开关瓶: requestCode。 从4开始了。 3*7=21个， 知道24。。。。
	 * @param reciveByte
	 */
	public boolean configPlanOnOffScrn(byte[] reciveByte) {
		boolean isSavePlan = false;
		List<PlanOnOff> planOnOffs = new ArrayList<PlanOnOff>();
		byte isWeekOn = reciveByte[8] ;
		byte planType = reciveByte[9];
		Intent intent = new Intent();
		intent.setAction(AutoConfigReciver.AUTOONOFF);
		
		if (planType==0) {//日计划模式
			//星期一
			byte isDay1On  = reciveByte[10];
			byte isMe1_1On = reciveByte[11];
			byte isMe1_1hourOn = reciveByte[12];
			byte isMe1_1minuteOn = reciveByte[13];
			byte isMe1_1hourOff = reciveByte[14];
			byte isMe1_1minuteOff = reciveByte[15];
			
			byte isMe1_2On = reciveByte[16];
			byte isMe1_2hourOn = reciveByte[17];
			byte isMe1_2minuteOn = reciveByte[18];
			byte isMe1_2hourOff = reciveByte[19];
			byte isMe1_2minuteOff = reciveByte[20];
			
			byte isMe1_3On = reciveByte[21];
			byte isMe1_3hourOn = reciveByte[22];
			byte isMe1_3minuteOn = reciveByte[23];
			byte isMe1_3hourOff = reciveByte[24];
			byte isMe1_3minuteOff = reciveByte[25];
			//设置闹铃
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_1On, isMe1_1hourOn, isMe1_1minuteOn, (byte) 1, 101,-1));
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_1On, isMe1_1hourOff, isMe1_1minuteOff, (byte)0, 102,-1));
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_2On, isMe1_2hourOn, isMe1_2minuteOn, (byte)1, 103,-1));
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_2On, isMe1_2hourOff, isMe1_2minuteOff, (byte)0, 104,-1));
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_3On, isMe1_3hourOn, isMe1_3minuteOn, (byte)1, 105,-1));
			planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDay1On, isMe1_3On, isMe1_3hourOff, isMe1_3minuteOff, (byte)0, 106,-1));
			for (int i = 0; i < planOnOffs.size(); i++) {
				PlanOnOff planOnOff = planOnOffs.get(i);
				PendingIntent operation = PendingIntent.getBroadcast(mContext, planOnOff._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				if(planOnOff.isMeOn ==1){
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.HOUR_OF_DAY, planOnOff.hour);
					calendar.set(Calendar.MINUTE, planOnOff.minute);
					if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
						calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
					}
					intent.putExtra("state", planOnOff.state); //开还是关。
					intent.putExtra("setTime",calendar.getTimeInMillis());
					operation = PendingIntent.getBroadcast(mContext, planOnOff._id, intent, PendingIntent.FLAG_UPDATE_CURRENT); 
					am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
				}else {
					am.cancel(operation);
				}
			}
			//保存到xml中。
			isSavePlan = savePlanOnOffXml(planOnOffs);
			
		}else {
			//周计划模式 0~6的id都已经使用。 从7到27的id
			for (int i = 0; i < 7; i++) {
				int index = 10+16*i; //day开关
				byte isDayOn = reciveByte[index];
				int tempIndex = index+1;
				int day_meta_id = 100*(i+1)+1;
				for (int j = 0; j < 3; j++) {
					int index_1 = tempIndex+5*j; //小段的索引。
					byte isMeOn = reciveByte[index_1];
					byte hourOn = reciveByte[index_1+1];
					byte minuteOn = reciveByte[index_1+2];
					byte hourOff = reciveByte[index_1+3];
					byte minuteOff = reciveByte[index_1+4];
					planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDayOn, isMeOn, hourOn, minuteOn, (byte) 1, day_meta_id+j*2,i+1));
					planOnOffs.add(new PlanOnOff(planType, isWeekOn, isDayOn, isMeOn, hourOff, minuteOff, (byte)0, day_meta_id+j*2+1,i+1));
				}
			}
			//建立闹钟
			for (int i = 0; i < planOnOffs.size(); i++) {
				PlanOnOff planOnOff = planOnOffs.get(i);
				PendingIntent operation = PendingIntent.getBroadcast(mContext, planOnOff._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				if (planOnOff.isMeOn==0) {
					//取消所有已经存在的闹钟。
					am.cancel(operation);
				}else {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.HOUR_OF_DAY, planOnOff.hour);
					calendar.set(Calendar.MINUTE, planOnOff.minute);
					int dayofWeek  =0;
					if (planOnOff.dayofweek==7) {
						dayofWeek = 1;
					}else {
						dayofWeek=planOnOff.dayofweek+1;
					}
Log.e("得到的星期是", "要设置的星期字段是："+dayofWeek);					
					calendar.set(Calendar.DAY_OF_WEEK, dayofWeek);
					if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
						calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+7);
//						calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK)+7);
//						calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
					}
					intent.putExtra("state", planOnOff.state); //开还是关。
					intent.putExtra("setTime",calendar.getTimeInMillis());
					am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000*7, operation);
				}
			}
			//保存这个周计划模式的数据到 xml中
			isSavePlan = savePlanOnOffXml(planOnOffs);
			
		}
		return isSavePlan;
		
	}

	private boolean savePlanOnOffXml(List<PlanOnOff> planOnOffs) {
		XmlSerializer xmlSerializer  = Xml.newSerializer();
		OutputStream os = null;
		try {
			os = mContext.openFileOutput("planOnOff", Context.MODE_WORLD_WRITEABLE);
			xmlSerializer.setOutput(os, "utf-8");
			xmlSerializer.startDocument("UTF-8", true); 
			xmlSerializer.text("\n");
			PlanOnOff planOnOff_one = planOnOffs.get(0);
		    setTag(xmlSerializer, "isWeekOn", planOnOff_one.isWeekOn+"");
		    setTag(xmlSerializer, "planType", planOnOff_one.planType+""); //如果是属于日计划类型，自然就只有6个子元素，否则有42个元素。
		    for (int i = 0; i < planOnOffs.size(); i++) {
		    	PlanOnOff planOnOff = planOnOffs.get(i);
	    		xmlSerializer.startTag(null, "planMeta");
	    		xmlSerializer.text("\n");
	    			setTag(xmlSerializer, "isDayOn", planOnOff.isDayOn+"");
	    			setTag(xmlSerializer, "isMeOn", planOnOff.isMeOn+"");
	    			setTag(xmlSerializer, "hour", planOnOff.hour+"");
	    			setTag(xmlSerializer, "minute", planOnOff.minute+"");
	    			setTag(xmlSerializer, "state", planOnOff.state+"");
	    			setTag(xmlSerializer, "_id", planOnOff._id+"");
	    			setTag(xmlSerializer, "dayofweek", planOnOff.dayofweek+"");
	    		xmlSerializer.endTag(null, "planMeta");
	    		xmlSerializer.text("\n");
			}
		    xmlSerializer.endDocument();
			if (os!=null) {
				os.close();
			}	
		    
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 解析出上次配置的开关瓶计划
	 * @return
	 */
	public List<PlanOnOff> parseLastOnOff(){
		List<PlanOnOff> planOnOffs = new ArrayList<PlanOnOff>();
		XmlPullParser xmlPullParser = Xml.newPullParser();
		InputStream is =  null;
		try {
			File file = new File(mContext.getFilesDir(),"planOnOff");
			if (file.exists()==false) {
				Log.e("要解析的planOnOff.xml", "要解析的planOnOff.xml不存在");
				return planOnOffs;
			}
			is = mContext.openFileInput("planOnOff");
			xmlPullParser.setInput(is, "utf-8");
			int event = xmlPullParser.getEventType();
			
			byte planType = 0; //计划类型 开关瓶是 日计划还是周计划
			byte isWeekOn = 0;
			
			PlanOnOff planOnOff = null;
			while (event!=XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = xmlPullParser.getName().trim();
					if (name.trim().equalsIgnoreCase("planType")) {
						planType = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("isWeekOn")) {
						isWeekOn = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("planMeta")) {
						planOnOff = new PlanOnOff();
						planOnOff.planType = planType;
						planOnOff.isWeekOn = isWeekOn;
						planOnOffs.add(planOnOff);
					}else if (name.equalsIgnoreCase("isDayOn")) {
						planOnOff.isDayOn = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("isMeOn")) {
						planOnOff.isMeOn = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("hour")) {
						planOnOff.hour = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("minute")) {
						planOnOff.minute = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("state")) {
						planOnOff.state = Byte.parseByte(xmlPullParser.nextText());
					}else if (name.equalsIgnoreCase("_id")) {
						planOnOff._id = Integer.parseInt((xmlPullParser.nextText()));
					}else if (name.equalsIgnoreCase("dayofweek")) {
						planOnOff.dayofweek = Byte.parseByte(xmlPullParser.nextText());
					}
					break;

				default:
					break;
				}
				event = xmlPullParser.next();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			planOnOffs.clear();
		}finally{
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return planOnOffs;
	}

	/**
	 * 配置自动调节亮度值。 这是一个日计划的操作。
	 * 根据文档所诉：一天三个时间段设置调节亮度。。。坑爹的设计。。 这个闹钟奇怪如果设定当前时间，手动改系统时间后不会触发广播接收器接受呢？？
	 * @param reciveByte
	 * @return
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 * @throws NumberFormatException 
	 */
	public boolean configPlanBright(byte[] reciveByte)  {
		boolean isSavePlan = false;
		List<PlanBrightNess> planBrightNesses = new ArrayList<PlanBrightNess>();
		
		byte isParentOn = reciveByte[8];
		byte is1On =reciveByte[9];
		byte hour1 = reciveByte[10];
		byte minute1 = reciveByte[11];
		byte bright1 = reciveByte[12];
			
		
		byte is2On = reciveByte[13];
		byte hour2 = reciveByte[14];
		byte minute2 = reciveByte[15];
		byte bright2 = reciveByte[16];
		
		byte is3On = reciveByte[17];
		byte hour3 = reciveByte[18];
		byte minute3 = reciveByte[19];
		byte bright3 = reciveByte[20];
		//存入xml
		planBrightNesses.add(new PlanBrightNess(isParentOn, is1On, hour1, minute1, bright1, 1));
		planBrightNesses.add(new PlanBrightNess(isParentOn, is2On, hour2, minute2, bright2, 2));
		planBrightNesses.add(new PlanBrightNess(isParentOn, is3On, hour3, minute3, bright3, 3));
		boolean isSavePlanToXml = savePlanBright(planBrightNesses);
		//建立闹钟计划
//		Intent intent = new Intent(mContext, AutoConfigReciver.class);
		Intent intent = new Intent();
		intent.setAction(AutoConfigReciver.AUTOBIGHTNESS);
		for (PlanBrightNess currPlanBrightNess : planBrightNesses) {
			if (currPlanBrightNess.isMeOn==1) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, currPlanBrightNess.hour);
				calendar.set(Calendar.MINUTE, currPlanBrightNess.minute);
				if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
					calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
				}
				intent.putExtra("bright", currPlanBrightNess.bright);
				intent.putExtra("setTime",calendar.getTimeInMillis());
				PendingIntent operation = PendingIntent.getBroadcast(mContext, currPlanBrightNess._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
			}else {
				PendingIntent operation = PendingIntent.getBroadcast(mContext, currPlanBrightNess._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.cancel(operation);
//				intent.filterEquals(other)
//				intent.getd
			}
		}
		return (isSavePlan=isSavePlanToXml);
	}
	
    /**
     * 解析出上次 的亮度自动调节计划
     * @return
     * @throws XmlPullParserException 
     * @throws IOException 
     * @throws NumberFormatException 
     */
	public List<PlanBrightNess> parseLastPlanBright()  {
		List<PlanBrightNess> planBrightNesses = new ArrayList<PlanBrightNess>();
		XmlPullParser pullParser = Xml.newPullParser();
		FileInputStream is=null;
		try {
			File file = new File(mContext.getFilesDir(),"planBright");
			if (!file.exists()) {
				return planBrightNesses;
			}
			is = new FileInputStream(mContext.getFilesDir().getAbsolutePath()+"/planBright");
			pullParser.setInput(is, "utf-8");
			int event = pullParser.getEventType();
			byte isParentOn = 0;
			PlanBrightNess planBrightNess = null;
			while (event!=XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = pullParser.getName();
					if (name.equalsIgnoreCase("isParentOn")) {
						isParentOn = Byte.parseByte(pullParser.nextText());
					}else if (name.equalsIgnoreCase("planbright")) {
						planBrightNess=new PlanBrightNess();
						planBrightNess.isParentOn = isParentOn;
						planBrightNesses.add(planBrightNess);
					}else if (name.equalsIgnoreCase("isMeOn")) {
						planBrightNess.isMeOn = Byte.parseByte(pullParser.nextText());
					}else if (name.equalsIgnoreCase("hour")) {
						planBrightNess.hour = Byte.parseByte(pullParser.nextText());
					}else if (name.equalsIgnoreCase("minute")) {
						planBrightNess.minute = Byte.parseByte(pullParser.nextText());
					}else if (name.equalsIgnoreCase("bright")) {
						planBrightNess.bright = Byte.parseByte(pullParser.nextText());
					}else if (name.equalsIgnoreCase("_id")) {
						planBrightNess._id = Integer.parseInt(pullParser.nextText());
					}
					break;

				default:
					break;
				}
				event = pullParser.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return planBrightNesses;
	}

	private boolean savePlanBright(List<PlanBrightNess> planBrightNesses) {
		boolean isSaveToXML = false;
		XmlSerializer xmlSerializer = Xml.newSerializer();
		FileOutputStream os;
		try {
			os = mContext.openFileOutput("planBright", Context.MODE_WORLD_WRITEABLE);
			xmlSerializer.setOutput(os, "utf-8");
			xmlSerializer.startDocument("UTF-8", true); 
			xmlSerializer.text("\n");
			setTag(xmlSerializer, "isParentOn", planBrightNesses.get(0).isParentOn+"");
			PlanBrightNess planBrightNess = null;
			for (int i = 0; i < planBrightNesses.size(); i++) {
				planBrightNess = planBrightNesses.get(i);
				xmlSerializer.startTag(null, "planbright");
				xmlSerializer.text("\n");
				setTag(xmlSerializer, "isMeOn", planBrightNess.isMeOn+"");
				setTag(xmlSerializer,"hour", planBrightNess.hour+"");
				setTag(xmlSerializer, "minute", planBrightNess.minute+"");
				setTag(xmlSerializer, "bright", planBrightNess.bright+"");
				setTag(xmlSerializer, "_id", planBrightNess._id+"");
				xmlSerializer.endTag(null, "planbright");
				xmlSerializer.text("\n");
			}
			xmlSerializer.endDocument();
			if (os!=null) {
				os.close();
			}
			isSaveToXML = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isSaveToXML;
	}
	
	
	private void setTag(XmlSerializer xmlSerializer,String tagName,String tagValue){
		try {
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(tagValue);
			xmlSerializer.endTag(null, tagName);
			xmlSerializer.text("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] createRespAutoBright() {
		//先解析出来上次配置的计划。
		List<PlanBrightNess> lastPlanBrightNesses = parseLastPlanBright();  //永远保证都只有三个。。屎
		byte[] resp = new byte[22];
		resp[0] = (byte) 0xaa;
		resp[1] = 0x55;
		resp[5] = 22;
		resp[6] = 10;
		resp[7] = 2;
		if (lastPlanBrightNesses.size()==0) {
			return resp;
		}
		//配置信息。
		PlanBrightNess planBrightNess1= lastPlanBrightNesses.get(0);
		resp[8] = planBrightNess1.isParentOn ;
		resp[9] = planBrightNess1.isMeOn;
		resp[10] = planBrightNess1.hour;
		resp[11] = planBrightNess1.minute;
		resp[12] = planBrightNess1.bright;
		PlanBrightNess planBrightNess2= lastPlanBrightNesses.get(1);
		resp[13] = planBrightNess2.isMeOn;
		resp[14] = planBrightNess2.hour;
		resp[15] = planBrightNess2.minute;
		resp[16] = planBrightNess2.bright;
		PlanBrightNess planBrightNess3= lastPlanBrightNesses.get(2);
		resp[17] = planBrightNess3.isMeOn;
		resp[18] = planBrightNess3.hour;
		resp[19] = planBrightNess3.minute;
		resp[20]  = planBrightNess3.bright;
		for (int i = 0; i < resp.length-1; i++) {
			resp[21]+= resp[i];
		}
		return resp;
	}

	/**
	 * 创建自动回复开关瓶配置信息
	 * @return
	 */
	public byte[] createRespAutoOnOff() {
		List<PlanOnOff> planOnOffs = parseLastOnOff();
		byte[] resp =new byte[123];
		resp[0] = (byte) 0xaa;
		resp[1] = 0x55;
		resp[5] = 123;
		resp[6] = 9;
		resp[7] = 2;
		if (planOnOffs.size()==0) {
Log.e("个事是oooo", "数量为何是000啊！！！");			
			return resp;
		}
		List<PlanOnOff> planOnOffs_new = new ArrayList<PlanOnOff>();
		if (planOnOffs.size()==6) {
			//复制7个相同的
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 6; j++) {
					planOnOffs_new.add(planOnOffs.get(j));
				}
			}
			planOnOffs = planOnOffs_new;
Log.e("数量", "元数据的个数是："+planOnOffs.size())	;
		}
		
		//配置信息
		PlanOnOff onePlan = planOnOffs.get(0);
		resp[8] = onePlan.isWeekOn;
		resp[9] = onePlan.planType;
		for (int i = 0; i < 7; i++) {
			resp[10+16*i] = planOnOffs.get(6*i).isDayOn;
		}
		int index = 11;
		int index_0 = 0;
		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 3; i++) {
				PlanOnOff planOnOff = planOnOffs.get(index_0++);
				resp[index++] = planOnOff.isMeOn;
				resp[index++] = planOnOff.hour;
				resp[index++] = planOnOff.minute;
				planOnOff =planOnOffs.get(index_0++);
				resp[index++] = planOnOff.hour;
				resp[index++] = planOnOff.minute;
			}
			index++;
		}
		
		return resp;
	}
	
	public BasicPage getBasicPageByDB(){
		BasicPage basicPage = null;
		//1.随便找个窗口建立界面再说
		ArrayList<UniversalWindow> universalWindows = mUniversalWindowDao.findUniversalWindowsByCondition(null, null, null, null, null, "0,1");
		if (universalWindows.size()>0) {
			basicPage = new BasicPage();
			basicPage.AddUniversalWindow(universalWindows.get(0));
			basicPage.Addwindowlist(0);
		}
		
		return basicPage;
	}
	/**
	 * 创建一个页面。
	 * @return
	 */
	public BasicPage getBasicPage(){
		BasicPage basicPage = null;
		//1.随便找个窗口建立界面再说
		UniversalWindow universalwindow = null;
		Cursor cursor = mContext.getContentResolver().query(Uri.parse(MetaData.TB_Universalwindow.URI_UNIVERSAL), null, null, null, null);
		if (cursor!=null&&cursor.moveToNext()) {
			basicPage = new BasicPage();
			universalwindow = new UniversalWindow();
			universalwindow.startX = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._STARTX));
			universalwindow.startY = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._STARTY));
			universalwindow.heiht = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._HEIGHT));
			universalwindow.width = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._WIDTH));
	//没用到		universalwindow.BorderColor = Color.parseColor(cursor.getString(cursor.getColumnIndex(MetaData.TB_Universalwindow._BORDERCOLOR)));
			universalwindow.BorderType = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._BORDERTYPE));
			//2.随便找个资源，不用找。。。  一个页面一个通用窗口
			basicPage.AddUniversalWindow(universalwindow);
			basicPage.Addwindowlist(0);
		}
		if (cursor!=null) {
			cursor.close();
		}
		
		
		return basicPage;
		
	}
	
	public Object getWindowResByDB(String beginTime,boolean isDeepSearch){
		Object obj = null;
		ArrayList<VideoVo> universalWindows = mVideoDao.findVideoVosByCondition("_begintime='"+beginTime+"'", null, null, null, null, null);
		if (universalWindows.size()==0) {
			ArrayList<ImageVo> imageVos = mImageDao.findImageVoByCondition("_begintime='"+beginTime+"'", null, null, null, null, null);
			if (imageVos.size()==0) {
				//接着判断是否有走马灯呢个
				ArrayList<TextVo> textVos = mTextDao.findTextVosByCondition("_begintime='"+beginTime+"'", null, null, null, null, null);
				if (textVos.size()==0) {
					 //什么资源都没有,尝试深度搜索
					if (isDeepSearch) {
						obj = researchDeep(beginTime);
						Log.i("DisplayBiz", "深度查询你是肿么办到的？~~~~来自原生态的数据库告白~~~~~~~~~");
					}
				}else {
					obj = textVos.get(0);
				}
			}else {
				obj = imageVos.get(0);
			}
		}else {
			obj = universalWindows.get(0);
		}
		return obj;
	}

	/**
	 * 得到窗口中的资源文件对象
	 * @param isDeepSearch 是否深度查询，条件是 刚开机或者点播刚结束（实际上我只把上次是否是点播传进来了），但是目前时间点属于某一个播放过程中
	 * @return 
	 */
	public Object getWindowRes(String beginTime,boolean isDeepSearch) {
		long beging = System.currentTimeMillis();
		Object obj = null;
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(MetaData.TB_Video.URI_TB_VIDEO), null, "_begintime='"+beginTime+"'", null, null);
		if (cursor==null) {
			Log.e("DispLayerBiz", "错误：video表不存在！！");
			return obj;
		}
		if (cursor.moveToNext()) {
			//有视频文件
			VideoVo videoVo = new VideoVo();
			videoVo.VideoProportion = cursor.getInt(2);
			videoVo.VoiceValue = cursor.getInt(1);
			videoVo.FilePath = cursor.getString(3);
			obj = videoVo;
Log.i("DisplayBiz", "beginTime:"+beginTime+"-----videoVo:"+videoVo+"--查出的begintime:"+cursor.getString(4));	
		}else {
			cursor.close();
			//没有视频，可能是图片或文字
			cursor = resolver.query(Uri.parse(MetaData.TB_Image.URI_TB_IMAGE), null, "_begintime='"+beginTime+"'", null, null);
			if (cursor==null) {
				Log.e("Display", "错误：Image表不存在！！！");
				return obj;
			}
			if (cursor.moveToFirst()) {
				//有图片资源
				ImageVo imageVo = new ImageVo();
				imageVo.BgClr = Color.parseColor("#"+cursor.getString(1));
				imageVo.Description = cursor.getInt(2);
				imageVo.Effects = cursor.getInt(3);
				imageVo.StayLine = cursor.getInt(5);//这个多余
				imageVo.setTxtSpeed(cursor.getInt(7));
				imageVo.FilePath = cursor.getString(4);
				imageVo.Transparent = cursor.getInt(6)==0?false:true;
				obj = imageVo;
				Log.i("DisplayBiz", "begingTime:"+beginTime+"-----imageVo:"+imageVo+"---db查出begingTime:"+cursor.getString(8));
			}else {
				cursor.close();
				//没有图片，接着判断是否有文字
				cursor = resolver.query(Uri.parse(MetaData.TB_Text.URI_TB_TEXT), null, "_begintime='"+beginTime+"'", null, null);
				if (cursor==null) {
					Log.e("Display", "错误：Text表不存在！！！");
					return obj;
				}
				if (cursor.moveToFirst()) {
					TextVo textVo = new TextVo();
					textVo.Transparent = cursor.getInt(9)==0?false:true;
					textVo.WalkSpeed = cursor.getInt(11);
					textVo.SingleLine = cursor.getInt(7)==0?false:true;
					textVo.Static = cursor.getInt(8) ==0?false:true;
					textVo.ProLen = cursor.getInt(6);
					textVo.textviewattr.Content = cursor.getString(2);
					textVo.textviewattr.WordClr = Color.parseColor("#"+cursor.getString(13));
					textVo.textviewattr.BgClr = Color.parseColor("#"+cursor.getString(1));
					textVo.textviewattr.Italic = cursor.getInt(5);
					textVo.textviewattr.Underline = cursor.getInt(10);
					textVo.textviewattr.Height = cursor.getInt(4);
					textVo.textviewattr.FaceName = cursor.getInt(3);
					textVo.textviewattr.Weight = cursor.getInt(12);
					obj = textVo;
					Log.e("DisplayBiz", "begingTime:"+beginTime+"---TextVo:"+textVo+"--db查询出beginTime:"+cursor.getString(14));
					cursor.close();cursor = null;
				}else {
					//神马都没有， 如果是刚开机是需要播放 当前时间所属于的时间段的那个文件的(不然空着直到下一个界面来临不妥)。
					//点播之后也要做同样的检测。
					//所有同一分钟内的 videoVo
					if (isDeepSearch) {
						obj = researchDeep(beginTime);
						
						Log.i("DisplayBiz", "深度查询如何做的了~~~~~~~~~~~~~");
					}
					
//					Log.i("神马都没有", "神马都没有"+beginTime);
					
				}
			}
		}
			
		if (cursor!=null) {
			cursor.close();
		}
//		Log.e("查询耗时", "----@@----查询耗时："+(System.currentTimeMillis()-beging)+"--begintime:"+beginTime);
		return obj;
	}

	private Object researchDeep(String beginTime) {
		Object object = null;
		if(db==null)
			db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();
		int image_dis = -1;
		int video_dis = -1;
		int text_dis = -1;
		
		Cursor cursor_image = db.rawQuery("select distinct "+beginTime+"-t1._begintime as re,t1.* from ImageVo t1 where re>0  group by re having re=(select min("+beginTime+"-t0.[_begintime]) from ImageVo t0 where "+beginTime+"-t0.[_begintime]>0) ", null);
		Cursor cursor_video = db.rawQuery("select distinct "+beginTime+"-t1._begintime as re,t1.* from VideoVo t1 where re in ( select min("+beginTime+"-t0._begintime) as re from VideoVo t0 where "+beginTime+"-t0._begintime>0 )", null);
		Cursor cursor_text = db.rawQuery("select distinct "+beginTime+"-t1._begintime as re,t1.* from TextVo t1 where re in ( select min("+beginTime+"-t0._begintime) as re from TextVo t0 where "+beginTime+"-t0._begintime>0 )", null);
		if (cursor_image.moveToFirst()) {
			image_dis = cursor_image.getInt(0);
		}
		if (cursor_video.moveToFirst()) {
			video_dis = cursor_video.getInt(0);
		}
		if (cursor_text.moveToFirst()) {
			text_dis = cursor_text.getInt(0);
		}
		
		if (image_dis!=-1&&video_dis!=-1&&text_dis!=-1) {
			int i = image_dis>=video_dis?(video_dis>=text_dis?text_dis:video_dis):(image_dis>=text_dis?text_dis:image_dis);
			if (i==image_dis) { //处于图片播放阶段
				ImageVo imageVo = getImageVoByMyCursor(cursor_image);
				object = imageVo;
			}else if (i==video_dis) { //处于视频播放阶段
				VideoVo videoVo = getVideoVoByMyCursor(cursor_video);
				object = videoVo;
			}else if (i==text_dis) { //处于文字播放阶段
				TextVo textVo = getTextVoByMyCursor(cursor_text);
				object = textVo;
			}
		}else if (image_dis!=-1&&video_dis!=-1) {//图片和视频有，文字没有
			int min_num = Math.min(image_dis, video_dis);
			if (min_num==image_dis) {
				object = getImageVoByMyCursor(cursor_image);
			}else if (min_num==video_dis) {
				object = getVideoVoByMyCursor(cursor_video);
			}
		}else if (image_dis!=-1&&text_dis!=-1) {//图片和文字有，视频没有
			int min_num = Math.min(image_dis, text_dis) ;
			if (min_num == image_dis) {
				object = getImageVoByMyCursor(cursor_image);
			}else if (min_num == text_dis) {
				object = getTextVoByMyCursor(cursor_text);
			}
		}else if (video_dis!=-1&&text_dis!=-1) {
			int min_num = Math.min(video_dis, text_dis);
			if (min_num == video_dis ) {
				object = getVideoVoByMyCursor(cursor_video);
			}else if (min_num == text_dis ) {
				object = getTextVoByMyCursor(cursor_text);
			}
		}
		
		if (cursor_image!=null) {
			cursor_image.close();
		}
		if (cursor_video!=null) {
			cursor_video.close();
		}
		if (cursor_text!=null) {
			cursor_text.close();
		}
		return object;
		
		
	}

	private TextVo getTextVoByMyCursor(Cursor cursor_text) {
		TextVo textVo = new TextVo();
		textVo.Transparent = cursor_text.getInt(10)==0?false:true;
		textVo.WalkSpeed = cursor_text.getInt(12);
		textVo.SingleLine = cursor_text.getInt(8)==0?false:true;
		textVo.Static = cursor_text.getInt(9) ==0?false:true;
		textVo.ProLen = cursor_text.getInt(7);
		textVo.textviewattr.Content = cursor_text.getString(3);
		textVo.textviewattr.WordClr = Color.parseColor("#"+cursor_text.getString(14));
		textVo.textviewattr.BgClr = Color.parseColor("#"+cursor_text.getString(2));
		textVo.textviewattr.Italic = cursor_text.getInt(6);
		textVo.textviewattr.Underline = cursor_text.getInt(11);
		textVo.textviewattr.Height = cursor_text.getInt(5);
		textVo.textviewattr.FaceName = cursor_text.getInt(4);
		textVo.textviewattr.Weight = cursor_text.getInt(13);
		return textVo;
	}

	private VideoVo getVideoVoByMyCursor(Cursor cursor_video) {
		VideoVo videoVo = new VideoVo();
		videoVo.VideoProportion = cursor_video.getInt(3);
		videoVo.VoiceValue = cursor_video.getInt(2);
		videoVo.FilePath = cursor_video.getString(4);
		return videoVo;
	}

	private ImageVo getImageVoByMyCursor(Cursor cursor_image) {
		ImageVo imageVo = new ImageVo();
		imageVo.BgClr = Color.parseColor("#"+cursor_image.getString(2));
		imageVo.Description = cursor_image.getInt(3);
		imageVo.Effects = cursor_image.getInt(4);
		imageVo.StayLine = cursor_image.getInt(6);//这个多余
		imageVo.setTxtSpeed(cursor_image.getInt(8));
		imageVo.FilePath = cursor_image.getString(5);
		imageVo.Transparent = cursor_image.getInt(7)==0?false:true;
		return imageVo;
	}

	private SharedPreferences sp_lan;
	private Editor sp_lan_editor;
	public byte[] createRespLANSyncHostState() {
		byte[] resp = new byte[10];
		resp[0] = (byte) 0xaa;
		resp[1] = 0x55;
		resp[5] = 10;
		resp[6] = 15;
		resp[7] = 2;
		if (sp_lan==null) {
			sp_lan = mContext.getSharedPreferences("isMeLANHost", Context.MODE_PRIVATE);
			sp_lan_editor = sp_lan.edit();
		}
		resp[8] = (byte) sp_lan.getInt("isMeLANHost", 0) ;
		for (int i = 0; i < resp.length-1; i++) {
			resp[9]+=resp[i];
		}
		return resp;
	}
	
	/**
	 * 设置局域网主机
	 * isMeLANHost  持久变量
	 * @return
	 */
	public boolean setLANHost(boolean isSet){
		boolean isHost = false;
		if (sp_lan==null) {
			sp_lan = mContext.getSharedPreferences("isMeLANHost", Context.MODE_PRIVATE);
			sp_lan_editor = sp_lan.edit();
		}
		sp_lan_editor.putBoolean("isMeLANHost", isSet);
		isHost = sp_lan_editor.commit();
		return isHost;
	}

	/**
	 * 回复上位机
	 * @return
	 */
	public byte[] createRespLANSyncSet(boolean isSetOk) {
		byte[] resp = new byte[10];
		resp[0] = (byte) 0xaa;
		resp[1] = 0x55;
		resp[5] = 10;
		resp[6] = 15;
		resp[7] = 4;
		resp[8] = (byte) (isSetOk?1:0);
		for (int i = 0; i < resp.length-1; i++) {
			resp[9]+=resp[i];
		}
		return null;
	}
	
}
