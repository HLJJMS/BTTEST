package com.example.lanyatest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Myrunable myrunable = new Myrunable();
        Thread mt11 = new Thread(myrunable);
        mt11.start();
    }


    class Myrunable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);//卖票速度是1s一张
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}
