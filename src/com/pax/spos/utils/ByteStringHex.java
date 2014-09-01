package com.pax.spos.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * author: fable tang
 * Comments: byte hex string 转换工具类
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */

public class ByteStringHex {

    static final String HEXES = "0123456789ABCDEF";

    // private static byte uniteBytes(byte src0, byte src1) {
    // byte _b0;
    // _b0 = Byte.valueOf("0x" + src0);
    // _b0 = (byte) (_b0 << 4);
    // byte _b1 = Byte.valueOf("0x" + src1);
    // return (byte) (_b0 ^ _b1);
    // }
    /*
     * Table of CRC values for high-order byte
	 */
    private final static short[] auchCRCHi = {0x00, 0xC1, 0x81, 0x40, 0x01,
            0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
            0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
            0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
            0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
            0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00,
            0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
            0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
            0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
            0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
            0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80,
            0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
            0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
            0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
            0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
            0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40};
    /*
     * Table of CRC values for low-order byte
     */
    private final static short[] auchCRCLo = {0x00, 0xC0, 0xC1, 0x01, 0xC3,
            0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04,
            0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB,
            0x0B, 0xC9, 0x09, 0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB,
            0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14,
            0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
            0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2,
            0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC,
            0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A, 0x3B, 0xFB, 0x39,
            0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA,
            0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25,
            0xE5, 0x27, 0xE7, 0xE6, 0x26, 0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21,
            0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66,
            0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D,
            0xAF, 0x6F, 0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8,
            0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
            0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5, 0x77,
            0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0,
            0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57,
            0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C, 0x5D, 0x9D, 0x5F, 0x9F,
            0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88,
            0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F,
            0x8D, 0x4D, 0x4C, 0x8C, 0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46,
            0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40};

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"–> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        // String sb0=new String(new byte[]{src0})).byteValue();
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，去除空格，以每两个字符分割转换为16进制形式
     * 如："2B 44EFD9" –> byte[]{0x2B, 0×44, 0xEF,0xD9}
     * <p/>
     *
     * @param src 输入字符串
     *            <p/>
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        src = src.replaceAll("\\s", "");
        // src = src.toUpperCase();
        int len = src.length();
        if (len % 2 != 0) {
            // return null;
            src = "0" + src;
            len = len + 1;
        }

        byte[] ret = new byte[len / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 把字节数组转换成16进制字符串,字母为大写
     *
     * @param bytes byte[]
     * @return String 16进制字符串 hexString
     */
    public static String bytes2HexStr(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len < 1) {
            return null;
        }
        StringBuilder sb = new StringBuilder(len);
        String sTemp;
        for (byte aByte : bytes) {
            sTemp = Integer.toHexString(0xFF & aByte);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     *单个字节转为16进制字符串
     *
     * @param bit8
     * @return hexStr
     */
    public static String byte2HexStr(byte bit8) {
        return Integer.toHexString(0xF & bit8).toUpperCase();
    }

    /**
     * 比较1个字符串的 16进制表示的byte, 用于TLV的tag处理 比如 "C"==(byte)(0xC)结果为真
     * 不区分大小写
     * @param hexStr
     * @param bit8
     * @return
     */
    public static boolean hexStrEqualByte(String hexStr,byte bit8){
        if (hexStr==null ||hexStr.length()!=1){
            return false;
        }
        return hexStr.equalsIgnoreCase(byte2HexStr(bit8));
    }
    /**
     * 把int转换成16进制字符串
     *
     * @param i int 待转换的int32
     * @return String 16进制字符串 hexString
     */
    public static String int2HexStr(int i) {
        return Integer.toHexString(i);
    }

    /**
     * int 转换为 byte[4]
     *
     * @param i
     * @return
     */
    public static byte[] int2Bytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 无符号int 转换为 byte[n] n= 1-4
     *
     * @param i int 待转换的int32
     * @return 不定长bytes <=4
     */
    public static byte[] int2BytesN(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        int j = 0;
        for (; j < 4; ) {
            if (result[j] == 0x00) {
                j += 1;
            } else {
                break;
            }
        }
        if (j == 0) {
            return result;
        }
        byte[] dest = new byte[4 - j];
        System.arraycopy(result, j, dest, 0, 4 - j);
        return dest;
    }

    /**
     * bytes 数组转int, 数组由高到低排列，数组不能大于4, 如果小于4, 高位补0x00
     *
     * @param bytes
     * @return int  如果为0, 不一定是正确结果，需要检验bytes
     */
    public static int bytes2Int(byte[] bytes) {
        int len = bytes.length;
        if (len > 4 || len < 1) {
            return 0;
        }
        byte[] bytesTemp = new byte[4];
        if (len < 4) {
            System.arraycopy(bytes, 0, bytesTemp, len - 2, len);
        } else {
            bytesTemp = bytes;
        }
        int mask = 0xff;
        int temp = 0;
        int res = 0;
        try {
            for (int i = 0; i < 4; i++) {
                res <<= 8;
                temp = bytesTemp[i] & mask;
                res |= temp;
            }
            return res;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bytes byte[] start int 开始位 num int 字符个数
     * @return String 16进制字符串 hexString
     */
    public static String bytes2HexStr(byte[] bytes, int start, int num) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(num);
        String sTemp;
        for (int i = start; i < num + start; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 把字节数组转换为对象
     *
     * @param bytes
     * @return Object
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public static final Object bytes2Object(byte[] bytes) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(in);
        Object o = oi.readObject();
        oi.close();
        return o;
    }

    /**
     * 把可序列化对象转换成字节数组
     *
     * @param s Serializable
     * @return byte[]
     * @throws java.io.IOException
     */
    public static final byte[] object2Bytes(Serializable s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream ot = new ObjectOutputStream(out);
        ot.writeObject(s);
        ot.flush();
        ot.close();
        return out.toByteArray();
    }

    public static final String object2HexStr(Serializable s)
            throws IOException {
        return bytes2HexStr(object2Bytes(s));
    }

    public static final Object hexStr2Object(String hex)
            throws IOException, ClassNotFoundException {
        return bytes2Object(hexStr2Bytes(hex));
    }

    /**
     * BCD码转为10进制串(阿拉伯数据)
     *
     * @param bytes byte[]
     * @return bcd string  10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * 10进制串转为BCD码
     *
     * @param asc 10进制串
     * @return BCD码 byte[]
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * MD5加密字符串，返回加密后的16进制字符串
     *
     * @param origin
     * @return
     */
    public static String MD5Encode2Hex(String origin) {
        return bytes2HexStr(MD5Encode(origin));
    }

    /**
     * MD5加密字符串，返回加密后的字节数组
     *
     * @param origin
     * @return
     */
    public static byte[] MD5Encode(String origin) {
        return MD5Encode(origin.getBytes());
    }

    /**
     * MD5加密字节数组，返回加密后的字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] MD5Encode(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }

    }
//
//    public static int byteArrayToInt(byte[] b, int offset) {
//        int value= 0;
//        for (int i = 0; i < 4; i++) {
//            int shift= (4 - 1 - i) * 8;
//            value +=(b[i + offset] & 0x000000FF) << shift;
//        }
//        return value;
//    }
//    public static byte[] int2Word(int integer) {
//
//        byte[] byteStream = new byte[4];
//        for (int i = 0; i < 4; i++) {
//            byteStream[i] = (byte) (integer >>> (24 - i * 8));
//        }
//        return byteStream;
//    }

    /**
     * char 转 byte[4]
     *
     * @param ch
     * @return byte[4]
     */
    public static byte[] char2Word(char ch) {

        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (ch >>> (24 - i * 8));
        }
        return bytes;
    }

    public static final int[] calculateCRC(byte[] data, int offset, int len) {

        int[] crc = {0xFF, 0xFF};
        int nextByte = 0;
        int uIndex; /*
					 * will index into CRC lookup
					 *//*
						 * table
						 */
		/*
		 * pass through message buffer
		 */
        for (int i = offset; i < len && i < data.length; i++) {
            nextByte = 0xFF & ((int) data[i]);
            uIndex = crc[0] ^ nextByte; // *puchMsg++; /* calculate the CRC */
            crc[0] = crc[1] ^ auchCRCHi[uIndex];
            crc[1] = auchCRCLo[uIndex];
        }

        return crc;
    }// calculateCRC

    public static final int calculateLRC(byte[] data, int off, int len) {
        int lrc = 0;
        for (int i = off; i < len; i++) {
            lrc += (int) data[i] & 0xff; // calculate with unsigned bytes
        }
        lrc = (lrc ^ 0xff) + 1; // two's complement
        return (int) ((byte) lrc) & 0xff;
    }// calculateLRC

    /**
     * @param hibyte
     * @param lowbyte
     * @return a word.
     */
    public static final int makeWord(int hibyte, int lowbyte) {
        int hi = 0xFF & hibyte;
        int low = 0xFF & lowbyte;
        return ((hi << 8) | low);
    }

    public static byte[] combatBytes(byte[] first, byte[] second) {
        int len_f = first.length;
        int len_s = second.length;
        int len = len_f + len_s;
        if (len == 0) {
            return null;
        }
        byte[] dest = new byte[len];
        System.arraycopy(first, 0, dest, 0, len_f);
        System.arraycopy(second, 0, dest, len_f, len_s);
        return dest;
    }

    public static byte[] GBKBytes2UTF8Bytes(byte[] gbkBytes) throws UnsupportedEncodingException {
        //new String ("a汉字").getBytes() ;
        //String str=new String(gbkBytes,"ISO-8859-1");
        String str = new String(gbkBytes, "GBK");
        return str.getBytes("UTF-8");
    }

    public static byte[] UTF8Bytes2GBKBytes(byte[] utf8Bytes) throws UnsupportedEncodingException {
        //new String ("a汉字").getBytes() ;
        //String str=new String(utf8Bytes,"ISO-8859-1");
        String str = new String(utf8Bytes, "UTF-8");
        return str.getBytes("GBK");
    }

    /**
     * GBK字节流 转 UTF8字符串
     *
     * @param gbkBytes gbk字节流
     * @return utf8Str UTF8字符串
     * @throws UnsupportedEncodingException
     */
    public static String GBKBytes2UTF8Str(byte[] gbkBytes) throws UnsupportedEncodingException {
        //new String ("a汉字").getBytes() ;
        //String str=new String(gbkBytes,"ISO-8859-1");
        String str = new String(gbkBytes, "GBK");
        return new String(str.getBytes("UTF-8"), "UTF-8");
    }

    public static String UTF8Bytes2GBKStr(byte[] utf8Bytes) throws UnsupportedEncodingException {
        //new String ("a汉字").getBytes() ;
        //String str=new String(utf8Bytes,"ISO-8859-1");
        String str = new String(utf8Bytes, "UTF-8");
        return new String(str.getBytes("GBK"), "GBK");
    }

    public static String UTF8Str2GBKStr(String utf8Str) throws UnsupportedEncodingException {
//       byte[] utf8Bytes=UTF8Str2GBKBytes(utf8Str);
//        System.out.println("UTF8Str2GBKStr:utf8Bytes-len:"+utf8Bytes.length);
        byte[] gbkBytes = UTF8Str2GBKBytes(utf8Str);
        // return UTF8Bytes2GBKStr(utf8Bytes);
        return new String(gbkBytes, "GBK");

    }

    public static String GBKStr2UTF8Str(String gbkStr) {
        try {
            return new String(GBKStr2UTF8Bytes(gbkStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }

    /**
     * UTF8字符串(java默认编码） 转 GBK字节流
     *
     * @param utf8Str UTF8字符串(java默认编码）
     * @return gbkBytes  GBK字节流
     * @throws UnsupportedEncodingException
     */
    public static byte[] UTF8Str2GBKBytes(String utf8Str) throws UnsupportedEncodingException {
        //return UTF8Bytes2GBKBytes(utf8Str.getBytes("ISO-8859-1"));
        return UTF8Bytes2GBKBytes(utf8Str.getBytes("UTF-8"));
    }

    public static byte[] GBKStr2UTF8Bytes(String gbkStr) throws UnsupportedEncodingException {
        byte[] gbkBytes = gbkStr.getBytes("GBK");
        //byte[] gbkBytes=gbkStr.getBytes("ISO-8859-1");
        return GBKBytes2UTF8Bytes(gbkBytes);
    }

    /**
     * GBK字符串 转 UTF8字节流 , 已经解决中英文混编，奇数汉字问题
     *
     * @param gbkStr GBK字符串
     * @return utf8bytes UTF8字节流
     */
    public static byte[] GBKStr2UTF8Bytes2(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }

    /**
     * 八个hex字符串 转 int,空格不计数
     * @param hexStr 最多8个非空格hex字符
     * @return int
     */
    public static int hex8Str2int (String hexStr){
        if (hexStr==null){
            return 0;
        }
        byte[] tmp=hexStr2Bytes(hexStr);
        if (tmp==null || tmp.length>4){
            return 0;
        }
        return bytes2Int(tmp);
    }
    public static byte[] ArrayBytes2Bytes(ArrayList<Byte> arrayBytes){
        if (arrayBytes==null||arrayBytes.size()<1) return null;
        int len=arrayBytes.size();
        byte[] result = new byte[len];
        for(int i = 0; i < len; i++) {
            result[i] = arrayBytes.get(i);
        }
        return result;
    }
    public static ArrayList<Byte> Bytes2ArrayBytes (byte[] bytes){
        if(bytes==null || bytes.length<1){return null;}
        ArrayList<Byte> dest=new ArrayList<Byte>();
        for(byte b : bytes) {
            dest.add(b);
        }
        return dest;
    }
}