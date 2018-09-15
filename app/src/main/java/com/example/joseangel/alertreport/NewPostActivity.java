package com.example.joseangel.alertreport;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity implements LocationClass.LocationStringListener {

    ScrollView myLayoutNewPost;
    AnimationDrawable animationDrawableNewPost;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnUpload, btnShow, btnChooseImage;
    private EditText edtTitle, edtDescription, edtLocation;
    private ImageView imgSelected;
    private ProgressBar prsProgressB;

    private Uri imgUri;

    LocationClass locationClass;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        btnUpload = findViewById(R.id.btn_upload_post);
        btnShow = findViewById(R.id.btn_show_uploads);
        btnChooseImage = findViewById(R.id.btn_choose_img);
        edtTitle = findViewById(R.id.edt_title_post);
        edtDescription = findViewById(R.id.edt_desc_post);
        edtLocation = findViewById(R.id.edt_address_post);

        locationClass = new LocationClass(this, this);

        imgSelected = findViewById(R.id.image_selected);
        prsProgressB = findViewById(R.id.progress_bar);
        Log.i("Ubicacion", edtLocation.getText().toString());

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(NewPostActivity.this, "Nueva publicación en proceso", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPosts();
            }
        });

        myLayoutNewPost = (ScrollView) findViewById(R.id.myLayoutNewPost);

        animationDrawableNewPost = (AnimationDrawable) myLayoutNewPost.getBackground();
        animationDrawableNewPost.setEnterFadeDuration(4500);
        animationDrawableNewPost.setExitFadeDuration(4500);
        animationDrawableNewPost.start();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();

            Picasso.with(this).load(imgUri).into(imgSelected);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imgUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    prsProgressB.setProgress(0);
                                }
                            }, 500);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(NewPostActivity.this, "Nueva publicación creada", Toast.LENGTH_LONG).show();
                                    UploadClass uploadClass = new UploadClass(edtTitle.getText().toString().trim(), edtDescription.getText().toString().trim(),
                                            edtLocation.getText().toString().trim(), uri.toString());
                                    String uploadId = databaseRef.push().getKey();
                                    databaseRef.child(uploadId).setValue(uploadClass);

                                    Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            prsProgressB.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No se seleccionó una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPosts() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void addressString(String address) {
        edtLocation.setText(address);

    }
}
