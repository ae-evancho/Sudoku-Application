package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

import java.io.File;

public class Game extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {


    Classifier classifier;
    TextView print;
    GameCanvas game;
    ImageView iv;
    static {
        if(OpenCVLoader.initDebug()) Log.e("OpenCVDebug", "OpenCV loaded properly");
        else Log.e("OpenCVDebug", "OpenCV Error");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Button but = findViewById(R.id.id_show_image);
        game = findViewById(R.id.canvas_id);
        game.setOnTouchListener(this);
        but.setOnClickListener(this);
        print = findViewById(R.id.textView2);
        print.setText(" ");
        classifier = new Classifier(Util.assetFilePath(this,"myModel2.pt"));

    }


    public void onClick(View view) {
        if(view.getId()==R.id.id_show_image){

            Bitmap bmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.number);
            ImageView iv = findViewById(R.id.my_image);
            iv.setImageBitmap(bmap);
           // int what=classifier.classify(bmap);
           // print.setText(String.valueOf(what));
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId()==R.id.canvas_id) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    /*
                    game.setDrawingCacheEnabled(true);
                    game.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                    Bitmap bmap = game.getDrawingCache();*/

                    Bitmap bmap= Bitmap.createBitmap(game.getWidth(), game.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bmap);
                    game.draw(canvas);
                    iv = findViewById(R.id.my_image);
                    AsyncGame calcul = new AsyncGame();
                    calcul.execute(bmap);
                    iv.setImageBitmap(bmap);

                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public int getPctID(int what){
        switch (what){
            case 0: return R.drawable.im0;
            case 1: return R.drawable.im1;
            case 2: return R.drawable.im2;
            case 3: return R.drawable.im3;
            case 4: return R.drawable.im4;
            case 5: return R.drawable.im5;
            case 6: return R.drawable.im6;
            case 7: return R.drawable.im7;
            case 8: return R.drawable.im8;
            case 9: return R.drawable.im9;
            default: return -1;
        }
    }

    private class AsyncGame extends AsyncTask<Bitmap, String, String>{

        @Override
        protected String doInBackground(Bitmap... bmap) {
          //  int predict = classifier.classify(bmap[0]);
          //  String result = String.valueOf(predict);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            print.append(s);
            game.clearCanvas();

        }
    }
}