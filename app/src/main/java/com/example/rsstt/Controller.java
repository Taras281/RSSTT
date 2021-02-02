package com.example.rsstt;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Controller implements  retrofit2.Callback<RSSFeed> {
    String BASE_URL;
    String RSS;
    RSSFeed rss;
    CallbackFromController obj;

    VogellaAPI vogellaAPI;
    Retrofit retrofit;
    Call<RSSFeed> call;

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
                .build();

        vogellaAPI = retrofit.create(VogellaAPI.class);
    }

    public void start() {
        call = vogellaAPI.loadRSSFeed(RSS);
        call.enqueue(this);
    }
    @Override
    public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
        if (response.isSuccessful()) {
            rss = response.body();
            Log.d("TAG", "     "+rss.getChannelTitle());
            obj.returnRss(rss);

        } else {

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
        String t="";
    }
}
