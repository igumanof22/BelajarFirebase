package com.mig.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ListDataActivity extends AppCompatActivity implements Adapter.dataListener {
    private RecyclerView recyclerView;

    private DatabaseReference reference;
    private ArrayList<dataMahasiswa> listMahasiswa;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        recyclerView = findViewById(R.id.datalist);
        getSupportActionBar().setTitle("Data Mahasiswa");
        auth = FirebaseAuth.getInstance();
        MyRecyclerView();
        GetData();
    }

    private void GetData() {
        Toast.makeText(getApplicationContext(), "Mohon Tunggu Sebentar....", Toast.LENGTH_LONG).show();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Mahasiswa")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMahasiswa = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataMahasiswa mahasiswa = dataSnapshot.getValue(dataMahasiswa.class);
                    assert mahasiswa != null;
                    mahasiswa.setKey(dataSnapshot.getKey());
                    listMahasiswa.add(mahasiswa);
                }
                Adapter adapter = new Adapter(listMahasiswa, ListDataActivity.this);
                recyclerView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(), "Data berhasil dimuat!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Data gagal dimuat!", Toast.LENGTH_LONG).show();
                Log.e("ListData", error.getDetails()+" "+error.getMessage());
            }
        });
    }

    private void MyRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line)));
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onDeleteData(dataMahasiswa data, int position) {
        String userID = auth.getUid();
        if (reference != null){
            reference.child("Admin").child(userID).child("Mahasiswa").child(data.getKey()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}