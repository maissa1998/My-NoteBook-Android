package com.example.mybooknotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addBtn;
    ImageView log;
    NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        log = findViewById(R.id.log);
        addBtn = findViewById(R.id.addBtn);

        setupRecyclerView();
        log.setOnClickListener((v)->logOut());
        addBtn.setOnClickListener((V)-> startActivity(new Intent(MainActivity.this,NoteDetailsActivity.class)));
    }
    void logOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }
    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        noteAdapter.stopListening();
    }
    @Override
    protected void onResume(){
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}