package com.led.player.service;

/**
方向：上行
说明：控制器发送给后台的是否允许下载数据的应答消息
格式： 
标志位	消息总长度	消息类型	功能码	消息内容	校验和
AA 55	10          	3	 2	       错误标志	
2B	    4B	           1B   1B	     1B	     1B

 * @author DELL
 *
 */
public class ResposHandler extends MessageHandler
{
	@Override
	public int handler(byte[] receivebuff) 
	{
		// TODO Auto-generated method stub
		
		
		return 0;
	}

	
	/**
	 * 获取 文件个数 从传进来的字节数组中，读取数组的 第9个和第10个字节
	 * @param receivebuff
	 * @return
	 */
	public int getFileNum(byte[] receivebuff)
	{
		   byte[] a = new byte[2];

		    for (int i = 0; i < a.length ;i++) 
			{              
			    	a[i] = receivebuff[i+8];
			}
		    
			/*for (int s = 0;s < a.length; s++)
			{ 
				String hex = Integer.toHexString(a[s] & 0xFF); 
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex; 
				} 
				System.out.print(hex.toUpperCase()  + " 0x"); 
			} */
			       
			    int v2 = (int)((a[0] & 0xff) * (int)Math.pow(2,8));
			    int v3 = (int)((a[1] & 0xff));


			    return v2 + v3;
	}
	
	@Override
	public  void handlerback(byte[] sendbuff) {
		
		PackRespos packRes = new PackRespos();
		packRes.Package(sendbuff);
		
		
	}
	
	
}