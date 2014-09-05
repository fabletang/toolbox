package com.pax.spos.utils.tlv;

import com.pax.spos.utils.tlv.model.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * author: fable tang
 * Comments: 用于归类 TLV, 服务于 TLVUtils
 * 对应tag.json 位于 com.pax.spos.utils 目录，jar包也可以，但是 目录优先
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */
public class ClazzUtils {
    private static int posClazz;
    private static int posFunc;
    private static int posPara;

    public static TagJson getTagJson() {
        try {
            return TagJsonUtils.getInstance().parseJson("tag.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TLV ClazzTag(int tag, TLV tlv) {
        if (tlv == null) return null;
        posClazz = 0;
        posFunc = 0;
        posPara = 0;
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        String clazz=justClazz(byteTag[0]);
//        tlv.setClazz(justClazz(byteTag[0]));
        if (clazz==null){return tlv;}
        tlv.setClazz(clazz);
        //byteTag[1] 八位 processo

        String func=justFunc(byteTag[1]);
        if (func==null){return tlv;}
        tlv.setFunc(func);

        String para=justPara(byteTag[2]);
        if (para==null){return tlv;}
        tlv.setPara(para);
        //byteTag[2] 八位 process
        // sposTLV.setPara(justPara(byteTag[2]));
        posClazz = 0;
        posFunc = 0;
        posPara = 0;
        return tlv;
    }

    private static String justClazz(byte bit8) {
        posClazz = 0;
        posFunc = 0;
        posPara = 0;
        //byteTag[0] 低四位 process
        byte clazz4bit = (byte) ((bit8 & 0x0F));
        String str = ByteStringHex.lo4Bit2HexStr(clazz4bit);
//        System.out.println("justClazz clazz4Bit="+str);
        TagJson tagJson=getTagJson();
        if (tagJson==null){return null;}
        ArrayList<TagJsonClazz> tagJsonClazzs = tagJson.getClazzs();
        if (tagJsonClazzs == null||tagJsonClazzs.size()<1) {posClazz=-1;return null;};
        int len = tagJsonClazzs.size();
        if (len == 0) return null;
        TagJsonClazz clazz;
        for (; posClazz < len; posClazz++) {
            clazz = tagJsonClazzs.get(posClazz);
            if (str.equalsIgnoreCase(clazz.getId())) {
                if (posClazz > 1) {
//                    posClazz -= 1;
                }
                return clazz.getName();
            }
        }
        return null;
    }

    private static String justClazz(int tag) {
        if (!TLVUtils.justSpos(tag)) {
            return null;
        }
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justClazz(byteTag[0]));
    }

    private static String justFunc(byte bit8) {
        //byteTag[1] 八位 process
//        if (clazz == null || clazz.equals("")) {
//            return null;
//        }
//        System.out.println("justFunc pos"+posClazz);
        String str = ByteStringHex.byte2HexStr(bit8);
        TagJson tagJson=getTagJson();
        if (tagJson==null){return null;}
        ArrayList<TagJsonClazz> tagJsonClazzs = tagJson.getClazzs();
        if (tagJsonClazzs==null|| tagJsonClazzs.size()<1){return null;}
        if (posClazz==-1){
            return null;
        }
//        System.out.println("----posClazz"+posClazz);
        ArrayList<TagJsonFunc> tagJsonFuncs = tagJson.getClazzs().get(posClazz).getFuncs();
        if (tagJsonFuncs == null||tagJsonFuncs.size()<1) {posFunc=-1;return null;}
//        System.out.println("justFunc="+tagJsonFuncs);
        int len = tagJsonFuncs.size();
        if (len == 0) return null;
        TagJsonFunc func;
        for (; posFunc < len; posFunc++) {
            func = tagJsonFuncs.get(posFunc);
            if (str.equalsIgnoreCase(func.getId())) {
                if (posFunc > 1) {
//                    posFunc -= 1;
                }
                return func.getName();
            }
        }
        return null;
    }

//    public static String justFunc(String clazz, int tag) {
//        if (clazz == null || clazz.equals("")) {
//            return null;
//        }
//        //byteTag[1] 八位 process
//        byte[] byteTag = ByteStringHex.int2Bytes(tag);
//        return (justFunc(clazz, byteTag[1]));
//    }

    /**
     * 参数变动较大，暂时不处理
     *
     * @param bit8
     * @return
     */
    private static String justPara(byte bit8) {
        //byteTag[2] 八位 process
        String str = ByteStringHex.byte2HexStr(bit8);
        TagJson tagJson=getTagJson();
        if (tagJson==null||tagJson.getClazzs().get(posClazz).getFuncs()==null){return null;}
        ArrayList<TagJsonPara> tagJsonParas = tagJson.getClazzs().get(posClazz).getFuncs().get(posFunc).getParas();
        if (tagJsonParas == null) {posPara=-1;return null;}
        int len = tagJsonParas.size();
        if (len == 0) return null;
        TagJsonPara para;
        for (; posPara < len; posPara++) {
            para = tagJsonParas.get(posPara);
            if (str.equalsIgnoreCase(para.getId())) {
                if (posPara > 1) {
//                    posPara -= 1;
                }
                return para.getName();
            }
        }
        return null;
    }

//    public static String justPara(int tag) {
//        byte[] byteTag = ByteStringHex.int2Bytes(tag);
//        return (justPara(byteTag[2]));
//    }
}
