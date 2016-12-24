package com.makinap.tineo.neotrack.data;

/**
 * Created by tineo on 19/09/16.
 */
public class Config {

    private int id;
    private String tag;
    private String value;

    public Config(int id, String tag, String value) {
        this.id = id;
        this.tag = tag;
        this.value = value;
    }

    public Config() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
