package com.example.myapplication;

import android.graphics.Bitmap;

 class BitmapWrapper {
    Bitmap bmap;
    float x,y;
    float height, width;

    public BitmapWrapper(Bitmap bmap, float x, float y, float width, float height){
        this.bmap = bmap;
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

}
