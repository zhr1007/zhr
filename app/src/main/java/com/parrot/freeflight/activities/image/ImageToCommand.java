package com.parrot.freeflight.activities.image;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.parrot.freeflight.activities.game.GameCommand;
import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by shisy13 on 16/8/23.
 */
public class ImageToCommand {
    private final String LOG_TAG = getClass().getSimpleName();

    Context context;
    GLBGVideoSprite glbgVideoSprite;

    int lastCnt = 0;

    public ImageToCommand(Context context){
        this.context = context;
        glbgVideoSprite = new GLBGVideoSprite(context.getResources());
        glbgVideoSprite.setAlpha(1.0f);
    }

    public GameCommand getCommand(){
        GameCommand command = new GameCommand();
        // read photo to bitmap
        Bitmap bitmap = loadImage();

        // use CJM
        bitmap = ImageProcessor.showPicRedBlack(bitmap);
        PointF pointF = ImageProcessor.centroid(bitmap);

        if (pointF.x < -1 && pointF.y < -1){
            command.yaw = 0;
        }
        else if (pointF.x > 0){
            command.yaw = (float) 0.2;
        }
        else if (pointF.x < 0){
            command.yaw = (float) -0.2;
        }
        Log.d(LOG_TAG, "command:"+pointF.x+","+pointF.y+";"+command.yaw);
        return command;
    }

    private Bitmap loadImage(){
//        // find image path of Android
//        FilenameFilter filenameFilter = new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String filename) {
//                return filename.toLowerCase().endsWith(".jpg");
//            }
//        };
//        File[] images = ardroneDir.listFiles(filenameFilter);
//
//        // wait for the photo ready
//        while (images.length == lastCnt){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            images = ardroneDir.listFiles(filenameFilter);
//        }
//        lastCnt = images.length;
//        Arrays.sort(images);
//        for (File image: images){
//            Log.d(LOG_TAG, image.getName());
//        }
//
//        // read the latest photo
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(images[images.length-1].getAbsolutePath(),bmOptions);

        Bitmap bitmap = glbgVideoSprite.getVideoBitmap();
        while (!glbgVideoSprite.updateVideoFrame()){
            bitmap = glbgVideoSprite.getVideoBitmap();
        }
        saveBitmap(bitmap);
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap){


        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Log.d(LOG_TAG, imageDir.getAbsolutePath());
        File ardroneDir = new File(imageDir, "AR.Drone");
        Log.d(LOG_TAG, ardroneDir.getAbsolutePath());
        File save = new File(ardroneDir, "test.jpg");

        try {
            FileOutputStream out = new FileOutputStream(save);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, save.getAbsolutePath());

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
