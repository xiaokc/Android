package com.google.xkc.mytheater;

/**
 * Created by xkc on 1/16/16.
 */
public class AReview {
    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AReview(String author, String content) {

        this.author = author;
        this.content = content;
    }
}
