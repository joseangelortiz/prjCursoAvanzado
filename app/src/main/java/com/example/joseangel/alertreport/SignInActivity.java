package com.example.joseangel.alertreport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    ScrollView myLayoutSignIn;
    AnimationDrawable animationDrawableSignIn;

    private EditText edEmail, edPassword;
    private Button btRegister, btForgot, btLogin;
    private ProgressDialog prbProcess;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener atListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edEmail = (EditText) findViewById(R.id.ed_email);
        edPassword = (EditText) findViewById(R.id.ed_password);
        btRegister = (Button) findViewById(R.id.btn_sign_up);
        btLogin = (Button) findViewById(R.id.btn_sign_in);
        btForgot = (Button) findViewById(R.id.btn_reset_password);
        prbProcess = new ProgressDialog(this);
        prbProcess.setIndeterminate(true);

        mAuth = FirebaseAuth.getInstance();

        btLogin.setOnClickListener((v) -> {
            accessUser();
        });

        btRegister.setOnClickListener((v) -> {
            openRegister();
        });

        btForgot.setOnClickListener((v) -> {
            openForgot();
        });

        atListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    openAccount();
                }
            }
        };

        myLayoutSignIn = (ScrollView) findViewById(R.id.myLayoutSignIn);

        animationDrawableSignIn = (AnimationDrawable) myLayoutSignIn.getBackground();
        animationDrawableSignIn.setEnterFadeDuration(4500);
        animationDrawableSignIn.setExitFadeDuration(4500);
        animationDrawableSignIn.start();

    }

    private void accessUser() {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Debe ingresar un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        prbProcess.setMessage("Ingresando usuario");
        prbProcess.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Ingreso satisfactorio", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ingreso erróneo", Toast.LENGTH_SHORT).show();
                    }
                    prbProcess.dismiss();
                });

    }


    private void openAccount() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void openRegister() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    private void openForgot() {
        Intent i = new Intent(this, ResetPasswordActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(atListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (atListener != null) {
            mAuth.removeAuthStateListener(atListener);
        }
    }
}
