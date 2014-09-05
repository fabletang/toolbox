package com.pax.spos.utils.tlv;

import com.google.gson.Gson;
import com.pax.spos.utils.tlv.model.TagJson;

import java.io.*;

/**
 * Created by fable on 14-9-2.
 * 解释tag.json 文件
 * For ClazzUtils
 */
public class TagJsonUtils {
    private static TagJsonUtils instance = null;

    private TagJsonUtils() {
    }

    public static TagJsonUtils getInstance() {
        if (instance == null) {
            instance = new TagJsonUtils();
        }
        return instance;
    }

    public TagJson parseJson(String tagjsonPath) throws IOException {
        if (tagjsonPath == null || tagjsonPath.length() < 6 || !tagjsonPath.endsWith(".json")) {
            return null;
        }
//        FileReader file=new FileReader(tagjsonPath);
        //读jar包
//        if (!file.ready()){
//            file=new FileReader(new InputStreamReader(is).toString());
//            file=new FileReader(is.toString());
//        }
        InputStream is = this.getClass().getResourceAsStream(tagjsonPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        Gson gson = new Gson();
        if (!br.ready()) return null;
        return gson.fromJson(br, TagJson.class);
    }
}
