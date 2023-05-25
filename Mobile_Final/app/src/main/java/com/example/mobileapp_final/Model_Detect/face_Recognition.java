package com.example.mobileapp_final.Model_Detect;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.mobileapp_final.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    //create a new function with input and output Mat
    public Mat recognizeImage(Mat mat_image){
        //rotate mat_image by 90 degree
        Core.flip(mat_image.t(),mat_image,1);
        //do all processing here
        //convert mat_image to grayscale
        Mat grayscaleImage=new Mat();
        //                 input    output          type
        Imgproc.cvtColor(mat_image,grayscaleImage,Imgproc.COLOR_RGB2GRAY);
        //define height and width
        height=grayscaleImage.height();
        width=grayscaleImage.width();
        //define minimum height and width of face in frame
        //below this height and width face will be neglected
        int absoluteFaceSize=(int) (height*0.1);
        MatOfRect faces=new MatOfRect();

        //check cascadeclassifier is loaded or not
        if(cascadeClassifier != null)
        {
            //detect face in frame
            //                                  input          output       scale of face
            cascadeClassifier.detectMultiScale(grayscaleImage,faces,1.1,2,
                                2,new Size(absoluteFaceSize,absoluteFaceSize),new Size());
                                        //minimun size of face
        }
        //now convert faces to array
        Rect[] faceArray=faces.toArray();
        //loop through each faces
        for(int i=0;i<faceArray.length;i++)
        {
            //draw rectangle faces
            //                in/output starting point     end point         Color     R  G  B  alpha
            Imgproc.rectangle(mat_image,faceArray[i].tl(),faceArray[i].br(),new Scalar(0,255,0,255),
                            2);
            //region of interest
            Rect roi=new Rect((int)faceArray[i].tl().x,(int)faceArray[i].tl().y,
                    ((int)faceArray[i].br().x)-((int)faceArray[i].tl().x),
                    ((int)faceArray[i].br().y)-((int)faceArray[i].tl().y));
            //roi is used to crop faces from image
            Mat cropped_rgb=new Mat(mat_image,roi);
            //now convert cropped_rgb to bitmap
            Bitmap bitmap=null;
            bitmap=Bitmap.createBitmap(cropped_rgb.cols(),cropped_rgb.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped_rgb,bitmap);
            //Scale bitmap to model input size 96
            Bitmap scaleBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);
            //convert scaleBitmap to buteBuffer
            //create convertBitmapToByteBuffer function
            ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaleBitmap);

            //create output
            float[][] face_value=new float[1][1];
            interpreter.run(byteBuffer,face_value);
            //To see face_val
            Log.d("face_recognition","Out: "+ Array.get(Array.get(face_value,0),0));
            //run
            //--//            
            float read_face=(float) Array.get(Array.get(face_value,0),0);
            //Read face_value
            //Create a new function input as read_face and output as name
            String face_name=get_face_name(read_face);
            
            //puttext on frame
            //              in/output       text
            Imgproc.putText(mat_image,""+face_name,
                    new Point((int)faceArray[i].tl().x+10,(int)faceArray[i].tl().y+20),
                            1,1.5,new Scalar(255,255,255,150),2);
            //                  size                    color   R   G   B   alpha   thickness
        }


        Core.flip(mat_image.t(),mat_image,0);

        return mat_image;
    }

    private String get_face_name(float read_face) {
        String val="";
        if (read_face>=0 & read_face<0.5)
        {
            val="Chi Pu";
        }
        else if (read_face>=0.5 & read_face<1.5)
        {
            val="Bảo Anh";
        }
        else if (read_face>=1.5 & read_face<2.5)
        {
            val="Đàm Vĩnh Hưng";
        }
        else if (read_face>=2.5 & read_face<3.5)
        {
            val="Akira Phan";
        }
        else if (read_face>=4.5 & read_face<5.5)
        {
            val="Elly Trần";
        }
        else if (read_face>=6.5 & read_face<7.5)
        {
            val="Đỗ Nhật Trường";
        }
        else if (read_face>=7.5 & read_face<8.5)
        {
            val="Đông Nhi";
        }
        else if (read_face>=8.5 & read_face<9.5)
        {
            val="Bích Phương";
        }
        else if (read_face>=9.5 & read_face<10.5)
        {
            val="Quỳnh Kool";
        }
        else if (read_face>=10.5 & read_face<11.5)
        {
            val="Phạm Hương";
        }
        else if (read_face>=11.5 & read_face<12.5)
        {
            val="Hồ Ngọc Hà";
        }
        else if (read_face>=12.5 & read_face<13.5)
        {
            val="Johnny Trí Nguyễn";
        }
        else if (read_face>=13.5 & read_face<14.5)
        {
            val="Angela Phương Trinh";
        }
        else if (read_face>=14.5 & read_face<15.5)
        {
            val="Linh Miu";
        }
        else if (read_face>=15.5 & read_face<16.5)
        {
            val="Chi Dân";
        }
        
        return val;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap scaleBitmap) {
        //define ByteBuffer
        ByteBuffer byteBuffer;
        //define input size
        int input_size=INPUT_SIZE;
        //multiply by 4 if input of model is float
        //multiply by 3 if input of model is RGB
        //if input is GRAY 3->1
        byteBuffer=ByteBuffer.allocateDirect(4*1*input_size*3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[input_size*input_size];
        scaleBitmap.getPixels(intValues,0,scaleBitmap.getWidth(),0,0,
                                scaleBitmap.getWidth(),scaleBitmap.getHeight());
        int pixels=0;
        //loop through each pixels
        for(int i=0;i<input_size;i++)
        {
            for (int j=0;j<input_size;j++)
            {
                //each pixels value
                final int val=intValues[pixels++];
                //put this pixel's values int bytebuffer
                byteBuffer.putFloat((((val>>16)&0xFF))/255.0f);
                byteBuffer.putFloat(((((val>>8))&0xFF))/255.0f);
                byteBuffer.putFloat(((val&0xFF))/255.0f);
                //these things is important
                //it is placing RGB to MSB to LSB

            }
        }
        return byteBuffer;

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
