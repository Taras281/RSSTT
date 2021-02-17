package com.example.rsstt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
RSSFeed rss;
ListView lv;
ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lv = (ListView)findViewById(R.id.list_view);
        Bundle arguments = getIntent().getExtras();
        String title = arguments.get("title").toString();
        rss = (RSSFeed) arguments.getSerializable("rss");

        String[] from = {"PUBDATE", "TITLE"};

        int[] to = {R.id.pub_date, R.id.title};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.layout_list, getTitle(rss));
        SimpleAdapter adapter = new SimpleAdapter(this, getHashMap(rss), R.layout.layout_item_list_castom, from, to);
        setTitle(title);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLink(rss).get(position)));
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> getTitle(RSSFeed array){
        ArrayList<String> resultList=new  ArrayList<String>(array.getArticleList().size());
        for (int i=0; i<array.getArticleList().size(); i++){
            resultList.add(i, array.getArticleList().get(i).getTitle());
        }
        return resultList;
    }
    private ArrayList<String> getLink(RSSFeed array){
        ArrayList<String> resultList=new  ArrayList<String>(array.getArticleList().size());
        for (int i=0; i<array.getArticleList().size(); i++){
            resultList.add(i, array.getArticleList().get(i).getLink());
        }
        return resultList;
    }
    private ArrayList<HashMap<String, Object>> getHashMap(RSSFeed array){
        ArrayList<HashMap<String, Object>> data = new ArrayList<>(array.getArticleList().size());
        HashMap<String, Object> map;
        for (int i = 0; i < array.getArticleList().size(); i++) {
            map = new HashMap<>();
            map.put("PUBDATE", array.getArticleList().get(i).getpubDate());
            map.put("TITLE", array.getArticleList().get(i).getTitle().replaceAll("\n|\r\n", " "));
            data.add(map);
        }
        return data;
    }
}