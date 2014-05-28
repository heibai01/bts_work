package com.led.player.aidance;

import android.os.Handler;
/**
 * 开始播放时，携带的播放视频的信息或 图片信息。
 * 包括handler(globalHandler或者是basicHandler),pageIndex,winIndex,fileType,obj（ImageVo,VideoVo,TextVo的一个，开放式给null）,filepath,statyLine(对视频无效，图片文字使用。)
 * Display 时的View
 * @author 1231
 *
 */
public class ViewInfoClass {
	/**
	 * 普通轮播模式，在流程上海市遵循轮播每个节目资源都受固定的时长属性约束，一个节目页下的所有窗口下的资源播放完毕后会 切换到下一个节目页，
	 * 轮播模式下的点播下一个节目页就是上一次的轮播节目页或者上一次没有轮播节目页就循环当前点播的当前节目页。
	 */
	public static final int LOOP_PLAYER_MODE = 0;
	/**
	 * 一个页面下一个通用窗口，该窗口下多个资源文件（图片视频或走马灯），gps轮播的原子是 这个窗口下的资源文件。
	 */
	public static final int SYNC_LOOP_MODE = 1;
	/**
	 * globalHander还是basicHandler, 如果是同步点播这个字段将是null
	 */
	private Handler handler;
	/**
	 * ViewAnimator[]  的下标， 如果是同步点播这个字段将是0
	 *窗口的index 如果是开放式播放，这个属性是 0. 开放式才一个所谓的通用窗来播放图片或视频。
	 */
	private int winIndex;
	/**
	 *节目页， 如果是开放式播放，这个属性是 0.开放式才一个 节目页。如果是同步点播这个将是0
	 */
	private int pageIndex;
	/**
	 * 根据MediaFile类得到的自己定义的文件类型。 0,1/2,24 分别表示图片视频和文字
	 */
	private int filetype; 
	/**
	 * ImageVo,VideoVo,TextVo这种。资源元数据。 对应xml中 ProgramData 对象。 只解析了有用的字段
	 * 如果是开放式播放模式，那么这个属性是 null，全局页面和基本页面这个是 赋值
	 */
	private Object obj;  
	/**
	 * "/mnt/sdcar 或者是 /mnt/extsd 这种路径" ，最终发现是文件全路劲
	 */
	private String filepath;
	
	/**
	 * 播放类型， 0.普通的点播和轮播(都是轮播模式，点播也是轮播一个页面下的窗口)
	 * 1.同步轮播播放(这个模式是纯粹的像点播的时间点触发的点播，只有一个页面一个窗口多个文件)
	 */
	private int playType;
	/**
	 * 视频和同步点播都不会受其约束。不会用该字段， led开放式播放需要此字段判断时长。
	 */
	private int stayline;
	
	/**
	 * 播放类型， 0.普通的点播和轮播(都是轮播模式，点播也是轮播一个页面下的窗口)
	 * 1.同步点播播放(这个模式是纯粹的点播，只有一个页面一个窗口多个文件)定点点播不会切换节目资源
	 */
	public int getPlayType() {
		return playType;
	}
	
	/**
	 * 播放类型， 0.普通的点播和轮播(都是轮播模式，点播也是轮播一个页面下的窗口)
	 * 1.同步点播播放(这个模式是纯粹的点播，只有一个页面一个窗口多个文件)
	 */
	public void setPlayType(int playType) {
		this.playType = playType;
	}
	public void setHandler(Handler mhandler){
		handler = mhandler;
	}
	public void setWinIndex(int mwinIndex){
		winIndex = mwinIndex;
	}
	public void setPageIndex(int mpageIndex){
		pageIndex = mpageIndex;
	}
	/**
	 * ImageWindow 这种vo
	 * 如果是开放式播放模式，那么这个属性是 null
	 */
	public void setObject(Object mobj){
		obj = mobj;
	}
	/**
	 * 根据MediaFile类得到的自己定义的文件类型。
	 */
	public void setFileType(int mfiletype){
		filetype = mfiletype;
	}
	/**
	 * "/mnt/sdcar 或者是 /mnt/extsd 这种路径" ，是给global页面
	 * 绝对文件路径是给Led_xx_xx开放式播放的
	 */
	public void setFilePath(String mfilepath){
		filepath = mfilepath;
	}
	/**
	 * 图片资源的停留时间，视频不受这个影响
	 */
	public void setStayLine(int mstayline){
		stayline = mstayline;
	}
	
	public Handler getHandler(){
		return handler;
	}
	/**
	 * ViewAnimator[] layoutlists  当前页面的窗口集合的下标
	 *窗口的index 如果是开放式播放，这个属性是 0. 开放式才一个所谓的通用窗来播放图片或视频。
	 */
	public int getWinIndex(){
		return winIndex;
	}
	public int getPageIndex(){
		return pageIndex;
	}
	/**
	 * ImageWindow 这种vo
	 * 如果是开放式播放模式，那么这个属性是 null
	 * 
	 * 
	 */
	public Object getObject(){
		return obj;
	}
	public int getFileType(){
		return filetype;
	}
	/**
	 * 如果是 Led开放式播放，这个路径就是绝对的资源全路径了，我艹艹！！！！屎代码<br>
	 * 否则是 媒介跟路径 "/mnt/sdcar 或者是 /mnt/extsd 这种路径"
	 * 
	 */
	public String getFilePath(){
		return filepath;
	}
	public int getStayLine(){
		return stayline;
	}
	@Override
	public String toString() {
		return "ViewInfoClass [handler=" + handler + ", winIndex=" + winIndex
				+ ", pageIndex=" + pageIndex + ", filetype=" + filetype
				+ ", obj=" + obj + ", filepath=" + filepath + ", stayline="
				+ stayline + "]";
	}
	
	
}
