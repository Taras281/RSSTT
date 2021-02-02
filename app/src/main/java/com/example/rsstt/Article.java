package com.example.rsstt;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "item", strict = false)
public class Article implements Serializable {
    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;
    @Element(name = "description")
    private String description;

    @Element(name = "pubDate")
    private String pubDate;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }
    public String getpubDate() { return pubDate;
    }
}

