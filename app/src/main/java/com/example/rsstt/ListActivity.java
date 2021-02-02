package com.example.rsstt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
RSSFeed rss;
ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lv = (ListView)findViewById(R.id.list_view);
        Bundle arguments = getIntent().getExtras();
        String title = arguments.get("title").toString();
        rss = (RSSFeed) arguments.getSerializable("rss");
        getTitle(rss);
        setTitle(title);
    }

    private ArrayList<String> getTitle(RSSFeed array){
        ArrayList<String> resultList=new  ArrayList<String>(array.getArticleList().size());
        for (int i=0; i<array.getArticleList().size(); i++){

            resultList.add(i, array.getArticleList().get(i).getTitle());
        }
        return resultList;

    }
}