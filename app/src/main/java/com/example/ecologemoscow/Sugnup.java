package com.example.ecologemoscow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Sugnup extends AppCompatActivity {
    private EditText textpassword, textgmail;
    private Button signupbut;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        FirebaseAuth dAuth= FirebaseAuth.getInstance();
        textpassword= findViewById(R.id.passwordtext);
        textgmail=findViewById(R.id.gmail);
        signupbut=findViewById(R.id.ButtonSignup);
        textView=findViewById(R.id.Войтивсистему);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Sugnup.this,Signing.class);
                startActivity(intent);
                finish();
            }
        });
        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password,gmail;
                password = textpassword.getText().toString().trim();
                gmail = textgmail.getText().toString().trim();
                dAuth.createUserWithEmailAndPassword(gmail,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Sugnup.this, "Аккаунт создан успешно", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Sugnup.this, "Аккаунт не создан", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }
}
