package com.led.player.moudle;
/**
 * 在安卓的 /proc/net/arp 文件的信息
 * @author 1231
 *
 */
public class ArpInfo {
	public String ip;
	public String mac;
	public ArpInfo() {
		// TODO Auto-generated constructor stub
	}
	public ArpInfo(String ip, String mac) {
		super();
		this.ip = ip;
		this.mac = mac;
	}
	@Override
	public String toString() {
		return "ArpInfo [ip=" + ip + ", mac=" + mac + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((mac == null) ? 0 : mac.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArpInfo other = (ArpInfo) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equalsIgnoreCase(other.ip))
			return false;
		if (mac == null) {
			if (other.mac != null)
				return false;
		} else if (!mac.equalsIgnoreCase(other.mac))
			return false;
		return true;
	}
	
}
