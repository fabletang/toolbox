package com.pax.spos.utils.tlv;

import com.pax.spos.utils.tlv.model.TLV;
import com.pax.spos.utils.tlv.model.TLVComparator;

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
public class TLVUtils {
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
//        byte isConstructed4bit = (byte) ((byteTag[0] & 0xF0) >> 4);
        return justConstructed(byteTag[0]);
    }

    /**
     * 判断 tag int 是否spos规范
     * tag>0xC1000000 && tag<0xEFFFFFFF 为合法
     *
     * @param tag int32
     * @return boolean 默认为false
     */
    public static boolean justSpos(int tag) {
        return (tag > 0xC1000000 && tag < 0xEFFFFFFF);
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
        //  byte isArray4bit = (byte) ((byteTag[3] & 0xF0) >> 4);
        return justArray(byteTag[3]);
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
            //默认  hex
            default:
                return "B";
        }
    }

    public static String justDataType(int tag) {
        if (!justSpos(tag)) {
            return null;
        }
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        return (justDataType(byteTag[3]));
    }

    /**
     * 根据tag进行处理，对应到 clazz/func/para, 具体请参考 sposTLV规则
     *
     * @param tag int 4byte
     * @param tlv 待处理的sposTLV对象
     * @return SposTLV
     */
    public static TLV processTag(int tag, TLV tlv) {
        if (!justSpos(tag)) {
            return null;
        }
        byte[] byteTag = ByteStringHex.int2Bytes(tag);
        //根据规则 tag 第一个byte的 b8-b5 只有 0xE 0xC两种情况
        //byte isConstructed4bit=(byte)(byteTag[0]|0xF0);
        // 取高四位
        tlv.setConstructed(justConstructed(byteTag[0]));
        //byteTag[0] 低四位 process

        //byteTag[3] 高4位 process
        tlv.setArray(justArray(byteTag[3]));
        //byteTag[3] 低四位 process
        //0x02 表示N(BCD), 0x03 表示B(HEX), 0x04  表示
        //GBK(汉字编码),0x05 表示Z(BCD扩展 字母/=,备用)
        tlv.setDataType(justDataType(byteTag[3]));
        // 调用 sortutils 处理
//        tlv = ClazzUtils.SortTag(tag, tlv);
        tlv = ClazzUtils.ClazzTag(tag, tlv);
        return tlv;
    }

    public static TLV processTag(TLV tlv) {
        if (tlv == null) return null;
        tlv = processTag(tlv.getTag(), tlv);
        return tlv;
    }

    /**
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes    字节流
     * @param bytesLen 指定长度 ，bytesLen<=bytes.length
     * @return
     */
    private static List<TLV> bytes2SposTLVs(byte[] bytes, int bytesLen) {
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
        List<TLV> items = new ArrayList<TLV>();

        for (int i = start; i < bytesLen; ) {
            // parse tag
            TLV tlv = new TLV();
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
            if (justSpos(tag)) {
                byte[] value = new byte[len];
                System.arraycopy(bytes, i, value, 0, len);
                tlv.setTag(tag);
                tlv.setLength(len);
                tlv.setValue(value);
                // process sposTLV
                tlv = processTag(tag, tlv);
                if (tlv != null) {
                    items.add(tlv);
                }
            }
        }
        return items;
    }

    private static byte[] filterSposBytes(byte[] bytes) {
        // 长度校验
        if (bytes == null || bytes.length < 6) {
            return null;
        }
        // 剔除无效数据
        int pos = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0x00 || (bytes[i] & 0xFF) == 0xFF) {
                pos = i;
                //continue;
            } else {
                break;
            }
        }
        int bytesLen = bytes.length;
        int len = bytesLen - pos;
        if (len <= 6) {
            return null;
        }
        byte[] dest = new byte[len];
        System.arraycopy(bytes, pos, dest, 0, len);
        return dest;
    }

    private static List<TLV> bytes2FlatTLVs(byte[] bytes) {
//        System.out.println("bytes2FlatTLVs bytes="+ByteStringHex.bytes2HexStr(bytes));
        if (bytes == null || bytes.length < 6) {
            return null;
        }
        bytes = filterSposBytes(bytes);
        List<TLV> flatTLVs = new ArrayList<TLV>();
        int fatherTag = 0, pos = 0;
        flatTLVs = (parseBytes(bytes, flatTLVs, fatherTag, pos));
//        System.out.println("bytes2FlatTLVs flatTLVS="+flatTLVs.get(0));
        return flatTLVs;
    }

    private static List<TLV> parseBytes(byte[] bytes, List<TLV> flatTLVs, int fatherTag, int pos) {
        if (bytes == null || bytes.length < 6) {
            return null;
        }
        if (bytes.length - pos < 6) {
            return flatTLVs;
        }
        //todo bug
//        bytes=filterSposBytes(bytes);
        int bytesLen = bytes.length;
        int tag;
        int len;
        int lenBytes = 1;// length 对应的bytes2Int
        //List<TLV> items = new ArrayList<TLV>();
        // parse tag
        TLV tlv = new TLV();
        byte[] tagBytes = new byte[4];
        System.arraycopy(bytes, pos, tagBytes, 0, 4);
        pos += 4;
        tag = ByteStringHex.bytes2Int(tagBytes);
        if (!justSpos(tag)) {
            return flatTLVs;
        }
        if ((bytes[pos] & 0x80) == 0x80) {
            lenBytes = (bytes[pos] & 0x7F);
            //假定length最多占用5个byte
            if (lenBytes > 4) {
                return flatTLVs;
            }
            byte[] lenValue = new byte[lenBytes];
            len = ByteStringHex.bytes2Int(lenValue);
        } else {
            len = bytes[pos];
        }
        byte[] value = new byte[len];
        System.arraycopy(bytes, pos + 1, value, 0, len);
        pos += lenBytes;
//            pos = i;
        tlv.setValue(value);
        tlv.setTag(tag);
        tlv.setLength(len);
        tlv.setFatherTag(fatherTag);
        tlv = processTag(tlv);

//        System.out.println("---tlv="+tlv);
//        System.out.println("---tlv.tag="+ByteStringHex.int2HexStr(tag));
//        System.out.println("---tlv.isConstructed="+tlv.isConstructed());
//        System.out.println("---pos="+pos);
        //todo
        if (tlv.isConstructed()) {
            flatTLVs.add(tlv);
            fatherTag = tlv.getTag();
//            System.out.println("======parse tlv.tag:" + tag);
        } else {
            flatTLVs.add(tlv);
            pos += tlv.getValue().length;
        }
        if (bytesLen - pos >= 6) {
//            System.out.println("---parse Bytes:" + flatTLVs);
//            System.out.println("---parse fatherTag:" + ByteStringHex.int2HexStr(fatherTag));

//            System.out.println("----------------------byteLen=" + bytesLen);
//            System.out.println("----------------------    pos=" + pos);
            flatTLVs = (parseBytes(bytes, flatTLVs, fatherTag, pos));
        }
        // System.out.println("===parse Bytes:"+flatTLVs);
        return flatTLVs;
    }

    /**
     * 根据 value bytes 递归处理 原始 sposTLV 对象，确立 fatherFlag ,转为flatTLVs, 但是subTLVs不处理
     * sposTLV.isConstructed() 为判断依据
     *
     * @param tlv      入参
     * @param flatTLVs 出参
     */
    //todo maybe bug
    private static List<TLV> sposTLV2FlatTLVs(TLV tlv, List<TLV> flatTLVs) {
        if (tlv == null) {
            return flatTLVs;
        }
//        if (!justConstructed(tlv.getTag())) {
        if (!flatTLVs.contains(tlv)) {
            flatTLVs.add(tlv);
        }
//        }
        byte[] bytes = tlv.getValue();
        int fatherTag = tlv.getTag();

        List<TLV> tlvs = bytes2SposTLVs(bytes);
        if (tlvs == null || tlvs.size() < 1) {
            return flatTLVs;
        }
        for (TLV tlv2 : tlvs) {
            tlv2.setFatherTag(fatherTag);
            flatTLVs.add(tlv2);
            if (tlv2.isConstructed()) {
                //todo 递归未测试
                flatTLVs = sposTLV2FlatTLVs(tlv2, flatTLVs);
            }
        }
        return flatTLVs;
    }

    private static void sposTLVs2FlatTLVs(List<TLV> tlvs, List<TLV> flatTLVs) {
        if (tlvs == null) {
            return;
        }
        for (TLV tlv : tlvs) {

            if (!flatTLVs.contains(tlv)) {
                flatTLVs.add(tlv);
                tlvs.remove(tlv);
            }
            byte[] bytes = tlv.getValue();

            List<TLV> tlvs2 = bytes2SposTLVs(bytes);
            if (tlvs2 == null || tlvs2.size() < 1) {
                return;
            } else {
                //todo bug
                sposTLVs2FlatTLVs(tlvs2, flatTLVs);
            }
        }
    }

    /**
     * 处理原始 sposTLVs ，转为flatTLVs
     *
     * @param TLVs
     * @return flatTLVs
     */
    public static List<TLV> sposTLVs2FlatTLVs(List<TLV> TLVs) {
        if (TLVs == null || TLVs.size() < 1) {
            return null;
        }
        List<TLV> items = new ArrayList<TLV>();
        for (TLV tlv : TLVs) {
            List<TLV> tmp2 = new ArrayList<TLV>();
            sposTLV2FlatTLVs(tlv, tmp2);
            if (!tmp2.isEmpty()) {
                items.addAll(tmp2);
            }
        }
        return items;
    }

    private static List<TLV> flatTLVs2NestedTLVs(List<TLV> flatTLVs) {
        if (flatTLVs == null) return null;
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            dest.add(tlv);
        }
        makeflatTLVsNested(dest, flatTLVs);
        return dest;
    }


    /**
     * 查找函数 根据tag int 查找, 无匹配返回null
     *
     * @param tag
     * @param flatTLVs
     * @return TLVs
     */
    public static List<TLV> findByTag(int tag, List<TLV> flatTLVs) {
        if (!justSpos(tag)) {
            return null;
        }
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        List<TLV> items = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            if (tag == tlv.getTag()) {
                items.add(tlv);
            }
        }
        if (items.size()==0){return null;}
        return items;
    }

    private static TLV getTLVNotArray(int tag, List<TLV> flatTLVs) {
        if (!justSpos(tag)) {
            return null;
        }
        //数组 判断
        if (justArray(tag)) {
            return null;
        }
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        //假定 数组里没有重复对象，如果重复，取第一个
        for (TLV tlv : flatTLVs) {
            if (tag == tlv.getTag()) {
                return tlv;
            }
        }
        return null;
    }

    private static List<TLV> getTLVsNotArray(List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            if (!justArray(tlv.getTag()) && justSpos(tlv.getTag())) {
                dest.add(tlv);
            }
        }
        if (dest.size() < 1) {
            return null;
        }
        return dest;
    }

    private static int[] getTLVIsArrayNums(List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }

        Map<Integer, TLV> map = new HashMap<Integer, TLV>();
        for (TLV tlv : flatTLVs) {
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

    private static List<TLV> getTLVsIsArray(List<TLV> flatTLVs) {
        int[] tags = getTLVIsArrayNums(flatTLVs);
        if (tags.length < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (int i = 0; i < tags.length; i++) {
            dest.addAll(getTLVsIsArray(tags[i], flatTLVs));
        }
        return dest;
    }

    private static List<TLV> getTLVsNoFather(List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            if (tlv.getFatherTag() == 0 && justSpos(tlv.getTag())) {
                dest.add(tlv);
            }
        }
        if (dest.size() < 1) {
            return null;
        }
        return dest;
    }

    private static List<TLV> getTLVsNotConstructed(List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            if (!justConstructed(tlv.getTag())) {
                dest.add(tlv);
            }
        }
        if (dest.size() < 1) {
            return null;
        }
        return dest;
    }

    private static void sortTLVs(List<TLV> flatTLVs) {
        Collections.sort(flatTLVs, new TLVComparator());
    }

    private static List<TLV> getTLVsNotConstructedAndIsArray(List<TLV> flatTLVs) {
        List<TLV> TLVs = getTLVsNotConstructed(flatTLVs);
        if (TLVs == null || TLVs.size() < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : TLVs) {
            if (justArray(tlv.getTag())) {
                dest.add(tlv);
            }
        }
        if (dest.size() < 1) {
            return null;
        }
        return dest;
    }

    private static List<TLV> getTLVsIsArray(int tag, List<TLV> flatTLVs) {
        //数组 判断
        if (!justArray(tag)) {
            return null;
        }
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        List<TLV> items = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            if (tag == tlv.getTag()) {
                items.add(tlv);
            }
        }
        return items;
    }

    public static List<TLV> getFatherTLVs(int fatherTag, List<TLV> TLVs) {
        if (!justSpos(fatherTag)) {
            return null;
        }
        //数组 判断
        if (justArray(fatherTag)) {
            return null;
        }
        if (TLVs == null || TLVs.size() < 1) {
            return null;
        }
        List<TLV> items = new ArrayList<TLV>();
        //假定 数组里没有重复对象，如果重复，取第一个
        for (TLV tlv : TLVs) {
            if (fatherTag == tlv.getTag()) {
                items.add(tlv);
                return items;
            }
        }
        return null;
    }

    public static TLV getFatherTLV(int fatherTag, List<TLV> TLVs) {
        if (!justSpos(fatherTag)) {
            return null;
        }
        if (TLVs == null || TLVs.size() < 1) {
            return null;
        }
        List<TLV> items = new ArrayList<TLV>();

        for (TLV tlv : TLVs) {
            if (fatherTag == tlv.getTag()) {
                items.add(tlv);
            }
        }
        if (items.size() < 1) return null;
//        if (items.size()==1)return items.get(0);
        //Collections.sort(items,new TLVComparator());
        return items.get(items.size() - 1);
    }

    public static void addSubTLV(TLV srcTLV, TLV destTLV) {
        if (srcTLV == null || destTLV == null) return;

        List<TLV> fatherSubTLVs = destTLV.getSubTLVs();
        srcTLV.setFatherTag(destTLV.getTag());
        if (fatherSubTLVs == null) {
            fatherSubTLVs = new ArrayList<TLV>();
            fatherSubTLVs.add(srcTLV);
            destTLV.setSubTLVs(fatherSubTLVs);
        } else {
//            if (fatherSubTLVs.contains(srcTLV)) return;
            fatherSubTLVs.add(srcTLV);
            destTLV.setSubTLVs(fatherSubTLVs);
        }
//        destTLV.setValue(null);
        destTLV.setConstructed(true);
        if (destTLV.getTag() != 0) {
            byte[] tagBytes = ByteStringHex.int2Bytes(destTLV.getTag());
            byte hi4bit = (byte) (0xF0 & tagBytes[0]);
            if (hi4bit == (byte) 0xC0) {
                tagBytes[0] = (byte) (0x0F & tagBytes[0]);
                tagBytes[0] = (byte) (0xE0 | tagBytes[0]);
                destTLV.setTag(ByteStringHex.bytes2Int(tagBytes));
            }


        }
//        return destTLV;
    }

    private static List<TLV> addSubTLVs(TLV srcTLV, List<TLV> destTLVs) {
        if (srcTLV == null || destTLVs == null) return null;
        int tag, fatherTag;
        tag = srcTLV.getTag();
        fatherTag = srcTLV.getFatherTag();
        if (fatherTag == 0) {
            return destTLVs;
        }
        TLV fatherTLV = getFatherTLV(fatherTag, destTLVs);
        if (fatherTLV == null) {
            return destTLVs;
        }

        List<TLV> fatherSubTLVs = fatherTLV.getSubTLVs();
        if (fatherSubTLVs == null) {
            fatherSubTLVs = new ArrayList<TLV>();
            fatherSubTLVs.add(0, srcTLV);
        } else {
            if (fatherSubTLVs.contains(srcTLV)) return destTLVs;
            fatherSubTLVs.add(0, srcTLV);
        }
        fatherTLV.setSubTLVs(fatherSubTLVs);
        int index = destTLVs.indexOf(fatherTLV);
        destTLVs.set(index, fatherTLV);
        return destTLVs;

    }

    private static List<TLV> makeflatTLVsNested(List<TLV> nestedTLVs, List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
//        if (nestedTLVs == null || nestedTLVs.size() < 1) {
//            return ;
//        }
        //for(int i=flatTLVs.size();i>0;i--){
        //}
        int pos = flatTLVs.size();
        pos -= 1;
        TLV tlv = flatTLVs.get(pos);
        if (tlv.getFatherTag() != 0) {
            addSubTLVs(tlv, nestedTLVs);
            flatTLVs.remove(pos);
            makeflatTLVsNested(nestedTLVs, flatTLVs);
        } else {
            //todo
            return nestedTLVs;
        }
        return nestedTLVs;
    }

    /**
     * bytes2SposTLVS 的多态
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes 字节流
     * @return sposTLV
     */
    private static List<TLV> bytes2SposTLVs(byte[] bytes) {
        return bytes2SposTLVs(bytes, bytes.length);
    }

    /**
     * bytes 转为嵌套的tlv, 包含多叉树结构的的所有节点TLV对象
     *
     * @param bytes
     * @return 多叉树结构的的所有节点TLV对象
     */
    public static List<TLV> bytes2NestedFlatTLVs(byte[] bytes) {
        List<TLV> flatTLVs = bytes2FlatTLVs(bytes);
        if (flatTLVs == null || flatTLVs.size() == 1) return flatTLVs;
        return flatTLVs2NestedTLVs(flatTLVs);
    }

    /**
     * bytes 转为嵌套的tlv, 不包含多叉树结构的的子节点TLV对象
     *
     * @param bytes
     * @return 不包含多叉树结构的的子节点TLV对象
     */
    public static List<TLV> bytes2TopNestedTLVs(byte[] bytes) {
        List<TLV> TLVs = bytes2NestedFlatTLVs(bytes);
        if (TLVs == null || TLVs.size() == 1) return TLVs;
        return getTLVsNoFather(TLVs);
    }

    //-------------------object to TLV bytes---------------

    /**
     * tlv.length 转 bytes
     *
     * @param len tlv.length
     * @return byte[]
     */
    private static byte[] TLVLenth2Bytes(int len) {
        if (len < 0) return null;
        //len 不能超过 0x7FFFFFFF, 即2048M, 也不能为0
        if (len >= 0x7FFFFFFF || len == 0) {
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
//            return ByteStringHex.int2BytesN(len);
        }
    }

    /**
     * tag/value不为空的TLV 转 bytes
     *
     * @param tlv
     * @return byte[]
     */
    private static byte[] hasValueTLV2Bytes(TLV tlv) {
//        if (tlv == null || !justSpos(tlv.getTag()) || tlv.getFatherTag() > 0) {
//            return null;
//        }
        if (tlv.getValue() == null || tlv.getValue().length < 1) { //            return null;
            return null;
        }
//        tlv.length 可以为0, 但是 如果不为零，并且不等于value的长度，视为非法
//        if (tlv.getLength() != tlv.getValue().length && tlv.getLength() != 0) {
//            return null;
//        }
        int tlv_l = tlv.getValue().length;
        byte[] lenBytes = TLVLenth2Bytes(tlv_l);
        byte[] tagBytes = ByteStringHex.int2Bytes(tlv.getTag());
        int len = 4 + lenBytes.length + tlv_l;
        tlv.setLength(len);
        byte[] dest = new byte[len];
        System.arraycopy(tagBytes, 0, dest, 0, 4);
        System.arraycopy(lenBytes, 0, dest, 4, lenBytes.length);
        System.arraycopy(tlv.getValue(), 0, dest, 4 + lenBytes.length, tlv_l);
        return dest;
    }

    private static byte[] noValueTLV2Bytes(TLV tlv, int sonBytesLen) {
        if (tlv == null) {
//            if (tlv == null || !justSpos(tlv.getTag()) || !justConstructed(tlv.getTag()) || tlv.getValue() != null) {
            return null;
        }
//        byte[] lenBytes = TLVLenth2Bytes(tlv.getLength()+sonBytesLen);
        byte[] lenBytes = TLVLenth2Bytes(sonBytesLen);

        byte[] tagBytes = ByteStringHex.int2Bytes(tlv.getTag());
        int len;
        if (lenBytes == null || lenBytes.length == 0) {

            len = 4 + 1;
        } else {
            len = 4 + lenBytes.length;
        }
        byte[] dest = new byte[len];
        System.arraycopy(tagBytes, 0, dest, 0, 4);
        if (lenBytes != null) {
            System.arraycopy(lenBytes, 0, dest, 4, lenBytes.length);
        }
//        System.arraycopy(lenBytes, 0, dest, 4, 16);
//        System.arraycopy(tlv.getValue(), 0, dest, 4 + lenBytes.length, sonBytesLen);
//        System.out.println("~~~ -----noValueTLV2Bytes dest=" + ByteStringHex.bytes2HexStr(dest));
        return dest;
    }

    private static byte[] insertBytes2Front(byte[] src, byte[] dest) {
        if (src == null || src.length < 1) {
            return null;
        }
        if (dest == null) {
            return null;
        }
        int srcLen = src.length;
        int destLen = dest.length;
        byte[] tmp = new byte[srcLen + destLen];
        System.arraycopy(dest, 0, tmp, srcLen, destLen);
        System.arraycopy(src, 0, tmp, 0, srcLen);
        return tmp;
    }

    private static ArrayList<Byte> insertBytes2ArrayFront(byte[] src, ArrayList<Byte> dest) {
        if (src == null || src.length < 1) {
            return dest;
        }
        if (dest == null) {
//            return null;
            dest = new ArrayList<Byte>();
        }
        ArrayList<Byte> srcArray = ByteStringHex.Bytes2ArrayBytes(src);
        ArrayList<Byte> tmp = new ArrayList<Byte>();
        for (Byte b : srcArray) {
            tmp.add(b);
        }
        for (Byte b : dest) {
            tmp.add(b);
        }
//        tmp.addAll(srcArray);
//        tmp.addAll(dest);
//        System.out.println("insertBystes2Array front. tmp=" + ByteStringHex.bytes2HexStr(ByteStringHex.ArrayBytes2Bytes(tmp)));
        return tmp;
    }

    private static ArrayList<Byte> parseTLVs(List<TLV> flatTLVs, ArrayList<Byte> bytes, int sonsLen) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return bytes;
        }
//        ArrayList<Byte> dest=new ArrayList<Byte>();
//        for (Byte b:bytes){
//            dest.add(b);
//        }
//        if (bytes == null) bytes = new byte[0];
        int len = flatTLVs.size();
        TLV tlv = flatTLVs.get(len - 1);
//        System.out.println("-----parseTLVs. tlv =" + ByteStringHex.int2HexStr(tlv.getTag()));
//        if (tlv.getFatherTag()!=0){
        // if (tlv.getSubTLVs()!=null){tlv.setValue(null);}
        if (tlv.isConstructed()) {
            tlv.setValue(null);
        }


        if (tlv.getValue() == null || tlv.isConstructed()) {
//            System.out.println ("===============================");
            if (tlv.isConstructed()) {
            }
            if (tlv.getValue() == null) {

                sonsLen += 0;

            } else {
                sonsLen += tlv.getValue().length;
            }
            TLV fatherTLV = getFatherTLV(tlv.getTag(), flatTLVs);
            int len2 = fatherTLV.getLength() + sonsLen;
//            int len2=fatherTLV.getLength()+ TLVLenth2Bytes(fatherTLV.getLength()).length+4;
//            int len2=TLVLenth2Bytes(fatherTLV.getLength()).length+4;
            fatherTLV.setLength(len2);
            int pos = flatTLVs.indexOf(fatherTLV);

            flatTLVs.set(pos, fatherTLV);

            byte[] addBytes = noValueTLV2Bytes(tlv, sonsLen);
            flatTLVs.remove(len - 1);
            if (addBytes != null && addBytes.length > 1) {
                bytes = insertBytes2ArrayFront(addBytes, bytes);
                sonsLen += addBytes.length;
//                System.out.println("======addbytes="+ByteStringHex.bytes2HexStr(addBytes));
            }
            if (flatTLVs.size() == 0) {
                return bytes;
            }
//            System.out.println("parseTLVs.10 bytes =" + ByteStringHex.bytes2HexStr(ByteStringHex.ArrayBytes2Bytes(bytes)));
        }
        if (tlv.getValue() != null) {
            if (tlv.isConstructed()) {
                //  sonsLen+=tlv.getValue().length;
            }
            byte[] addBytes = hasValueTLV2Bytes(tlv);
            if (addBytes != null && addBytes.length > 1) {
                bytes = insertBytes2ArrayFront(addBytes, bytes);
            }
            flatTLVs.remove(len - 1);
            sonsLen += addBytes.length;
            if (tlv.isConstructed()) {
//                    sonsLen+=tlv.getValue().length;
                TLV fatherTLV = getFatherTLV(tlv.getTag(), flatTLVs);
//                int len2=fatherTLV.getLength()+sonsLen;
                int len2 = fatherTLV.getLength() + addBytes.length;
                fatherTLV.setLength(len2);
//                System.out.println("-------len2="+len2);
                int pos = flatTLVs.indexOf(fatherTLV);

                flatTLVs.set(pos, fatherTLV);

            }
        }
        if (flatTLVs.size() == 0) {
            return bytes;
        }
        if (flatTLVs.size() > 0) {
            bytes = parseTLVs(flatTLVs, bytes, sonsLen);
//            parseTLVs(flatTLVs, dest, sonsLen);
        }
//        System.out.println("parseTLVs.11 bytes =" + ByteStringHex.bytes2HexStr(ByteStringHex.ArrayBytes2Bytes(bytes)));
        return bytes;
//        System.out.println("parseTLVs. tlv.getvalue="+ tlv);
    }

    /**
     * 根据 subTLVs 递归处理 原始 TLV 对象 ,转为flatTLVs, 但是value不处理
     * sposTLV.isConstructed() 为判断依据
     *
     * @param tlv      入参
     * @param flatTLVs 出参
     */
    private static List<TLV> TLV2FlatTLVs2(TLV tlv, List<TLV> flatTLVs) {
        if (tlv == null || tlv.getTag() == 0) {
            return flatTLVs;
        }
        if (!flatTLVs.contains(tlv)) {
            tlv = processTag(tlv);
            flatTLVs.add(tlv);
//                System.out.println("TLV2FlatTLVs: tlv="+ ByteStringHex.int2HexStr(tlv.getTag()));
        }

        if (tlv.isConstructed()) {

            flatTLVs = TLV2FlatTLVs2(tlv, flatTLVs);
        }
        //     return flatTLVs;
        int fatherTag = tlv.getTag();
//        System.out.println("TLV2FlatTLVs: tag="+ ByteStringHex.int2HexStr(tlv.getTag()));
        List<TLV> tlvs = tlv.getSubTLVs();
        if (tlvs == null || tlvs.size() < 1) {
            return flatTLVs;
        }
        for (TLV tlv2 : tlvs) {
            tlv2.setFatherTag(fatherTag);
            tlv2 = processTag(tlv2);

            if (!flatTLVs.contains(tlv2)) {
                flatTLVs.add(tlv2);
            }
//            flatTLVs.add(tlv2);
            if (tlv2.isConstructed()) {
//                if (tlv2.getSubTLVs()!=null) {
                //todo 递归未测试
                flatTLVs = TLV2FlatTLVs2(tlv2, flatTLVs);
            }
        }
//        System.out.println("--TLV2FlatTLVs flatTLVs size="+flatTLVs.size());
//        System.out.println("--TLV2FlatTLVs flatTLVs size="+flatTLVs.get(0));
        if (tlv.isConstructed()) {

            flatTLVs = TLV2FlatTLVs2(tlv, flatTLVs);
        }
        return flatTLVs;
    }

    private static List<TLV> TLV2FlatTLVs(TLV tlv) {

        List<TLV> flatTLVs = new ArrayList<TLV>();
        if (tlv == null || tlv.getTag() == 0) {
            return flatTLVs;
        }
        flatTLVs.add(tlv);
        List<TLV> subTLVs = tlv.getSubTLVs();
        if (subTLVs == null) return flatTLVs;
        for (TLV tlv1 : subTLVs) {
            flatTLVs.addAll(TLV2FlatTLVs(tlv1));
        }
        return flatTLVs;
    }

    private static List<TLV> TLVs2FlatTLVs(List<TLV> TLVs) {
        if (TLVs == null || TLVs.size() < 1) {
            return null;
        }
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : TLVs) {
//            TLV2FlatTLVs2(tlv, dest);
            dest.addAll(TLV2FlatTLVs(tlv));
        }
        return dest;
    }

    public static byte[] TLV2Bytes(TLV nestedTLV) {
        List<TLV> flatTLVs = new ArrayList<TLV>();
        flatTLVs = TLV2FlatTLVs(nestedTLV);
//        System.out.println("--TLV2Bytes flatTLVs size=" + flatTLVs.size());
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return null;
        }
        //byte[] dest = new byte[0];
        ArrayList<Byte> dest = new ArrayList<Byte>();
//        byte[] dest = new byte[0];
        int sonsLen = 0;
        dest = parseTLVs(flatTLVs, dest, sonsLen);
//        parseTLVs(flatTLVs, dest, sonsLen);
        return ByteStringHex.ArrayBytes2Bytes(dest);
    }

    public static byte[] TLVs2Bytes(List<TLV> nestedTLVs) {
        if (nestedTLVs == null || nestedTLVs.size() < 1) {
            return null;
        }
        List<TLV> flatTLVs = new ArrayList<TLV>();
        flatTLVs = TLVs2FlatTLVs(nestedTLVs);
//        System.out.println("---flatTLVs size"+flatTLVs.size());
//        int sonsLen = 0;
//        byte[] dest = new byte[0];
//        ArrayList<Byte> dest = new ArrayList<Byte>();
//        parseTLVs(flatTLVs, dest, 0);
//        parseTLVs(flatTLVs, dest, 0);
//        return ByteStringHex.ArrayBytes2Bytes(dest);
        ArrayList<Byte> dest = new ArrayList<Byte>();
//        byte[] dest = new byte[0];
        int sonsLen = 0;
        dest = parseTLVs(flatTLVs, dest, sonsLen);
//        parseTLVs(flatTLVs, dest, sonsLen);
        return ByteStringHex.ArrayBytes2Bytes(dest);
    }

}
