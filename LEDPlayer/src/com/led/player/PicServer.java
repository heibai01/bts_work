package com.led.player;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter; 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException; 
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter; 
import java.io.PrintWriter; 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket; 
import java.net.Socket; 
import java.net.SocketException;
import java.util.ArrayList; 
import java.util.List; 
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 

import com.led.player.aidance.LedProtocol;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/** 
* com Server 
* PicServer 图片Socket类。
*/ 
public class PicServer
{
	private static final int PORT = 4095;
	private List<Socket> mList = new ArrayList<Socket>();
	private ServerSocket server = null; 
	private ExecutorService mExecutorService = null;
	private Handler sHandler;
	private int jpegFileCount = 0;

	private ScoketServerS pScoketServer;

	private Context mContext;
	private boolean isClientOn = false;
	
	public void PrintBuffer(byte[] buffer,int len)
	{
		
		for (int i = 0; i <len; i++)
		{ 
			String hex = Integer.toHexString(buffer[i] & 0xFF); 
			if (hex.length() == 1) 
			{ 
				hex = '0' + hex; 
			} 
			System.out.print(hex.toUpperCase()  + " "); 
		}
		System.out.println(" ");	
	}
	
	private void  InitBuffer(byte []buffer,int BufferLen)
	{
		int Len = 0;
		if( BufferLen > buffer.length)
			Len = buffer.length;
		else
			Len = BufferLen;
		for(int i = 0 ; i < Len;i++)
		{
			buffer[i] = 0;
		}
	}
	
	public PicServer(Handler handler,Context contxt) {
		this.sHandler = handler;
		this.mContext = contxt;
		pScoketServer = new ScoketServerS();
		pScoketServer.setName("PicServerThread");
		pScoketServer.start();
	} 
	
		public void send(byte[] message,int messLen,InetAddress local,int port) 
		{ 
	        int server_port = port; 
	        DatagramSocket s = null; 
	        try { 
	            s = new DatagramSocket(); 
	        } catch (SocketException e) { 
	            e.printStackTrace(); 
	        } 
	
	        DatagramPacket p = new DatagramPacket(message, messLen, local, server_port); 
	        try { 
	        	Log.v("Send message","send message");
	            s.send(p); 
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	        } 
	        s.close();
	    }



	public class ScoketServerS extends Thread
	{
		private Socket preSocket = null;
		
		public void run()
		{
			 try {
				   server = new ServerSocket(PORT,200); 
				   
				   mExecutorService = Executors.newCachedThreadPool();
				   System.out.println("PicServer Start..."); 
				   Socket client = null; 
				   while (true) {
//					if (isClientOn) {
//						if (client!=null&&mList.size()>1) {
//							mList.remove(preSocket);
//							preSocket.close();
//							preSocket = null;
//						}
//					}
					if (isClientOn) {
						Log.e("PicServer", "当前有Pic 客户端连接着，不再接受..");
						Log.e("joybug", "目前有连接PicServer的socket数："+mList.size());		
						Thread.sleep(100);
						continue;
					}
				    client = server.accept(); 
//				    if (preSocket==null) {
//						preSocket = client;
//					}
//				    client.setSoTimeout(5000);
				    isClientOn = true;
				    Log.e("ScoketServerS","客户端上线②------------by PicServer");
				    mList.add(client); 
Log.v("joybug", "目前有连接PicServer的socket数："+mList.size());				    
				    mExecutorService.execute(new Service(client));
	
				   }
				  } catch (Exception ex) {
					  Log.e("joybug", "绑定失败，端口address already in use");
				     ex.printStackTrace(); 
				  }
		}
	}


	public class Service implements Runnable
	{
	  private Socket socket; 
	  private InputStream in = null; 
	  private OutputStream out = null;
	
		private final int DATA_BUFFER_LENGTH = 65535;
		private	  byte[] ReciveByte = new byte[DATA_BUFFER_LENGTH];
	
	  
//	  private byte[] data = new byte[28];
	  private Message message;// = new Message();
	  private Bundle bundle;// = new Bundle();
	  private int len; 
	  byte[] buffer = new byte[DATA_BUFFER_LENGTH];
  
	  public Service(Socket socket) throws IOException 
	  {
		  this.socket = socket; 
		  try
		  {
			  in = socket.getInputStream();
			  out = socket.getOutputStream();
		  }
		  catch (IOException e) 
		  {
			  mList.remove(socket);
		      socket.close();
		      isClientOn = false;
			  e.printStackTrace(); 
		  } 
	  }
  
	  /**
	   * 往 pc端写入数据？ 回复长度 10个字节
	   * @param out
	   * @param outputdata
	   * @param dataLen
	   * @param packType 0x05
	   * @param packFun	 0x02
	   */
	  public void sendPackData(OutputStream out,byte[] outputdata,byte dataLen,byte packType,byte packFun)
	  {
		  	byte[] bsendMessage = new byte[9+dataLen];
		  	int packSendLen = 9+dataLen;
		  	int checkSum = 0,copyDataLen;
			bsendMessage[0] = (byte)0xaa;
			bsendMessage[1] = (byte)0x55;
			bsendMessage[2] = (byte)((packSendLen>>24)&0xff);
			bsendMessage[3] = (byte)((packSendLen>>16)&0xff);
			bsendMessage[2] = (byte)((packSendLen>>8)&0xff);
			bsendMessage[3] = (byte)(packSendLen&0xff);
			bsendMessage[6] = packType;
			bsendMessage[7] = packFun;
			
			for(copyDataLen = 0;copyDataLen < dataLen;copyDataLen++)
			{
				bsendMessage[8+copyDataLen] = outputdata[copyDataLen];	
			}
			for(copyDataLen = 0;copyDataLen < dataLen+8;copyDataLen++)
				checkSum += bsendMessage[copyDataLen];
			bsendMessage[copyDataLen] = (byte)(checkSum);
	
			try{
				out.write(bsendMessage, 0, packSendLen);
				out.flush();
			}catch (Exception ex) {
			    Log.e("PicServer","上位机早已退出，回复失败。。"); 
			    ex.printStackTrace(); 
			}
	  }
  
	  public void run() 
	  {
		  try 
		  {
Log.e("joybug", "picServer 的run进入。。。");			  
			label:  while (true) 
			  {
//			  InitBuffer(ReciveByte,ReciveByte.length);  //不需要的初始化
				  Log.e("PicServer", "loop~~~~~~");
				 if ((len = in.read(ReciveByte)) > 7)
				  {
//PrintBuffer(ReciveByte, 55);	
Log.e("joybug", "picserver read len: "+len);
					  if((ReciveByte[1] == 0x55)&&(ReciveByte[0] == (byte)0xaa)&&(ReciveByte[2] == (byte)0x00))
					  {
						  int count;//字节总长度计算
						  int data1,data2,data3,data4;
						  if(ReciveByte[2]<0)
						  {
							  data1 = 256 + ReciveByte[2];
						  }
						  else
						  {
							  data1 = ReciveByte[2];
						  }
	    		
						  if(ReciveByte[3]<0)
						  {
							  data2 = 256 + ReciveByte[3];
						  }
						  else
						  {
							  data2 = ReciveByte[3];
						  }
	    		
						  if(ReciveByte[4]<0)
						  {
							  data3 = 256 + ReciveByte[4];
						  }
						  else
						  {
							  data3 = ReciveByte[4];
						  }
						  if(ReciveByte[5]<0)
						  {
							  data4 = 256 + ReciveByte[5];
						  }
						  else
						  {
							  data4 = ReciveByte[5];
						  }
	    		
						  count = (int)(data1<<24) |(data2<<16)|(data3<<8)|(data4);
Log.e("PicServer", "消息总长度："+count);						  
						  
						  //1.指令功能 未知。。。。mark
						  if(ReciveByte[6] == 0x05)
						  {
							  if(ReciveByte[7] == 0x03)
							  {
								  //in.close();
								  message = Message.obtain();
								  message.what = LedProtocol.STATUS_STOP_JPEG_DATA;
								  bundle = new Bundle();
								  bundle.putString("StopDispJpeg", "exit");
								  message.setData(bundle);
				  Log.v("send message","before send bundle-----StopDispJpeg");
								  sHandler.sendMessage(message);
								  break;
							  } 
							  String path = "/data/data/com.led.player/testjpeg"+jpegFileCount+".jpg";
//Log.e("PicServer", "mContext:"+mContext+" fileDir:"+mContext.getFilesDir());							  
//String path_2 = mContext.getFilesDir().getAbsolutePath()+"/nihao.txt"						  ;
//new File(path_2).createNewFile();
							  File jpegfile = new File(path);
							  
							  if(jpegfile.exists())
							  {
								  jpegfile.delete();
							  }
							  jpegfile.createNewFile(); 
							  
							  jpegFileCount ++;
							  if(jpegFileCount >= 10)
								  jpegFileCount = 0;
							  //Log.v("jpeg file","size:"+count);
							  int fileLen = 0,readLen = 0;
							  fileLen = count- 8;
							  FileOutputStream outRef = new FileOutputStream(jpegfile);
							  BufferedOutputStream outBuff=new BufferedOutputStream(outRef);
							  BufferedInputStream bis =new BufferedInputStream(in);
							  
	  System.out.println("RecvLen : " + len + "count num : " + count + " fileLen :  " +fileLen );

	  							int countNum = 0;
							  while(fileLen > 0)
							  {
								  if (countNum>=65535) {
									break label;
								  }
								  countNum++;
								 // System.out.println("readLen readLen 1: " + readLen );
								  //Log.v("Read jpeg file","read file");
//Log.e("PicServer", "文件数据剩余。。。");								  
								  if((readLen = bis.read(ReciveByte)) != -1 )	//连接 ,这里有警告
								  {
//		Log.e("PicServer", "读取图片数据信息");							  
									  //System.out.println("readLen readLen 2: " + readLen );
									  //Log.v("save jpeg file","read file:" + readLen);
									  if(readLen == fileLen)
										  outBuff.write(ReciveByte,0,readLen-1);
									  else
										  outBuff.write(ReciveByte,0,readLen);
									  outBuff.flush();
								  }
								  //System.out.println("readLen readLen 3: " + readLen );
								  fileLen -= readLen;
								  //System.out.println("fileLen readLen 4: " + fileLen );
							  }    		
							  outBuff.flush(); 
							  outBuff.close();
							  outRef.close();
							  byte[] senddata = new byte[2];
							  sendPackData(out,senddata,(byte)1,(byte)5,(byte)2);
							  message = Message.obtain();
							  message.what = LedProtocol.STATUS_RECE_JPEG_DATA;
							  bundle = new Bundle();
							  bundle.putString("SetDispJpeg", path);
							  message.setData(bundle);
							  //Log.v("send message","before send bundle-----");,
							  sHandler.sendMessage(message);
							  //Log.v("send message","send bundle-----");
							 // System.out.println("send jpg!!!");
							  //break;
						  }
					  }
					  
				  }else {
					  //读到的数据小于7 也要打印
					  Log.e("PicServer", "读到长度。。："+len);
					  if (len==-1) {
						break;
					}
//					PrintBuffer(ReciveByte, len);
			    	}
			  }
			  System.out.println("----------------picServer close  the data---------- ");
			  out.flush();
			  out.close();
			  in.close();
		  }
		  catch (Exception ex) 
		  {
			  Log.e("PicServer","异常：pic server connect out 上位机退出了。"); 
			  ex.printStackTrace(); 
		  } finally{
			  isClientOn = false;
			  Log.e("joybug", "移除前PicServer端连上的socket数："+mList.size());
			  mList.remove(socket); 
			  Log.e("joybug", "移除后的PicServer端连上的socket数："+mList.size());
			  if (socket!=null) {
				  try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}
		  }
	  }
  
  
			public void PrintBuffer(byte[] buffer,int len)
			{
				System.out.print("pic: ");
				for (int i = 0; i <len; i++)
				{ 
					String hex = Integer.toHexString(buffer[i] & 0xFF); 
					if (hex.length() == 1) 
					{ 
						hex = '0' + hex; 
					} 
					System.out.print(hex.toUpperCase()  + " "); 
				}
				System.out.println(" ");	
			}
	
	
		  public void createSendPack(byte[] sendBuff,byte[] sendData,int dataLen,byte sendDataType,byte funcType)
		  {
			  int packLen = dataLen+9;
			  int tempCount;
			  int checksum = 0;
			  sendBuff[0] = (byte)0xaa;
			  sendBuff[1] = (byte)0x55;
			  sendBuff[2] = (byte)((packLen>>24)&0xff);
			  sendBuff[3] = (byte)((packLen>>16)&0xff);
			  sendBuff[4] = (byte)((packLen>>8)&0xff);
			  sendBuff[5] = (byte)((packLen)&0xff);
			  sendBuff[6] = sendDataType;
			  sendBuff[7] = funcType;
			  for(tempCount = 0;tempCount< 8; tempCount++)
				  checksum +=  sendBuff[tempCount];
			  for(tempCount = 0;tempCount< dataLen; tempCount++)
			  {
				  sendBuff[8+tempCount] = sendData[tempCount];
				  checksum += sendBuff[8+tempCount];  
			  }
			  sendBuff[8+tempCount] = (byte)(checksum&0xff);
		  }

  
		  public void sendmsg() 
		  {
			  int num = mList.size(); 
			  for (int i = 0; i < num; i++) 
			  { 
				  Socket mSocket = mList.get(i); 
				  PrintWriter pout = null; 
				  try 
				  {
					  pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())),true); 
				  }
				  catch (IOException e) 
				  { 
					  
					  e.printStackTrace(); 
				  } 
			  }	 
		  } 
		  /**
		   * 获得写入权限
		   */
		  public void getAuthority(File file){
				/* Check access permission */
				if (!file.canRead() || !file.canWrite())
				{
							try
							{
							/* Missing read/write permission, trying to chmod the file */
							Process su;
							su = Runtime.getRuntime().exec("/system/xbin/su");
							String cmd = "chmod 777 " + file.getAbsolutePath() + "\n"+ "exit\n";
							su.getOutputStream().write(cmd.getBytes());
								if ((su.waitFor() != 0) || !file.canRead()|| !file.canWrite()) 
								{
									throw new SecurityException();
								}
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
								throw new SecurityException();
							}
				}
		  }
	} 
} 
