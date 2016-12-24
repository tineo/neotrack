package com.makinap.tineo.neotrack.model;

import java.util.UUID;

import io.realm.RealmModel;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by tineo on 27/10/16.
 */
@RealmClass
public class Photo implements RealmModel {

    @PrimaryKey
    @Index
    private String id = UUID.randomUUID().toString();
    private String trackId;
    private String uri;

    private byte[] image;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
