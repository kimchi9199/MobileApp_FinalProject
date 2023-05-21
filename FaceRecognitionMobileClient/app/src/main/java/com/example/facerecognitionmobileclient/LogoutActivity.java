package com.example.facerecognitionmobileclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogoutActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    TextView profilePhone, profileEmail, profileUsername, profilePassword;
    TextView  titleUsername;
    Button editProfile, backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        profilePhone = findViewById(R.id.profilePhone);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);

        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        backHome = findViewById(R.id.backButton);

        auth = FirebaseAuth.getInstance();


        user= auth.getCurrentUser();
        showAllUserData();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(LogoutActivity.this, MainActivity.class);
                startActivity(intentMain);
            }
        });
    }

    public void showAllUserData(){
        User user1= Singleton.getInstance().getData();

        titleUsername.setText(user1.username);
        profilePhone.setText(user1.phone);
        profileEmail.setText(user1.email);
        profileUsername.setText(user1.username);
        profilePassword.setText(user1.password);

    }
    public void passUserData(){
        String emailUsername = profileEmail.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(emailUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String phoneFromDB = snapshot.child(emailUsername).child("phone").getValue(String.class);
                    String emailFromDB = snapshot.child(emailUsername).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(emailUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(emailUsername).child("password").getValue(String.class);
                    Intent intent = new Intent(LogoutActivity.this, EditProfileActivity.class);
                    intent.putExtra("phone1", phoneFromDB);
                    intent.putExtra("email1", emailFromDB);
                    intent.putExtra("username1", usernameFromDB);
                    intent.putExtra("password1", passwordFromDB);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}