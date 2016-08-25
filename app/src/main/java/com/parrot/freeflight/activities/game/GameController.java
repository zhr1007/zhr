package com.parrot.freeflight.activities.game;

import android.util.Log;

import com.parrot.freeflight.activities.image.ImageToCommand;
import com.parrot.freeflight.service.DroneControlService;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shisy13 on 16/8/24.
 */
public class GameController {
    final String LOG_TAG = getClass().getSimpleName();
    GameActivity gameActivity;
    DroneControlService controlService;

    Timer commandTimer;
    Thread controlThread;
    ImageToCommand imageToCommand;

    int ardroneStatus = 0;

    public GameController(GameActivity gameActivity, DroneControlService controlService){
        this.gameActivity = gameActivity;
        this.controlService = controlService;
        imageToCommand = new ImageToCommand(gameActivity);
        commandTimer = new Timer();
    }

    public void start(){
        controlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                controlService.switchCamera(); //switch to the camera below
                controlService.triggerTakeOff();
                try {
                    Thread.sleep(5000);     // wait takeoff
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                controlService.moveForward((float) 0.2); // slowly move forward
                while (ardroneStatus == 0){
//                    controlService.takePhoto();
                    GameCommand command = imageToCommand.getCommand();
                    controlService.setYaw(command.yaw);
                }
            }
        });

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, gameActivity, new BaseLoaderCallback(gameActivity) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(LOG_TAG, "OpenCV loaded successfully");
                        controlThread.start();
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        });
    }

    public void stop(){
        controlService.triggerTakeOff();
        ardroneStatus = -1;
    }
}
