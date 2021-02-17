package com.example.rsstt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

public class CreatViewtem implements Serializable {
    View view;
    ImageView iv;
    TextView tv;
    CheckBox cb;

    RelativeLayout rl;

    CreatViewtem(Activity activity, Bitmap bitmap, String title, int elevate, int height, int widht){
        height = (int)(height*0.9);
        widht = (int)(widht*0.9);

        LayoutInflater Linflater = LayoutInflater.from(MainActivity.mainactivityContext);
        view = Linflater.inflate(R.layout.item_layout, null, false);

        rl = (RelativeLayout)view.findViewById(R.id.layot_main);

        iv = (ImageView) view.findViewById(R.id.imageView2);
        iv.setMinimumHeight(height);
        iv.setMaxHeight(height);
        iv.setMaxWidth(height);
        iv.setMinimumWidth(height);
        tv = (TextView) view.findViewById(R.id.textView);
        cb = (CheckBox)  view.findViewById(R.id.checkBox);
        tv.setText(title);
        tv.setWidth(widht);
        bitmap=Bitmap.createScaledBitmap(bitmap, widht, height, false);

        iv.setImageBitmap(bitmap);
        view.setElevation(elevate);

    }

    public View getView(){
        return view;
    }
    public String getName(){
        return tv.getText().toString();
    }
    public CheckBox getCheckBox(){return cb;}

}
