package com.led.player.vo;

public class PlanBrightNess {
	public byte isParentOn;  //总开关 1开0关
	public 	byte isMeOn;
	public byte hour;
	public byte minute;
	public byte bright;
	public int _id;
	public PlanBrightNess() {
		// TODO Auto-generated constructor stub
	}
	public PlanBrightNess(byte isParentOn, byte isMeOn, byte hour, byte minute,	byte bright, int _id) {
		super();
		this.isParentOn = isParentOn;
		this.isMeOn = isMeOn;
		this.hour = hour;
		this.minute = minute;
		this.bright = bright;
		this._id = _id;
	}
	
	
}
