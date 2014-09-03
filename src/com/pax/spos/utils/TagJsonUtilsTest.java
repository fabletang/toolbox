package com.pax.spos.utils;

import com.pax.spos.utils.model.TagJson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagJsonUtilsTest {
    private TagJson tagJson;
    private TagJsonUtils tagJsonUtils;
    @Before
    public void setUp() throws Exception {
       tagJsonUtils=TagJsonUtils.getInstance();
       tagJson=tagJsonUtils.parseJson("tag.json");
    }
    @Test
    public void testGetGson() throws Exception {
     assertNotNull(tagJson);
     assertEquals("PdkCard_Read", tagJson.getClazzs().get(1).getFuncs().get(0).getName());
    }
}