package com.example.mobileapp_final;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    TextView detailDate, detailDesc;
    ImageView detailImage;
    String date, desc;
    Button deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailImage = findViewById(R.id.detailImage);
        detailDate = findViewById(R.id.detailDate);
        detailDesc = findViewById(R.id.detailDesc);
//        deleteButton = findViewById(R.id.deleteButton);
//        editButton = findViewById(R.id.editButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailDate.setText(bundle.getString("Date"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
//                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        reference.child(date).child(desc).removeValue();
//                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getApplicationContext(), ListShowActivity.class));
//                        finish();
//                    }
//                });
//            }
//        });

    }


}