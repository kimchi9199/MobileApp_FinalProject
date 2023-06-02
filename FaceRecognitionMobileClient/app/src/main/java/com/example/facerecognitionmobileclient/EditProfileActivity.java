package com.example.facerecognitionmobileclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    EditText editPhone, editEmail, editUsername, editPassword;
    Button saveButton;
    String phoneUser, emailUser, usernameUser, passwordUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        reference = FirebaseDatabase.getInstance().getReference("users");
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        usernameUser = editUsername.getText().toString();
//        showData();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhoneChanged() || isPasswordChanged() || isUsernameChanged()||isEmailChanged()){
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }

        });
    }

    private boolean isPhoneChanged() {
        String encodedEmail = emailUser.replace(".", ",");
        if (!phoneUser.equals(editPhone.getText().toString())){
            reference.child(encodedEmail).child("phone").setValue(editPhone.getText().toString());
            phoneUser = editPhone.getText().toString();
            // Set the result code and any relevant data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("phone", phoneUser);
            setResult(RESULT_OK, resultIntent);
            return true;
        } else {
            return false;
        }
    }
    private boolean isUsernameChanged() {
        String encodedEmail = emailUser.replace(".", ",");
        if (!usernameUser.equals(editUsername.getText().toString())){
            reference.child(encodedEmail).child("username").setValue(editUsername.getText().toString());
            usernameUser = editUsername.getText().toString();
            return true;
        } else {
            return false;
        }
    }
    private boolean isEmailChanged() {
        if (!emailUser.equals(editEmail.getText().toString())){
            reference.child(emailUser).child("email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString();
            return true;
        } else {
            return false;
        }
    }
    private boolean isPasswordChanged() {
        String encodedEmail = emailUser.replace(".", ",");
        if (!passwordUser.equals(editPassword.getText().toString())){
            reference.child(encodedEmail).child("password").setValue(editPassword.getText().toString());
            passwordUser = editPassword.getText().toString();
            return true;
        } else {
            return false;
        }
    }
//    public void showData() {
//        User user1 = Singleton.getInstance().getData();
//        emailUser = user1.email;
//        usernameUser=user1.username;
//        phoneUser=user1.phone;
//
//
//        editPhone.setText(user1.phone);
//        editEmail.setText(user1.email);
//        editUsername.setText(user1.username);
//
//    }

}