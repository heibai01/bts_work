package  com.led.player.service;
/**
 * 抽象类 初始化一个应答数据包和
 * 实现2个静态方法 GetMsgType(byte[] buffer)方法和isHeader（byte[] buffer）方法
 * @author 1231
 *
 */
public abstract  class MessageHandler
{
	/**
	 * 没用的汗水。。。
	 * @param receivebuff
	 * @return
	 */
	public abstract int  handler(byte receivebuff[]);

	/**
	 * 作用：实例化sendbuff数组
	 * @param sendbuff
	 */
	public abstract void handlerback(byte sendbuff[]);
	
	/**
	 * 获取消息类型，就是获取第 8个字节的值
	 * @param buffer
	 * @return
	 */
	   public static int GetMsgType(byte[] buffer)
	   {
		
		   if(buffer.length < 8)
			   return 0;
		   
		   int iMsgType = (int)buffer[7];
		   
		   return iMsgType;
		   
	   }
	   
	   public static boolean isHeader(byte[] buffer)
	   {
		   
		   /*for (int i = 0; i <20; i++)
			{ 
				String hex = Integer.toHexString(buffer[i] & 0xFF); 
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex; 
				} 
				System.out.print(hex.toUpperCase()  + " "); 
			}
			System.out.println(" ");
			*/
		   
		   if(buffer.length < 8)
			   return false;
		   int iFrist =  (int)buffer[0];
		   int iSecond = (int)buffer[1];
		   int iThree= (int)buffer[2];
		   int iFourth = (int)buffer[3];
		   int seven = (int)buffer[6];
		   int eight = (int)buffer[7];
		   if((iFrist== -86)&&(iSecond == 85)&&(seven == 3)&&((eight == 3)||(eight == 1)||(eight == 5)) &&(iThree == 0)&&(iFourth == 0))
			   return true;
		   
		   return false;
		   
	   }
}