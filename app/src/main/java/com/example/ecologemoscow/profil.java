package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profil extends AppCompatActivity {
    Button logbutton;
    TextView textemail;
    String email;
    public boolean ustrs;



    @Override
    protected void onCreate(Bundle Pes){
        super.onCreate(Pes);
        setContentView(R.layout.profil_l);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        textemail=findViewById(R.id.emailtext);
        email = currentUser.getEmail();
        textemail.setText(""+email);
        logbutton=findViewById(R.id.butlogout);
        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(profil.this,MainActivity.class));
            }
        });





    }
}
