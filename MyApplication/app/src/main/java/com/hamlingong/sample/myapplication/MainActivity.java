package com.hamlingong.sample.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public String TAG = "hamlingong";
    private SimpleDraweeView mBaselineJpegView;
    private SimpleDraweeView mProgressiveJpegView;
    private SimpleDraweeView mAnimatedGifView;
    private SimpleDraweeView mDataWebpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(listeners)
//                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .build();
        Fresco.initialize(this, config);

        setContentView(R.layout.activity_main);
        Button btText = (Button) findViewById(R.id.bt_test);
        btText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "x: " + v.getX() + "" + v.getRotationX() + v.getRotationY()
                        + v.getRotation());

            }
        });

        mBaselineJpegView = (SimpleDraweeView) findViewById(R.id.baseline_jpeg);
        mProgressiveJpegView = (SimpleDraweeView) findViewById(R.id.progressive_jpeg);
//        mAnimatedGifView = (SimpleDraweeView) findViewById(R.id.animated_gif);
//        mDataWebpView = (SimpleDraweeView) findViewById(R.id.data_webp);
//
        DraweeController animatedWebpController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(Uri.parse("https://www.gstatic.com/webp/animated/1.webp"))
                .build();
        mBaselineJpegView.setController(animatedWebpController);

        DraweeController animatedGifController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(Uri.parse("https://s3.amazonaws.com/giphygifs/media/4aBQ9oNjgEQ2k/giphy.gif"))
                .build();
        mProgressiveJpegView.setController(animatedGifController);

        Log.d(TAG, "x: " + btText.getX() + ", getTranslationX:" + btText.getTranslationX() +
                ", getTranslationY:" + btText.getTranslationY() +
                ", getLeft:" + btText.getLeft() + ", getRight:" + btText.getRight() +
                    ", getBottom:" + btText.getBottom());

    }
}
