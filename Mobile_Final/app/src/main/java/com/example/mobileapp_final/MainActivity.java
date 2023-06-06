package com.example.mobileapp_final;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.mobileapp_final.Model.FaceVector;
import com.example.mobileapp_final.Model_Detect.face_Recognition;
import com.example.mobileapp_final.fragment.all_devices_fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    CardView cardView;
    BottomNavigationView bottomNavigationView;
    MaterialButton btnmenu;
    MaterialButton btninstruction;
    DatabaseReference databaseReference;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 1;
    private HashMap<String, ArrayList<float[][]>> FaceVectorHashMap = new HashMap<>();
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new all_devices_fragment());
        if (OpenCVLoader.initDebug()){
            Log.d("OPENCV", "OK");
        }

        gson = new Gson();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bt_navigation_menu);
        btnmenu = findViewById(R.id.Btn_Profile);

        btninstruction = findViewById(R.id.Btn_Instruction);




        // Initialize the Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        // Retrieve the data from the Firebase Realtime Database

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.page_2) {

                    gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });

                }
                return false;
            }
        });
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListShowActivity.class);
                startActivity(intent);
            }
        });

        btninstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetupInstruction.class);
                startActivity(intent);
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        // haven't implemented yet
                        return true;

                    case R.id.page_2:
                        Intent LogoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(LogoutIntent);
                        return true;

                    case R.id.page_3:
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
                }
                return false;
            }
        });
    }

    public void openRecyclerView(View view)
    {
        Intent intent = new Intent(this, Recycler_View.class);
        startActivity(intent);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();

                // Retrieve the document tree for the selected directory
                DocumentFile treeDocumentFile = DocumentFile.fromTreeUri(this, treeUri);
                assert treeDocumentFile != null;
                Thread ScanFace = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Recursive function to read images in each folder
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "We are scanning faces...! Please wait until the process finishes", Toast.LENGTH_SHORT).show();
                                }
                            });
                            ReadAndVectorizeImagesInFolder(treeDocumentFile);

//                            String FaceVectorJSONString = gson.toJson(FaceVectorHashMap);

                            try {
                                // Store the Face Vector HashMap to a file
                                File path = getApplicationContext().getFilesDir();
                                FileOutputStream writer = new FileOutputStream(new File(path, "FaceVector.ser"));
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(writer);
                                objectOutputStream.writeObject(FaceVectorHashMap);
//                                writer.write(FaceVectorJSONString.getBytes());
//                                writer.close();
                                Log.d("CONVERT", "OK");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Scanning faces done", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                ScanFace.start();


            }
        }
    }

    private void ReadAndVectorizeImagesInFolder (DocumentFile documentFile) throws IOException {
        if (documentFile.isDirectory()) {

            FaceVector faceVector = null;

            ContentResolver contentResolver = getContentResolver();

            // Initialize face_Recognition
            face_Recognition faceRecognition = new face_Recognition(
                    MainActivity.this.getAssets(),
                    MainActivity.this,
                    "facenet.tflite",
                    160
            );

            // Read images in the current directory
            DocumentFile[] files = documentFile.listFiles();

            for (DocumentFile file: files) {
                if (file.isFile() && isImageFile(file.getName())) {
                    Uri imageUri = file.getUri();

                    // Create a Bitmap
                    Bitmap FaceBitmap = null;
                    try {
                        InputStream inputStream = contentResolver.openInputStream(imageUri);
                        FaceBitmap = BitmapFactory.decodeStream(inputStream);

                        float[][] face_vector_value = new float[1][128];
                        face_vector_value = faceRecognition.VectorizeFace(FaceBitmap);
                        if (face_vector_value != null) {
                            try {
                                String PersonName = documentFile.getName();
                                if (FaceVectorHashMap.containsKey(PersonName)) {
                                    ArrayList<float[][]> existingFaceVector = FaceVectorHashMap.get(PersonName);
                                    existingFaceVector.addAll(Collections.singleton(face_vector_value));
                                } else {
                                    FaceVectorHashMap.put(PersonName, new ArrayList<>(Collections.singleton(face_vector_value)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (FaceBitmap != null) {
                            FaceBitmap.recycle();
                        }
                    }
                }
            }

            // Recursively process subdirectories
            DocumentFile[] subDirectories = documentFile.listFiles();
            for (DocumentFile subDirectory : subDirectories) {
                if (subDirectory.isDirectory()) {
                    ReadAndVectorizeImagesInFolder(subDirectory);
                }
            }
        }
    }

    // Function to check if a file has an image extension
    private boolean isImageFile(String ImageName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(ImageName);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType != null && mimeType.startsWith("image/");
    }
}