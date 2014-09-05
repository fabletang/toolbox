package com.pax.spos.utils.tlv.model;

import java.io.Serializable;

/**
 * Created by fable on 14-9-2.
 */
public class TagJsonPara implements Serializable {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
