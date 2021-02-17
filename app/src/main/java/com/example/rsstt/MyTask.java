package com.example.rsstt;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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
            result.add(getURLList(doc, doc.select("link"), "href", "rss"));// поиск в ленте
            result.add(getURLList(doc, doc.select("link"), "href", "rss/news"));// поиск в ленте
            result.add(getURLList(doc, doc.select("link"), "href", "rss2"));// поиск в ленте
            result.add(getURLList(doc, doc.select("link"), "href", "rss2/news"));// поиск в ленте
            result.add(getURLList(doc, doc.select("a"), "href", "feed"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("a"), "href", "feed/news"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("link"), "href", "feed"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("link"), "href", "feed/news"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("a"), "href", "rss"));// поиск в компютерре
            result.add(getURLList(doc, doc.select("a"), "href", "rss/news"));// поиск в компютерре
            // поиск ссылок на фото
            resultPicture.add(getURLList(doc, doc.select("link"), "href", "favicon"));// поиск в ленте
            resultPicture.add(getURLList(doc, doc.select("link"), "href", "png"));// поиск в ленте
            titl = doc.title();
        }
        else
        Log.d("TAG", "EROR: ");
        //for (int i = 0; i<result.size();i++) Log.d("TAG", "result " + result.get(i));
       // Log.d("TAG", "result String " + getUrlRss(result));
        //for (int i = 0; i<resultPicture.size();i++) Log.d("TAG", "result PICTURE " + resultPicture.get(i));
       // Log.d("TAG", "TITLE   " + titl);
       // формируем выходной списко
        results.add(getUrlRss(result));
        results.add(titl);
        results.add(getUrlPicture(resultPicture));
        hearer.setData(results);
        return getUrlPicture(resultPicture);
    }
// метод возвращает сылку на найденную  RSS ленту, принимает сам DOC, тег елемента - link, a, тег содержащий ссылку на RSS - href, и ключевое слов по которому ищем-RSS, feed
    private String getURLList(Document doc, Elements title, String atr, String searchContains){
        String res=BAD_URL;
         for (Element link : title) {
                if (link.attr(atr).contains(searchContains))
                {String js = (link.attr(atr));

                 if(js.matches(".*(\\W|^)"+searchContains+"(\\W|$).*")|| js.matches(".*[ /]"+searchContains+"[ /].*"))
                    res = (link.absUrl(atr));}
            }

        return res;
    }
    private String getUrlRss(ArrayList<String> data){
        ArrayList<String> list = data;
        String result = MainActivity.mainactivityContext.getResources().getString(R.string.bad_url);
        int checker=-1;
        for (int i = 0; i<data.size(); i++){
            if (data.get(i).contains("news")) checker = i;// запоминаем позицию самого ролевантного адреса
            if (data.get(i).contains("rss")) checker = i;
            if (data.get(i).contains("feed")) checker = i;
        }
        if (checker==-1) return result;
        return data.get(checker);
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