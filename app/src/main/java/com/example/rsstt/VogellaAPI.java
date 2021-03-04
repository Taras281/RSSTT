package com.example.rsstt;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VogellaAPI {

    @GET("")
    Call<RSSFeed> loadRSS();

    @GET("all.php")
    Call<RSSFeed> loadRSSFeedPHP();

    @GET("rss/news")
    Call<RSSFeed> loadRSSFeedNews();

    @GET("{news}")
    Call<RSSFeed> loadRSSFeed (@Path("news") String news);

    @GET("news.rss")
    Call<RSSFeed> loadRSSFeedNewsPointRss();

    //https://zelenogradsk.online/home/city-news?format=feed&type=rss

    @GET("city-news?")
    Call<RSSFeed> loadRSSQery(@Query("format") String format, @Query("type") String type);


}