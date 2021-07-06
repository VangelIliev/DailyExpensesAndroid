package com.example.familyexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyexpenses.Models.UserModel;
import com.example.familyexpenses.Validation.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView UsernameInputRegister;
    private TextView EmailRegisterInput;
    private TextView PasswordRegisterInput;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void Register(View view){
        UsernameInputRegister = findViewById(R.id.UsernameInputRegister);
        EmailRegisterInput = findViewById(R.id.EmailRegisterInput);
        PasswordRegisterInput = findViewById(R.id.PasswordRegisterInput);
        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("User");
        db = FirebaseFirestore.getInstance();
        String username = UsernameInputRegister.getEditableText().toString();
        String email = EmailRegisterInput.getEditableText().toString();
        String password = PasswordRegisterInput.getEditableText().toString();
        if(username.isEmpty()){
            UsernameInputRegister.setError("Username is required!");
            UsernameInputRegister.requestFocus();
            return;
        }
        if(password.isEmpty()){
            PasswordRegisterInput.setError("Password is required!");
            PasswordRegisterInput.requestFocus();
            return;
        }
        if(email.isEmpty()){
            EmailRegisterInput.setError("Email is required!");
            EmailRegisterInput.requestFocus();
            return;
        }

        if(!Validation.validateEmail(email)){
            EmailRegisterInput.setError("Email is incorrect try again!");
            EmailRegisterInput.requestFocus();
            return;

        }
        if(!Validation.isValidUsername(username)){
            UsernameInputRegister.setError("Username is invalid try again!");
            UsernameInputRegister.requestFocus();
            return;
        }
        if(password.length() < 6){
            PasswordRegisterInput.setError("Password must be atleast 6 symbols! try again");
            PasswordRegisterInput.requestFocus();
            return;
        }
        // if we have got so far everything is fine save in db
        Map<String,Object> user = new HashMap<>();
        user.put("First Name",username);
        user.put("Password",password);
        user.put("Email",email);
        db.collection("Users").add(user);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // if there is no such user registered
                    UserModel model = new UserModel(username, email, password);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(model).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull  Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this,"User has been registered successfully",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                    }
                                    else{
                                        Toast.makeText(SignUpActivity.this,"Failed to register try again",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    );

                }
                else{
                    Toast.makeText(SignUpActivity.this,"Failed to register try again",Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public void SignIn(View view){
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
    }
}