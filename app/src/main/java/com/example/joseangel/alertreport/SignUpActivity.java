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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    ScrollView myLayoutSignUp;
    AnimationDrawable animationDrawableSignUp;

    private EditText edtEmail, edtPassword;
    private Button btnRegister, btnForgot, btnLogin;
    private ProgressDialog prsProcess;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener athListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = (EditText) findViewById(R.id.email);
        edtPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.sign_up_button);
        btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnForgot = (Button) findViewById(R.id.bt_reset_password);
        mAuth = FirebaseAuth.getInstance();
        prsProcess = new ProgressDialog(this);
        prsProcess.setIndeterminate(true);
        btnRegister.setOnClickListener((v) -> {
            registerUser();
        });

        btnLogin.setOnClickListener((v) -> {
            openLogin();
        });

        btnForgot.setOnClickListener((v) -> {
            openForgot();
        });

        athListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    openAccount();
                }
            }
        };

        myLayoutSignUp = (ScrollView) findViewById(R.id.myLayoutSignUp);

        animationDrawableSignUp = (AnimationDrawable) myLayoutSignUp.getBackground();
        animationDrawableSignUp.setEnterFadeDuration(4500);
        animationDrawableSignUp.setExitFadeDuration(4500);
        animationDrawableSignUp.start();
    }

    private void registerUser() {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Debe ingresar un email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        prsProcess.setMessage("Registrando usuario");
        prsProcess.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Se ha registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "Usuario existente", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), "No se ha podido registrar satisfactoriamente", Toast.LENGTH_SHORT).show();
                    }
                    prsProcess.dismiss();
                });

    }

    private void openAccount() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void openLogin() {
        Intent i = new Intent(this, SignInActivity.class);
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
        mAuth.addAuthStateListener(athListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (athListener != null) {
            mAuth.removeAuthStateListener(athListener);
        }
    }


}



