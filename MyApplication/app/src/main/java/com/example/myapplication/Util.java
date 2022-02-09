package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Util {
    public static String assetFilePath (Context context, String assetName){
        File file = new File(context.getFilesDir(), assetName);

        try(InputStream is=context.getAssets().open(assetName)){
            try(OutputStream os = new FileOutputStream(file)){
                byte[] buffer = new byte[4*1024];
                int read;
                while((read=is.read(buffer)) !=-1){
                    os.write(buffer,0,read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("pytorchandroid", "error process asset"+assetName + " to file path");
        }
        return null;
    }

    public static String assetFileString(Context context, String assetName){
        String data="";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(assetName)));
            String line;
            while ((line = reader.readLine()) != null){
                data=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
        }

        return data;
    }

    public static void writeToAssetFile(String[] highscores){

    }
}
