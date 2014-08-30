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
        byte isConstructed4bit = (byte) ((byteTag[0] & 0xF0) >> 4);
        return justConstructed(isConstructed4bit);
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
        byte isArray4bit = (byte) ((byteTag[3] & 0xF0) >> 4);
        return justArray(isArray4bit);
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
            default:
                return null;
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
        tlv = SortUtils.SortTag(tag, tlv);
        return tlv;
    }

    /**
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes    字节流
     * @param bytesLen 指定长度 ，bytesLen<=bytes.length
     * @return
     */
    public static List<TLV> bytes2SposTLVs(byte[] bytes, int bytesLen) {
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
    private static byte[] filterSposBytes(byte[] bytes){
        // 长度校验
        if (bytes == null || bytes.length<6) {
            return null;
        }
        // 剔除无效数据
        int start = 0;
        for (int i = 0; i < bytes.length; i++) {
            start++;
            if (bytes[i] == 0x00 || (bytes[i] & 0xFF) == 0xFF) {
                continue;
            } else {
                break;
            }
        }
        int bytesLen=bytes.length;
        int len=bytesLen-start;
        if (len<=6) {
            return null;
        }
        byte[] dest=new byte[len];
        System.arraycopy(bytes,start,dest,0,len);
        return dest;
    }
    public static List<TLV> bytes2FlatTLVs (byte[] bytes){
        bytes=filterSposBytes(bytes);
        if (bytes==null||bytes.length<6){return null;}
        List<TLV> flatTLVs=new ArrayList<TLV>();
        int fatherTag = 0,pos=0;
        parseBytes(bytes,flatTLVs,fatherTag,pos);
        return flatTLVs;
    }
    public static void parseBytes (byte[] bytes, List<TLV> flatTLVs,int fatherTag,int pos) {
        if(bytes==null){return;}
        if (pos>=bytes.length){return;}
        //bytes=filterSposBytes(bytes);
        int bytesLen=bytes.length;
        //内容校验
        byte[] bytesTag=new byte[4];
        if (!justSpos(ByteStringHex.bytes2Int(bytesTag))) {
            return ;
        }
        int tag;
        int len;
        int lenBytes;// length 对应的bytes2Int
        //List<TLV> items = new ArrayList<TLV>();

        for (int i = 0; i < bytesLen; ) {
            // parse tag
            TLV tlv = new TLV();
            byte[] tagBytes = new byte[4];
            System.arraycopy(bytes, 0, tagBytes, 0, 4);
            tag = ByteStringHex.bytes2Int(tagBytes);
            i += 4;
            if ((bytes[i] & 0x80) == 0x80) {
                lenBytes = (bytes[i] & 0x7F);
                //假定length最多占用5个byte
                if (lenBytes > 4) {
                    return ;
                }
                byte[] lenValue = new byte[lenBytes];
                len = ByteStringHex.bytes2Int(lenValue);
                i += 1 + lenBytes;
            } else {
                len = bytes[i];
                i += 1;
            }
                byte[] value = new byte[len];
                System.arraycopy(bytes, i, value, 0, len);
                i+=len;
                pos=i;
           //todo
            if (justConstructed(tag)) {
                tlv.setTag(tag);
                tlv.setLength(len);
                tlv.setValue(value);
                tlv.setFatherTag(fatherTag);
                // process sposTLV
                tlv = processTag(tag, tlv);
                if (tlv != null && !flatTLVs.contains(tlv)) {
//                    items.add(tlv);
                    flatTLVs.add(tlv);
                    if(tlv.isConstructed()){
                        flatTLVs.add(tlv);
                        fatherTag=tlv.getTag();
                    }
                }
                parseBytes(bytes,flatTLVs,fatherTag,pos);
            }else{
                fatherTag=0;
                tlv.setTag(tag);
                tlv.setLength(len);
                tlv.setValue(value);
                // process sposTLV
                tlv = processTag(tag, tlv);
                flatTLVs.add(tlv);
            }
        }
        return ;
    }

    /**
     * 递归处理 原始 sposTLV 对象，确立 fatherFlag ,转为flatTLVs
     * sposTLV.isConstructed() 为判断依据
     *
     * @param tlv      入参
     * @param flatTLVs 出参
     */
    //todo maybe bug
    private static void sposTLV2FlatTLVs(TLV tlv, List<TLV> flatTLVs) {
        if (tlv == null) {
            return;
        }
        if (!tlv.isConstructed()) {
            return;
        }
        if (!flatTLVs.contains(tlv)){
           flatTLVs.add(tlv);
        }
        byte[] bytes = tlv.getValue();
        int fatherTag = tlv.getTag();

        List<TLV> tlvs = bytes2SposTLVs(bytes);
        if (tlvs == null || tlvs.size() < 1) {
            return;
        }
        for (TLV tlv2 : tlvs) {
            tlv.setFatherTag(fatherTag);
            flatTLVs.add(tlv2);
            if (tlv2.isConstructed()) {
                //todo 递归未测试
                sposTLV2FlatTLVs(tlv2, flatTLVs);
            }
        }
    }

    private static void sposTLVs2FlatTLVs(List<TLV> tlvs, List<TLV> flatTLVs) {
        if (tlvs == null) {
            return;
        }
        for (TLV tlv : tlvs) {

        if (!flatTLVs.contains(tlv)){
            flatTLVs.add(tlv);
            tlvs.remove(tlv);
        }
        byte[] bytes = tlv.getValue();
        int fatherTag = tlv.getTag();

        List<TLV> tlvs2 = bytes2SposTLVs(bytes);
        if (tlvs2 == null || tlvs2.size() < 1) {
            return;
        }else{
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

    public static List<TLV> flatTLVs2NestedTLVs(List<TLV> flatTLVs) {
        if (flatTLVs == null) return null;
        List<TLV> dest = new ArrayList<TLV>();
        for (TLV tlv : flatTLVs) {
            dest.add(tlv);
        }
        makeflatTLVsNested(dest, flatTLVs);
        return dest;
    }

    /**
     * 多叉树结构的tlvbytes 转为 flatTLV 对象数组
     *
     * @param bytes
     * @return spostlv list 包含多叉树的所有节点，但是subTLVs为空。
     */
    public static List<TLV> bytes2FlatTLVs2(byte[] bytes) {
        //假定第一层 没有fatherTag
        List<TLV> tlvs = bytes2SposTLVs(bytes);
        if (tlvs == null) {
            return null;
        }
        return sposTLVs2FlatTLVs(tlvs);
    }

    /**
     * 查找函数 根据tag int 查找
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
        return items;
    }

    public static TLV getTLVNotArray(int tag, List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsNotArray(List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsIsArray(List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsNoFather(List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsNotConstructed(List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsNotConstructedAndIsArray(List<TLV> flatTLVs) {
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

    public static List<TLV> getTLVsIsArray(int tag, List<TLV> flatTLVs) {
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

    //    public static TLV getFatherTLV(int fatherTag, List<TLV> TLVs) {
//        if (!justSpos(fatherTag)) {
//            return null;
//        }
//        //数组 判断
//        if (justArray(fatherTag)) {
//            return null;
//        }
//        if (TLVs == null || TLVs.size() < 1) {
//            return null;
//        }
//        //假定 数组里没有重复对象，如果重复，取第一个
//        for (TLV tlv : TLVs) {
//            if (fatherTag == tlv.getTag()) {
//                return tlv;
//            }
//        }
//        return null;
//    }
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

    //todo maybe remove
//    private static boolean combineNotConstructedArray2Nested(List<TLV> flatTLVs, List<TLV> arrays) {
//        if (arrays == null || arrays.size() < 1) {
//            return false;
//        }
//        if (flatTLVs == null || flatTLVs.size() < 1) {
//            return false;
//        }
//        int[] nums = getTLVIsArrayNums(arrays);
//        if (nums == null || nums.length < 1) {
//            return false;
//        }
//        for (int i = 0; i < nums.length; i++) {
//            List<TLV> items = new ArrayList<TLV>();
////           if (nums[i]==arrays)
//            for (TLV tlv : arrays) {
//                if (nums[i] == tlv.getTag()) {
//                    items.add(tlv);
//                }
//            }
//            TLV fatherTLV = getFatherTLV(nums[i], flatTLVs);
//            if (fatherTLV != null) {
//                fatherTLV.setSubTLVs(items);
//                int pos = flatTLVs.indexOf(fatherTLV);
//                flatTLVs.set(pos, fatherTLV);
//            }
//
//        }
//        return false;
//    }

    private static void addSubTLVs(TLV srcTLV, TLV destTLV) {
        if (srcTLV == null || destTLV == null) return;
        int tag, fatherTag;
        tag = srcTLV.getTag();
        fatherTag = srcTLV.getFatherTag();
        if (fatherTag == 0) {
            return;
        }
        List<TLV> fatherSubTLVs = destTLV.getSubTLVs();
        if (fatherSubTLVs == null) {
            fatherSubTLVs = new ArrayList<TLV>();
            fatherSubTLVs.add(srcTLV);
        } else {
            if (fatherSubTLVs.contains(srcTLV)) return;
            fatherSubTLVs.add(srcTLV);
        }

    }

    private static void addSubTLVs(TLV srcTLV, List<TLV> destTLVs) {
        if (srcTLV == null || destTLVs == null) return;
        int tag, fatherTag;
        tag = srcTLV.getTag();
        fatherTag = srcTLV.getFatherTag();
        if (fatherTag == 0) {
            return;
        }
        TLV fatherTLV = getFatherTLV(fatherTag, destTLVs);
        if (fatherTLV == null) {
            return;
        }

        List<TLV> fatherSubTLVs = fatherTLV.getSubTLVs();
        if (fatherSubTLVs == null) {
            fatherSubTLVs = new ArrayList<TLV>();
            fatherSubTLVs.add(srcTLV);
        } else {
            if (fatherSubTLVs.contains(srcTLV)) return;
            fatherSubTLVs.add(srcTLV);
        }

    }

    private static void makeflatTLVsNested(List<TLV> nestedTLVs, List<TLV> flatTLVs) {
        if (flatTLVs == null || flatTLVs.size() < 1) {
            return;
        }
        if (nestedTLVs == null || nestedTLVs.size() < 1) {
            return;
        }
        //for(int i=flatTLVs.size();i>0;i--){
        //}
        int pos = flatTLVs.size();
        pos -= 1;
        TLV tlv = flatTLVs.get(pos);
        if (tlv.getFatherTag()!=0) {
            addSubTLVs(tlv, nestedTLVs);
            flatTLVs.remove(pos);
            makeflatTLVsNested(nestedTLVs, flatTLVs);
        }else{
            return;
        }
    }

    /**
     * bytes2SposTLVS 的多态
     * 字节流转换为 spostlv对象Array, array 包含一个或者多个spostlv对象
     *
     * @param bytes 字节流
     * @return sposTLV
     */
    public static List<TLV> bytes2SposTLVs(byte[] bytes) {
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
     * TLV 转 bytes ,tag/value为必须
     *
     * @param tlv
     * @return byte[]
     */
    public static byte[] TLV2Bytes(TLV tlv) {
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
