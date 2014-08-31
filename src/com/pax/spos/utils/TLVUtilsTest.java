package com.pax.spos.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class TLVUtilsTest {

    @Test
    public void testJustConstructed() throws Exception {

        String hexStr="E1 01 00 00";
        int tag=ByteStringHex.hex8Str2int(hexStr);
        boolean dest=TLVUtils.justConstructed(tag);
        assertEquals(true,dest);
        hexStr="C1 01 00 02";
        tag=ByteStringHex.hex8Str2int(hexStr);
        dest=TLVUtils.justConstructed(tag);
        assertEquals(false,dest);
    }

    @Test
    public void testJustArray() throws Exception {
        String hexStr="E1 01 00 12";
        int tag=ByteStringHex.hex8Str2int(hexStr);
        boolean dest=TLVUtils.justArray(tag);
        assertEquals(true,dest);
        hexStr="E1 01 00 02";
        tag=ByteStringHex.hex8Str2int(hexStr);
        dest=TLVUtils.justArray(tag);
        assertEquals(false,dest);

    }

    @Test
    public void testJustDataType() throws Exception {

    }

    @Test
    public void testProcessTag() throws Exception {

    }

    @Test
    public void testFindByTag() throws Exception {

    }

    @Test
    public void testBytes2NestedFlatTLVs() throws Exception {

    }

    @Test
    public void testBytes2TopNestedTLVs() throws Exception {

    }

    @Test
    public void testTLV2Bytes() throws Exception {
        String hexStr="C1 01 02 03";
        int tag=ByteStringHex.hex8Str2int(hexStr);
        byte[] value=ByteStringHex.hexStr2Bytes("00 01 05");
        TLV tlv=new TLV();
        tlv.setTag(tag);
        tlv.setValue(value);
//        System.out.println("testTLV2Bytes tlv="+tlv);
        byte[] res=TLVUtils.TLV2Bytes(tlv);
        assertEquals(8,res.length);
//        System.out.println("=="+Byte.parseByte("C",16));
        assertEquals((byte)(0xC1),res[0]);
        assertEquals((byte)(0x05),res[7]);
        tlv=TLVUtils.processTag(tlv.getTag(),tlv);
//        System.out.println("testTLV2Bytes tlv="+tlv);
    }

    @Test
    public void testTLVs2Bytes() throws Exception {
        String hexStr="E1 01 00 00";
        int tag=ByteStringHex.hex8Str2int(hexStr);
        byte[] value=ByteStringHex.hexStr2Bytes("00 01 05");
        TLV tlv1=new TLV();
        tlv1.setTag(tag);
        tlv1=TLVUtils.processTag(tlv1.getTag(),tlv1);

        hexStr="C1 01 01 03";
        tag=ByteStringHex.hex8Str2int(hexStr);
        value=ByteStringHex.hexStr2Bytes("01 01 05");
        TLV tlv11=new TLV(tag,value);

        hexStr="E1 01 02 03";
        tag=ByteStringHex.hex8Str2int(hexStr);
        value=ByteStringHex.hexStr2Bytes("02 01 06");
        TLV tlv12=new TLV(tag,value);

        hexStr="C1 01 02 03";
        tag=ByteStringHex.hex8Str2int(hexStr);
        value=ByteStringHex.hexStr2Bytes("02 11 36");
        TLV tlv121=new TLV(tag,value);

        TLVUtils.addSubTLV(tlv11,tlv1);
        TLVUtils.addSubTLV(tlv121,tlv12);
        TLVUtils.addSubTLV(tlv12,tlv1);
        System.out.println("11 testTLV2Bytes tlv="+tlv1);

        byte[] res=TLVUtils.TLV2Bytes(tlv1);
        assertEquals((byte)(0xE1),res[0]);
        assertEquals((byte)(0x06),res[res.length-1]);

        System.out.println("===========13 testTLV2Bytes res="+ByteStringHex.bytes2HexStr(res));
    }
}