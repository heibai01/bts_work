package com.led.player.moudle;

import java.io.Serializable;

/**
 * 保存在文件中的连接过的AP信息,或许创建wifi热点时也需要的哦，这里提供ssid和psk了。
 * @author 1231
 */
public class APInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int AP_TYPE =0;
	public static final int TM_TYPE=1;
	public static final int OFF_TYPE=2;
	public int wifi_type; //0,1,2分别表示 热点，终端，禁用wifi 的三中功能
	public String ssid ;
	public String psk;
	public String key_mgmt="WPA-PSK";
	
	public APInfo() {}
	/**
	 * full constructor
	 * @param ssid
	 * @param psk
	 * @param key_mgmt
	 */
	public APInfo(String ssid, String psk, String key_mgmt) {
		this.ssid = ssid;
		this.psk = psk;
		if (key_mgmt!=null) {
			this.key_mgmt = key_mgmt;
		}
	}
	/**
	 * middle constructor， wpa-psk加密
	 */
	public APInfo(String ssid, String psk,int type) {
		this.ssid = ssid;
		this.psk = psk;
		this.wifi_type = type;
	}
	@Override
	public String toString() {
		return "APInfo [wifi_type=" + wifi_type + ", ssid=" + ssid + ", psk="
				+ psk + ", key_mgmt=" + key_mgmt + "]";
	}
	
}
