package com.led.player.vo;
/**
 * 同步点播 视图类信息
 * @author joychine
 *
 */
public class SyncDemandViewVo {
	/**
	 * ViewAnimator[]  layoutlist 的下标, 其实如果是同步点播，这个 索引值会永远是0， 因为只有一个窗口。
	 *窗口的index 如果是开放式播放，这个属性是 0. 开放式才一个所谓的通用窗来播放图片或视频。
	 */
	public int winIndex;
	
	/**
	 *节目页， 如果是开放式播放，这个属性是 0.开放式才一个 节目页。
	 */
	public int pageIndex;
	/**
	 * 根据MediaFile类得到的自己定义的文件类型。 0,1/2,24 分别表示图片视频和文字
	 */
	public int filetype; 
	/**
	 * ImageVo,VideoVo,TextVo这种。资源元数据。 对应xml中 ProgramData 对象。 只解析了有用的字段
	 * 如果是开放式播放模式，那么这个属性是 null，全局页面和基本页面这个是 赋值
	 */
	public Object obj;  
	/**
	 * "/mnt/sdcar 或者是 /mnt/extsd 这种路径" ，最终发现是文件全路劲
	 */
	public String filepath;
}
