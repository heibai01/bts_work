package com.led.player.po;

public class GPSLoopTextPo {
	public int _transparent;
	public int _walkspeed;
	public int _singleline;
	public int _static;
	public int _prolen;
	public String _content;
	public String _wordclr;
	public String _bgclr;
	public int _italic;
	public int _underline;
	public int _height;
	public int _facename;
	public int _weight;				
	public String _begintime; 			
	public GPSLoopTextPo() { }
	
	public GPSLoopTextPo(int _transparent, int _walkspeed, int _singleline,
			int _static, int _prolen, String _content, String _wordclr,
			String _bgclr, int _italic, int _underline, int _height,
			int _facename, int _weight, String _begintime) {
		super();
		this._transparent = _transparent;
		this._walkspeed = _walkspeed;
		this._singleline = _singleline;
		this._static = _static;
		this._prolen = _prolen;
		this._content = _content;
		this._wordclr = _wordclr;
		this._bgclr = _bgclr;
		this._italic = _italic;
		this._underline = _underline;
		this._height = _height;
		this._facename = _facename;
		this._weight = _weight;
		this._begintime = _begintime;
	}

	@Override
	public String toString() {
		return "TextVo [_transparent=" + _transparent + ", _walkspeed="
				+ _walkspeed + ", _singleline=" + _singleline + ", _static="
				+ _static + ", _prolen=" + _prolen + ", _content=" + _content
				+ ", _wordclr=" + _wordclr + ", _bgclr=" + _bgclr
				+ ", _italic=" + _italic + ", _underline=" + _underline
				+ ", _height=" + _height + ", _facename=" + _facename
				+ ", _weight=" + _weight + ", _begintime=" + _begintime + "]";
	}
	public int get_transparent() {
		return _transparent;
	}
	public void set_transparent(int _transparent) {
		this._transparent = _transparent;
	}
	public int get_walkspeed() {
		return _walkspeed;
	}
	public void set_walkspeed(int _walkspeed) {
		this._walkspeed = _walkspeed;
	}
	public int get_singleline() {
		return _singleline;
	}
	public void set_singleline(int _singleline) {
		this._singleline = _singleline;
	}
	public int get_static() {
		return _static;
	}
	public void set_static(int _static) {
		this._static = _static;
	}
	public int get_prolen() {
		return _prolen;
	}
	public void set_prolen(int _prolen) {
		this._prolen = _prolen;
	}
	public String get_content() {
		return _content;
	}
	public void set_content(String _content) {
		this._content = _content;
	}
	public String get_wordclr() {
		return _wordclr;
	}
	public void set_wordclr(String _wordclr) {
		this._wordclr = _wordclr;
	}
	public String get_bgclr() {
		return _bgclr;
	}
	public void set_bgclr(String _bgclr) {
		this._bgclr = _bgclr;
	}
	public int get_italic() {
		return _italic;
	}
	public void set_italic(int _italic) {
		this._italic = _italic;
	}
	public int get_underline() {
		return _underline;
	}
	public void set_underline(int _underline) {
		this._underline = _underline;
	}
	public int get_height() {
		return _height;
	}
	public void set_height(int _height) {
		this._height = _height;
	}
	public int get_facename() {
		return _facename;
	}
	public void set_facename(int _facename) {
		this._facename = _facename;
	}
	public int get_weight() {
		return _weight;
	}
	public void set_weight(int _weight) {
		this._weight = _weight;
	}
	public String get_begintime() {
		return _begintime;
	}
	public void set_begintime(String _begintime) {
		this._begintime = _begintime;
	}
	
}
