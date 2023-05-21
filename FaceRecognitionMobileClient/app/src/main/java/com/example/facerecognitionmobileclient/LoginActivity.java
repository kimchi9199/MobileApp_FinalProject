package com.example.facerecognitionmobileclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facerecognitionmobileclient.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    EditText loginemail, loginpassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView signupRedirectText;
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

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginemail = findViewById(R.id.login_email);
        loginpassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar2);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                } else {
                    checkUser();
                }
            }
        });
    }
    public Boolean validateUsername() {
        String val = loginemail.getText().toString();
        if (val.isEmpty()) {
            loginemail.setError("Email cannot be empty");
            return false;
        } else {
            loginemail.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = loginpassword.getText().toString();
        if (val.isEmpty()) {
            loginpassword.setError("Password cannot be empty");
            return false;
        } else {
            loginpassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String emailUsername = loginemail.getText().toString().trim();
        String userPassword = loginpassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(emailUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String encodedEmail = emailUsername.replace(".", ",");
                    loginemail.setError(null);
                    String passwordFromDB = snapshot.child(encodedEmail).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        loginemail.setError(null);
                        String phoneFromDB = snapshot.child(encodedEmail).child("phone").getValue(String.class);
                        String emailFromDB = snapshot.child(encodedEmail).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(encodedEmail).child("username").getValue(String.class);

                        Intent intentmain = new Intent(LoginActivity.this, MainActivity.class);
//
                        startActivity(intentmain);
                        User user = new User(emailFromDB, usernameFromDB, passwordFromDB, phoneFromDB);
                        Singleton.getInstance().setData(user);
                    } else {
                        loginpassword.setError("Invalid Credentials");
                        loginpassword.requestFocus();
                    }
                } else {
                    loginemail.setError("User does not exist");
                    loginemail.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

}