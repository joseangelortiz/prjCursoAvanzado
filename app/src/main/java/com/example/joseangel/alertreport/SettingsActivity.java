package com.example.joseangel.alertreport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    ScrollView myLayoutSettings;
    AnimationDrawable animationDrawableSettings;

    private TextView tvEmail;
    private EditText edtPassword;
    private Button btnClose, btnRemove, btnChange;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener athListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvEmail = (TextView) findViewById(R.id.tv_user_email);
        edtPassword = (EditText) findViewById(R.id.new_password);
        btnClose = (Button) findViewById(R.id.sign_out);
        btnRemove = (Button) findViewById(R.id.remove_user);
        btnChange = (Button) findViewById(R.id.change_password);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getEmail();

        athListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    openLogin();
                } else {
                    tvEmail.setText("Tiene la sesión iniciada con el correo: " + user.getEmail());
                }
            }
        };

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && !edtPassword.getText().toString().trim().equals("")) {
                    if (edtPassword.getText().toString().trim().length() < 6) {
                        edtPassword.setError("La contraseña es muy corta, introduce al menos 6 caracteres");
                    } else {
                        user.updatePassword(edtPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "La contraseña se ha actualizado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "No se ha podido actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else if (edtPassword.getText().toString().trim().equals("")) {
                    edtPassword.setError("Introduce la contraseña");
                }
            }
        });


        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, "El usuario se ha borrado ¡Regístrate nuevamente!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingsActivity.this, SignUpActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "No se ha podido borrar el usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnClose.setOnClickListener((v) -> {
            mAuth.signOut();
        });

        myLayoutSettings = (ScrollView) findViewById(R.id.myLayoutSettings);

        animationDrawableSettings = (AnimationDrawable) myLayoutSettings.getBackground();
        animationDrawableSettings.setEnterFadeDuration(4500);
        animationDrawableSettings.setExitFadeDuration(4500);
        animationDrawableSettings.start();
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