package com.example.rsstt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements CallbackFromController {
    GridLayout gridLayout;
    LinearLayout lv;
    FloatingActionButton fab;
    static Context mainactivityContext;
    Activity mainActivityActivity;
    static Application application;
    CreatViewtem cvi;
    int MARGIN,  widhtItem, elevation;
    public ArrayList<String> listNameRss;
    public ArrayList<String> listUrlPicture;
    public ArrayList<String> listUrlRSS;
    public ArrayList<Boolean> listChecer;

    RSSFeed rss;
    Controller controller;
    boolean isRotate = false;
    final int NUM_COLUMN=3;

    final String SAVED_NAME = "SAVED_NAME";
    final String SAVED_PICTURES = "SAVED_PICTURES";
    final String SAVED_URL = "SAVED_URL";
    final String SAVED_BOOL = "SAVED_BOOL";
    SharedPreferences sp;
    SharedPreferences.Editor mEdit1;
    boolean flagInterupt = true;
    boolean flagMenuNotif = true;
    static  Timer mTimer;
    int timeZaprosRSS;
    Timer timerRSS= new Timer();
    TimerTask timerTaskRSS;
    ArrayList<HashMap<String, String>> listChanalForNotife;
    String URL = "URL";
    String NAME = "NAME";
    String TITLE = "TITLE";
    float historyX;// точки для слежения за пальцем
    float historyY;
    int TIME_DELITE = 3000;// время нажатия за которое удалится иконка с лентой

@Override
public void onResume() {
    super.onResume();
    if (myLoader(SAVED_URL, listUrlRSS) != null&&myLoader(SAVED_URL, listUrlRSS).size()>0){
    listUrlRSS = myLoader(SAVED_URL, listUrlRSS);
    listNameRss= myLoader(SAVED_NAME, listNameRss);
    listUrlPicture =  myLoader(SAVED_PICTURES, listUrlPicture);
    listChecer = myLoaderBool(SAVED_BOOL, listChecer);
    flagMenuNotif=myLoaderFlagNotyf("FLAG_NOTIFY", flagMenuNotif);}
    startNotif();// метод запускающий создание Таймера создающего потоки чтения RSS
    creatDisplay(listUrlPicture, listNameRss, listChecer, NUM_COLUMN);
}

    @Override
public void onStop() {
    super.onStop();
    mySaver(SAVED_URL, listUrlRSS);
    mySaver(SAVED_NAME, listNameRss);
    mySaver(SAVED_PICTURES, listUrlPicture);
    mySaverBool(SAVED_BOOL, listChecer);
    mySaverFlag(flagMenuNotif);
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainactivityContext= getApplicationContext();
        application = getApplication();
        mainActivityActivity = this;

diagnostic();

        Log.d("TAG", "Creat" + "creater");

        fab = (FloatingActionButton)findViewById(R.id.fab) ;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);

                Intent intent = new Intent(MainActivity.mainactivityContext, ActivitySearchRSS.class);
                startActivityForResult(intent, 111);
            }
        });
        controller = new Controller();
        controller.register(this);

        widhtItem = 200;
        elevation = 20;
        listNameRss = new ArrayList<String>();
        listUrlPicture = new ArrayList<String>();
        listUrlRSS = new ArrayList<String>();
        listChecer = new ArrayList<>();
        listChanalForNotife = new ArrayList<>();

        listNameRss.add("Lenta.ru : Новости");
        listUrlPicture.add("https://lenta.ru/images/small_logo.png");
        listUrlRSS.add("https://lenta.ru/rss/news");
        listChecer.add(true);

        listNameRss.add("Хабр");
        listUrlPicture.add("https://dr.habracdn.net/habr/6013e4d4/images/apple-touch-icon.png");
        listUrlRSS.add("https://habr.com/ru/rss/all/all/?fl=ru");
        listChecer.add(false);

        listNameRss.add("Хакер");
        listUrlPicture.add("https://xakep.ru/wp-content/uploads/2017/06/xakep-favicon-93x93.png");
        listUrlRSS.add("https://xakep.ru/feed");
        listChecer.add(true);

        listNameRss.add("4PDA");
        listUrlPicture.add("https://s.4pda.to/heB5pXFz14I2pMyz16i8q.png");
        listUrlRSS.add("https://4pda.ru/feed");
        listChecer.add(false);

        listNameRss.add("КЛОПС");
        listUrlPicture.add("https://source.klops.ru/images/logo.svg");
        listUrlRSS.add("https://rss.klops.ru/rss");
        listChecer.add(true);
        flagMenuNotif =false;


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mEdit1 = sp.edit();
    }


    // мето который сосздает Vied из списка принимает на вход список фото и список имен групп
    private void creatDisplay(ArrayList<String> imageUrl, ArrayList<String> nameRss, ArrayList<Boolean> checKer, int numColumn){

               lv = (LinearLayout) findViewById(R.id.linear_main);

               widhtItem = (int)(getDisplay().getWidth()*0.9/numColumn);
               MARGIN = (getDisplay().getWidth() - widhtItem*numColumn)/(numColumn+1);
               gridLayout = new GridLayout(MainActivity.mainactivityContext);
               gridLayout.setColumnCount(numColumn);
               gridLayout.setRowCount((int)(imageUrl.size()/numColumn));
               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

               lp.leftMargin=MARGIN/2;
               lp.rightMargin=MARGIN/2;
               lp.topMargin=0;
               lp.bottomMargin=MARGIN;

               ArrayList<CreatViewtem> arrayAVI = new ArrayList<CreatViewtem>(imageUrl.size());// массив для хранения созданных итемов с тновостями
               //for (int i =0; i<imageUrl.size(); i++) {checker.add(i, false);}
               for (int i =0; i<imageUrl.size(); i++) {// создаем ВЮШКИ с лентами

                   cvi = new CreatViewtem(this, BitmapFactory.decodeResource(getResources(), R.drawable.rsserror), nameRss.get(i), elevation, widhtItem, widhtItem);
                   arrayAVI.add(cvi);
                   View v = cvi.getView();
                   ImageView iv = cvi.iv;

                   v.setId(i);


                   cvi.cb.setChecked(checKer.get(i));// в создаваем VIEW  ставит чеки

                   cvi.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                       @Override
                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                           if (isChecked) {
                               checKer.set(v.getId(), true);
                               startNotif();
                           }// ЕСЛИ ПОСТАВИЛИ ЧЕК ТО ЗАПИСЫВАЕМ В МАСИВ TRUE И СТАРТУЕМ СЕРВИС ЧТОБ СЕРВИС ЗАПОМНИЛ СПИСОК ЛЕНТ
                           else {
                               checKer.set(v.getId(), false);
                               startNotif();
                           }
                       }
                   });
                   v.setOnTouchListener(new View.OnTouchListener() {// добавляем слушателя для ТАЧА по вюшке
                       @Override

                       public boolean onTouch(View v, MotionEvent event) {
                           int i = v.getId();
                           String nameRSS = listNameRss.get(i);
                           long startTime = 0;
                           long totalSecunds = 0;
                           long totalTime = 0;
                           //Log.d("MOVEMENT", " event " +event.getAction());

                           // делаем таймер для отслеживания времени нажатия кнопки
                           TimerTask timerTask = new TimerTask() {
                               @Override
                               public void run() {
                                   MainActivity.this.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           flagInterupt = false;
                                           CustomDialog dialog = new CustomDialog(MainActivity.this, listNameRss, listUrlPicture, listUrlRSS, listChecer, NUM_COLUMN);
                                           Bundle args = new Bundle();
                                           args.putString("RSS", nameRSS);
                                           args.putInt("id", i);
                                           dialog.setArguments(args);
                                           dialog.show(getSupportFragmentManager(), "custom");

                                       }
                                   });
                               }
                           };

                           boolean dd = arrayAVI.get(i).cb.isChecked();
                           if (event.getAction() == MotionEvent.ACTION_DOWN) {
                               mTimer = new Timer();
                              // Log.d("TIMER", " DOWN--> "+mTimer.hashCode());
                               mTimer.schedule(timerTask, TIME_DELITE);// через 3 секунды удалим иконку
                               //Log.d("TIMER", "sheldue - " + mTimer.hashCode());
                               arrayAVI.get(i).rl.setBackgroundResource(R.drawable.shape2);// подкрашиваем кнопку
                               historyX=event.getX();
                               historyY=event.getY();
                               flagInterupt = true;// флаг для отслеживания запуска таймера
                               return true;
                           }
                           int di = v.getId();
                           if (event.getAction() == MotionEvent.ACTION_UP  && flagInterupt) {

                               mTimer.cancel();// если подняли палец отменяем таймер
                               //Log.d("TIMER", " UP --> "+mTimer.hashCode());
                               arrayAVI.get(i).rl.setBackgroundResource(R.drawable.shape);
                               String adr = getUrLRSS(listUrlRSS.get(i));
                               String suf = getUrLSufixRSS(listUrlRSS.get(i));
                               controller.setRss(adr);
                               controller.setSufixRss(suf);
                               controller.start();
                               // запускаем читатель RSS а обрабатываем и выводим результат в слушателе 89118521032 алтухин игорь александрович
                               return true;
                           }
                           if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                               //float mX = event.getX();
                               //float mY = event.getY();
                               //if(((historyX-mX)*(historyX-mX) + (historyY-mY)*(historyY-mY))>5000)
                               //{mTimer.cancel();// если подняли палец отменяем таймер
                               // mTimer = new Timer();  mTimer.schedule(timerTask, TIME_DELITE);flagInterupt = true;
                               //}
                               arrayAVI.get(i).rl.setBackgroundResource(R.drawable.shape);
                               mTimer.cancel();
                               }

                            return true;
                       }

                   });
                   // записываем в масив состояние флажков

                   Picasso.with(this)
                           .load(imageUrl.get(i))
                           .transform(new MyTransformation(widhtItem))
                           .placeholder(R.drawable.rss)
                           .error(R.drawable.rss)
                           .into(iv);

                   gridLayout.addView(v, lp);
               }
        lv.removeAllViews();

        lv.addView(gridLayout);




    }

    private  String getUrLRSS(String fullUrl){

        int posPoint = fullUrl.lastIndexOf("/");
         return  fullUrl.substring(0, posPoint+1);
    }
    private  String getUrLSufixRSS(String fullUrl){

        int posPoint = fullUrl.lastIndexOf("/");
        return  fullUrl.substring(posPoint+1);
    }

// метод вызываемый Контроллером РСС лент(парсером) когда лента разобрана
    @Override
    public void returnRss(RSSFeed rss) {
        this.rss=rss;
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("title", rss.getChannelTitle());
        intent.putExtra("rss", rss);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==111&&resultCode==5) {
            if (data != null) {
                ArrayList<String> list = data.getStringArrayListExtra("data");
                listNameRss.add(list.get(1));
                listUrlPicture.add(list.get(2));
                listUrlRSS.add(list.get(0));
                listChecer.add(true);

                // перезаписываем память!!!!!
                mySaver(SAVED_URL, listUrlRSS);
                mySaver(SAVED_NAME, listNameRss);
                mySaver(SAVED_PICTURES, listUrlPicture);
                mySaverBool(SAVED_BOOL, listChecer);
                creatDisplay(listUrlPicture, listNameRss, listChecer, NUM_COLUMN);
                return;
            }
        }
    }


    // метод для сохранения списка перед закрытием окна, передаем КЛЮЧ. и сам списко
    private void mySaver(String key, ArrayList<String> list){
        Gson gson = new Gson();
        String jsonText = gson.toJson(list);
        mEdit1.putString(key, jsonText);
        mEdit1.apply();
    }

    private void mySaverBool(String key, ArrayList<Boolean> list){
        Gson gson = new Gson();
        ArrayList<Boolean> boolList = new ArrayList<>();
        for (int i = 0; i<listUrlRSS.size();i++){
            boolList.add(list.get(i));
        }
        String jsonBool = gson.toJson(boolList);
        mEdit1.putString(key, jsonBool);
        mEdit1.apply();
    }

    private void mySaverFlag(Boolean flag){
        Gson gson = new Gson();
        String jsonBool = gson.toJson(flag);
        mEdit1.putString("FLAG_NOTIFY", jsonBool);
        mEdit1.apply();
    }
    private ArrayList<String> myLoader(String key, ArrayList<String> list){
       Gson gson = new Gson();
       String jsonText = sp.getString(key, null);
       String[] f = gson.fromJson(jsonText, String[].class);  //EDIT: gso to gson
       list = new ArrayList<>();
       if (f==null){return list;}
       for (int i=0;i<f.length;i++){
           list.add(i, f[i]);
       }
       int y =0;
       return list;

    }

    private ArrayList<Boolean> myLoaderBool(String key, ArrayList<Boolean> list) {
        Gson gson = new Gson();
        String jsonBool = sp.getString(key, null);
        String[] f = gson.fromJson(jsonBool, String[].class);  //EDIT: gso to gson
        listChecer = new ArrayList<>();
        if (f==null){return list;}
        for (int i=0;i<f.length;i++){
            listChecer.add(i, Boolean.parseBoolean(f[i]));
        }
        int t=9;
        return listChecer;
    }

    private Boolean myLoaderFlagNotyf(String key, Boolean flag){
        Gson gson = new Gson();
        String jsonBool = sp.getString("FLAG_NOTIFY", null);
          //EDIT: gso to gson
        if (jsonBool==null) {flag=false;}
        else {
        flag = Boolean.parseBoolean(jsonBool);}
        return flag;
    }



public void startNotif(){
    timeZaprosRSS = 600000;// периодичность запроса лент
    timerTaskRSS = new TimerTask() {
        @Override
        public void run() {
            listChanalForNotife = new ArrayList<>();// переписываем массив ссылок для уведомлений
            for (int i =0; i<listUrlRSS.size();i++) {
                if (listChecer.get(i)) {
                    HashMap<String, String> hm= new HashMap<>();
                    hm.put(URL, listUrlRSS.get(i));
                    hm.put(NAME, listNameRss.get(i));
                    listChanalForNotife.add(hm);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        startForegroundService(new Intent(MainActivity.mainactivityContext, ServiceNotife.class).putExtra("LIST", listChanalForNotife));
                    }
                }).start();
            }

            int y = 8;// здесь создавать уведомления
            int ya = 8;// здесь создавать уведомления
            ya = ya + y;
        }
    };
    timerRSS.schedule(timerTaskRSS,0,timeZaprosRSS);
}




    @SuppressLint("ValidFragment")

    public static class CustomDialog extends DialogFragment {

         ArrayList<String> listNameRss;
         ArrayList<String> listUrlPicture;
         ArrayList<String> listUrlRSS;
         ArrayList<Boolean> listChecer;
         MainActivity act;
         int NUM_COLUMN;
         CustomDialog( MainActivity act, ArrayList<String> listNameRss, ArrayList<String> listUrlPicture, ArrayList<String> listUrlRSS, ArrayList<Boolean> listChecer, int NUM_COLUMN){
             this.listNameRss=listNameRss;
             this.listUrlPicture=listUrlPicture;
             this.listUrlRSS=listUrlRSS;
             this.listChecer=listChecer;
             this.NUM_COLUMN=NUM_COLUMN;
             this.act = act;
         }


         @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String rssDelited = getArguments().getString("RSS");
            int id = getArguments().getInt("id");
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

            return builder
                    .setTitle(R.string.dialog_window)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getResources().getString(R.string.you_wanted_delite) + rssDelited + " ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listUrlRSS.remove(id);
                            listNameRss.remove(id);
                            listUrlPicture.remove(id);
                            listChecer.remove(id);
                            act.creatDisplay(listUrlPicture, listNameRss, listChecer, NUM_COLUMN);
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .create();

        }


    }

    private void diagnostic(){
        String LOG_FILE = "fffff";
// Создадим новый файл лога
        Diagnostics.createLog(LOG_FILE);
        int param1 = 200;
// Распечатаем строку в LogCat и сохраним ее в файл
        Diagnostics.i(this, "onCreate w/param1 = " + param1).append(LOG_FILE);
// И еще одну строку, так, для теста
        Diagnostics.i(this, "onCreate completed").append(LOG_FILE);
    }


}