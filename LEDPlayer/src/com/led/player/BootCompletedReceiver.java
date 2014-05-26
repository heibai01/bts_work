package com.led.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
Log.e("开机广播", "开机广播啊。。。。"+intent.getAction());		
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			if (LedActivity.isLedActivityOn==false) {
Log.e("BootCompleted", "收到开机广播~~in BootCompletedReceiver");				
				Intent newIntent = new Intent(context, LedActivity.class);  
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				Log.e("BootCompletedReceiver", "BootCompletedReceiver onReceive,而且LedActivity还没启动");
				context.startActivity(newIntent);
			} 
		}
	}

}
