package com.pax.spos.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteStringHexTest {

    @Test
    public void testHexStr2Bytes() throws Exception {

    String test="0102 03";
    byte[] bytes=ByteStringHex.hexStr2Bytes(test);
    int i =(int)(bytes[2]);
    assertEquals(3,i);

    }

    @Test
    public void testBytes2HexStr() throws Exception {
       //0104 0000 0a
        byte[] test = {(byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x0a };
        String str=ByteStringHex.bytes2HexStr(test);
        assertEquals("010400000A",str);

    }

    @Test
    public void testBcd2Str() throws Exception {
        byte[] test = {(byte) 0x02, (byte) 0x1a };
        String str=ByteStringHex.bcd2Str(test);
        assertEquals("2110",str);
    }

    @Test
    public void testStr2Bcd() throws Exception {
        String test="0113";
        byte[] bytes=ByteStringHex.str2Bcd(test);
        assertEquals((byte)(0x13),bytes[1]);
        assertEquals((byte)(0x01),bytes[0]);
    }

    @Test
    public void testBytes2Int() throws Exception {
        String hexStr="0F00 0001";
        byte[] bytes=ByteStringHex.hexStr2Bytes(hexStr);
        assertEquals((byte)(0x0F),bytes[0]);
        assertEquals((byte)(0x01),bytes[3]);
        int i=ByteStringHex.bytes2Int(bytes);
        assertEquals(251658241,i);
        hexStr="0F 0001";
        bytes=ByteStringHex.hexStr2Bytes(hexStr);
        assertEquals(3,bytes.length);
        assertEquals((byte)(0x0F),bytes[0]);
        assertEquals((byte)(0x01),bytes[2]);
        i=ByteStringHex.bytes2Int(bytes);
        assertEquals(983041,i);
    }

    @Test
    public void testInt2Bytes() throws Exception {
        int i=251658241;
        byte[] bytes=ByteStringHex.int2Bytes(i);
        assertEquals(4,bytes.length);
        assertEquals((byte)(0x0F),bytes[0]);
        assertEquals((byte)(0x01),bytes[3]);
        i=983041;
        bytes=ByteStringHex.int2Bytes(i);
        assertEquals(4,bytes.length);
        assertEquals((byte)(0x0F),bytes[1]);
        assertEquals((byte)(0x01),bytes[3]);
    }

    @Test
    public void testGBKUTF8() throws Exception {
        String str="a中文字";
        String str2=ByteStringHex.GBKStr2UTF8Str(ByteStringHex.UTF8Str2GBKStr(str));
        assertEquals(str,str2);
    }
    @Test
    public void testCalculateCRC() throws Exception {

    }

    @Test
    public void testCalculateLRC() throws Exception {

    }
    @Test
    public void testInt2BytesN () throws Exception {
        int i=0x3FD;
        String str;
        str=ByteStringHex.bytes2HexStr(ByteStringHex.int2BytesN(i));
        assertEquals("03FD",str);
        i=0x12FC;
        str=ByteStringHex.bytes2HexStr(ByteStringHex.int2BytesN(i));
        assertEquals("12FC",str);
        i=0xE2FC1234;
        str=ByteStringHex.bytes2HexStr(ByteStringHex.int2BytesN(i));
        assertEquals("E2FC1234",str);
    }
}