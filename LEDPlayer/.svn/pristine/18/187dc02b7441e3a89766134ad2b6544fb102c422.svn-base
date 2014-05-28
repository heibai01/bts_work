package com.led.player.vo;
/**
 * 定时开关瓶
 * @author 1231
 *
 */
public class PlanOnOff {
	public byte planType;  //是属于日计划还是属于周计划
	public byte isWeekOn;  //总开关 1开0关 周计划总开关
	public byte isDayOn;    //天计划的总开关
	public 	byte isMeOn;	//一天中某个时段的开关
	public byte hour;
	public byte minute;
	public byte state; //1打开屏幕，0关闭屏幕
//	public byte hourOff;
//	public byte minuteOff;
	
	public int _id; //这个很重要 星期一第一个id是101，以此类推。
	public int dayofweek; //1~7 表示星期一到星期日 , -1表示周计划变日计划，循环周期是1天，每天执行。
	public PlanOnOff() {}
	public PlanOnOff(byte planType, byte isWeekOn, byte isDayOn, byte isMeOn,
			byte hour, byte minute, byte state, int _id, int dayofweek) {
		this.planType = planType;
		this.isWeekOn = isWeekOn;
		this.isDayOn = isDayOn;
		this.isMeOn = isMeOn;
		this.hour = hour;
		this.minute = minute;
		this.state = state;
		this._id = _id;
		this.dayofweek = dayofweek;
	}
	
	@Override
	public String toString() {
		return "PlanOnOff [planType=" + planType + ", isWeekOn=" + isWeekOn
				+ ", isDayOn=" + isDayOn + ", isMeOn=" + isMeOn + ", hour="
				+ hour + ", minute=" + minute + ", state=" + state + ", _id="
				+ _id + ", dayofweek=" + dayofweek + "]";
	}
	
	
}
