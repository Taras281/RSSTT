package com.example.rsstt;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(name="rss", strict=false)
public class RSSFeed implements Serializable {


    @Element(name="title")
    @Path("channel")
    private String channelTitle;

    @ElementList(name="item", inline=true)
    @Path("channel")
    private List<Article> articleList;

    @Element(name="link")
    @Path("channel/image")
     //@Path("channel")
    private String image;


    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public List<Article> getArticleList() {        return articleList;    }

    public void setArticleList(List<Article> articleList) { this.articleList = articleList; }

    public String getImage() { return image; }
}
