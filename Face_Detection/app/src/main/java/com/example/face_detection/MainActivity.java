package com.example.face_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    File cascFile;
    CascadeClassifier faceDetector;
    private Mat mRgba,mGray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView=(JavaCameraView) findViewById(R.id.javaCamView);

        if(OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this, baseCallback);
        }
        else
        {
            baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        javaCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat();
        mGray = new Mat();

    }

    @Override
    public void onCameraViewStopped() {

        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        //detect face

        MatOfRect facDetections = new MatOfRect();
        faceDetector.detectMultiScale(mRgba,facDetections);

        for (Rect rect: facDetections.toArray())
        {
            Imgproc.rectangle(mRgba, new Point(rect.x,rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(255,0,0));
        }
        return mRgba;
    }

    private BaseLoaderCallback baseCallback= new BaseLoaderCallback(this) {
        FileOutputStream fileOutputStream;

        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir= getDir("cascade", Context.MODE_PRIVATE);
                    cascFile = new File(cascadeDir,"haarcascade_frontalface_alt2");

                    try {
                        fileOutputStream = new FileOutputStream(cascFile);
                        byte[] buffer = new byte[65536];
                        int bytesRead;
                        while((bytesRead = inputStream.read(buffer)) != -1)
                        {

                            fileOutputStream.write(buffer,0,bytesRead);
                        }
                        inputStream.close();
                        fileOutputStream.close();

                        faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());

                        if (faceDetector.empty())
                        {
                            faceDetector=null;
                        }
                        else
                        {
                            cascadeDir.delete();
                            javaCameraView.enableView();

                        }
                        break;


                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }
                default:
                {
                    super.onManagerConnected(status);
                }
                break;

            }

        }
    };
}