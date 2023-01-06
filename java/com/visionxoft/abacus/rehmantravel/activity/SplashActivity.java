package com.visionxoft.abacus.rehmantravel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.utils.PreferenceHelper;

/**
 * Activity displayed first time when Application is started
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceHelper.clearAgentSession(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 1000);
    }
}
