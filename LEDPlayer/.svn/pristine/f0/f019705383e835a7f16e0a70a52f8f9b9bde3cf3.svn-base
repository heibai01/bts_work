package com.led.player.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class SetTime{
	// date -s "20130603.184023"  ��Ȼ��׿��������������ã�����linux������ȫ��ͬ
	static String TAG = "setTime";
	public static boolean timeSynchronization(Date data){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd.HHmmss");
		String datetime=sdf.format(data);  
Log.e(TAG, datetime+"JoyChine***设置时间。。。。****");

//		ArrayList<String> envlist = new ArrayList<String>();
//		Map<String, String> env = System.getenv();
//		for (String envName : env.keySet()) {
//			envlist.add(envName + "=" + env.get(envName));
//		}
//		String[] envp = (String[]) envlist.toArray(new String[0]);
		String command = "date -s\""+datetime+"\"";
		
//Process p =Runtime.getRuntime().exec(new String[] { "su", "-c", command },envp);
//int code = p.waitFor();
		return EthernetSet.writeOsData(command);
	}
	
	public static boolean timeSynchronization(long millseconds){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd.HHmmss");
		Date date = new Date(millseconds);
		return timeSynchronization(date);
//		String datetime=sdf.format(date);  
//Log.e(TAG, datetime+"JoyChine*******settime");
//
//		ArrayList<String> envlist = new ArrayList<String>();
//		Map<String, String> env = System.getenv();
//		for (String envName : env.keySet()) {
//			envlist.add(envName + "=" + env.get(envName));
//		}
//		String[] envp = (String[]) envlist.toArray(new String[0]);
//		String command;
//		command = "date -s\""+datetime+"\"";
//		try {
//			Log.e("joychieng", "阻塞前。。。");
//			Process p =Runtime.getRuntime().exec(new String[] { "su", "-c", command },
//					envp);
//			p.getOutputStream().write("exit\n".getBytes());
//			Log.e("settime", "阻塞后了。。。");
//			int code = p.waitFor();
//			return code==0?true:false;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
	}
}