package com.led.player.moudle;

public class ConfigInfo {
	public short netport ;// 2B  如8080
	public short controlport;  //2B		如8999
	public String ip;		//15B 如 192.168.1.11
	public byte nettype;		//1B 如0,1,2分别代表3g，wifi，有线
	public byte dhcp	;	//1B  如 0,1分别代表关闭和打开
	public String displayid;	//64B 字符串类型， 如 157773336616228
	public String domainname;	//32B 字符串类型	如 test168
	public String displayname	;		//32B	字符串类型		如 betters
	public short heartbeat	;		//2B 如 10
	public long fecthweatherinteval	;	//8B	如600
	public byte savetype;	//	1B 如2
	public byte readconfigtype;	//	1B 如2
	//下面是预留字段
	public String passport;  //32B  如 上网帐号32个字母数字下划线的组合。
	public String password;		//  32B  如 对应上网帐号的密码
	public String preip; 	// 4B(预留ip信息)  如 192.168.10.1
	public String premask;	//	4B(预留掩码)	如 255.255.255.0
	public String pregateway;		//  4B(预留网关)	如  192.168.1.1
	@Override
	public String toString() {
		return "ConfigInfo [netport=" + netport + ", controlport="
				+ controlport + ", ip=" + ip + ", nettype=" + nettype
				+ ", dhcp=" + dhcp + ", displayid=" + displayid
				+ ", domainname=" + domainname + ", displayname=" + displayname
				+ ", heartbeat=" + heartbeat + ", fecthweatherinteval="
				+ fecthweatherinteval + ", savetype=" + savetype
				+ ", readconfigtype=" + readconfigtype + ", passport="
				+ passport + ", password=" + password + ", preip=" + preip
				+ ", premask=" + premask + ", pregateway=" + pregateway + "]";
	}

	
}
