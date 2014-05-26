package com.led.player.po;

public class GPSLoopImagePo {
	public String _bgclr;
	public int _description;
	public int _effects;
	public int _stayline;
	public int _txtspeed;
	public String _filepath;
	public int _transparent;
	public String _begintime;
	public GPSLoopImagePo() {
		// TODO Auto-generated constructor stub
	}
	
	public GPSLoopImagePo(String bgclr, int description, int effects, int stayline,
			int txtspeed, String filepath, int transparent, String begintime
			) {
		super();
		this._bgclr = bgclr;
		this._description = description;
		this._effects = effects;
		this._stayline = stayline;
		this._txtspeed = txtspeed;
		this._filepath = filepath;
		this._transparent = transparent;
		this._begintime = begintime;
	}

	@Override
	public String toString() {
		return "ImageVo [bgclr=" + _bgclr + ", description=" + _description
				+ ", effects=" + _effects + ", stayline=" + _stayline
				+ ", txtspeed=" + _txtspeed + ", filepath=" + _filepath
				+ ", transparent=" + _transparent + ", begintime=" + _begintime
				+ "]";
	}
	public String getBgclr() {
		return _bgclr;
	}
	public void setBgclr(String bgclr) {
		this._bgclr = bgclr;
	}
	public int getDescription() {
		return _description;
	}
	public void setDescription(int description) {
		this._description = description;
	}
	public int getEffects() {
		return _effects;
	}
	public void setEffects(int effects) {
		this._effects = effects;
	}
	public int getStayline() {
		return _stayline;
	}
	public void setStayline(int stayline) {
		this._stayline = stayline;
	}
	public int getTxtspeed() {
		return _txtspeed;
	}
	public void setTxtspeed(int txtspeed) {
		this._txtspeed = txtspeed;
	}
	public String getFilepath() {
		return _filepath;
	}
	public void setFilepath(String filepath) {
		this._filepath = filepath;
	}
	public int getTransparent() {
		return _transparent;
	}
	public void setTransparent(int transparent) {
		this._transparent = transparent;
	}
	public String getBegintime() {
		return _begintime;
	}
	public void setBegintime(String begintime) {
		this._begintime = begintime;
	}
	
	
	
}
