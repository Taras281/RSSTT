package com.example.rsstt;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;



public class ActivitySearchRSSGood extends AppCompatActivity implements ListenerAsync {

    WebView webView;
    String URL = "https://yandex.ru/search/?text=новости&lr=22";
    MyTask mt;
    ArrayList<String> results = new ArrayList<>();
    Handler h;
    ActivitySearchRSSGood asrg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_search_good);
        asrg = this;
        webView = findViewById(R.id.webView);
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        webView.loadUrl(URL);
        webView.setWebViewClient(new MyWebViewClient());

    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setData(ArrayList<String> data) {
        results=data;
        CustomDialog cd = new CustomDialog(this, results);
        cd.show(getSupportFragmentManager(), "custom");
    }


    private class MyWebViewClient extends WebViewClient {
        Boolean check = true;
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d("TAG", "URL ----- "+ request.getUrl().toString());
            String f = request.getUrl().toString();
            if(check) view.loadUrl(f); // первая загрузка WebView  показываем страницу
            if(!check) // проверка ссылок после второго и последующих вызовов метода
            if (validURL(f))
            {   mt = new MyTask();
                mt.registerHearer(asrg);
                mt.execute(f);
               }
            else {
                // Уведомление о плохой ссылке
            }
            check = false;// устанавливаем флаг чтобы не загружались страници
            return true;
        }
        private boolean validURL(String data){
            if (data.contains("https://")|| data.contains("http://")) return true;
            return false;
        }

    }

    public static class CustomDialog extends DialogFragment {

        ArrayList<String> list;
        Activity act;
        int NUM_COLUMN;
        CustomDialog( Activity act, ArrayList<String> list){
            this.list=list;
            this.act = act;
        }


        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(act);
            if (list.get(0).equals("BAD URL"))
            { return builder
                    .setTitle(R.string.nothing_serach)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton("Отмена", null)
                    .create();}
                else
            return builder
                    .setTitle(R.string.result_serach)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getResources().getString(R.string.creat_item) + "  " + list.get(0))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        onFinish(list);
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .create();
        }
        private void onFinish(ArrayList<String> list){
            Intent intent = new Intent();
            intent.putExtra("data", list);
            intent.putExtra("check2", 8);
            intent.putExtra("check", "XXXXXXXX");
            intent.setAction("sdagdsgsdfg");
            act.setResult(5, intent);
            act.finish();
        }

    }
}