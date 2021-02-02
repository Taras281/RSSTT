package com.example.rsstt;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class MyTransformation implements Transformation {
    double maxWidht;
    MyTransformation(int widht){
        maxWidht = widht;
    }
    @Override public Bitmap transform(Bitmap source) {
        double wi =  source.getWidth();
        double coef = wi/maxWidht;
        Bitmap result = Bitmap.createScaledBitmap(source, (int)(source.getWidth()/coef), (int)(source.getHeight()/coef), false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override public String key() { return "square()"; }
}
