package com.pax.spos.utils;

/**
 * author: fable tang
 * Comments: 用于归类 TLV, 服务于 TLVUtils
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */
public class SortUtils {


    public static TLV SortTag(int tag,TLV tlv){
        if (tlv==null)return null;
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        tlv.setClazz(justClazz(byteTag[0]));
        //byteTag[1] 八位 process
        tlv.setFunc(justFunc(tlv.getClazz(), byteTag[1]));
        //byteTag[2] 八位 process
        //todo para有点复杂，暂时不处理
        //para 暂时不处理
        // sposTLV.setPara(justPara(byteTag[2]));
        return tlv;
    }
    private static String justClazz(byte bit8) {
        //byteTag[0] 低四位 process
        byte clazz4bit = (byte) ((bit8 & 0x0F));
        switch (clazz4bit) {
            //todo 规则待定
            case ((byte) (0x1)): { return ("PED"); }
            case ((byte) (0x2)): { return ("CARD"); }
            case ((byte) (0x3)): { return ("PRINT"); }
            case ((byte) (0x4)): { return ("SYSTEM"); }
            case ((byte) (0x5)): { return ("EMV"); }
            default: return null;
        }
    }
    public static String justClazz(int tag) {
        if (!TLVUtils.justSpos(tag)){return null;}
        //byteTag[0] 低四位 process
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justClazz(byteTag[0]));
    }
    private static String justFunc(String clazz,byte bit8) {
        //byteTag[1] 八位 process
        if (clazz==null||clazz.equals("")){return null;}
        //todo 拟采用json 配置文件
        switch (bit8) {
            //todo 规则待定
            case ((byte) (0x01)): { return ("GetVer"); }
            case ((byte) (0x02)): { return ("GetModel"); }
            default:return null;
        }
    }

    public static String justFunc(String clazz,int tag) {
        if (clazz==null||clazz.equals("")){return null;}
//        if (!TLVUtils.justSpos(tag)){return null;}
        //byteTag[1] 八位 process
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justFunc(clazz,byteTag[1]));
    }

    /**
     * 参数变动较大，暂时不处理
     * @param bit8
     * @return
     */
    private static String justPara(byte bit8) {
        return null;
        //byteTag[2] 八位 process
//        switch (bit8) {
        //todo 规则待定
//            case ((byte) (0x01)): {
//                return ("pszSN");
//            }
//            case ((byte) (0x02)): {
//                return ("pszKey");
//            }
//            default:return null;
//        }
    }
    public static String justPara(int tag) {
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justPara(byteTag[2]));
    }
}
