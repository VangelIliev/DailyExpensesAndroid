package com.example.familyexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyexpenses.Validation.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText EmailLogin;
    private EditText PasswordLogin;
    private Button LogInBtn;
    private TextView tvSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmailLogin = findViewById(R.id.EmailLogin);
        PasswordLogin = findViewById(R.id.PasswordLogin);
        LogInBtn = findViewById(R.id.LogInBtn);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    public void SignUp(View view){
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
    public void LogIn(View view){
        String email = EmailLogin.getText().toString().trim();
        String password = PasswordLogin.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();
        if(email.isEmpty()){
            EmailLogin.setError("Email is required!");
            EmailLogin.requestFocus();
            return;
        }
        if(!Validation.validateEmail(email)){
            EmailLogin.setError("Email is incorrect try again!");
            EmailLogin.requestFocus();
            return;
        }

        if(password.isEmpty()){
            PasswordLogin.setError("Password is required");
            PasswordLogin.requestFocus();
            return;
        }
        if(password.length() < 6){
            PasswordLogin.setError("Password must be atleast 6 symbols");
            PasswordLogin.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,LogInActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this,"Failed to login! Please check your credentials",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}