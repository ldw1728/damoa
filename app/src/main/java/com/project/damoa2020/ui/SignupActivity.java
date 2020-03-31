package com.project.damoa2020.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.damoa2020.R;
import com.project.damoa2020.User;
import com.project.damoa2020.controller.DBC;

public class SignupActivity extends AppCompatActivity {

    private EditText txt_email;
    private EditText txt_pw;
    private EditText txt_name;
    private Button btn_comSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        init();

        btn_comSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_email.getText().equals("") || txt_name.getText().equals("") || txt_pw.getText().equals("")){
                    Toast.makeText(SignupActivity.this, "모든 정보를 기입해주세요.",
                            Toast.LENGTH_SHORT).show();
                }
                else signUp();
            }
        });
    }

    private void init(){
        txt_email = findViewById(R.id.text_signupEmail);
        txt_name = findViewById(R.id.text_signupName);
        txt_pw = findViewById(R.id.text_signupPW);
        btn_comSignup = findViewById(R.id.btn_comsignup);

    }

    private void signUp(){
        mAuth.createUserWithEmailAndPassword(txt_email.getText().toString().trim(), txt_pw.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser Fuser = task.getResult().getUser();
                            User user = new User();
                           user.setUser(Fuser);
                           user.setName(txt_name.getText().toString().trim());
                            DBC.addDataToDB(null, "users", user); //db에 유저 정보를 저장.
                           finish();
                        }else{
                            Toast.makeText(SignupActivity.this, "가입 실패.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
