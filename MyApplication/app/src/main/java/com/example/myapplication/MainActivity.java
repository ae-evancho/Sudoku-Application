package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGame = findViewById(R.id.id_start_game);
        Button highscores= findViewById(R.id.id_highscores);
        Button exit = findViewById(R.id.id_exit);

        startGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                startActivity(new Intent(MainActivity.this, Puzzle.class));
            }
        });
        highscores.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                startActivity(new Intent(MainActivity.this, HighScores.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                finish();
                System.exit(0);
            }
        });
    }


}