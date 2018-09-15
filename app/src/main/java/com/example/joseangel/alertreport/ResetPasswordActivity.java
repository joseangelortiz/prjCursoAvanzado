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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    ScrollView myLayoutResetPassword;
    AnimationDrawable animationDrawableResetPassword;

    private EditText edtResetPassword;
    private Button btnResetPassword;
    private ProgressDialog prsProcessReset;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener athListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtResetPassword = (EditText) findViewById(R.id.reset_pass);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_pass);

        mAuth = FirebaseAuth.getInstance();
        prsProcessReset = new ProgressDialog(this);
        prsProcessReset.setIndeterminate(true);

        btnResetPassword.setOnClickListener((v) -> {
            resetPass();
        });

        athListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    openLogin();
                }
            }
        };

        myLayoutResetPassword = (ScrollView) findViewById(R.id.myLayoutResetPassword);

        animationDrawableResetPassword = (AnimationDrawable) myLayoutResetPassword.getBackground();
        animationDrawableResetPassword.setEnterFadeDuration(4500);
        animationDrawableResetPassword.setExitFadeDuration(4500);
        animationDrawableResetPassword.start();

    }

    private void resetPass() {
        final String email = edtResetPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Introduzca el correo electrónico con el cual está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        prsProcessReset.setMessage("Enviando correo para cambiar la contraseña");
        prsProcessReset.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Se han enviado las instrucciones para cambiar las contraseña", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "No se ha podido enviar el correo electrónico", Toast.LENGTH_SHORT).show();
                        }
                        prsProcessReset.dismiss();
                    }
                });
    }


    private void openLogin() {
        Intent i = new Intent(this, SignInActivity.class);
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
