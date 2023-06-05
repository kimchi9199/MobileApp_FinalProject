package com.example.mobileapp_final.Model_Detect;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobileapp_final.MainActivity;
import com.example.mobileapp_final.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.LogRecord;

public class face_Recognition {

    //implementation of tensorflow
    private Interpreter interpreter = null;
    //define input size of model
    private int INPUT_SIZE = 0;

    //define model input size
    private int modelInputSize = 0;
    //define height and width of frame
    private int inputImageHeight = 0;
    private int inputImageWidth = 0;
    //now define Gpudelegate
    private GpuDelegate gpuDelegate=null;// run model using GPU
    private CascadeClassifier cascadeClassifier;

    private  int FLOAT_TYPE_SIZE = 4;
    private int  PIXEL_SIZE = 3;

    private AssetManager assetManager;
    private Context context;
    private String modelFileName;
    private int input_size;
    private static final int SCALING_FACTOR = 10;
    private String identity;
    FaceDetector faceDetector;
    HashMap<String, ArrayList<float[][]>> StoredFaceVectorHashMap;
    HashMap<String, Float> FaceCosineSimilarityScoreHashMap = new HashMap<>();
    boolean isFaceVectorFileExist;
    final float THRESHOLD = 0.87f;

    //create
    public face_Recognition(AssetManager assetManager, Context context, String modelFileName, int input_size) throws IOException{

        this.assetManager = assetManager;
        this.context = context;
        this.modelFileName = modelFileName;
        this.input_size = input_size;

        //get input-size
        INPUT_SIZE = input_size;


        // Initialize interpreter with GPU delegate
        Interpreter.Options options = new Interpreter.Options();
        CompatibilityList compatList = new CompatibilityList();
        NnApiDelegate nnApiDelegate = null;

        if(compatList.isDelegateSupportedOnThisDevice()){
            // if the device has a supported GPU, add the GPU delegate
            GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
            GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
            options.addDelegate(gpuDelegate);
        } else {
            // if the GPU is not supported, run on 4 threads
            options.setNumThreads(4);
        }
        // Initialize interpreter with NNAPI delegate for Android Pie or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            nnApiDelegate = new NnApiDelegate();
            options.addDelegate(nnApiDelegate);
        }

        options.setUseXNNPACK(true);


        try {
            //load model
            ByteBuffer model = loadModel(assetManager, modelFileName);
            interpreter = new Interpreter(model, options);
            //when model is successfully load
            Log.d("MODEL", "face_Recognition: Model is loaded");

            // Read model input shape from model file
            int[] inputShape = interpreter.getInputTensor(0).shape();
            inputImageWidth = inputShape[1];
            inputImageHeight = inputShape[2];
            modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE; // still has bug

        } catch (Exception e) {
            e.printStackTrace();
        }

        //load haar cascade file
        try {
            //define input stream to read haar cascade file
            InputStream inputStream=context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);

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
        Thread GetStoredFaceVector = new Thread(new Runnable() {
            @Override
            public void run() {
                File path = context.getApplicationContext().getFilesDir();

                // Check if FaceVector.ser exist
                File FaceVectorFile = new File(path + "/FaceVector.ser");
                isFaceVectorFileExist = FaceVectorFile.exists();
                if (!isFaceVectorFileExist) {
                    Log.d("Test", "OK");
                    boolean handler = new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Looks like you haven't added any sample faces yet. " +
                                    "Please add sample face by choosing Face Directory button", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    // read faces in Face Vector JSON file
                    File readFrom = new File(path, "FaceVector.ser");
                    String FaceVectorJSONString;
                    byte[] FaceVector = new byte[(int) readFrom.length()];
                    try {
                        FileInputStream inputStream = new FileInputStream(readFrom);
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        StoredFaceVectorHashMap = (HashMap<String, ArrayList<float[][]>>) objectInputStream.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
        GetStoredFaceVector.start();
    }

//    //create a new function with input and output Mat
//    public Mat recognizeImage(Mat mat_image){
//
//        MatOfRect faces = new MatOfRect();
//       // check cascade classifier is loaded or not
//        if(cascadeClassifier != null)
//        {
//            cascadeClassifier.detectMultiScale(mat_image, faces);
//        }
//
//        //now convert faces to array
//        Rect[] faceArray = faces.toArray();
//
//        //loop through each faces
//        for (Rect rect : faceArray) {
//            //draw rectangle faces
//            Imgproc.rectangle(mat_image, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255),
//                    2);
//
//            // region of interest
//            Rect roi = new Rect((int) rect.tl().x, (int) rect.tl().y,
//                    ((int) rect.br().x) - ((int) rect.tl().x),
//                    ((int) rect.br().y) - ((int) rect.tl().y));
//
//            //roi is used to crop faces from image
//            Mat cropped_rgb = new Mat(mat_image, roi);
//
//            //now convert cropped_rgb to bitmap
//            Bitmap bitmap = null;
//            bitmap = Bitmap.createBitmap(cropped_rgb.cols(), cropped_rgb.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(cropped_rgb, bitmap);
//
//            //Scale bitmap to model input size 96
//            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
//
//            //convert scaleBitmap to byteBuffer
//            //create convertBitmapToByteBuffer function
//            ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaleBitmap);
//
//            //create output
//            float[][] face_value = new float[1][128];
//            if (isFaceVectorFileExist) {
//                interpreter.run(byteBuffer, face_value);
//                for (Map.Entry<String, ArrayList<float[][]>> entry : StoredFaceVectorHashMap.entrySet()) {
//                    String key = entry.getKey();
//                    ArrayList<float[][]> FaceVectorsOfAPerson = entry.getValue();
//                    int NumberOfStoredVector = 0;
//                    float SumOfCosineSimilarity = 0;
//                    float CosineSimilarity = 0;
//                    // Iterate over the face vector array list of a person
//                    for (float[][] vector : FaceVectorsOfAPerson) {
//                        // Calculate Cosine Similarity between two face vectors
//                        CosineSimilarity = CalculateCosineSimilarity(vector, face_value);
//                        SumOfCosineSimilarity += CosineSimilarity;
//                        NumberOfStoredVector += 1;
//                    }
//
//                    // Calculate Average Cosine Similarity
//                    float AverageCosineSimilarity = SumOfCosineSimilarity / NumberOfStoredVector;
//                    FaceCosineSimilarityScoreHashMap.put(key, AverageCosineSimilarity);
//                }
//                float max = Objects.requireNonNull(FaceCosineSimilarityScoreHashMap
//                        .entrySet()
//                        .stream()
//                        .max(Map.Entry.comparingByValue())
//                        .orElse(null)).getValue();
//                if (max >= THRESHOLD) {
//                    // Get identity of the person by choosing the key that has the highest average Cosine Similarity score
//                    identity = FaceCosineSimilarityScoreHashMap
//                            .entrySet()
//                            .stream()
//                            .max(Map.Entry.comparingByValue())
//                            .map(Map.Entry::getKey)
//                            .orElse(null);
//                } else {
//                    identity = "unknown";
//                }
//            }
//
//            //put text on frame
//            Imgproc.putText(mat_image, " " + identity,
//                    new Point((int) rect.tl().x + 10, (int) rect.tl().y + 20),
//                    1, 1.5, new Scalar(255, 255, 255, 150), 2);
//        }
//        return mat_image;
//    }

    //create a new function with input and output Mat
    public void recognizeImage(Mat mat_image, ImageView imageView){

        ImageView mImageView = imageView;
        MatOfRect faces = new MatOfRect();
        // check cascade classifier is loaded or not
        if(cascadeClassifier != null)
        {
            cascadeClassifier.detectMultiScale(mat_image, faces);
        }

        //now convert faces to array
        Rect[] faceArray = faces.toArray();

        //loop through each faces
        for (Rect rect : faceArray) {
            //draw rectangle faces
            Imgproc.rectangle(mat_image, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255),
                    2);

            // region of interest
            Rect roi = new Rect((int) rect.tl().x, (int) rect.tl().y,
                    ((int) rect.br().x) - ((int) rect.tl().x),
                    ((int) rect.br().y) - ((int) rect.tl().y));

            //roi is used to crop faces from image
            Mat cropped_rgb = new Mat(mat_image, roi);

            //now convert cropped_rgb to bitmap
            Bitmap bitmap = null;
            bitmap = Bitmap.createBitmap(cropped_rgb.cols(), cropped_rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped_rgb, bitmap);

            //Scale bitmap to model input size 96
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

            //convert scaleBitmap to byteBuffer
            //create convertBitmapToByteBuffer function
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaleBitmap);

            //create output
            float[][] face_value = new float[1][128];
            if (isFaceVectorFileExist) {
                interpreter.run(byteBuffer, face_value);
                for (Map.Entry<String, ArrayList<float[][]>> entry : StoredFaceVectorHashMap.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<float[][]> FaceVectorsOfAPerson = entry.getValue();
                    int NumberOfStoredVector = 0;
                    float SumOfCosineSimilarity = 0;
                    float CosineSimilarity = 0;
                    // Iterate over the face vector array list of a person
                    for (float[][] vector : FaceVectorsOfAPerson) {
                        // Calculate Cosine Similarity between two face vectors
                        CosineSimilarity = CalculateCosineSimilarity(vector, face_value);
                        SumOfCosineSimilarity += CosineSimilarity;
                        NumberOfStoredVector += 1;
                    }

                    // Calculate Average Cosine Similarity
                    float AverageCosineSimilarity = SumOfCosineSimilarity / NumberOfStoredVector;
                    FaceCosineSimilarityScoreHashMap.put(key, AverageCosineSimilarity);
                }
                float max = Objects.requireNonNull(FaceCosineSimilarityScoreHashMap
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null)).getValue();
                if (max >= THRESHOLD) {
                    // Get identity of the person by choosing the key that has the highest average Cosine Similarity score
                    identity = FaceCosineSimilarityScoreHashMap
                            .entrySet()
                            .stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse(null);
                } else {
                    identity = "unknown";
                }
            }

            //put text on frame
            Imgproc.putText(mat_image, " " + identity,
                    new Point((int) rect.tl().x + 10, (int) rect.tl().y + 20),
                    1, 1.5, new Scalar(255, 255, 255, 150), 2);
        }
        // Convert Mat to Bitmap
        Bitmap result_bitmap = Bitmap.createBitmap(mat_image.cols(), mat_image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat_image, result_bitmap);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(result_bitmap);
            }
        });
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap scaleBitmap) {

        try {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelInputSize);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] pixels = new int[inputImageWidth * inputImageHeight];
            scaleBitmap.getPixels(pixels, 0, scaleBitmap.getWidth(), 0, 0, scaleBitmap.getWidth(), scaleBitmap.getHeight());

            for (int pixelValue: pixels) {
                int r = (pixelValue >> 16 & 0xFF);
                int g = (pixelValue >> 8 & 0xFF);
                int b = (pixelValue & 0xFF);

                // normalize pixel values
                float normalizedPixelValue = (r + b + g) / 3.0f / 255.0f;
                byteBuffer.putFloat(normalizedPixelValue);
            }
            return byteBuffer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    //Function to load model
    private ByteBuffer loadModel(AssetManager assetManager, String modelFileName) throws IOException{
        
        //Description of modelPath
        AssetFileDescriptor assetFileDescriptor= assetManager.openFd(modelFileName);

        //Create a input stream to read model path
        FileInputStream inputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long declaredLength=assetFileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset,declaredLength);
    }

    public Interpreter CreateTFModelInstance(AssetManager assetManager, String modelFileName) throws IOException{
        ByteBuffer model = loadModel(assetManager, modelFileName);
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        return new Interpreter(model, options);
    }

    public float[][] VectorizeFace (Bitmap bitmap) {
        //get input-size
        INPUT_SIZE = this.input_size;
        //set GPU for the interpreter
        Interpreter.Options options = new Interpreter.Options();

        //before load add number of threads
        options.setNumThreads(4);

        // Convert input Bitmap to Mat
        Mat faceMat = new Mat(
                bitmap.getHeight(),
                bitmap.getWidth(),
                CvType.CV_8UC4
        );
        Utils.bitmapToMat(bitmap, faceMat);

        try {
            // load FaceNet model
            ByteBuffer model = loadModel(this.assetManager, this.modelFileName);
            interpreter = new Interpreter(model, options);
            //when model is successfully load
            Log.d("MODEL", "face_Recognition: Model is loaded");

            // Read model input shape from model file
            int[] inputShape = interpreter.getInputTensor(0).shape();
            inputImageWidth = inputShape[1];
            inputImageHeight = inputShape[2];
            modelInputSize = FLOAT_TYPE_SIZE * inputImageWidth * inputImageHeight * PIXEL_SIZE;

        } catch (Exception e) {
            e.printStackTrace();
        }

        //load haar cascade file
        try {
            //define input stream to read haar cascade file
            InputStream inputStream=context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);

            //create a new folder to save classifier
            File cascadeDir = context.getDir("cascade",Context.MODE_PRIVATE);
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
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            Log.d("face_recognition","Classifier is loaded");

        } catch (IOException e)
        {
            e.printStackTrace();
        }


        // Start the vectorization face process
        MatOfRect faces=new MatOfRect();
        // check cascade classifier is loaded or not
        if(cascadeClassifier != null)
        {
            try {
                cascadeClassifier.detectMultiScale(faceMat, faces);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // now convert faces to array
        Rect[] faceArray=faces.toArray();
        // loop through each faces
        for(int i = 0; i < faceArray.length; i++) {
            //draw rectangle faces
            Imgproc.rectangle(faceMat, faceArray[i].tl(), faceArray[i].br(), new Scalar(0, 255, 0, 255),
                    2);

            // region of interest
            Rect roi = new Rect((int) faceArray[i].tl().x, (int) faceArray[i].tl().y,
                    ((int) faceArray[i].br().x) - ((int) faceArray[i].tl().x),
                    ((int) faceArray[i].br().y) - ((int) faceArray[i].tl().y));
            //roi is used to crop faces from image
            Mat cropped_rgb = new Mat(faceMat, roi);
            //now convert cropped_rgb to bitmap
            Bitmap coppedBitmap = null;
            coppedBitmap = Bitmap.createBitmap(cropped_rgb.cols(), cropped_rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped_rgb, coppedBitmap);
            //Scale bitmap to model input size 160
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(coppedBitmap, INPUT_SIZE, INPUT_SIZE, false);

            //convert scaleBitmap to byteBuffer
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaleBitmap);

            //create output
            float[][] face_value = new float[1][128];
            try {
                interpreter.run(byteBuffer, face_value);
                return face_value;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String modelFileName) {
        this.modelFileName = modelFileName;
    }

    private float[] FlattenArrayList (float[][] array) {
        int rows = array.length;
        int cols = array[0].length;
        float[] flattenedArray  = new float[rows * cols];
        int index = 0;
        for (float[] row : array) {
            for (float value : row) {
                flattenedArray[index] = value;
                index++;
            }
        }
        return flattenedArray;
    }

    private float CalculateDotProduct (float[] vector1, float[] vector2) {
        float dotProduct = 0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
        }
        return dotProduct;
    }

    private float CalculateVectorLength(float[] vector) {
        float sumOfSquares = 0;
        for (float value : vector) {
            sumOfSquares += value * value;
        }
        return (float) Math.sqrt(sumOfSquares);
    }

    private float CalculateCosineSimilarity(float[][] vector1, float[][] vector2) {
        // Flatten vectors (convert 2D vector to 1D vector)
        float[] flattenedVector1 = FlattenArrayList(vector1);
        float[] flattenedVector2 = FlattenArrayList(vector2);

        // Calculate length of each flattened vector
        float lengthFlattenedVector1 = CalculateVectorLength(flattenedVector1);
        float lengthFlattenedVector2 = CalculateVectorLength(flattenedVector2);

        // Calculate dot product of two vectors
        float dotProduct = CalculateDotProduct(flattenedVector2, flattenedVector1);

        // Calculate Cosine Similarity between two face vectors
        float CosineSimilarity = dotProduct / (lengthFlattenedVector1 * lengthFlattenedVector2);

        return CosineSimilarity;
    }
}
