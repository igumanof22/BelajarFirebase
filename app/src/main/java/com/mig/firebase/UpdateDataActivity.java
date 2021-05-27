package com.mig.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UpdateDataActivity extends AppCompatActivity {
    private EditText nimBaru, namaBaru, jurusanBaru;
    private Button update;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String cekNIM, cekNama, cekJurusan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);
        getSupportActionBar().setTitle("Update Data");
        nimBaru = findViewById(R.id.new_nim);
        namaBaru = findViewById(R.id.new_nama);
        jurusanBaru = findViewById(R.id.new_jurusan);
        update = findViewById(R.id.update);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        getData();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekNIM = nimBaru.getText().toString();
                cekNama = namaBaru.getText().toString();
                cekJurusan = jurusanBaru.getText().toString();

                if (isEmpty(cekNIM) || isEmpty(cekNama) || isEmpty(cekJurusan)){
                    Toast.makeText(getApplicationContext(), "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show();
                } else{
                    dataMahasiswa setMhs = new dataMahasiswa();
                    setMhs.setNim(cekNIM);
                    setMhs.setNama(cekNama);
                    setMhs.setJurusan(cekJurusan);
                    updateMhs(setMhs);
                }
            }
        });
    }

    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    private void getData() {
        nimBaru.setText(getIntent().getExtras().getString("dataNIM"));
        namaBaru.setText(getIntent().getExtras().getString("dataNama"));
        jurusanBaru.setText(getIntent().getExtras().getString("dataJurusan"));
    }

    private void updateMhs(dataMahasiswa setMhs) {
        String UserID = auth.getUid();
        String getKey = getIntent().getExtras().getString("dataKey");
        database.child("Admin").child(Objects.requireNonNull(UserID)).child("Mahasiswa").child(getKey).setValue(setMhs)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nimBaru.setText("");
                        namaBaru.setText("");
                        jurusanBaru.setText("");
                        Toast.makeText(getApplicationContext(), "Data berhasil diubah!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}