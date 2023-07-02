package com.example.architectureexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviderGetKt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        /*instantiating ViewModel. We don't call new NoteViewModel() because it will then create a instance with every activity
        * . Instead we have to ask Android because Android know when to create a new instance or when to give
        * already created instance*/

        /*With the keyword this the ViewModel knows with which activity is has scope. When this activity will
        destroy then Android will destroy this ViewModel also*/
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        /*Here observe() is LiveData method*/
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
               adapter.setNotes(notes);
            }
        });

    }
}