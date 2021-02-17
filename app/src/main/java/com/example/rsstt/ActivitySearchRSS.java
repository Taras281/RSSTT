package com.example.rsstt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivitySearchRSS extends AppCompatActivity implements ListenerAsync, Serializable {
    //String urlRSS = "https://klops.ru/";
    //String urlRSS = "https://lenta.ru/";
    //String urlRSS = "https://www.computerra.ru/";
    //String urlRSS = "https://habr.com/ru/";
    //String urlRSS = "https://4pda.ru/";
    //String urlRSS = "https://kaliningrad.rbc.ru/";
    //String urlRSS = "https://ria.ru/science/";
    //String urlRSS = "https://elementy.ru/novosti_nauki";
    //String urlRSS = "https://www.gazeta.ru/science/";

    //String urlRSS = "https://klg.aif.ru/sport";
    //String urlRSS = "https://russian.rt.com/trend/345585-priroda";
    Context context;
    EditText et;
    TextView tvURLRSS;
    TextView tvNameRSS;
    TextView tvPhotoURL;
    Button bt;
    Handler h;
    MyTask mt;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_r_s_s);
        setTitle(R.string.search_rss);
        context = MainActivity.mainactivityContext;
        ActivitySearchRSS asrss = this;

        et = (EditText)findViewById(R.id.et_rss);
        et.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( actionId==6){
                    if (validURL(et.getText().toString()))
                    {   mt = new MyTask();
                        mt.registerHearer(asrss);
                        mt.execute(et.getText().toString());}
                    else {
                        et.setText(R.string.requirement);
                        bt.setEnabled(false);
                        }
                    return false;
                }
                return false;
            }
        });

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                list = (ArrayList<String>)msg.obj;
                tvURLRSS.setText(getResources().getString(R.string.found_rss) + list.get(0));
                tvNameRSS.setText(getResources().getString(R.string.name_RSS) + list.get(1));
                tvPhotoURL.setText(getResources().getString(R.string.link_item) + list.get(2));
                bt.setEnabled(true);
                if (list.get(0).equals(getResources().getString(R.string.bad_url))){bt.setEnabled(false);}
                                mt.cancel(false);

            };
        };
        tvURLRSS = (TextView) findViewById(R.id.tv_url_rss);
        tvNameRSS = (TextView) findViewById(R.id.tv_name_rss);
        tvPhotoURL = (TextView) findViewById(R.id.tv_url_photo_rss);
        bt = (Button) findViewById(R.id.bt_ok);
        bt.setEnabled(false);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data", list);
                intent.putExtra("check2", 8);
                intent.putExtra("check", "XXXXXXXX");
                intent.setAction("sdagdsgsdfg");
                setResult(5, intent);
                finish();
            }
        });

    }

    @Override
    public void setData(ArrayList<String> data) {


        Message msg;
        msg=h.obtainMessage(111, data);
        h.sendMessage(msg);
    }

    protected boolean validURL(String data){
        if (data.contains("https://")|| data.contains("http://")) return true;
        return false;
    }
}