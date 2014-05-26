package  com.led.player.service;

public abstract class Pack
{
	/**
	 * 打包 应答消息 包
	 * @param buffer
	 */
	public abstract void  Package(byte[] buffer);
	/**
	 * 计算效验和 
	 * @param buffer
	 */
	public abstract void CalculCRC(byte buffer[]);

}