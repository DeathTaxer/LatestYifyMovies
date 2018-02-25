package com.clicsixdev.yifymovies;

/**
 * Created by aravind on 10-02-2018.
 */

public class Movie {
    private String name;
    private String url;
    private String img;

    public Movie(String name,String url,String img){
        this.name = name;
        this.url = url;
        this.img = img;
    }


    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImg() {
        return img;
    }
}
