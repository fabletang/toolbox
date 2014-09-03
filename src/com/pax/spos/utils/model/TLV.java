package com.pax.spos.utils.model;

import com.pax.spos.utils.ByteStringHex;

import java.util.Arrays;
import java.util.List;

/**
 * author: fable tang
 * Comments: byte <-> TLV对象 转换
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */
public class TLV {
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
    private List<TLV> subTLVs;

    public TLV() {
    }

    public TLV(int tag, byte[] value) {
        this.tag = tag;
//        if (value!=null){this.length=value.length;}
        this.value = value;
    }
    public TLV(int tag, int length, byte[] value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public TLV(String clazz, String func, String para, boolean isArray, boolean isConstructed, int tag, int length, byte[] value, String dataType, int fatherTag, List<TLV> subTLVs) {
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

    public List<TLV> getSubTLVs() {
        return subTLVs;
    }

    public void setSubTLVs(List<TLV> subTLVs) {
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

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
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
        if (!(o instanceof TLV)) return false;

        TLV TLV = (TLV) o;

        if (length != TLV.length) return false;
        if (tag != TLV.tag) return false;
        if (subTLVs != null ? !subTLVs.equals(TLV.subTLVs) : TLV.subTLVs != null) return false;
        if (!Arrays.equals(value, TLV.value)) return false;

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
                ", tag_Hex=" + Integer.toHexString(tag).toUpperCase() + '\'' +
                ", tag=" + tag +
                ", length=" + length +
                ", value=" + ByteStringHex.bytes2HexStr(value) +
//                ", value=" + Arrays.toString(value) +
                ", dataType='" + dataType + '\'' +
                ", clazz='" + clazz + '\'' +
                ", func='" + func + '\'' +
                ", para='" + para + '\'' +
                ", fatherTag_Hex=" + Integer.toHexString(fatherTag).toUpperCase() + '\'' +
                ", fatherTag=" + fatherTag +
                ", subTLVs=" + subTLVs +
                '}';
    }
}
