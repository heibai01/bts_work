//*******************************************************
// 
// 类型定义
// 2014/11/22
// chenjt
// Copyright(C) 深圳佰特思科技有限公司 2014-2113
//
//*******************************************************

#ifndef __UTILITY_H
#define __UTILITY_H

#include <stdio.h>
#include <string.h>
#include "malloc.h"

#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define MIN(a, b) ((a) > (b) ? (b) : (a))

#define SWAP16(data) (((data) & 0XFF) << 8 | ((data) & 0XFF00) >> 8)		// <高低字节交换>
#define SWAP32(data) (((data) & 0X000000FF) << 24 \
											| ((data) & 0X0000FF00) << 8 \
											| ((data) & 0X00FF0000) >> 8 \
											| ((data) & 0XFF000000) >> 24)

u8 Util_Chr2Hex(char chr);	// <char 转 hex>
char Util_Hex2Chr(u8 hex);	// <hex 转 char>
u8 Util_Chr2Int(char chr); // <char 转 int>
u32 Util_Str2Int(const char *str); // <字符串 转 int>
u32 Util_Str2Int2(const char *str, u32 u32Begin, u32 u32End); // <字符串 转 int>

void Util_Unicode2GBK(u8 *src, u8 *dst); // <unicode 转 gbk>
void Util_GBK2Unicode(u8 *src, u8 *dst); // <gbk 转 unicode>

int32_t Util_StrFind(const char *src, const char *dst, u16 u16Times); 					// <查找字符串>
u32 		Util_StrSub(const char *pSrc, char *pDest, u32 n32Begin, u32 n32End); // <获取子字符串>


void Util_PrintChr(u8* pData, u16 u16Len); // <打出字符值>
void Util_PrintHex(u8* pData, u16 u16Len); // <打出16进制值>
/*
	将字符串切割存放进数组中
	buff 		数组容器
	strSrc 		源字符串
	splitStr 	切割字符串
	num 		要存放的个数
*/
void Util_Str2Buff(int *buff, char *strSrc, char *splitStr, u16 num);


#endif
