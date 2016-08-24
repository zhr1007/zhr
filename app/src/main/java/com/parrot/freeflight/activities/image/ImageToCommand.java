package com.parrot.freeflight.activities.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.parrot.freeflight.activities.game.GameCommand;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by shisy13 on 16/8/23.
 */
public class ImageToCommand {
    private final String LOG_TAG = getClass().getSimpleName();

    Context context;

    int lastCnt = 0;

    public ImageToCommand(Context context){
        this.context = context;
    }

    public GameCommand getCommand(){
        GameCommand command = new GameCommand();
        Bitmap bitmap = loadImage();
        bitmap = ImageProcessor.showPicRedBlack(bitmap);
        PointF pointF = ImageProcessor.centroid(bitmap);
        if (pointF.x > 0){
            command.roll = (float) 0.5;
        }
        else if (pointF.x < 0){
            command.roll = (float) -0.5;
        }
        return command;
    }

    private Bitmap loadImage(){
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Log.d(LOG_TAG, imageDir.getAbsolutePath());
        File ardroneDir = new File(imageDir, "AR.Drone");
        Log.d(LOG_TAG, ardroneDir.getAbsolutePath());
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".jpg");
            }
        };
        File[] images = ardroneDir.listFiles(filenameFilter);
        while (images.length == lastCnt){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            images = ardroneDir.listFiles(filenameFilter);
        }
        lastCnt = images.length;
        Arrays.sort(images);
        for (File image: images){
            Log.d(LOG_TAG, image.getName());
        }
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(images[images.length-1].getAbsolutePath(),bmOptions);
        return bitmap;
    }
}
