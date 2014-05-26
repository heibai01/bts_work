package com.led.player.window;

import java.util.ArrayList;
/**
 * 整个xml文件解析出来后的属性。
 * @author 1231
 *
 */
public class LedXmlClass {
	
	private GlobalPage globalpage = new GlobalPage();
	private ArrayList<BasicPage> basicpagelist = new ArrayList<BasicPage>();
	private boolean IsGlobalPage = false;
	
	public void setIsGlobalPage(boolean mtemp){
		IsGlobalPage = mtemp;
	}
	public boolean getIsGlobalPage(){
		return IsGlobalPage;
	}
	public GlobalPage getGlobalPage(){
		return globalpage;
	}
	public void setGlobalPage(GlobalPage mglobalpage){
		globalpage = mglobalpage;
	}
	
	public BasicPage getBasicPage(int i){
		return basicpagelist.get(i);
	}
	
	public Boolean AddBasicPage(BasicPage basicpage){
		return basicpagelist.add(basicpage);
	}
	public ArrayList<BasicPage> getBasicPageList(){
		return basicpagelist;
	}

}
