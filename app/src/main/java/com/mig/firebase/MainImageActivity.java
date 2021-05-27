package com.mig.firebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class MainImageActivity extends AppCompatActivity implements View.OnClickListener{
    private Button upload, unggah, lihat;
    private ImageView imageContainer;
    private ProgressBar progressBar;

    private StorageReference reference;
    private DatabaseReference databaseReference;

    private static final int REQ_CODE_CAMERA = 1;
    private static final int REQ_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image);
        getSupportActionBar().setTitle("Upload Image");
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(this);
        unggah = findViewById(R.id.select_Image);
        unggah.setOnClickListener(this);
        lihat = findViewById(R.id.lihat);
        imageContainer = findViewById(R.id.imageContainer);
        progressBar = findViewById(R.id.progressBar);

        reference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainImageActivity.this, ImageActivity.class));
            }
        });
    }

    private void uploadImage(){
        imageContainer.setDrawingCacheEnabled(true);
        imageContainer.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageContainer.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Kompress ke JPEG dengan kualitas 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        //Lokasi lengkap dimana gambar akan disimpan
        String namaFile = UUID.randomUUID()+".jpg";
        String pathFile = "image/"+namaFile;
        UploadTask uploadTask = reference.child(pathFile).putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child(pathFile).getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadURL = uri.toString();
                    databaseReference.child("image").push().setValue(new ModelsImage(downloadURL));
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainImageActivity.this.getApplicationContext(), "Uploading Berhasil!!", Toast.LENGTH_SHORT).show();
                    MainImageActivity.this.finish();
                });
            }
        }). addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "uploading Gagal! -> "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });
    }

    private void getImage(){
        CharSequence[] menu = {"Kamera", "Galeri"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Upload Image").setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent imageIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(imageIntentCamera, REQ_CODE_CAMERA);
                        break;
                    case 1:
                        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(imageIntentGallery, REQ_CODE_GALLERY);
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_CAMERA:
                if (resultCode == RESULT_OK){
                    imageContainer.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
                    imageContainer.setImageBitmap(bitmap);
                    upload.setEnabled(true);
                }
                break;
            case REQ_CODE_GALLERY:
                if (resultCode == RESULT_OK){
                    imageContainer.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    imageContainer.setImageURI(uri);
                    upload.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload:
                uploadImage();
                break;
            case R.id.select_Image:
                getImage();
                break;
        }
    }
}