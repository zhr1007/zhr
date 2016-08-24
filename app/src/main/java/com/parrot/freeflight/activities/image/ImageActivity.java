package com.parrot.freeflight.activities.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parrot.freeflight.R;
import com.parrot.freeflight.ui.hud.Image;

public class ImageActivity extends Activity {

    ImageProcessor imageProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        imageProcessor = new ImageProcessor();
        setContentView(R.layout.activity_image);
        ImageView imageBefore = (ImageView) findViewById(R.id.image_before);
        ImageView imageAfter = (ImageView) findViewById(R.id.image_after);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth/3, screenHeight);
        imageBefore.setLayoutParams(layoutParams);
        imageAfter.setLayoutParams(layoutParams);

        Bitmap before = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        imageBefore.setImageBitmap(before);
        imageAfter.setImageBitmap(imageProcessor.processImage(before));
    }
}
