package com.example.joseangel.alertreport;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ScrollView myLayoutMain;
    AnimationDrawable animationDrawableMain;

    private RecyclerView rvRecyclerView;
    private PostAdapter psAdapter;

    private ProgressBar prProgressCircle;

    private DatabaseReference dtbDatabaseRef;
    private List<UploadClass> upUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.settings).setOnClickListener((v) -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        findViewById(R.id.bt_new_post).setOnClickListener((v) -> {
            startActivity(new Intent(this, NewPostActivity.class));
        });

        rvRecyclerView = findViewById(R.id.recycler_view);
        rvRecyclerView.setHasFixedSize(true);
        rvRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        prProgressCircle = findViewById(R.id.progress_bar_circ);

        upUploads = new ArrayList<>();

        dtbDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        dtbDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadClass uploadClass = postSnapshot.getValue(UploadClass.class);
                    upUploads.add(uploadClass);
                    Collections.reverse(upUploads);
                }

                psAdapter = new PostAdapter(MainActivity.this, upUploads);

                rvRecyclerView.setAdapter(psAdapter);
                prProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                prProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        myLayoutMain = (ScrollView) findViewById(R.id.myLayoutMain);

        animationDrawableMain = (AnimationDrawable) myLayoutMain.getBackground();
        animationDrawableMain.setEnterFadeDuration(4500);
        animationDrawableMain.setExitFadeDuration(4500);
        animationDrawableMain.start();
    }
}
