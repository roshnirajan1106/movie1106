package com.example.myapplication_facebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    TextView txt;
    LoginButton btn;
    CallbackManager cm;
    EditText email, pass;
    TextView txt1, txt2, txt3;
    Button lgn;
    FirebaseAuth fAuth;

    private SignInButton SignIn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 900;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.text);
        btn = findViewById(R.id.login);
        lgn = findViewById(R.id.button2);
        txt1 = findViewById(R.id.textView2);
        txt2 = findViewById(R.id.textView3);
        txt3 = findViewById(R.id.textView4);
        email = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);
        fAuth = FirebaseAuth.getInstance();

        SignIn = findViewById(R.id.bn_login);

        SignIn.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                finish();
            }
        });

        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                AlertDialog.Builder prd = new AlertDialog.Builder(v.getContext());

                prd.setMessage(("Enter your email to recieve the reset password link"));
                prd.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetmail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error! in sending the reset password link" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                prd.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog

                    }
                });
                AlertDialog alert = prd.create();
                alert.setTitle("Reset password");
                alert.show();
            }
        });
        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = email.getText().toString().trim();
                String room = pass.getText().toString().trim();
                if (TextUtils.isEmpty((name))) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(room)) {
                    pass.setError("Room no. is must ");
                    return;
                }
                if (room.length() < 6) {
                    pass.setError("password should be of atleast 6 characters");
                    return;
                }
                fAuth.signInWithEmailAndPassword(name, room).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    }
                });
            }
        });


        cm = CallbackManager.Factory.create();
        btn.registerCallback(cm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                txt.setText("userid" + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(i);
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cm.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }




    private void signin1(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }


    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            updateUI(true);
        }
    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            //goto next page
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
        }
    }
















    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_login) {
            signin1();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}



