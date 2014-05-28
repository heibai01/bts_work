package com.led.player;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.led.player.biz.DisplayerBiz;
import com.led.player.moudle.APInfo;
import com.led.player.service.HandleXMLFiles;
import com.led.player.service.MessageHandler;
import com.led.player.service.ResposHandler;
import com.led.player.service.StopHandler;
import com.led.player.service.TransHandler;
import com.led.player.utils.ApinfoParse;
import com.led.player.utils.CommUtils;
import com.led.player.utils.EthernetSet;
import com.led.player.utils.SetTime;

/**
 * com Server
 * 
 * @author Aina.huang E-mail: 674023920@qq.com
 * @version ����ʱ�䣺2010 Jul 14, 2010 10:45:35 AM ��˵��
 */
public class TCPUDPserver {
	public static boolean isSetIp = false;  //是否设置了ip
	public static final boolean isJoyDebug = false;
	public static boolean isClientOn = false; // 客户端是否在线。
	private static final int PORT = 4096;//
	private List<Socket> mList = new ArrayList<Socket>();
	private ServerSocket server = null;
	private ExecutorService mExecutorService = null;
	private Handler mHandler;
	private int jpegFileCount = 0;
	private ScoketServerS pScoketServer;
	private UdpClient pUdpClient;

	public static SerialPort pSerialPort = null;
	private Context mContext;
	private DisplayerBiz mDisplayerBiz = null;

	public TCPUDPserver(final Handler mHandler, Context contxt, SerialPort pSerialPort) {
		TCPUDPserver.pSerialPort = pSerialPort;

		this.mHandler = mHandler;
		this.mContext = contxt;
		mDisplayerBiz = new DisplayerBiz(contxt);
		pScoketServer = new ScoketServerS();
		pScoketServer.setName("TCPUDPServerThread");
		pScoketServer.start();
		// Log.v("UDP service","udp services start");
		pUdpClient = new UdpClient();
		pUdpClient.start();
		
//	    new Timer("udp服务是不是活着").schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				mHandler.sendMessage(Message.obtain(null, LedProtocol.TOAST_MSG, (Object)pUdpClient.isAlive()));
//			}
//		}, 0,8000);
		
		
	}

	/**
	 * 好像是 发送从串口来的数据 到网口，然后网口发送到 pc端
	 * 
	 * @param sendData
	 *            串口数据数组28
	 * @param len
	 *            28长度
	 */
	public void sendUartData(byte[] sendData, int len) {
		byte[] bsendMessage = new byte[9 + len];
		int packSendLen = 9 + len;
		int checkSum = 0, copyDataLen;
		bsendMessage[0] = (byte) 0xaa;
		bsendMessage[1] = (byte) 0x55;
		bsendMessage[2] = (byte) ((packSendLen >> 24) & 0xff);
		bsendMessage[3] = (byte) ((packSendLen >> 16) & 0xff);
		bsendMessage[2] = (byte) ((packSendLen >> 8) & 0xff);
		bsendMessage[3] = (byte) (packSendLen & 0xff);
		bsendMessage[6] = (byte) 0x02;
		bsendMessage[7] = (byte) 0x02;

		for (copyDataLen = 0; copyDataLen < len; copyDataLen++) {
			bsendMessage[8 + copyDataLen] = sendData[copyDataLen];
		}
		for (copyDataLen = 0; copyDataLen < len + 8; copyDataLen++)
			checkSum += bsendMessage[copyDataLen];
		bsendMessage[copyDataLen] = (byte) (checkSum);

		int num = mList.size();
		System.out.println("send uardata socket List size is num: " + num);
		ArrayList<Socket> outSockets = new ArrayList<Socket>();
		for (int i = 0; i < num; i++) {
			Socket mSocket = mList.get(i);

			if (mSocket != null && mSocket.isClosed() == false
					&& mSocket.isConnected()) {
				try {
					OutputStream output = mSocket.getOutputStream();
					// Log.v("send message","Senddata-----");
					output.write(bsendMessage, 0, packSendLen);

				} catch (Exception ex) {
					System.out.println("serverSend failed!");
					outSockets.add(mSocket); // 掉线的客户端移除
					if (isJoyDebug)				Log.i("joybug", mSocket + " 是将被移除的socket");
					ex.printStackTrace();
				}
			}
		}
		// 移除 掉线的客户端
		for (Socket socket : outSockets) {
			if (isJoyDebug)			Log.i("joybug", socket + " 是被移除的socket");
			mList.remove(socket);
		}
	}

	private static String parseByte(byte b) {
		int intValue = 0;
		if (b >= 0) {
			intValue = b;
		} else {
			intValue = 256 + b;
		}
		return Integer.toHexString(intValue);
	}

	public static byte[] getCurrentMac() {
		List<String> list = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();//
			while (e.hasMoreElements()) {
				NetworkInterface network = e.nextElement();
				if (network != null) {
					if (network.isUp()) {
						if (network.getHardwareAddress() != null) {
							// network.
							return network.getHardwareAddress();
							/*
							 * byte[] addres = network.getHardwareAddress();
							 * 
							 * StringBuffer sb = new StringBuffer(); if (addres
							 * != null && addres.length > 1) {
							 * sb.append(parseByte
							 * (addres[0])).append(":").append(
							 * parseByte(addres[1])).append(":").append(
							 * parseByte(addres[2])).append(":").append(
							 * parseByte(addres[3])).append(":").append(
							 * parseByte(addres[4])).append(":").append(
							 * parseByte(addres[5]));
							 * Log.v("getAllMac",sb.toString());
							 * list.add(sb.toString());
							 */
						}
					}
				} else {
					System.out.println("��ȡMAC��ַ�����쳣");
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取本地IP函数 方法里面判断IPV4和IPV6如果IPV4不为空返回IPV4。
	 * 因为如果不加判断默认返回时IPV6的地址，也就是一串字符串。那是IPV6的IP表示方式。
	 * 
	 * @return
	 */
	public String getLocalIpAddress() {
		try {
			String ipv4;

			ArrayList<NetworkInterface> mylist = Collections.list(NetworkInterface.getNetworkInterfaces());
			// for (NetworkInterface networkInterface : mylist)
			// {//eth0,lo,tun10,sit0,ip6tn10,wlan0
			// Log.e("网卡的名称", networkInterface.getName()) ;
			// }
			for (NetworkInterface ni : mylist) {
				if (ni != null && ni.isUp()) {
					ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
					for (InetAddress address : ialist) {
						if (!address.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
							return ipv4;
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("TCPUDPserver", "设备无网络接口");
			ex.printStackTrace();
		}
		return null;
	}

	public class UdpClient extends Thread {

		byte[] messageByte = new byte[100];

		public void run() {
			setName("请叫我UDP_SERVER");
			Integer port = 4096;
			byte[] message = new byte[65535];
			try {

				DatagramSocket datagramSocket = new DatagramSocket(port);
//				datagramSocket.setReuseAddress(true);// mark
//				Log.e("TCPUDPServer", "绑定了："+datagramSocket.isBound()+" 连接了："+datagramSocket.isConnected()
//						+"本地地址："+datagramSocket.getLocalAddress()+""+datagramSocket.getRemoteSocketAddress());
//				datagramSocket.bind(new InetSocketAddress(port));
				Log.e("TCPUDPserver", "udp启动了");
				byte[] currMacData = getCurrentMac();

				datagramSocket.setBroadcast(true);
				DatagramPacket inPackage = new DatagramPacket(message,message.length);
				try {
					// List<ArpInfo> arpInfos = new ArrayList<ArpInfo>();
					while (true) {
	if (isJoyDebug)Log.e("UDP SERVER", "udp server is alive-----------udp的接收是阻塞的");						
						// int length = inPackage.getLength();
						// Log.e("joybug", "接收之前得到的长度"+length);
						inPackage.setData(message);// 每次接受完毕后容量不会变小。但是会限制你接受数据的大小为上次那个大小的容量
						datagramSocket.receive(inPackage);
						byte[] tempData = inPackage.getData();  
//						 Log.e("joytest", tempData==message?"相等数组":"不是相同个"); //相等数组
if (isJoyDebug)Log.e("TCPUDPServer","----------------------------udp get------------------------------");
						// TCPUDPserver.PrintBuffer(tempData, inPackage.getLength());
if (isJoyDebug) Log.i("UDP Demo","上位机ip和端口   " + inPackage.getAddress() + ":"+ inPackage.getPort());
if (isJoyDebug) Log.i("TCPUDPserver","udpSocket收到的数据长度：" + inPackage.getLength());
//PrintBuffer(inPackage.getData(), inPackage.getData().length);
//Log.e("TCPUDPServer", "udp收到的packe长度："+inPackage.getData().length);

						if ((tempData[0] == (byte) 0xaa)&& (tempData[1] == (byte) 0x55)) {
							if ((tempData[6] == (byte) 0x01)&& (tempData[7] == (byte) 0x01)) {
								if (isJoyDebug)Log.e("搜索广播", "搜素广播指令收到");
								sendRespPack(currMacData, inPackage, 0x02);
									
							}else
							//高大上的一条协议
							if ((tempData[6] == (byte) 0x01)&& (tempData[7] == (byte) 0x05)) {
								if (isJoyDebug)	Log.e("查找板卡", "已受到搜索请求");
								sendFullRespPack(currMacData,inPackage,0x06);
							}else
						    //最新的最全的搜索板卡协议，带当前板卡是否属于主机的标识
							if ((tempData[6] == (byte) 0x01)&& (tempData[7] == (byte) 0x07)) {
								Log.e("TCPUDPServer", "重构版上位机最新搜索协议");
								sendFullMoreRespPack(currMacData, inPackage, 0x08);
							}else
							//局域网同步点播指令接收到
							if(tempData[6]==14 && tempData[7] ==1 ){
								if (LedService.currPlayType==LedService.LAN_SYNC_LOOP_PLAY_TYPE) {
//									Log.e("TCPUDPServer", "--------------fuck,自己也收到了局域网同步播放请求------------------");
								}else {
									sendLanSyncloop(tempData[8]);
								}
							}
							

							// 发送udp让我设置ip成 169.254.x.x的样子(连接控制卡用)
							else if ((tempData[6] == 0x01) && (tempData[7] == 0x03)) {
								if (inPackage.getLength() != 23) {
									// PrintBuffer(tempData, 24);
									if (isJoyDebug)	Log.e("TCPUDPserver","udp数据连包了,丢弃此次来此上位机的广播数据");
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									continue;
								}
								// 下标8~13 是mac, 14~17是ip，18~21是mask22是效验和
								String recvIP = null;
								String mask = null;

								// 组合ip开始
								int ip1 = tempData[14], ip2 = tempData[15], ip3 = tempData[16], ip4 = tempData[17];
								if (tempData[14] < 0) {
									ip1 = 256 + tempData[14];
								}
								if (tempData[15] < 0) {
									ip2 = 256 + tempData[15];
								}
								if (tempData[16] < 0) {
									ip3 = 256 + tempData[16];
								}
								if (tempData[17] < 0) {
									ip4 = 256 + tempData[17];
								}
								// 组合ip字符串
								recvIP = ip1 + "." + ip2 + "." + ip3 + "."+ ip4;
								// 组合netmask掩码开始
								int mask1 = tempData[18], mask2 = tempData[19], mask3 = tempData[20], mask4 = tempData[21];
								if (mask1 < 0) {
									mask1 += 256;
								}
								if (mask2 < 0) {
									mask2 += 256;
								}
								if (mask3 < 0) {
									mask3 += 256;
								}
								if (mask4 < 0) {
									mask4 += 256;
								}
								// try {
								// Log.e("joyching", "测试发的错误产生arp");
								// EthernetSet.setEthIP(recvIP, mask);
								// sendRespPack(currMacData, inPackage, 0x08);
								// } catch (Exception e) {
								// e.printStackTrace();
								// }
								// 解析出mac地址 9~14
								int mac_1 = tempData[8], mac_2 = tempData[9], mac_3 = tempData[10], mac_4 = tempData[11], mac_5 = tempData[12], mac_6 = tempData[13];
								String mac_str = Integer.toHexString(mac_1)
										+ ":" + Integer.toHexString(mac_2)
										+ ":" + Integer.toHexString(mac_3)
										+ ":" + Integer.toHexString(mac_4)
										+ ":" + Integer.toHexString(mac_5)
										+ ":" + Integer.toHexString(mac_6);
if (isJoyDebug)Log.v("tcpudpserver", "pc_mac前面6个：" + mac_str);

								// 组合netmask掩码字符串
								mask = mask1 + "." + mask2 + "." + mask3 + "."+ mask4;
if (isJoyDebug)Log.v("TCPUDPserver", "从数据包中解析的senderIP:" + recvIP+ " mask:" + mask);
								String linkIP = inPackage.getAddress().getHostAddress();
if (isJoyDebug)Log.v("TCPUDPserver", "从数据报附带的：" + linkIP);
								// Log.e("gdgdg", "从保重获得主机名："+hostName);

								ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
								NetworkInfo etherNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);//mark 这里也会阻塞，这个时候是cpu使用率90%以上高负荷工作。
								boolean isEtherneted = (etherNetInfo != null&& etherNetInfo.isConnected());
								NetworkInfo threeGInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
								boolean isThreeGInfo = threeGInfo != null&& threeGInfo.isConnected();
								NetworkInfo wifiNetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
								boolean isWifiConnected = (wifiNetInfo!=null)&&wifiNetInfo.isConnected();
if (isJoyDebug)Log.e("网卡与3G卡的目前状态", (isEtherneted?etherNetInfo.getTypeName():"以太未连接")+"--"+(isThreeGInfo?threeGInfo.getTypeName():"3G未连接"));								
								if (linkIP != null&& linkIP.equals(recvIP) == false) {
									// 就用这个收到数据里面的netmask，否则不理会cotinue
									continue;
								}
								
								try {
									if(isEtherneted==false){//没连接路由器但是直连时要跟随上位机ip改动，路由怎么也能转播局域网广播啊
										if (isWifiConnected) {
											NetworkInterface networkInterface_wlan = NetworkInterface.getByName("wlan0");
											InterfaceAddress interfaceAddress = networkInterface_wlan.getInterfaceAddresses().get(1);
											String localIPStr = interfaceAddress.getAddress().getHostAddress();
											if (isEqualNetDuan(localIPStr,linkIP,mask)) {
												//判断是否与pc的ip同网段，如果通往段说明大家都是路由器下连接的，
												//不同网段证明pc网线直连和板卡，同时板卡的(如果这个板卡同时wifi又连接到了wifi路由器，但是同局域网内其他pc端可能广播192.xx段
												//的ip过来)
//												NetworkInterface networkInterface_eth = NetworkInterface.getByName("eth0");
//												if (networkInterface_eth!=null&&networkInterface_eth.getInterfaceAddresses().size()>1) {
//													EthernetSet.setNullIP();
//												}
												if (isJoyDebug) Log.e("现在wifi是通的", "wifi目前连接的是 正确的路由器");	//为了清楚以太网ip请重启。										
											}else {
												//连接上其他的路由器，数据不能让他走这里啊。。。必须走以太口(当前有网线直连或者是没网线连接)还可能是路由器下的其他169段的ip
												if (isJoyDebug) Log.e("现在wifi是通的", "wifi目前连接的是 非官方的路由器");	//切记这个非官方的路由不要发信息过来
												NetworkInterface networkInterface_eth = NetworkInterface.getByName("eth0");
												if (networkInterface_eth!=null&&networkInterface_eth.getInterfaceAddresses().size()<=1) {
													if (isJoyDebug)	Log.e("设置ip	", "我要设置ip啊");
													EthernetSet.setEthIP(linkIP, mask);
												}
											}
										}else {//可能是网线直连
											//判断是否在一个段，不在一个段改成一个段，这样连你的wifi口都不用断开一下再连接一下了。
											NetworkInterface networkInterface_eth = NetworkInterface.getByName("eth0");
											if (networkInterface_eth.getInterfaceAddresses().size()>1) {
												InterfaceAddress interfaceAddress = networkInterface_eth.getInterfaceAddresses().get(1);
												String localIPStr = interfaceAddress.getAddress().getHostAddress();
												boolean isEqualNet = isEqualNetDuan(localIPStr,linkIP,mask);
												if (isEqualNet==false) {
													if (isJoyDebug) Log.e("不在一个网段啊", "不在一个网段了");									
																							
													EthernetSet.setEthIP(linkIP, mask);
												}else {
													if (isJoyDebug)	Log.e("在一个网段啊", "在一个网段了，，，，，，！！！！！");												
												}
												
											}
										}
										
										
										
										// 当以太网连接时 所有的网络类型都是 true包括wifi的，不让同时连接2个网
										// 如果以太网连接是false那么要么是 wifi连接了要么是3g连接了。
								/*		if (isThreeGInfo) {
											// 如果3g连接了。不要去清空 以太网的ip地址
											Log.e("TCPUDPServer", "3g网络是通的！！！");
											sendUDPInSeted(currMacData, inPackage,recvIP, mask);
										} else {*///极有可能 3g和以太网都没通。。。。 
											String ethI = getEth0IP();
											// 判断网线是否插入了，如果没插入就把以太网ip清空， 可是目前没找到方法
											// 如果是无线网连接的，就把以太网的ip置为空，这种情况是 板子不能同时
											// 连接无线网和有线网
											//网线直连时得到的ip是 169.254.x.x得到的isEthernetCon是个false
											if (isJoyDebug)	Log.i("TCPUDPServer","可能是网线直连(169端ip得到boolean是false)，以太网ip"+ethI);
//											EthernetSet.setNullIP();
//										}
									}
									sendRespPack(currMacData,inPackage, 0x04);//直连时如果3g网卡存在会导致这个异常不会抛出
								} catch (Exception e) {
									//下面这个错在网线直连(而且是从以太网口出去的数据)会报出来。
									//如果是wlan连上某个路由了，不会报这种错。(这中情况是wifi连接路由网线再直连，数据从wifi口出去了)
									//ping 10.12.3.0
									//PING 10.12.3.0 (10.12.3.0) 56(84) bytes of data.
									//From 192.168.43.1 icmp_seq=1 Destination Net Unreachable
									//当前还有可能是因为以太网卡总是可以得到对象的总是不为null
									if (isJoyDebug)	Log.e("TCPUDPServer","当前是无限网网连接或者上位机ip手动改了，发送数据Network is unreachable");
									e.printStackTrace();
									// 发生异常远程不可达，更改以太网ip 说明已经存在的ip不能联通
									sendUDPInSeted(currMacData,inPackage, recvIP, mask);
								}
							}

						}
						// 清空接受数组
						for (int i = 0; i < 23; i++) {
							message[i] = 0;
						}
					}
				} catch (Exception e) {
					Log.e("TCPUDPServer", "UDPsocket被你弄死了");
					e.printStackTrace();
					datagramSocket.close();
					datagramSocket = null;
					// 重启自己
					pUdpClient = new UdpClient();
					pUdpClient.start();

				}

			} catch (SocketException e) {
				Log.e("TCPUDPserver", "UDPSocket需要的端口" + PORT+ "已经被绑定--UDPSocket");
				e.printStackTrace();
				//重启算了
//				EthernetSet.writeOsData("reboot -f"); 
			}
		}

		/*8
		 * 是否通往段
		 */
		private boolean isEqualNetDuan(String localIPStr, String linkIP,String linkMask) {
			int localIpInt = ip_plus(localIPStr);
			int linkIpInt = ip_plus(linkIP);
			int linkMaskInt = ip_plus(linkMask);
	if (isJoyDebug)Log.e("整形的数据", localIPStr+"----"+linkIpInt+"------"+linkMaskInt)			;
	if (isJoyDebug)Log.e("数据与计算", (localIpInt&linkMaskInt)+"-----"+(linkIpInt&linkMaskInt)+"-------------------"+((localIpInt&linkMaskInt)==(linkIpInt&linkMaskInt)));
			return (localIpInt&linkMaskInt)==(linkIpInt&linkMaskInt);
		}
		
		private int ip_plus(String str){
			String[] duanStrings = str.split("\\.");
			int total = 0;
			for (int i = 0; i < duanStrings.length; i++) {
//				int tt = Integer.parseInt(duanStrings[i]);
//				System.out.println(tt+"该位上的数据："+(tt<<((3-i)*8)));
				total=total+(Integer.parseInt(duanStrings[i])<<((3-i)*8));
			}
			return total;
		}

		/**
		 * 设置本机以太网ip 并且回复
		 * 
		 * @param currMacData
		 * @param inPackage
		 * @param recvIP
		 * @param mask
		 * @throws IOException
		 */
		public void sendUDPInSeted(byte[] currMacData,DatagramPacket inPackage, String recvIP, String mask)	throws IOException {
			boolean isSetIPOK = EthernetSet.setEthIP(recvIP, mask);
			if (isSetIPOK) {
				// 发送应答包，关闭上位机广播
				if (isJoyDebug)				Log.i("TCPUDPServer", "设置以太网ip成功");
				sendRespPack(currMacData, inPackage, 0x04);
			}else {
				if (isJoyDebug)			Log.i("TCPUDPServer", "设置以太网失败了");
			}
		}

		/**
		 * 得到以太网卡 的ip或是无线网卡的ip
		 * 
		 * @throws SocketException
		 */
		public String getEth0IP() throws SocketException {
			NetworkInterface nInterface = NetworkInterface.getByName("eth0");
			Enumeration<InetAddress> iEnumeration = nInterface.getInetAddresses(); // 如果本地没ip就是个null
			if (iEnumeration != null) {
				while (iEnumeration.hasMoreElements()) {
					InetAddress inetAddress = iEnumeration.nextElement();
					// Log.e("没判断前ip", inetAddress.getHostAddress());
					// //有一个ipv6地址和ipv4地址
					String ip = inetAddress.getHostAddress();
					if (inetAddress.isLoopbackAddress() == false&& InetAddressUtils.isIPv4Address(ip)) {
						return ip;
					}
				}
			}
			return null;
		}

		/**
		 * 发送回复包。
		 * 
		 * @param currMacData
		 * @param datagramPacket
		 * @throws IOException
		 *             //如果本机ip还是0.0.0.0 那会会是 Network is unrecichable 错误
		 */
		private void sendRespPack(byte[] currMacData,DatagramPacket datagramPacket, int funcCode) throws IOException {
			byte[] sendDataBuff = new byte[35];
			byte count;
			int sum = 0;
			sendDataBuff[0] = (byte) 0xaa;
			sendDataBuff[1] = (byte) 0x55;
			sendDataBuff[2] = (byte) 0;
			sendDataBuff[3] = (byte) 0;
			sendDataBuff[4] = (byte) 0;
			sendDataBuff[5] = (byte) 35;
			sendDataBuff[6] = (byte) 1;
			sendDataBuff[7] = (byte) funcCode;
			sendDataBuff[8] = currMacData[0];
			sendDataBuff[9] = currMacData[1];
			sendDataBuff[10] = currMacData[2];
			sendDataBuff[11] = currMacData[3];
			sendDataBuff[12] = currMacData[4];
			sendDataBuff[13] = currMacData[5];
			//别名 here mark
//			for (count = 0; count < 16; count++) {
//				sendDataBuff[14 + count] = (byte) ('A' + count);
//			}
			//读取别名
			SharedPreferences sPreferences = mContext.getSharedPreferences("device_config", Context.MODE_PRIVATE);
			String getNickName = sPreferences.getString("nick_name", null);
			if (getNickName==null) {
				sendDataBuff[14] = 'L';
				sendDataBuff[15] = 'E';
				sendDataBuff[16] = 'D';
			}else {
//				Log.e("Tcpudpserver", getNickName.length()+"--是别名长度");//32ge
				for (int i = 0,j=0; i<16;i++,j+=2) {
					int num = Integer.parseInt(getNickName.substring(j, j+2), 16);
//					num = num<0?(256+num):num;
//					sendDataBuff[14+i] = Byte.parseByte(num+"",16);
					sendDataBuff[14+i] = (byte) num;
				}
				//打印下
				StringBuilder sBuilder = new StringBuilder();
				for (int i = 14; i < 30; i++) {
					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
					if (hex.length() == 1) {
						hex = '0' + hex;
					}
//					System.out.print(hex.toUpperCase() + " ");
					sBuilder.append(hex.toUpperCase() + " ");
				}
//				Log.e("TCPUDPServer","回复打印别名："+ sBuilder.toString());
			}
			//读取品题宽度
			String width_height= sPreferences.getString("device_width_height", null);
			if (width_height!=null) {
				sendDataBuff[31] = (byte) Integer.parseInt(width_height.substring(0,2), 16);
				sendDataBuff[30] = (byte) Integer.parseInt(width_height.substring(2,4), 16);;
				sendDataBuff[33] = (byte) Integer.parseInt(width_height.substring(4,6), 16);;
				sendDataBuff[32] = (byte) Integer.parseInt(width_height.substring(6,8), 16);;
				//打印下
//				StringBuilder sBuilder = new StringBuilder();
//				for (int i = 30; i < 34; i++) {
//					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
//					if (hex.length() == 1) {
//						hex = '0' + hex;
//					}
//				System.out.print(hex.toUpperCase() + " ");
//					sBuilder.append(hex.toUpperCase() + " ");
//				}
//				Log.e("TCPUDPServer","回复打印宽度高度："+ sBuilder.toString());
			}else {
				sendDataBuff[30] = 0x01;
				sendDataBuff[31] = (byte) 0x40;
				sendDataBuff[32] = 0x00;
				sendDataBuff[33] = (byte) 0xF0;
				
			}
			
			for (count = 0; count < 34; count++) {
				sum += sendDataBuff[count];
			}
			sendDataBuff[count] = (byte) sum;
			send(sendDataBuff, 35, datagramPacket.getAddress(),datagramPacket.getPort());
		}

		/**
		 * @author joychine@qq.com I used to be a programer as you when I token an arrow in the knee.
		 * @param currMacData
		 * @param datagramPacket
		 * @param funcCode
		 * @throws IOException
		 */
		private void sendFullRespPack(byte[] currMacData,DatagramPacket datagramPacket, int funcCode) throws IOException{
			byte[] sendDataBuff = new byte[71];
			byte count;
			int sum = 0;
			sendDataBuff[0] = (byte) 0xaa;
			sendDataBuff[1] = (byte) 0x55;
			sendDataBuff[2] = (byte) 0;
			sendDataBuff[3] = (byte) 0;
			sendDataBuff[4] = (byte) 0;
			sendDataBuff[5] = (byte) 71;
			sendDataBuff[6] = (byte) 1;
			sendDataBuff[7] = (byte) funcCode;
			sendDataBuff[8] = currMacData[0];
			sendDataBuff[9] = currMacData[1];
			sendDataBuff[10] = currMacData[2];
			sendDataBuff[11] = currMacData[3];
			sendDataBuff[12] = currMacData[4];
			sendDataBuff[13] = currMacData[5];
			//别名 here mark
//			for (count = 0; count < 16; count++) {
//				sendDataBuff[14 + count] = (byte) ('A' + count);
//			}
			//读取别名
			SharedPreferences sPreferences = mContext.getSharedPreferences("device_config", Context.MODE_PRIVATE);
			String getNickName = sPreferences.getString("nick_name", null);
			if (getNickName==null) {
				sendDataBuff[14] = 'L';
				sendDataBuff[15] = 'E';
				sendDataBuff[16] = 'D';
			}else {
//				Log.e("Tcpudpserver", getNickName.length()+"--是别名长度");//32ge
				for (int i = 0,j=0; i<16;i++,j+=2) {
					int num = Integer.parseInt(getNickName.substring(j, j+2), 16);
//					num = num<0?(256+num):num;
//					sendDataBuff[14+i] = Byte.parseByte(num+"",16);
					sendDataBuff[14+i] = (byte) num;
				}
				//打印下
//				StringBuilder sBuilder = new StringBuilder();
//				for (int i = 14; i < 30; i++) {
//					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
//					if (hex.length() == 1) {
//						hex = '0' + hex;
//					}
////					System.out.print(hex.toUpperCase() + " ");
//					sBuilder.append(hex.toUpperCase() + " ");
//				}
//				Log.e("TCPUDPServer","回复打印别名："+ sBuilder.toString());
			}
			//读取品题宽度
			String width_height= sPreferences.getString("device_width_height", null);
			if (width_height!=null) {
				sendDataBuff[31] = (byte) Integer.parseInt(width_height.substring(0,2), 16);
				sendDataBuff[30] = (byte) Integer.parseInt(width_height.substring(2,4), 16);;
				sendDataBuff[33] = (byte) Integer.parseInt(width_height.substring(4,6), 16);;
				sendDataBuff[32] = (byte) Integer.parseInt(width_height.substring(6,8), 16);;
				//打印下
//				StringBuilder sBuilder = new StringBuilder();
//				for (int i = 30; i < 34; i++) {
//					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
//					if (hex.length() == 1) {
//						hex = '0' + hex;
//					}
//				System.out.print(hex.toUpperCase() + " ");
//					sBuilder.append(hex.toUpperCase() + " ");
//				}
//				Log.e("TCPUDPServer","回复打印宽度高度："+ sBuilder.toString());
			}else {
				sendDataBuff[30] = 0x01;
				sendDataBuff[31] = (byte) 0x40;
				sendDataBuff[32] = 0x00;
				sendDataBuff[33] = (byte) 0xF0;
				
			}
			//版本号
			int verCode1 = CommUtils.getVersionCode(mContext, "com.led.player");
			int verCode2 = CommUtils.getVersionCode(mContext, "com.led.clientscan");
			sendDataBuff[34] =0;
			sendDataBuff[35] = (byte) verCode1;
			sendDataBuff[36] = 0;
			sendDataBuff[37] = (byte) verCode2;
			
			byte[] time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("utf-8");
			for (int k = 0; k < time.length; k++) {
				sendDataBuff[38+k] = time[k];
			}
			
			if (isJoyDebug) Log.e("时间是版本是", new String(sendDataBuff, 38, 32, "utf-8")+"\n版本1："+verCode1+"\n版本2:"+verCode2);
			//和为贵！！
			for (count = 0; count < 70; count++) {
				sum += sendDataBuff[count];
			}
			sendDataBuff[count] = (byte) sum;
			send(sendDataBuff, sendDataBuff.length, datagramPacket.getAddress(),datagramPacket.getPort());
		}
		
		
		private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		/**
		 * 重构版上位机搜索用的 协议。回复中带有板卡当前是否是局域网同步播放的主机
		 * @param currMacData
		 * @param datagramPacket
		 * @param funcCode
		 * @throws IOException
		 */
		private void sendFullMoreRespPack(byte[] currMacData,DatagramPacket datagramPacket, int funcCode) throws IOException{
			byte[] sendDataBuff = new byte[76];
			byte count;
			int sum = 0;
			sendDataBuff[0] = (byte) 0xaa;
			sendDataBuff[1] = (byte) 0x55;
			sendDataBuff[5] = (byte) 76;
			sendDataBuff[6] = (byte) 1;
			sendDataBuff[7] = (byte) funcCode;
			sendDataBuff[8] = currMacData[0];
			sendDataBuff[9] = currMacData[1];
			sendDataBuff[10] = currMacData[2];
			sendDataBuff[11] = currMacData[3];
			sendDataBuff[12] = currMacData[4];
			sendDataBuff[13] = currMacData[5];
			//别名 here mark
//			for (count = 0; count < 16; count++) {
//				sendDataBuff[14 + count] = (byte) ('A' + count);
//			}
			//读取别名
			SharedPreferences sPreferences = mContext.getSharedPreferences("device_config", Context.MODE_PRIVATE);
			String getNickName = sPreferences.getString("nick_name", null);
			if (getNickName==null) {
				sendDataBuff[14] = 'L';
				sendDataBuff[15] = 'E';
				sendDataBuff[16] = 'D';
			}else {
//				Log.e("Tcpudpserver", getNickName.length()+"--是别名长度");//32ge
				for (int i = 0,j=0; i<16;i++,j+=2) {
					int num = Integer.parseInt(getNickName.substring(j, j+2), 16);
//					num = num<0?(256+num):num;
//					sendDataBuff[14+i] = Byte.parseByte(num+"",16);
					sendDataBuff[14+i] = (byte) num;
				}
				//打印下
//				StringBuilder sBuilder = new StringBuilder();
//				for (int i = 14; i < 30; i++) {
//					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
//					if (hex.length() == 1) {
//						hex = '0' + hex;
//					}
////					System.out.print(hex.toUpperCase() + " ");
//					sBuilder.append(hex.toUpperCase() + " ");
//				}
//				Log.e("TCPUDPServer","回复打印别名："+ sBuilder.toString());
			}
			//读取品题宽度
			String width_height= sPreferences.getString("device_width_height", null);
			if (width_height!=null) {
				sendDataBuff[31] = (byte) Integer.parseInt(width_height.substring(0,2), 16);
				sendDataBuff[30] = (byte) Integer.parseInt(width_height.substring(2,4), 16);;
				sendDataBuff[33] = (byte) Integer.parseInt(width_height.substring(4,6), 16);;
				sendDataBuff[32] = (byte) Integer.parseInt(width_height.substring(6,8), 16);;
				//打印下
//				StringBuilder sBuilder = new StringBuilder();
//				for (int i = 30; i < 34; i++) {
//					String hex = Integer.toHexString(sendDataBuff[i] & 0xFF);
//					if (hex.length() == 1) {
//						hex = '0' + hex;
//					}
//				System.out.print(hex.toUpperCase() + " ");
//					sBuilder.append(hex.toUpperCase() + " ");
//				}
//				Log.e("TCPUDPServer","回复打印宽度高度："+ sBuilder.toString());
			}else {
				sendDataBuff[30] = 0x01;
				sendDataBuff[31] = (byte) 0x40;
				sendDataBuff[32] = 0x00;
				sendDataBuff[33] = (byte) 0xF0;
				
			}
			//版本号
			int verCode1 = CommUtils.getVersionCode(mContext, "com.led.player");
			int verCode2 = CommUtils.getVersionCode(mContext, "com.led.clientscan");
			sendDataBuff[34] =(byte) (verCode1>>24);
			sendDataBuff[35] = (byte) (verCode1>>16);
			sendDataBuff[36] = (byte) (verCode1>>8);
			sendDataBuff[37] = (byte) verCode1;
			sendDataBuff[38] = (byte) (verCode2>>24);
			sendDataBuff[39] = (byte) (verCode2>>16);
			sendDataBuff[40] = (byte) (verCode2>>8);
			sendDataBuff[41] = (byte) (verCode2);
			
			
			byte[] time =sdf.format(new Date()).getBytes("utf-8");
			for (int k = 0; k < time.length; k++) {
				sendDataBuff[41+k] = time[k];
			}
			
			if (isJoyDebug) Log.e("时间是版本是", new String(sendDataBuff, 41, 32, "utf-8")+"\n版本1："+verCode1+"\n版本2:"+verCode2);
			//和为贵！！
			for (count = 0; count < 75; count++) {
				sum += sendDataBuff[count];
			}
			sendDataBuff[count] = (byte) sum;
			send(sendDataBuff, sendDataBuff.length, datagramPacket.getAddress(),datagramPacket.getPort());
		}
		
		public void send(byte[] message, int messLen, InetAddress remoteIP,	int remotPort) throws IOException {
			// message = (message == null ? "Hello IdeasAndroid!" : message);
			int server_port = remotPort;
			DatagramSocket s = null;
				// 这里需要绑定 到一个能与外界通讯的 ip上。
//				ConnectivityManager cm = (ConnectivityManager) mContext
//						.getSystemService(Context.CONNECTIVITY_SERVICE);
//				NetworkInfo wifi_info = cm
//						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//				NetworkInfo eth_info = cm
//						.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
//				Log.i("所发生的的", "wifi连接：" + wifi_info.isConnected());// 要能连接上一个路由设备才会是true,否则是false
//				Log.i("所发生的的", "以太网连接：" + eth_info.getState());// 要能连接上一个路由设备才会是CONNECTED,否则是DISCONNECTED

				// if (wifi_info!=null&&wifi_info.isConnected()) {
				// // s = new DatagramSocket();
				// //随便绑定，以太网都没ip了，必须指定wlan的ip不然会绑定回环地址。
				// // String boundIP = s.getLocalAddress().getHostAddress();
				// // Log.e("joy", "绑定的ip:"+boundIP); //得到的为啥是个空
				// WifiManager wm = (WifiManager)
				// mContext.getSystemService(Context.WIFI_SERVICE);
				// String ip =
				// WifiHotSpot.intToString(wm.getConnectionInfo().getIpAddress());
				// Log.e("joybog", "无线网卡ip:"+ip);
				// s = new DatagramSocket(new InetSocketAddress(ip,
				// server_port));
				// Log.e("joyboug",
				// "给datagram绑定的ip:"+s.getLocalAddress().getHostAddress());
				// }else if (eth_info!=null) {
				// s = new DatagramSocket(new InetSocketAddress(getEth0IP(),
				// server_port));
				// }
				NetworkInterface networkInterface_eth = NetworkInterface.getByName("eth0");//我的s8600居然有这个没wlan0奇葩弄错名了吧
				NetworkInterface networkInterface_ppp = NetworkInterface.getByName("ppp0");
				NetworkInterface networkInterface_wlan = NetworkInterface.getByName("wlan0");
				
//				Enumeration<NetworkInterface> netwoEnumeration = NetworkInterface.getNetworkInterfaces();
				List<InterfaceAddress> eth_interfaceAddresses = networkInterface_eth.getInterfaceAddresses();
				for (InterfaceAddress interfaceAddress : eth_interfaceAddresses) {
					if (isJoyDebug)				Log.e("以太网设备信息", "以太地址："+interfaceAddress.getAddress().getHostAddress());
				}
				if (networkInterface_wlan!=null) {
					List<InterfaceAddress> wlan_interfaceAddresses = networkInterface_wlan.getInterfaceAddresses();//连接wifi过程中会有时关闭wifi网卡。
					for (InterfaceAddress interfaceAddress : wlan_interfaceAddresses) {
						if (isJoyDebug)					Log.e("wlan设备信息", "wlan地址："+interfaceAddress.getAddress().getHostAddress());
					}
				}
				
				if (eth_interfaceAddresses.size()>1) {
					InterfaceAddress interfaceAddress_eth = eth_interfaceAddresses.get(1);//当这个网卡还没ip这里必然报错，不过那边有处理。会设置ip后再次进入这里
					s = new DatagramSocket(new InetSocketAddress(interfaceAddress_eth.getAddress(), 0));
				}else if (networkInterface_wlan!=null&&networkInterface_wlan.getInterfaceAddresses().size()>1) {
					//如果
					InterfaceAddress interfaceAddress_wlan = networkInterface_wlan.getInterfaceAddresses().get(1);
					s = new DatagramSocket(new InetSocketAddress(interfaceAddress_wlan.getAddress(), 0));
					
				}else{
					Log.e("TCPUDPserver.java", "不能啊，这里来了： 这个时候就是说不支持ipv6咯");
					s = new DatagramSocket();
				}

//				s = new DatagramSocket();
				if (isJoyDebug) Log.e("joyboug",	 "给datagram绑定的ip:"+s.getLocalAddress().getHostAddress()+"-----端口："+s.getLocalPort());
				DatagramPacket respPacket = new DatagramPacket(message, messLen,remoteIP, server_port);
				
//				boolean isReachable = s.getLocalAddress().isReachable(300);
//				if (!isReachable) {
//					EthernetSet.setNullIP();
//				}
				s.send(respPacket);//只要是wlan连了路由，这个永远不会爆出异常connect: Network is unreachable而是 From 192.168.43.1 icmp_seq=4 Destination Net Unreachable (这个没异常信息)
				s.close(); 
				if (isJoyDebug)	Log.v("Send udp resp message", "Send udp resp message--回复结束了");
				
				
		}
	}

	public class ScoketServerS extends Thread {
		public void run() {
			try {
				server = new ServerSocket();
				server.setReuseAddress(true);// mark ，默认是false改成true
				server.bind(new InetSocketAddress(PORT));
				mExecutorService = Executors.newCachedThreadPool();// 构建线程池
				System.out.println("Socket Server start ok...");
				Socket client = null;
				while (true) {
						if (isClientOn) {// 有客户端在线暂停7秒
							Log.e("TCPUDPServer", "已经有客户端在线,不会再接受任何连接，我是服务器。。程总为我带盐！");
							Thread.sleep(100);
							continue;
						}
						client = server.accept();
						isClientOn = true;
						Log.e("ScoketServerS", "客户端上线----by TCPServer改socket："+client.getLocalAddress());
						Log.v("ScoketServerS", "客户端信息：" + client.toString());
						mList.add(client);
						Log.v("ScoketServerS", "目前客户端数：" + mList.size());
						mExecutorService.execute(new Service(client));// 读写操作也是阻塞的，在另外的线程中执行
//						Thread.sleep(100);//mark
				}
			} catch (Exception ex) {
				Log.i("ScoketServerS", "端口已被绑定：in tcpudpserver");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 运行在线程池中的 Runnable实现类，每一个连接上来的客户端独立运行在分配的各自线程中
	 * 
	 * @author joychine
	 */
	public static int count = 0;
	public class Service implements Runnable {
		private Socket socket;
		private InputStream in = null;
		private OutputStream out = null;

		// add by betters 2013-10-13
		private final static int MAX_DATA_BUFFER_LENGTH = 65535;
		private final static int DATA_BUFFER_LENGTH = 65535;

		private byte[] reciveByte = new byte[65535]; //mark

		/**
		 * 缓存接收到的数据
		 */
		private byte[] ReceiveDateBuffer = new byte[MAX_DATA_BUFFER_LENGTH];

		/*
		 * private ResposHandler responseHandler = new ResposHandler(); private
		 * StopHandler stopHandler = new StopHandler(); private TransHandler
		 * transHandler = new TransHandler(); private int iHaveReciveLength = 0;
		 * private int iTotalLength = 0;
		 */

		Context mainHDloadCtx;
		private int COM_DATA_LENGTH = 28;
		private int NET_DATA_LENGTH = 37;

		private byte[] data = new byte[28];
		private byte[] netdata = new byte[37];
		private Message message;// = new Message();
		private Bundle bundle;// = new Bundle();
		byte[] buffer = new byte[DATA_BUFFER_LENGTH];

		public Service(Socket socket) throws IOException {
			this.socket = socket;
			try {
				// 设置Socket读取超时时间，设置成28800000毫秒==8个小时
				 this.socket.setSoTimeout(20000);
				in = socket.getInputStream();
				out = socket.getOutputStream();
				// Log.e("dfdfdfdfdff", "tcpDelay?::"+socket.getTcpNoDelay());
				// socket.setTcpNoDelay(true);
				// Log.e("dfdfdfdfdff", "tcpDelay?::"+socket.getTcpNoDelay());
				// this.sendmsg();
			} catch (IOException e) {
				mList.remove(socket); // 掉线就remove
				this.socket.close();
				isClientOn = false;
				Log.e("TCPUDPserver", "数据读取超时,远程Socket管道已经关闭");
				e.printStackTrace();
			}
		}

		//useless
		public void sendPackData(OutputStream out, byte[] outputdata,
				byte dataLen, byte packType, byte packFun) {
			byte[] bsendMessage = new byte[9 + dataLen];
			int packSendLen = 9 + dataLen;
			int checkSum = 0, copyDataLen;
			bsendMessage[0] = (byte) 0xaa;
			bsendMessage[1] = (byte) 0x55;
			bsendMessage[2] = (byte) ((packSendLen >> 24) & 0xff);
			bsendMessage[3] = (byte) ((packSendLen >> 16) & 0xff);
			bsendMessage[2] = (byte) ((packSendLen >> 8) & 0xff);
			bsendMessage[3] = (byte) (packSendLen & 0xff);
			bsendMessage[6] = packType;
			bsendMessage[7] = packFun;

			for (copyDataLen = 0; copyDataLen < dataLen; copyDataLen++) {
				bsendMessage[8 + copyDataLen] = outputdata[copyDataLen];
			}
			for (copyDataLen = 0; copyDataLen < dataLen + 8; copyDataLen++)
				checkSum += bsendMessage[copyDataLen];
			bsendMessage[copyDataLen] = (byte) (checkSum);

			// inReader.close();
			try {
				out.write(bsendMessage, 0, packSendLen);
			} catch (Exception ex) {
				System.out.println("server Socket 回复异常");
				ex.printStackTrace();
			}
		}

		public void run() {
			Thread.currentThread().setName("你妹妹的tcpthread");
			if (!mList.contains(this.socket)) {
				isClientOn = false; //2014年1月18日 09:48:17 mark add
				return;
			}
			try {
				ResposHandler responseHandler = new ResposHandler();
				StopHandler stopHandler = new StopHandler();
				TransHandler transHandler = new TransHandler();
				int iHaveReciveLength = 0;
				int iMsgLength = 0;
				byte[] sendBytes = new byte[10]; // 三个处理下载数据类的操作，应答消息都是长度为10的字节数组
				int iFilesNum = -1;
				while (true) {
					int readLen = 0;
					byte[] tempNetData = new byte[37];
					int tempIndex = -1; // 目前缓存数组的已经存入数据的 下标
					InitBuffer(reciveByte);  //mark your advance speed
					count++;
		Log.e("TCPUDPServer", "傻叉哦"+count);
					if ((readLen = in.read(reciveByte)) > 7) // 这里读 偶尔会// Connection reset by peer错误				
					{
		Log.v("joybug", "读到长度：" + readLen);
	PrintBuffer(reciveByte, 58);
						// if((ReciveByte[1] == 0x55)&&(ReciveByte[0] ==
						// (byte)0xaa)&&(ReciveByte[2] ==
						// (byte)0x00)&&(ReciveByte[3] == (byte)0x00))
						if (((reciveByte[0] == (byte) 0xaa) && reciveByte[1] == 0x55)&& (reciveByte[2] == (byte) 0x00)) {
							int count;
							int data1, data2, data3, data4;
							if (reciveByte[2] < 0) {
								data1 = 256 + reciveByte[2]; 
							} else {
								data1 = reciveByte[2];
							}   

							if (reciveByte[3] < 0) {
								data2 = 256 + reciveByte[3];
							} else {
								data2 = reciveByte[3];
							}

							if (reciveByte[4] < 0) {
								data3 = 256 + reciveByte[4];
							} else {
								data3 = reciveByte[4];
							}
							if (reciveByte[5] < 0) {
								data4 = 256 + reciveByte[5];
							} else {
								data4 = reciveByte[5];
							}
							count = (int) (data1 << 24) | (data2 << 16)| (data3 << 8) | (data4);
							// 0x02,0x01串口数据下发, 0x02,0x03是结束的标识
							// byte[] tempNetData = new byte[37];
							// int tempIndex = -1; //目前缓存数组的已经存入数据的 下标
							// boolean isNeedDelMark = false; //是否需要删除标志位
							if (reciveByte[6] == 0x02) {
								if (reciveByte[7] == 0x03) {
									break;
								}
								if (readLen == 37 && tempIndex == -1) {
									Log.e("我记得我来个", "readLen == 37 && tempIndex == -1");
									// 发送到 串口去,
									if (reciveByte[6] == 0x02&& reciveByte[7] == 0x01) {
										// 2.2.1 串口通信下行
										byte[] comData = new byte[28];
										for (int i = 0; i < 28; i++) {
											comData[i] = reciveByte[i + 8];
										}
										pSerialPort.setNetSendData(comData, 28);
										tempNetData = null;
										tempNetData = new byte[37];
										
									}
								} else if (readLen < 37) {
									System.out.println("少于37");
									// 存到一个临时数组
									if (tempIndex == -1) {
										for (int i = 0; i < readLen; i++) {
											tempNetData[i] = reciveByte[i];
											tempIndex++;
										}
									} else {
										for (int i = 0; i < readLen; i++) {
											if (tempIndex + 1 < 37) {
												tempNetData[tempIndex + 1] = reciveByte[i];
												tempIndex++;
												if (tempIndex + 1 == 37) {
													// 已满 36个元素，写入到串口
													byte[] comData = new byte[28];
													for (int j = 0; i < 28; j++) {
														comData[j] = tempNetData[j + 8];
													}
													pSerialPort.setNetSendData(
															comData, 28);
													tempNetData = null;
													tempNetData = new byte[37];
													tempIndex = -1;
												}
											}
										}

									}
								}else {
									System.out.println("多余37个");
									// 一次性读取超过了37个，读到下一个去了，截取前面37个，36个有用1个标志无视，然后把剩余的存到临时数组
									if (tempIndex == -1) {
										byte[] comData = new byte[28];
										for (int i = 0; i < 28; i++) {
											comData[i] = reciveByte[i + 8];
										}
										pSerialPort.setNetSendData(comData, 28);
										tempNetData = null;
										tempNetData = new byte[37];

										// 把多余的缓存起来
										int countNum = -1;
										for (int i = 0; i < readLen - 37; i++) {
											countNum++;
											if (countNum == 36) {
												// 发送数据到串口
												tempNetData[countNum] = reciveByte[37 + i];
												byte[] comData1 = new byte[28];
												for (int j = 0; j < 28; j++) {
													comData1[j] = tempNetData[j + 8];
												}
												pSerialPort.setNetSendData(
														comData1, 28);
												tempNetData = null;
												tempNetData = new byte[37];
												tempIndex = -1;
												countNum = -1;
												continue;
											}
											tempNetData[countNum] = reciveByte[37 + i];
											tempIndex++;
										}
									} else {
										for (int i = 0; i < readLen; i++) {
											if (tempIndex + 1 < 37) {
												tempNetData[tempIndex + 1] = reciveByte[i];
												tempIndex++;
												if (tempIndex + 1 == 37) {
													// 已满 37个元素，写入到串口
													byte[] comData = new byte[28];
													for (int j = 0; j < 28; j++) {
														comData[j] = tempNetData[j + 8];
													}
													pSerialPort.setNetSendData(
															comData, 28);
													tempNetData = null;
													tempNetData = new byte[37];
													tempIndex = -1;
												}
											}
										}
									}
									//
									Log.e("over", "多余37个的发完了");
								}
								Log.e("trueover", "真的发完了");
								//在判断最后一次的数据是否连包了。
								if (tempNetData[6]==0x02&&tempNetData[7]==0x03) {
									Log.e("最后都有联保，我靠", "粘包。。。");
									break;
								}
							}
							// 0x04 ,0x01 写入配置,改别名和瓶体宽度高度设置
							if (reciveByte[6] == 0x04&&reciveByte[7]==0x01) {
								StringBuilder nick_name= new StringBuilder();
								for (int i = 0; i < 16; i++) {
									String hex = Integer.toHexString(reciveByte[8+i] & 0xFF);
									if (hex.length() == 1) {
										hex = '0' + hex;
									}
									nick_name.append(hex);
								}
Log.e("TCPUDPServer", "收到数据别名："+nick_name);								
								SharedPreferences sPreferences = mContext.getSharedPreferences("device_config", Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sPreferences.edit();
								editor.putString("nick_name", nick_name.toString());
								editor.commit();
								//写入瓶体宽度高度
								StringBuilder sbBuilder = new StringBuilder();
								for (int i = 0; i < 4; i++) {
									String hex = Integer.toHexString(reciveByte[24+i] & 0xFF);
									if (hex.length() == 1) {
										hex = '0' + hex;
									}
									sbBuilder.append(hex);
								}
	Log.e("TCPUDPServer", "收到数瓶体宽度高度："+sbBuilder);								
								editor.putString("device_width_height", sbBuilder.toString());
								editor.commit();
								//回复上位机
								out.write(createRespNickNamePack((byte)0x04, (byte)0x02));
								out.flush();
								break;
							}
							
							//文件传输，discarded
							if (reciveByte[6] == 0x03) {
								int iMSGType = MessageHandler.GetMsgType(reciveByte);
								boolean isHeader = false;
								isHeader = MessageHandler.isHeader(reciveByte);

								/*
								 * if(isHeader)
								 * System.out.println("isHeader : true"); else
								 * System.out.println("isHeader : false");
								 */

								// 0x03,0x01代表 2.3.1 下载数据请求消息
								if ((1 == iMSGType) && (isHeader)) {
									iFilesNum = responseHandler
											.getFileNum(reciveByte);
									System.out.println("下载数据请求--lenght : "
											+ readLen + " iMSGType : "
											+ iMSGType + " iFilesNum : "
											+ iFilesNum);
									// responseHandler.handler(ReciveByte);
									InitBuffer(sendBytes);
									responseHandler.handlerback(sendBytes);
									// 回复上位机，当前是否允许下载错 误标志 0：允许下载 -1：系统忙
									try {
										out.write(sendBytes, 0, 10);
										out.flush();
										System.out.println("下载数据请求回复上位机");
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								// 0x03,0x03 下载数据传送消息
								else if ((3 == iMSGType) && (isHeader)) {
									Log.e("TCPUDPServer",
											"---------------------------begin---------------------");
									iMsgLength = transHandler
											.GetInforTotalLength(reciveByte);
									iHaveReciveLength = readLen;
									System.out
											.println("下载传输数据-------readLen : "
													+ readLen + "iMSGType : "
													+ iMSGType
													+ " iMsgLength :"
													+ iMsgLength);
									if ((iMsgLength == readLen)
											|| (iMsgLength == (readLen + 1)))
									// if(iTotalLength == (len+1))
									{
										transHandler.handler(reciveByte);
										InitBuffer(sendBytes);
										transHandler.handlerback(sendBytes);
										System.out
												.println("0x03,0x03------readLen : "
														+ readLen
														+ " iMSGType : "
														+ iMSGType
														+ " HandBack");
										// 我把下面注释掉了， 注释掉了，注意啊~~~mark
										try {
											out.write(sendBytes, 0, 10);
											out.flush();
											System.out.println("回复上位机，数据传输回复");
										} catch (IOException e) {
											e.printStackTrace();
										}

										if (transHandler.isFileFinished())
											iFilesNum--;

										if (iFilesNum == 0) {
											// 删除之前的loopprogram.xml
											HandleXMLFiles.deleteloopprogramFile();
											// 修改名字
											HandleXMLFiles.renameProgramListXml(1);
											Thread.sleep(100);
											startPlay(1); // 播放
											Thread.sleep(100);
											HandleXMLFiles.deleteExcessFiles();// mark
																				// 删除其他不需要的文件
											iFilesNum = -1;
											break;
										}
									} else {
										InitBuffer(ReceiveDateBuffer);
										for (int i = 0; i < readLen; i++) {
											ReceiveDateBuffer[i] = reciveByte[i];
										}
									}
									Log.e("TCPUDPServer",
											"----------------------------end-------------------------");
								} else if ((5 == iMSGType) && (isHeader)) {
									stopHandler.handler(reciveByte);
									InitBuffer(sendBytes);
									stopHandler.handlerback(sendBytes);
									try {
										out.write(sendBytes, 0, 10);
										out.flush();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}

							// 2.1 查询控制器命令
						 if (reciveByte[6] == 0x01) {
								in.read(reciveByte, 0, count - 8);
								createSendPack(reciveByte, data, 26, (byte) 1,(byte) 2);
								out.write(reciveByte);
								out.flush();
								break;
						  }else
						 //3.时间同步 0x06,0x01
						 if (reciveByte[6]==0x06&&reciveByte[7]==0x01) {
	 Log.e("信息", "收到0601");
							int year_1 = reciveByte[8];
							int year_2 = reciveByte[9];
							int year = (year_1&0xff)+(year_2<<8);
Log.e("fdfdfq", "算出的年份："+year);							
							if (year==0) {//上位机要求获取我的当前时间
Log.e("TCPUDPServer", "years是0");								
								out.write(createTimeSyncPackage(reciveByte,0));
								out.flush();
Log.e("dfdfd", "时间已经回复了！！！");								
								break;
							}
							int month = reciveByte[10]-1;
							int day = reciveByte[11];
							int hourOfDay = reciveByte[12];
							int minute = reciveByte[13];
							int second = reciveByte[14];
Log.e("TCPUDPServer", "时间是："+year+"/"+month+"/"+day+" "+hourOfDay+":"+minute+":"+second);
							Calendar calendar = Calendar.getInstance();
							calendar.set(year, month, day, hourOfDay, minute, second);
							boolean isSetTime = SetTime.timeSynchronization((calendar.getTimeInMillis()));
	Log.e("TCPUDPServer", "时间同步："+(isSetTime?"成功":"失败"));
							//回复上位机
							out.write(createTimeSyncPackage(reciveByte,1));
							out.flush();
							break;
						 }else
						 //更改当前下位机的 wlan网络模式，热点还是终端
						 if (reciveByte[6]==0x07&&reciveByte[7]==0x01) {
							 APInfo apInfo = new APInfo();
							apInfo.wifi_type = reciveByte[8];
							String ssid = new String(reciveByte, 8, 32,"utf-8").trim();
Log.e("得到的ssid", "长度ssid:"+ssid.length()+"\t 内容："+ssid);
							apInfo.ssid = ssid;
							String psk = new String(reciveByte, 41, 16,"utf-8").trim();
Log.e("得到的psk", "长度psk:"+psk.length()+"\t 内容："+psk);							
							apInfo.psk = psk;
							ApinfoParse apinfoParse = new ApinfoParse(mHandler, mContext);
							apinfoParse.saveAndConfigWlan(apInfo);
							break;
						 }else
						 //读取当前的wlan网络配置信息
						 if (reciveByte[6]==0x07&&reciveByte[7]==0x03) {
							 //回复上位机
							 Log.e("dfdfdf here ", "com here~~~~读取信息-0x07 0x03");
							 responseWlanConfig();
						 }else
						 //设置网络配置参数信息
						 if (reciveByte[6]==0x08&&reciveByte[7]==0x01) {
		if(true) Log.e("配置上网信息", "配置上网信息");
							CommUtils.updateConfig(CommUtils.parseRecive(reciveByte));; 
							Intent i = new Intent("com.led.clientscan.RESTART");
							mContext.sendBroadcast(i);
Log.i("配置信息广播放出状态", "配置3g上网信息已经发出去了。。。。！！！");							
							break;
						 }else
						 //读取网络配置参数信息 config
						 if (reciveByte[6]==0x08&&reciveByte[7]==0x03) {
							 Log.e("响应上位机", "回复下位机的配置信息");
							 responseNetconfig();
						 }else
						 //读取：定时开关瓶
					     if (reciveByte[6]==0x09&&reciveByte[7]==0x01) {
		 Log.e("定时开关瓶", "读取定时开关瓶。。");					    	 
					    	 byte[] autoonoff = mDisplayerBiz.createRespAutoOnOff();
		PrintBuffer(autoonoff, autoonoff.length);		    	 
							out.write(autoonoff);
							out.flush();
					     }
						 //设置:定时开关屏
						 if (reciveByte[6]==0x09&&reciveByte[7]==0x03) {
	 Log.e("定时开关瓶", "设置定时开关瓶。。");									 
							boolean isConfigOk = mDisplayerBiz.configPlanOnOffScrn(reciveByte);
							out.write(mDisplayerBiz.createConfigResult(9, 4, isConfigOk));
							out.flush();
					     }else
						 //读取：定时调节亮度
						 if (reciveByte[6]==10&&reciveByte[7]==0x01) {
	Log.e("亮度调节", "自动亮度读取啊。。");
							byte[] respData = mDisplayerBiz.createRespAutoBright();
	PrintBuffer(respData, respData.length);
							out.write(respData);
							out.flush();
					     }else
						 //设置：定时调节亮度
						 if (reciveByte[6]==10&&reciveByte[7]==0x03) {
Log.e("亮度调节", "自动亮度  设置了啊。。");							 
							boolean isConfig = mDisplayerBiz.configPlanBright(reciveByte);
							out.write(mDisplayerBiz.createConfigResult(10, 4, isConfig));
							out.flush();
					     }else
						 //读取：定时重启
						 if (reciveByte[6]==11&&reciveByte[7]==0x01) {
							out.write(mDisplayerBiz.createRespAutoReboot());
							out.flush();
	Log.e("chongqi", "读取。。重启啊。。。")							 ;
						}else
						 //设置：定时重启
						 if (reciveByte[6]==11&&reciveByte[7]==0x03) {
Log.e("chongqi", "设置。。。重启啊。。。")							 ;
							 boolean isConfig = mDisplayerBiz.configRebootTime(reciveByte);
							 out.write(mDisplayerBiz.createConfigResult(11,4,isConfig));
							 out.flush();
					     }else
					     //立刻重启设备
					     if (reciveByte[6]==12&&reciveByte[7]==0x01) {
//							EthernetSet.writeOsData("reboot -n");
					    	 Intent rebootIntent = new Intent("com.led.player.SERIAL_HARD_REBOOT");
							 mContext.sendBroadcast(rebootIntent);
						 }else
						 //查询当前板卡是否为 局域网同步播放的主机
						 if (reciveByte[6]==15&&reciveByte[7]==0x01) {
							out.write(mDisplayerBiz.createRespLANSyncHostState());
							out.flush();
						 }else
					     //设置当前板卡为局域网同步播放的主机
						 if(reciveByte[6]==15&&reciveByte[7]==0x03){
							 boolean isSet = mDisplayerBiz.setLANHost(reciveByte[8]==1?true:false);
							 out.write(mDisplayerBiz.createRespLANSyncSet(isSet));
							 out.flush();
						 }
						 
						 
						}
						// 我艹，这里
						else {
							// System.out.println("10101010-----------the data length is: "
							// + len);
							Thread.sleep(1);
							// System.out.println("22222222222222-------len : "
							// + len+ "iHaveReciveLength :" +
							// iHaveReciveLength);
							for (int i = iHaveReciveLength; i < iHaveReciveLength
									+ readLen; i++) {
								if (i >= MAX_DATA_BUFFER_LENGTH)
									break;
								ReceiveDateBuffer[i] = reciveByte[i
										- iHaveReciveLength]; // mark
																// 这里喜欢报java.lang.ArrayIndexOutOfBoundsException:
																// length=65535;
																// index=65535
							}
							iHaveReciveLength = iHaveReciveLength + readLen;
							// System.out.println("2-------lenght : " + len+
							// "HandBack");
							if ((iHaveReciveLength == iMsgLength)
									|| (iMsgLength == (iHaveReciveLength + 1)))
							// if(iHaveReciveLength == iTotalLength)
							{
								// System.out.println("---------------handler2------------");
								transHandler.handler(ReceiveDateBuffer);
								// SendByte = new byte[10];
								InitBuffer(sendBytes);
								transHandler.handlerback(sendBytes);
								// ��socket�еõ����������
								// os = socket.getOutputStream();
								out.write(sendBytes, 0, 10);
								out.flush();
								// System.out.println("22-------lenght : " +
								// len+ "HandBack");
								if (transHandler.isFileFinished())
									iFilesNum--;
								if (iFilesNum == 0) {
									// 先修改名字
									HandleXMLFiles.renameProgramListXml(1);
									Thread.sleep(100);
									startPlay(1);
									Thread.sleep(100);
									HandleXMLFiles.deleteExcessFiles();
									iFilesNum = -1;
									break;
								}
							}
						}
					}else {
						Log.e("TCPUDPServer", "else读到长度："+readLen);
						if (readLen==-1) {
							break;
						}
					}
				}

				System.out.println("----------------接收完毕 close  the data---------- ");
				if (isJoyDebug)			Log.v("joybug", "这次连接过后被移除的socket：" + socket);
				out.flush();
				out.close();
				in.close();
				if (isJoyDebug)			Log.v("joybug", "移除前有socket数：" + mList.size());
				mList.remove(socket);
				if (isJoyDebug)		Log.v("joybug", "还有的socket数：" + mList.size());
			} catch (Exception ex) {
				System.out.println("server connect out 读超时或者是客户端那边退出导致 Connection reset 错误");
				mList.remove(socket);
				ex.printStackTrace();
			} finally{
				isClientOn = false;
				if (socket!=null) {
					try {
						out.close();
						in.close();
						socket.close();
						socket = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}


		/**
		 * 回复上位机，当前netclient的配置信息
		 */
		private void responseNetconfig() {
			try {
				byte[] arr = CommUtils.createNetpackage();
//		PrintBuffer(arr, arr.length);
				out.write(arr);
				out.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 回复上位机，当前下位机的wlan配置信息
		 */
		private void responseWlanConfig() {
			final SharedPreferences sPreferences = mContext.getSharedPreferences("wifiLastState", Context.MODE_PRIVATE);
			 String ap_ssid = sPreferences.getString("ap_ssid", null);
			 String ap_password = sPreferences.getString("ap_password", null);
			 String wifi_type_str = sPreferences.getString("wifi_mode", "-1"); //0,1,2
			 int wifi_type = Integer.parseInt(wifi_type_str);
			 //构建一个回复的数据包
			 byte[] backPackage = new byte[58];
			 backPackage[0] = (byte) 0xaa;
			 backPackage[1] = 0x55;
			 backPackage[5] = 58;
			 backPackage[6] = 7;
			 backPackage[7] = 4;
			 backPackage[8] = (byte) wifi_type;
			 try {
				 if (wifi_type==-1) {
					 out.write(backPackage);
					out.flush();
					return;
				 }
				byte[]ssidArr =  ap_ssid.getBytes("utf-8");
				byte[] pskArr = ap_password.getBytes("utf-8");
				for (int i = 0; i < ssidArr.length; i++) {
					backPackage[9+i] = ssidArr[i];
				}
				for (int i = 0; i < pskArr.length; i++) {
					backPackage[41+i] = pskArr[i];
				}
				backPackage[57] = 12; //和 没用到不计算

				out.write(backPackage);
				out.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
		}

		public void PrintBuffer(byte[] buffer, int len) {
			System.out.print("net: ");
			for (int i = 0; i < len; i++) {
				String hex = Integer.toHexString(buffer[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				System.out.print(hex.toUpperCase() + " ");
			}
			System.out.println(" ");
		}

		private void InitBuffer(byte[] buffer) {
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = 0;
			}
		}

		/**
		 * 
		 * @param iPlayType
		 */
		public void startPlay(int iPositionType) {

			System.out
					.println("-----------------------start play!---------------------------------------");
			// Android �ڷ��͹㲥ʱ�ķ��� sendBroadcast��Intent����

			String sPlay = "PLAY_NET_PROGRAM";
			String sPath = "";

			if (1 == iPositionType)// 播放sdcard上的文件
			{
				sPath = "/mnt/sdcard";
			} else {
				sPath = "/mnt/extsd";
			}

			Intent myIntent = new Intent();// ����������Intent����

			myIntent.setAction(sPlay);// String����������һ���Ҫִ�еĶ�����������һ����������ƣ���ACTION_VIEW��Ӧ�ó���ľ����ж���Ӧ�빩Ӧ�̵İ�����Ϊǰ׺����

			myIntent.putExtra("path", sPath);// String,Object�������㲥�ж��ⷢ�͵���ݣ�StringΪ�Զ���key,Object��ʾ����������͡�

			mContext.sendBroadcast(myIntent);// ���������͹㲥��

	System.out.println("start play!");

		}

		public void createSendPack(byte[] sendBuff, byte[] sendData,
				int dataLen, byte sendDataType, byte funcType) {
			int packLen = dataLen + 9;
			int tempCount;
			int checksum = 0;
			sendBuff[0] = (byte) 0xaa;
			sendBuff[1] = (byte) 0x55;
			sendBuff[2] = (byte) ((packLen >> 24) & 0xff);
			sendBuff[3] = (byte) ((packLen >> 16) & 0xff);
			sendBuff[4] = (byte) ((packLen >> 8) & 0xff);
			sendBuff[5] = (byte) ((packLen) & 0xff);
			sendBuff[6] = sendDataType;
			sendBuff[7] = funcType;
			for (tempCount = 0; tempCount < 8; tempCount++)
				checksum += sendBuff[tempCount];
			for (tempCount = 0; tempCount < dataLen; tempCount++) {
				sendBuff[8 + tempCount] = sendData[tempCount];
				checksum += sendBuff[8 + tempCount];
			}
			sendBuff[8 + tempCount] = (byte) (checksum & 0xff);
		}
		
		/*
		 * 该别名。回复消息。 屏体宽度高度等信息
		 */
		public byte[] createRespNickNamePack(byte sendDataType, byte funcType) {
			int packLen =10;
			int checksum = 0;
			byte[] sendBuff = new byte[10];
			sendBuff[0] = (byte) 0xaa;
			sendBuff[1] = (byte) 0x55;
			sendBuff[2] = (byte) ((packLen >> 24) & 0xff);
			sendBuff[3] = (byte) ((packLen >> 16) & 0xff);
			sendBuff[4] = (byte) ((packLen >> 8) & 0xff);
			sendBuff[5] = (byte) ((packLen) & 0xff);
			sendBuff[6] = sendDataType;
			sendBuff[7] = funcType;
			sendBuff[8]  ='c';//消息内容任意
			for (int tempCount = 0; tempCount < 9; tempCount++)
				checksum += sendBuff[tempCount];
			sendBuff[9] = (byte) checksum;
			return sendBuff;
		}

		/**
		 * 暂时不明白其作用
		 */
		public void sendmsg() {
			int num = mList.size();
			for (int i = 0; i < num; i++) {
				Socket mSocket = mList.get(i);
				PrintWriter pout = null;
				try {
					pout = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(mSocket.getOutputStream())),
							true);
				} catch (IOException e) {
					if (isJoyDebug)			Log.v("joybug", "socket离线，在TCPUDPServer907行");
					mList.remove(mSocket);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * udp信息打印
	 * 
	 * @param buffer
	 * @param len
	 */
	public static void PrintBuffer(byte[] buffer, int len) {
		System.out.print("udp: ");
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
	 * 局域网同步轮播，收到方以点播的形式点播指定页面。
	 */
	public void sendLanSyncloop(int demanPage) {
		Intent lanSyncIntent = new Intent("PLAY_NET_PROGRAM");
//		demandIntent.putExtra("demand", demanPage);
		lanSyncIntent.putExtra("lanSyncPageIndex", demanPage);
		lanSyncIntent.putExtra("path", "/mnt/sdcard/program/loopprogram.xml");
Log.i("TCPUDPServer", "这里是TCPUDPServer中。从机播放广播。。。。。----------------------"+System.currentTimeMillis());		
		mContext.sendBroadcast(lanSyncIntent);
	}

	/**
	 * 时间同步，回复上位机，数据长度16个字节
	 * @param reciveByte
	 * @param j 1代表我完成上位机的设置，0代表回复上位机我的时间
	 * @return
	 */
	public byte[] createTimeSyncPackage(byte[] reciveByte, int j) {
		byte[] resp = new byte[16];
		StringBuilder sbBuilder = new StringBuilder();
		if (j==1) {
			for (int i = 0; i < resp.length; i++) {
				resp[i] = reciveByte[i];
				String str = Integer.toHexString(resp[i]& 0xFF);
				if (str.length()==1) {
					str= "0"+str+" ";
				}
				sbBuilder.append(str);
			}
			resp[7] = 0x02; //0x01,0x02 回复上位机
			System.out.println("回复："+sbBuilder.toString());
		}
		if (j==0) {
			Calendar calendar = Calendar.getInstance();
			resp[0]=(byte) 0xaa;
			resp[1]=0x55;
			resp[2] = 0x00;
			resp[3] = 0x00;
			resp[4] = 0x00;
			resp[5] = 0x10;
			resp[6] = 0x06;
			resp[7] = 0x02;
			int year = calendar.get(Calendar.YEAR);
			resp[8] = (byte) (0xff&year);
			resp[9] = (byte) (0xff&(year>>8));
			resp[10] = (byte) (calendar.get(Calendar.MONTH)+1);
			resp[11] = (byte) (calendar.get(Calendar.DAY_OF_MONTH));
			resp[12] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
			resp[13] = (byte) calendar.get(Calendar.MINUTE);
			resp[14] = (byte) calendar.get(Calendar.SECOND);
			byte xiaoyanhe = 0;
			for (int i = 0; i < resp.length-1; i++) {
				xiaoyanhe+=resp[i];
			}
			resp[15] = xiaoyanhe;
		}
		return resp;
	}
}
