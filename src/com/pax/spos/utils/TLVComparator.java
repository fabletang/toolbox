package com.pax.spos.utils;

import java.util.Comparator;

/**
 * Created by fable on 14-8-29.
 */
public class TLVComparator implements Comparator<TLV> {
    public int compare(TLV lhs, TLV rhs) {
        int result = 0;
        int tag1, tag2;
        //数组的tag是相同的
        tag1 = lhs.getTag();
        tag2 = rhs.getTag();
        if (tag1 - tag2 == 0) {
            //todo bug
            return 0;
        }
        //复合结构
        tag1 = lhs.getTag() & 0xF0000000;
        tag2 = rhs.getTag() & 0xF0000000;
        if (tag1 - tag2 != 0) {
            return tag1 - tag2;
        }
        int len1 = lhs.getLength();
        int len2 = rhs.getLength();
        result = len1 - len2;
        return result;
//             Collections.sort(list, new ValComparator());
    }
}
