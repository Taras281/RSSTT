package com.example.rsstt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
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
    final int TIME_TIMER=60_000;
    Timer timer;
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
        ExecutorService service = Executors.newFixedThreadPool(1);
        CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
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
        if (timer!=null){
            timer.cancel();
            timer.purge();
        }
        timer  = new Timer();


        final TimerTask tt = new TimerTask() {
            @Override
            public void run() {
               final Thread tr = new Thread(new Runnable() {

                   @Override
                   public void run() {
                      // Log.d("TAG", "ALL THREADS_>"+Thread.activeCount());
                       try {
                           onStartComandForRunnable(intent, flags, startId);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                       //onStartComandForRunnable(intent, flags, startId);
                   }
               }); tr.start();

            }
        };

        timer.schedule(tt,0, TIME_TIMER);

        return START_STICKY;
    }

    private int onStartComandForRunnable(Intent intent, int flags, int startId) throws Exception {

        listChanalForNotife= (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("LIST");
        if (listChanalForNotife==null|| listChanalForNotife.size()<1)
            return START_STICKY;
        listRSSForNotife = new ArrayList<>();
        listRSSFeed = new ArrayList<>();
        for (int i =0; i<listChanalForNotife.size();i++) {
            ControllerThreadTood c = ControllerThreadTood.getInstance(listChanalForNotife.get(i).get("URL"));
            listRSSFeed.add(c.read());
        }
       // printThread();
        Log.d("TOG", "COUNT THREAD   " + Thread.activeCount());

        if (listRSSFeed.get(0)!=null&& listRSSFeed!=null&&listRSSFeed.size()>0){// если список есть  то делаем прошлый список и вызываем метод для сравнения списков
            if (listlastRSSFeed.size()>0){
                compareList(listlastRSSFeed, listRSSFeed);}
            for (int i =0;i<listRSSFeed.size();i++){
                listlastRSSFeed.add(i, listRSSFeed.get(i));
                while (listlastRSSFeed.size()>listRSSFeed.size()){
                    listlastRSSFeed.remove(listlastRSSFeed.size()-1);}
            }
        }
        return START_STICKY;
    }



    public void myNotyf(ArrayList<HashMap<String,String>> list){
                for (int i=0; i<list.size();i++)
                {

                Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(i).get(LINK)));

                PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.mainactivityContext, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(MainActivity.mainactivityContext, CHANNEL_ID)
                        .setContentTitle(list.get(i).get(NAMERSS))
                        .setContentText(list.get(i).get(TITLE))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(list.get(i).get(TITLE)))
                       // .setStyle(new NotificationCompat.InboxStyle()
                                //.addLine(list.get(i).get(TITLE))
                      //     .addLine(list.get(i).get(TITLE)))


                        .setSmallIcon(R.drawable.rss)

                        .setTimeoutAfter(7_200_000)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .build();
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(count++, notification);
                }
            }


    // в клас передаем URL RSS а получаем ленту. Класс посылает синхронно запрос


    private boolean compareList(ArrayList<RSSFeed> lastlist, ArrayList<RSSFeed> currentlist){

        listRSSForNotife = new ArrayList<>();// список HASH Mapod  для уведомлений

        int minSizeList = Math.min(currentlist.size(), lastlist.size());
        for (int j=0; j<minSizeList; j++){ // перебор объектов лент
            String l = currentlist.get(j).getChannelTitle();
            String ll = lastlist.get(j).getChannelTitle();
            if (!currentlist.get(j).getChannelTitle().equals(lastlist.get(j).getChannelTitle()) ) {return false;}// если ленты перепутались
            for (int i=0; i<1;++i){// перебор по Title  в лентах
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
        // убийство повторяющихся новостей
        Iterator<HashMap<String, String>> iterator = listRSSForNotife.iterator();
        String last = "";
        while (iterator.hasNext()){
            String s = iterator.next().get(TITLE);
            if (s.equals(last)) iterator.remove();
            last = s;

        }
        // создание уведомлений
        if (listRSSForNotife.size()>0){
            myNotyf(listRSSForNotife);

        }
        return true;

    }
    private void printThread() {
    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    for (Thread t: threadSet)
    Log.d("TOG", t.getName() + " ALIVE  " + t.isAlive() );
}

    static class ControllerThreadTood {
        private static String url;
        RSSFeed rss;
        String BASE_URL;
        String RSS;
        VogellaAPI vogellaAPI;
        Retrofit retrofit;
        Call<RSSFeed> call;

        private static ControllerThreadTood cntrTh;

        ConnectionPool pool = new ConnectionPool(5, 10000, TimeUnit.MILLISECONDS);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .build();


        public static  ControllerThreadTood getInstance(String urll){
            url = urll;
            cntrTh = new ControllerThreadTood();
            return cntrTh;
        }

        // ControllerThread(String url){ this.url = url; }


            public RSSFeed read() throws Exception {
            BASE_URL = getUrLRSS(url);
            RSS= getUrLSufixRSS(url);
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(client)
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
}