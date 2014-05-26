package com.led.player;

import com.led.player.utils.ToastHandler;
import com.taobao.cnwatch.CrashHandler;

import android.app.Application;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance() ;
		crashHandler.init(this) ; 
		
		ToastHandler toastHandler = new ToastHandler();
		toastHandler.init(this);
	}
}
