package com.led.player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.led.player.aidance.LedProtocol;
import com.led.player.aidance.LedTextInfoVo;
import com.led.player.aidance.PageInfoVo;
import com.led.player.aidance.ParserImage;
import com.led.player.aidance.ViewInfoClass;
import com.led.player.biz.DisplayerBiz;
import com.led.player.defview.DigitalClockView;
import com.led.player.defview.LedTextView;
import com.led.player.defview.SurfaceAnalogClockView;
import com.led.player.defview.TextSurfaceView;
import com.led.player.defview.gif3.GifImageView;
import com.led.player.moudle.APInfo;
import com.led.player.receiver.AutoConfigReciver;
import com.led.player.receiver.ConnectedReceiver;
import com.led.player.receiver.SDCardReceiver;
import com.led.player.utils.PicHandler;
import com.led.player.utils.ToastUtil;
import com.led.player.utils.WifiHotSpot;
import com.led.player.utils.WriteAPinfo;
import com.led.player.vo.PlanBrightNess;
import com.led.player.vo.PlanOnOff;
import com.led.player.window.AnalogClockWindow;
import com.led.player.window.CameraWindow;
import com.led.player.window.CountDownWindow;
import com.led.player.window.DigitalClockWindow;
import com.led.player.window.HumitureWindow;
import com.led.player.window.ImageVo;
import com.led.player.window.TextViewAttr;
import com.led.player.window.TextVo;
import com.led.player.window.UniversalWindow;
import com.led.player.window.VideoVo;

public class LedActivity extends Activity {
	public static boolean isBootNow = true;//避免启动2次apclienttimer
	public static boolean wifi_ap_state= false;
	public static boolean isNetWorkCardError = false;
	public static boolean isLedActivityOn = false;
	
	private static final boolean DEBUG = false;
	private boolean DEBUG1 = false;
	private boolean ORIGINAL_SCALE = false;
	private Context mContext = LedActivity.this;
	private AbsoluteLayout absLedLayout; 
	
	/**
	 * 同步点播窗口是否创建好了。
	 */
	public static boolean isSyncLoopWindowReady = false;
	/**
	 * 当前led状态
	 */
	public static int currLED_state = -1;
	/**
	 * 目前的播放模式有很多种，比如 ViewInfoClass中定义的几种轮播和同步播放。。。
	 */
	private int currentPlayMode = ViewInfoClass.LOOP_PLAYER_MODE;
	
	private Handler videohandler;
	private int videoWinIndex;
	private int pageIndex;
	
	private Messenger rMessenger;  
	private Messenger mMessenger;
	/**
	 * //全局页的通用窗口(这个容器数组中add了ImageView和surfaceView和TextSurfaceView来显示不同元素达到他通用目的)
	 * 当是 开放式播放时，这个值个的size是1，才一个窗口。
	 */
	private ViewAnimator [] gLayoutlists;	
	/**
	 * 基本页的通用窗口(这个容器数组中add了ImageView和surfaceView和TextSurfaceView来显示不同元素达到他通用目的)
	 */
	private ViewAnimator [] bLayoutlists;  
	private ArrayList<View> BOtherWins ;
	/***
	 *  //其它窗口的 列表存储顺序对应 窗口顺序
	 */
	private ArrayList<View> GOtherViews ; 
	private LedTextView ledTextView = null;
	private Bitmap firstscaledBitmap = null;

	private Timer sysRunFlagTimer;
	private Timer playerRunFlagTimer;

	private SerialPort pSerialPort;
	private TCPUDPserver pScoketServer;
	private PicServer pPicScoketServer;
	private FileTcpServer mFileTcpServer;
	
	private final static String ledPath = "/dev/LED";
	
	private SurfaceView mVideoView;
	private SurfaceView CameraView;
	private SurfaceHolder CameraHolder;
	private Camera camera;
	private boolean isPreview = false;
	/**
	 * 给视频窗口设置的宽度
	 */
	private int mSurWidth;
	/**
	 * 给视频窗口设置的高度
	 */
	private int mSurHight;
	private MediaPlayer mPlayer;
	private SurfaceHolder holder;
	private String mediaPath;//视频文件需要这个字段作为路径
	/**
	 * 决定设置的视频宽度
	 */
	private int mVideoWidth;
    private int mVideoHeight;
	private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    /**
     * 视频对应的surfaceview是否创建好了
     */
    private boolean surfacecreatecalled = false;
    
    private AudioManager audioMgr = null; 
	private int maxVolume ;
//	private int curVolume ;
	
	private ImageView pImageView = null;
	private boolean haspImageView = false;
	
	private Timer CountDownTimer;
	private TextView countdownText = null;
	private SimpleDateFormat    formatter    = null;
	/**
	 * 温湿度视图
	 */
	private TextView humitureText = null; 
	//private String HumidityText;
	//private String TemperatureText;
	private HumitureWindow mhumiturewindow;
	private Boolean hashumiditywindow = false;
	//private Boolean MultiLine = false;
	
	private SurfaceAnalogClockView analogClockView = null;
	private DigitalClockView digitalClockView = null;
	private AutoConfigReciver mAutoConfigReciver;
	
	private Handler mHandler = new Handler(){
		@Override  
        public void handleMessage(Message msg){
			switch(msg.what){
			case LedProtocol.CREATE_WINDOW:
//				if(sysRunFlagTimer!=null){
//					sysRunFlagTimer.cancel();
//					if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%SysRunFlagTimer.cancel()");
//					sysRunFlagTimer = null;
//				}
//				if(playerRunFlagTimer!=null){
//					playerRunFlagTimer.cancel();
//					if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%PlayerRunFlagTimer.cancel()");
//					playerRunFlagTimer = null;
//				}
//				playerRunFlagTimer = new Timer();
//				playerRunFlagTimer.schedule(new PlayerRunFlagTask(), 0, 5000);
				
				if(pSerialPort!=null){
					LedActivity.currLED_state = 2;
					pSerialPort.setLedStatus(1,2);
				}
				
	if(DEBUG)Log.i("LedActivity handleMessage", "%%%%%%%%%%CREATE_WINDOW");
				PageInfoVo pageInfoVo = (PageInfoVo)msg.obj;
				if(msg.arg1 == 0){//全局页面播放或者是 插入外设 带led_xx_xx文件夹式的播放。
					//led_xx_xx 开放式播放模式
					removeGlobalPageLayout();
					
					gLayoutlists = CreateWin(pageInfoVo);
					ledTextView = CreateLedTextView(pageInfoVo);
					if(pageInfoVo.getWindowlist()!=null){ //目前开放式播放模式没有其他窗口的。但是全局界面有啊。
						GOtherViews = CreateOtherView(pageInfoVo);
						for(int i = 0;i < GOtherViews.size();i++){
							absLedLayout.addView(GOtherViews.get(i));
						}
					}
					
					for(int i = 0;i < gLayoutlists.length;i++){
						absLedLayout.addView(gLayoutlists[i]);
					}
					if(ledTextView!=null){
						absLedLayout.addView(ledTextView);
						Log.e("LedActivity", "led开放式中有。走马灯")	;
						ledTextView.startScroll(); //走马灯现播放。额。
					}else {
						Log.e("LedActivity", "开放式播放~~~没有设置走马灯。。。");
					}
				//如果是基本页面	
				}else if(msg.arg1 == 1){//基础页面
					removeBasicPageLayout(); 
					
					if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Base Page CreateWin");
					bLayoutlists = CreateWin(pageInfoVo);
					BOtherWins = CreateOtherView(pageInfoVo);
					for(int i = 0;i < BOtherWins.size();i++){
//						System.out.println("当前："+BOtherWins.get(i));
						absLedLayout.addView(BOtherWins.get(i));
					}
					for(int i = 0;i < bLayoutlists.length;i++){
						absLedLayout.addView(bLayoutlists[i]);
					}
				}
				
				Bundle data = msg.getData();
				int playType = data.getInt("playType", ViewInfoClass.LOOP_PLAYER_MODE);
				if (playType==ViewInfoClass.SYNC_LOOP_MODE) {
					//同步点播只需要创建好窗口即好, 同步按时间点点播(也可以叫他时间轮播~~~因为可能还有点播的存在)
					isSyncLoopWindowReady = true;
					return;
				}
				
				//通用处理。。Create_Window, 顶住压力测试。容易出现空指针异常。
				Handler tHandler = pageInfoVo.getHandler();
				if (tHandler==null) {
					return;
				}
				tHandler.removeMessages(LedProtocol.DISPLAYER_NEXT_VIEW);
				tHandler.removeMessages(LedProtocol.BEGIN_DISPLAYER_VIEW);
				
				Message beginPlayMsg = new Message();
				beginPlayMsg.arg1 = playType;//把当前的播放模式传给LedService
				beginPlayMsg.what = LedProtocol.BEGIN_DISPLAYER_VIEW;
	if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%send msg BEGIN_DISPLAYER_VIEW to BaseThread");
//				tHandler.sendMessageDelayed(beginPlayMsg,15000);
				tHandler.sendMessage(beginPlayMsg); 
				
				break;
			case LedProtocol.DISPLAYER_VIEW:
//				if(pSerialPort!=null) pSerialPort.apkRunFlag = true;
				ViewInfoClass viewclass = (ViewInfoClass)msg.obj;
				try {
					if(msg.arg1 == 0){//Led_xx_xx开放式播放。或全局页面
						//Log.i("全局页面", "全局页面in ledActivity中");					
						OpinionView(gLayoutlists,viewclass);
					}else if(msg.arg1 == 1){      //基本页播放
						//if(DEBUG)Log.i("LedActivity", ".................................BLayoutlists length " +BLayoutlists.length);
						OpinionView(bLayoutlists,viewclass);
//						//同步点播， 不主动请求下一个
					}
				} catch (Exception e) {
					e.printStackTrace();//只要为了压力测试时出现的空指针异常捕获。
				}
				
				break;
			case LedProtocol.SEND_HUMIDUTY_T_APP:
				if(hashumiditywindow&&humitureText!=null){
					if(DEBUG)Log.i("LedActivity", "........SEND_HUMIDUTY_T_APP ");
					if(mhumiturewindow.getDisplayUnit()){
						if(mhumiturewindow.getHasTemperature()&&mhumiturewindow.getHasHumidity()){
							if(mhumiturewindow.getCentigrade()){
								if(mhumiturewindow.getMultiLine()){
									humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1+"℃"
											+"\n"+mhumiturewindow.getStrHumidity()+msg.arg2+"% ");
								}else{
									humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1
											+"℃ "+mhumiturewindow.getStrHumidity()+msg.arg2+"%");
								}
							}else{
								int Fahrenheit = 32 + msg.arg1*(9/5);
								if(mhumiturewindow.getMultiLine()){
									humitureText.setText(mhumiturewindow.getStrTemperature()+Fahrenheit
											+"℉"+"\n"+mhumiturewindow.getStrHumidity()+msg.arg2+"% ");
								}else{
									humitureText.setText(mhumiturewindow.getStrTemperature()+Fahrenheit
											+"℉ "+mhumiturewindow.getStrHumidity()+msg.arg2+"%");
								}
							}
							
						}else if(mhumiturewindow.getHasTemperature()){
							if(mhumiturewindow.getCentigrade()){
								humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1+"℃ ");
							}else{
								int Fahrenheit = (32 + msg.arg1*(9/5));
								humitureText.setText(mhumiturewindow.getStrTemperature()+Fahrenheit+"℉ ");
							}
							
						}else if(mhumiturewindow.getHasHumidity()){
							humitureText.setText(mhumiturewindow.getStrHumidity()+msg.arg2+"%");
						}
					}else{
						if(mhumiturewindow.getHasTemperature()&&mhumiturewindow.getHasHumidity()){
							if(mhumiturewindow.getCentigrade()){
								if(mhumiturewindow.getMultiLine()){
									humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1
											+"\n"+mhumiturewindow.getStrHumidity()+msg.arg2);
								}else{
									humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1
											+" "+mhumiturewindow.getStrHumidity()+msg.arg2);
								}
							}else{
								int Fahrenheit = 32 + msg.arg1*(9/5);
								if(mhumiturewindow.getMultiLine()){
									humitureText.setText(mhumiturewindow.getStrTemperature()+Fahrenheit
											+"\n"+mhumiturewindow.getStrHumidity()+msg.arg2);
								}else{
									humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1
											+Fahrenheit+" "+mhumiturewindow.getStrHumidity()+msg.arg2);
								}
							}
							
						}else if(mhumiturewindow.getHasTemperature()){
							if(mhumiturewindow.getCentigrade()){
								humitureText.setText(mhumiturewindow.getStrTemperature()+msg.arg1);
							}else{
								int Fahrenheit = 32 + msg.arg1*(9/5);
								humitureText.setText(mhumiturewindow.getStrTemperature()+Fahrenheit);
							}
							
						}else if(mhumiturewindow.getHasHumidity()){
							humitureText.setText(mhumiturewindow.getStrHumidity()+msg.arg2);
						}
					}
					
					
				}
				break;
			case LedProtocol.START_COUNTDOWN:
				if(DEBUG)Log.i("LedActivity", "........START_COUNTDOWN ");
				SpannableStringBuilder str = (SpannableStringBuilder)msg.obj;
				if(countdownText!=null){
					countdownText.setText(str);
				}
				break;
			case LedProtocol.STOP_COUNTDOWN:
				if(DEBUG)Log.i("LedActivity", "........STOP_COUNTDOWN ");
				absLedLayout.removeView(countdownText);
				CountDownTimer.cancel();
				countdownText = null;
				CountDownTimer= null;
				break;
			case LedProtocol.REMOVE_ALL_VIEW:
				if (ledTextView !=null ) {
					ledTextView.stopScroll();
					ledTextView = null;
				}
				removeBasicPageLayout();
				removeGlobalPageLayout();
				absLedLayout.removeAllViews();
				try {
					if(mPlayer!=null){
						mPlayer.release();
						Log.e("释放播放", "释放播放器来来来来来，喝完这一杯~~");
						mPlayer =null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		//------------------原始社会代码-------------------
				/*absLedLayout.removeAllViews(); 
				gLayoutlists = null;
				bLayoutlists = null;
//				GOtherWin = null;
//				BOtherWin = null;
				
				hashumiditywindow = false;
				humitureText = null;
				if(digitalClockView!=null){
					digitalClockView.StopClockTimer();
					digitalClockView = null;
				}
				analogClockView = null;
				if(CountDownTimer!=null){
					CountDownTimer.cancel();
					CountDownTimer = null;
				}
				if(countdownText!=null){
					absLedLayout.removeView(countdownText);
				}
				countdownText = null;
				
				try {
					if(mPlayer!=null){
						mPlayer.release();
						Log.e("释放播放", "释放播放器来来来来来");
						mPlayer =null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} */
				if(playerRunFlagTimer!=null){
					playerRunFlagTimer.cancel();
					playerRunFlagTimer = null;
				}
				if (sysRunFlagTimer!=null) {//mark add if block
					sysRunFlagTimer.cancel();
					sysRunFlagTimer = null;
				}
				//清楚标志位
				LedActivity.isSyncLoopWindowReady = false;
//				SysRunFlagTimer = new Timer();
//				SysRunFlagTimer.schedule(new SysRunFlagTask(), 0, 5000);
				break;
			case LedProtocol.ACCREDIT_FAIL:
				absLedLayout.setBackgroundColor(Color.RED);//认证失败，改变颜色,红
				if(playerRunFlagTimer!=null){
					playerRunFlagTimer.cancel();
				}
				if(sysRunFlagTimer!=null){
					sysRunFlagTimer.cancel();
				}
				break;
			case LedProtocol.STATUS_SEND_DATA_TO_UART:
//				byte[] uartData = msg.getData().getByteArray("TranslateToUart");
//				if(pSerialPort!= null)
//				{
////here&~~~~~~~			
//					pSerialPort.setNetSendData(uartData,uartData.length);
//PrintBuffer(uartData,28);
//				}
			break;
			case LedProtocol.SEND_DATA_F_UART_T_NET:    //已经在串口类中取消了这个的使用
				byte[] netData = msg.getData().getByteArray("SendDataToNet");

				if(pScoketServer != null)
				{
					pScoketServer.sendUartData(netData, netData.length);
					if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%SEND_DATA_F_UART_T_NET");
				}
			break;
			/*case LedProtocol.STATUS_RECE_JPEG_DATA:
				if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%STATUS_RECE_JPEG_DATA");
				if(pImageView==null&&haspImageView==false){
					haspImageView = true;
					pImageView = new ImageView(context);
					pImageView.setLayoutParams(new AbsoluteLayout.LayoutParams(320,240,0,0));
			        LedLayout.addView(pImageView);
					if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%pImageView add");
				}
				String path;
				path = msg.getData().getString("SetDispJpeg");
				Bitmap bitmap = getLoacalBitmap(path); 
				if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%pImageView  bitmap"+bitmap);
				pImageView.setImageBitmap(bitmap);
			break;
			case LedProtocol.STATUS_STOP_JPEG_DATA:
				if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%STATUS_STOP_JPEG_DATA");
				LedLayout.removeView(pImageView);
				haspImageView = false;
				pImageView = null;
			break;
			*/
			
			//关闭热点 close ap
			case LedProtocol.OFF_AP:
				boolean isClose = stopHotSpot(new APInfo("joyhaha", "12345678", APInfo.AP_TYPE));
//				Toast.makeText(mContext, isClose?"关闭热点成功":"关闭热点失败", 0).show();
				ToastUtil.setLTShow(mContext, isClose?"AP off ok!":"AP off fail!", 0);
				break;
			case LedProtocol.TEST_MSG:
				Toast.makeText(mContext, "非淡泊无以明志，非宁静无以致远。", 1).show();
				break;
			case LedProtocol.NETWORK_NOT_FOUND:
				ToastUtil.setLTShow(mContext, "/mnt/extsd/config/Network"+getResources().getString(R.string.wififile_not_found), 0);
				break;
			case LedProtocol.NETWORK_PARSE_FAIL:
				ToastUtil.setLTShow(mContext, getResources().getString(R.string.parse_wifixml_fail), 0);
				break;
			case LedProtocol.NETWORK_PARSE_OK:
				Serializable object = msg.getData().getSerializable("apInfo");
				if (object==null) {
					ToastUtil.setLTShow(mContext, getResources().getString(R.string.wificonf_not_found), 0);
					break;
				}
				APInfo apInfo = (APInfo) object;
				int wifi_type = apInfo.wifi_type;
				switch (wifi_type) {
				case APInfo.AP_TYPE:
					//启动热点，关闭wifi开关先
					LedActivity.isNetWorkCardError = false;
					LedActivity.wifi_ap_state=false;
					ConnectedReceiver.wifi_conn_state =true;//AP模式就要关闭终端的timer，所以把这个 终端的连接改为true让 timer都结束
					
					new Thread(){
						public void run() {
							try {
								Thread.sleep(15000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (isAPParentOn==false) {
								resumeWifi();
							}
						};
					}.start();
						
//					startHotSpot(LedActivity.this,apInfo);
					break;
				case APInfo.TM_TYPE:
					LedActivity.isNetWorkCardError = false;
					LedActivity.wifi_ap_state=true;    //为了顺利的从热点状态转化到，终端模式，因为我要让热点的timer先结束
					ConnectedReceiver.wifi_conn_state = false;
					boolean isWrited = writeAPconfig(apInfo);
					Log.e("LedActivity", "写入终端网络配置："+isWrited);
					if(isWrited==false){
						break;
					}
					//因为如果网线也介入了，那么这个 板卡需要重启下。因为那个NETWORK_STATE_CHANGED_ACTION总是被触发了，而得到的Network去我是isConnected（）为true,所以被迫重启
					//重启系统
//					new Thread(){
//						public void run() {
//							try {
//								Process process = Runtime.getRuntime().exec("su\n");
//								OutputStream os = process.getOutputStream();
//								os.write("ifconfig eth0 down".getBytes());
//								os.write("ifconfig wlan0 up".getBytes());
//								os.write("exit".getBytes());
//								os.flush();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						};
//					}.start();
				/*	ToastUtil.setLTShow(mContext, R.string.shutdown_count, Toast.LENGTH_LONG);
					new Timer().schedule(new TimerTask() {
						
						@Override
						public void run() {
							try {
								Process process = Runtime.getRuntime().exec("su\n");
								process.getOutputStream().write("reboot".getBytes());
								Log.e("执行后", "执行了命令");
							} catch (IOException e) {
								e.printStackTrace();
								Log.e("出错了", "命令执行出错了");
							}
							
						}
					},3000);*/
					
					//写入热点配置，重新开关wifi开关，就可以激活更新的文件
					//不管开没开热点，关闭热点
					WifiHotSpot.getInstance(LedActivity.this).closeWifiHot(apInfo);
//					WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//					wifiManager.setWifiEnabled(false);
//					wifiManager.setWifiEnabled(true);//热点如果打开了这个操作会一直等待热点关闭后才执行。
//					wifiManager = null;
		//2014年2月13日 15:47:28
					new Thread(){
						public void run() {
							try {
								Thread.sleep(15000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (isTMParentOn==false) {
								resumeWifi();
							}
						};
					}.start();
					break;
				case APInfo.OFF_TYPE:
					WifiManager manage = (WifiManager) LedActivity.this.getSystemService(Context.WIFI_SERVICE);
					WifiHotSpot.getInstance(LedActivity.this).closeWifiHot(apInfo);
					manage.setWifiEnabled(false);
					LedActivity.isNetWorkCardError = true;   //关闭所以的timer
					LedActivity.isAPParentOn = false;
					LedActivity.isTMParentOn = false;
					break;
				}
				break;
				//case 外面那一层的。
			case LedProtocol.TOAST_MSG:
//				ToastHandler.getInstance().toast("udp服务器还在。。。",0);
//				Toast.makeText(LedActivity.this,"udp服务器还在。。。"+msg.obj,0).show();
				break;
			case LedProtocol.OFF_SCRN_TST:
//				Toast.makeText(LedActivity.this,R.string.delay5offscrn,1).show();
				ToastUtil.setLTShow(LedActivity.this,R.string.delay5offscrn,1);
				break;
			}
		}

		/**
		 * 移除全局页面的布局
		 */
		private void removeGlobalPageLayout(){
			if(gLayoutlists!=null&&gLayoutlists.length > 0){
				for(int i = 0;i < gLayoutlists.length;i++){
					ViewAnimator v = gLayoutlists[i];
					GifImageView gifImageView = (GifImageView) v.getChildAt(LedProtocol.IMAGE_VIEW);
					if (gifImageView instanceof GifImageView) {
						recycleBm(gifImageView);
						gifImageView.recycleGif();
					}
//					absLedLayout.removeView(gLayoutlists[i]);
					absLedLayout.removeView(v);
					gLayoutlists[i] = null;
				}
				gLayoutlists = null;
			}
			
			if(GOtherViews!=null&&GOtherViews.size() > 0){
				for(int i = 0;i < GOtherViews.size();i++){
					absLedLayout.removeView(GOtherViews.get(i));
				}
				if(humitureText!=null){
					hashumiditywindow = false;
					humitureText = null;
				}
				if(digitalClockView!=null){
					digitalClockView.StopClockTimer();
					digitalClockView = null;
				}
				//add by joychine 2014年4月29日 15:31:28
				if (countdownText!=null) {
					CountDownTimer.cancel();
					countdownText = null;
				}
				analogClockView = null;
				GOtherViews = null;
			}
		}
		/**
		 * 移除即本页面布局
		 */
		private void removeBasicPageLayout() {
			if(bLayoutlists!=null&&bLayoutlists.length > 0){
				for(int i = 0;i < bLayoutlists.length;i++){
					ViewAnimator v = bLayoutlists[i];
					GifImageView gifImageView = (GifImageView) v.getChildAt(LedProtocol.IMAGE_VIEW);
					if (gifImageView instanceof GifImageView) {
						recycleBm(gifImageView);
						gifImageView.recycleGif();
					}
					absLedLayout.removeView(v);
					bLayoutlists[i] = null;
				}
				bLayoutlists = null;
			}
			
			if(BOtherWins!=null&&BOtherWins.size() > 0){
				for(int i = 0;i < BOtherWins.size();i++){
					absLedLayout.removeView(BOtherWins.get(i));
				}
				if(humitureText!=null){
					hashumiditywindow = false;
					absLedLayout.removeView(humitureText);
					humitureText = null;
				}
				if(digitalClockView!=null){
					digitalClockView.StopClockTimer();
					absLedLayout.removeView(digitalClockView);
					digitalClockView = null;
				}
				if (CountDownTimer!=null) {
					CountDownTimer.cancel();
					CountDownTimer = null;
				}
				if (countdownText!=null) {
					absLedLayout.removeView(countdownText);
					countdownText = null;
				}
			
				analogClockView = null;
				BOtherWins = null;
			}
		}
	};
	
	private Handler mPicHandler = new Handler(){
		@Override  
        public void handleMessage(Message msg){
			if(DEBUG)
				Log.i("LedActivity", "%%%%%%%%%%handlePicMessage");
			switch(msg.what)
			{
			case LedProtocol.STATUS_RECE_JPEG_DATA: 
				if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%STATUS_RECE_JPEG_DATA");
				if(pImageView==null&&haspImageView==false){
					haspImageView = true;
					pImageView = new ImageView(mContext);
					pImageView.setLayoutParams(new AbsoluteLayout.LayoutParams(320,240,0,0));
			        absLedLayout.addView(pImageView);
					if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%pImageView add");
				}
				String path= msg.getData().getString("SetDispJpeg");
				Bitmap bitmap = getLoacalBitmap(path); 
				pImageView.setImageBitmap(bitmap);
			break;
			case LedProtocol.SEND_DATA_F_UART_T_NET: //新增的case 2014年1月11日 09:07
				byte[] netData = msg.getData().getByteArray("SendDataToNet");
				if(pScoketServer != null)
				{
					pScoketServer.sendUartData(netData, netData.length);
				}
			break;
			case LedProtocol.STATUS_STOP_JPEG_DATA:
				if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%STATUS_STOP_JPEG_DATA");
				absLedLayout.removeView(pImageView);
				//mark 下面是停止播放当前图片前，清除他占用的内存
				if (pImageView!=null) {
					Drawable drawable = pImageView.getDrawable();
					if(drawable != null)
					{
						if(drawable instanceof BitmapDrawable)
						{
							Log.i("LedActivity", "图片资源回收~");
							BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
							Bitmap bitmap1 = bitmapDrawable.getBitmap( );
							if(bitmap1 != null)
								bitmap1.recycle( );
						}
					}
				}
				haspImageView = false;
				pImageView = null;
				break;
			}
		}
	};
	
	
//	private class SysRunFlagTask extends TimerTask{
//		@Override
//		public void run() {
//			Thread.currentThread().setName("timer-SysRunFlagTimer");
//			//if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%SysRunFlagTask setSysRunFlag");
//			pSerialPort.apkRunFlag = true;
//		}
//		
//	}
//	
//	private class PlayerRunFlagTask extends TimerTask{
//		@Override
//		public void run() {
//			Thread.currentThread().setName("timer-PlayerRunFlagTimer");
//			//if(DEBUG1)Log.i("LedActivity", "%%%%%%%%%%PlayerRunFlagTask setSysRunFlag");
//			pSerialPort.apkRunFlag = true;
//		}
//		
//	}
	
/**	//mark 新增的方法，， 和我下面那个。。
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
			}
	}   */
	
	/**
	 * 根据指定路径 解析出bitmap
	 * @param picPath	一个字符串代表的 pic path
	 * @return	bitmap对象
	 */
	public  Bitmap getLoacalBitmap(String picPath) {
		File picFile = new File(picPath);
		if (picFile.isDirectory()||!picFile.exists()) {
			Log.e("joybug", "给定的图片不路劲存在---in ledactivity");
			return null;
		}
			
		return PicHandler.optimizeBitmap(mContext, picPath);
	}
	
	
	/**
	 * 创建 容器(也即是说的窗口。。)，根据wininfo中存放的字段(视频，图片，文字和其他窗口)，生成多少个需要的 容器，一个容器对应一个通用窗口
	 * 其他窗口，不用这个容器。
	 * @param pageInfo
	 * @return
	 */
	public ViewAnimator [] CreateWin(PageInfoVo pageInfo){
		int mWdith,mHeight,startx,starty;
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%CreateWin");
		ArrayList<UniversalWindow> universityWindows = (ArrayList<UniversalWindow>)pageInfo.getObject();
		ViewAnimator [] windows = new ViewAnimator[pageInfo.getWinAmount()];
		for(int i = 0;i < pageInfo.getWinAmount() ;i++){
    		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%UniversalWindow = "+i);
    		windows[i] = new ViewAnimator(mContext);
    		if(universityWindows==null){//窗口集合为null代表是 led开放式播放
    			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%1 Base Page setLayoutParams");
    			mWdith = pageInfo.getWidth(); //开放式 ，试图与窗口同大小， 与页面同大小？？
    			mHeight = pageInfo.getHeigth();
    			startx = 0;
    			starty = 0;
    			windows[i].setLayoutParams(new AbsoluteLayout.LayoutParams(	mWdith, mHeight, pageInfo.getStartX(), pageInfo.getStartY()));
    			
    		}else{
    			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Base Page setLayoutParams");
    			mWdith = universityWindows.get(i).getWidth();
    			mHeight = universityWindows.get(i).getHeiht();
    			startx = universityWindows.get(i).getStartX();
    			starty = universityWindows.get(i).getStartY();
    			windows[i].setLayoutParams(new AbsoluteLayout.LayoutParams(	mWdith, mHeight,startx, starty));
    			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Width "+universityWindows.get(i).getWidth()+"Heiht "+universityWindows.get(i).getHeiht());
    			
    		}
    		
    		CreateViewForUWin(startx,starty,mWdith,mHeight,windows[i]);
    	}
		return windows;
	}
	
	/**
	 * 创建其他窗口，非 通用窗口
	 * @param pageInfo
	 * @return
	 */
	public ArrayList<View> CreateOtherView(PageInfoVo pageInfo){
		ArrayList<Integer> Windowlist = pageInfo.getWindowlist();
		ArrayList<View> windows = new ArrayList<View>();
		if(DEBUG)Log.i("LedActivity", "........CreateOtherWin Windowlist.size "+Windowlist.size());
		for(int i = 0;i < Windowlist.size();i++){
			if(DEBUG)Log.i("LedActivity", "........CreateOtherWin Windowlist.get("+i+") "+Windowlist.get(i));
			if(Windowlist.get(i)!=0){
				switch(Windowlist.get(i)){
				case 1://windowtype==0 是通用窗。 1 是时钟窗口。
					AnalogClockWindow analogclockwindow = pageInfo.getAnalogClockWindow();
					DigitalClockWindow digitalclockwindow = pageInfo.getDigitalClockWindow();
					if(analogclockwindow!=null){
						if(analogClockView==null){
							analogClockView = new SurfaceAnalogClockView(this);
							analogClockView.setLayoutParams(new AbsoluteLayout.LayoutParams(analogclockwindow.getWidth(),
									analogclockwindow.getHeiht(), analogclockwindow.getStartX(), analogclockwindow.getStartY()));
							analogClockView.Init(analogclockwindow);
							windows.add(analogClockView);
						}
					}
					if(digitalclockwindow!=null){
						if(digitalClockView==null){
							digitalClockView = new DigitalClockView(this,digitalclockwindow);
							setDigitalClock(digitalclockwindow);
							if(DEBUG)Log.i("LedActivity", "........"+digitalclockwindow.getStartX()+" "+digitalclockwindow.getStartY()
									+" "+digitalclockwindow.getWidth()+" "+digitalclockwindow.getHeiht());
							windows.add(digitalClockView);
						}
					}
					break; 
				case 2: //WindowType==2是 天气窗口：温湿度窗口。
					mhumiturewindow = pageInfo.getHumitureWindow();
					if(mhumiturewindow!=null){
						humitureText = new TextView(mContext);
						setHumitureWindow(mhumiturewindow);
						windows.add(humitureText);
					}
					break;
				case 3://倒计时窗口。
					CountDownWindow countdownwindow = pageInfo.getCountDownWindow();
					if(countdownwindow!=null){
//						if(countdownText==null){
							countdownText = new TextView(mContext);
							setCountDown(countdownwindow);
							windows.add(countdownText);
//						}else{
//							if(DEBUG)Log.i("######countdownText", "countdownText!=null");
//						}
					}
					break;
				case 4://走马灯窗口
					
					break;
				case 5://视屏设备输入窗口
					CameraWindow camerawindow = pageInfo.getCameraWindow();
//					if(DEBUG)Log.i("LedActivity", "........"+camerawindow.getStartX()+" "+camerawindow.getStartY()
//							+" "+camerawindow.getWidth()+" "+camerawindow.getHeiht());
					if(camerawindow!=null){
						if(DEBUG)Log.i("LedActivity", "........Create CameraWindow");
						CameraHolder = CameraView.getHolder();
						CameraHolder.addCallback(new CameraCB());
						CameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
						CameraView.setLayoutParams(new AbsoluteLayout.LayoutParams(camerawindow.getWidth(),
								camerawindow.getHeiht(), camerawindow.getStartX(), camerawindow.getStartY()));
						CameraHolder.setKeepScreenOn(true);
						windows.add(CameraView);
					}
					
					break;
					default:
						break;
				}
			}
			
		}
		return windows;
	}
	
	public void setDigitalClock(DigitalClockWindow digitalclockwindow){
		digitalClockView.setLayoutParams(new AbsoluteLayout.LayoutParams(digitalclockwindow.getWidth(),
				digitalclockwindow.getHeiht(), digitalclockwindow.getStartX(), digitalclockwindow.getStartY()));
		
		digitalClockView.setGravity(Gravity.CENTER);
		if(digitalclockwindow.getSingleLineShow()){
			digitalClockView.setLines(1);
		}
		setTextView(digitalClockView,digitalclockwindow.getTextViewAttr());
		
	}
	
	/**
	 * 设置倒计时窗口试图的 布局和 文字属性。
	 * @param countdownwindow
	 */
	public void setCountDown(CountDownWindow countdownwindow){
		if(DEBUG)Log.i("LedActivity", "........setCountDown");
		//countdownText = (TextView)v.findViewById(R.id.countdown);
		countdownText.setLayoutParams(new AbsoluteLayout.LayoutParams(countdownwindow.getWidth(),
				countdownwindow.getHeiht(), countdownwindow.getStartX(), countdownwindow.getStartY()));
		
		countdownText.setGravity(Gravity.CENTER);
		setTextView(countdownText,countdownwindow.getTextViewAttr());
		CountDownTimer = new Timer("countDown_timer");
		CountDownTimer.schedule(new CountDownTask(countdownwindow), 1000,1000);
		
//		absLedLayout.addView(countdownText);
	}
	
	private class CountDownTask extends TimerTask{

		private long Dvalue = 0; 
		private String FixedText;
		private String currentTime;
		private String destTime;
		private CountDownWindow mcountdownwindow;
		private TextViewAttr mtextviewattr;
		
		public CountDownTask(CountDownWindow countdownwindow){
			mcountdownwindow = countdownwindow;
			mtextviewattr = countdownwindow.getTextViewAttr();
			FixedText = mtextviewattr.getContent();
			currentTime = getCurrentTime();
			destTime = countdownwindow.getEndDate()+countdownwindow.getEndTime();
//Log.e("LedActivity", "destTime: "+destTime);			//2014年04月29日20:05:09
			try {
				Dvalue = getDvalue(currentTime,destTime,"yyyy年MM月dd日HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run(){
			if(Dvalue > 0){
				Dvalue-=1000;
				String str = null;
				String timeStr = getDateDiff(mcountdownwindow,Dvalue);
				
				SpannableStringBuilder msp;
    			int fixedlen = 0;
    			if(FixedText!=null){
    				if(mcountdownwindow.getMultiLine()){
    					str = FixedText + "\n"+timeStr;
    				}else{
    					str = FixedText + timeStr;
    				}
        			fixedlen = FixedText.length();
        			msp = new SpannableStringBuilder(str);
        			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getFixedWordClr()), 0, fixedlen, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getWordClr()), fixedlen, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        			
    			}else{
    				str = timeStr;
        			msp = new SpannableStringBuilder(str);
        			msp.setSpan(new ForegroundColorSpan(mtextviewattr.getWordClr()), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        		
    			}
				if(DEBUG)Log.i("CountDownTask", "........CountDownThread diff "+str);
				
				Message startmsg = new Message();
				startmsg.what = LedProtocol.START_COUNTDOWN;
				startmsg.obj = msp;
				mHandler.sendMessage(startmsg);
				
				
			}else{
				if(DEBUG)Log.e("设定的时间已经过了", "设定的时间已经是过去式。。。");
				Message msg = new Message();
				msg.what = LedProtocol.STOP_COUNTDOWN;
				mHandler.sendMessage(msg);
			}
			
			
		}
	}
	
	private long getDvalue(String startTime, String endTime,String format) throws ParseException{
		if(DEBUG)Log.i("LedActivity", "........getDvalue");
		long diff = 0;
		SimpleDateFormat sd = new SimpleDateFormat(format);
		//获得两个时间的毫秒时间差异
		diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		if(DEBUG)Log.i("LedActivity", "........getDvalue diff");
		return diff;
	}
	
	/**
	 * 得到要显示的倒计时字符串。
	 * @param countdownwindow
	 * @param Dvalue
	 * @return
	 * @author joychine
	 */
	private String getDateDiff(CountDownWindow countdownwindow,long Dvalue){
		String str;
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数
		
		long day = Dvalue/nd;//计算差多少天
		long hour = Dvalue%nd/nh;//计算差多少小时
		long min = Dvalue%nd%nh/nm;//计算差多少分钟
		long sec = Dvalue%nd%nh%nm/ns;//计算差多少秒
		if(countdownwindow.getTextViewAttr().getLanguage()){
			if(countdownwindow.getShowDate()){
				str = day+"天";
			}else{
				str = "";
			}
			if(countdownwindow.getShowHour()){
				str += hour+"小时";
			}else{
				str += "";
			}
			if(countdownwindow.getShowMins()){
				str += min+"分钟";
			}else{
				str += "";
			}
			if(countdownwindow.getShowSeconds()){
				str += sec+"秒";
			}else{
				str += "";
			}
			return str;

		}else{
			if(countdownwindow.getShowDate()){
				str = day+"days";
			}else{
				str = "";
			}
			if(countdownwindow.getShowHour()){
				str += hour+"hours";
			}else{
				str += "";
			}
			if(countdownwindow.getShowMins()){
				str += min+"minutes";
			}else{
				str += "";
			}
			if(countdownwindow.getShowSeconds()){
				str += sec+"seconds";
			}else{
				str += "";
			}
			return str;
		}
		
	}
	
	private String getCurrentTime(){
		if (formatter==null) {
			formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日HH:mm:ss"); 
		}
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间        
		String    str    =    formatter.format(curDate); 
		return str;
	}
	
	/**
	 * 设置温湿度窗口的温湿度属性, 和温湿度窗口试图的布局。
	 * @param humiturewindow
	 */
	public void setHumitureWindow(HumitureWindow humiturewindow){
		if(DEBUG)Log.i("LedActivity", "........setHumitureWindow");
		hashumiditywindow = true;
		humitureText.setLayoutParams(new AbsoluteLayout.LayoutParams(humiturewindow.getWidth(),
				humiturewindow.getHeight(),humiturewindow.getStartX(),humiturewindow.getStartY()));
		if(DEBUG)Log.i("LedActivity", "........"+humiturewindow.getWidth()+" "+humiturewindow.getHeight()+" "+humiturewindow.getStartX()+" "+humiturewindow.getStartY());
		
//		humitureText.setGravity(Gravity.CENTER);
		humitureText.setGravity(humiturewindow.getShowType()==0?Gravity.LEFT:humiturewindow.getShowType()==1?Gravity.CENTER:Gravity.RIGHT);
		TextViewAttr textViewAttr = humiturewindow.getTextViewAttr();
		humitureText.setTextColor(textViewAttr.getWordClr());
		setTextView(humitureText,textViewAttr);
		
		Message msg = new Message();
		msg.what = LedProtocol.SEND_HUMIDUTY_T_APP;
		msg.arg1 = 25;  //默认显示的温度和
		msg.arg2 = 25;//默认 湿度  值
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 设置并得到 开放式 播放时那个唯一的 走马灯文字视图。
	 * @param pageInfoVo
	 * @return LedTextView
	 */
	@SuppressWarnings("deprecation")
	public LedTextView CreateLedTextView(PageInfoVo pageInfoVo){
		LedTextInfoVo ledTextInfo = (LedTextInfoVo)pageInfoVo.getLedTextInfo();
		LedTextView ledTextView = null;
		if(ledTextInfo!=null){//开始时播放的专属。
			ledTextView = new LedTextView(this);
			
			ledTextView.setSingleLine(true);
			ledTextView.setText(ledTextInfo.getTextContent());
			ledTextView.setTextSize(ledTextInfo.getSize()>0?ledTextInfo.getSize():32);
			ledTextView.setBackgroundColor(Color.TRANSPARENT);
			
			Paint paint =  ledTextView.getPaint();
//			int width = 0;
//			if(ledTextInfo.getTextContent()!=null){
//				width = (int)paint.measureText(ledTextInfo.getTextContent());
//			}
//			if(ledTextInfo.getMove()==0){
//				int StartX = 0;
//				if(width < ledTextInfo.getSurWidth()){
//					StartX = (int)((ledTextInfo.getSurWidth() - width)/2);
//					ledTextView.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,
//							StartX,ledTextInfo.getStartY()));
//				}else{
//					ledTextView.setLayoutParams(new AbsoluteLayout.LayoutParams(ledTextInfo.getSurWidth(),LayoutParams.WRAP_CONTENT,
//							StartX,ledTextInfo.getStartY()));
//				}
//				ledTextView.setStatic(true);
//				ledTextView.init(width);
//			}else{
//				ledTextView.setLayoutParams(new AbsoluteLayout.LayoutParams(ledTextInfo.getSurWidth(),LayoutParams.WRAP_CONTENT,
//						0,ledTextInfo.getStartY()));
//				ledTextView.setStatic(false);
//				ledTextView.init(ledTextInfo.getSurWidth());
//			}
			
			ledTextView.setLayoutParams(new AbsoluteLayout.LayoutParams(ledTextInfo.getSurWidth(),LayoutParams.WRAP_CONTENT,
					0,ledTextInfo.getStartY()));
			if(ledTextInfo.getMove()==0){ //走马灯静止不动。。。。。。
				ledTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP); //mark joychine 修改2014年5月16日 18:04:50 去掉注释
				ledTextView.setSingleLine(false);
				ledTextView.setStatic(true);
			}else{
				ledTextView.setStatic(false);
				ledTextView.init(ledTextInfo.getSurWidth());
			}
			
			ledTextView.setWalkSpeed(ledTextInfo.getMove());
			
			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%ledTextInfo.getColor()"+ledTextInfo.getColor());
			switch(ledTextInfo.getColor()){
			case 0:
				paint.setColor(Color.RED);
				ledTextView.setTextColor(Color.RED);
				break;
			case 1:
				paint.setColor(Color.GREEN);
				ledTextView.setTextColor(Color.GREEN);
				break;
			case 2:
				paint.setColor(Color.BLUE);
				ledTextView.setTextColor(Color.BLUE);
				break;
			case 3:
				paint.setColor(Color.WHITE);
				ledTextView.setTextColor(Color.WHITE);
				break;
			case 4:
				paint.setColor(Color.YELLOW);
				ledTextView.setTextColor(Color.YELLOW);
				break;
			case 5:
				paint.setColor(Color.MAGENTA);
				ledTextView.setTextColor(Color.MAGENTA);
				break;
			case 6:
				paint.setColor(Color.CYAN);
				ledTextView.setTextColor(Color.CYAN);
				break;
			}
//			setFaceName(paint,ledTextInfo.getFaceName());
		}
		return ledTextView;
	}
	
	/**
	 * 为 window 创建显示资源需要的 view层。
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param viewAnimator
	 */
	public void CreateViewForUWin(int startX,int startY,int width,int height,ViewAnimator viewAnimator){
		
//		XImageView imageview = new XImageView(mContext);
//		ImageView imageview = new ImageView(mContext);
		GifImageView imageview = new GifImageView(mContext);
    	imageview.setLayoutParams(new ViewAnimator.LayoutParams(width,height));
    	//imageview.setBackgroundResource(R.drawable.background);
    	if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%imageview Width "+width+"Height"+height);
    	viewAnimator.addView(imageview,LedProtocol.IMAGE_VIEW,imageview.getLayoutParams());
    	if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Create ImageView");
		
    	SurfaceView videoview = new SurfaceView(mContext);
    	videoview.setLayoutParams(new ViewAnimator.LayoutParams(width,height));
    	if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%videoview Width "+width+"Height"+height);
    	viewAnimator.addView(videoview, LedProtocol.VIDEO_VIEW, videoview.getLayoutParams());
//    	videoview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	//videoview.getHolder().setFixedSize(720, 576);
    	videoview.getHolder().addCallback(new SurfaceListener());
    	
    	if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Create SurfaceView");
    	
    	//MarqueeText textview = new MarqueeText(context);
    	TextSurfaceView textview = new TextSurfaceView(this);
    	textview.setLayoutParams(new ViewAnimator.LayoutParams(width,height));
    	viewAnimator.addView(textview,LedProtocol.TEXT_VIEW,textview.getLayoutParams());
    	textview.setSurfaceView();
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%Create TextView");
		
	}
	
	private class SurfaceListener implements SurfaceHolder.Callback
	{
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
			if(DEBUG)Log.i("surfaceChanged", "%%%%%%%%%%*********&&&&&&&&&&@@@@@@@@@@@@@@@@surfaceChanged");
		}

		public void surfaceCreated(SurfaceHolder holder) {
			if(DEBUG)Log.i("surfaceCreated", "%%%%%%%%%%*********&&&&&&&&&&@@@@@@@@@@@@@@@@surfaceCreated");
			PlayerVideo(); 
			surfacecreatecalled = true;
//Log.e("LedActivity", "surfaceView已经被创建好了。。。"+new SimpleDateFormat("yyyyMMdd HHmmssSSS").format(new Date()))			;
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
//			if(true)Log.i("surfaceDestroyed", "------------------@@surfaceDestroyed------------");
			mPlayer.release();
			mPlayer =null;
			surfacecreatecalled = false;
		}
	}
	
	/**
	 * 打开某个view来显示元素
	 * @param layoutlists
	 * @param viewclass
	 */
	public void OpinionView(ViewAnimator [] layoutlists,ViewInfoClass viewclass){
		int playType = viewclass.getPlayType();
		this.currentPlayMode = playType;//给当前的播放模式赋值。
		switch(viewclass.getFileType()){
		case 0: //Led_xx_xx开放式播放/全局页面，0参数代表图片资源文件。
			layoutlists[viewclass.getWinIndex()].setDisplayedChild(LedProtocol.IMAGE_VIEW);
			try {
				DisplayerImageView(layoutlists,viewclass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (playType==ViewInfoClass.LOOP_PLAYER_MODE) {//轮播
				Message backmsg = Message.obtain();
				backmsg.arg1 = viewclass.getPageIndex();
				backmsg.arg2 = viewclass.getWinIndex();
				backmsg.obj = firstscaledBitmap; 
				backmsg.what = LedProtocol.DISPLAYER_NEXT_VIEW;
				if(viewclass.getObject()!=null){//图片的停留时间等于= statyline+textspeed  错误搞法
//					viewclass.getHandler().sendMessageDelayed(backmsg,(((ImageVo)viewclass.getObject()).getStayLine()*1000)+((ImageVo)viewclass.getObject()).getTxtSpeed());
//	Log.i("LedActivity", "预计"+(((ImageVo)viewclass.getObject()).getStayLine()*1000)+"毫秒后播放下一个资源")				;
					viewclass.getHandler().sendMessageDelayed(backmsg,(((ImageVo)viewclass.getObject()).getStayLine()*1000));
					//总会在这个点播时出现anr。。。。持续10秒的样子。什么情况。？而且在这个语句之后出现anr，注释DisplayerImageView(layoutlists,viewclass);就不会出现，说明与图片处理不当有光
					//有时间了来处理。mark

				}else{//为null时是开放式播放。
					viewclass.getHandler().sendMessageDelayed(backmsg,(viewclass.getStayLine())*1000); //mark +1是干嘛的。开放式默认10秒就好了，难道默认让他11秒？
				}
			}else if (playType==ViewInfoClass.SYNC_LOOP_MODE) {
				//同步点播，不用主动请求下一个页面
				
			}
			
			
			break;
		case 1:
		case 2://Led_xx_xx开放式播放时，2参数代表视频资源文件。 (其实1,2都是视频文件)
			DiaplayerVideoView(layoutlists,viewclass);
			break;
		case 24: //如果是走马灯。
			if (playType==ViewInfoClass.LOOP_PLAYER_MODE) {
				Message backmsg1 = new Message();
				backmsg1.arg1 = viewclass.getPageIndex();
				backmsg1.arg2 = viewclass.getWinIndex();
				backmsg1.obj = null;
				backmsg1.what = LedProtocol.DISPLAYER_NEXT_VIEW;
//				viewclass.getHandler().sendMessageDelayed(backmsg1,((TextVo)viewclass.getObject()).getProLen()*1000 + 2000);
//				Log.e("LedActivity", "----------走马灯的。。。这个handler: "+viewclass.getHandler())	;
				viewclass.getHandler().sendMessageDelayed(backmsg1,((TextVo)viewclass.getObject()).getProLen()*1000);//changed to this line
				DisplayerTextView(layoutlists,viewclass);
			}else if (playType==ViewInfoClass.SYNC_LOOP_MODE) {
				//同步点播不往下走的，不回传消息
			}
			
			break;
		default: //led_xxx_xxx开放式播放时，如果是txt也传过来了，因此跳过，那边穿的type是10， 傻逼做法
			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%default");
			Message backmsg2 = new Message();
			backmsg2.arg1 = viewclass.getPageIndex();
			backmsg2.arg2 = viewclass.getWinIndex();
			backmsg2.obj = null;
			backmsg2.what = LedProtocol.DISPLAYER_NEXT_VIEW;
			
			viewclass.getHandler().sendMessage(backmsg2);
			break;
		}
	}
	
	/**
	 * 真正的播放图片资源的地方。显示出图片的view
	 * @param layoutlists
	 * @param viewinfoclass
	 */
	public void DisplayerImageView(ViewAnimator [] layoutlists,ViewInfoClass viewinfoclass){
		ImageVo imageVo = (ImageVo)viewinfoclass.getObject();
		int winIndex = viewinfoclass.getWinIndex();
		
		ImageView imageview = (ImageView)layoutlists[winIndex].getChildAt(LedProtocol.IMAGE_VIEW);
	
		int imageViewWith = imageview.getWidth();
		int imageViewHeight = imageview.getHeight();
		
		Bitmap bitmap = null;
		Bitmap tempscaledBitmap = null; 
		String fileAbsPath = null;   //当前需要的资源文件路径
		if(imageVo!=null){
			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%DisplayerImageView imagewindow!=null");
			if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%DisplayerImageView imageview.getHeight() "+imageViewHeight);
            layoutlists[winIndex].setBackgroundColor(imageVo.getBgClr());
            fileAbsPath = viewinfoclass.getFilePath()+"/"+imageVo.getFilePath();
            if (new File(fileAbsPath).exists() == false) {
				return;
			}
            int scaleType = imageVo.getDescription();
            scaleType = scaleType==1?scaleType:0;
			if(scaleType==0){
				bitmap = ParserImage.decodeFile(fileAbsPath,imageViewWith,imageViewHeight);
				if(bitmap!=null){
					tempscaledBitmap = ParserImage.getReduceBitmap(bitmap,imageViewWith,imageViewHeight);
		    		if (tempscaledBitmap!=bitmap) {
						bitmap.recycle();
						bitmap = null;
					}
		    	} 
			}else if(scaleType ==1){//图片的原始比例。
				bitmap = ParserImage.decodeFile(fileAbsPath,imageViewWith,imageViewHeight);
				if(bitmap!=null){
					tempscaledBitmap = ParserImage.createScaledBitmap(bitmap,imageViewWith,imageViewHeight);
		    		if (tempscaledBitmap!=bitmap) {
						bitmap.recycle();
						bitmap = null;
					}
		    		
		    	}
			}else {
				//其他比例
			}
			
			if(imageVo.getTransparent()){
				layoutlists[winIndex].setBackgroundColor(Color.argb(0, 0, 0, 0));
				//imageview.setBackgroundColor(Color.argb(0, 0, 0, 0)); 
			}

		}else{//led_xx_xx开放式播放的 obj传过来的ImageVo是个null， 而且资源文件的路径还是个全的…………o(︶︿︶)o 
			layoutlists[winIndex].setBackgroundColor(Color.BLACK); //mark 开放式播放图片窗口背景给黑色。
            fileAbsPath = viewinfoclass.getFilePath();
            if (new File(fileAbsPath).exists() == false) {
				return;
			}
            bitmap = ParserImage.decodeFile(fileAbsPath,imageViewWith,imageViewHeight);
			if(bitmap!=null){
				tempscaledBitmap = ParserImage.getReduceBitmap(bitmap,imageViewWith,imageViewHeight);
	    		if (tempscaledBitmap!=bitmap) {
					bitmap.recycle();
					bitmap = null;
				}
	    	}
		}
		
		SetAnimation( (GifImageView) imageview, fileAbsPath,tempscaledBitmap,imageVo);
		
//		firstscaledBitmap = tempscaledBitmap;
	}
	
	/**
	 * 给图片视图加特效
	 */
	private void SetAnimation(GifImageView imageview,String fileAbsPath,Bitmap tempscaledBitmap,ImageVo imageVo){
		int effect;
		int speed;
		BitmapDrawable bitdrawable =null;
		//判断是否是gif图片
		if (fileAbsPath.endsWith("null")) {
			Log.e("LedService", "------EEEEroor-----------图片路径为空，是："+fileAbsPath)		;
			return;
		}
		boolean isGif = fileAbsPath.substring(fileAbsPath.lastIndexOf("."),fileAbsPath.length()).equalsIgnoreCase(".gif");
		if (isGif) {
			try {
				recycleBm(imageview);		
				imageview.recycleGif();
				imageview.setScaleType(ScaleType.FIT_XY);
				imageview.setCurrentGif(Uri.fromFile(new File(fileAbsPath)));
			} catch (Exception e) {
				e.printStackTrace();
				return; //不要再进入特效代码了，不然会让定时器一直不退出
			}
		}else {
			recycleBm(imageview);
			//mark 严重问题，gif的下一个静态图不会显示，下下才能显示出。
			imageview.recycleGif();
			imageview.setImageBitmap(null);
			if(imageVo!=null){
				if (imageVo.getDescription()==1) {
					imageview.setScaleType(ScaleType.FIT_CENTER);
				}
			}
			bitdrawable = new BitmapDrawable(getResources(),tempscaledBitmap);
//			bitdrawable = new BitmapDrawable(tempscaledBitmap);
		}

		if(imageVo!=null){
			if(imageVo.getEffects() == 255){
				effect = (int)(Math.random()*16);//让特效随机
				if(effect==0){
					effect = 1;
				}
			}else{
				effect = imageVo.getEffects();
			}
			speed = imageVo.getTxtSpeed();
//			Log.e("图片指定的特效时长", "图片指定的特效"+effect+"特效速度: "+speed);
		}else{
			effect = (int)(Math.random()*16);
			if(effect==0){
				effect = 1;//随机特效，总有特效默认是渐渐的显示，淡入
			}
			speed = 501;
		}
		if (isGif) {
			effect = 0;//gif图片 不做特效。
		}
//		imageview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);  //你好mark。add  2014年3月31日 16:08:52
		switch(effect){
		case 0:
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
			break;
		case 1:
	    	AlphaAnimation Alpha = new AlphaAnimation(0.0f,1.0f);
	    	Alpha.setDuration(speed);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(Alpha);
			break;
		case 2:
			ScaleAnimation Scale = new ScaleAnimation(0.1f,1.0f,0.1f,1.0f,
	    			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	    	Scale.setDuration(speed);
//	    	Scale.setDuration(10);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(Scale);
			break;
		case 3:
	    	RotateAnimation Rotate = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
	    			0.5f,Animation.RELATIVE_TO_SELF,0.5f);
	    	Rotate.setDuration(speed);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(Rotate);
			break;
		case 4:
			TranslateAnimation leftTranslate = new TranslateAnimation(imageview.getWidth(),0,0,0);
	    	leftTranslate.setDuration(speed);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(leftTranslate);
			break;
		case 5:
			TranslateAnimation rightTranslate = new TranslateAnimation(-imageview.getWidth(),0,0,0);
	    	rightTranslate.setDuration(speed);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(rightTranslate);
			break;
		case 6:
			TranslateAnimation belowTranslate = new TranslateAnimation(0,0,-imageview.getHeight(),0);
	    	belowTranslate.setDuration(speed);
//	    	imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(belowTranslate);
			break;
		case 7:
			TranslateAnimation topTranslate = new TranslateAnimation(0,0,imageview.getHeight(),0);
	    	topTranslate.setDuration(speed);
//		    imageview.setBackgroundDrawable(bitdrawable);
	    	if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(topTranslate);
			break;
		case 8:
			TranslateAnimation lefttopTranslate = new TranslateAnimation(-imageview.getWidth(),0,-imageview.getHeight(),0);
			lefttopTranslate.setDuration(speed);
//			imageview.setBackgroundDrawable(bitdrawable);
			if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(lefttopTranslate);
			break;
		case 9:
			TranslateAnimation leftbelowTranslate = new TranslateAnimation(-imageview.getWidth(),0,imageview.getHeight(),0);
			leftbelowTranslate.setDuration(speed);
//			imageview.setBackgroundDrawable(bitdrawable);
			if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(leftbelowTranslate);
			break;
		case 10:
			TranslateAnimation righttopTranslate = new TranslateAnimation(imageview.getWidth(),0,-imageview.getHeight(),0);
			righttopTranslate.setDuration(speed);
			if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(righttopTranslate);
			break;
		case 11:
			TranslateAnimation rightbelowTranslate = new TranslateAnimation(imageview.getWidth(),0,imageview.getHeight(),0);
			rightbelowTranslate.setDuration(speed);
//			imageview.setBackgroundDrawable(bitdrawable);
			if (isGif==false) imageview.setImageBitmap(tempscaledBitmap);
	    	imageview.startAnimation(rightbelowTranslate);
			break;
		case 12:
			ClipDrawable CHDrawable = new ClipDrawable(bitdrawable, Gravity.CENTER_HORIZONTAL, ClipDrawable.HORIZONTAL);
			//设置特效的drawable回收时间和特效停止时间
			setStopEffect(CHDrawable,imageVo, imageview, speed);
			break;
		case 13:
			ClipDrawable CVDrawable = new ClipDrawable(bitdrawable, Gravity.CENTER_HORIZONTAL, ClipDrawable.VERTICAL);
			setStopEffect(CVDrawable,imageVo, imageview, speed);
			break;
		case 14:
			ClipDrawable RHDrawable = new ClipDrawable(bitdrawable, Gravity.RIGHT, ClipDrawable.HORIZONTAL);
			setStopEffect(RHDrawable,imageVo, imageview, speed);
			break;
		case 15:
			ClipDrawable LHDrawable = new ClipDrawable(bitdrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
			setStopEffect(LHDrawable,imageVo, imageview, speed);
			break;
		case 16:
			ClipDrawable TVDrawable = new ClipDrawable(bitdrawable, Gravity.TOP, ClipDrawable.VERTICAL);
			setStopEffect(TVDrawable,imageVo, imageview, speed);
			break;
		case 17:
			ClipDrawable BVDrawable = new ClipDrawable(bitdrawable, Gravity.BOTTOM, ClipDrawable.VERTICAL);
			setStopEffect(BVDrawable,imageVo, imageview, speed);
			break;
		default:
			
			break;
		}
//		if (firstscaledBitmap!=null&&firstscaledBitmap.isRecycled()==false) {//不能这样，可能有多个窗口，但是现在只有一个对象。
//			firstscaledBitmap.recycle();
//			firstscaledBitmap = null;
//		}
		
		
	}
	
	private void setStopEffect(ClipDrawable drawable,final ImageVo imageVo,ImageView imageview,int speed) {
		drawable.setLevel(0);
		final Timer animationTimer=new Timer();
		final ImageTask iamgeTask = new ImageTask(drawable,imageview,speed);
		animationTimer.schedule(iamgeTask, 0);
		new Thread("clear_animation_thread"){
			@Override
			public void run() {
				int waitTime = 0;
				if (imageVo!=null) {
					waitTime = imageVo.getStayLine()*1000;
				}else {
					waitTime = 10000;
				}
				try {
					Thread.sleep(waitTime);
					iamgeTask.txDrawable = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
	}


	/**
	 * 收回bitmap
	 * @param imageView
	 */
	private void recycleBm(ImageView imageView){
		if (imageView!=null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable==null?null:bitmapDrawable.getBitmap();
				if (bitmap!=null&&bitmap.isRecycled()==false) {
					imageView.setImageDrawable(null);
					imageView.setImageDrawable(null);
//					Log.e("需要回收", "回收方法中需要回收：。。。");				
					bitmap.recycle();
					bitmap = null;
					bitmapDrawable = null;
				}
			}
		}
	}
	
	private class ImageTask extends TimerTask 
    {
		public ClipDrawable txDrawable;
		public ImageView imageview;
		public int speed;
		ImageTask(ClipDrawable mdrawable,ImageView mimageview,int mspeed){
			txDrawable = mdrawable; 
			imageview = mimageview;
			speed = mspeed;
		}
        @Override 
        public void run()  
        {
        	Thread.currentThread().setName("animation_timer");
        	while(txDrawable!=null&&txDrawable.getLevel() < 10000){
        		mHandler.post(new UpdateRun(txDrawable,imageview));
        		try {
					Thread.sleep(speed/50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	cancel();
        }
    } 
	private class UpdateRun implements Runnable{
		
		private ClipDrawable drawable;
		private ImageView imageview;
		UpdateRun(ClipDrawable mdrawable,ImageView mimageview){
			drawable = mdrawable;
			imageview = mimageview;
		}

		public void run() {
			if (drawable==null) {
				return;
			}
			drawable.setLevel(drawable.getLevel()+200); 
			imageview.setImageDrawable(drawable);
		}
		
	}
	
	/**
	 * 显示播放视频文件的view, 视频播放完毕后会自动请求下一个资源(基本上视频都是耗时最长的一个资源文件，这个时候一般都是切换到下一个页面了)
	 * @param layoutlists
	 * @param viewinfoclass
	 */
	public void DiaplayerVideoView(ViewAnimator [] layoutlists,ViewInfoClass viewinfoclass){
		VideoVo videoVo = (VideoVo)viewinfoclass.getObject();
		videohandler = viewinfoclass.getHandler();
		videoWinIndex = viewinfoclass.getWinIndex();
		pageIndex = viewinfoclass.getPageIndex();
		
		mSurWidth = layoutlists[videoWinIndex].getWidth();
		mSurHight = layoutlists[videoWinIndex].getHeight();
		
		//layoutlists[viewinfoclass.getWinIndex()].setBackgroundResource(R.drawable.background);
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%DiaplayerVideoView init mPlayer");
		mVideoView = (SurfaceView)layoutlists[videoWinIndex].getChildAt(LedProtocol.VIDEO_VIEW);
		holder = mVideoView.getHolder();
		
		//mVideoView.setBackgroundResource(R.drawable.background);
		
		if(videoVo!=null){
			mediaPath = viewinfoclass.getFilePath()+"/"+videoVo.getFilePath();
			
			//curVolume = (videowindow.getVoiceValue()*maxVolume)/100;
			if(videoVo.getVideoProportion()==1){
				ORIGINAL_SCALE = true;
			}
		}
		else{
			mediaPath = viewinfoclass.getFilePath();
		}
		
		//audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND); 
		
		if(surfacecreatecalled){
//			Log.e("surfacecreatecalled", "--------surfacecreatecalled准备好了-------------");
//			if(true)Log.i("LedActivity", "准备好surfaceview  surfacecreatecalled = true 时间是: "+new SimpleDateFormat("yyyyMMdd HHmmssSSS").format(new Date()));
			PlayerVideo();
		}else {
//			Log.e("还没准备好了", "还没准备好surfaceview");
		}
			
		layoutlists[videoWinIndex].setDisplayedChild(LedProtocol.VIDEO_VIEW);
		
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%DiaplayerVideoView  viewinfoclass.getWinIndex():"+viewinfoclass.getWinIndex());
	}
	
	/**
	 * 显示走马灯。 全局页面和基本页面
	 * @param layoutlists
	 * @param viewinfoclass
	 */
	public void DisplayerTextView(ViewAnimator [] layoutlists,ViewInfoClass viewinfoclass){
		TextVo textVo = (TextVo)viewinfoclass.getObject();
		int index = viewinfoclass.getWinIndex();
		TextSurfaceView textview = (TextSurfaceView)layoutlists[index].getChildAt(LedProtocol.TEXT_VIEW);
		
		textview.init(layoutlists[index].getWidth(),textVo,textVo.getProLen()*1000);
		
		layoutlists[index].setDisplayedChild(LedProtocol.TEXT_VIEW);
	}
	
	/**
	 * 设置textView的属性。神马温湿度窗口、倒计时窗口。。
	 * @param textview
	 * @param textviewattr
	 */
	private void setTextView(TextView textview,TextViewAttr textviewattr){
		Paint txtPaint = textview.getPaint();
		textview.setTextSize(TypedValue.COMPLEX_UNIT_PX,textviewattr.getHeight());
		textview.setBackgroundColor(textviewattr.getBgClr());
//		setFaceName(txtPaint,textviewattr.getFaceName());	2014年2月18日 16:41:36 mark 注释掉
		if(textviewattr.getItalic()==1){ //modify by joychine from 255 to 1 2014年4月29日 19:43:08
			txtPaint.setTextSkewX(-0.5f);
		}
		if(textviewattr.getUnderline()==1){
			txtPaint.setUnderlineText(true);
		}
		if(textviewattr.getWeight()==700){
			txtPaint.setFakeBoldText(true);
		}
		
	}
	
	private void setFaceName(Paint textpaint,int typeface){
		String texttype  = "";
		switch(typeface){
		case 0:
			texttype = "simsunb.ttf";   //默认字体
			break;
		case 1:
			texttype = "simfang.ttf";
			break;
		case 2:
			texttype = "black.ttf";
			break;
		case 3:
			texttype = "simkai.ttf";
			break;
		case 4:
			texttype = "simsun.ttc";
			break;
		case 5:
			texttype = "msyhbd.ttf";
			break;
		case 6:
			texttype = "xinsimsun.ttc";
			break;
		case 7:
			//texttype = "";
			break;
		case 8:
			//texttype = "";
			break;
		case 9:
			texttype = "cambria.ttc";
			break;
		case 10:
			//texttype = "";
			break;
		case 11:
			//texttype = "";
			break;
		case 12:
			//texttype = "";
			break;
		case 13:
			texttype = "seguisym.ttf";
			break;
		case 14:
			texttype = "extsimsunb.ttf";
			break;
		case 15:
			//texttype = "symbol.ttf";
			break;
		case 16:
			//texttype = "";
			break;
		case 17:
			texttype = "webdings.ttf";
			break;
		case 18:
			texttype = "wingding.ttf";
			break;
		case 19:
			texttype = "simfang.ttf";
			break;
		case 20:
			texttype = "black.ttf";
			break;
		case 21:
			texttype = "simkai.ttf";
			break;
		case 22:
			texttype = "simsun.ttc";
			break;
		case 23:
			texttype = "msyhbd.ttf";
			break;
		case 24:
			texttype = "xinsimsun.ttc";
			break;
		default:
			texttype = "simsunb.ttf";
		}
		
//		textpaint.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/simhei.ttf"));
//		textpaint.setTypeface(Typeface.createFromFile("/mnt/sdcard/fonts/"+texttype));
Log.e("joybug", texttype+" 字体类型");		
		Typeface typeface_o = Typeface.createFromAsset(getAssets(),"fonts/simfang.ttf");
		textpaint.setTypeface(typeface_o);
	}
	

	
	/**
	 * 绑定启动LedService 的Connection对象。携带mMessenger用。通知LedService检测程序上次退出前要继续播放的东西。
	 */
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder iBinder) {
			
			mMessenger = new Messenger(mHandler);
			rMessenger = ((LedService.MyBinder)iBinder).getMessenger();
			Message msg = Message.obtain(null, LedProtocol.MSG_SERVICE_APK); 
			msg.replyTo = mMessenger;//mark 把当前的messenger带给LedService
			msg.obj = pSerialPort;
			try {
				rMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
		public void onServiceDisconnected(ComponentName className) {
			rMessenger = null;
		}	
	};
	
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
	};
	protected void onPause() {
		super.onPause();
		mWakeLock.release();
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	};
	private PowerManager.WakeLock mWakeLock = null;
	private Intent intent= null; // 启动LedService的意图
	public static boolean isTMParentOn=false; //终端timer如果存在一个就不在启动宁外一个
	public static boolean isAPParentOn = false;  //父timer是不是还在。如果在子timer就不用启动先。
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		closeIntenet();
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK , "WakeLock");
		mContext = LedActivity.this;
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		isLedActivityOn = true;
//		WifiManager wifim = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//		ToastUtil.setRBShow(this, wifim.getWifiState()==4?getResources().getString(R.string.wlan_card_state_innormal):wifim.getWifiState()+"", 1);
//		Log.e("LedActivity", "刚启动app当前wifi状态："+wifim.getWifiState());
		//注册tfcard的监听广播接收器
		registerTFReaceiver();
		LedActivity.isBootNow=false;
		resumeWifi();
		//注册wifi连接广播接收器
		registerWifiConnReceiver();
		//注册wifi热点变化接收器
		registerAPListener();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN);//mi 对setFlag不通过，要不不设置android版本在xml中也能启动。
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横竖屏设置
		setContentView(R.layout.led_player);
		mContext = LedActivity.this;
		CameraView = new SurfaceView(mContext);
		
        absLedLayout = (AbsoluteLayout)findViewById(R.id.ledproject);
        Drawable drawable = PicHandler.optimizePic(mContext, R.drawable.background);
//        Drawable drawable = PicHandler.optimizePic(context, R.drawable.haha);
//        LedLayout.setBackground(drawable);	//这个方法只能是4.1.2以上
        absLedLayout.setBackgroundDrawable(drawable);
        
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
//        curVolume = (maxVolume*60)/100;
//        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_PLAY_SOUND); 
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND); 
        
     // comment by joychine 2014年5月1日 20:27:59 to install apk in phone
        mountLedDriver();
        try {
			pSerialPort = new SerialPort(0,LedActivity.this);
			LedActivity.currLED_state = 0; //程序一旦跑起来了，就常量
			pSerialPort.setLedStatus(1,0);
			pSerialPort.setLedStatus(2,0);
				
		} catch (IOException e) {
			Log.e("SerialPortTest","error exit");
			try {
				FileOutputStream fos  = new FileOutputStream("/mnt/sdcard/error.txt");
				String str = "构建串口对象失败了。。。"+new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date());
				fos.write(str.getBytes("utf-8"));
				
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			e.printStackTrace();
			//finish();   暂时不 关闭 mark
			System.exit(-1);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
        pScoketServer = new TCPUDPserver(mHandler,this,pSerialPort);	//启动tcpudp服务器。
        pPicScoketServer = new PicServer(mPicHandler,this);				//启动PicSercver服务器	
        mFileTcpServer = new FileTcpServer(this); 						//启动文件传输服务器。
        
        pSerialPort.mTcpudPserver = pScoketServer; //把TcpudpServer的引用送给serialPort  mark
        
        pSerialPort.apkRunFlag = true;
         
        //启动LedService服务 
        intent=new Intent();
    	intent.setClass(LedActivity.this, LedService.class);
//        startService(intent); mark
        bindService(intent, connection, Context.BIND_AUTO_CREATE);//Service启动后去绑定会调用onBind
        
//        SysRunFlagTimer = new Timer();
//        SysRunFlagTimer.schedule(new SysRunFlagTask(), 0, 5000);
        
        //注册定时任务广播
        IntentFilter filter = new IntentFilter();
		filter.addAction(AutoConfigReciver.AUTOBIGHTNESS);
		filter.addAction(AutoConfigReciver.AUTOONOFF);
		filter.addAction(AutoConfigReciver.AUTOREBOOT);
		mAutoConfigReciver = new AutoConfigReciver(pSerialPort);//android:exported="true"
		mContext.registerReceiver(mAutoConfigReciver, filter);
        //启动闹钟设置
        initAutoRebootAlarm();
        initAutoBrightAlarm();
        initAutoOnOffScrnAlarm();
	}
	
	
	private void initAutoOnOffScrnAlarm() {
		new Thread("自动开关屏"){
			@Override
			public void run() {
				List<PlanOnOff> planOnOffs = new DisplayerBiz(mContext).parseLastOnOff();
				if (planOnOffs.size()==6) { //记住掉电前的屏幕状态。
					boolean isOneOn = isOneOn(planOnOffs); //是否有个时间段是开着的。
					if (isOneOn) {
						//是周计划的天计划模式
						long nowTimes = System.currentTimeMillis();
						long planTime1 = getPlanTime(planOnOffs.get(0));
						boolean day_1 = planTime1<=nowTimes&&nowTimes<=getPlanTime(planOnOffs.get(1));
						boolean day_2 = getPlanTime(planOnOffs.get(2))<=nowTimes&&nowTimes<=getPlanTime(planOnOffs.get(3));
						boolean day_3 = getPlanTime(planOnOffs.get(4))<=nowTimes&&nowTimes<getPlanTime(planOnOffs.get(5));
						if (day_1||day_2||day_3) {
	//						没事
						}else {
							//提示信息
							mHandler.sendEmptyMessage(LedProtocol.OFF_SCRN_TST);
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//黑掉屏幕
							Intent intent = new Intent();
							intent.setAction(AutoConfigReciver.AUTOONOFF);
							intent.putExtra("state", (byte)0);
							long nextOnTime = 0;
							if (nowTimes<getPlanTime(planOnOffs.get(2))&&(planOnOffs.get(2).isMeOn==1)) {
								nextOnTime = getPlanTime(planOnOffs.get(2))-nowTimes;
							}else if (nowTimes<getPlanTime(planOnOffs.get(4))&&(planOnOffs.get(4).isMeOn==1)) {
								nextOnTime = getPlanTime(planOnOffs.get(4))- nowTimes;
							}else {
								Calendar calendar = Calendar.getInstance();
								calendar.set(Calendar.HOUR_OF_DAY, planOnOffs.get(0).hour);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
								nextOnTime = calendar.getTimeInMillis() - nowTimes;
								
							}
							intent.putExtra("nextOnTime", nextOnTime);
							sendBroadcast(intent);
						}
					}
				}else if (planOnOffs.size()==42) {
					//周计划的周模式
					boolean isOneOn = isOneOn(planOnOffs); 
					if (isOneOn) {
						if (isOneOn) {
							long nowTimes = System.currentTimeMillis();
							int now_dayofweek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
							if (now_dayofweek==7) {
								now_dayofweek=1;
							}else {
								now_dayofweek+=1;
							}
							
							boolean isInOnTime = isInOnTime(nowTimes,planOnOffs);
Log.e("当前时间属于", "当前属于："+(isInOnTime?"开屏":"关平"))							;
							if (isInOnTime) {
								//do nothing
							}else {
								Intent intent = new Intent();
								intent.setAction(AutoConfigReciver.AUTOONOFF);
								intent.putExtra("state", (byte)0);
								//计算下次亮屏幕的时长。
//								long nextOnTime = 0;
//								intent.putExtra("nextOnTime", nextOnTime);
								
								//提示信息
								Log.e("注备关闭屏幕了", "准备挂关闭屏幕了");
								mHandler.sendEmptyMessage(LedProtocol.OFF_SCRN_TST);
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								sendBroadcast(intent);
							}
						}
						
					}
				}
				if (planOnOffs.size()==0) {
					return;
				}else {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent();
					intent.setAction(AutoConfigReciver.AUTOONOFF);
					for (int i = 0; i < planOnOffs.size(); i++) {
						PlanOnOff planOnOff = planOnOffs.get(i);
						if (planOnOff.isWeekOn==0) {
//							Log.e("当前开关瓶闹铃设置", "闹铃开关瓶设置是全部关闭");
							return;
						}
						if (planOnOff.isMeOn==0) {
//							Log.e("当前遍历的闹设置", "当前遍历到的闹铃设置是关闭的："+planOnOff);
							continue;
						}else {
							PendingIntent operation = PendingIntent.getBroadcast(mContext, planOnOff._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, planOnOff.hour);
							calendar.set(Calendar.MINUTE, planOnOff.minute);
							if (planOnOff.planType==0) {//日计划
								if (calendar.getTimeInMillis()<System.currentTimeMillis()) {
									calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
								}
								intent.putExtra("state", planOnOff.state);
								intent.putExtra("setTime",calendar.getTimeInMillis());
								am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
								
							}else {
								int dayofWeek  =0;
								if (planOnOff.dayofweek==7) {
									dayofWeek = 1;
								}else {
									dayofWeek=planOnOff.dayofweek+1;
								}
//			Log.e("得到的星期是", "要设置的星期字段是："+dayofWeek);					
								calendar.set(Calendar.DAY_OF_WEEK, dayofWeek);
								if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
									calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+7);
//									calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK)+7);
//									calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
								}
								intent.putExtra("state", planOnOff.state); //开还是关。
								intent.putExtra("setTime",calendar.getTimeInMillis());
								am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000*7, operation);
							}
						}
					}
				}
			}

			private boolean isInOnTime(long nowTimes, List<PlanOnOff> planOnOffs) {
				boolean isInOnTime = false;
//				判断当前是否属于 开屏时间段
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(nowTimes);
				int dayofweek = calendar.get(Calendar.DAY_OF_YEAR);
				dayofweek = dayofweek==7?1:(dayofweek+1);
				
				List<PlanOnOff> temp_planOnOffs = new ArrayList<PlanOnOff>();
				PlanOnOff planOnOff = null;
				for (int i = 0; i < planOnOffs.size(); i++) {
					planOnOff = planOnOffs.get(i);
					if (dayofweek==planOnOff.dayofweek) {
						if (planOnOff.isMeOn==1) {
							temp_planOnOffs.add(planOnOff); //最多6个
						}
					}
				}
				Log.e("这个天数有计划任务数", "这个天数计划任务书："+temp_planOnOffs.size());
				for (int i = 0; i < temp_planOnOffs.size(); i+=2) {
				     isInOnTime = getPlanTime(temp_planOnOffs.get(i))<=nowTimes&&nowTimes<=getPlanTime(temp_planOnOffs.get(i+1));
				     if (isInOnTime) {
						return true;
					}
				}
				
				
				
				return isInOnTime;
			}

			private boolean isOneOn(List<PlanOnOff> planOnOffs) {
				for (PlanOnOff planOnOff : planOnOffs) {
					if (planOnOff.isMeOn==1) {
						return true;
					}
				}
				return false;
			}

			private long getPlanTime(PlanOnOff planOnOff) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, planOnOff.hour);
				calendar.set(Calendar.MINUTE, planOnOff.minute);
				return calendar.getTimeInMillis();
			}
		}.start();
		
	}

	private void initAutoBrightAlarm() {
		new Thread("自动亮度调节设置线程"){
			public void run() {
				//自动亮度调节
				List<PlanBrightNess> planBrightNesses = new DisplayerBiz(mContext).parseLastPlanBright();
				if (planBrightNesses.size()==0) {
					return;
				}else {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//					Intent intent = new Intent(mContext, AutoConfigReciver.class);
					Intent intent = new Intent();
					intent.setAction(AutoConfigReciver.AUTOBIGHTNESS);
					for (int i = 0; i < planBrightNesses.size(); i++) {
						PlanBrightNess planBrightNess = planBrightNesses.get(i);
						if (planBrightNess.isParentOn==0) {
//							Log.e("上次闹钟配置全部关闭", "上次闹钟配置全部关闭");
							return;
						}
						if (planBrightNess.isMeOn==0) {
//							Log.e("要取消上次的", "要取消上次的配置啊。。。");
							PendingIntent operation = PendingIntent.getBroadcast(mContext, planBrightNess._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
							am.cancel(operation);;
							continue;
						}
//						Log.e("要新增一个闹钟了。", "要新增一个任务啊。。——！！！");
						//每次重启后， 都会新增一个，而不是 取消前面已经创建的呢？？，如果前面的闹钟count=0可以取消不是0却不能取消了
						intent.putExtra("bright", planBrightNess.bright);
						
						PendingIntent operation = PendingIntent.getBroadcast(mContext, planBrightNess._id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.HOUR_OF_DAY, planBrightNess.hour);
						calendar.set(Calendar.MINUTE, planBrightNess.minute);
						if (calendar.getTimeInMillis()<System.currentTimeMillis()) {
							calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
						}
						intent.putExtra("setTime",calendar.getTimeInMillis());
						am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
					}
				}
			};
		}.start();
		
	}

	private void initAutoRebootAlarm() {
		new Thread("重启闹铃设置线程"){
			public void run() {
				//自动重启
				SharedPreferences sp = getSharedPreferences("autoReboot", Context.MODE_WORLD_READABLE);
				int isOn = sp.getInt("isOn", 0);
				if (isOn==0) {
					return;
				}
				int hour = Integer.parseInt(sp.getString("hour", "-1"));
				int minute = Integer.parseInt(sp.getString("minute", "-1"));
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				if (System.currentTimeMillis()>calendar.getTimeInMillis()) {
					calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
				}
//				Intent intent = new Intent(mContext, AutoConfigReciver.class);
				Intent intent = new Intent();
				intent.setAction(AutoConfigReciver.AUTOREBOOT);
				PendingIntent operation = PendingIntent.getBroadcast(mContext, 1018, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 3600*24*1000, operation);
				
			};
		}.start();
		
		
	}

//mark 新增 2014年1月11日 09:21:15
private void mountLedDriver(){
		byte[] buffer = new byte[1024 * 4];
		int len;
		InputStream inRef;
		BufferedInputStream inReader;
		File file;
		
		inRef = this.getResources().openRawResource(R.raw.led_driver);
		inReader=new BufferedInputStream(inRef);
		try {
		// FileOutputStream outRef = SambaMainTest.this.openFileOutput("files/nmbd", Context.MODE_PRIVATE);
			file = new File("/data/data/com.led.player/led_driver.ko");
			if(!file.exists())
			{
				file.createNewFile();
				//file.setExecutable(true);
				FileOutputStream outRef = new FileOutputStream(file);
				BufferedOutputStream outBuff=new BufferedOutputStream(outRef);

			
				while((len =inReader.read(buffer)) != -1 )
				{
					outBuff.write(buffer,0,len);
				}
				outRef.getChannel().force(true);
				outBuff.flush();
				outBuff.close();
				outRef.close();
				inReader.close();
				inRef.close();
			}
			else
			{
				inReader.close();
				inRef.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断上次状态如果是客户端 就自动启动上次那种状态
	 */
	public void resumeWifi() {
		final SharedPreferences sPreferences = getSharedPreferences("wifiLastState", Context.MODE_PRIVATE);
		int wifi_conn_last_state = sPreferences.getInt("wifi_conn_last_state", -1);//还是0,1代表热点与终端。
		if (wifi_conn_last_state==0) {
			final Timer timer = new Timer("wifi_ap_timer");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (LedActivity.isNetWorkCardError) {
						timer.cancel();
						isAPParentOn  = false;
						return;
					}
					isAPParentOn  = true;
					if (LedActivity.wifi_ap_state==false) {
						Log.e("上次开机是热点", "这次还没开启热点。。in LedActivity");
						//sdcard没插入也需要停止。
						//不用读取上次sdcard的信息，去sharedpreference中读取。
						String ap_ssid = sPreferences.getString("ap_ssid", null);
						String ap_password = sPreferences.getString("ap_password", null);
						APInfo apInfo = new APInfo(ap_ssid, ap_password, null);
						try {
							startHotSpot(LedActivity.this,apInfo);//如果上次写入配置意外断电。。
						} catch (Exception e) {
							e.printStackTrace();
							isAPParentOn = false;
							timer.cancel();
							return ;
						}
					}else {
						Log.e("热点启动成功 in LedActivity", "热点终于启动了或模式改变成终端了，退出timer");
						timer.cancel();
						isAPParentOn = false;
						LedActivity.wifi_ap_state=true;
						return;
					}
					
				}
			}, 0,15000);
		}
		if (wifi_conn_last_state==1) {
			final Timer timer = new Timer("wifi_client_timer");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (LedActivity.isNetWorkCardError) {
						timer.cancel();
						isTMParentOn = false;
						return;
					}
					isTMParentOn = true;
						if(ConnectedReceiver.wifi_conn_state==false){
							//不管开没开热点，关闭热点
Log.e("上次开机是终端，", "还没连接上。。");							
					stopHotSpot(new APInfo("joyhaha", "12345678", APInfo.AP_TYPE));
							WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						System.out.println(	wifiManager.setWifiEnabled(false));
						System.out.println(	wifiManager.setWifiEnabled(true));//热点如果打开了这个操作会一直等待热点关闭后才执行。
							wifiManager = null;
	Log.e("跑完了", "设置连接完了--Activity");							
						}else {
Log.e("连接状态目前终端已经连接上了：", ConnectedReceiver.wifi_conn_state+"");
							Log.e("wifi终端", "wifi终端终于连接上了或模式改变成热点了");
							timer.cancel();
							isTMParentOn=false;
							ConnectedReceiver.wifi_conn_state= true;
							return;
						}
				}
			}, 0, 15000);
		}
	}

	/**
	 * 关闭网络接口
	 */
	/*private void closeIntenet() {
		// TODO Auto-generated method stub
		//关闭以太网
		try {
			Process process = Runtime.getRuntime().exec("su");
			BufferedOutputStream bos = new BufferedOutputStream(process.getOutputStream());
			bos.write("ifconfig eth0 0".getBytes());
			bos.write("\n".getBytes());
			bos.write("ifconfig eth0 down".getBytes());
			bos.write("\n".getBytes());
			bos.flush();
			bos.write("exit\n".getBytes());
			bos.flush();
			process.waitFor();
			Log.e("joychien", "关闭了eth0");
			bos.close();
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					//开启以太网
					Process process2;
					try {
						process2 = Runtime.getRuntime().exec("su");
						BufferedOutputStream bos = new BufferedOutputStream(process2.getOutputStream());
						bos.write("ifconfig eth0 0".getBytes());
						bos.write("\n".getBytes());
						bos.write("ifconfig eth0 up".getBytes());
						bos.write("\n".getBytes());
						bos.flush();
					
						bos.write("exit\n".getBytes());
						bos.flush();
						process2.waitFor();
						Log.e("joychien", "打开了eth0");
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 120000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	*/private BroadcastReceiver wifiConnReceiver = null;
	/**
	 * 注册wifi连接状态广播接收器
	 */
	private void registerWifiConnReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);	//supplicant祈求者状态改变，打开wifi和关闭wifi监听
		wifiConnReceiver = new ConnectedReceiver();
		this.registerReceiver(wifiConnReceiver, filter);
	}
	private SDCardReceiver tfCardReceiver = null;
	/**
	 * 注册TFcard 热插拔的监听
	 */
	private void registerTFReaceiver(){
		IntentFilter filter = new IntentFilter();
		 filter.addAction(Intent.ACTION_MEDIA_MOUNTED);  
         // 必须添加，否则无法接收到广播  ,果然是这样的。因为这个Intent如果带了数据就是这样要指定scheme的。
         filter.addDataScheme("file");
         tfCardReceiver = new SDCardReceiver(mHandler);
         this.registerReceiver(tfCardReceiver, filter);
	}

	@Override  
   protected void onNewIntent(Intent intent) {
       super.onNewIntent(intent);  
       Log.e("LedActivity", "onNewIntent....被调用");
   }  

    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.e("LedActivity", "LedActivity onDestory...");
    	LedActivity.isLedActivityOn=false;
    	//解除网络状态改变的广播接收器
    	if (wifiConnReceiver!=null) {
    		this.unregisterReceiver(wifiConnReceiver);
		}
    	if (tfCardReceiver!=null) {
			this.unregisterReceiver(tfCardReceiver);
		}
    	if (apReceiver!=null) {
			this.unregisterReceiver(apReceiver);
		}
    	
    	if(pSerialPort!=null) pSerialPort.releaseSerialResource();
    	
    	if(playerRunFlagTimer!=null){
			playerRunFlagTimer.cancel();
		}
		if(sysRunFlagTimer!=null){
			sysRunFlagTimer.cancel();
		}
    	
        stopService(this.intent); //解除绑定+stopservice=退出服务
        unbindService(connection);
        
        //解除所有的socket。。。。。端口绑定，  端口不释放
        
        unregisterReceiver(mAutoConfigReciver);
        if(pSerialPort!=null) unregisterReceiver(pSerialPort.myReceiver);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    
    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            	if(DEBUG)Log.i("OnCompletionListener", "%%%%%%%%%%OnCompletionListener");
            	Message msg = new Message();
            	msg.arg1 = pageIndex;
            	msg.arg2 = videoWinIndex; 
            	msg.obj = null;
            	msg.what = LedProtocol.DISPLAYER_NEXT_VIEW;
            	if(videohandler!=null){
            		videohandler.sendMessage(msg);
//            		Log.i("播放完毕了，", "视频播放完毕了，请求下一个节目");
            		if(DEBUG)Log.i("OnCompletionListener", "%%%%%%%%%%send msg to service");
            	}else{
            		if(DEBUG)Log.i("OnCompletionListener", "%%%%%%%%%%videohandler = null");
            		Log.e("播放完毕了", "视频播放完毕了，没有handler，说明是sync轮播，重播");
            		if (currentPlayMode==ViewInfoClass.SYNC_LOOP_MODE) {
						PlayerVideo();
					}
            	}
            	videohandler = null;
            	
//            	mPlayer.release();
//            	mPlayer = null;
            }
    };
    
    private MediaPlayer.OnErrorListener mErrorListener = 
    		new MediaPlayer.OnErrorListener() {
				
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
	 Log.i("OnErrorListener", "%%%%%%%%%%OnErrorListener");
					//mPlayer.stop();
					//mPlayer.release();
					return true;
				}
			};
			
	private MediaPlayer.OnPreparedListener mPreparedListener = 
			new MediaPlayer.OnPreparedListener() {
				
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					if(DEBUG)Log.i("OnPreparedListener", "%%%%%%%%%%OnPreparedListener");
					mIsVideoReadyToBePlayed = true;
					if (mIsVideoReadyToBePlayed || mIsVideoSizeKnown) {
						if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%mPlayer.start()");
						startVideoPlayback();
			        }
				}
			};
			
	private MediaPlayer.OnVideoSizeChangedListener mChangedListener = 
			new MediaPlayer.OnVideoSizeChangedListener() {
				
				public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
					// TODO Auto-generated method stub
					if(DEBUG)Log.i("onVideoSizeChanged", "%%%%%%%%%%onVideoSizeChanged");
					
					 if (width == 0 || height == 0) {
				            return;
				        }
				        mIsVideoSizeKnown = true;
				        mVideoWidth = width;
				        mVideoHeight = height;
				        if(DEBUG)Log.i("onVideoSizeChanged", "%%%%%%%%%%width: "+width+" height: "+height );
				        holder.setFixedSize(mVideoWidth, mVideoHeight);
				        if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%setFixedSize");
//				        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//				            startVideoPlayback();
//				        }
				}
			};
			
	private void doCleanUp() {
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%doCleanUp");
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }
	
	private void startVideoPlayback() {
		if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%startVideoPlayback");
        mPlayer.start();
        if(DEBUG)Log.i("LedActivity", "%%%%%%%%%%mPlayer.start()");
    }
	
	private class CameraCB implements SurfaceHolder.Callback{
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
			{
				
			}

		
			public void surfaceCreated(SurfaceHolder holder)
			{
				if(DEBUG)Log.i("LedActivity", "...............CameraCB surfaceCreated");
				initCamera();
			}
		
			public void surfaceDestroyed(SurfaceHolder holder)
			{
				if(DEBUG)Log.i("LedActivity", "...............CameraCB surfaceDestroyed");
				if(camera != null)
				{
					if(isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
					isPreview = false;
				}
				
			}
	
	}
	
	void initCamera()
	{
		if(!isPreview)
		{
			try {
				camera = Camera.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(camera == null)
			{
				Log.v("CAMERA","camera null+++++++++++");
				
			}
		}
		
		if(camera != null && !isPreview)
		{
			int tempfordebug = 0;
			try
			{
				Camera.Parameters parameters = camera.getParameters();
				List<Integer> frameRates = parameters.getSupportedPreviewFrameRates(); 
				List<Size> supported = parameters.getSupportedPreviewSizes();
				List<Integer> previewFormat =  parameters.getSupportedPreviewFormats();
				if (frameRates != null) {
			       Integer max = Collections.max(frameRates);
			       parameters.setPreviewFrameRate(max);

				}
				 tempfordebug |= 0x02;
				if(supported != null)
				{	
					parameters.setPreviewSize(640, 480);
				}

				if(previewFormat != null)
				{

					parameters.setPreviewFormat(previewFormat.get(0));
				}

				parameters.set("jpeg-quality",85);

				camera.setParameters(parameters);
				camera.setPreviewDisplay(CameraView.getHolder());
				camera.startPreview();
			}
			catch (Exception e)
			{
				//pEditText.append("Camera setPreviewDisplay except" + tempfordebug);
				//e.printStackTrace();
				Log.v("CAMERA","Camera except");
				//camera.stopPreview();
				camera.release();
				camera = null;
				isPreview = false;
				
			}
			
			isPreview = true;
		}
		else
		{
			Log.v("CAMERA","Camera except");
			//pEditText.append("Camera null");
			
		}
	}
    
	
	/**
	 * 播放视频。。。。
	 */
	private void PlayerVideo(){
		doCleanUp();
		int videoWidth = mSurWidth;
		int videoHeight = mSurHight;
		try {
			if (mPlayer==null) {
//				Log.i("mPlayer状态", "mPlay现在为null"); 
				mPlayer = new MediaPlayer();
			}else {
				Log.i("mPlayer状态", "mPlay现在-----不为null");
			}
			mPlayer.reset();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//	 		 File file = new File(mediaPath);
//	 	    FileInputStream fis = new FileInputStream(file);
			mPlayer.setDataSource(mediaPath); 
//			mPlayer.setDataSource(fis.getFD()); //mark 修改原来的直接设置路径为 设置fd,增加代码  上面2行
//			mPlayer.setOnVideoSizeChangedListener(mChangedListener);
			mPlayer.setOnCompletionListener(mCompletionListener);
			mPlayer.setOnErrorListener(mErrorListener);
//	 		mPlayer.setOnPreparedListener(mPreparedListener);
			mVideoWidth = mPlayer.getVideoWidth();
			mVideoHeight = mPlayer.getVideoHeight();
			if(ORIGINAL_SCALE){
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%ORIGINAL_SCALE");
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%mVideoWidth "+mVideoWidth+"mVideoHeight "+mVideoHeight);
				if(mVideoWidth>0 && mVideoHeight>0)
				{
					//下面计算方法…………不懂。。
					if(mVideoWidth*mSurHight > mVideoHeight*mSurWidth){
						videoHeight = mVideoHeight*mSurWidth/mVideoWidth;
						if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%videoHeight "+videoHeight);
					}
					else if(mVideoWidth*mSurHight < mVideoHeight*mSurWidth){
						videoWidth = mVideoWidth*mSurHight/mVideoHeight;
						if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%videoWidth "+videoWidth);
					}
						
				}
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%videoWidth "+videoWidth+"videoHeight "+videoHeight);
				mVideoView.setLayoutParams(new ViewAnimator.LayoutParams(videoWidth,videoHeight));
				
				mVideoView.setX((mSurWidth-videoWidth)/2);
				mVideoView.setY((mSurHight-videoHeight)/2);//安卓2.3.4还不支持这个方法 Call requires API level 11 (current min is 9): android.view.SurfaceView#setY
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%mSurWidth "+mSurWidth);
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%mSurHight "+mSurHight);
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%startX "+(mSurWidth-videoWidth)/2);
				if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%startY "+(mSurHight-videoHeight)/2);
				ORIGINAL_SCALE = false;
			}else{
				mVideoView.setLayoutParams(new ViewAnimator.LayoutParams(mSurWidth,mSurHight));
				mVideoView.setX(0);
				mVideoView.setY(0);
			}
			if(DEBUG)Log.i("PlayerVideo", "%%%%%%%%%%videoWidth "+videoWidth+"videoHeight "+videoHeight);
			mPlayer.setDisplay(mVideoView.getHolder());
			if(currentPlayMode==ViewInfoClass.SYNC_LOOP_MODE) mPlayer.setLooping(true);  //破机没效果~~add by joychine 2014年4月30日 16:13:48 mark，这样永远都不会completely，至少nubia是这样
//long begin = System.currentTimeMillis();			
			mPlayer.prepare(); //这个方法本色会阻塞知道可以播放。
//Log.i("prepate时间", "prepare所需要时间："+(System.currentTimeMillis()-begin));	
			
			mPlayer.start();
			 
		} catch (Exception e) {
			e.printStackTrace();
        }
	}

	/**
	 * 启动wifi热点
	 */
	private void startHotSpot(Context context,APInfo apInfo){
		WifiHotSpot wifiHotSpot = WifiHotSpot.getInstance(context);
		wifiHotSpot.startHotSpot(apInfo);
		
/*		if (HotSpotServer.isRun==false) {
			//开启ServerSocket
			new Thread(new HotSpotServer(mHandler)).start();
			HotSpotServer.isRun=true;
		}*/
	}
	
	/**
	 * 关闭热点
	 */
	private boolean stopHotSpot(APInfo apInfo){
		WifiHotSpot wifiHotSpot = WifiHotSpot.getInstance(LedActivity.this);
		return wifiHotSpot.closeWifiHot(apInfo);
	}
	
	/**
	 * 写入wifi连接配置，配置信息需要从另外一个i额文件中解析,需要开关一次wifi开关才能激活写入的配置
	 */
	private boolean writeAPconfig(APInfo apInfo){ 
		return WriteAPinfo.writeApinfo(apInfo);
	}
	
	/**
	 * 注册广播接收器，监听ap变化
	 */
	private  void registerAPListener() {
		IntentFilter filter = new IntentFilter(); 
		filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");//@hide ap的wifi状态改变
		registerReceiver(apReceiver, filter);
	}
	
	private BroadcastReceiver apReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {//艹，怎么广播的state是 0~3 和wifi状态改变一样啊2.3是这样
				int state = intent.getIntExtra("wifi_state", -1);
				if (state==3) {
					ToastUtil.setRBShow(context, "ap启动成功了: 居然广播的和wifi状态值一样，2.3.4会这样", 0);
				}
				switch (state) {
				case 10://或者 case 0
//					Toast.makeText(context, "state: "+state+"ap关闭中..", Toast.LENGTH_SHORT).show();
					ToastUtil.setLTShow(context, "state: "+state+context.getResources().getString(R.string.ap_disabling), Toast.LENGTH_SHORT);
//					isNetWorkCardError = false;
					break;
				case 11:
//					Toast.makeText(context, "state: "+state+"ap关闭成功", Toast.LENGTH_SHORT).show();
					ToastUtil.setLTShow(context, "state: "+state+context.getResources().getString(R.string.ap_disabled), Toast.LENGTH_SHORT);
//					isNetWorkCardError = false;
					LedActivity.wifi_ap_state = false;
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (isAPParentOn==false&&isTMParentOn==false) {
						resumeWifi();
					}
					break;
				case 12:
//					Toast.makeText(context, "state: "+state+"ap启动中..", Toast.LENGTH_SHORT).show();
					ToastUtil.setLTShow(context, "state: "+state+context.getResources().getString(R.string.ap_enabling), Toast.LENGTH_SHORT);
//					isNetWorkCardError = false;
					break;
				case 13:
//					Toast.makeText(context, "state: "+state+"ap启动成功", Toast.LENGTH_SHORT).show();
					LedActivity.wifi_ap_state = true;
					ToastUtil.setLTShow(context, "state: "+state+context.getResources().getString(R.string.ap_enabled), Toast.LENGTH_SHORT);
Log.e("热点已经启动", "热点启动成功 in LedActivity~in Receiver");					
					isNetWorkCardError = false;
					break;
				default:
					WifiManager wifiManager = (WifiManager) LedActivity.this.getSystemService(Context.WIFI_SERVICE);
					ToastUtil.setRBShow(mContext, getResources().getString(R.string.AP_boot_exception)+wifiManager.getWifiState(), 0);
					//isNetWorkCardError = true; 这个用来让用户手动主动来产生 关闭wifi和热点时用算了。
					break;
				}
			}
		}
	};
	
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;};
}
