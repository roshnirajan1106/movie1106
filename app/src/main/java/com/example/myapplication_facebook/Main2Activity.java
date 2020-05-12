package com.example.myapplication_facebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {
    EditText email,pass;
    FirebaseAuth fAuth;
    Button btn1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        email=findViewById(R.id.editText3);
        pass=findViewById(R.id.editText2);
        btn1=findViewById(R.id.button2);
        fAuth=FirebaseAuth.getInstance();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = email.getText().toString().trim();
                String room = pass.getText().toString().trim();
                if (TextUtils.isEmpty((name))) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(room)) {
                    pass.setError("Password is must ");
                    return;
                }
                if (room.length() <6 ) {
                    pass.setError("password  should be of atleast  6 characters");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(name, room).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(Main2Activity.this, "Successfully signed up", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
                        } else {
                            Toast.makeText(Main2Activity.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }
        });
    }

}
