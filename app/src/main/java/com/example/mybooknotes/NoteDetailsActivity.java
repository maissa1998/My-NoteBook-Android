package com.example.mybooknotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText noteTitle,noteContent;
    TextView titleView;
    String title,content,docId;
    ImageButton save;
    Button deleteBtn;
    boolean isEditMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        deleteBtn = findViewById(R.id.deleteBtn);

        titleView = findViewById(R.id.title);
        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        save = findViewById(R.id.save);

        //get the data to edit it :
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }
        noteTitle.setText(title);
        noteContent.setText(content);
        if(isEditMode){
            titleView.setText("Edit your note");
            deleteBtn.setVisibility(View.VISIBLE);
        }
        save.setOnClickListener((v)-> saveNote());
        deleteBtn.setOnClickListener((v)->deleteNoteFromFirebase());
    }
    void saveNote(){
        String Title = noteTitle.getText().toString();
        String Content = noteContent.getText().toString();
        if (Title==null || Title.isEmpty()){
            noteTitle.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(Title);
        note.setContent(Content);
        note.setTimestamp(Timestamp.now());
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            //update note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();
                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }else {
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }
}