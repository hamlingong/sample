package com.hamlingong.sample.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public String TAG = "hamlingong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btText = (Button)findViewById(R.id.bt_test);
        btText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "x: " + v.getX() + "" + v.getRotationX() + v.getRotationY()
                        + v.getRotation());

            }
        });
        Log.d(TAG, "x: " + btText.getX() + ", getTranslationX:" + btText.getTranslationX() +
                    ", getTranslationY:" + btText.getTranslationY() +
                    ", getLeft:" + btText.getLeft() + ", getRight:" + btText.getRight() +
                    ", getBottom:" + btText.getBottom());

    }
}
