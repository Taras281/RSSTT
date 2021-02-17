package com.example.rsstt;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VogellaAPI {


    @GET("all.php")
    Call<RSSFeed> loadRSSFeedPHP();

    @GET("rss/news")
    Call<RSSFeed> loadRSSFeedNews();

    @GET("{news}")
    Call<RSSFeed> loadRSSFeed (@Path("news") String news);

    @GET("news.rss")
    Call<RSSFeed> loadRSSFeedNewsPointRss();
}