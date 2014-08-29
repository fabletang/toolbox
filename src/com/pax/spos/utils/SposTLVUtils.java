package com.pax.spos.utils;

import java.util.*;

/**
 * author: fable tang
 * Comments: SposTLVUtils   功能： 查找 归类 复制TLV  spostlv<->bytes
 * Create Date：2014-08-18
 * Modified By：
 * Modified Date:
 * JDK version used: 1.7
 * version: 0.9
 */
public class SposTLVUtils {
//    /**
//     * 复制 原始TLV对象到 SposTLV
//     *
//     * @param tlv 原始TLV对象
//     * @return SposTLV
//     */
//    public static SposTLV copyFromTLV(TLV tlv) {
//        if (tlv == null) return null;
//        SposTLV sposTLV = new SposTLV();
//
//        sposTLV.setConstructed(tlv.isConstructed());
//        sposTLV.setTag(tlv.getTag());
//        sposTLV.setLength(tlv.getLength());
//        sposTLV.setValue(tlv.getValue());
//        sposTLV.setDataType(tlv.getDataType());
//
//        return sposTLV;
//    }

    /**
     * 判断 tag 8bit byte 是否复合结构
     * <p/>
     * 根据规则 tag 第一个byte的 b8-b5 只有 0xE 0xC两种情况
     * byte isConstructed4bit=(byte)(byteTag[0]|0xF0);
     * 取高四位
     *
     * @param bits8 byte
     * @return boolean 默认为false
     */
    public static boolean justConstructed(byte bits8) {
        byte isConstructed4bit = (byte) ((bits8 & 0xF0) >> 4);
        switch (isConstructed4bit) {
            case ((byte) (0xC)): {
                return false;
            }
            case ((byte) (0xE)): {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * 判断 tag int 是否复合结构
     *
     * @param tag int32
     * @return boolean 默认为false
     */
    public static boolean justConstructed(int tag) {
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        byte isConstructed4bit = (byte) ((byteTag[0] & 0xF0) >> 4);
        return justConstructed(isConstructed4bit);
    }

    /**
     * 判断 tag int 是否spos规范
     * tag>0xC1010101 && tag<0xEFFFFFFF 为合法
     *
     * @param tag int32
     * @return boolean 默认为false
     */
    public static boolean justSpos(int tag) {
        return (tag > 0xC1010101 && tag < 0xEFFFFFFF);
    }

    /**
     * 判断 tag 8bit byte 是否数组
     * <p/>
     * 根据规则 tag 第4个byte的 b8-b5 只有 0x1 0x0两种情况
     * 取高四位
     *
     * @param bits8 byte
     * @return boolean 默认为false
     */
    public static boolean justArray(byte bits8) {
        byte isArray4bit = (byte) ((bits8 & 0xF0) >> 4);
        switch (isArray4bit) {
            case ((byte) (0x1)): {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * 判断 tag int 是否数组
     *
     * @param tag int32
     * @return boolean 默认为false
     */
    public static boolean justArray(int tag) {
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        byte isArray4bit = (byte) ((byteTag[3] & 0xF0) >> 4);
        return justArray(isArray4bit);
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
        if (!justSpos(tag)){return null;}
        //byteTag[0] 低四位 process
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justClazz(byteTag[0]));
    }
    private static String justFunc(byte bit8) {
        //byteTag[1] 八位 process
        switch (bit8) {
            //todo 规则待定
            case ((byte) (0x01)): { return ("GetVer"); }
            case ((byte) (0x02)): { return ("GetModel"); }
            default:return null;
        }
    }

    public static String justFunc(int tag) {
        if (!justSpos(tag)){return null;}
        //byteTag[1] 八位 process
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justFunc(byteTag[1]));
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
        if (!justSpos(tag)){return null;}
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justPara(byteTag[2]));
    }
    private static String justDataType(byte bit8) {
        //byteTag[3] 低四位 process
        byte dataType4Lo = (byte) ((bit8 & 0x0F));
        //0x02 表示N(BCD), 0x03 表示B(HEX), 0x04  表示
        //GBK(汉字编码),0x05 表示Z(BCD扩展 字母/=,备用)
        switch (dataType4Lo) {
            case ((byte) (0x1)): {
                return ("ANS");
            }
            case ((byte) (0x2)): {
                return ("N");
            }
            case ((byte) (0x3)): {
                return ("B");
            }
            case ((byte) (0x4)): {
                return ("GBK");
            }
            case ((byte) (0x5)): {
                return ("Z");
            }
            default:return null;
        }
    }
    public static String justDataType(int tag) {
        if (!justSpos(tag)){return null;}
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justDataType(byteTag[3]));
    }

    /**
     * 根据tag进行处理，对应到 clazz/func/para, 具体请参考 sposTLV规则
     *
     * @param tag     int 4byte
     * @param sposTLV 待处理的sposTLV对象
     * @return SposTLV
     */
    public static SposTLV processTag(int tag, SposTLV sposTLV) {
        if (!justSpos(tag)) {
            return null;
        }
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        //根据规则 tag 第一个byte的 b8-b5 只有 0xE 0xC两种情况
        //byte isConstructed4bit=(byte)(byteTag[0]|0xF0);
        // 取高四位
        sposTLV.setConstructed(justConstructed(byteTag[0]));
        //byteTag[0] 低四位 process
        sposTLV.setClazz(justClazz(byteTag[0]));
        //byteTag[1] 八位 process
        sposTLV.setFunc(justFunc(byteTag[1]));
        //byteTag[2] 八位 process
        sposTLV.setPara(justPara(byteTag[2]));
        //byteTag[3] 高4位 process
        sposTLV.setArray(justArray(byteTag[3]));
        //byteTag[3] 低四位 process
        //0x02 表示N(BCD), 0x03 表示B(HEX), 0x04  表示
        //GBK(汉字编码),0x05 表示Z(BCD扩展 字母/=,备用)
        sposTLV.setDataType(justDataType(byteTag[3]));
        return sposTLV;
    }

    /**
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes    字节流
     * @param bytesLen 指定长度 ，bytesLen<=bytes.length
     * @return
     */
    public static List<SposTLV> bytes2SposTLVs(byte[] bytes, int bytesLen) {
        // 长度校验
        if (bytes == null || bytesLen <= 0 || bytesLen > bytes.length) {
            return null;
        }
        // 剔除无效数据
        int start = 0;
        for (int i = 0; i < bytesLen; i++) {
            start++;
            if (bytes[i] == 0x00 || (bytes[i] & 0xFF) == 0xFF) {
                continue;
            } else {
                break;
            }
        }
        if (start >= bytesLen) {
            return null;
        }
        //内容校验
        if (bytes[start] > 0xEF || bytes[start] < 0xC1) {
            return null;
        }
        int tag;
        int len;
        int lenBytes;// length 对应的bytes2Int
        List<SposTLV> items = new ArrayList<SposTLV>();

        for (int i = start; i < bytesLen; ) {
            // parse tag
            SposTLV tlv = new SposTLV();
            byte[] tagBytes = new byte[4];
            System.arraycopy(bytes, start, tagBytes, 0, 4);
            tag = ByteStringHex.bytes2Int(tagBytes);
            i += 4;
            if ((bytes[i] & 0x80) == 0x80) {
                lenBytes = (bytes[i] & 0x7F);
                //假定length最多占用5个byte
                if (lenBytes > 4) {
                    return null;
                }
                byte[] lenValue = new byte[lenBytes];
                len = ByteStringHex.bytes2Int(lenValue);
                i += 1 + lenBytes;
            } else {
                len = bytes[i];
                i += 1;
            }
            if(justSpos(tag)) {
                byte[] value = new byte[len];
                System.arraycopy(bytes, i, value, 0, len);
                tlv.setTag(tag);
                tlv.setLength(len);
                tlv.setValue(value);
                // process sposTLV
                tlv = processTag(tag, tlv);
                if(tlv!=null) {
                    items.add(tlv);
                }
            }
        }
        return items;
    }
//    private static List<SposTLV> parseSposTLV2(SposTLV sposTLV) {
//        if (!sposTLV.isConstructed()){
//          return null;
//        }
//        byte[] bytes=sposTLV.getValue();
//        int fatherTag=sposTLV.getFatherTag();
//
//        List<SposTLV> tlvs=bytes2SposTLVs(bytes);
//        for (SposTLV tlv:tlvs){
//           tlv.setFatherTag(fatherTag);
//        }
//        return tlvs;
//    }

    /**
     * 递归处理 原始 sposTLV 对象，转为flatTLVs
     * sposTLV.isConstructed() 为判断依据
     *
     * @param sposTLV  入参
     * @param flatTLVs 出参
     */
    private static void sposTLV2FlatTLVs(SposTLV sposTLV, List<SposTLV> flatTLVs) {
        if (sposTLV == null) {
            return;
        }
        if (!sposTLV.isConstructed()) {
            return;
        }
        byte[] bytes = sposTLV.getValue();
        int fatherTag = sposTLV.getTag();

        List<SposTLV> tlvs = bytes2SposTLVs(bytes);
        if (tlvs == null || tlvs.size() < 1) {
            return;
        }
        for (SposTLV tlv : tlvs) {
            tlv.setFatherTag(fatherTag);
            flatTLVs.add(tlv);
            if (tlv.isConstructed()) {
                //todo 递归未测试
                sposTLV2FlatTLVs(tlv, flatTLVs);
            }
        }
    }

    /**
     * 处理原始 sposTLVs ，转为flatTLVs
     *
     * @param sposTLVs
     * @return flatTLVs
     */
    public static List<SposTLV> sposTLVs2FlatTLVs(List<SposTLV> sposTLVs) {
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        List<SposTLV> items = new ArrayList<SposTLV>();
        for (SposTLV tlv : sposTLVs) {
            List<SposTLV> tmp2 = new ArrayList<SposTLV>();
            sposTLV2FlatTLVs(tlv, tmp2);
            if (!tmp2.isEmpty()) {
                items.addAll(tmp2);
            }
        }
        return items;
    }

    /**
     * tlvbytes 转为 flatTLV 对象数组, 同一tag的tlv有可能重复，比如数组(因为数组里的元素tag一样)
     *
     * @param bytes
     * @return spostlv list
     */
    public static List<SposTLV> bytes2FlatTLVs(byte[] bytes) {
        //假定第一层 没有fatherTag
        List<SposTLV> tlvs = bytes2SposTLVs(bytes);
        if (tlvs == null) {
            return null;
        }
        return sposTLVs2FlatTLVs(tlvs);
    }

    /**
     * 查找函数 根据tag int 查找
     *
     * @param tag
     * @param sposTLVs
     * @return TLVs
     */
    public static List<SposTLV> findByTag(int tag, List<SposTLV> sposTLVs) {
        if (!justSpos(tag)) {
            return null;
        }
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        List<SposTLV> items = new ArrayList<SposTLV>();
        for (SposTLV tlv : sposTLVs) {
            if (tag == tlv.getTag()) {
                items.add(tlv);
            }
        }
        return items;
    }

    public static SposTLV getTLVNotArray(int tag, List<SposTLV> sposTLVs) {
        if (!justSpos(tag)) {
            return null;
        }
        //数组 判断
        if (justArray(tag)) {
            return null;
        }
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        //假定 数组里没有重复对象，如果重复，取第一个
        for (SposTLV tlv : sposTLVs) {
            if (tag == tlv.getTag()) {
                return tlv;
            }
        }
        return null;
    }

    public static List<SposTLV> getTLVsNotArray(List<SposTLV> sposTLVs) {
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        List<SposTLV> dest = new ArrayList<SposTLV>();
        //假定 数组里没有重复对象，如果重复，取第一个
        for (SposTLV tlv : sposTLVs) {
            if (!justArray(tlv.getTag()) && justSpos(tlv.getTag())) {
                dest.add(tlv);
            }
        }
        if (dest.size() < 1) {
            return null;
        }
        return dest;
    }

    private static int[] getTLVIsArrayNums(List<SposTLV> sposTLVs) {
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }

        Map<Integer, SposTLV> map = new HashMap<Integer, SposTLV>();
        for (SposTLV tlv : sposTLVs) {
            if (justArray(tlv.getTag())) {
                map.put(tlv.getTag(), tlv);
            }
        }
        int nums = map.size();
        if (nums < 1) {
            return null;
        }
        int[] dest = new int[nums];
        Iterator it = map.keySet().iterator();
        for (int i = 0; i < nums; i++) {
            dest[i] = Integer.parseInt(it.next().toString());
        }
        return dest;
    }

    public static List<SposTLV> getTLVsIsArray(List<SposTLV> sposTLVs) {
        int[] tags = getTLVIsArrayNums(sposTLVs);
        if (tags.length < 1) {
            return null;
        }
        List<SposTLV> dest = new ArrayList<SposTLV>();
        for (int i = 0; i < tags.length; i++) {
            dest.addAll(getTLVsIsArray(tags[i], sposTLVs));
        }
        return dest;
    }

    public static List<SposTLV> getTLVsIsArray(int tag, List<SposTLV> sposTLVs) {
        //数组 判断
        if (!justArray(tag)) {
            return null;
        }
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        List<SposTLV> items = new ArrayList<SposTLV>();
        for (SposTLV tlv : sposTLVs) {
            if (tag == tlv.getTag()) {
                items.add(tlv);
            }
        }
        return items;
    }

    public static SposTLV getFatherTLV(int tag, List<SposTLV> sposTLVs) {
        if (!justSpos(tag)) {
            return null;
        }
        //数组 判断
        if (justArray(tag)) {
            return null;
        }
        if (sposTLVs == null || sposTLVs.size() < 1) {
            return null;
        }
        //假定 数组里没有重复对象，如果重复，取第一个
        for (SposTLV tlv : sposTLVs) {
            if (tag == tlv.getFatherTag()) {
                return tlv;
            }
        }
        return null;
    }

    /**
     * bytes2SposTLVS 的多态
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes 字节流
     * @return sposTLV
     */
    public static List<SposTLV> bytes2SposTLVs(byte[] bytes) {
        return bytes2SposTLVs(bytes, bytes.length);
    }

    //-------------------object to TLV bytes---------------
    private static byte[] len2Bytes(int len) {
        //len 不能超过 8个F, 即4G, 也不能为0
        if (len > 0xFFFFFFFF || len == 0) {
            return null;
        }
        if (len > 0x7F) {
            byte[] lenValue = ByteStringHex.int2BytesN(len);
            byte[] dest = new byte[1 + lenValue.length];
            byte[] lenvalutebytes = ByteStringHex.int2BytesN(lenValue.length);

            dest[0] = (byte) (lenvalutebytes[0] | 0x80);
            System.arraycopy(lenValue, 0, dest, 1, lenValue.length);
            return dest;
        } else {
            byte[] dest = new byte[1];
            dest[0] = (byte) (len & 0x7F);
            return dest;
        }
    }

    /**
     * TLV 转 bytes ,tag,value,fatherTag 为必须
     *
     * @param tlv
     * @return byte[]
     */
    public static byte[] TLV2Bytes(SposTLV tlv) {
        if (tlv == null || !justSpos(tlv.getTag())) {
            return null;
        }
        if (tlv.getValue() == null || tlv.getValue().length < 1) {
            return null;
        }
        // tlv.length 可以为0, 但是 如果不为零，并且不等于value的长度，视为非法
        if (tlv.getLength() != tlv.getValue().length && tlv.getLength() != 0) {
            return null;
        }
        int tlv_l = tlv.getValue().length;
        byte[] lenBytes = len2Bytes(tlv_l);
        byte[] tagBytes = ByteStringHex.int2Bytes(tlv.getTag());
        int len = 4 + lenBytes.length + tlv_l;
        byte[] dest = new byte[len];
        System.arraycopy(tagBytes, 0, dest, 0, 4);
        System.arraycopy(lenBytes, 0, dest, 4, lenBytes.length);
        System.arraycopy(tlv.getValue(), 0, dest, 4 + lenBytes.length, tlv_l);
        return dest;
    }

}
