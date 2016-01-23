package com.anagualter.popularmovies2.model;


public class MovieTrailers {
    String id;
    String key;
    String name;
    String site;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieTrailers() {}

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
}
