package  com.led.player.service;

/**
2.3.6 停止下载应答消息 
 */
public class PackStop extends Pack
{
	private byte byteCRC = 0x00;
	
	/**
	 * 打包初始一个 停止下载应答消息包 ，0x03,0x06
	 */
	@Override
	public void Package(byte buffer[]) {
		buffer[0] = (byte) 0xaa;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 0x00;
		buffer[3] = (byte) 0x00;
		buffer[4] = (byte) 0x00;
		buffer[5] = (byte) 0x0a;
		buffer[6] = (byte) 0x03;
		buffer[7] = (byte) 0x06;
		buffer[8] = (byte) 0x00;			
		CalculCRC(buffer);
		buffer[9] = byteCRC;
	}

	@Override
	public void CalculCRC(byte []buffer) {
		byteCRC = 0x00;
		for(int i = 0 ; i < buffer.length-1;i++)
		{
			byteCRC +=  buffer[i];
		}
	}
	
}