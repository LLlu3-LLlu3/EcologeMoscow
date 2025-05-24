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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signing extends AppCompatActivity {
    private EditText textpassword, textgmail;
    private Button signingbut;
    private TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.signing);
        FirebaseAuth dAuth= FirebaseAuth.getInstance();
        textpassword=findViewById(R.id.passwordtext);
        textgmail=findViewById(R.id.gmail);
        textView=findViewById(R.id.backtoreg);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signing.this,Sugnup.class);
                startActivity(intent);
                finish();
            }
        });
        signingbut=findViewById(R.id.ButtonSigning);
        signingbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password,gmail;
                password = textpassword.getText().toString().trim();
                gmail = textgmail.getText().toString().trim();
                dAuth.signInWithEmailAndPassword(gmail,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Signing.this, "Вход Успешен", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else {
                                    Toast.makeText(Signing.this, "МИША БЛЯТЬ", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });


    }
}
