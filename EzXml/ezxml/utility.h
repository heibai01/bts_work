//*******************************************************
// 
// ���Ͷ���
// 2014/11/22
// chenjt
// Copyright(C) ���ڰ���˼�Ƽ����޹�˾ 2014-2113
//
//*******************************************************

#ifndef __UTILITY_H
#define __UTILITY_H

#include <stdio.h>
#include <string.h>
#include "malloc.h"

#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define MIN(a, b) ((a) > (b) ? (b) : (a))

#define SWAP16(data) (((data) & 0XFF) << 8 | ((data) & 0XFF00) >> 8)		// <�ߵ��ֽڽ���>
#define SWAP32(data) (((data) & 0X000000FF) << 24 \
											| ((data) & 0X0000FF00) << 8 \
											| ((data) & 0X00FF0000) >> 8 \
											| ((data) & 0XFF000000) >> 24)

u8 Util_Chr2Hex(char chr);	// <char ת hex>
char Util_Hex2Chr(u8 hex);	// <hex ת char>
u8 Util_Chr2Int(char chr); // <char ת int>
u32 Util_Str2Int(const char *str); // <�ַ��� ת int>
u32 Util_Str2Int2(const char *str, u32 u32Begin, u32 u32End); // <�ַ��� ת int>

void Util_Unicode2GBK(u8 *src, u8 *dst); // <unicode ת gbk>
void Util_GBK2Unicode(u8 *src, u8 *dst); // <gbk ת unicode>

int32_t Util_StrFind(const char *src, const char *dst, u16 u16Times); 					// <�����ַ���>
u32 		Util_StrSub(const char *pSrc, char *pDest, u32 n32Begin, u32 n32End); // <��ȡ���ַ���>


void Util_PrintChr(u8* pData, u16 u16Len); // <����ַ�ֵ>
void Util_PrintHex(u8* pData, u16 u16Len); // <���16����ֵ>
/*
	���ַ����и��Ž�������
	buff 		��������
	strSrc 		Դ�ַ���
	splitStr 	�и��ַ���
	num 		Ҫ��ŵĸ���
*/
void Util_Str2Buff(int *buff, char *strSrc, char *splitStr, u16 num);


#endif
