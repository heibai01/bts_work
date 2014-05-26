package  com.led.player.service;

/**
 * 回复上位机的 包处理类
 * 2.3.2 下载数据应答消息 长度为10
 * @author 1231
 *
 */
public class PackRespos extends Pack
{
	private boolean bAllow = true; 
	/**
	 * 第10个效验和
	 */
	private byte byteCRC = 0x00;


	@Override
	public void Package(byte buffer[])
	{
		buffer[0] = (byte) 0xaa;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 0x00;
		buffer[3] = (byte) 0x00;
		buffer[4] = (byte) 0x00;
		buffer[5] = (byte) 0x0a;
		buffer[6] = (byte) 0x03;
		buffer[7] = (byte) 0x02;
		if(bAllow)
		{
			buffer[8] = (byte) 0x00;			
		}
		else
		{
			buffer[8] = (byte) -1;
		}

		CalculCRC(buffer);
		buffer[9] = byteCRC;
	}


	@Override
	public void CalculCRC(byte buffer[]) 
	{
		// TODO Auto-generated method stub
		byteCRC = 0x00;
		for(int i = 0 ; i < buffer.length-1;i++)
		{
			byteCRC +=  buffer[i];
		}
		
	}


	public boolean isbAllow() {
		return bAllow;
	}


	public void setbAllow(boolean bAllow) {
		this.bAllow = bAllow;
	}


//	public byte[] getRespos() {
//		return Respos;
//	}
//
//
//	public void setRespos(byte[] respos) {
//		Respos = respos;
//	}
	
	
	
}