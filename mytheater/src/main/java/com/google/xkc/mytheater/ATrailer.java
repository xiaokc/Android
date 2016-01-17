package com.google.xkc.mytheater;

/**
 * Created by xkc on 1/16/16.
 */
public class ATrailer {
    private String id;
    private String site;

    public ATrailer(String id, String site) {
        this.id = id;
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
