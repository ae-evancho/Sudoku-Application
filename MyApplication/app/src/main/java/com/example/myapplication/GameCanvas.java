package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class GameCanvas extends View {

    private Paint paint;
    private Path path;
    private boolean b;
    private Canvas drawCanvas;
    private Bitmap drawBitmap;
    private Bitmap lookMap;
    private ArrayList<Bitmap> lookMaps = new ArrayList();
    private boolean cc=false;

    public GameCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path= new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15f);
        b=false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);


    }

       public boolean onTouchEvent(MotionEvent event){
        float y=event.getY();
        float x=event.getX();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
                b=false;
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public Path getPath(){
        return this.path;
    }

    public void clearCanvas(){
        cc=true;
        path=new Path();
        invalidate();
    }
}
