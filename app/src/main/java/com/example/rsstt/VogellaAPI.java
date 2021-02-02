package com.example.rsstt;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VogellaAPI {

    @GET("rss/news")
    Call<RSSFeed> loadRSSFeed( );

    @GET("{news}")
    Call<RSSFeed> loadRSSFeed (@Path("news") String news);


}