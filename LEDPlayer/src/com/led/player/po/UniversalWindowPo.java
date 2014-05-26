package com.led.player.po;

public class UniversalWindowPo {
	public int _startX;
	public int _startY;
	public int _width;
	public int _height;
	public int _windowrotation;
	public String _bordercolor;
	public int _bordertype;
	public UniversalWindowPo() {
		// TODO Auto-generated constructor stub
	}
	public UniversalWindowPo(int _startX, int _startY, int _width, int _height,
			int _windowrotation, String _bordercolor, int _bordertype) {
		super();
		this._startX = _startX;
		this._startY = _startY;
		this._width = _width;
		this._height = _height;
		this._windowrotation = _windowrotation;
		this._bordercolor = _bordercolor;
		this._bordertype = _bordertype;
	}
	@Override
	public String toString() {
		return "UniversalWindowVo [_startX=" + _startX + ", _startY=" + _startY
				+ ", _width=" + _width + ", _height=" + _height
				+ ", _windowrotation=" + _windowrotation + ", _bordercolor="
				+ _bordercolor + ", _bordertype=" + _bordertype + "]";
	}
	public int get_startX() {
		return _startX;
	}
	public void set_startX(int _startX) {
		this._startX = _startX;
	}
	public int get_startY() {
		return _startY;
	}
	public void set_startY(int _startY) {
		this._startY = _startY;
	}
	public int get_width() {
		return _width;
	}
	public void set_width(int _width) {
		this._width = _width;
	}
	public int get_height() {
		return _height;
	}
	public void set_height(int _height) {
		this._height = _height;
	}
	public int get_windowrotation() {
		return _windowrotation;
	}
	public void set_windowrotation(int _windowrotation) {
		this._windowrotation = _windowrotation;
	}
	public String get_bordercolor() {
		return _bordercolor;
	}
	public void set_bordercolor(String _bordercolor) {
		this._bordercolor = _bordercolor;
	}
	public int get_bordertype() {
		return _bordertype;
	}
	public void set_bordertype(int _bordertype) {
		this._bordertype = _bordertype;
	}
	
	
}
