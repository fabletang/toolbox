package com.pax.spos.utils.model;

/**
 * author: fable tang
 * Comments: 用于归类 TLV (类别/功能/参数）
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */public class TLVTagClazz {
    private int tag;
    private String type;
    private boolean isConstructed;

    private String clazz;
    private String func;
    private String para;
    private int paraIndex;
    public TLVTagClazz(){
    }
    public TLVTagClazz(int tag, String type, boolean isConstructed, String clazz, String func, String para, int paraIndex) {
        this.tag = tag;
        this.type = type;
        this.isConstructed = isConstructed;
        this.clazz = clazz;
        this.func = func;
        this.para = para;
        this.paraIndex = paraIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TLVTagClazz)) return false;

        TLVTagClazz that = (TLVTagClazz) o;

        if (isConstructed != that.isConstructed) return false;
        if (paraIndex != that.paraIndex) return false;
        if (tag != that.tag) return false;
        if (!clazz.equals(that.clazz)) return false;
        if (!func.equals(that.func)) return false;
        if (!para.equals(that.para)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag;
        result = 41 * result + type.hashCode();
        result = 41 * result + (isConstructed ? 1 : 0);
        result = 41 * result + clazz.hashCode();
        result = 41 * result + func.hashCode();
        result = 41 * result + para.hashCode();
        result = 41 * result + paraIndex;
        return result;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isConstructed() {
        return isConstructed;
    }

    public void setConstructed(boolean isConstructed) {
        this.isConstructed = isConstructed;
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

    public int getParaIndex() {
        return paraIndex;
    }

    public void setParaIndex(int paraIndex) {
        this.paraIndex = paraIndex;
    }
}


