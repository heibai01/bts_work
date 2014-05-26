package com.led.player.utils;

import java.lang.reflect.Method;

import com.led.player.R;
import com.led.player.moudle.APInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
/**
 * wifi热点
 * @author 1231
 */
public class WifiHotSpot  {
	private static   WifiManager mWifiManager;
	private static Context mContext;
	private static WifiHotSpot wifiHotSpot = null;
	private APInfo oldApInfo;
	
	 private WifiHotSpot() {}
	 /**
	  * 启动热点
	  * @param apInfo
	  */
	 public void startHotSpot(APInfo apInfo) {
			 this.oldApInfo = apInfo;
Log.e("WifiHotspot", oldApInfo.toString());
		 setWifiApEnabled(oldApInfo,false);//这是前一个apinfo 我靠，又可以关闭wifi热点了，哪怕oldapinfo不是我的现在正创建的。
		setWifiApEnabled(apInfo,true);
	}
	
	public static  WifiHotSpot getInstance(Context context){
		if (wifiHotSpot==null) {
			mContext = context;
			//获取wifi管理服务
			mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			wifiHotSpot = new WifiHotSpot();
		}
		return wifiHotSpot;
	}
	

	/** wifi热点开关*/
	private boolean setWifiApEnabled(APInfo apInfo,boolean enabled) {
		 // disable WiFi in any case
		//wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi ， 这个操作系统在开启热点前其实会帮我做。但是不可靠~~还是自己判断下
		if (apInfo.wifi_type!=APInfo.TM_TYPE) {
			mWifiManager.setWifiEnabled(false); 
		}
		
		try {
     		//通过反射调用设置热点
			Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			//返回热点打开状态
			WifiConfiguration wifiConfiguration = CreateWifiInfo(apInfo.ssid, apInfo.psk, 3);
			//下面这种操作似乎是 队列式的，方法不阻塞，但是操作都会做。
	//		method.invoke(mWifiManager,wifiConfiguration , false); //先关闭其他可能是其他类型热点，如开放式，下面就不行更新成wpa式
			return (Boolean) method.invoke(mWifiManager,wifiConfiguration , enabled); //如果接入点已经打开就更新配置信息(没效)，禁用好像只会判断apconfig的id
		} catch (Exception e) {
			return false;
		}
	} 
	
	/**
	 * 艹 ，1和2 都是开放的在我手机上，只有3 可行啊不开放
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	 public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)  
    {
// Password = Password+(int)(Math.random()*100);
          WifiConfiguration config = new WifiConfiguration();    
          config.allowedAuthAlgorithms.clear();  
           config.allowedGroupCiphers.clear();  
           config.allowedKeyManagement.clear();  
           config.allowedPairwiseCiphers.clear();  
           config.allowedProtocols.clear();  
          config.SSID =SSID ;    	//不是说要引号引起来么，我没这么操作也没事， 还是改正规反而出现""在名字中
           
          if(Type == 1) //WIFICIPHER_NOPASS 
          {
               config.wepKeys[0] = "";   //没用的 还是开放式。。。
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
               config.wepTxKeyIndex = 0;  
          }  
          if(Type == 2) //WIFICIPHER_WEP 
          {
              config.hiddenSSID = true; 
              config.wepKeys[0]=Password;  
              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
              config.wepTxKeyIndex = 0;  
          }  
          if(Type == 3) //WIFICIPHER_WPA 
          {
          config.preSharedKey =Password;  
          config.hiddenSSID = true;    
          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
          config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
          config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);   
          
//          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
//          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
          
          }  
          
//          config.status = WifiConfiguration.Status.ENABLED;  
           return config;  
    }  
 
	 /**
	  * 关闭热点
	  */
	 public boolean closeWifiHot(APInfo apInfo){
		boolean isClosed = setWifiApEnabled(apInfo,false);
		 return isClosed;
	 }
	 
	 /**
	  * 整形的ip转换层字符串形式
	  * @param a
	  * @return
	  */
	 public static String intToString(int a){
		 StringBuilder sb = new StringBuilder();
		 int b = a&0xff;
		 sb.append(b+".");
		 b=(a>>8)&0xff;
		 sb.append(b+".");
		 b=(a>>16)&0xff;
		 sb.append(b+".");
		 b=(a>>24)&0xff;
		 sb.append(b);
		 return sb.toString();
		 
		 
	 }
}