package com.heaton.baselibsample.bean;

import java.io.Serializable;

/**
 * description $desc$
 * created by jerry on 2019/5/28.
 */
public class Article implements Serializable {

    public String author;
    public String title;
    public String createDate;
    public String content;

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", createDate='" + createDate + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
