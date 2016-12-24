package com.makinap.tineo.neotrack.model;

import java.util.UUID;

import io.realm.RealmModel;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by tineo on 28/10/16.
 */
@RealmClass
public class Configuration  implements RealmModel {

    @PrimaryKey
    @Index
    private String id = UUID.randomUUID().toString();
    private String tag;
    private String value;
    private String state;


    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
