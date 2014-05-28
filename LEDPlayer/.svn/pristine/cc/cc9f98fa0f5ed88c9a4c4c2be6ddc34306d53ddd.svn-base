package com.led.player.receiver;


import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.led.player.LedActivity;
import com.led.player.R;
import com.led.player.moudle.APInfo;
import com.led.player.utils.ToastUtil;
import com.led.player.utils.WifiHotSpot;

/**
 * 判断连接上wifi热点后，做出的操作。比如提示信息什么的
 * @author 1231
 */
public class ConnectedReceiver extends BroadcastReceiver{
	public static boolean wifi_conn_state = false;
	private WindowManager wm  = null;
	private WindowManager.LayoutParams params =null;
	private boolean prestate = false;
	private Resources resources = null;
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		if (resources==null) {
			resources=context.getResources();
		}
		String action = intent.getAction();
		// wifi连接状态改变时触发，如从 认证到连接上wifi要触发3次，断开wifi好像触发了2此，下面只会type是wifi的。
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
Log.e("NETWORK_STATE_CHANGED_ACTION", "网络状态改变:"+networkInfo.getTypeName()+
		"是否已连接:"+networkInfo.isConnected()); //app启动会收到系统上次wifi状态改变发出来大的广播。
//Toast.makeText(context, "networkinfo state:"+networkInfo.getState(), 0).show();			
//ToastUtil.setRBShow(context, "networkinfo state:"+networkInfo.getState(), Toast.LENGTH_SHORT);
//			Toast.makeText(context, networkInfo.getTypeName(), 0).show();总是WIFI
			if (networkInfo.isConnected()) {
				prestate=true;
				ConnectedReceiver.wifi_conn_state = true;
				
				String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);//mac地址
				WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
ToastUtil.setRBShow(mContext, Build.VERSION.SDK_INT+"--当前api版本数值", 1000);
if (wifiInfo==null) {//强制死
	return;
}				
				StringBuilder sBuilder = new StringBuilder();
				Resources resource = context.getResources();
				String apnnameString = resource.getString(R.string.apname);
				String getSSID = wifiInfo.getSSID();
				sBuilder.append(resource.getString(R.string.apname)+wifiInfo.getSSID()+"\n");
//				sBuilder.append(resource.getString(R.string.apmac)+bssid+"\n");
//				sBuilder.append(resource.getString(R.string.localmac)+wifiInfo.getMacAddress()+"\n");
				sBuilder.append(resource.getString(R.string.link_speed)+wifiInfo.getLinkSpeed()+"Mbps\n");
				sBuilder.append(resource.getString(R.string.rssi)+wifiInfo.getRssi()+"dBm");
				String msg = sBuilder.toString();
ToastUtil.setRBShow(context, msg, Toast.LENGTH_LONG);
				
				LayoutInflater inflater = LayoutInflater.from(context);
				final View tip_msg = inflater.inflate(R.layout.connected_tip, null);
				TextView tv = (TextView) tip_msg.findViewById(R.id.tip_msg);
				tv.setText(msg);
				//得出这个状态让高度
				if (wm==null) {
					wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				}
				if (params==null) {
					params = new WindowManager.LayoutParams();
				}
				params.alpha = 0.99f; 
				params.width=LayoutParams.WRAP_CONTENT;
				params.height=LayoutParams.WRAP_CONTENT;
				params.type = LayoutParams.TYPE_SYSTEM_ALERT;
				params.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角，便于调整坐标 ]
				params.y=10;
				//设置Window flag  
				params.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL|LayoutParams.FLAG_NOT_FOCUSABLE;  
				wm.addView(tip_msg, params);
				
				//设置定时器
				final Timer timer = new Timer("定时器关闭提示信息");
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						wm.removeView(tip_msg);
						timer.cancel();
						return;
					}
				}, 25000);
				
				ToastUtil.setLTShow(context, resource.getString(R.string.ap_connected), Toast.LENGTH_SHORT);
			}
			if (networkInfo.getState()==NetworkInfo.State.DISCONNECTED&&prestate==true) {//程序只要一启动就会收到这个
				prestate=false;
				ToastUtil.setLTShow(context, R.string.wifi_disconnected, Toast.LENGTH_LONG);
				ConnectedReceiver.wifi_conn_state = false;
				//掉线重连。
				if (LedActivity.isTMParentOn==false&&LedActivity.isAPParentOn==false&&LedActivity.isBootNow==false) {
					resumeWifi();
				}
			}
			if (networkInfo.getState()==NetworkInfo.State.CONNECTING) {
				ToastUtil.setLTShow(context,resources.getString(R.string.ap_connecting), Toast.LENGTH_SHORT);
			}
		}
		
		//下面就2个状态，wifi开关打开了和关闭了//连到别人都有广播。，艹，我打开wifi也说连上了！！
		if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			boolean isConnected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
//			ToastUtil.setLTShow(context, isConnected?"wifi开关已经打开了(enabled)":"wifi开关已经断开了(disabling)", Toast.LENGTH_SHORT);
			ToastUtil.setLTShow(context, isConnected?mContext.getResources().getString(R.string.wifi_open):mContext.getResources().getString(R.string.wifi_closed), Toast.LENGTH_SHORT);
		}
		
		if (action.endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)) {
			case WifiManager.WIFI_STATE_ENABLED:
				ToastUtil.setLTShow(context, resources.getString(R.string.wifi_open), Toast.LENGTH_SHORT);
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				ToastUtil.setLTShow(context, resources.getString(R.string.wifi_closed), Toast.LENGTH_SHORT);
				break;
//			case WifiManager.WIFI_STATE_UNKNOWN:
//				ToastUtil.setLTShow(context, resources.getString(R.string.wlan_card_state_innormal), Toast.LENGTH_SHORT);
//				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 关闭热点
	 */
	private boolean stopHotSpot(APInfo apInfo){
		WifiHotSpot wifiHotSpot = WifiHotSpot.getInstance(mContext);
		return wifiHotSpot.closeWifiHot(apInfo);
	}
	
	/**
	 * 判断上次状态如果是客户端 就自动启动上次那种状态
	 */
	public void resumeWifi() {
		final SharedPreferences sPreferences = mContext.getSharedPreferences("wifiLastState", Context.MODE_PRIVATE);
		int wifi_conn_last_state = sPreferences.getInt("wifi_conn_last_state", -1);//还是0,1代表热点与终端。
		if (wifi_conn_last_state==0) {
			final Timer timer = new Timer("wifi_ap_timer");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (LedActivity.isNetWorkCardError) {
						timer.cancel();
						LedActivity.isAPParentOn = false;
						return;
					}
					LedActivity.isAPParentOn = true;
					if (LedActivity.wifi_ap_state==false) {
						Log.e("上次开机是热点", "这次还没开启热点。。in ConnectedReceiver");
						//sdcard没插入也需要停止。
						//不用读取上次sdcard的信息，去sharedpreference中读取。
						String ap_ssid = sPreferences.getString("ap_ssid", null);
						String ap_password = sPreferences.getString("ap_password", null);
						APInfo apInfo = new APInfo(ap_ssid, ap_password, null);
						try {
							//如果上次写入配置意外断电。。
							
							WifiHotSpot wifiHotSpot = WifiHotSpot.getInstance(mContext);
							wifiHotSpot.startHotSpot(apInfo);
						} catch (Exception e) {
							e.printStackTrace();
							timer.cancel();
							return ;
						}
					}else {
						Log.e("热点启动成功in ConnRecv", "热点终于启动了，退出timer in ConnectedReceiver");
						timer.cancel();
						LedActivity.isAPParentOn = false;
						LedActivity.wifi_ap_state = true;
						return;
					}
					
				}
			}, 0,15000);
		}
		if (wifi_conn_last_state==1) {
			final Timer timer = new Timer("wifi_client_timer");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (LedActivity.isNetWorkCardError) {
						timer.cancel();
						LedActivity.isTMParentOn = false;
						return;
					}
					LedActivity.isTMParentOn = true;
						if(ConnectedReceiver.wifi_conn_state==false){
							//不管开没开热点，关闭热点
Log.e("上次开机是终端，", "还没连接上。。in ConnectedReceiver");			
		stopHotSpot(new APInfo("joyhaha", "12345678", APInfo.AP_TYPE));
							WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
							wifiManager.setWifiEnabled(false);
							wifiManager.setWifiEnabled(true);//热点如果打开了这个操作会一直等待热点关闭后才执行。
							wifiManager = null;
Log.e("跑完了", "设置连接完了-----Receiver");							
						}else {
							Log.e("wifi终端", "wifi终端终于连接上了 in ConnectedReceiver");
							timer.cancel();
							LedActivity.isTMParentOn = false;
							ConnectedReceiver.wifi_conn_state=true;
							return;
						}
				}
			}, 0, 15000);
		}
	}
	
}
