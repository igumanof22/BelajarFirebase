package com.mig.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private EditText nim, nama, jurusan;
    private FirebaseAuth auth;
    private Button logout, simpan, login, showData, image;

    //Kode Permintaan
    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        simpan = findViewById(R.id.save);
        simpan.setOnClickListener(this);
        showData = findViewById(R.id.showdata);
        showData.setOnClickListener(this);
        image = findViewById(R.id.uploadimage);
        image.setOnClickListener(this);

        auth = FirebaseAuth.getInstance(); //Get Instance Firebase Autentifikasi

        nim = findViewById(R.id.nim);
        nama = findViewById(R.id.nama);
        jurusan = findViewById(R.id.jurusan);

        /*
         * Mendeteksi apakah ada user yang masuk, Jika tidak, maka setiap komponen UI akan dinonaktifkan
         * Kecuali Tombol Login. Dan jika ada user yang terautentikasi, semua fungsi/komponen
         * didalam User Interface dapat digunakan, kecuali tombol Logout
         */
        if (auth.getCurrentUser() == null){
            defaultUI();
        } else{
            updateUI();
        }
    }

    private void defaultUI(){
        logout.setEnabled(false);
        simpan.setEnabled(false);
        showData.setEnabled(false);
        login.setEnabled(true);
        nim.setEnabled(false);
        nama.setEnabled(false);
        jurusan.setEnabled(false);
    }

    private void updateUI(){
        logout.setEnabled(true);
        simpan.setEnabled(true);
        login.setEnabled(false);
        showData.setEnabled(true);
        nim.setEnabled(true);
        nama.setEnabled(true);
        jurusan.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    // Mengecek apakah ada data yang kosong, akan digunakan pada Tutorial Selanjutnya
    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN adalah kode permintaan yang Anda berikan ke startActivityForResult, saat memulai masuknya arus.
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                updateUI();
            } else{
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login Dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login:
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false).build(),RC_SIGN_IN);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.save:
                //Mendapatkan UserID dari pengguna yang Terautentikasi
                String getUserID = auth.getCurrentUser().getUid();

                //Mendapatkan Instance dari Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getReference;

                //Menyimpan Data yang diinputkan User kedalam Variable
                String getNIM = nim.getText().toString();
                String getNama = nama.getText().toString();
                String getJurusan = jurusan.getText().toString();

                getReference = database.getReference(); // Mendapatkan Referensi dari Database

                // Mengecek apakah ada data yang kosong
                if(isEmpty(getNIM) || isEmpty(getNama) || isEmpty(getJurusan)){
                    //Jika Ada, maka akan menampilkan pesan singkan seperti berikut ini.
                    Toast.makeText(MainActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    getReference.child("Admin").child(getUserID).child("Mahasiswa").push()
                            .setValue(new dataMahasiswa(getNIM, getNama, getJurusan))
                                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Peristiwa ini terjadi saat user berhasil menyimpan datanya kedalam Database
                                        nim.setText("");
                                        nama.setText("");
                                        jurusan.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                                    }
                                });
                }
                break;
            case R.id.logout:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Logout Berhasil!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            case R.id.showdata:
                startActivity(new Intent(MainActivity.this, ListDataActivity.class));
                break;
            case R.id.uploadimage:
                startActivity(new Intent(MainActivity.this, MainImageActivity.class));
                break;
        }
    }
}