package com.example.rsstt;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MyTask extends AsyncTask <String, Void, String> {
    final String BAD_URL="BAD_URL";
    protected Elements title;
    protected ArrayList<String> result = new ArrayList<>();
    protected ArrayList<String> resultPicture = new ArrayList<>();
    protected ArrayList<String> results = new ArrayList<>();
    protected String titl="BAD_URL";
    protected String urlRSS = "BAD_URL";
    private ListenerAsync hearer = null;


    public void registerHearer(ListenerAsync obj){
        hearer = obj;
    }


    @Override
    protected String doInBackground(String... strings) {
        if(isCancelled())
        {return null;}
        urlRSS=strings[0];
        Document doc = null;//Здесь хранится будет разобранный html документ
        try {
               doc = Jsoup.connect(urlRSS).get();
        } catch (IOException e) {

            e.printStackTrace();
        }


        if (doc!=null)


        {// запуск поиска RSS разные комбинации

            result.addAll(createrAllLinkRss( doc, doc.select("link"), "href", "rss"));
            result.addAll(createrAllLinkRss( doc, doc.select("link"), "href", "feed"));
            result.addAll(createrAllLinkRss( doc, doc.select("a"), "href", "rss"));
            result.addAll(createrAllLinkRss( doc, doc.select("a"), "href", "feed"));

          /*  result.add(getURLList( doc, doc.select("link"), "href", "rss"));// поиск в ленте
            result.add(getURLList( doc, doc.select("link"), "href", "rss/news"));// поиск в ленте
            result.add(getURLList( doc, doc.select("link"), "href", "rss2"));// поиск в ленте
            result.add(getURLList( doc, doc.select("link"), "href", "rss2/news"));// поиск в ленте
            result.add(getURLList( doc, doc.select("a"), "href", "feed"));// поиск в компютерре
            result.add(getURLList( doc, doc.select("a"), "href", "feed/news"));// поиск в компютерре
            result.add(getURLList( doc, doc.select("link"), "href", "feed"));// поиск в компютерре
            result.add(getURLList( doc, doc.select("link"), "href", "feed/news"));// поиск в компютерре
            result.add(getURLList( doc, doc.select("a"), "href", "rss"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("a"), "href", "rss/news"));// поиск в компютерре
           */

            // поиск ссылок на фото
            resultPicture.add(getURLList(doc, doc.select("link"), "href", "favicon"));// поиск в ленте
            resultPicture.add(getURLList(doc, doc.select("link"), "href", "png"));// поиск в ленте
            titl = doc.title();
        }


        results.add(checerURL(result));

        results.add(titl);
        results.add(getUrlPicture(resultPicture));
        hearer.setData(results);
        return getUrlPicture(resultPicture);
    }
// метод возвращает сылку на найденную  RSS ленту, принимает сам DOC, тег елемента - link, a, тег содержащий ссылку на RSS - href, и ключевое слов по которому ищем-RSS, feed
    private String getURLList( Document doc, Elements title, String atr, String searchContains){
        String res=BAD_URL;
         for (Element link : title) {
                if (link.attr(atr).contains(searchContains))
                {String js = (link.attr(atr));

                 if(js.matches(".*(\\W|^)"+searchContains+"(\\W|$).*")|| js.matches(".*[ /]"+searchContains+"[ /].*"))
                     res = (link.absUrl(atr));

                    }
            }

        return res;
    }

    // метод создает массив ссылок на все похожее на RSS ленты
    private ArrayList<String> createrAllLinkRss(Document doc, Elements title, String atr, String searchContains){
        HashSet<String> res = new HashSet<>();
        ArrayList<String> resList = new ArrayList<>();
         String r="N";

         for (Element link : title) {
            if (link.attr(atr).contains(searchContains))
             {res.add(link.absUrl(atr)); }
        }
       for(String str: res)
        {resList.add(str);}
        return  resList;
    }

    // метод проверяет ссылки на ленты путем их загрузки и проверки соответствия заголовка
    private String checerURL(ArrayList<String> list){

        Document doc = null;//Здесь хранится будет разобранный html документ
        for (String url:list) {
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {

                e.printStackTrace();
            }
            if(doc!=null){
            String el = doc.toString();
            if (el.contains("<channel>")&&el.contains("<title>")&&el.contains("<link>") ) return url;
            if (el.contains("rss")) return url;
            }
        }
     return "BAD URL";

    }


    private String getUrlPicture(ArrayList<String> data){
        ArrayList<String> list = data;
        String result = BAD_URL;
        for (String res: data){
            if (res.contains("ico")) return res;
            if (res.contains("png")) result = res;
        }
        return result;
    }




    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(String.valueOf(result));


    }
@Override
    protected void onCancelled(){
        super.onCancelled();
}
}