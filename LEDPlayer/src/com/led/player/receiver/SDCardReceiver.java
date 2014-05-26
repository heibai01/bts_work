package com.led.player.receiver;

import java.io.File;

import com.led.player.LedActivity;
import com.led.player.R;
import com.led.player.utils.ApinfoParse;
import com.led.player.utils.ToastUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
public class SDCardReceiver extends BroadcastReceiver{
	private Handler mHandler = null;
	
	public SDCardReceiver(Handler mHandler) {
		this.mHandler = mHandler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			Uri uri = intent.getData();
			String path = uri.getPath();
Log.e("TFCardReceiver", "path:"+path); //path:file:///mnt/extsd  这个是uri.toString()的结果scheme是file看到了
			if (path!=null&&path.trim().equalsIgnoreCase("/mnt/extsd")) {
				File flFile = new File("/mnt/extsd/config/Network");
				if (flFile.exists()==false||flFile.isFile()==false) {
					return;
				}
				ToastUtil.setLTShow(context, context.getResources().getString(R.string.sd_insert), 0);
					//tf卡已经插入设备。解析配置，更新wifi
					//启动线程去解析xml文件，判断是作为热点还还是作为终端还是禁用wifi功能
					Thread thread = new Thread(new ApinfoParse(mHandler,context));
					thread.setName("sdcard receiver thread");
					thread.start();
			}
		}
	}

}
