package com.sibangstudio.popularmovie.data;

import java.io.Serializable;

/**
 * Created by ibti on 11/13/17.
 */

public class TrailerData implements Serializable {

    private String id;
    private String key;
    private String name;
    private String site;
    private String size;
    private String type;

    public TrailerData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
