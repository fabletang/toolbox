package com.pax.spos.utils.tlv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fable on 14-9-2.
 */
public class TagJsonFunc implements Serializable {
    String id;
    String name;
    ArrayList<TagJsonPara> paras;

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

    public ArrayList<TagJsonPara> getParas() {
        return paras;
    }

    public void setParas(ArrayList<TagJsonPara> paras) {
        this.paras = paras;
    }
}
