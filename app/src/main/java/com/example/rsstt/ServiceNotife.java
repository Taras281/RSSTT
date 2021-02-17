package com.example.rsstt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ServiceNotife extends Service {

    int count;
    String CHANNEL_ID;
    ArrayList<HashMap<String,String>> listChanalForNotife;// список со ссылками на каналы для уведомления
    ArrayList<RSSFeed> listRSSFeed, listlastRSSFeed;
    ArrayList<HashMap<String,String>> listRSSForNotife;// список

    String TITLE = "TITLE";
    String LINK = "LINK";
    String DATA = "DATA";
    String NAMERSS = "NAMERSS";
    public ServiceNotife() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate() {
        super.onCreate();
        listlastRSSFeed = new ArrayList<>();
        count=0;

        CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(MainActivity.mainactivityContext.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RSS start notif")
                .setSmallIcon(R.drawable.rss)
                .setAutoCancel(true).build();

                startForeground(1, notification);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        listChanalForNotife= (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("LIST");
        if (listChanalForNotife==null|| listChanalForNotife.size()<1)
            return START_STICKY;

        ExecutorService service = Executors.newFixedThreadPool(listChanalForNotife.size());
        listRSSForNotife = new ArrayList<>();
        ArrayList<Future> futures = new ArrayList<>();
        for (int i =0; i<listChanalForNotife.size();i++) {
            Future<RSSFeed> future = service.submit(new ControllerThread(listChanalForNotife.get(i).get("URL")));
            futures.add(future);
        }
        listRSSFeed = new ArrayList<>();
        for (int i =0; i<listChanalForNotife.size();i++) {
            try {
                listRSSFeed.add((RSSFeed) futures.get(i).get());
            } catch (ExecutionException e) {
                // e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (listRSSFeed.size()>0){// если список есть  то делаем прошлый список и вызываем метод для сравнения списков
            if (listlastRSSFeed.size()>0){
                compareList(listlastRSSFeed, listRSSFeed);}
            for (int i =0;i<listRSSFeed.size();i++){
                listlastRSSFeed.add(i, listRSSFeed.get(i));
                 if (listlastRSSFeed.size()>listRSSFeed.size()){
                     listlastRSSFeed.remove(listRSSFeed.size());}
            }
        }
         return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("TAGER", "SERVISE Destroy");
    }

    public void myNotyf(ArrayList<HashMap<String,String>> list){
                for (int i=0; i<list.size();i++)
                {
                    //Intent resultIntent = new Intent(MainActivity.mainActivityContext, MainActivity.class);
                Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(i).get(LINK)));

                PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.mainactivityContext, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(MainActivity.mainactivityContext, CHANNEL_ID)
                        .setContentTitle(list.get(i).get(NAMERSS))
                        .setContentText(list.get(i).get(TITLE))
                        .setStyle(new NotificationCompat.InboxStyle()
                                //.addLine(list.get(i).get(TITLE))
                                  .addLine(list.get(i).get(TITLE)))
                        .setSmallIcon(R.drawable.rss)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .build();
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(count++, notification);
                }
            }


    // в клас передаем URL RSS а получаем ленту. Класс посылает синхронно запрос
    class ControllerThread implements Callable<RSSFeed> {
        RSSFeed rss;
        String url;
        String BASE_URL;
        String RSS;
        VogellaAPI vogellaAPI;
        Retrofit retrofit;
        Call<RSSFeed> call;

        ControllerThread(String url){
            this.url = url;
         }

        @Override
        public RSSFeed call() throws Exception {
        BASE_URL = getUrLRSS(url);
        RSS= getUrLSufixRSS(url);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        vogellaAPI = retrofit.create(VogellaAPI.class);
            call = vogellaAPI.loadRSSFeed(RSS);
            if (RSS.equals("rss/news"))
                call = vogellaAPI.loadRSSFeedNews();
            if (RSS.equals("all.php"))
                call = vogellaAPI.loadRSSFeedPHP();
            if (RSS.equals("news.rss"))
                call = vogellaAPI.loadRSSFeedNewsPointRss();
            Response<RSSFeed> response = call.execute();
            rss =response.body();
            return rss;
        }
        private  String getUrLRSS(String fullUrl){

            int posPoint = fullUrl.lastIndexOf("/");
            return  fullUrl.substring(0, posPoint+1);
        }
        private  String getUrLSufixRSS(String fullUrl){

            int posPoint = fullUrl.lastIndexOf("/");
            return  fullUrl.substring(posPoint+1);
        }
    }

    private void compareList(ArrayList<RSSFeed> lastlist, ArrayList<RSSFeed> currentlist){
        Log.d("TAGER", "Im WORK->"+Thread.currentThread().hashCode());
        listRSSForNotife = new ArrayList<>();// список HASH Mapod  для уведомлений
        int minSizeList = Math.min(currentlist.size(), lastlist.size());
        for (int j=0; j<minSizeList; j++){ // перебор объектов лент
            for (int i=0; i<1;i++){// перебор по Title  в лентах
                String s1 = currentlist.get(j).getArticleList().get(i).getTitle();
                String s2 = lastlist.get(j).getArticleList().get(i).getTitle();

                if (!s1.equals(s2)){
                HashMap<String, String> hm = new HashMap<>();
                hm.put(TITLE,currentlist.get(j).getArticleList().get(i).getTitle());
                hm.put(LINK, currentlist.get(j).getArticleList().get(i).getLink());
                hm.put(DATA,currentlist.get(j).getArticleList().get(i).getpubDate());
                hm.put(NAMERSS, currentlist.get(j).getChannelTitle());
                listRSSForNotife.add(hm);

                }
            }
        }
        if (listRSSForNotife.size()>0){
            myNotyf(listRSSForNotife);
           // Log.d("TAGER", "Im send notife " + listRSSForNotife.get(0).get(NAMERSS)+"   "+ listRSSForNotife.get(0).get(TITLE));
        }

    }

}