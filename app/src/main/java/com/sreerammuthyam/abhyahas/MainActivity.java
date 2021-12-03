package com.sreerammuthyam.abhyahas;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.sreerammuthyam.abhyahas.R;


public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent Start = new Intent(MainActivity.this, com.sreerammuthyam.abhyahas.IntroPage.class);
                    startActivity(Start);
                    finish();
                }
            },SPLASH_TIME_OUT);    //Splashscreen timeout code

    }

}