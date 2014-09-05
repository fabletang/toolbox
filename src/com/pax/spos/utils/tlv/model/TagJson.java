package com.pax.spos.utils.tlv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fable on 14-9-2.
 */
public class TagJson implements Serializable {
    ArrayList<TagJsonClazz> clazzs;

    public ArrayList<TagJsonClazz> getClazzs() {
        return clazzs;
    }

    public void setClazzs(ArrayList<TagJsonClazz> clazzs) {
        this.clazzs = clazzs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagJson)) return false;

        TagJson tagJson = (TagJson) o;

        if (clazzs != null ? !clazzs.equals(tagJson.clazzs) : tagJson.clazzs != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clazzs != null ? clazzs.hashCode() : 0;
    }
}
