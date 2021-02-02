package com.example.rsstt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CallbackFromController {
    GridLayout gridLayout;
    LinearLayout lv;
    static Context mainactivityContext;
    CreatViewtem cvi;
    int MARGIN, numColumn, widhtItem, elevation;
    ArrayList<String> listNameRss;
    ArrayList<String> listUrlPicture;
    ArrayList<String> listUrlRSS;
    RSSFeed rss;
    Controller controller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainactivityContext= getApplicationContext();
        controller = new Controller();
        controller.register(this);
        numColumn=3;
        widhtItem = 200;
        elevation = 20;
        listNameRss = new ArrayList<String>();
        listUrlPicture = new ArrayList<String>();
        listUrlRSS = new ArrayList<String>();
        listNameRss.add("Lenta.ru : Новости");
        listUrlPicture.add("https://lenta.ru/images/small_logo.png");
        listUrlRSS.add("https://lenta.ru/rss/news");

        listNameRss.add("Хабр");
        listUrlPicture.add("https://dr.habracdn.net/habr/6013e4d4/images/apple-touch-icon.png");
        listUrlRSS.add("https://habr.com/ru/rss/all/all/?fl=ru");

        listNameRss.add("Хакер");
        listUrlPicture.add("https://xakep.ru/wp-content/uploads/2017/06/xakep-favicon-93x93.png");
        listUrlRSS.add("https://xakep.ru/feed");

        listNameRss.add("4PDA");
        listUrlPicture.add("https://s.4pda.to/heB5pXFz14I2pMyz16i8q.png");
        listUrlRSS.add("https://4pda.ru/feed");

        listNameRss.add("КЛОПС");
        listUrlPicture.add("https://source.klops.ru/images/logo.svg");
        listUrlRSS.add("https://rss.klops.ru/rss");

        creatDisplay(listUrlPicture, listNameRss, 4);

    }

    // мето который сосздает Vied из списка принимает на вход список фото и список имен групп
    private void creatDisplay(ArrayList<String> imageUrl, ArrayList<String> nameRss, int numColumn){
        lv = (LinearLayout) findViewById(R.id.linear_main) ;

        widhtItem = (int)(getDisplay().getWidth()*0.9/numColumn);
        MARGIN = (getDisplay().getWidth() - widhtItem*numColumn)/(numColumn+1);
        gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(numColumn);
        gridLayout.setRowCount((int)(imageUrl.size()/numColumn));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.leftMargin=MARGIN/2;
        lp.rightMargin=MARGIN/2;
        lp.topMargin=0;
        lp.bottomMargin=MARGIN;

        ArrayList<CreatViewtem> arrayAVI = new ArrayList<CreatViewtem>(imageUrl.size());// массив для хранения созданных итемов с тновостями
        for (int i =0; i<imageUrl.size(); i++) {
            cvi = new CreatViewtem(this, BitmapFactory.decodeResource(getResources(), R.drawable.rsserror), nameRss.get(i), elevation, widhtItem, widhtItem);
            arrayAVI.add(cvi);
            View v = cvi.getView();
            ImageView iv = cvi.iv;
            v.setId(i);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int i = v.getId();
                    boolean dd = arrayAVI.get(i).cb.isChecked();
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        arrayAVI.get(i).rl.setBackgroundResource(R.drawable.shape2);}
                    int di = v.getId();
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        arrayAVI.get(i).rl.setBackgroundResource(R.drawable.shape);
                        String adr = getUrLRSS(listUrlRSS.get(i));
                        String suf = getUrLSufixRSS(listUrlRSS.get(i));
                        controller.setRss(adr);
                        controller.setSufixRss(suf);
                        controller.start();}
                    //


                    return true;
                }
            });
            Picasso.with(this)
                    .load(imageUrl.get(i))
                    .transform(new MyTransformation(widhtItem))
                    .placeholder(R.drawable.rss)
                    .error(R.drawable.rsserror)
                    .into(iv);

            gridLayout.addView(v, lp);
        }
        lv.addView(gridLayout);


    }
    private  String getUrLRSS(String fullUrl){
        int posPoint = fullUrl.indexOf(".");
        int posSlash = fullUrl.indexOf("/", posPoint);
        return  fullUrl.substring(0, posSlash+1);
    }
    private  String getUrLSufixRSS(String fullUrl){
        int posPoint = fullUrl.indexOf(".");
        int posSlash = fullUrl.indexOf("/", posPoint);
        return  fullUrl.substring(posSlash+1);
    }


    @Override
    public void returnRss(RSSFeed rss) {
        this.rss=rss;
        String t="" ;
        // Intent intent = new Intent(this, ActivityList.class);
        // startActivity(intent);
    }
}