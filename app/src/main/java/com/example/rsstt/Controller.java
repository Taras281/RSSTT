package com.example.rsstt;

import android.util.Log;

import java.io.Serializable;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Controller implements  retrofit2.Callback<RSSFeed> {
    String BASE_URL;
    String RSS;
    RSSFeed rss;
    CallbackFromController obj;

    VogellaAPI vogellaAPI;
    Retrofit retrofit;
    Call<RSSFeed> call;
    OkHttpClient client;

    Controller() {}

    public void setRss(String rss){
        BASE_URL=rss;
    }
    public void setSufixRss(String sufixRss){
        RSS=sufixRss;
        creatRetrofit();
    }

    private void creatRetrofit(){

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)

                .addConverterFactory(SimpleXmlConverterFactory.create())

                //.client(client)
                .build();
        vogellaAPI = retrofit.create(VogellaAPI.class);
    }

    public void start() {
        call = vogellaAPI.loadRSSFeed(RSS);
        if (RSS.equals("rss/news"))
            call = vogellaAPI.loadRSSFeedNews();
        if (RSS.equals("all.php"))
            call = vogellaAPI.loadRSSFeedPHP();
        if (RSS.equals("news.rss"))
            call = vogellaAPI.loadRSSFeedNewsPointRss();

        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
        if (response.isSuccessful()) {
            rss = response.body();
            Log.d("TAG", "     "+rss.getChannelTitle());
            obj.returnRss(rss);

        } else {
            //call = vogellaAPI.loadRSSFeed();
            //call.enqueue(this);
            Log.d("TAG", " NO     "+response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<RSSFeed> call, Throwable t) {

        t.printStackTrace();
        Log.d("TAG", "  FAILURE   "+t.toString());
    }

    public RSSFeed getRSS(){
        if(rss!=null){
            return rss;
        }
        return null;
    }

    public void register(CallbackFromController obj){
        this.obj = obj;
        String name = this.obj.getClass().getName();
        String t="";
    }
}
