package com.example.magedmilad.movieranker;

import java.io.Serializable;

/**
 * Created by magedmilad on 12/25/15.
 */
public class Trailer implements Serializable {
    public String name,key;

    public Trailer(String name , String key){
        this.name = name;
        this.key = key;
    }
}

