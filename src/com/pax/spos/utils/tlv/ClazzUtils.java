package com.pax.spos.utils.tlv;

import com.pax.spos.utils.TLV.model.*;
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
        tlv.setClazz(justClazz(byteTag[0]));
        //byteTag[1] 八位 process
        tlv.setFunc(justFunc(byteTag[1]));
        tlv.setPara(justPara(byteTag[2]));
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

        ArrayList<TagJsonClazz> tagJsonClazzs = getTagJson().getClazzs();
        if (tagJsonClazzs == null) return null;
        int len = tagJsonClazzs.size();
        if (len == 0) return null;
        TagJsonClazz clazz;
        for (; posClazz < len; posClazz++) {
            clazz = tagJsonClazzs.get(posClazz);
            if (str.equalsIgnoreCase(clazz.getId())) {
                if (posClazz > 1) {
                    posClazz -= 1;
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
        String str = ByteStringHex.byte2HexStr(bit8);
        ArrayList<TagJsonFunc> tagJsonFuncs = getTagJson().getClazzs().get(posClazz).getFuncs();
        if (tagJsonFuncs == null) return null;
        int len = tagJsonFuncs.size();
        if (len == 0) return null;
        TagJsonFunc func;
        for (posFunc = 0; posFunc < len; posFunc++) {
            func = tagJsonFuncs.get(posFunc);
            if (str.equalsIgnoreCase(func.getId())) {
                if (posFunc > 1) {
                    posFunc -= 1;
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
        ArrayList<TagJsonPara> tagJsonParas = getTagJson().getClazzs().get(posClazz).getFuncs().get(posFunc).getParas();
        if (tagJsonParas == null) return null;
        int len = tagJsonParas.size();
        if (len == 0) return null;
        TagJsonPara para;
        for (; posPara < len; posPara++) {
            para = tagJsonParas.get(posPara);
            if (str.equalsIgnoreCase(para.getId())) {
                if (posPara > 1) {
                    posPara -= 1;
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
