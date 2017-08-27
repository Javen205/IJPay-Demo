package com.ijpay.entity;

/**
 * Created by Javen on 2017/8/26.
 */
public class LearnResouce {
    private long id;
    private String author;
    private String title;
    private String url;

    public LearnResouce() {
    }

    public LearnResouce(String author, String title, String url) {
        this.author = author;
        this.title = title;
        this.url = url;
    }

    public LearnResouce(long id, String author, String title, String url) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
