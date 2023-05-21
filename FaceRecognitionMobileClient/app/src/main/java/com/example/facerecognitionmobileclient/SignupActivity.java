package com.example.facerecognitionmobileclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText signupemail, signuppassword, signupusername, signupphone;
    Button btnSingup;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView loginRedirectText;
     FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        signupemail = findViewById(R.id.signup_email);
        signuppassword = findViewById(R.id.signup_password);
        signupusername = findViewById(R.id.signup_username);
        signupphone = findViewById(R.id.signup_phone);
        btnSingup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        loginRedirectText = findViewById(R.id.loginRedirectText);



        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                mAuth = FirebaseAuth.getInstance();
                String phone = signupphone.getText().toString();
                String email = signupemail.getText().toString();
                String username = signupusername.getText().toString();
                String password = signuppassword.getText().toString();
//                User user = new User( email, username, password,phone);
//                reference.child(email).setValue(user);

                // Check if email exists in the database
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(SignupActivity.this, "Please enter an username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Please enter an password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignupActivity.this, "Please enter an phone", Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Email exists in the database
                            Toast.makeText(SignupActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email does not exist, proceed with creating the new
                            String encodedEmail = email.replace(".", ",");
                            User user = new User(email, username, password, phone);
                            reference.child(encodedEmail).setValue(user);
                            Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                        Toast.makeText(SignupActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}
