package com.led.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.led.player.SerialPort;
import com.led.player.utils.ToastUtil;

public class AutoConfigReciver extends BroadcastReceiver {
	public static final String AUTOREBOOT = "com.led.player.AutoReboot";
	public static final String AUTOONOFF = "com.led.player.AUTOONOFF";
	public static final String AUTOBIGHTNESS = "com.led.player.AUTOBIGHTNESS";
	private SerialPort mSerialPort;
	
	public AutoConfigReciver() {}
	public AutoConfigReciver(SerialPort mSerialPort) {
		this.mSerialPort = mSerialPort;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
//		long lastSetTime = intent.getLongExtra("setTime", -1);
//Log.e("设定闹钟的时候是：", (System.currentTimeMillis()-lastSetTime+""))	;	
//Log.e("设定的时间是：", "setTIme:"+lastSetTime);
//		if (System.currentTimeMillis()-lastSetTime>5000) {
//			Log.e("你妹妹的", "谁教你手动调整时间的啊");   不行会导致第二次甚至永远不会再循环其他得了。
//			return;
//		}
//		ToastUtil.setRBShow(context, "收到广播："+intent.getAction(), 1);
		String getAction = intent.getAction();
		if (AUTOREBOOT.equals(getAction)) {
			//配置自动重启。
//			EthernetSet.writeOsData("reboot -n");
			Intent rebootIntent = new Intent("com.led.player.SERIAL_HARD_REBOOT");
			context.sendBroadcast(rebootIntent);
			
		}else
		if (AUTOONOFF.equals(getAction)) {
			//自动开关屏幕
Log.e("自动开关屏幕", "自动开关屏幕。。。");		
			byte state = intent.getByteExtra("state", (byte) 1);
			//自动调节亮度。
			byte[] reciveByte = createPackage(2,state);
			byte[] comData = new byte[28];
			for (int i = 0; i < 28; i++) {
				comData[i] = reciveByte[i + 8];
			}
			mSerialPort.setNetSendData(comData, 28);
//Log.e("距离下次开屏时长", "距离下次开屏的时长是："+intent.getLongExtra("nextOnTime", -1))			;

		}else
		if(AUTOBIGHTNESS.equals(getAction)){
Log.e("亮度调节", "亮度调节。。。");
			byte bright = intent.getByteExtra("bright", (byte) -1);
			//自动调节亮度。
			byte[] reciveByte = createPackage(1,bright);
			byte[] comData = new byte[28];
			for (int i = 0; i < 28; i++) {
				comData[i] = reciveByte[i + 8];
			}
//Log.e("难道回事kong？？", TCPUDPserver.pSerialPort+"")			;
//			TCPUDPserver.pSerialPort.setNetSendData(comData, 28);
			mSerialPort.setNetSendData(comData, 28);
			
		}
		
	}
	
	/**
	 * 创建一个串口数据
	 * @param operType  数据类型。
	 * @param metaData  关键数据字段
	 * @return
	 */
	private byte[] createPackage(int operType,byte metaData){
		byte[] data = new byte[37];
		data[0] = (byte) 0xaa;
		data[1] = 0x55;
		data[2] = 0;
		data[3] = 0;
		data[4] = 0;
		data[5] = 37;
		data[6] = 0x02;
		data[7] = 0x01;
		
		data[8] = 0x55;
		data[9] = 0x55;
		data[10] = (byte) 0xab;
		data[11] = (byte) 0xcd;
		data[12] = (byte) 0xff;
		data[13] = (byte) 0xaf;
		data[14] = 0x00;
		data[15] = 0x00;	//这个和下一个用来表示设置亮度的 消息类型和功能吗
		data[16] = 0x08;
		data[17] = 0x00; //这个和下个字节是用来  保留的。
		data[18] = 0x00;
		for (int i = 0; i < 16; i++) {
			//16个有效数据的赋值初始化
			data[19+i]  = 0x69;
		}
		//串口协议数据开始
		switch (operType) {
		case 1:
			data[19] = metaData; //调节亮度
			break;
		case 2:
			data[13] = (byte) 0xac;
			data[16] = 0x00;
			data[17] = 0x69;
			data[18] = 0x69;
			data[19]  =(byte) (metaData==1?0x02:0x04);  //开关瓶
			break;
		}
		//效验和 28个的
		byte xyh28 = 0;
		for (int i = 12; i < 35; i++) {
			xyh28 ^= data[i];
		}
		data[35] = xyh28;
		
		//全部效验和
		for (int i = 0; i < data.length-1; i++) {
			data[36] += data[i];
		}
		
PrintBuffer(data, data.length);			//打印下
		return data;
		
	}
	
	
	public void PrintBuffer(byte[] buffer, int len) {
		System.out.print("rec: ");
		for (int i = 0; i < len; i++) {
			String hex = Integer.toHexString(buffer[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println(" ");
	}
	

}
