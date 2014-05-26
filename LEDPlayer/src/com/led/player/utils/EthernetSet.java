package com.led.player.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;

/**
 * 以太网ip设置
 * @author 1231
 */
public class EthernetSet {
	
	public static Process p ;
	public static OutputStream os;
	public static InputStream is;
	public static WatchThread normal_p ;
	public static WatchThread error_p;
	
	public static boolean isJoyDebug = true;
	private static Object lock = new Object();
	/**
	 * 一般我的做法是把上面的代码写到一个监视线程中，清空
	 * @author 1231
	 */
static	class WatchThread extends Thread{
		
		InputStream is;
		String type;
		boolean isGoOn = true;
		public WatchThread(InputStream is,String type) {
			this.is = is;
			this.type = type;
			setName(type);
		} 

		public void run() {
			BufferedReader bis = new BufferedReader(new InputStreamReader(is)); 
			String line = null;
			while (isGoOn) {
//Log.i("打回电话", "这里难道就不来么。。；。。。。；");				
				try {
					while( (line = bis.readLine())!=null){
						//一直读，清空缓存
						if (type.equals("Error")) {
							System.out.println("Error:"+line);
						}else {
							System.out.println("Output:"+line);
						}
					}
					Thread.sleep(3000);
					System.out.println("休息一会儿。。。。");
				} catch (Exception e) {
					e.printStackTrace(); 
					Log.i("读取缓存", "读缓存出错了");
				}
			}
		} 
		
//		/** 执行 shell 命令之后返回 String 类型的结果 */
//		public static String execShellStr(String cmd){
//		    String[] cmdStrings = new String[] {"sh", "-c", cmd};
//		    String retString = "";
//
//		    try
//		    {
//		        Process process = Runtime.getRuntime().exec(cmdStrings);
//		        BufferedReader stdout = new BufferedReader(new InputStreamReader( process.getInputStream()), 7777);
//		        BufferedReader stderr = new BufferedReader(new InputStreamReader( process.getErrorStream()), 7777);
//
//		        String line = null;
//
//		        while ((null != (line = stdout.readLine()))|| (null != (line = stderr.readLine())))
//		        {
//			        if (line.trim().length()>0) {
//			            retString += line + "\n";
//			        }
//		        }
//
//		    }
//		    catch (Exception e)
//		    {
//		        e.printStackTrace();
//		    }
//
//		    return retString;
//		}
		
		
	}
	
public static int count = 0;
public static long lastTime = System.currentTimeMillis();
	/**
	 * 
	 * @param senderIP 发送者的ip
	 * @param netmask	发送者的掩码
	 * @return	设置安卓的以太网ip是否成功
	 */
	public static synchronized boolean setEthIP(String senderIP, String netmask)   {
		boolean isSet = true;
		synchronized (lock) {
				try {
					count++;
		System.out.println("这是第："+count+"次了");
		long timeCha = System.currentTimeMillis()-lastTime;
//		if (timeCha<1000) {
//			
//			return false;
//		}
		System.out.println("两次操作时差:"+(System.currentTimeMillis()-lastTime));
		lastTime = System.currentTimeMillis();
					if (isJoyDebug)Log.e("请求本地进程前", "请求本地进程前")	;
					String setIp = "ifconfig eth0 "+calcMyIp(senderIP)+" netmask "+netmask+"\n";
//					p = new ProcessBuilder().command("ifconfig","eth0 "+calcMyIp(senderIP),"netmask "+netmask).redirectErrorStream(true).start();
//					p = new ProcessBuilder().command(new String[]{"su"}).redirectErrorStream(true).start();
//					p = Runtime.getRuntime().exec("su\n"+setIp);
					ensureSU() ;
					
//					Thread.sleep(2000);
					if (isJoyDebug)Log.e("请求本地进程后", "求情本地进程后");
					os.write("\n".getBytes("utf-8"));
//					byte[] setByte = ("ifconfig eth0 169.254.35.103 netmask 255.255.0.0").getBytes("utf-8");
					byte[] setByte = setIp.getBytes("utf-8");
		//Log.e("joybug", "命令："+new String(setByte, 0, setByte.length, "utf-8"));			
					os.write(setByte);
					os.write("\n".getBytes("utf-8"));
//					dos.write("exit\n".getBytes("utf-8"));  //这个+p.destory()会设置ip不成功，这个+p.waitFor()就ok，没这个会卡在p.waitFor()
					os.flush();
					
//					int result = p.waitFor();
//					p.destroy();
//					if (result==0) {
////						正常终止
//						if (isJoyDebug)Log.e("joybug", "进程退出了");	
//						isSet = true;
//					}else {
//						if (isJoyDebug)Log.e("jybug", "进程退出异常");
//						isSet = false;
//					}
//					normal_t.isGoOn=false;
//					error_t.isGoOn=false;
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
//					try{   难怪我的error没信息，这里关闭了哦。
//						if (os!=null) {
//							os.close();
//						}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//					p.destroy();//9240692261
				}
				
//				Process process = new ProcessBuilder()
//			       .command("/system/bin/ping", "android.com")
//			       .redirectErrorStream(true)
//			       .start();
//			   try {
//			     InputStream in = process.getInputStream();
//			     OutputStream out = process.getOutputStream();
		//
//			     readStream(in);
		//
//			   } finally {
//			     process.destroy();
//			   }
//			 }
		}
		
		
		return isSet;
	}

	
	protected static void readStream(final InputStream in) throws IOException {
		new Thread(){
			@Override
			public void run() {
				BufferedReader br;
				try {
//					br = new BufferedReader(new InputStreamReader(in, "utf-8"));
//					String msg = null;
					byte[] tem = new byte[1024];
					int leng=  -1;
					while ((leng=in.read(tem))>-1) {
						Log.e("读取缓存", "读取缓存:"+new String(tem,0,leng));  //自打 有 os.write(exit)这里就没进来过
						System.out.println(new String(tem, 0, leng));
					}
		Log.i("最后一次的长度是", "最后的长度："+leng)			;
//					while ((msg=br.readLine())!=null) {
//						System.out.println(msg);
//					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("读取缓存", "读取缓存出错了"); //没有过。
				} finally{
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		
	}

	/**
	 * 根据广播发送者的ip设置我的ip
	 * @param senderIP 发送者的ip
	 * @return
	 */
	private static String calcMyIp(String senderIP) {
		// TODO Auto-generated method stub
		int index = senderIP.lastIndexOf(".");
		String myIPPrefix = senderIP.substring(0, index);
		//得到发送者ip的主机位
		int host = Integer.parseInt(senderIP.substring(index+1, senderIP.length()));
		int result = host-1;
		if (result<=1) {
			result= host+1;
		}
		if (isJoyDebug)Log.e("EthernetSet", "得到的要设置的ip:"+(myIPPrefix+"."+result));		
		return myIPPrefix+"."+result;
	}
	/**
	 * 把以太网卡设置成空ip
	 */
	public synchronized static boolean setNullIP(){
		boolean isSet = true;
		synchronized (lock) {
			try {
				ensureSU() ;
				os.write("ifconfig eth0 0\n".getBytes("utf-8"));
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("以太网ip置为空", "制空出错了.....");
			}
				
		}
		return isSet;
	}
	
	
	public synchronized static void ensureSU() throws IOException{
		if (p==null) {
			if (p==null) {
				p = Runtime.getRuntime().exec("su"); //一定需要root权限……唉， 每次都要卡这里，看来是死锁啊，前面还没退你就想进来。  还是可能会卡哪怕第一次。。
			}
			if (normal_p==null) {
				normal_p = new WatchThread(p.getInputStream(), "Output");
				normal_p.start();
			}
			if (error_p==null) {
				error_p = new WatchThread(p.getErrorStream(), "Error");
				error_p.start();
			}
			if (os==null) {
				os= p.getOutputStream();
			}
			os.write("\n".getBytes("utf-8"));
		}
	}

	public synchronized static boolean writeOsData(String... cmd) {
		boolean isWrited = false;
		try {
			ensureSU();
			if (cmd!=null&&cmd.length>0) {
				for (int i = 0; i < cmd.length; i++) {
					os.write(cmd[i].getBytes("utf-8"));
					os.write("\n".getBytes("utf-8"));
					os.flush();
				}
			}
			
			isWrited = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isWrited;
	}
	
}
