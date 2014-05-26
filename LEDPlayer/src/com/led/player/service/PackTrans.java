package  com.led.player.service;

/**
2.3.4 下载数据接收应答消息
 */

public  class PackTrans  extends Pack
{
	private boolean bStop = false; 

	private byte byteCRC = 0x00;
	
	/**
	 * 打包一个下载数据接受包，初始化传进来的数组， 0x03,0x04
	 */
	@Override
	public void Package(byte[]buffer) 
	{
		buffer[0] = (byte) 0xaa;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 0x00;
		buffer[3] = (byte) 0x00;
		buffer[4] = (byte) 0x00;
		buffer[5] = (byte) 0x0a;
		buffer[6] = (byte) 0x03;
		buffer[7] = (byte) 0x04;
		if(bStop)
		{
			buffer[8] = (byte)-1;			
		}
		else
		{
			buffer[8] = (byte)0x00;
		}

		CalculCRC(buffer);
		buffer[9] = byteCRC;
	}

	@Override
	public void CalculCRC(byte[]buffer) {
		byteCRC = 0x00;
		for(int i = 0 ; i < buffer.length-1;i++)
		{
			byteCRC +=  buffer[i];
		}
	}
	
}