package com.example.mobileapp_final.Model_Detect;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.mobileapp_final.R;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class face_Recognition {
    //implementation of tensorflow
    private Interpreter interpreter;
    //define input size of model
    private int INPUT_SIZE;
    //define height and width of frame
    private int height=0;
    private int width=0;
    //now define Gpudelegate
    private GpuDelegate gpuDelegate=null;// run model using GPU
    private CascadeClassifier cascadeClassifier;

    //create
    public face_Recognition(AssetManager assetManager, Context context, String modelPath, int input_size) throws IOException{

        //get inputsize
        INPUT_SIZE = input_size;
        //set GPU for the interpreter
        Interpreter.Options options = new Interpreter.Options();
        gpuDelegate=new GpuDelegate();

        //before load add number of threads
        options.setNumThreads(4);
        //load model

        interpreter=new Interpreter(loadModel(assetManager,modelPath),options);
        //when model is successfully load
        Log.d("face_Recognition", "face_Recognition: Model is loaded");
        //load haar cascade file
        try {
            //define input stream to read haar cascade file
            InputStream inputStream=context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);

            //create a new folder to save classifier
            File cascadeDir=context.getDir("cascade",Context.MODE_PRIVATE);
            //create a new cascade file in that folder
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt");
            //Define output stream to save haarcascade_frontalface_alt in mCascadefile
            FileOutputStream outputStream=new FileOutputStream(mCascadeFile);
            //create a empty file buffer to store byte
            byte[] buffer=new byte[4096];
            int byteRead;
            //Read byte in loop, when it read -1 that means no data to read
            while ((byteRead=inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer,0,byteRead);
            }
            //when reading file was completed
            inputStream.close();
            outputStream.close();

            //load cascade classifier
            cascadeClassifier=new CascadeClassifier(mCascadeFile.getAbsolutePath());
            Log.d("face_recognition","Classifier is loaded");

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    //Function to load model
    private MappedByteBuffer loadModel(AssetManager assetManager, String modelPath) throws IOException{
        //Decription of modelPath
        AssetFileDescriptor assetFileDescriptor= assetManager.openFd(modelPath);
        //Create a inputstream to read model path
        FileInputStream inputStream = new FileInputStream((assetFileDescriptor.getFileDescriptor()));
        FileChannel fileChannel=inputStream.getChannel();

        long startOffset=assetFileDescriptor.getDeclaredLength();
        long declaredLength=assetFileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset,declaredLength);
    }


}
