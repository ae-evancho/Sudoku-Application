package com.example.myapplication;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.IValue;
import org.pytorch.Tensor;
import org.pytorch.Module;
import org.pytorch.torchvision.TensorImageUtils;


public class Classifier {

    Module model;


    public Classifier(String modelPath){
        model=Module.load(modelPath);
    }

    public Tensor processIMG(BitmapWrapper bwrap){

        Mat img=new Mat();
        Utils.bitmapToMat(bwrap.bmap, img);
        int cellX=(int) (bwrap.x/bwrap.width);
        int cellY=(int) (bwrap.y/bwrap.height);
        float x = cellX * bwrap.width;
        float y = cellY * bwrap.height;

        Rect roi = new Rect((int)(x+1), (int)y, (int)bwrap.width-1, (int)bwrap.height);
        Log.e("test", (int)x+" "+(int)y+" "+(int)bwrap.width+" "+(int)bwrap.height);
        Mat imgCropped = new Mat(img, roi);
        Mat imgScaled=new Mat();
        Size size=new Size(28,28);
        Imgproc.resize(imgCropped, imgScaled,size);



        float [] data = new float[100*28*28];

        for (int i = 0; i <imgScaled.rows() ; i++) {
            for (int j = 0; j <imgScaled.cols() ; j++) {
                if(imgScaled.get(i,j)[0]==255) data[i*imgScaled.rows()+j]=0.0f;
                else data[i*imgScaled.rows()+j]=1.0f;
            }
        }

        long[] shape={100,28,28};
        Tensor finalTensor=Tensor.fromBlob(data, shape);
        data= finalTensor.getDataAsFloatArray();


        String aff="";
        for (int i = 0; i < 28*28; i++) {
            if(i%28==0) aff=aff+"\n";
            aff=aff+(int)data[i];
        }

        Log.e("Test", aff);
        return finalTensor;
    }

    public int torchMax(float[] score){
        float max=0;
        int index=-1;
        for (int i = 0; i <10 ; i++) {
            if (score[i]>max) {
               max=score[i];
               index=i;
            }
        }

        return index;
    }

    public int classify(BitmapWrapper bwrap){
        Tensor temp = processIMG(bwrap);
        IValue input = IValue.from(temp);
        Tensor output = model.forward(input).toTensor();
        float [] score = output.getDataAsFloatArray();
        int prediction = torchMax(score);
        return prediction;
    }
}
