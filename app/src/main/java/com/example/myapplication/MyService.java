package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CapturePhoto();
    }

    private void CapturePhoto() {

        Log.d("kkkk","Preparing to take photo");
        Camera camera = null;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

//        int frontCamera = 1;
        int backCamera=0;

        Camera.getCameraInfo(backCamera, cameraInfo);

        try {
            camera = Camera.open(backCamera);
            Camera.Parameters camParams = camera.getParameters();
            String supportedIsoValues = camParams.get("iso-values"); //supported values, comma separated String
            camParams.set("iso", "100");
            camera.setParameters(camParams);
        } catch (RuntimeException e) {
            Log.d("kkkk","Camera not available: " + 0);
            camera = null;
            //e.printStackTrace();
        }
        try {
            if (null == camera) {
                Log.d("kkkk","Could not get camera instance");
            } else {
                Log.d("kkkk","Got the camera, creating the dummy surface texture");
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                    camera.startPreview();
                } catch (Exception e) {
                    Log.d("kkkk","Could not set the surface preview texture");
                    e.printStackTrace();
                }
                camera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                        File pictureFileDir= new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "kkkk");

                        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                            pictureFileDir.mkdirs();
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                        String date = dateFormat.format(new Date());
                        String photoFile = "ServiceClickedPic_" + "_" + date + ".jpg";
                        String filename = pictureFileDir.getPath() + File.separator + photoFile;
                        File mainPicture = new File(filename);

                        try {
                            FileOutputStream fos = new FileOutputStream(mainPicture);
                            fos.write(data);
                            fos.close();
                            Log.d("kkkk","image saved at "+filename);
                        } catch (Exception error) {
                            Log.d("kkkk","Image could not be saved");
                        }
                        camera.release();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // this code will be executed after 2 seconds
                            }
                        }, 2000);
                        Log.d("kkkk","Broadcasting to activity started");
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("positive");
                        broadcastIntent.putExtra("Data", "Broadcast Data");
                        sendBroadcast(broadcastIntent);
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }
//        stopSelf();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}