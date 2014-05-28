package  com.led.player.service;

/**
 * 2.3.6 停止下载应答消息 0x03,0x06
(1)	方向：控制器 -> PC
(2)	说明： 控制器收到“停止下载消息”时，停止下载工作，并按如下格式返回消息
(3)	格式：
标志位	消息总长度	类别	功能码	消息内容	校验和
AA 55	 10	        3	 6	      0	
2B	     4B	       1B	1B	     1B	     1B

 * 停止 处理，艹，就一个打包的作用有点类似于 ssh中的业务层，调用PackStop这个dao成。实例化了一个module这里就是一个传进来的指定了长度的字节数组
 * @author 1231
 *@since 2013年12月4日 11:12:23
 */
public class StopHandler extends MessageHandler
{


	@Override
	public int handler(byte[] receivebuff) {
		return 0;
	}

	@Override
	public void handlerback(byte[] sendbuff) {
		
		PackStop packStop = new PackStop();
		packStop.Package(sendbuff);
		
	}
	
}