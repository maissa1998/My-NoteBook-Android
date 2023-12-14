package com.example.mybooknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    TextView signup;
    Button signin;
    EditText password,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        signin = findViewById(R.id.signin);
        signin.setOnClickListener((v)->loginUser());

        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });
    }
    void loginUser(){
        String email = username.getText().toString();
        String pass = password.getText().toString();

        boolean isValidated = validateData(email,pass);
        if (!isValidated){
            return;
        }
        loginAccountInFirebase(email,pass);
    }

    void loginAccountInFirebase(String email,String pass){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(Login.this,MainActivity.class));
                    }else {
                        Utility.showToast(Login.this,"Email not verified, Please verify your email");
                    }
                }else {
                    Utility.showToast(Login.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }

    Boolean validateData(String email,String pass){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Email is invalid");
            return false;
        }
        if (pass.length()<6){
            password.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}