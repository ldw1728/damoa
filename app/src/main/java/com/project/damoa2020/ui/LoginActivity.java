package com.project.damoa2020.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.User;
import com.project.damoa2020.controller.DBC;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseUser user = null;

    EditText textId;
    EditText textPw;
    Button btnSignup;
    Button btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        mAuth = FirebaseAuth.getInstance();

        setEvent();

    }

    public void goToMainActivity(User user){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user", user);

        startActivity(intent);
        finish();
    }

    public void init(){
        textId = findViewById(R.id.text_id);
        textPw = findViewById(R.id.text_pw);
        btnSignIn = findViewById(R.id.btn_signin);
        btnSignup = findViewById(R.id.btn_signup);
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(textId.getText().toString().trim(), textPw.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DBC.getUser(textId.getText().toString().trim(), new Consumer<User>() {
                                @Override
                                public void accept(User user) {
                                    goToMainActivity(user);
                                }
                            });

                        }else{
                            Toast.makeText(LoginActivity.this, "로그인 실패, id pw 확인해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setEvent(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textId.getText().equals("") || textPw.getText().equals("")) {
                    Toast.makeText(LoginActivity.this, "정보를 모두 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else signIn();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
