package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import org.opencv.android.OpenCVLoader;

import java.util.Calendar;


public class Puzzle extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private String puzzleData;
    private PuzzleCanvas puzzle;
    private Classifier classifier;
    private GestureDetector detector;
    private Chronometer chronometer;
    private static final int clickTime = 200;

    static {
        if(OpenCVLoader.initDebug()) Log.e("OpenCVDebug", "OpenCV loaded properly");
        else Log.e("OpenCVDebug", "OpenCV Error");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        puzzle = findViewById(R.id.puzzle_view);
        puzzle.setOnTouchListener(this);

        chronometer = findViewById(R.id.chrono);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        Button button = findViewById(R.id.finish_button);
        button.setOnClickListener(this);

        classifier = new Classifier(Util.assetFilePath(this,"myModel2.pt"));

        puzzleData = Util.assetFileString(this, "puzzle1.sudk");

        detector = new GestureDetector(this, new GestureTap());

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId()==R.id.puzzle_view && !puzzle.isfinished) {

            detector.onTouchEvent(motionEvent);

            switch (motionEvent.getAction()) {


                case MotionEvent.ACTION_UP:
                    long endTime = Calendar.getInstance().getTimeInMillis();
                    if(endTime - puzzle.startTime > clickTime) {
                        Bitmap bmap = Bitmap.createBitmap(puzzle.getWidth(), puzzle.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmap);
                        puzzle.draw(canvas);
                        AsyncGame calcul = new AsyncGame();
                        BitmapWrapper bwrap = new BitmapWrapper(bmap, puzzle.getAltX(), puzzle.getAltY(), puzzle.getWidth() / 9, puzzle.getHeight() / 9);
                        calcul.execute(bwrap);
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public String getPuzzleData(){
        return this.puzzleData;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.finish_button){
            chronometer.stop();
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            String temp = Util.assetFileString(this, "highscores.txt");
            String [] highscores = temp.split(" ");
            String format = (time / 100) / 60 + ":" + (time / 100) % 60;
            if(highscores.length<10)
                highscores[highscores.length-1] = format;
            else
                for (int i = 0; i <highscores.length ; i++) {
                    String [] minSecs = highscores[i].split(":");
                    int t = Integer.parseInt(minSecs[0])*60+Integer.parseInt(minSecs[1]);
                    if(time/100 > t) {
                        String tt = highscores[i];
                        highscores[i] = format;
                        int j = i + 1;
                        while (j < highscores.length) {
                            String ttt = highscores[j];
                            highscores[j] = tt;
                            tt = ttt;
                            j++;
                        }
                    }
                }

            puzzle.isfinished = true;
            puzzle.invalidate();
        }
    }


    private class AsyncGame extends AsyncTask<BitmapWrapper, String, Integer> {

        @Override
        protected Integer doInBackground(BitmapWrapper... bwrap) {
            return classifier.classify(bwrap[0]);

        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            puzzle.clearCanvas(-1);
            puzzle.setNumberInGrid(i);
        }
    }

    class GestureTap extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            puzzle.setNumberInGrid(0, e.getX(), e.getY());
            return super.onSingleTapConfirmed(e);
        }
    }
}