package com.pax.spos.utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fable on 14-9-2.
 */
public class TagJsonClazz  implements Serializable {
    ArrayList<TagJsonFunc> funcs;
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

    public ArrayList<TagJsonFunc> getFuncs() {
        return funcs;
    }

    public void setFuncs(ArrayList<TagJsonFunc> funcs) {
        this.funcs = funcs;
    }
}
