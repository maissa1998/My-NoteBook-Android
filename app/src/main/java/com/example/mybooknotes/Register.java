package com.example.mybooknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText addEmail,password,confpass;
    Button button;
    TextView loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        addEmail = findViewById(R.id.addEmail);
        password = findViewById(R.id.password);
        confpass = findViewById(R.id.confpass);
        button = findViewById(R.id.button);
        loginBtn = findViewById(R.id.loginBtn);

        button.setOnClickListener(v-> createAccount());
        loginBtn.setOnClickListener(v-> finish());
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Login.class);
                startActivity(i);
            }
        });

    }
    void createAccount(){
        String email = addEmail.getText().toString();
        String pass = password.getText().toString();
        String passconfirm = confpass.getText().toString();

        boolean isValidated = validateData(email,pass,passconfirm);
        if (!isValidated){
            return;
        }
        createAccountInFirebase(email,pass);

    }

    void createAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Utility.showToast(Register.this,"Successfully create account,Cheack email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            Utility.showToast(Register.this,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    Boolean validateData(String email,String pass,String passconfirm){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            addEmail.setError("Email is invalid");
            return false;
        }
        if (pass.length()<6){
            password.setError("Password length is invalid");
            return false;
        }
        if (!pass.equals(passconfirm)){
            confpass.setError("Password not match");
            return false;
        }
        return true;
    }
}