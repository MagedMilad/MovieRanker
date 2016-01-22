package com.example.magedmilad.movieranker;

import java.io.Serializable;

/**
 * Created by magedmilad on 12/25/15.
 */
public class Review implements Serializable {
    public  String author,content;

    public Review(String author , String content){
        this.author = author;
        this.content = content;
    }
}
