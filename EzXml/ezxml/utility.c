#include <stdio.h>
#include "utility.h"

// ��1���ַ�ת��Ϊ16��������
// chr:�ַ�,0~9/A~F/a~F
// ����ֵ:chr��Ӧ��16������ֵ
u8 Util_Chr2Hex(char chr)
{
	if(chr>='0'&&chr<='9')return chr-'0';
	if(chr>='A'&&chr<='F')return (chr-'A'+10);
	if(chr>='a'&&chr<='f')return (chr-'a'+10); 
	return 0;
}

// ��1��16��������ת��Ϊ�ַ�
// hex:16��������,0~15;
// ����ֵ:�ַ�
char Util_Hex2Chr(u8 hex)
{
	if(hex<=9)return hex+'0';
	if(hex>=10&&hex<=15)return (hex-10+'A'); 
	return '0';
}

// <char ת int>
u8 Util_Chr2Int(char chr)
{
	if (chr >= '0' && chr <= '9')
		return chr - '0';
	else 
		return chr;
}

// <�ַ��� ת int>
u32 Util_Str2Int(const char *str)
{
	u32 u32Result = 0;
	u32 u32Len = 0;
	u32 u32Index = 0;
	
	if (NULL != str)
	{
		u32Len = strlen(str);
		for (; u32Index < u32Len; ++u32Index)
		{
			//u32Result = 10 * u32Result + Util_Chr2Int(str[u32Len - u32Index - 1]);
			u32Result = 10 * u32Result + Util_Chr2Int(str[u32Index]);
		}
	}
	
	return u32Result;	
}

// <�ַ��� ת int>
u32 Util_Str2Int2(const char *str, u32 u32Begin, u32 u32End)
{
	u32 u32Result = 0;
	u32 u32Index = 0;
	
	if (NULL != str && u32Begin < u32End)
	{
		u32Index = u32Begin;
		for (; u32Index < u32End; ++u32Index)
		{
			u32Result = 10 * u32Result + Util_Chr2Int(str[u32Index]);
		}
	}
	
	return u32Result;	
}

void Util_Unicode2GBK(u8 *src, u8 *dst)
{
  OS_CPU_SR cpu_sr = 0;
	u16 temp; 
	u8 buf[2];
	
	OS_ENTER_CRITICAL();//�����ٽ���(�޷����жϴ��)  
	
	while(*src!=0)
	{
		buf[1]=Util_Chr2Hex(*src++)*16;
		buf[1]+=Util_Chr2Hex(*src++);
		buf[0]=Util_Chr2Hex(*src++)*16;
		buf[0]+=Util_Chr2Hex(*src++);
		temp=(u16)ff_convert((WCHAR)*(u16*)buf,0);
		if(temp<0X80){*dst=temp;dst++;}
		else {*(u16*)dst=SWAP16(temp);dst+=2;}
	}		
	*dst = 0;//��ӽ�����
	
	OS_EXIT_CRITICAL();	// �˳��ٽ���(���Ա��жϴ��)	
}

void Util_GBK2Unicode(u8 *src,u8 *dst)
{
  OS_CPU_SR cpu_sr = 0;
	u16 temp; 
	u8 buf[2];
	
	OS_ENTER_CRITICAL();// �����ٽ���(�޷����жϴ��)  

	while(*src!=0)
	{
		if(*src<0X81)	// �Ǻ���
		{
			temp=(u16)ff_convert((WCHAR)*src,1);
			src++;
		}
		else 			// ����,ռ2���ֽ�
		{
			buf[1]=*src++;
			buf[0]=*src++;    
			temp=(u16)ff_convert((WCHAR)*(u16*)buf,1); 
		}
		*dst++=Util_Hex2Chr((temp>>12)&0X0F);
		*dst++=Util_Hex2Chr((temp>>8)&0X0F);
		*dst++=Util_Hex2Chr((temp>>4)&0X0F);
		*dst++=Util_Hex2Chr(temp&0X0F);
	}

	*dst = 0;// ��ӽ�����
	
	OS_EXIT_CRITICAL();	// �˳��ٽ���(���Ա��жϴ��)	
}

int32_t Util_StrFind(const char *src, const char *dst, u16 u16Times)
{
  char *p = (char*)src;
	
	while(NULL != p && u16Times > 0)
	{
		p = strstr(p, dst);
		--u16Times;
	}

	return ((NULL != p && 0 == u16Times) ? (p-src+1): -1);
}

u32 Util_StrSub(const char *pSrc, char *pDest, u32 n32Begin, u32 n32Len)
{
	u32 n32Index = 0;
	u32 n32TotalLen = 0;
	
	if (NULL != pSrc && NULL != pDest)
	{
		n32TotalLen = strlen(pSrc);
		
		if (n32Begin < n32TotalLen)
		{
			n32Len = MIN(n32TotalLen - n32Begin, n32Len);			
			while (n32Index < n32Len)
			{
				pDest[n32Index] = pSrc[n32Begin + n32Index];
				++n32Index;
			}
			pDest[n32Len] = '\0';
		}
		else
		{
			n32TotalLen = 0;
		}
	}	

	return n32TotalLen;
}

// <����ַ�ֵ>
void Util_PrintChr(u8* pData, u16 u16Len)
{
	u16 u16Index = 0;
	OS_CPU_SR cpu_sr = 0;
	OS_ENTER_CRITICAL();	
	
	for (u16Index = 0; u16Index < u16Len; ++u16Index)
	{
		printf("%c", pData[u16Index]);
	}
	
	OS_EXIT_CRITICAL();
}

// <���16����ֵ>
void Util_PrintHex(u8* pData, u16 u16Len)
{
	u16 u16Index = 0;
	OS_CPU_SR cpu_sr = 0;
	OS_ENTER_CRITICAL();	
	
	for (u16Index = 0; u16Index < u16Len; ++u16Index)
	{
		printf("%02x", pData[u16Index]);
	}
	
	OS_EXIT_CRITICAL();
}
//"2578969,8986"���Ÿ���,���ַ���ת��������,�浽buff������
void Util_Str2Buff(int *buff, char *strSrc, char *splitStr, u16 num)
{
	int start = 0, end = 0;
	char *temp = strSrc;
	char temp_buff[32] = {0};//"2578969,8986..."==>>1.[2][5][7][8][9][6][9] 2... 3.[8][9][8][6]...[..](���32��Ԫ��)
	char *preStr = strSrc;
	int nIndex = 0;
	
	do{		
		temp = strstr(preStr, splitStr);		
		end = temp - preStr;
		//snprintf((char *)temp_buff, end, "%s", preStr);
		for (nIndex=0; nIndex < (num!=1?end:sizeof(preStr)); ++ nIndex)
		{
			temp_buff[nIndex] = preStr[nIndex];
		}
		printf("temp_buff = %s, end: %d, preStr: %s\r\n", (char*)temp_buff, end, preStr);
		buff[start]	= Util_Str2Int((const char*)temp_buff);
		printf("buff[%d]=%d\r\n", start, Util_Str2Int((const char*)temp_buff));
		preStr = &temp[1];
		start++;
		memset(temp_buff, 0, sizeof(temp_buff));
	}while(--num);
}

