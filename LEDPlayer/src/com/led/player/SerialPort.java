package com.led.player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.led.player.utils.EthernetSet;
import com.led.player.utils.HttpClientUtil;
import com.led.player.utils.SystemInfo;

/**
 * 控制串口通讯及LED控制 串口主要用于与MCU通讯，LED用于显示一些系统状态：
 * 
 * @author 1231
 * 
 */
public class SerialPort {
	private Context mContext;
	// private Handler uartHandler;
	private final String path = "/dev/ttyS2";
	private final int baud = 57600;

	String TAG = "SerialPort";
	private boolean isNotAuthority = false; // 是否写入授权。
	public TCPUDPserver mTcpudPserver;

	private final int LED_STATUS_OFF = 0;
	private final int LED_STATUS_FAST = 1;
	private final int LED_STATUS_NORM = 2;
	private final int LED_STATUS_ON = 3;

	private int led1Status = LED_STATUS_OFF;
	private int led2Status = LED_STATUS_OFF;

	private Long mFrequency;
	/**
	 * led1记数，
	 */
	private int led1TimeCount = 0;
	private int led2TimeCount = 0;
	private int time10SCount = 0;
	private int timeReceTimer = 0;
	private int authErrorCount = 0;

	private boolean getTempHumiFlag = false;
	private byte temperature;
	private byte humidity;

	private FileDescriptor mFd;
	/**
	 * led设备的描述符id
	 */
	private int ledFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private ReadThread mReadThread;

	private FileInputStream fis_s2;
	/**
	 * 暂时没用了。。
	 */
	public boolean apkRunFlag = true;
	public boolean apkAuthorizeFlag = true;

	private int apkUnRunCount = 0;

	private boolean serialReadRunFlag = true;
	private int timer1HourCount = 0;

	private Timer timer;
	private TimerTask task;
	private TimerTask sendDataTask;
	/**
	 * 定时及周期性执行任务的线程池
	 */
	ScheduledExecutorService schedulePool;

	private static Handler handler;

	private byte[] commondata;

	private byte[] randomData;

	/**
	 * 原始长度16字节的key要与receCOmPack中比较是否授权ok的
	 */
	private byte[] idCommond;

	/**
	 * 收到的串口上传数据 长度28B
	 */
	private byte[] receComPack;

	private boolean netSendDataReadly = false;
	private byte[] netSendPack;

	private boolean uartDataReadly = false;
	private byte[] uartRecePack;

	private byte[] netSendDataBuf;
	/*
	 * wLedStatus:bit0:Led1 on/off flag;bit1:Led2 on/off flag
	 */
	private int wLedStatus = 0;

	// add by betters 20131028
	// private LinkedList<byte[]> seriDataList =
	// Collections.synchronizedList(new LinkedList<byte[]>());
	private LinkedList<byte[]> seriDataList = new LinkedList<byte[]>();

	protected int blueScrnCount; // 记数这个蓝屏前 需要的次数，定为3

	public SerialPort(int flags, Context context) throws SecurityException,
			IOException {
		this.mContext = context;
		/* Check access permission */
		File file = new File(path);// /dev/ttyS2
		// Log.e("SerialPort", "路径去：："+file.getAbsolutePath()) ; // "/dev/ttyS2"
		if (!file.canRead() || !file.canWrite()) {
			try {
				ensureFile(file);
				Log.e("得到文件描述符", "得到文件描述富豪。。。not in here。");
			} catch (Exception e) {
				e.printStackTrace();
				ensureFile(file);
			}

		}
		EthernetSet
				.writeOsData("insmod /data/data/com.led.player/led_driver.ko");
		EthernetSet.writeOsData("chmod 0666 /dev/LED_file");

		Log.v("serivalProt----", "Path:" + file.getAbsolutePath() + "baud: "
				+ baud);
		mFd = open(path, baud, 0);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}

		// 再打开一个。。。
		File file2 = new File("/dev/ttyS1");
		changeAttr4File(file2);

		FileDescriptor anothFd = open(file2.getAbsolutePath(), 4800, 0);
		if (anothFd == null) {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
		fis_s2 = new FileInputStream(file2);

		Log.e("SerialPort", "是否打开了啊：：" + anothFd);

		// uartHandler = mhandler;

		mFileInputStream = new FileInputStream(mFd);
		bis = new BufferedInputStream(mFileInputStream);
		mFileOutputStream = new FileOutputStream(mFd);
		bos = new BufferedOutputStream(mFileOutputStream);
		/*
		 * try { Missing read/write permission, trying to chmod the file Process
		 * su; su = Runtime.getRuntime().exec("/system/xbin/su"); // String cmd
		 * = "insmod /data/data/com.led.player/led_driver.ko"+ "\n" // +
		 * "exit\n"; // su.getOutputStream().write(cmd.getBytes());
		 * 
		 * String cmd = "chmod 0666 /dev/LED_file" + "\n" + "exit\n";
		 * su.getOutputStream().write(cmd.getBytes()); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		idCommond = new byte[16];
		randomData = new byte[16];
		commondata = new byte[28];
		receComPack = new byte[28];
		creatRandomData(randomData, 16);
		createSendDataPack(commondata, (byte) 0x22, (byte) 0xfe, (byte) 0x00,
				randomData, (byte) 0x00, (byte) 0x00, (byte) 0x00);
		this.serialPortSendString(commondata, 28);

		timer = new Timer("serport-led-thread");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 在发数据，对方没有回的情况下可能会比较快就蓝屏
				time10SCount++;
				// if(timeReceTimer > 0)//mark timeReceTimer改为blueblueScrnCount
				// {
				// timeReceTimer --;
				// if(timeReceTimer == 0&&numCount>=3)
				// if (blueScrnCount >= 3) {
				// Log.e("SerialPort", "蓝屏次数到达3次，授权失败");
				// apkAuthorizeFlag = false;
				// }
				// 这里需要led灯操作需要。这个if block
				if (ledFd == 0) {
					statusLedOpen("/dev/LED_file", 0);

				}
				// if (netSendDataReadly) {
				// serialPortSendString(netSendPack, netSendPack.length);
				// netSendDataReadly = false;
				// }

				// 下面注释掉
				if (time10SCount >= 240) // 一分钟 250*240 =60000
				{
					time10SCount = 0;
					// timer1HourCount++;
					// if(timer1HourCount >=6)
					// {
					// timer1HourCount = 0;
					// creatRandomData(randomData,16);
					// calcEncryption(randomData,idCommond);//获取授权
					// createSendDataPack(commondata,(byte)0x21,(byte)0xfe,(byte)0x00,randomData,(byte)0x00,(byte)0x00,(byte)0x01);
					// serialPortSendString(commondata,28);
					// timeReceTimer = 6;
					// }

					if (apkRunFlag) {
						creatRandomData(randomData, 16);// 喂狗指令
						createSendDataPack(commondata, (byte) 0x20,
								(byte) 0xfe, (byte) 0x00, randomData,
								(byte) 0x00, (byte) 0x00, (byte) 0x00);
						serialPortSendString(commondata, 28);
						// apkRunFlag = false;
						// apkUnRunCount = 0;
					}
					// else
					// {
					// apkUnRunCount++;
					// if(apkUnRunCount > 5)
					// {
					// timer.cancel();
					// releaseSerialResource();
					// }
					// }
				}

				// mark 上面注掉
				changeLedStatus();
			}
		};

		task = new TimerTask() {
			@Override
			public void run() {
				Message message = Message.obtain();
				// message.what = 1;
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 1000, 250);

		mReadThread = new ReadThread();
		mReadThread.setName("serialPort的读线程");
		mReadThread.start();
		Log.i(TAG, "开启读取线程");

		new ReadThread4s2("tty2-thread").start();
		try {
			new WriteThreadToCom().start();
		} catch (Exception e) {
			e.printStackTrace();
			new WriteThreadToCom().start();
		}

		// 注册串口传输广播监听器
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.led.player.SERIAL_HARD_REBOOT");
		context.registerReceiver(myReceiver, filter);
	}

	private void changeAttr4File(File file2) {
		if (!file2.canRead() || !file2.canWrite()) {
			try {
				ensureFile(file2);
				Log.e("得到文件描述符", "一定要得到文件描述符啊，，file2 fight！");
				changeAttr4File(file2);
			} catch (Exception e) {
				e.printStackTrace();
				ensureFile(file2);
			}
		} else {
			System.out.println("可以度读取啊。。。ttyS1 ---------------------");
		}
	}

	/**
	 * 重启设备监听消息。
	 */
	public BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.led.player.SERIAL_HARD_REBOOT")) {
				Log.e("SerialPort", "串口硬件重启！！！。");
				seriDataList.add(getData(0));
			}
		}

		/**
		 * 命令类型 0：重启，1：指定节目页
		 */
		private byte[] getData(int cmdType) {
			byte[] data = new byte[28];
			data[0] = 0x55;
			data[1] = 0x55;
			data[2] = (byte) 0xab;
			data[3] = (byte) 0xcd;
			data[4] = (byte) 0xff; // 包来自广播地址
			// data[5] = 0x23; //包类型,重启

			if (cmdType == 0) {
				data[5] = 0x23;
			}
			byte xyh27 = 0;
			for (int i = 0; i < data.length; i++) {
				xyh27 ^= data[i];
			}
			data[27] = xyh27;
			return data;
			// 55 55 AB CD FF 23 00 00 00 01 00 69 69 69 69 69 69 69 69 69 69 69
			// 69 69 69 69 69 2E

		}
	};

	private void ensureFile(File file) {
		EthernetSet.writeOsData(new String[] { "chmod 777 "
				+ file.getAbsolutePath() + "\n" });
	}

	/**
	 * 写入数据到串口的线程
	 * 
	 * @author 1231
	 */
	private class WriteThreadToCom extends Thread {
		public void run() {
			setName("写到串口WriteThreadToCom");
			while (serialReadRunFlag) {
				synchronized (SerialPort.this) {
					while (!seriDataList.isEmpty()) {
						// netSendDataBuf = new byte[28];
						// Log.i("joybug",
						// "serialPort集合linklist中的数据量："+seriDataList.size()) ;
						netSendDataBuf = seriDataList.removeFirst(); // mark
																		// 总是会有这种错
																		// NoSuchElementException
						// /System.out.print("发送个数" + ssss + " ：");
						// PrintBuffer(netSendDataBuf,28);//从linklist中移除来
						SerialPort.this.serialPortSendString(netSendDataBuf,
								netSendDataBuf.length);
						// netSendDataReadly = false;
					}
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}

	public void PrintBuffer(byte[] buffer, int len) {
		System.out.print("com: ");
		for (int i = 0; i < len; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);

			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println(" ");
	}

	/**
	 * 下面2哥参数干嘛的？？
	 * 
	 * @param ledIndex
	 *            :1:LED1 2:LED2
	 * @param flag
	 *            0才是常量啊。1快闪，2慢闪，3关闭 LED_STATUS_OFF = 0; private final int
	 *            LED_STATUS_FAST = 1; led灯快速闪动 private final int
	 *            LED_STATUS_NORM = 2; led灯慢闪。 private final int LED_STATUS_ON =
	 *            3;
	 */
	public void setLedStatus(int ledIndex, int flag) {
		if (ledIndex == 1)
			led1Status = flag;
		else
			led2Status = flag;
	}

	/**
	 * 把 数据（byte数组）加入到 linklist中
	 * 
	 * @param data
	 * @param len
	 */
	public synchronized void setNetSendData(byte[] data, int len) {
		// byte cpLen;
		/*
		 * netSendPack = new byte[len]; for(cpLen = 0;cpLen < len;cpLen ++) {
		 * netSendPack[cpLen]= data[cpLen]; }
		 */
		byte[] netbuffer = Arrays.copyOf(data, len);
		// byte[] netbuffer = new byte[28];
		// for(int iLen = 0;iLen < len;iLen ++)
		// {
		// netbuffer[iLen]= data[iLen];
		// }
		// add by betters add data to the list
		// Log.e("joybug", "写入数据前~~~");
		seriDataList.add(netbuffer);
		// Log.e("joybug", "写入数据后。。。");
		// PrintBuffer(netbuffer,28); //

		// netSendDataReadly = true;

		// //写入到文件中。
		// try {
		// BufferedOutputStream bos = new BufferedOutputStream(new
		// FileOutputStream("/mnt/sdcard/logs_joy.txt",true));
		// bos.write("\r\n".getBytes());
		// for (int i = 0; i < netbuffer.length; i++) {
		// String hex = Integer.toHexString(netbuffer[i] & 0xFF);
		// if (hex.length() == 1)
		// {
		// hex = '0' + hex;
		// }
		// bos.write((hex.toUpperCase()+" ").getBytes());
		// // bos.write('\t');
		//
		// }
		// bos.flush();
		// bos.close();
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public int statusLedOpen(String path, int flags) {

		ledFd = ledOpen(path, flags);
		if (ledFd != 0) {
			// Log.v(TAG, "Led Open ok");
			ledWrite(ledFd, wLedStatus);
		} else {
			// Log.e(TAG, "Led Open fail");
		}
		// Log.e("现在的ledFd", "现在的LedFd。。。："+ledFd);
		return ledFd;
	}

	/**
	 * 关闭 定时器 和 关闭led？ 在当前类和LedActivity类的onDestroy()中都有调用。
	 */
	public void releaseSerialResource() {
		serialReadRunFlag = false;
		// timer.cancel();
		close();
		if (ledFd != 0) {
			ledClose(ledFd);
		}
	}

	private void calcEncryption(byte[] srcdata, byte[] destdata) {

		destdata[0] = (byte) ((srcdata[1] ^ srcdata[2] & srcdata[3])
				+ srcdata[4] + srcdata[5]);
		destdata[1] = (byte) ((srcdata[0] + srcdata[1] + srcdata[2])
				& srcdata[3] | srcdata[4]);
		destdata[5] = (byte) ((srcdata[11] + srcdata[12]) | srcdata[13]
				^ srcdata[14] ^ srcdata[0] ^ 0xde);
		destdata[6] = (byte) (srcdata[10] & srcdata[11] | srcdata[12]
				^ srcdata[13] ^ srcdata[15] ^ 0xbc);
		destdata[7] = (byte) (srcdata[9] & srcdata[10] | srcdata[11]
				^ srcdata[11] ^ srcdata[12] ^ 0x78);
		destdata[8] = (byte) (srcdata[8] & srcdata[9] | srcdata[10]
				^ srcdata[12] ^ srcdata[14] ^ 0x9a);

		destdata[9] = (byte) (((srcdata[7] & srcdata[8] | srcdata[9])
				+ srcdata[10] + srcdata[11]) & 0x56);
		destdata[10] = (byte) (srcdata[4] | srcdata[5] | srcdata[6]
				| srcdata[7] | srcdata[8] | 0x55);
		destdata[11] = (byte) (srcdata[3] & srcdata[4] & srcdata[5]
				& srcdata[6] & srcdata[7] & 0x88);
		destdata[12] = (byte) (srcdata[2] + srcdata[3] + srcdata[4]
				+ srcdata[5] + srcdata[6] + 0x38);

	}

	/**
	 * 改变led的状态。
	 */
	private void changeLedStatus() {
		switch (led1Status) {
		case 0:
			setLed1OnOff(false);
			break;
		case 1:
			// Log.e("改变led灯的状态", "改变led等的状态嗄。。。led1的状态：" + getLed1Status());
			if (getLed1Status())
				setLed1OnOff(false);
			else
				setLed1OnOff(true);
			break;
		case 2:
			// Log.e("改变led灯的状态", "--------。。led2的状态：" + getLed2Status());
			led1TimeCount++;
			if (led1TimeCount >= 3) {
				led1TimeCount = 0;
				if (getLed1Status())
					setLed1OnOff(false);
				else
					setLed1OnOff(true);
			}
			break;
		case 3:
			setLed1OnOff(true);
			break;
		default:
			setLed1OnOff(false);
			break;
		}
		switch (led2Status) {
		case 0:
			setLed2OnOff(false);
			break;
		case 1:
			if (getLed2Status())
				setLed2OnOff(false);
			else
				setLed2OnOff(true);
			break;
		case 2:
			led2TimeCount++;
			if (led2TimeCount >= 3) {
				led2TimeCount = 0;
				if (getLed2Status())
					setLed2OnOff(false);
				else
					setLed2OnOff(true);
			}
			break;
		case 3:
			setLed2OnOff(true);
			break;
		default:
			setLed2OnOff(false);
			break;
		}
	}

	// private boolean checkSysRunFlag( )
	// {
	// return apkRunFlag;
	// }

	/**
	 * 从未给srcdata初始化，字段全是0. 判断数据域第一个和第二个值也要为0才行，难道这个2个字段总会有值么？？？
	 * 
	 * @param srcdata
	 * @param destdata
	 * @return
	 */
	private boolean checkEncryption(byte[] srcdata, byte[] destdata) {
		int count;
		for (count = 0; count < 2; count++) {
			if (srcdata[count] != destdata[count + 11]) {
				return false;
			}
		}

		for (count = 5; count < 13; count++) {
			if (srcdata[count] != destdata[count + 11]) {
				return false;
			}
		}
		return true;
	}

	private void creatRandomData(byte[] resultBuffer, int count) {
		int lenCount;
		Random rand = new Random();
		// commondata[]
		for (lenCount = 0; lenCount < 16; lenCount++) {
			resultBuffer[lenCount] = (byte) rand.nextInt();
		}
	}

	// createSendDataPack(commondata,(byte)0x22,(byte)0xfe,(byte)0x00,randomData,(byte)0x00,(byte)0x00,(byte)0x00);
	/**
	 * @param sendbuffer
	 *            commData数组
	 * @param packType
	 *            包类型，第6个
	 * @param destAdd
	 * @param srcaddr
	 * @param dataRegion
	 *            randData数组
	 * @param seqH
	 * @param seqL
	 * @param needACKFlag
	 */
	private void createSendDataPack(byte[] sendbuffer, byte packType,
			byte destAdd, byte srcaddr, byte[] dataRegion, byte seqH,
			byte seqL, byte needACKFlag) {
		byte count, temp;

		sendbuffer[0] = 0x55;
		sendbuffer[1] = 0x55;
		sendbuffer[2] = (byte) 0xab;
		sendbuffer[3] = (byte) 0xcd;
		sendbuffer[4] = destAdd;
		sendbuffer[5] = packType;
		sendbuffer[6] = srcaddr;
		sendbuffer[7] = seqL;
		sendbuffer[8] = seqH;
		sendbuffer[9] = needACKFlag;
		sendbuffer[10] = 0x00;

		for (count = 0; count < 16; count++) {
			sendbuffer[11 + count] = dataRegion[count];
		}
		temp = sendbuffer[4];
		for (count = 5; count < 27; count++) {
			temp = (byte) (temp ^ sendbuffer[count]);
		}
		sendbuffer[27] = temp;// 效验和？？
	}

	/**
	 * 数据从串口到 本地文件
	 * 
	 * @param commdata
	 *            串口数据 长度28个
	 * @param len
	 *            28
	 */
	private synchronized void serialPortSendString(byte[] commdata, int len) {
		synchronized (SerialPort.this) {
			try {
				// Log.e("下发数据长度", "len"+len);
				// PrintBuffer(commdata, len);//mark bug
				// mFileOutputStream.write(commdata, 0, len);
				bos.write(commdata, 0, len);
				bos.flush();// mark add
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// protected abstract void onDataReceived(final byte[] buffer, final int
	// size);
	/*
	 * private class WriteThread extends Thread {
	 * 
	 * @Override public void run() { super.run(); while(true) { Message message
	 * = new Message(); message.what = 1; handler.sendMessage(message); } } }
	 */

	/*
	 * private class CstmHandler extends Handler {
	 * 
	 * @Override public void handleMessage(Message msg) { try { // TODO
	 * Auto-generated method stub time10SCount ++; if(timeReceTimer > 0) {
	 * timeReceTimer --; if(timeReceTimer == 0) { apkAuthorizeFlag = false; } }
	 * 
	 * if(netSendDataReadly) {
	 * serialPortSendString(netSendPack,netSendPack.length); netSendDataReadly =
	 * false; }
	 * 
	 * if(time10SCount >= 20) { time10SCount = 0; timer1HourCount++;
	 * if(timer1HourCount >=6) { timer1HourCount = 0;
	 * creatRandomData(randomData,16); calcEncryption(randomData,idCommond);
	 * createSendDataPack
	 * (commondata,(byte)0x21,(byte)0xfe,(byte)0x00,randomData,
	 * (byte)0x00,(byte)0x00,(byte)0x01); serialPortSendString(commondata,28);
	 * timeReceTimer = 6; }
	 * 
	 * if(apkRunFlag) { creatRandomData(randomData,16);
	 * createSendDataPack(commondata
	 * ,(byte)0x20,(byte)0xfe,(byte)0x00,randomData,
	 * (byte)0x00,(byte)0x00,(byte)0x00); serialPortSendString(commondata,28);
	 * apkRunFlag = false; apkUnRunCount = 0; } else { apkUnRunCount++;
	 * if(apkUnRunCount > 5) { timer.cancel(); releaseSerialResource(); } } }
	 * changeLedStatus(); } catch(NullPointerException e) { e.printStackTrace();
	 * } } }
	 */

	private class ReadThread4s2 extends Thread {
		private String name;

		public ReadThread4s2(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			setName(name);
			byte[] buffer = new byte[65535];
			boolean isReady = false;
			StringBuilder sb = new StringBuilder();
			String gpsContent = "";
			while (serialReadRunFlag) {
				try {
					int lenth = fis_s2.read(buffer);
					// PrintBuffer(buffer,lenth);
					// Log.i("SerialPort","gps数据："+new String(buffer, 0, lenth,
					// "utf-8"));

					gpsContent = new String(buffer, 0, lenth, "utf-8");

					if (isReady) {
						isReady = false;
						sb.append(gpsContent);
						String needContent = sb.toString();
						String trueStr = needContent.substring(
								needContent.indexOf("$GPRMC,"),
								needContent.length());
						String[] feils = trueStr.split(",");// 13个
						// Log.e("SerialPort",
						// "数组："+Arrays.toString(feils)+"----长度："+feils.length)
						// ;
						if (feils != null && feils.length == 13) {
							String time = feils[1];
							try {
								if (LedService.currPlayType == LedService.GPS_SYNC_LOOP_PLAY_TYPE) {
									Intent chkIntent = new Intent(
											"com.led.player.action.syncloopcheck");
									chkIntent.putExtra("checkTime", time
											.substring(0, time.indexOf("."))); // HH:mm:ss
									mContext.sendBroadcast(chkIntent);
								}
							} catch (Exception e) {
								e.printStackTrace();
								new FileOutputStream(
										"/mnt/sdcard/gps模块输出错误.txt")
										.write((time + "\n").getBytes());
								Log.e("SerialPort", "---！！！--怎么回事，时间字串问题？："
										+ time);
							}

							Log.e("SerialPort", "time:  " + time
									+ "--currPlayType:"
									+ LedService.currPlayType);

						} else {
							Log.e("SerialPort", "----error-----字段长度不为13");
							;
						}
						sb.delete(0, sb.length());
					}
					if (gpsContent.indexOf(("$GPRMC,")) > -1) {
						// Log.e("SerialPort", "已经是第三段数据已$GPRMC,结尾了");
						isReady = true;
						sb.append(gpsContent);
					} else {

						// Log.i("LedService", "没GPRMC---为之奈何？"+gpsContent);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ReadThread extends Thread {
		private byte[] dataReceBuffer;
		private int pDataPre;

		@Override
		public void run() {
			schedulePool = Executors.newScheduledThreadPool(1);
			dataReceBuffer = new byte[64];
			pDataPre = 0;
			boolean flag = true;
			while (serialReadRunFlag) {// while(true)
				int readLen;
				int tempCount;
				try {
					byte[] buffer = new byte[32];
					if (mFileInputStream == null) {
						return;
					}
					readLen = mFileInputStream.read(buffer);
					Log.i("Data Rece", "Data receiverd11111:" + readLen);
					if (readLen > 0) {
						for (tempCount = 0; tempCount < readLen; tempCount++) {
							dataReceBuffer[pDataPre] = buffer[tempCount];
							pDataPre++;
						}
						// onDataReceived(buffer, size);
						for (tempCount = 0; tempCount < pDataPre; tempCount++) {// how
																				// strange
																				// loop
							if ((dataReceBuffer[tempCount] == 0x55)
									&& (dataReceBuffer[tempCount + 1] == 0x55)
									&& (dataReceBuffer[tempCount + 2] == (byte) 0xab)
									&& (dataReceBuffer[tempCount + 3] == (byte) 0xcd)) {
								if (pDataPre >= tempCount + 28) {
									Log.e("readTHreafd",
											"，pDataPre >= tempCount + 28 字节了，pDataPre:"
													+ pDataPre
													+ "目前的tempCount数是："
													+ tempCount);
									// size = tempCount+28;
									for (readLen = 0; readLen < 28; readLen++) {
										receComPack[readLen] = dataReceBuffer[tempCount
												+ readLen];
									}
									// PrintBuffer(receComPack,receComPack.length);//mark
									// 打印出mcu向a10发送的数据
									if (pDataPre > tempCount + 28) {
										Log.e("herer",
												"unbelievab......!!.pDataPre > tempCount + 28");
										tempCount = tempCount + 28;
										readLen = 0;
										for (; tempCount < pDataPre; readLen++, tempCount++) {
											dataReceBuffer[readLen] = dataReceBuffer[tempCount];
										}
										pDataPre = readLen;
									} else {
										pDataPre = 0;

									}
									timeReceTimer = 0;

									if (receComPack[5] == (byte) 0x21) {
										Log.e("receComPack[5]",
												"加密验证字段receComPack[5]的值：0x21");
										if (checkEncryption(idCommond,
												receComPack)) {
											Log.v(TAG, "Authorize OK");
											apkAuthorizeFlag = true;
											authErrorCount = 0;
											blueScrnCount = 0;// mark受到了就不蓝屏，次数为0
										} else {
											Log.e(TAG, "Authorize false");
											authErrorCount++;
											blueScrnCount++;
											if (authErrorCount >= 3)
												apkAuthorizeFlag = false;
										}
									} else if (receComPack[5] == (byte) 0xd0) {
										Log.i(TAG, "======0xd0======");
										temperature = receComPack[13];
										humidity = receComPack[14];
										getTempHumiFlag = true;
										// 发送数据
										if(flag){
											schedulePool.scheduleAtFixedRate(new Runnable() {
												public void run() {
													sendSerialData2Server(receComPack);
												}
											}, 0, 10, TimeUnit.SECONDS);
											flag = false;
										}
										// 发送数据。用handler去主线程，然后主线程又用
										// 用tcpudpServer的sendUraData（）方法发送数据
										sendByDirect();

									} else if ((receComPack[5] == (byte) 0xd4)
											|| (receComPack[5] == (byte) 0xd5)
											|| (receComPack[5] == (byte) 0x03)) {
										sendByDirect();
									} else
									// 串口点播节目
									if (receComPack[5] == 0x24) {
										// 计算校验和
										byte jyh27 = 0;
										for (int i = 0; i < receComPack.length - 1; i++) {
											jyh27 ^= buffer[i];
										}
										if (receComPack[27] != jyh27) {
											Log.e("校验和不想等", "校验" + jyh27
													+ "和不相等，数据不正cue不点播节目");
											break;
										}
										int demandPage = receComPack[11];
										Intent intent = new Intent(
												"PLAY_NET_PROGRAM");
										intent.putExtra("path",
												"/mnt/sdcard/demand_program/demandprogram.xml");
										demandPage = demandPage <= 0 ? 0
												: demandPage - 1;
										intent.putExtra("demand", demandPage);
										mContext.sendBroadcast(intent);
										;
									}

								} else {
									Log.e("《致青春》", "你神经病啊！居然没有28个丢弃");
								}
								break;
							}

						}
					}
				} catch (IOException e) {
					Log.v("Data Rece", "Data IOException:");
					e.printStackTrace();
					return;
				}
			}
		}

		public void sendByDirect() {
			// Message message = new Message();
			// message.what = LedProtocol.SEND_DATA_F_UART_T_NET;
			// Bundle bundle = new Bundle();
			// bundle.putByteArray("SendDataToNet", receComPack);
			// message.setData(bundle);
			// //Log.v("send message","before send bundle-----");
			// uartHandler.sendMessage(message);

			// 串口类中又使用网口类
			if (mTcpudPserver != null) {
				// PrintBuffer(receComPack, receComPack.length);
				mTcpudPserver.sendUartData(receComPack, receComPack.length);
			}
		}
	}

	/**
	 * Http请求向服务器发送串口16字节数据域
	 */
	private void sendSerialData2Server(byte[] buff) {
		// 向服务器发送串口的数据
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < receComPack.length; i++) {

			String hex = Integer.toHexString(receComPack[i] & 0xff);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex + ",");
		}
		String data = sb.substring(0, sb.length() - 1);
		String url = ConstantValue.LEDPLAYER_URI
				+ "/public/TerminalState.jsp?i=" + SystemInfo.getAndroidId(mContext) + "&m=" + data;
		HttpClientUtil http = new HttpClientUtil();
		String result = http.sendDataByGet(url);
		Log.i(TAG, result + "===========");
		if (result.equals("0") || result == null) {
			try {
				// 如果返回为0,需要校验数据,重新发送
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sendSerialData2Server(buff);
		}
		// new AsyncTask<String, Void, String>() {
		//
		// @Override
		// protected String doInBackground(String... params) {
		// Log.i(TAG, params[0]);
		// HttpClientUtil http = new HttpClientUtil();
		// String result = http.sendDataByGet(params[0]);
		// Log.i(TAG, "发送串口服务器反馈:" + result);
		// return result;
		// }
		//
		// @Override
		// protected void onPostExecute(String result) {
		// if (result != null) {
		// if (!result.equals("1")) {
		// try {
		// Thread.sleep(5000);
		// sendSerialData2Server(buff);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// super.onPostExecute(result);
		// }
		// }.execute(url);
	}

	public boolean getTempDataFlag() {
		return getTempHumiFlag;
	}

	/**
	 * 得到温度
	 * 
	 * @return
	 */
	public byte getTemperature() {
		getTempHumiFlag = false;
		return temperature;
	}

	/**
	 * 湿度。
	 * 
	 * @return
	 */
	public byte getHumidity() {
		getTempHumiFlag = false;
		return humidity;
	}

	private boolean getLed2Status() {
		if ((wLedStatus & 0x02) != 0) {
			return true;
		} else {
			return false;

		}
	}

	private boolean getLed1Status() {
		if ((wLedStatus & 0x01) != 0) {
			return true;
		} else {
			return false;

		}
	}

	/**
	 * 设置led开关的状态，开与关。
	 * 
	 * @param on
	 * @return
	 */
	private boolean setLed1OnOff(boolean on) {
		if (ledFd != 0) {
			if (on) {
				wLedStatus |= 0x01;
			} else {
				wLedStatus &= ~(0x01);
			}
			ledWrite(ledFd, wLedStatus);
			// Log.v(TAG, "Led1 fd set ok");
			return true;
		} else {
			Log.v(TAG, "Led fd null");
			return false;
		}
	}

	private boolean setLed2OnOff(boolean on) {
		if (ledFd != 0) {
			if (on) {
				wLedStatus |= 0x02;
			} else {
				wLedStatus &= ~(0x02);
			}
			ledWrite(ledFd, wLedStatus);
			// Log.v(TAG, "Led2 fd set ok");
			return true;
		} else {
			Log.v(TAG, "Led2 fd null");
			return false;
		}

	}

	/**
	 * open:打开串口设备， path：串口设备路径； baudrate：串口波特率； flag：0
	 */
	// JNI
	private native static FileDescriptor open(String path, int baudrate,
			int flags);

	/**
	 * close:关闭串口
	 */
	public native void close();

	/**
	 * ledOpen:打开LED设备 path：Led设备路径：/dev/LED_file flags：0
	 */
	private native static int ledOpen(String path, int flags);

	/**
	 * ledClose:关闭LED设备
	 */
	private native static void ledClose(int ledFd);

	/**
	 * ledWrite：写数据到LED设备 data:LED状态： 0：led1、2 off； 1：LED1：on，Led2：off
	 * 2：LED1：off，Led2：on 3：LED1：on，Led2：on
	 */
	private native static void ledWrite(int ledFd, int wLedStatus);

	static {

		System.loadLibrary("serial_port");

		// System.loadLibrary("myserial");
	}

}