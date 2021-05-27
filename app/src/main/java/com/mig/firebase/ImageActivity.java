package com.mig.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ImageActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<ModelsImage, AdapterImage> recyclerAdapter;
    private FirebaseRecyclerOptions<ModelsImage> options;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setTitle("Lihat Gambar");
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        reference = FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerViewAdapter();
    }

    private void firebaseRecyclerViewAdapter() {
        options = new FirebaseRecyclerOptions.Builder<ModelsImage>().setQuery(reference.child("image"), ModelsImage.class)
                .setLifecycleOwner(this).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<ModelsImage, AdapterImage>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdapterImage adapterImage, int i, @NonNull ModelsImage modelsImage) {
                adapterImage.setDisplayImage(modelsImage.getImage_url(), ImageActivity.this);
                progressBar.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public AdapterImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new AdapterImage(LayoutInflater.from(parent.getContext()).inflate(R.layout.image, parent, false));
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }
}