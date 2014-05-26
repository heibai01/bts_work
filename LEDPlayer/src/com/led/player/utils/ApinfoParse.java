package com.led.player.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.led.player.aidance.LedProtocol;
import com.led.player.moudle.APInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 判断 apinfo的wifi_type, 会用handler带一个key为apInfo的对象过去。
 * @author 1231
 */
public class ApinfoParse implements Runnable{
	private Handler mHandler;
	private Context mContext;
	
	public ApinfoParse(Handler mHandler,Context context) {
		this.mHandler = mHandler;
		this.mContext = context;
	}

	public void run() {
		File file = new File("/mnt/extsd/config/Network");
		if (file.isFile()==false||file.exists()==false) {
			mHandler.sendEmptyMessage(LedProtocol.NETWORK_NOT_FOUND);
			return;
		}
		FileInputStream is = null;
		APInfo apInfo;
		try {
			apInfo = PullParseBTS.getParseObject();
			saveAndConfigWlan(apInfo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(LedProtocol.NETWORK_PARSE_FAIL);
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
		
	}

	/**
	 * 保存本次网路配置信息 和 配置生效
	 * @param apInfo
	 * @param mHandler
	 */
	public  void saveAndConfigWlan(APInfo apInfo) {
		Message msg = Message.obtain();
		Bundle bundle = new Bundle();
Log.e("APINFO---in ApinfoParse", apInfo+"---")			;
		bundle.putSerializable("apInfo", apInfo);
		//保存本次读取到的wifi热点信息到sharedpreference中
		SharedPreferences spPreferences = mContext.getSharedPreferences("wifiLastState", Context.MODE_PRIVATE);
		Editor editor = spPreferences.edit();
		editor.putString("wifi_mode", apInfo.wifi_type+"");
		editor.putString("ap_ssid", apInfo.ssid);
		editor.putString("ap_password", apInfo.psk);
		if (apInfo.wifi_type==0) {
			editor.putInt("wifi_conn_last_state", 0);
		}else if (apInfo.wifi_type==1) {
			editor.putInt("wifi_conn_last_state", 1);
		}else {
			editor.putInt("wifi_conn_last_state", 2);
		}
		editor.commit();
		spPreferences=null;
		editor=null;
		
		msg.setData(bundle);
		msg.what = LedProtocol.NETWORK_PARSE_OK;
		mHandler.sendMessage(msg);
	}

}
