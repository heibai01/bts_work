package com.led.player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.led.player.aidance.FileTypeHandler;
import com.led.player.aidance.LedProtocol;
import com.led.player.aidance.LedTextInfoVo;
import com.led.player.aidance.PageInfoVo;
import com.led.player.aidance.ParserXml;
import com.led.player.aidance.ViewInfoClass;
import com.led.player.biz.DisplayerBiz;
import com.led.player.biz.GPSSyncLoopParser;
import com.led.player.utils.FileHandler;
import com.led.player.utils.ToastUtil;
import com.led.player.window.BasicPage;
import com.led.player.window.GlobalPage;
import com.led.player.window.ImageVo;
import com.led.player.window.LedXmlClass;
import com.led.player.window.PageInterface;
import com.led.player.window.TextVo;
import com.led.player.window.UniversalWindow;
import com.led.player.window.VideoVo;
/**
 * 
 * @author 1231
 * mark 2014年4月22日 21:10:34 在netReceiver中把播放优先级改成了 demand
 */
public class LedService extends Service{
	private static final boolean DEBUG = false;
	private static final  boolean DEBUG1 = false;
	
	private DisplayerBiz mDisplayerBiz;
	/**
	 *现在播放的类型比如，0,，1，2，3分别代表 开放式播放，外部媒介播放，内部拷贝播放，网络发送播放。
	 * 还有 gps同步轮播就是6了
	 */
	public static int currPlayType = -1; 
	
	/**
	 * 等待播放状态。
	 */
	public static final int DEFAULT_WAIT_PLAY_TYPE = 0;
	/**
	 * 网络发送过来的轮播
	 */
	public static final int NORMAL_LOOP_PLAYE_TYPE = 0X01;
	/**
	 * 网络发过来的点播
	 */
	public static final int NORMAL_DEMAND_PLAY_TYPE = 0X02;

	/**
	 * 拷贝到内部后的轮播
	 */
	public static final int INNER_LOOP_PLAYE_TYPE = 0X03;
	/**
	 * 外部媒介直接轮播
	 */
	public static final int OUTER_LOOP_PLAY_TYPE = 0x04;
	/**
	 * LED开放式播放
	 */
	public static final int LED_OPEN_LOOP_PLAY_TYPE = 0X05;
	/**
	 * gps同步轮播
	 */
	public static final int GPS_SYNC_LOOP_PLAY_TYPE = 0X06;
	/**
	 * 局域网同步轮播播放(主机轮播，从机点播)
	 */
	public static final int LAN_SYNC_LOOP_PLAY_TYPE = 0X07;
	
	/**
	 * 当前任务的xml代表的对象
	 */
	private LedXmlClass loopXmlObj;
	private LedXmlClass demandXmlObj;
	private LedXmlClass plugXmlObj;
	
	/**
	 * 上次的轮播页面页码，， 主要是标记在播放模式切换时的轮播时拨到了第几个页面。
	 */
	private int currentLoopPageIndex; 
	
	private String currDemandNetPath;//当前的点播全路劲。
	public static final int XML_LOOP_TYPE = 1;
	public static final int XML_DEMAND_TYPE = 2;
	public static final int XML_LAN_SYNCLOOP_TYPE = 3;
	public static final int XML_PLUG_TYPE = 4;
	/**
	 * 其实已经没用到他了，只是保持队形 
	 */
	public static final int XML_SYNC_LOOP_TYPE = 4;
	/**
	 * ,插播和点播先存这个值。 插播或点播前的xml对象。
	 */
	private LedXmlClass lastBasicLoopXmlObj;
	private LedXmlClass lastGlobalLoopXmlObj;
	/**
	 * 点播前是否是处于同步轮播模式下
	 */
	private boolean isLastGPSSyncLoop;
//	private boolean isLastLanSyncLoop;
	/**
	 * 是否需要深度搜索。GPS同步时间同步播放第一次启动和点播结束后第一次搜索需要。
	 */
	private boolean isNeedDeepSearch = false;
	private ParserXml parserxml = new ParserXml();
	private GPSSyncLoopParser gpsSyncLoopParser = null;
	
	private GlobalPage globalpage;  //全局页面只允许一个，基础页面可以有多个。
	private ArrayList<BasicPage> basicpagelist;
	/**
	 * ,插播和点播先存这个值。插播或点播前播放页面。
	 */
	private int lastPlayerpage;
	/**
	 * 基本页面数量
	 */
	private int bpageAmount;
	/**
	 * 基本页面可以多个，切换条件是数组值都为1，每一个元素对应一个通用窗口，他的值只有0和1，1表示当前窗口虽有的资源播放完毕，0表示还没播放完。
	 * 都播放完毕了才能切换到下一个页面去。 basic
	 */
	private byte [] PlayerPignArray;
	/**
	 * 目前正在播放的节目页，不管是全局页面还是基本页面  ,插播和点播先存这个值。basic
	 */
	private int currentPageIndex = 0;
	/**
	 * lan 播放指定同步的页面。
	 */
	private int lanSyncPageIndex=0;
	/**
	 * 每一个元素对应一个通用窗，这个数组的值对应这个窗口当前播放的第几个元素
	 */
	private int [] BaseFileIndexArray;
	/**
	 * 每一个元素对应一个通用窗，这个的值对应这个窗口当前播放的第几个元素，下标从0开始。
	 */
	private int [] GlobalFileIndexArray;
	private int mediastorecount = 0;    //这是干嘛用的？？？？？
	
	private Messenger rMessenger;  
	private Messenger mMessenger;
	
	private String [] fileNames;
	private String fileDirName;
	private String interiorMediaPath = "/mnt/sdcard";
	/**
	 * ,插播和点播先存这个值。插播货点播前的播放路劲。
	 */
	private String lastLoopMediaPath; 
	private int demandpage;   //点播第几个页面。
	/**
	 * 媒体文件的媒介路径,如 /mnt/sdcard或/mnt/extsd 或 /mnt/usbhost1，媒体文件可能是视频/图片
	 * ParserAndPlayer(prepath,programPath)中赋值，
	 * CheckMountMedia(String path)赋值
	 */
	/**
	 * 目前的媒介路径
	 */
	private String mediaPath;    
	/**
	 * 目前保存的轮播媒介路劲,为了防止多次点播照成路劲不对的现象。
	 */
	private String currentLoopMediaPath;
	
	private Timer accreditTimer;    //暂时不用了
	private Timer mountedTimer;     //U盘插入后的4秒后定时任务
	private Timer ejectTimer;		//U盘拔出后的定时任务
	private Timer netTimer;			//收到网络发来的文件后，的定时任务。 其实都是线程，只是执行一次就exit
	SDCardReceiver sdCardReceiver;
	NetReceiver netReceiver;
	SyncLoopReceiver syncLoopReceiver ;
	/**
	 * 内部播放，拷贝U盘上的文件到内部sdcard，模式
	 */
	private boolean copyfilefinish = false;
	private boolean hasledfile = false;
	private boolean hasprofile = false;
	private boolean hasmediastore = false;    //干嘛的？？？？？
	private boolean mAccredit = true;
	
	private boolean isdemand = false;
	
	private GlobalThread globalThread;
	private BaseThread baseThread;
	private LedThread ledThread;
	private GlobalHandler globalHandler;
	private BaseHandler baseHandler;
	private LedHandler ledHandler;
	
	private SerialPort pSerialPort;
    private SharedPreferences pathSp;
    /**
     * 文件 filepath
     */
    private SharedPreferences.Editor pathEditor;
    
    private SharedPreferences lanSyncSp;
    private SharedPreferences.Editor lanSyncEditor;
    
    private String [] FlagArray = new String[]{"Y","M","C","S","F"}; ;

	private final IBinder binder = new MyBinder();
	
	/**
	 * 在制卡式拷贝播放时，源文件夹的总大小。
	 */
	public long totalSize;
	/**
	 * 拷贝文件，当前已经完成字节数
	 */
	public long finishSize;
	
	 
	public class MyBinder extends Binder {
       public LedService getService() {
           return LedService.this;
       }
       public Messenger getMessenger(){
    	   return mMessenger;
       }
	}
	
	/**
	 * 注册时间变化监听器,1分钟来一次
	 */
	class SyncLoopReceiver extends BroadcastReceiver{
		private long beginParseXmlTime = 0;
		private long begingCheckTaskTime = 0;
		private long key = 0;
		public SyncLoopReceiver(long key) {
			this.key = key;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
//			long nowTime = System.currentTimeMillis();
//			if (nowTime-beginCountTime<2000&&isStartNow==false) {
//				beginCountTime = nowTime;
//Log.e("---", "-----------时间间隔太短，少于2秒----");				
//				return;
//			}
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)||action.equals("com.led.player.action.syncloopcheck")) {
				long nowTime = System.currentTimeMillis();
				if (nowTime-begingCheckTaskTime<1000) {
					begingCheckTaskTime = nowTime;
					Log.e("LedService", "程总说：广播发送间隔不能小于1秒");
					return;
				}
				if (currPlayType==GPS_SYNC_LOOP_PLAY_TYPE) {
					startCheckSyncLoopThread(intent.getStringExtra("checkTime"));
				}
			}else if (action.equals("com.led.player.action.parser_gpsloopxml")) {
				long nowTime = System.currentTimeMillis();
				if (nowTime-beginParseXmlTime<2000) {
					ToastUtil.setRBShow(context, "please wait a moment for gps_demand", 1);
					beginParseXmlTime = nowTime;
					return;
				} 
				//接受来自Netclient的通知，解析GPS同步轮播的xml并且插入到数据中。
				if (gpsSyncLoopParser ==null) {
					gpsSyncLoopParser = new GPSSyncLoopParser(context);
				}
				//还要检测前一次是否有解析操作在执行……………………我艹！
				if (gpsSyncLoopParser.isParseOn) {
					//停止解析，亲, 如果我这样操作了，解析还是停不下来咋办？ 总会有时间差。。
					ToastUtil.setRBShow(context, "Please wait for last task!", 0);					
					gpsSyncLoopParser.stopParserXml();
				}
				
				new Thread("parseSyncloopxml-t"){
					public void run() {
						try {
							try {
								sleep(1000); //一共3秒 让 上次的解析有时间停止解析。
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							boolean isParseOk = gpsSyncLoopParser.parseSyncLoopXml();
							Log.e("LedService", "解析syncloopxml成功否？："+isParseOk);
							if (isParseOk) {
								Intent gpsIntent = new Intent("PLAY_SYNC_LOOP_PROGRAM");
								gpsIntent.putExtra("path", "mnt/sdcard/syncloop_program/syncloopprogram.xml");
								sendBroadcast(gpsIntent);
								//删除非播放文件
								deleteNeedlessFile();
							}else {
								Log.e("LedService", "--warn: 解析SyncLoopprogram.xml失败~~可能被后一次手动停止前一次！！！！");
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					};
				}.start();
					
				
			}
			
		}

		private void deleteNeedlessFile() {
			gpsSyncLoopParser.filePaths.add("/mnt/sdcard/syncloop_program/syncloopprogram.xml");
			String[] destPaths = new File("/mnt/sdcard/syncloop_program").list();
			File file = null;
			for (String path : destPaths) {
				if (!gpsSyncLoopParser.filePaths.contains("/mnt/sdcard/syncloop_program/"+path)) {
					file = new File("/mnt/sdcard/syncloop_program/"+path);
					Log.e("LedService", "删除文件："+"/mnt/sdcard/syncloop_program/"+path);
					file.delete();
					file = null;
				}
			}
			Log.i("LedService", "所有的路径："+gpsSyncLoopParser.filePaths);
		}
		
	}
	
	private SimpleDateFormat secondFormat = null;
	private SimpleDateFormat hmsFormat = null;
	private long key = System.currentTimeMillis();//时间检测线程是否在运行的检测变化值
	/**
	 * 每隔一秒检测任务一次的 同步点播线程。废弃代码，曾经用来模拟gps同步轮播，
	 */
	class SyncLoopThread extends Thread{
		@Override
		public void run() {
			setName("sync_loop_check_t");
			while (gpsSyncLoopParser==null||(currPlayType==GPS_SYNC_LOOP_PLAY_TYPE&&gpsSyncLoopParser.isParseOn==false)) {//解析……第二次可能来了，第一次还在检测
				try {
					key = System.currentTimeMillis();
					if (LedActivity.isSyncLoopWindowReady) {
						if (hmsFormat==null) {
							hmsFormat = new SimpleDateFormat("HHmmss");
						}
//						String checkTime = timeFormat.format(new Date());
						String checkTime = hmsFormat.format(System.currentTimeMillis());
						checkAndPlaySyncLoopFile(checkTime);
					}else {
						Log.e("检测", "警告！！！！！-----检测~~但是窗口还没准备好。。。");
					}
					//每隔一秒检测是否有需要点播的节目。
					if (secondFormat==null) {
						secondFormat = new SimpleDateFormat("SSS");
					}
					Thread.sleep(1000-Integer.parseInt(secondFormat.format(new Date())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 检测是否有同步点播的文件在这个点上,有就播放这个文件
	 */
	private void checkAndPlaySyncLoopFile(String checkTime) {
//		把自动的DISPLAYER_NEXT_VIEW，改成手动下一个资源
//		BaseSendMsgDisplayView(UniversalWindow universalwindow,int fileindex,int winindex,int playType)
//Log.e("LedService", "checkTime: "+checkTime);			
		
//		Object winRes = mDisplayerBiz.getWindowRes(checkTime,isNeedDeepSearch);//得到当前秒的资源文件对象，可能是视频图片文字中的一种。"201023" 这样。
		Object winRes = mDisplayerBiz.getWindowResByDB(checkTime,isNeedDeepSearch);//得到当前秒的资源文件对象，可能是视频图片文字中的一种。"201023" 这样。
		isNeedDeepSearch = false;
		if(winRes!=null){
			//BaseFileIndexArray[0]意味着窗口下标为0的窗口已经播放到的资源文件索引值，
//			BaseSendMsgDisplayView(universalWindows.get(0), BaseFileIndexArray[0], 0, ViewInfoClass.SYNC_DEMAND_MODE);
			if (LedActivity.isSyncLoopWindowReady) {
				BaseSyncLoopSendMsgDisplayView(winRes);//建立数据库索引表  startTime(时间戳)<-->filePath(短路径)
			}
		}
	}
	public synchronized void startCheckSyncLoopThread(String checkTime) {
//		new SyncLoopThread().start();
		//如果是串口过来的GPS点播
		if (currPlayType==GPS_SYNC_LOOP_PLAY_TYPE) {//隐患，，， 第二次的gps轮播如果窗口与第一次不同，这线程程可是一直在运行着。也没事isSyncLoopWindowReady还判断着了。
			LedService.this.key = System.currentTimeMillis();
			if (LedActivity.isSyncLoopWindowReady) {
				if (hmsFormat==null) {
					hmsFormat = new SimpleDateFormat("HHmmss");
				}
//				String checkTime = hmsFormat.format(System.currentTimeMillis());
				if (checkTime==null) {
					Log.e("LedService", "---------------初始刚解析完gps同步播放的xml,不是来自串口GPS数据------------------");
					checkTime = hmsFormat.format(System.currentTimeMillis());
				}
				checkAndPlaySyncLoopFile(checkTime);
			}else {
				Log.e("检测", "警告！！！！！-----检测~~但是窗口还没准备好。。。");
			}
			//每隔一秒检测是否有需要点播的节目。
		}
		
	}
	/**
	 * 显示指定的资源和view
	 * @param universalwindow
	 * @param mediaPath
	 * @param filePath
	 */
	private void BaseSyncLoopSendMsgDisplayView(Object winRes) {
		ViewInfoClass viewinfoclass = new ViewInfoClass();
//		viewinfoclass.setFileType(universalwindow.getPlayerList().get(fileindex));//ProType 这个字段 xml中
		viewinfoclass.setHandler(null);//给null都行啊。 都手动指定下一个文件了，这个handler其实就没用到了
String fileSimpleName = "";			
		if (winRes instanceof ImageVo) {
			viewinfoclass.setObject(winRes);
			fileSimpleName = ((ImageVo)viewinfoclass.getObject()).getFilePath();
			viewinfoclass.setFileType(0);
		}else if (winRes instanceof VideoVo ) {
			viewinfoclass.setObject(winRes);
			fileSimpleName = ((VideoVo)viewinfoclass.getObject()).getFilePath();
			viewinfoclass.setFileType(2);
		}else if (winRes instanceof TextVo) {
			viewinfoclass.setObject(winRes);
			viewinfoclass.setFileType(24);//ProType 这个字段 xml中
		}
		
		viewinfoclass.setWinIndex(0); //用不到的参数。。。
		viewinfoclass.setPageIndex(0); //用不到的参数。。。
		viewinfoclass.setFilePath(mediaPath);
		viewinfoclass.setPlayType(ViewInfoClass.SYNC_LOOP_MODE);
		
Log.i("当前窗口同步点播", "当前窗口同步点播，需要的资源文件是走马灯？："+(mediaPath+"/"+fileSimpleName));	
		Message msg = new Message();
    	msg.obj = viewinfoclass;
    	msg.arg1 = 1;//基本页
    	msg.what = LedProtocol.DISPLAYER_VIEW;
    	try {
			rMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	
	}
  
	/**
	 * 创建同步点播播放需要的窗口
	 * @param basicPage  需要手动构建一个此对象
	 */
	public void sendMsgCreateSyncDemandWindow(BasicPage  basicPage,int playType){
		PageInfoVo pageInfoVo = new PageInfoVo();
		Message msg = new Message();
		ArrayList<Integer> winTypeList= basicPage.getWindowList();  //实际情况只有一个窗口。。。
		for(int i = 0;i < winTypeList.size();i++){
			switch(winTypeList.get(i)){
			case 0://windowType 0 代表通用窗口。
				ArrayList<UniversalWindow> universalwindowlist = basicPage.getUniversalWindowList(); 
				pageInfoVo.setObject(universalwindowlist);
				pageInfoVo.setWinCount(universalwindowlist.size()); //winCount为1,但是obj为null是Led开放式播放。
				break;
			}
		}
		pageInfoVo.setWindowlist(winTypeList);
		pageInfoVo.setLedTextInfo(null);//开放式播放这个赋值的。
		pageInfoVo.setHandler(null);
		msg.arg1 = 1; //基本页
		Bundle data = new Bundle();
		data.putInt("playType", playType);
		msg.setData(data);
		msg.what = LedProtocol.CREATE_WINDOW;
		msg.obj = pageInfoVo;
		
		try {
			rMessenger.send(msg);
			if(DEBUG)Log.i("Thread", "&&&&&&&&& sunThread send msg CREATE_WINDOW to activity");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 播放全局页面线程：
	 * 全局页面：只有一个节目页循环播放，
	 * @author 1231
	 *
	 */
	class GlobalThread extends Thread{
		private GlobalPage globalPage;
		
		public GlobalThread(GlobalPage globalPage){
			this.globalPage = globalPage;
		}
		public void run(){
			setName("GlobalThread"+((int)Math.random()*2));
			Looper.prepare();
			globalHandler = new GlobalHandler(Looper.myLooper());
			if(globalPage.getUniversalWindowList().size() > 0){
				SendMsgCreateWindow(globalPage,true,ViewInfoClass.LOOP_PLAYER_MODE);
			
			}
			
			Looper.loop();
			if(DEBUG)Log.i("GlobalThread", "&&&&&&&&& GlobalThread after Looper.loop()");
		}
	}
	
	class GlobalHandler extends Handler{
		private ArrayList<UniversalWindow> universalwindowlist;
		public GlobalHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case LedProtocol.BEGIN_DISPLAYER_VIEW:
				if(true)Log.i("GlobalHandler", "Global&&&&&&&&& BEGIN_DISPLAYER_VIEW");
				init();
				
				//遍历必然照成每个窗口出现的时间不一样，延迟，看到窗口先后出现，不会一闪就全部出现了。
				UniversalWindow universalWindow = null;
				for(int winindex = 0;winindex < universalwindowlist.size();winindex++){
					universalWindow = universalwindowlist.get(winindex);
					if(universalWindow.getPlayerList().size()!=0){
						GlobalSendMsgDisplayView(universalWindow,GlobalFileIndexArray[winindex],winindex);
					}
				}
				break;
			case LedProtocol.DISPLAYER_NEXT_VIEW:
				int winindex = msg.arg2;
				if(msg.obj!=null){
					Bitmap bitmap = (Bitmap)msg.obj;
					if(bitmap!=null&&!bitmap.isRecycled()){
						if(true)Log.i("GlobalHandler", "&&&&&*****^^^^^$$$ bitmap.recycle()");
//						bitmap.recycle(); 
						bitmap = null;
					}
				}
				GlobalSendMsgDisplayView(universalwindowlist.get(winindex),GlobalFileIndexArray[winindex],winindex);
				break;
			}
		}
		
		public void init(){
			if(DEBUG)Log.i("GlobalHandler", "&&&&&&&&& init()");
			universalwindowlist = globalpage.getUniversalWindowList();
			GlobalFileIndexArray  = new int[universalwindowlist.size()];
		}
	}
	
	/**
	 * 基本页面的 启动线程。
	 * @author 1231
	 *
	 */
	class BaseThread extends Thread{
		private ArrayList<BasicPage> basicpagelist;
		private int playType;
		
		public BaseThread(ArrayList<BasicPage> mbasicpagelist,int playType){
			basicpagelist = mbasicpagelist;
			this.playType = playType;
		}
		public void run(){
			setName("BaseThread");
			Looper.prepare();
			baseHandler = new BaseHandler(Looper.myLooper());
			if(bpageAmount > 0){
Log.e("当前准备创建窗口了	", "创建窗口时的currentPageIndex:"+currentPageIndex);			
				if (currentPageIndex>=basicpagelist.size()) {
					return;
				}
				
				SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);//数组越界的错误
			}
			Looper.loop();
		}
	}
	
	class BaseHandler extends Handler{
		private ArrayList<UniversalWindow> baseuniversalwindowlist;
		public BaseHandler(Looper looper){
			super(looper);
		}
		
		/**
		 * 初始化基础线程的一些工作。
		 * PlayerPignArray = new byte[baseuniversalwindowlist.size()];
			BaseFileIndexArray  = new int[baseuniversalwindowlist.size()];
		 */
		public void BaseInit(){
			PlayerPignArray = null;
			BaseFileIndexArray = null;
//Log.i("当前的页面下标", "BaseHandler--BaseInit()当前的页面下标："+currentPageIndex);
			BasicPage basepage = basicpagelist.get(currentPageIndex);
			baseuniversalwindowlist = basepage.getUniversalWindowList();
			PlayerPignArray = new byte[baseuniversalwindowlist.size()];
			BaseFileIndexArray  = new int[baseuniversalwindowlist.size()];
		}
		
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case LedProtocol.DISPLAYER_NEXT_VIEW://如果是同步点播不会让自动执行到这，都是我手动过来
				if(msg.arg1==currentPageIndex){//arg1是pageIndex,arg2是winIndex，都是上次传来传去的那个数。
					int winindex = msg.arg2;
					if(msg.obj!=null){
						Bitmap bitmap = (Bitmap)msg.obj;
						if(bitmap!=null&&!bitmap.isRecycled()){
							if(true)Log.i("BaseHandler", "&&&&&*****^^^^^$$$ bitmap.recycle()");
//							bitmap.recycle();
							bitmap = null;
						}
					}
					
					if(DEBUG)Log.i("LedService", "%%%%%%%%%%"+"window index :"+winindex+" receive current time "+SystemClock.uptimeMillis());
					BaseSendMsgDisplayView(baseuniversalwindowlist.get(winindex),BaseFileIndexArray[winindex],winindex,ViewInfoClass.LOOP_PLAYER_MODE);
				}else {
					Log.i("骇人听闻", "当前 msg.arg1!=currentPageIndex 真是奇葩啊。。。");
				}
				
				break;
			case LedProtocol.BEGIN_DISPLAYER_VIEW://同步播放在第一次创建窗口时才会到这里来。
				int playType = msg.arg1;//播放模式回传，虽然现在还没用到。
				if(DEBUG)Log.i("BaseHandler", "&&&&&&&&& handler BEGIN_DISPLAYER_VIEW");
				BaseInit();
				if(baseuniversalwindowlist.size()!=0){
//	Log.i("开始播放", "baseuniversalwindowlist.size()!=0---BEGIN_DISPLAYER_VIEW----开播了");		
					int count = 0;//给一个页面下的空窗口做统计。
					for(int winindex = 0;winindex < baseuniversalwindowlist.size();winindex++){
						if(baseuniversalwindowlist.get(winindex).getPlayerList().size()!=0){	//延迟  mark
							BaseSendMsgDisplayView(baseuniversalwindowlist.get(winindex),BaseFileIndexArray[winindex],winindex,playType);
//Log.i("窗口中有文件", "BEGIN_DISPLAYER_VIEW------窗口中有文件----播放");
						}else{
							PlayerPignArray[winindex] = 1;
							count++;
//Log.i("当前界面页面下", "当前节目页下的窗口是空文件资源(/ □ \\"+baseuniversalwindowlist.get(i));	
							continue;
						}
					}
					if (count==baseuniversalwindowlist.size()) {
						currentPageIndex = ++currentPageIndex>=bpageAmount?0:currentPageIndex;
						SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);
						//分析：如果点播总页面数是1，但是这1页下窗口中没文件---->不会启动播放流程。所以这里一定是多个页面。
						// 但是点播了一个空白内容的页面反而去播放 点播总节目的下一个页面…………
						if(currPlayType == NORMAL_LOOP_PLAYE_TYPE){
							currentLoopPageIndex++; //轮播页面要+1
							Log.e("LedService", "当前页面难道是轮播？");
						}
						Log.e("LedService", "指定点播的页面是有窗口没资源文件，so，我尝试播放点播页面的下一个页面");		//joychine注意：点播页面有窗口无资源会尝试播放下一页，不会死循环。	
					}
				}else{//页面下无窗口。当通用窗口集合size是0，走这里？？？？？？？？？？？  
Log.e("当通用窗口集合size是0", "-----页面下无窗口，通用窗口集合size是0-----------当前页面是："+currentPageIndex+("还要加1判断是否是最后一个页面"))	;				
					if((++currentPageIndex)>=bpageAmount){//防止有空节目页时一直停留在空节目页上
						currentPageIndex = 0;
						
					}else {
						Log.e("当前页面", "当前页面不是最后一个页面，肿么办");
					}
					
					if(bpageAmount!=1){    //防止只有一个节目而且没有通用窗时会死循环，用basepage里面windowlist做判断
						if(isdemand){
							if(lastBasicLoopXmlObj!=null){//重新创建窗口，准备重新播放的条件即可。
								currentPageIndex = lastPlayerpage;
								InitWinInfo(lastBasicLoopXmlObj);
								mediaPath = lastLoopMediaPath;
								
//								if(globalpage.getUniversalWindowList().size() > 0){//也要启动全局页面。
//									SendMsgCreateWindow(globalpage,true,playType);
//								}
								if (judgeIsOnePageOK(globalpage)) {
									SendMsgCreateWindow(globalpage,true,playType); //点播页面全是基本页可以忽略这个是来自点播节目的风险
								}
							}
							isdemand = false;
							Log.e("LedService", "一定不可能执行的代码，代吗执行到这里表示，点播某一个页面，但是页面下面没窗口，必须保证点播页面的完整！！！");
						}
						Log.e("LedService", "点播的页面下没任何窗口。。。接着播放轮播任务");
						SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);//接着播放指定页面。
					}else {
						Log.e("当前基本页总数", "当前的基本页总数是1个："+bpageAmount); //理论上这里不被执行。页面下无窗口&&一个页面 是不可能运行的。
					}
					
				}
				break;
			}
		}
	}
	

	
	/**
	 * 播放 led_xx_xx格式的文件资源的 线程，专用。
	 * @author 1231
	 */
	class LedThread extends Thread{
		
		private String parentPath; //插入的媒介 根目录如/mnt/extsd 或者是 /mnt/usbhost1
		private String fileDirName;	//是led_xx_xx目录， 存在。。。才会启动这个线程。
		
		public LedThread(String filepath,String fileDirName){
			this.parentPath = filepath;
			this.fileDirName = fileDirName;
		}
		
		public void run(){
			setName("Led_xx_xx_Thread");
if(DEBUG)Log.i("LedThread", "&&&&&&&&& LedThread have start");
			Looper.prepare();
			ledHandler = new LedHandler(Looper.myLooper());
			
			int i = fileDirName.indexOf("_");
		    int j = fileDirName.indexOf("_", i+1);
		    fileNames = GetFileNames(parentPath+"/"+fileDirName);
		    if(DEBUG)Log.i("LedService", "&&&&&&&&& fileNames [0] = "+ fileNames[0]);
		    
		    
		    LedTextInfoVo ledtextinfo = null;
		    File TxtFile = null;
		    String TxtFileName = null;
		    for(int index = 0;index < fileNames.length;index++){
		    	String mimetype = FileTypeHandler.getMimeTypeForFile(fileNames[index]);
				int filetype = FileTypeHandler.getFileTypeForMimeType(mimetype);
				if(FileTypeHandler.isTxtFileType(filetype)){
					TxtFile = new File(parentPath+"/"+fileDirName+"/"+fileNames[index]);
					//只能放一个txt的走马灯文件。
					TxtFileName = fileNames[index];
					break;
				}
		    }
		    if(TxtFileName!=null&&TxtFile!=null){
		    	ledtextinfo = new LedTextInfoVo();
		    	for(int m = 0;m < FlagArray.length;m++){
			    	int pos = TxtFileName.indexOf(FlagArray[m], 0);
			    	if(pos!=-1){
			    		int pos1 = TxtFileName.indexOf("_", pos);
			    		if(pos1==-1){
			    			pos1 = TxtFileName.indexOf(".", pos);
			    		}
			    		switch(m){
			    		case 0:
			    			ledtextinfo.setStartY(Integer.parseInt(TxtFileName.substring(pos+1, pos1)));
			    			break;
			    		case 1:
			    			ledtextinfo.setMove(Integer.parseInt(TxtFileName.substring(pos+1, pos1)));
			    			break;
			    		case 2:
			    			ledtextinfo.setColor(Integer.parseInt(TxtFileName.substring(pos+1, pos1)));
			    			break;
			    		case 3:
			    			ledtextinfo.setSize(Integer.parseInt(TxtFileName.substring(pos+1, pos1)));
			    			break;
			    		case 4:
//			    			ledtextinfo.setFaceName(Integer.parseInt(TxtFileName.substring(pos+1, pos1)));  注释掉，现在用系统默认字体了。
			    			break;
			    		}
			    		
			    	}
			    	
			    }
			    ledtextinfo.setSurWidth(Integer.parseInt(fileDirName.substring(i+1, j)));
			    try {
			    	String hourseContent = getContent(parentPath+"/"+fileDirName+"/"+TxtFileName);
			    	String text = hourseContent.substring(0, hourseContent.length()); //其实字符0和1都可以…………
					ledtextinfo.setTextContent(text);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    
	    	PageInfoVo pageInfoVo = new PageInfoVo();
	    	
	    	pageInfoVo.setHandler(ledHandler);
	    	pageInfoVo.setStartX(0);
	    	pageInfoVo.setStartY(0);
	    	pageInfoVo.setWidth(Integer.parseInt(fileDirName.substring(i+1, j)));
	    	pageInfoVo.setHeigth(Integer.parseInt(fileDirName.substring(j+1, fileDirName.length())));
	    	pageInfoVo.setWinCount(1); //设置通用窗口的数量 全局页面播放，只能有一个全局页面。
	    	pageInfoVo.setObject(null); //设置通用窗口集合  由于在Activity中与别的解析用同全局页面复用，这里来判断是属于 开放式播放还是全局页面播放
	    	pageInfoVo.setLedTextInfo(ledtextinfo);
	    	Message msg = new Message();
	    	msg.what = LedProtocol.CREATE_WINDOW;
	    	msg.arg1 = 0;   //和globalthread传递的what一个值。0是全局页面/开放式页面 ,1是基本页面
	    	msg.obj = pageInfoVo;
	    	try {
				rMessenger.send(msg);
				if(DEBUG)Log.i("LedThread", "&&&&&&&&& LedThread send msg CREATE_WINDOW to activity");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			Looper.loop();
			if(DEBUG)Log.i("LedThread", "&&&&&&&&& LedThread Looper.loop()");
		}
		
		//不理解， 有问题，如输入"联通"
		private String getContent(String filename) throws UnsupportedEncodingException, IOException{
			String content = "文本解析错误，这是默认提示文字";
			
//			 File file = new File(filename);
//	          InputStream in= new java.io.FileInputStream(file);
//	          byte[] b = new byte[3];
//	          in.read(b);
//	          in.close();
//	          if(DEBUG)Log.i("getContent", "&&&&&&&&&############## b[0] "+b[0]+"b[1] "+b[1]+"b[2] "+b[2]);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
			int p = (bis.read() << 8) + bis.read();
			bis.close();

	          //if (b[0] == (byte) 0xEF  && b[1] ==  (byte) 0xBB   && b[2] == (byte) 0xBF ){
	          if (p==0xefbb){ //判断utf-8？？？还要有bom？
	        	  
	          }
	          else{
	        	  if(DEBUG)Log.i("getContent", "&&&&&&&&& change to UTF_8");
	        	  try {
		 				BufferedReader buf = null;
		 				OutputStreamWriter osw=null;		
		 				String str = null;
		 				byte[] d=new byte[3];
		 				d[0] = (byte) 0xEF;
		 				d[1] = (byte) 0xBB;
		 				d[2] = (byte) 0xBF;
		 				String allstr = new String(d);
//		 				
//		 				byte[] c=new byte[2];
//		 		        c[0]=0x0d;
//		 		        c[1]=0x0a;
//		 		        String t=new String(c);
		 		        
		 		        buf=new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GBK"));
		 				while((str = buf.readLine()) != null){
//		 					allstr=allstr+str+t;
		 					allstr=allstr+str;
		 				}
		 				
		 				buf.close();		

		 				osw =new OutputStreamWriter(new FileOutputStream(filename),"UTF-8");
		 				osw.write(allstr); 
		 				osw.close();
		 			}
	 				 catch (java.io.FileNotFoundException e) 
	 	             {
	 	                 Log.d("TestFile", "The File doesn't not exist.");
	 	             } 
	 	             catch (IOException e) 
	 	             {
	 	                  Log.d("TestFile", e.getMessage());
	 	             }
	          }
	          
				 
				FileInputStream fin = new FileInputStream(filename);            
				// FileInputStream fin = openFileInput(fileName);            
				// FileInputStream            
				int length = fin.available();            
				byte[] buffer = new byte[length];            
				fin.read(buffer);           
				content = EncodingUtils.getString(buffer, "UTF-8");//// 
				fin.close();
             System.out.println("解析到的文字内容："+content.charAt(5));
            
			return content;
		}
	}
	
	class LedHandler extends Handler{
		private int ledFileIndex = 0;
		
		public LedHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case LedProtocol.DISPLAYER_NEXT_VIEW:
				if(DEBUG)Log.i("LedThread", "&&&&&&&&& LedThread handler message");
				if(msg.obj!=null){
					Bitmap bitmap = (Bitmap)msg.obj;
					if(bitmap!=null&&!bitmap.isRecycled()){
						if(true)Log.i("LedHandler", "&&&&&*****^^^^^$$$ bitmap.recycle()");
//						bitmap.recycle(); 
						bitmap = null;
					}
				}
				if(ledFileIndex < fileNames.length){
					LedSendMessage(fileNames,fileDirName,ledFileIndex);					
					ledFileIndex++;
				}else{
					ledFileIndex = 0;
					LedSendMessage(fileNames,fileDirName,ledFileIndex);
					ledFileIndex++;
				}
				break;
			case LedProtocol.BEGIN_DISPLAYER_VIEW:
				if(ledFileIndex < fileNames.length){
					LedSendMessage(fileNames,fileDirName,0);					
					ledFileIndex++;
				}
				break;
			}
		}
	}
	
	/**
	 * 全局页面发送消息显示view
	 * @param universalwindow
	 * @param fileindex   GlobalFileIndexArray数组中的 GlobalFileIndexArray[winindex]对应的值
	 * @param winindex	  GlobalFileIndexArray数组的下标。也就是 第几个窗口，从0开始。
	 */
	public void GlobalSendMsgDisplayView(UniversalWindow universalwindow,int fileindex,int winindex){
		if(DEBUG)Log.i("GlobalHandler", "&&&&&&&&& GlobalSendMsgCreateView()  window "+winindex+"fileindex"+fileindex);
		if(fileindex < universalwindow.getPlayerList().size()){
			ViewInfoClass viewinfoclass = new ViewInfoClass();
			viewinfoclass.setFileType(universalwindow.getPlayerList().get(fileindex));
			viewinfoclass.setHandler(globalHandler);
//System.out.println("文件类型："+universalwindow.getPlayerList().get(fileindex));			
			switch(viewinfoclass.getFileType()){
			case 0://图片 类型
				viewinfoclass.setObject(universalwindow.getImageVo(universalwindow.getImageCountAdd()));
				break;
			case 1:
			case 2: //视频类型
				viewinfoclass.setObject(universalwindow.getVideoVo(universalwindow.getVideoCount()));
				break;
			case 24: //走马灯类型
				viewinfoclass.setObject(universalwindow.getTextVo(universalwindow.getTextCount()));
				break;
			}
			viewinfoclass.setWinIndex(winindex);
			viewinfoclass.setPageIndex(0);
			viewinfoclass.setFilePath(mediaPath);
			viewinfoclass.setPlayType(ViewInfoClass.LOOP_PLAYER_MODE);
			
			Message msg = new Message();
	    	msg.obj = viewinfoclass;
	    	msg.arg1 = 0;
	    	msg.what = LedProtocol.DISPLAYER_VIEW;
	    	if(DEBUG)Log.i("GlobalHandler", "&&&&&&&&&   window "+winindex+"  send msg DISPLAYER_VIEW to activity");
	    	try {
//Log.i("信息", viewinfoclass.toString())	    		;
//Log.i("VideoVo", "videoVo:"+viewinfoclass.getObject());
				rMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	    	
	    	GlobalFileIndexArray[winindex]++;
		}else if (universalwindow.getPlayerList().size()>0) {//以前这个没判断 直接else，导致 当有窗口没资源时出问题。
			Log.e("fileindex", "fileindex:"+fileindex);  //mark 这里 来的速度非常快。。。。ff
			Log.e("universalwindow.getPlayerList().size()", "---:"+universalwindow.getPlayerList().size());
			GlobalFileIndexArray[winindex] = 0;

			GlobalSendMsgDisplayView(universalwindow,GlobalFileIndexArray[winindex],winindex);
		}
	}
	
	/**
	 * 基本页面发送消息给LedActivty让他显示View。
	 * @param universalwindow
	 * @param fileindex
	 * @param winindex
	 * @param playType 播放类型。普通轮播，同步点播
	 */
	public void BaseSendMsgDisplayView(UniversalWindow universalwindow,int fileindex,int winindex,int playType){
		if(fileindex < universalwindow.getPlayerList().size()){
			ViewInfoClass viewinfoclass = new ViewInfoClass();
			viewinfoclass.setFileType(universalwindow.getPlayerList().get(fileindex));//ProType 这个字段 xml中
			viewinfoclass.setHandler(baseHandler);
String fileSimpleName = "";			
			switch(viewinfoclass.getFileType()){
			case 0:
				viewinfoclass.setObject(universalwindow.getImageVo(universalwindow.getImageCountAdd()));
				fileSimpleName = ((ImageVo)viewinfoclass.getObject()).getFilePath();
				break;
			case 1:
			case 2:
				viewinfoclass.setObject(universalwindow.getVideoVo(universalwindow.getVideoCount()));
				fileSimpleName = ((VideoVo)viewinfoclass.getObject()).getFilePath();
				break;
			case 24:
				viewinfoclass.setObject(universalwindow.getTextVo(universalwindow.getTextCount()));
				break;
			}
			viewinfoclass.setWinIndex(winindex);
			viewinfoclass.setPageIndex(currentPageIndex);
			viewinfoclass.setFilePath(mediaPath);
			viewinfoclass.setPlayType(playType);
			
//Log.i("当前窗口需要的资源文件是", "当前窗口需要的资源文件是走马灯？："+(mediaPath+"/"+fileSimpleName))	;	
			Message msg = new Message();
	    	msg.obj = viewinfoclass;
	    	msg.arg1 = 1;//基本页
	    	msg.what = LedProtocol.DISPLAYER_VIEW;
	    	if(DEBUG)Log.i("BaseHandler", "&&&&&&&&&   window "+winindex+"  send msg DISPLAYER_VIEW to activity");
	    	try {
				rMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	    	
	    	BaseFileIndexArray[winindex]++;
//Log.i("点播也会小于总数老卡", "----点播时fileindex也会小于窗口资源总数啦---------");	    	
		}else{
			//如果上次点播前没有轮播任务，断电重启后需要急着循环播放这个点播的节目页
			int rememberPageIndex = pathSp.getInt("rememberPageIndex", -1);
			boolean isExternalPlay = pathSp.getBoolean("isExternalPlay", false); //。外部播放完了后(切掉U盘，原来demand存入持久层的点播要继续点播)
			if (rememberPageIndex!=-1&&isExternalPlay==false) {
				Log.e("当前页已经被记住", "当前页已经被记住了，循环播放当前页");
				currentPageIndex = rememberPageIndex;
				BaseFileIndexArray[winindex] = 0;
				BaseSendMsgDisplayView(universalwindow,BaseFileIndexArray[winindex],winindex,playType);
Log.e("当前的playtype", "当前的playtype:"+playType);				
				return;
			}
//Log.i("当前窗口播放完毕", "当前窗口播放完毕，需要判断当前页面下的所有窗口是否都播放完毕了。")			;
			if(bpageAmount == 1&&!isdemand){//当播放完毕判读属于基本页的轮播。才一页就不用切换。 , 如果是同步点播，就只有一个页面。
				Log.i("LedService", "1个即本页面的数量："+bpageAmount);				
				BaseFileIndexArray[winindex] = 0; //如果才一个页面，，和全局页面差不多，不用切换成下一页了。
				BaseSendMsgDisplayView(universalwindow,BaseFileIndexArray[winindex],winindex,playType);
			}else{//当不止一个播放页或者说可能是插播或点播时，这是个时候当前基本页面状态要 置为标志 已经播放完了。
				if(PlayerPignArray[winindex] != 1){
					PlayerPignArray[winindex] = 1;
				}
				
				if(!IsAllOne(PlayerPignArray)){ //一个页面下的窗口尚且没有播放完毕，无论是点播还是普通轮播
					BaseFileIndexArray[winindex] = 0;
					BaseSendMsgDisplayView(universalwindow,BaseFileIndexArray[winindex],winindex,playType);
					
				}else{//窗口都播放完了。。。
					if(isdemand){
Log.e("当前是点播页面里的窗口资源都播放完毕", "currentPageIndex:"+currentPageIndex+"----点播页面bpageAmount总的数量："+bpageAmount)			;			
						if(lastBasicLoopXmlObj!=null){//重新创建窗口，准备重新播放轮播的条件即可。
Log.e("哈哈哈", "----点播完毕结束了lastBasicLoopXmlObj不为null，上次轮播播到了下标是"+lastPlayerpage+"的页面！")							;
					System.out.println("播放完毕这个时候的golobalhandler："+globalHandler);
					
							StopPlayer();  //1
							currentPageIndex = lastPlayerpage;
							InitWinInfo(lastBasicLoopXmlObj);
							currPlayType = pathSp.getInt("CurrPlayType", NORMAL_LOOP_PLAYE_TYPE);
							mediaPath = lastLoopMediaPath;
System.out.println("上次保存的mediaPath:"+mediaPath);							//记住这里等待修改 mark
							
							if(judgeIsOnePageOK(globalpage)){
								 globalThread = new GlobalThread(globalpage);
								 globalThread.start();
//								SendMsgCreateWindow(globalpage,true);
								
								
							}
							if (judgeIsBasicProgramOk()) {
								 baseThread = new BaseThread(basicpagelist,playType); //2 如果1和2组合代替下面一句
								 baseThread.start();
//								SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);//这个可以重用后一次的handler但是全局页面不行，点播可都是基本页啊。
							}else {
								lastBasicLoopXmlObj = null;
								//接着播放啊，不能UI不变化了，继续切换点播的当前页面
								Log.i("由于基本页面的轮播给了空内容", "由于轮播给了空内容，接着播放点播啊，不能UI不变化了，继续切换点播的当前页面，而且把当前点播页面记住！");
								Log.i("dfd", "~~~~~~上次的点播路劲："+currDemandNetPath+"界面如果不动了，就对了，已处理，记忆点播了");
								//下面……哎呀，对象都变了…………
								/*patheditor.putString("netXmlPath", currDemandNetPath);
								patheditor.putInt("rememberPageIndex", demandpage);
								patheditor.commit();
								SendMsgCreateWindow(basicpagelist.get(--currentPageIndex),false);*/
							}
							
						}else { //点播结束了，上次没有轮播任务。
							if (isLastGPSSyncLoop) {
								//恢复上次的同步轮播
								resumeGPSSyncLoop();
								return;
							}
							Log.e("-----", "上次没有任何基本页面轮播播放任务，这次点播结束呢，怎么办呢？不处理就是接着播放我这个点播中的任务了");
							//既然没有轮播任务就把这次的点播任务存入持久层当作“轮播任务”执行，断电后可以仍然记住这个页面(当前页面，无法定位到当前页面的窗口下去，太复杂)播放，
							pathEditor.clear();
							pathEditor.putString("netXmlPath", interiorMediaPath+"/demand_program/demandprogram.xml");
							pathEditor.putInt("rememberPageIndex", currentPageIndex);
							pathEditor.putInt("CurrPlayType", NORMAL_DEMAND_PLAY_TYPE);
							pathEditor.commit();
							//还必须要一直循环这个 点播页，而不是循环这个点播节目所有页。
							SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);
						}
						isdemand = false;
					}else{//不是点播，还是轮播时。//loop play
						if(++currentPageIndex>=bpageAmount){ //此处状态：当前页窗口中的文件播放完毕，当前也是总页数的最后一页。
							currentLoopPageIndex = 0;
							currentPageIndex = 0;  //当前页窗口中的文件播放完毕，当前也是总页数的最后一页,而且不是插播，不是点播，就是轮播， 页面索引 mark
						}else {
							currentLoopPageIndex = currentPageIndex;  //给上次的轮播时的页面索引赋值 mark
						}
						
						SendMsgCreateWindow(basicpagelist.get(currentPageIndex),false,playType);
					}
					
				}
			}
			
		}
		
	}
	
	/**
	 * 通知局域网机器播放指定页面(点播指定页面)
	 * @param currentPageIndex 
	 */
	private void notifyLocalDevicePlayCurr(final int currentPageIndex) {
		Log.i("LedService", "------------------我是主机，搜索并发送消息给局域网内的设备!!!--------------------");
		new Thread("notify_local_dev_thread"){
			public void run() {
				DatagramSocket udpSocket = null;
				DatagramPacket pack = null;
				try {
					udpSocket = new DatagramSocket();
					byte[] data = createLocalSyncByte(currentPageIndex); 
					pack = new DatagramPacket(data,data.length,InetAddress.getByName("255.255.255.255"),4096);
					for (int i = 0; i < 5; i++) {
						udpSocket.send(pack);
						sleep(100);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					if (udpSocket!=null) {
						udpSocket.close();
					}
				}

			};
		}.start();
		
	}
	
	private byte[] createLocalSyncByte(int pageIndex){
		byte[] data = new byte[10];
		data[0] = (byte) 0xaa;
		data[1] = 0x55;
		data[2] = (byte) ((10 >> 24) & 0xff);
		data[3] = (byte) ((10 >> 16) & 0xff);
		data[4] = (byte) ((10 >> 8) & 0xff);
		data[5] = (byte) (10 & 0xff);
		data[6] = 14;
		data[7] = 1;
		data[8] = (byte) pageIndex;
		for (int i = 0; i < data.length-1; i++) {
			data[9]+=data[i];
		}
		return data;
	}
	/**
	 * 恢复点播前的 同步轮播任务
	 */
	private void resumeGPSSyncLoop() {
		new Thread("点播完毕,恢复gps轮播任务线程"){
			@Override
			public void run() {
				currPlayType = GPS_SYNC_LOOP_PLAY_TYPE;
//				isNeedDeepSearch = true;
				try {
					Thread.sleep(1000);
					syncLoopReceiver.key = key;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent gpsIntent = new Intent("PLAY_SYNC_LOOP_PROGRAM");
				gpsIntent.putExtra("path", "mnt/sdcard/syncloop_program/syncloopprogram.xml");
				sendBroadcast(gpsIntent);
			}
		}.start();
		
	}

	/**
	 * 发送显示创建窗口的。
	 * 兼容 全局页面和基本页面。
	 * @param pageObj  全局页面或者是基本页面的实例对象。
	 * @param isGlobalPage 是否为全局页面，不是全局页面就是基本页面
	 */
	public void SendMsgCreateWindow(PageInterface pageObj,boolean isGlobalPage,int playType){
		//如果是局域网同步点播任务的主机，广播通知局域网其他用户
		if ((isGlobalPage==false)&&(currPlayType == LAN_SYNC_LOOP_PLAY_TYPE)) {
			notifyLocalDevicePlayCurr(currentPageIndex);
		}
		PageInfoVo pageInfoVo = new PageInfoVo();
		Message msg = new Message();
		ArrayList<Integer> winTypeList= pageObj.getWindowList();
		for(int i = 0;i < winTypeList.size();i++){
			switch(winTypeList.get(i)){
			case 0://windowType 0 代表通用窗口。
				ArrayList<UniversalWindow> universalwindowlist = pageObj.getUniversalWindowList();
				pageInfoVo.setObject(universalwindowlist);
				pageInfoVo.setWinCount(universalwindowlist.size());
				break;
			case 1:
				if(pageObj.getAnalogClockWindow()!=null){
					pageInfoVo.setAnalogClockWindow(pageObj.getAnalogClockWindow());	
					
				}
				if(pageObj.getDigitalClockWindow()!=null){
					if(DEBUG)Log.i("LedSevices", "........DigitalClockWindow!=null");
					pageInfoVo.setDigitalClockWindow(pageObj.getDigitalClockWindow());
				}
				break;
			case 2:
				pageInfoVo.setHumitureWindow(pageObj.getHumitureWindow());
				break;
			case 3: 
				pageInfoVo.setCountDownWindow(pageObj.getCountDownWindow());
//				pageObj.setCountDownWindow(null);//防止第二遍播放时重新创建倒计时窗。 被我注释了 modify by joychine 2014年4月29日 16:34:09
				break;
			case 5:
				pageInfoVo.setCameraWindow(pageObj.getCameraWindow());
				
				if(DEBUG)Log.i("LedSevices", "........"+pageObj.getCameraWindow().getStartX()+" "
				+pageObj.getCameraWindow().getStartY()+" "+pageObj.getCameraWindow().getWidth()
				+" "+pageObj.getCameraWindow().getHeiht());
				break;
			}
		}
		pageInfoVo.setWindowlist(winTypeList);
		pageInfoVo.setLedTextInfo(null);//开放式播放这个赋值的。
		if(isGlobalPage){
			pageInfoVo.setHandler(globalHandler);
			msg.arg1 = 0;
		}else{
			pageInfoVo.setHandler(baseHandler);
			msg.arg1 = 1;
		}
		Bundle data = new Bundle();
		data.putInt("playType", playType);
		msg.setData(data);
		msg.what = LedProtocol.CREATE_WINDOW;
		msg.obj = pageInfoVo;
		
		try {
			rMessenger.send(msg);
			if(DEBUG)Log.i("Thread", "&&&&&&&&& sunThread send msg CREATE_WINDOW to activity");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * led_xx_xx开放式播放时，指定播放第 fileIndex个资源文件。
	 * @function 发送 DISPLAYER_VIEW 到LedActivity的Handler处理。
	 * @param fileNames
	 * @param LedFileDirName
	 * @param fileIndex
	 */
	public void LedSendMessage(String [] fileNames,String LedFileDirName,int fileIndex){
	Log.e("LedService", "!!--!!---fileIndex:"+fileIndex);
    	ViewInfoClass viewinfoclass = new ViewInfoClass();
    	int fileType = getFileType(fileNames[fileIndex]);
    	viewinfoclass.setFileType(fileType==24?-1:fileType);//绕开走马灯，无下一个走马灯需要切换。
    	viewinfoclass.setHandler(ledHandler);
    	viewinfoclass.setFilePath(mediaPath+"/"+LedFileDirName+"/"+fileNames[fileIndex]);
    	viewinfoclass.setStayLine(10); //默认停留时间给10秒，这是开放式嘛，给固定值。
    	viewinfoclass.setObject(null);
    	viewinfoclass.setWinIndex(0);
    	viewinfoclass.setPageIndex(0);
    	viewinfoclass.setPlayType(0);
    	if(DEBUG)Log.i("LedService SendMessage", "&&&&&&&&& LedThread send DISPLAYER_VIEW msg to activity");
    	Log.i("LedService", "-开放播放："+mediaPath+"/"+LedFileDirName+"/"+fileNames[fileIndex]+"--文件："+Arrays.toString(fileNames));
    	Message msg = new Message();
    	msg.obj = viewinfoclass;
    	msg.arg1 = 0;
    	msg.what = LedProtocol.DISPLAYER_VIEW;
    	
    	try {
			rMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    
	}
	
	/**
	 * 检测是不是所有的窗口都播放完了。
	 * @param array
	 * @return
	 */
	public boolean IsAllOne(byte[] array){
    	int i = 0;
    	if(array!=null){
    		while(i < array.length){
        		if(array[i] != 1){
        			return false;
        		}
        		i++;
        	}
        	return true;
    	}
    	return true;
    }
	
	private int judgeXmlType(String netXmlPath) {
		String xmlName = netXmlPath.substring(netXmlPath.lastIndexOf("/")+1, netXmlPath.length());
		int xmlType = XML_LOOP_TYPE;
		if (xmlName.equals("demandprogram.xml")) {
			xmlType = XML_DEMAND_TYPE;
		}else if (xmlName.equals("loopprogram.xml")) {
			xmlType = XML_LOOP_TYPE;
		}else if (xmlName.equals("plugprogram.xml")) {
			xmlType = XML_PLUG_TYPE;
		}else if (xmlName.equals("interior.xml")) {
			xmlType = XML_LOOP_TYPE;
		}else if (xmlName.equals("external.xml")) {
			xmlType = XML_LOOP_TYPE;
		}else if (xmlName.equalsIgnoreCase("lansyncloopprogram")) { //已经废弃的设计
			xmlType = XML_LAN_SYNCLOOP_TYPE;
		}
		return xmlType;
	}
	/**
	 * mark 当前Service的handler
	 * 当这个LedService启动后，断电重新开机后，检测 继续播放
	 * 检测优先级：上次外设路劲>/mnt/sdcard/program/interior.xml>上次网络路劲
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LedProtocol.MSG_SERVICE_APK:  //当bind连接Connection后会发这个过来。
				rMessenger = msg.replyTo;//把主线程的messenger拿出来。
				pSerialPort = (SerialPort)msg.obj;
				
				String mountPath = pathSp.getString("mountPath", null); //开机要能继续播放。U盘优先,插了U盘就要播放U盘中的先不管上次了。
				String netXmlPath = pathSp.getString("netXmlPath", null);
				int remeberPlayType = pathSp.getInt("CurrPlayType", -1);
//				int remeberPlayType = pathshared.getInt("CurrPlayType", NORMAL_LOOP_PLAYE_TYPE);
				
				if(mountPath!=null&&new File(mountPath).list()!=null){ //还不能是插了外部sdcard的情况，不然会开机就拷贝。。handler还不能显示拷贝进度
					Log.e("LedService", mountPath+" 路劲已经挂载--LedService's mHandler");
					mediastorecount++;
					CheckMountMedia(mountPath);
					return;
				}
				if (netXmlPath==null||remeberPlayType==-1||new File(netXmlPath).exists()==false) {
					Log.e("LedService", "-----ERROR------上次记忆已经丢失&&&&&&&&或者无记忆&&&或者文件丢失!");
					pathEditor.clear(); 
					pathEditor.commit();
					return;
				}
				switch (remeberPlayType) {
				case INNER_LOOP_PLAYE_TYPE:
				case NORMAL_LOOP_PLAYE_TYPE:
				case NORMAL_DEMAND_PLAY_TYPE:
				case LAN_SYNC_LOOP_PLAY_TYPE:
//					pathEditor.remove("mountPath");
//					pathEditor.remove("isExternalPlay");
//					ParserAndPlayer(netXmlPath);
//					currPlayType = pathshared.getInt("CurrPlayType", NORMAL_LOOP_PLAYE_TYPE);
//					LedService.currPlayType = pathSp.getInt("CurrPlayType", NORMAL_LOOP_PLAYE_TYPE);
					Intent normalIntent = new Intent("PLAY_NET_PROGRAM");
					normalIntent.putExtra("path", netXmlPath);
					sendBroadcast(normalIntent);
					break;
				case GPS_SYNC_LOOP_PLAY_TYPE:
					//接着启动同步播放
					Intent gpsIntent = new Intent("PLAY_SYNC_LOOP_PROGRAM");
					gpsIntent.putExtra("path", netXmlPath);
					sendBroadcast(gpsIntent);
					break;
				default:
					pathEditor.clear(); 
					pathEditor.commit();
					break;
				}
				
/*				if(mountPath!=null&&new File(mountPath).list()!=null){ //还不能是插了外部sdcard的情况，不然会开机就拷贝。。handler还不能显示拷贝进度
					Log.e("LedService", mountPath+" 路劲已经挂载--LedService's mHandler");
					mediastorecount++;
					CheckMountMedia(mountPath);
				}
				else if(netXmlPath!=null&&new File(netXmlPath).exists()&&currPlayType!=GPS_SYNC_LOOP_PLAY_TYPE) //存在网路发送过来的xml
				{
					patheditor.remove("mountPath");
					patheditor.remove("isExternalPlay");
//					isnetloop = false;
					ParserAndPlayer(netXmlPath);
//					ParserAndPlayer(netXmlPath,LOOP_TYPE); //开机后统一成轮播。。。哪怕是内部拷贝不播放

				}else if (netXmlPath!=null&&new File(netXmlPath).exists()&&currPlayType==GPS_SYNC_LOOP_PLAY_TYPE) {
					//接着启动同步播放
					syncLoopReceiver.isStartNow = true;
					isNeedDeepSearch = true;
					Intent gpsIntent = new Intent("PLAY_SYNC_LOOP_PROGRAM");
					gpsIntent.putExtra("path", netXmlPath);
					sendBroadcast(gpsIntent);
				}else {
					patheditor.clear(); 
				}
				patheditor.commit();			*/
				//1.如果内部拷贝，但是没有拔掉sdcard就关机了，我重启后应该上次的loopprogram.xml路劲还是保存的，要接着播放这个节目
				//2.拔掉U盘才播放拷贝进去的文件
				if(accreditTimer!=null){
					accreditTimer.cancel();
					accreditTimer = null;
				}
				
//				accreditTimer = new Timer();   //不授权操作了。
//				accreditTimer.schedule(new AccreditTask(),10000,Integer.MAX_VALUE);//无限期限
				
				break;
			case LedProtocol.SOURCE_ZERO_SIZE:
				ToastUtil.setLTShow(getApplicationContext(), "Source file not exiset", 0);
				break;
			case LedProtocol.BEGIN_COPY:
				showTopMsg("COPY File: 0%");
				break;
			case LedProtocol.COPYING:
				String msg12 = (int)(finishSize*100f/totalSize)+"%";
				Log.e("copyed", msg12);
				msgView.setText("COPY File: "+msg12); //发现文字会重叠，是前面大婶代码设计有误，UI线程负荷太重所致。
				break;
			case LedProtocol.COPY_END:
				ToastUtil.setLTShow(getApplicationContext(), "Copy is completed！", 1);
				dismissTopMsg();
				
				break;
			case LedProtocol.COPY_NEED_MORE_ROM:
				File f = new File(interiorMediaPath+"/copyprogram");
				boolean isExistCopyProgramDir = f.exists();
				ToastUtil.setLTShow(getApplicationContext(), "more storage is need!"+(isExistCopyProgramDir?"\nauto free some rom\ntry again after 7 seconds!":""), 1);
				//删除已经拷贝的文件
				FileHandler.deleteFailFiles(interiorMediaPath+"/copyprogram");
				//删除LOST.DIR目录下的错误文件
				FileHandler.deleteFailFiles("/mnt/sdcard/LOST.DIR");
				dismissTopMsg();
				break;
			case LedProtocol.COPY_ERROR:
				ToastUtil.setLTShow(getApplicationContext(), "U-disk or SD removed unexpected！", 1);
				//删除已经拷贝的文件
				FileHandler.deleteFailFiles(interiorMediaPath+"/copyprogram");
				dismissTopMsg();
				break;
			case LedProtocol.COPY_CLEAR_MSG:
				dismissTopMsg();
				break;
			case LedProtocol.CREATE_SYNC_DEMAND_WINDOW:
				BasicPage pageObj = (BasicPage) msg.obj;
				sendMsgCreateSyncDemandWindow(pageObj,ViewInfoClass.SYNC_LOOP_MODE);
				break;
			}
		}


		WindowManager wm = null;
		TextView msgView = null;
		/**
		 * 显示顶级窗口消息提示
		 */
		private void showTopMsg(String msg) {
			wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//			params.alpha=0f;
			params.gravity = Gravity.LEFT|Gravity.TOP;
			params.width= WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			params.type = LayoutParams.TYPE_PHONE;
			params.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL ;
			params.format=PixelFormat.RGBA_8888; 
			msgView = new TextView(getApplicationContext());
			msgView.setTextColor(Color.WHITE);
			msgView.setText(msg);
			wm.addView(msgView, params);
		}
		/**
		 * 取消掉顶级提示框
		 */
		private void dismissTopMsg(){
			if (wm!=null&&msgView!=null) {
				wm.removeView(msgView);
				msgView = null;
				wm = null;
				totalSize = 0;
				finishSize=0;
			}
		}
	};

	
	public long begin = 0;
	/**
	 * 网络任务接收器，监听动作Action: PLAY_NET_PROGRAM
	 * @author 1231
	 */
	public class NetReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mAccredit==false) return;
				
			long end = System.currentTimeMillis();
			if (end-begin<1000) {
				Log.i("LedService", "NetReceiver：接收广播太快了~~~")				;
				return;
			}else {
				begin = end;
			}
			if(mountedTimer!=null){
				mountedTimer.cancel();
				mountedTimer = null;
			}
			if(ejectTimer!=null){
				ejectTimer.cancel();
				ejectTimer = null;
			}
			if (netTimer!=null) {
				netTimer.cancel();
				netTimer = null;
			}
			
			String action = intent.getAction();
			if(action.equals("PLAY_NET_PROGRAM")){
Log.e("LedService", "%%%%%%%%%%onReceive  PLAY_NET_PROGRAM 已经收到!");
		
				String netXmlPath = intent.getStringExtra("path"); //mnt/sdcard（extsd）/program(demandprogram/plugprogram)/loopprogram.xml（demandprogram.xml或plugprogram.xml） 轮播/点播/插播
				String xmlName = netXmlPath.substring(netXmlPath.lastIndexOf("/")+1, netXmlPath.length());
				/**
				 * 清空前一次的其他播放模式状态
				 */
				LedActivity.isSyncLoopWindowReady = false; //清楚GPS的轮播窗口状态。
					
				if(xmlName.equals("loopprogram.xml")){
					StopPlayer();
					clearGPSSynMark();
					//当前我是主机，仅仅是主机能收到这条广播，主机轮播，从机轮播指定页面的模式。,xml不同多一个标识字段，有这个字段则是主机收到这个广播。
					
//					currPlayType = intent.getIntExtra("CurrPlayType", NORMAL_LOOP_PLAYE_TYPE);
					if (lanSyncSp==null) {
						lanSyncSp = getSharedPreferences("isMeLANHost", Context.MODE_PRIVATE);
						lanSyncEditor = lanSyncSp.edit();
					}
					boolean isMeLANHost = lanSyncSp.getBoolean("isMeLANHost", false);
					if (isMeLANHost) {
						Log.i("dfdfdf", "主，我目前是主机：------------------------");						
						currPlayType=LAN_SYNC_LOOP_PLAY_TYPE;
					}else {
						currPlayType=NORMAL_LOOP_PLAYE_TYPE;
						Log.i("dfdfdf", "从，我目前是从机：~~~~~~~~~~~~~~~~~~~~~~~~");						
					}
					
					lanSyncPageIndex = intent.getIntExtra("lanSyncPageIndex", 0);//不管是普通的轮播，还是局域网的指定页面同步轮播。这个默认为0就成
					pathEditor.clear();
					pathEditor.putString("netXmlPath", netXmlPath);
					pathEditor.putInt("CurrPlayType", currPlayType);
					pathEditor.commit();
					
				}else if("demandprogram.xml".equals(xmlName)){
					//1.首先判断指定的点播页面是否存在合法
					int tmpdemandpage = intent.getIntExtra("demand", 0);//点播的页面
					Log.i("LedService", "亲，欲点播页面~~demandPage:"+tmpdemandpage);
					try{
						LedXmlClass tmpDemandXml = parserxml.ParserXML(netXmlPath);
						if (tmpdemandpage>=tmpDemandXml.getBasicPageList().size()||tmpdemandpage<0) {
							tmpDemandXml = null;
							Log.e("点播页面不存在提示", "-----------------------点播页面不存在-------------------");						
							return;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					StopPlayer();
					demandpage = tmpdemandpage;
					if (currPlayType==GPS_SYNC_LOOP_PLAY_TYPE) {
						isLastGPSSyncLoop = true;
					}
					currPlayType = NORMAL_DEMAND_PLAY_TYPE;
					//如果没有轮播,当然就是没有在播放轮播，就保存点播。。
					//为了支持 null -->demandprogram.xml(多次) --> external.xml -->demandprogram.xml .....,
					//还需要判断是不是 external.xml在播放,要保护external.xml(开放式播放的无法保护他是无xml状态可言)状态。上面只要判断isExternalPlay为false和rememberIndex有就会一直轮训当前页面
					String savedMountPath = pathSp.getString("mountPath", null);
					if (savedMountPath==null) {
						String savedNetPath = pathSp.getString("netXmlPath", null);
						//如果是null -->demandprogram.xml 就是点播上面点播需要记住最后一次点播的路径才行。
						if (savedNetPath==null||savedNetPath.endsWith("demandprogram.xml")) {//此时不论mediapath是否为null我的目的是存下点播。
							currDemandNetPath = netXmlPath;
							pathEditor.clear();
							pathEditor.putString("netXmlPath", netXmlPath);
							pathEditor.putInt("rememberPageIndex", demandpage);
							pathEditor.putInt("CurrPlayType", NORMAL_DEMAND_PLAY_TYPE);
							Log.e("提交啊", "把点播任务当轮播写入到持久层，下次开机播放刚才点播的任务，提交啊。。。。。。记住了");						
						}
					}
					pathEditor.commit(); 
				}
				
				netTimer = new Timer("NetReceiver_timer");
				netTimer.schedule(new NetTask(netXmlPath), 0);
				
			
			}else if (action.equals("GET_DEMAND_PROGRAM")) {//注册广播接收器，获取点播列表
				if (demandXmlObj!=null&&new File(interiorMediaPath+"/demand_program/demandprogram.xml").exists()) {
					ArrayList<BasicPage> basicPages  = demandXmlObj.getBasicPageList();
					
				}
			}else if (action.equals("PLAY_SYNC_LOOP_PROGRAM")) {
Log.e("LedServices","----PLAY_SYNC_LOOP_PROGRAM广播已经受到了----" );
				StopPlayer();
				LedActivity.isSyncLoopWindowReady = false; //如果前一次也是gps轮播 要清空此次的标识
				isLastGPSSyncLoop = true;
				isNeedDeepSearch = true;
				currPlayType = GPS_SYNC_LOOP_PLAY_TYPE;
				
				//同步点播，不再能解析xml
				//1.保存目前路劲为同步点播的xml路劲。这个路劲是 "/mnt/sdcard/syncloop_program/syncloopprogram.xml"
				String netXmlPath = intent.getStringExtra("path");
				pathEditor.clear();
				pathEditor.putString("netXmlPath", netXmlPath);   //断电后要能记得重新创建窗口
				pathEditor.putInt("CurrPlayType", GPS_SYNC_LOOP_PLAY_TYPE);
				mediaPath = netXmlPath.substring(0, netXmlPath.lastIndexOf("/syncloop_program"));
//				isSyncLoop = true;
				
				//2.查询数据库创建窗口布局，查询并且创建一个pageInterface对象
				new Thread("创建窗口查询数据库_t"){
					public void run() {
						if (mDisplayerBiz==null) {
							mDisplayerBiz = new DisplayerBiz(LedService.this);
						}
//						BasicPage pageObj = mDisplayerBiz.getBasicPage(); mark
						BasicPage pageObj = mDisplayerBiz.getBasicPageByDB();
						if (pageObj==null) {
							Log.e("LedService", "pageBasic为null数据库中的窗口不存在吗？");
							return;
						}else {
//							System.out.println("查数据库PageBasic:"+pageObj);
						}
						//赋值pageObj字段信息。
						Message msg = Message.obtain();
						msg.obj = pageObj;
						msg.what = LedProtocol.CREATE_SYNC_DEMAND_WINDOW;
						mHandler.sendMessage(msg);
					};
				}.start();
				//启动检测线程
				Intent chkIntent = new Intent("com.led.player.action.syncloopcheck");
				sendBroadcast(chkIntent);
				pathEditor.commit();
			}
			
		}
		

	}
	
	/**
	 * 清除 GPS同步点播的标志
	 */
	private void clearGPSSynMark() {
		isLastGPSSyncLoop = false;//清楚同样的GPS同步轮播
		LedActivity.isSyncLoopWindowReady = false;
	}
	
	
	public class SDCardReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("path:::", intent.getAction()+"外设改变了。。。path: "+intent.getData().getPath());
			if(mAccredit){
				if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
					Log.e("LedService", "外部sdcard被挂载了");
					mediastorecount++;  //这个字段干嘛的啊？？？
					
					if(mountedTimer!=null){
						mountedTimer.cancel();
						mountedTimer = null;
					}
					if(ejectTimer!=null){
						ejectTimer.cancel();
						ejectTimer = null;
					}
					mountedTimer = new Timer();
					mountedTimer.schedule(new MountedTask(intent.getData().getPath()),4000);
				}
				else if(intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
					if(DEBUG)Log.i("LedService", "%%%%%%%%%%onReceive  ACTION_MEDIA_EJECT");
					if(mediastorecount!=0){
						mediastorecount--;
					}
					
					if(mediastorecount==0){
						StopPlayer();
					}
					if(mountedTimer!=null){
						mountedTimer.cancel();
					}
					if(ejectTimer!=null){
						ejectTimer.cancel();
					}
					
					ejectTimer = new Timer();
					ejectTimer.schedule(new EjectTask(),4000);
				}
				else if(intent.getAction().equals("LED_NET")){
					//what you want to do？ big god！！！！
				}
			}
			
		}

	}
	
	/**
	 * 定时器当做普通线程用。 有 外界媒介物质挂载时执行。是在sdcardReceiver中调用它。
	 * 开机时如果U盘或sdcard设备是插上面的，会收到这个挂载命令。
	 * @author 1231
	 */
	private class MountedTask extends TimerTask{
		private String mountPath;
		public MountedTask(String mountPath) {
			this.mountPath = mountPath;
		}

		@Override
		public void run() {
			if(!hasmediastore){
				hasmediastore = true;
//				mountPath = mediaIntent.getData().getPath();
				//1.把 mediapath路径 如 /mnt/usbhost1 或/mnt/extsd 存进sharepreference中,  更合适的位置是 外部播放、开放式播放这2个时才存入
//				patheditor.putString("mountPath", mountPath); 
//				patheditor.commit();
				//2.复制U盘或者是外部sdcard根目录下 copy目录下的文件到内部sdcard上
//				CopyConfigFile();   //不知道干嘛的。
				//3.我估计是……播放操作
Log.e("检测到U盘已经挂载", "MountedTask--检测到U盘或sdcard已经挂载"+mountPath);			 	
				CheckMountMedia(mountPath);
//				CheckNetMedia(mountPath);
			}
			
			//2.本来就不是当作定时器用，及时取消
			mountedTimer.cancel();
		}
	}
	
	/**
	 * 外部媒介被 弹出，还得继续播放上次网络文件发过来的文件
	 * @author joychine
	 *@since 2013年12月11日 16:51:05
	 */
	private class EjectTask extends TimerTask{
		@Override
		public void run() {
			if(DEBUG)Log.i("LedService", "%%%%%%%%%%EjectTask run ");
			if(mediastorecount==0){
				hasmediastore = false;
				//删除持久层多余的字段。
				pathEditor.remove("mountPath");
				pathEditor.remove("isExternalPlay");
				String netXmlPath = pathSp.getString("netXmlPath", null); //再读取上次网络路劲
				if(copyfilefinish){
					if(new File(interiorMediaPath+"/copyprogram").exists()){
	            		if(new File(interiorMediaPath+"/program").exists()){
	            			try {
								if(deletefile(interiorMediaPath+"/program")){
									if(DEBUG)Log.i("LedService", "%%%%%%%%%%delete program ");
									new File(interiorMediaPath+"/copyprogram").renameTo(new File(interiorMediaPath+"/program"));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
	            		}
	            		else{
	            			new File(interiorMediaPath+"/copyprogram").renameTo(new File(interiorMediaPath+"/program"));
	            		}
	            		if(DEBUG)Log.i("LedProjectActivity", "%%%%%%%%%%rename to program ");
	            	}
				}
				//判断这次拨出U盘 是不是上次插入U盘是不是打算内部播放文件。
				if(copyfilefinish&&new File(interiorMediaPath+"/program/interior.xml").exists()){
					clearGPSSynMark();
					currPlayType = INNER_LOOP_PLAYE_TYPE;
					copyfilefinish = false;
					pathEditor.clear();
					pathEditor.putInt("CurrPlayType", INNER_LOOP_PLAYE_TYPE);  
					pathEditor.putString("netXmlPath", interiorMediaPath+"/program/interior.xml");
					isdemand = false; //如果上次是点播，让流程回归轮播。
Log.e("想知道", "想知道这个 sdcard拔掉后的 但前播放界面页currentpageIndex:"+currentPageIndex);					
					ParserAndPlayer(interiorMediaPath+"/program/interior.xml");
				}
				else if(netXmlPath!=null&&new File(netXmlPath).exists())
				{
					int remeberPlayType = pathSp.getInt("CurrPlayType", -1);
					if (remeberPlayType==GPS_SYNC_LOOP_PLAY_TYPE) {
						syncLoopReceiver.key = key;
						Intent gpsIntent = new Intent("PLAY_SYNC_LOOP_PROGRAM");
						gpsIntent.putExtra("path", netXmlPath);
						sendBroadcast(gpsIntent);
					}else if (remeberPlayType == LAN_SYNC_LOOP_PLAY_TYPE) {
						Intent demandIntent = new Intent("PLAY_LOCAL_SYNC_LOOP_PROGRAM");
						demandIntent.putExtra("path", netXmlPath);
						sendBroadcast(demandIntent);
					}
					else {
						Log.i("joychine-ledservie", "拔掉U盘，上次还有网络任务了");// NET_DEMAND_PLAY_TYPE 或者是 NET_LOOP_PLAYE_TYPE
						ParserAndPlayer(netXmlPath);
					}
					
				}
				
				pathEditor.commit();
				
			}
			if(DEBUG)Log.i("LedService", "%%%%%%%%%%EjectTimer.cancel() ");
			ejectTimer.cancel();
		}
		
	}
	
	private class NetTask extends TimerTask{
		private String netXmlPath;
		
		public NetTask(String netPath) {
			this.netXmlPath = netPath;
		}

		@Override
		public void run() {
			ParserAndPlayer(netXmlPath);
			
/*		    if(new File(netPath+"/program/demandprogram.xml").exists()){ 
				lastMediaPath = mediaPath;
				lastLoopXmlObj = loopXmlObj;
				
//				lastPlayerpage = currentPageIndex;
				lastPlayerpage = lastLoopPageIndex;
				isdemand = true;
				currentPageIndex = demandpage;
Log.e("infos", "信息--lastMediaPath:"+mediaPath+"\nlastLoopXmlObj:"+loopXmlObj+"\nlastPlayerpage:"+lastPlayerpage+"\ncurrentplayerpage:"+currentPageIndex)		;		;
				ParserAndPlayer(netPath,"/program/demandprogram.xml",DEMAND_TYPE);
				
				 
			}
			else if(new File(netPath+"/program/loopprogram.xml").exists()){
				ParserAndPlayer(netPath,"/program/loopprogram.xml",LOOP_TYPE); 
				
			} else if(new File(netPath+"/program/pluginprogram.xml").exists()){
				lastMediaPath = mediaPath;
				lastLoopXmlObj = loopXmlObj;
//				lastPlayerpage = currentPageIndex;
				lastPlayerpage = lastLoopPageIndex;
				isplugin = true;
				ParserAndPlayer(netPath,"/program/pluginprogram.xml",PLUG_TYPE);
			}*/
		}
		
	}
	
	/**
	 * 解析xml文件并且 播放文件。
	 * @param Prepath   /mnt/sdcard   or  /mnt/extsd
	 * @param xmlpath   /program/interior.xml  或者是轮播，插播，点播的xml路劲 如："/program/loopprogram.xml"
	 */
	private void ParserAndPlayer(String xmlabsPath){
		try {
			String prePath = null;
			String xmlName = xmlabsPath.substring(xmlabsPath.lastIndexOf("/")+1, xmlabsPath.length());
			int xmlType = judgeXmlType(xmlabsPath);
			switch (xmlType) {
			case XML_LAN_SYNCLOOP_TYPE:
			case XML_LOOP_TYPE:
				loopXmlObj = parserxml.ParserXML(xmlabsPath);
				currentPageIndex=0;
				//计算出媒介路劲
				if (xmlName.equals("external.xml")) {
					prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/program"));
				}else if (xmlName.equals("interior.xml")) {
					prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/program"));
				}else if (xmlName.equals("loopprogram.xml")) {
					currentPageIndex = lanSyncPageIndex;
					prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/program"));
				}else if (xmlName.equalsIgnoreCase("lansyncloopprogram")) { //没有这个分类了。废弃的设计
					prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/lan_syncloop_program"));
				}
				currentLoopMediaPath = prePath;
				initCase(loopXmlObj, prePath,XML_LOOP_TYPE);
				break;
			case XML_DEMAND_TYPE:
				lastLoopMediaPath =currentLoopMediaPath;
				lastBasicLoopXmlObj = loopXmlObj;
				lastPlayerpage = currentLoopPageIndex;
				isdemand = true;
				
				demandXmlObj = parserxml.ParserXML(xmlabsPath);
				prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/demand_program"));
		Log.e("infos", "NetTask得到上一次任务信息--\nlastLoopMediaPath:"+lastLoopMediaPath+"\nlastLoopXmlObj:"+lastBasicLoopXmlObj+"\nlastPlayerpage:"+lastPlayerpage+"\ncurrentPageIndex:"+currentPageIndex) ;
				currentPageIndex = pathSp.getInt("rememberPageIndex", 0);
				if(currentPageIndex==0) currentPageIndex = demandpage;//除了持久循环页面还要判断临时页面。
		Log.i("LedService", "点播页面，demandpage:"+currentPageIndex);
				initCase(demandXmlObj, prePath,XML_DEMAND_TYPE);
				break;
			case XML_PLUG_TYPE://还没做的功能~~
				plugXmlObj = parserxml.ParserXML(xmlabsPath);
				initCase(plugXmlObj, prePath,XML_LOOP_TYPE);
				break;
/*			case SYNC_DEMAND_TYPE:    //    /mnt/sdcard/syncloop_program/syncloopprogram.xml"
				currentPageIndex=0;
				LedXmlClass syncXmlObj = parserxml.ParserXML(xmlabsPath);
				prePath = xmlabsPath.substring(0, xmlabsPath.lastIndexOf("/syncloop_program"));
				initCase(syncXmlObj, prePath, SYNC_DEMAND_TYPE);
//				currentLoopMediaPath = prePath;  同步播放居然也要普通点播来嵌套。。。。。要能回到上次的同步播放模式。。。
				break;*/
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if(netTimer!=null){
				netTimer.cancel();
				netTimer = null;
			}
		}
		
	}
	
	/**
	 * 判断一个page页面是否合法
	 * @param pageInterface
	 * @return
	 */
	private boolean judgeIsOnePageOK(PageInterface pageInterface ){
		if (pageInterface==null) { 
			Log.i("pageInterface", "pageInterface是null，太惊悚了！！！");
			return false;
		}
//Log.e("dfdf", "当前判断的pageINterface是属于GlobalPage:"+(pageInterface instanceof GlobalPage))		;
		if (pageInterface.getWindowList().size()==0) {
			Log.e("windowlist", "windowlist.size是0！！！是globalpage么？"+(pageInterface instanceof GlobalPage));
			return false;
		}
		ArrayList<UniversalWindow> globalUniversalWindows = pageInterface.getUniversalWindowList();
		if(pageInterface.getWindowList().size()>0){
			 if (globalUniversalWindows.size()>0) {
				 int universalWindoNullCount = 0;
				 for (int i = 0; i < globalUniversalWindows.size(); i++) {
					UniversalWindow universalWindow = globalUniversalWindows.get(i);
					ArrayList<ImageVo> imageVos = universalWindow.getImageVoList();
					ArrayList<VideoVo> videoVos = universalWindow.getVideoVoList();
					ArrayList<TextVo> textVos = universalWindow.getTextVoList();
					if (imageVos.size()==0&&videoVos.size()==0&&textVos.size()==0) {
						universalWindoNullCount++;
					}
					//用得着上面那样判断？ playerlist字段包含了通用窗口中文件类型啦
				 }
				 if (universalWindoNullCount==pageInterface.getWindowList().size()) {
					 Log.e("坑或啊", "全是坑，当前一个页面下的所有窗口下面都没有资源文件,而且当前没有除通用窗口以外的其它窗--judgeIsOnePageOK(PageInterface pageInterface )");
					 return false;
				 }else {
					 return true;
				}
			 }else {//当前这个状态 是：globalpage.getWindowList().size()>0而 通用窗口用全部为0，那必然是其他窗口！
				//通用窗口等于0 时接着判断 非通用窗口。
Log.e("运行吧", "当前这个状态 是：pageInterface.getWindowList().size()>0而 通用窗口用全部为0，那必然是其他窗口！");				 
				 return true; 
			}
		}

		 return false;
	}
	private void initCase(LedXmlClass ledXmlClass,String Prepath,int xmlType) {
		long beginTime = System.currentTimeMillis();
		if(ledXmlClass!=null){
			 InitWinInfo(ledXmlClass);
			 mediaPath = Prepath; //mediapath 初始赋值处 mark，  这里此时mediapath可以赋值 轮播的媒介路劲也可能是点播的媒介路劲
			 if (judgeIsOnePageOK(globalpage)) {
				 globalThread = new GlobalThread(globalpage);
				 globalThread.start();
			 }
			 
			 //这个节目页如果没窗口或者说窗口下都没资源都不启动。
			 if (judgeIsBasicProgramOk()){
				 int playType = ViewInfoClass.LOOP_PLAYER_MODE;
				 if (xmlType==XML_LOOP_TYPE||xmlType == XML_DEMAND_TYPE) {
					playType = ViewInfoClass.LOOP_PLAYER_MODE;
				 }
				 baseThread = new BaseThread(basicpagelist,playType);
				 baseThread.start();
			 }else {
				 if (xmlType==XML_LOOP_TYPE) {
					 Log.e("LedService", "发送的是空任务，清空loopXmlObj对象");
					 loopXmlObj = null; //让点播记忆的上次轮播任务为null。避免在点播任务结束后检测到一个非合法的轮播 照成死循环
					 lastBasicLoopXmlObj = null;
					 pathEditor.clear();
					 pathEditor.commit();
				 }
			 }
			if(netTimer!=null){
				netTimer.cancel();
				netTimer = null;
			}
			
			Log.e("判断是否启动线程", "判断是否启动线程话费的时间："+(System.currentTimeMillis()-beginTime)+"毫秒");
		 }
	}

	private boolean judgeIsBasicProgramOk() {
		if (basicpagelist==null) {
			return false;
		}
		if (basicpagelist.size()==0) {
			return false;
		}
		 int basicPageNullCount = 0;
		 for (int i = 0; i < basicpagelist.size(); i++) {
			 BasicPage basicPage = basicpagelist.get(i);
			 boolean isOnePageOk = judgeIsOnePageOK(basicPage);
			 if (!isOnePageOk) {
				 basicPageNullCount++;
			 }
		 };
		 if (basicPageNullCount==basicpagelist.size()) {//空页面数等于总页面数
			 currentLoopPageIndex = 0;  //把上一次轮播页面索引置为0
			 Log.e("基本页面状况", "未通过检验所有的基本页面下要么没有窗口要么窗口下没资源！lastLoopPageIndex置为0，坑货，mediaPath:"+mediaPath);
			 return false;
		 }else {
			 Log.e("基本页面状况", "通过检验，基本页可以进入播放流程，mediaPath:"+mediaPath);
			 return true;
		 }
			 
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		if(DEBUG)Log.i("LedService", "&&&&&&&&& onBind");
		
	       return binder;
	              
	}
	@Override
	public  boolean onUnbind (Intent intent){
		Log.e("LedService", "解除绑定, unBind");		
		super.onUnbind(intent);
		
		//this.unregisterReceiver(ServiceReceiver);
		
//		Intent intent1 = new Intent();
//		intent1.setClass(this, LedActivity.class);
//		intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		this.startActivity(intent1);
		
		return true;
	}
	

	
	
	/*@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
		Log.e("LedService", "~~~~~~~~~~~ onStartCommand  in LedService");
		if(ledHandler!=null){
			ledHandler.getLooper().quit();
			ledHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			ledHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
		}
		if(baseHandler!=null){
			baseHandler.getLooper().quit();
			baseHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			baseHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
		}
		if(globalHandler!=null){
			globalHandler.getLooper().quit();
			globalHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			globalHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
		}
		currentpage = 0;
		mediastorecount = 0;
	    //return START_REDELIVER_INTENT;
	    return Service.START_NOT_STICKY; //如果是当前Service自己机制重启了，那么这个方法不要再启动了。
		//return 0;
	}*/
	
	@Override
	public void onDestroy(){
		super.onDestroy();
Log.e("LedService", "~~~~~~~~~~~ onDestroy~~~~~~~~~~~~");
		if (sdCardReceiver!=null) {
			unregisterReceiver(sdCardReceiver);
		}
		if (netReceiver!=null) {
			unregisterReceiver(netReceiver);
		}
		if (syncLoopReceiver!=null) {
			unregisterReceiver(syncLoopReceiver);
		}
		mHandler.sendEmptyMessage(LedProtocol.COPY_CLEAR_MSG);
		if (pSerialPort!=null) {
			LedActivity.currLED_state = 3;
			pSerialPort.setLedStatus(1, 3);
		}
		stopSelf();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//setForeground(true);
		mMessenger = new Messenger(mHandler);
		
		//1.注册sdcard变化监听接收器
		sdCardReceiver = new SDCardReceiver();
		IntentFilter sdFilter = new IntentFilter();
//		sdFilter.addAction(Intent.ACTION_MEDIA_SHARED);
	    sdFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
//	    sdFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//	    sdFilter.addAction(Intent.ACTION_MEDIA_CHECKING); 
	    sdFilter.addAction(Intent.ACTION_MEDIA_EJECT);  
//	    sdFilter.addAction(Intent.ACTION_MEDIA_REMOVED); 
	    sdFilter.addAction("LED_NET");
	    sdFilter.addDataScheme("file"); 
		registerReceiver(sdCardReceiver,sdFilter);
		//2.注册网络任务接收器
		netReceiver = new NetReceiver();
		IntentFilter netfilter = new IntentFilter();
		netfilter.addAction("PLAY_NET_PROGRAM");
		netfilter.addAction("PLAY_SYNC_LOOP_PROGRAM");
		registerReceiver(netReceiver,netfilter);
		
		//3.注册系统时间变化监听接收器
		syncLoopReceiver = new SyncLoopReceiver(key);
//		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.led.player.action.syncloopcheck");
		filter.addAction("com.led.player.action.parser_gpsloopxml");
		registerReceiver(syncLoopReceiver, filter);
		
		pathSp = getSharedPreferences("filepath", MODE_WORLD_READABLE);
		pathEditor = pathSp.edit();
		
Log.e("LedService", "onCreate....LedService");	

		if (LedActivity.isLedActivityOn==false) {
			Log.e("LedService", "LedActivity还没启动。启动。in LedService");
			Intent intent = new Intent();
			intent.setClass(this, LedActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
		}
		
		
//		apnset = new ApnSetting(this);
//		apnset.SetApn();
		
		
/*		//监听内存变化
		new Thread("内存显示线程"){
			public void run() {
				displayBriefMemory();
			};
		}.start();*/
	}
	
	 private void displayBriefMemory() {    
	        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);    
	        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();   
	        activityManager.getMemoryInfo(info);    
	        Log.i("内存信息","系统剩余内存:"+(info.availMem >> 10)+"k");   
	        Log.i("内存信息","系统是否处于低内存运行："+info.lowMemory);
	        Log.i("内存信息","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
	    } 
	
	
	public  void StopPlayer(){
//System.out.println("当前线程的id:"+Thread.currentThread().getId());	
		if(pSerialPort!=null){
			LedActivity.currLED_state = 0;
			pSerialPort.setLedStatus(1,0);
		}
//	long beging = System.currentTimeMillis();
		if(ledHandler!=null){
			ledHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			ledHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
			ledHandler.getLooper().quit();//mark 这里喜欢报个warn ：null sending message to a Handler on a dead thread
			ledHandler = null;
			
		}
		if(baseHandler!=null){
			baseHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			baseHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
			baseHandler.getLooper().quit();
			baseHandler = null;
		} 
		if(globalHandler!=null){
			globalHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
			globalHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
			globalHandler.getLooper().quit();//mark 这里喜欢报个warn ：null sending message to a Handler on a dead thread
			globalHandler = null;
		}
//		
		Message msg = Message.obtain(null, LedProtocol.REMOVE_ALL_VIEW);
		try {
			rMessenger.send(msg);
			currentPageIndex = 0;
			isdemand = false; //如果上次是点播，外部播放要去掉点播。
			//如果上次是gps同步播放还在解析xml。要停止解析
			if (gpsSyncLoopParser!=null) {
				gpsSyncLoopParser.stopParserXml();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
//		System.out.println("消耗时间："+(System.currentTimeMillis()-beging));
	}
	
	/**
	 * LedService启动后，Connection中发消息过来检测媒介或 外部设备挂载时检测。
	 * 检查U盘/外部sdcard根目录下(这个路劲是外部媒介路劲) 是否有开放式播放模式的文件夹
	 * @param path   /mnt/usbhost1或 /mnt/extsd
	 */
	public void CheckMountMedia(String path){
		String status = Environment.getExternalStorageState(); 
		  if (status.equals(Environment.MEDIA_MOUNTED)){
//			  Toast.makeText(getApplicationContext(), "sdcard has MOUNTED",Toast.LENGTH_SHORT).show();
	 if(DEBUG)Log.i("LedService", "&&&&&&&&& CheckAndReadSD"+Environment.getExternalStorageDirectory().getAbsolutePath());
			  File RootFile = new File(path);
			  File []files = RootFile.listFiles();
			  if(files!=null){
				  for(File f:files){
					  if(DEBUG)Log.i("LedService", "&&&&&&&&& CheckAndReadMedia find led");
					  if (f.isFile()) {//mark 如果是文件我就不用判断，需要判断的是 led_xx_xx的文件名
						continue;
					  }
					  fileDirName = f.getName();
					  String prefixStr = null;
					  if(fileDirName.length()>=4){
						  prefixStr = fileDirName.substring(0, 4);
					  }
					  
					  if(DEBUG)Log.i("LedService", "&&&&&&&&& FileName  "+fileDirName);
					  if("led_".equalsIgnoreCase(prefixStr)){//优先 判断是不是有 led_xx_xx的文件夹，然后采取判断 program文件夹
						  hasledfile = true;
						  hasprofile = false;
						  break;
					  }
					  else if(f.isDirectory()&&f.getName().equalsIgnoreCase("program")){
						  if(!hasledfile){
							  hasprofile = true;
						  }
					  }
				  }
			  }
			  if(hasledfile){
				  if(DEBUG)Log.i("LedService", "&&&&&&&&& CheckAndReadMedia hasledfile=true");
//				  isnetloop = false;
				  StopPlayer();
				  currPlayType = LED_OPEN_LOOP_PLAY_TYPE;
				  pathEditor.putString("mountPath", path); 
//				  patheditor.putInt("CurrPlayType", LED_OPEN_LOOP_PLAY_TYPE);  //这种中断式的播放模式不记录进持久层，为了使能回到上次的状态模式。它自身的记忆靠 mountpath字段
				  pathEditor.commit();
				  mediaPath = path;//不知道这个mediapath是不是又是干啥，这里与 赋值插入的外界介质 传过来的path一致 mark 赋值mediapath
				  ledThread = new LedThread(path,fileDirName);//启动LedThread，参数，媒介根目录，和文件夹名
				  ledThread.start();
				  
				  hasledfile = false;
			  }
			  else if(hasprofile){
				  String filepath1 = path+"/program/external.xml";
				  String filepath2 = path+"/program/interior.xml";
				  File file1 = new File(filepath1);
				  File file2 = new File(filepath2);
				  if(file1.exists()){
					  if(DEBUG)Log.i("LedService", "&&&&&&&&& external.xml  exists");
//					  isnetloop = false;
					  StopPlayer();
					  currPlayType = OUTER_LOOP_PLAY_TYPE;
					  pathEditor.putString("mountPath", path); 
//					  patheditor.putInt("CurrPlayType", LED_OPEN_LOOP_PLAY_TYPE);
					  //如果上次是点任务还需要记住上次点播任务的点播位置。
					  //1.上次点播任务发现自己点播完毕后没有可用的轮播任务，so继续自己的点播播放。2.点播记住了界面页，存入了持久层，所以要临时存放
					  //上次点播的界面页码，等到U盘拔出后再恢复上次点播的页码。不好使，再加入一个标志位表示当前是来 外部播放的external.xml
					  pathEditor.putBoolean("isExternalPlay", true);
					  pathEditor.commit();
					  mediaPath = path;
					  ParserAndPlayer(path+"/program/external.xml");
					  if(DEBUG)Log.i("LedService", "&&&&&&&&& external.xml  exists");
					  
				  }
				  else if(file2.exists()){
//					  isnetloop = false;
//					  nowPlayType = INNER_LOOP_PLAYE_TYPE;  让轮播还继续着直到拔掉外设
					  final String sourceDir = path+"/program";
					  final String targetDir = interiorMediaPath+"/copyprogram";
					  //准备U盘上的文件复制到内部sdcard中的copyprogram上。
					  //1.cale total file size
					 totalSize =  calcTotalSize(sourceDir);
					 if (totalSize==0) {
						 mHandler.sendEmptyMessage(LedProtocol.SOURCE_ZERO_SIZE);
						return;
					 }
Log.e("extsd卡存在，准备拷贝了", "extsd卡存在，准备拷贝了");		
					 //2.判断当前sdcard剩余空间是否足够拷贝外部文件进来
					 if (!isEnoughRom(totalSize)) {
						 mHandler.sendEmptyMessage(LedProtocol.COPY_NEED_MORE_ROM);
						 return;
					  }
					 if(pSerialPort!=null) {
						 pSerialPort.setLedStatus(1,1);   //拷贝文件快闪
					 }
					 //3.显示初始进度条在屏幕左上角。
					 mHandler.sendEmptyMessage(LedProtocol.BEGIN_COPY);
					 Thread.currentThread().setPriority(Thread.MIN_PRIORITY); 
					 Log.i("当前拷贝文件的线程信息", "当前线程名："+Thread.currentThread().getName()+"\t当前线程id:"+Thread.currentThread().getId());
					 new Thread("copy_files_thread"){
						 public void run() {
							  copyfilefinish =  copyDirectiory(sourceDir,targetDir);
//							  copyfilefinish = true;
							  if(DEBUG)Log.i("LedService", "&&&&&&&&& copy file finish");
							  if(copyfilefinish){
								  mHandler.sendEmptyMessage(LedProtocol.COPY_END);
							  }else {//理论上拷贝不会出错的，但是不排除在拷贝时 NetClient在使坏占用了空间大小导致空间不足以拷贝结束
								  mHandler.sendEmptyMessage(LedProtocol.COPY_ERROR);
							  }
							  if(pSerialPort!=null) pSerialPort.setLedStatus(1,LedActivity.currLED_state); 
						 };
					 }.start();
					 	
					
				  }
				  hasprofile = false;
				  
			  }
			  
		  }
		  else if(status.equals(Environment.MEDIA_CHECKING)){
			  if(DEBUG)Log.i("LedService", "&&&&&&&&& Environment.MEDIA_CHECKING");
			  Log.e("外部设备检测中", "MEDIA_CHECKING");
			  try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}                                 
			  status = Environment.getExternalStorageState();     
			          
			  this.CheckMountMedia(path);

		  }
	}
	
	/**
	 * 判断sdcard空间是否足够拷贝。
	 * @return
	 */
	private boolean isEnoughRom(long neddCopySize) {
		File path =Environment.getExternalStorageDirectory();
        //取得sdcard文件路径
        StatFs statfs=new StatFs(path.getPath());
        //获取block的SIZE
        long blocSize=statfs.getBlockSize();
//        //获取BLOCK数量
//        long totalBlocks=statfs.getBlockCount();
        //己使用的Block的数量
        long availaBlock=statfs.getAvailableBlocks();
        long availRom = availaBlock*blocSize;
Log.e("LedService", "当前可用内部存储是："+availRom+"\t当前需要存储是:"+neddCopySize);      
        if (availRom<neddCopySize) {
			return false;
		}
		return true;
	}

	/**
	 * 计算 sourceDir路径下总文件的大小数单位 Byte。
	 * @param sourceDir
	 */
	private long calcTotalSize(String sourceDir) {
		long totalSize = 0;
		File[] files = new File(sourceDir).listFiles();
		if (files!=null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					totalSize+=files[i].length();
				}else {
					totalSize+=calcTotalSize(sourceDir);
				}
			}
		}
		return totalSize;
	}

/*	*//**
	 * 开机检测网路路径下是否有可以"轮播的文件"，有则播放
	 * @param lastNetPath 存进了sharedpreference中的网络路劲。
	 *//*
	public void CheckNetMedia(String lastNetPath){
	  if(isnetloop&&new File(lastNetPath+"/program/loopprogram.xml").exists()){
		  StopPlayer();
		  ParserAndPlayer(lastNetPath,"/program/loopprogram.xml",LOOP_TYPE);
	  }else{
		  isnetloop = true;
	  }
	}*/
	
	/**
	 * @globalpage = xmlobj.getGlobalPage();
    	@basicpagelist = xmlobj.getBasicPageList();
    	@bpageAmount = basicpagelist.size();
	 * @param xmlobj
	 */
	public void InitWinInfo(LedXmlClass xmlobj){
    	globalpage = xmlobj.getGlobalPage();
//Log.i("globalpage", "globalpage的通用窗口数量是:"+globalpage.getUniversalWindowList().size())    	;
//Log.i("globalpage", "globalpage的第一个通用窗口的视频数量:"+globalpage.getUniversalWindow(0).getVideoVoList().size())    	;
    	basicpagelist = xmlobj.getBasicPageList();
    	bpageAmount = basicpagelist.size();
    	
    }
	
	//参数1是拷贝源目录或文件，参数2是拷贝目的文件夹
	public boolean copyDirectiory(String sourceDir,String targetDir)  {
		boolean isCopyOk = true;
		File targetFileDir = new File(targetDir);
		File sourceFileDir = new File(sourceDir);
		if(targetFileDir.exists()&&!targetDir.equalsIgnoreCase("/mnt/sdcard")){//先删除/mnt/sdcard/copyprogram目录下的文件
			try {
				deletefile(targetDir);
				if(DEBUG)Log.i("LedProjectActivity", "%%%%%%%%%%delete file ");
			} catch (Exception e) {
				e.printStackTrace();
				isCopyOk = false;
				return isCopyOk;
			}
		}
		try {
			if(sourceFileDir.isDirectory()&&(targetFileDir.mkdirs() || targetFileDir.isDirectory())){
				File[] files = (new File(sourceDir)).listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						File sourceFile=files[i];
						File targetFile=new File(new File(targetDir).getAbsolutePath()+File.separator+sourceFile.getName());
						copyFile(sourceFile,targetFile);
						if(DEBUG)Log.i("LedService", "&&&&&&&&& isDirectory coping......");
					}else if(files[i].isDirectory()){
						copyDirectiory(sourceDir+"/"+files[i].getName(),targetDir+"/"+files[i].getName());
					}
				} 

			 }else if(sourceFileDir.isFile()){ //这里不可能的啊，program必须要是文件夹。。
				 File sourceFile=sourceFileDir;
				 File targetFile=new File(targetDir+"/"+sourceFileDir.getName());
				 copyFile(sourceFile,targetFile);
				 if(DEBUG)Log.i("LedService", "&&&&&&&&& isFile coping......");
			}
		} catch (Exception e) {
			e.printStackTrace();
			isCopyOk = false;
		}
		
		return isCopyOk;
	}
	
	public void copyFile(File  sourceFile,File  targetFile) throws IOException, InterruptedException{
		FileInputStream fis = new FileInputStream(sourceFile);
		BufferedInputStream bis=new BufferedInputStream(fis);
		FileOutputStream fos = new FileOutputStream(targetFile);
		BufferedOutputStream bos=new BufferedOutputStream(fos);
		byte[] b = new byte[1024*10];
		int len;
		long now = System.currentTimeMillis();
		while ((len =fis.read(b)) != -1) {
			fos.write(b, 0, len);
			finishSize+=len;
			long now_me = System.currentTimeMillis();
			if (now_me-now>1000) {//复制循环速度，比这个发通知的处理速度要快多了，
				now = now_me;
				mHandler.sendMessage(Message.obtain(null, LedProtocol.COPYING, finishSize));//开机如果sdcard插上没会导致这个消息一直阻塞在管道中呢？拔掉sdcard瞬间全部发过去了。
			}
			Thread.sleep(10); 
		}
		bos.flush();
		bis.close();
		bos.close();
		fos.close();
		fis.close();  

	}
	
	public boolean deletefile(String delDir) throws Exception {
			File file = new File(delDir);
			if (!file.isDirectory()) {
				file.delete();
			} 
			else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delDir + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delDir + File.separator + filelist[i]);
					}
				}
				file.delete(); //删除该空目录。
			}
		return true;
	} 
	
	/**
	 * 得到 led_xx_xx目录下的 资源文件列表， 用的很原始的 file.list()，不要放其他非视屏图片文字资源。
	 * @param filepath
	 * @return
	 */
	public String []GetFileNames(String filepath){
		File fl = new File(filepath);
		String [] filenames= fl.list();
//		String [] filenames= null;
//		filenames = fl.list( new FilenameFilter() {
//			
//			public boolean accept(File dir, String filename) {
//				// TODO Auto-generated method stub
//				return !filename.substring(filename.lastIndexOf(".")+1, filename.length()).equalsIgnoreCase("txt");
//			}
//		});
		Arrays.sort(filenames);
		return filenames;
	}
	
	/**
	 * 得到文件类型，0代表图片，2代表视频。。。如果是其他文件，跳过！只有开放式播放 传来一个 -1过来表示txt文件
	 * @param filename 短文件名
	 * @return
	 */
	public int getFileType(String filename){
		String mimetype = FileTypeHandler.getMimeTypeForFile(filename);
		int filetype = FileTypeHandler.getFileTypeForMimeType(mimetype);
		if(FileTypeHandler.isVideoFileType(filetype)){
			return 2;
		}
		else if(FileTypeHandler.isImageFileType(filetype)){
			return 0;
		}else if (FileTypeHandler.isTxtFileType(filetype)) {
			return 24;  
		}
		return -1;
	}
	
	
	
	
//-----------------------------------------下面代码暂时废弃----------------------------------------------------------//	

	/**
	 * 授权失败 后通知LedActivity中的handler去 红掉屏幕。
	 * @author joychine
	 */
	private class AccreditTask extends TimerTask{
		@Override
		public void run() {
			Log.e("LedService", "授权OK?=="+pSerialPort.apkAuthorizeFlag);
			if(!pSerialPort.apkAuthorizeFlag){
				StopPlayer();
				
				Message msg = Message.obtain(null, LedProtocol.ACCREDIT_FAIL);
				try {
					rMessenger.send(msg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if(DEBUG1)Log.i("LedService", "%%%%^^^^^^^&&&&&&&((((((%%%%%%AccreditTask run 2");
				mAccredit = false;
				accreditTimer.cancel();
			}
		}
	}
	
	
}
