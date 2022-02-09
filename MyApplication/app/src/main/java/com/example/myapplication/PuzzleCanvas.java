package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * TODO: document your custom view class.
 */
public class PuzzleCanvas extends View {
    private Path primaryPath;
    private Paint primaryPaint;
    private Paint wrongPaint;
    private ArrayList<Path> pathList = new ArrayList<>();
    private float height, width;
    private String puzzleData;
    private ArrayList<Integer> numbers;
    private float x,y;
    public long startTime;
    public boolean isfinished;
    Puzzle puzzle;


    public PuzzleCanvas(Context context) {
        super(context);
        this.puzzle = (Puzzle) context;
        isfinished = false;

    }

    public PuzzleCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.puzzle = (Puzzle) context;
        isfinished = false;
        primaryPath= new Path();
        primaryPaint= new Paint();
        primaryPaint.setAntiAlias(true);
        primaryPaint.setColor(Color.BLACK);
        primaryPaint.setStrokeJoin(Paint.Join.ROUND);
        primaryPaint.setStyle(Paint.Style.STROKE);
        primaryPaint.setStrokeWidth(height*0.75f);
        wrongPaint = new Paint();
        wrongPaint.setColor(getResources().getColor(R.color.incorrect));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.height=h/9f;
        this.width=w/9f;

        numbers = new ArrayList<>();
        puzzleData = puzzle.getPuzzleData();
        char ch;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                ch = puzzleData.charAt(i * 9 + j);
                numbers.add(Integer.parseInt(String.valueOf(ch)));
            }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isfinished)
            if(isSolved().isEmpty())
                canvas.drawColor(getResources().getColor(R.color.correct));
            else {
                for (int i = 0; i < isSolved().size() ; i++) {
                    int cellY = isSolved().get(i)/9;
                    int cellX = isSolved().get(i)%9;

                    @SuppressLint("DrawAllocation") Rect rect = new Rect((int)(cellX*width), (int)(cellY*height),(int)((cellX+1)*width), (int)((cellY+1)*height));
                    canvas.drawRect(rect, wrongPaint);
                }
            }
        Paint dark= new Paint();
        Paint light= new Paint();
        light.setColor(Color.argb(0x64,0x56,0x64,0x8f));
        dark.setColor(Color.BLACK);

        for (int i = 0; i < 9; i++) {
            canvas.drawLine(0,i*height, width*9, i*height, light);
            canvas.drawLine(i*width, 0, i*width, height*9, light);
        }

        for (int i = 0; i < 9; i++) {
            if(i%3==0){
                canvas.drawLine(0,i*height, width*9, i*height, dark);
                canvas.drawLine(i*width, 0, i*width, height*9, dark);
            }
        }


        Paint numberStatPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        numberStatPaint.setColor(Color.BLACK);
        numberStatPaint.setTextSize(height*0.7f);
        numberStatPaint.setTextAlign(Paint.Align.CENTER);

        Paint numberDynPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        numberDynPaint.setColor(Color.BLUE);
        numberDynPaint.setTextSize(height*0.7f);
        numberDynPaint.setTextAlign(Paint.Align.CENTER);
        //numberDynPaint.setTextScaleX(height/width);

        Paint.FontMetrics metrics = numberStatPaint.getFontMetrics();

        float x= width/2;
        float y= height/2 - (metrics.ascent + metrics.descent)/2;
        char ch1,ch2;

        puzzleData = puzzle.getPuzzleData();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++){
                ch1 = String.valueOf(numbers.get(i * 9 + j)).charAt(0);
                ch2 = puzzleData.charAt(i * 9 + j);
                if(ch1!='0')
                    if(ch2!='0')
                        canvas.drawText(String.valueOf(ch1), j*width + x, i*height + y, numberStatPaint);
                    else
                        canvas.drawText(String.valueOf(ch1), j*width + x, i*height + y, numberDynPaint);
                else
                    canvas.drawText("", j*width + x, i*height + y, numberStatPaint);}

        canvas.drawPath(primaryPath, primaryPaint);


    }

    public boolean onTouchEvent(MotionEvent event){
         y=event.getY();
         x=event.getX();
         if(!isfinished) {
             switch (event.getAction()) {
                 case MotionEvent.ACTION_DOWN:
                     startTime = Calendar.getInstance().getTimeInMillis();
                     if (freeCell(x, y))
                         primaryPath.moveTo(x, y);
                     return true;
                 case MotionEvent.ACTION_MOVE:
                     if (freeCell(x, y))
                         primaryPath.lineTo(x, y);
                     break;
                 case MotionEvent.ACTION_UP:
                     break;
                 default:
                     return false;
             }
             invalidate();
         }
        return true;
    }

    public boolean freeCell(float x, float y){
        int cellX=(int) (x/width);
        int cellY=(int) (y/height);

        int a=numbers.get(cellY*9+cellX);
        Log.e("yummy", width+" "+x+" "+height+" "+y);
        if(a == 0)
            return true;
        else
            return false;
    }

    public void clearCanvas(int pathNb){
        if(pathNb == -1)
            primaryPath=new Path();
        else {
            Path p = pathList.get(pathNb);
            p = new Path();
        }
        invalidate();
    }

    public float getAltX() {
        return this.x;
    }
    public float getAltY() {
        return this.y;
    }

    public void setNumberInGrid(int v){
        int cellX=(int) (x/width);
        int cellY=(int) (y/height);

        if(v != 0)
            this.numbers.set(cellX+cellY*9, v);
        else
            if(puzzleData.charAt(cellX+cellY*9) == '0')
                this.numbers.set(cellX+cellY*9, v);
            else ;
    }

    public void setNumberInGrid(int v, float x, float y){
        int cellX=(int) (x/width);
        int cellY=(int) (y/height);

        if(v != 0)
            this.numbers.set(cellX+cellY*9, v);
        else
        if(puzzleData.charAt(cellX+cellY*9) == '0')
            this.numbers.set(cellX+cellY*9, v);
        else ;

        invalidate();
    }

    public ArrayList<Integer> isSolved(){
        ArrayList<Integer> wrongCells = new ArrayList<>();
        String solved = Util.assetFileString(puzzle, "puzzle1_solved.sudk");
        for (int i = 0; i < 9*9 ; i++) {
           if(this.numbers.get(i) != Integer.parseInt(String.valueOf(solved.charAt(i))))
               wrongCells.add(i);
        }
        return wrongCells;
    }
}
