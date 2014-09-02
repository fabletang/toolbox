package com.pax.spos.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClazzUtilsTest {

    @Test
    public void testGetTagJson() throws Exception {
       TagJson tagJson=ClazzUtils.getTagJson();
       assertNotNull(tagJson);


    }

    @Test
    public void testClazzTag() throws Exception {
        // E2010310 CARD PdkCard_Read 出参CARD_INFO stCardInfo
        int tag=0xE2010310;
        TLV tlv=new TLV();
        tlv=ClazzUtils.ClazzTag(tag,tlv);
        assertNotNull(tlv.getClazz());
        assertEquals("stCardInfo",tlv.getPara());

    }
}