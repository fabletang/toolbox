package com.pax.spos.utils;

import java.util.Arrays;
import java.util.List;

/**
 * author: fable tang
 * Comments: SposTLV spos转有TLV对象，增加 大类/函数 string,用于归类
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */
public class SposTLV {
    private String clazz;
    private String func;
    private String para;
    private boolean isArray;//是否数组

    private boolean isConstructed;//是否复合结构
    private int tag;
    private int length;
    private byte[] value;
    private String dataType;

    private int fatherTag;
    private List<SposTLV> subTLVs;

    public SposTLV() {
    }

    public SposTLV(int tag, int length, byte[] value ) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }
    public SposTLV(String clazz, String func, String para, boolean isArray, boolean isConstructed, int tag, int length, byte[] value, String dataType, int fatherTag, List<SposTLV> subTLVs) {
        this.clazz = clazz;
        this.func = func;
        this.para = para;
        this.isArray = isArray;
        this.isConstructed = isConstructed;
        this.tag = tag;
        this.length = length;
        this.value = value;
        this.dataType = dataType;
        this.fatherTag = fatherTag;
        this.subTLVs = subTLVs;
    }

    public List<SposTLV> getSubTLVs() {
        return subTLVs;
    }

    public void setSubTLVs(List<SposTLV> subTLVs) {
        this.subTLVs = subTLVs;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public boolean isArray(){
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray=isArray;
    }
    public boolean isConstructed() {
        return isConstructed;
    }

    public void setConstructed(boolean isConstructed) {
        this.isConstructed = isConstructed;
    }

    public int getFatherTag() {
        return fatherTag;
    }

    public void setFatherTag(int fatherTag) {
        this.fatherTag = fatherTag;
    }
    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SposTLV)) return false;

        SposTLV sposTLV = (SposTLV) o;

        if (length != sposTLV.length) return false;
        if (tag != sposTLV.tag) return false;
        if (subTLVs != null ? !subTLVs.equals(sposTLV.subTLVs) : sposTLV.subTLVs != null) return false;
        if (!Arrays.equals(value, sposTLV.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag;
        result = 31 * result + length;
        result = 31 * result + Arrays.hashCode(value);
        result = 31 * result + (subTLVs != null ? subTLVs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SposTLV{" +
                "isArray=" + isArray +
                ", isConstructed=" + isConstructed +
                ", tag_Hex=" + Integer.toHexString(tag) + '\'' +
                ", tag=" + tag +
                ", length=" + length +
                ", value=" + Arrays.toString(value) +
                ", dataType='" + dataType + '\'' +
                ", clazz='" + clazz + '\'' +
                ", func='" + func + '\'' +
                ", para='" + para + '\'' +
                ", fatherTag_Hex=" + Integer.toHexString(fatherTag) + '\'' +
                ", fatherTag=" + fatherTag +
                ", subTLVs=" + subTLVs +
                '}';
    }
//    @Override
//    public String toString() {
//        return "SposTLV{" +
//                "clazz='" + clazz + '\'' +
//                ", func='" + func + '\'' +
//                ", para='" + para + '\'' +
//                ", tag=" + tag + '\'' +
//                ", tag_Hex=" + Integer.toHexString(tag) + '\'' +
//                ", length=" + length + '\'' +
//                ", value_Hex=" + ByteStringHex.bytes2HexStr(value) + '\'' +
//                ", dataType='" + dataType + '\'' +
//                ", fatherTag=" + fatherTag + '\'' +
//                ", fatherTag_Hex=" + Integer.toHexString(fatherTag) + '\'' +
//                ", isConstructed=" + isConstructed +
//                ", isArray=" + isArray +
//                '}';
//    }

}
