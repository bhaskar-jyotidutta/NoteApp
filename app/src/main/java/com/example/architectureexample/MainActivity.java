package com.example.architectureexample;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote  = findViewById(R.id.button_add_note);

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        ActivityResultLauncher<Intent> addEditActivityEditResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent data = result.getData();
                            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

                            if(id == -1){
                                Toast.makeText(MainActivity.this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                            Note note = new Note(title, description, priority);
                            note.setId(id);
                            noteViewModel.update(note);

                            Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(MainActivity.this, "Note not saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {

                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                addEditActivityEditResultLauncher.launch(intent);

            }
        });

        ActivityResultLauncher<Intent> addEditActivityAddResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent data = result.getData();
                            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                            Note note = new Note(title, description, priority);
                            noteViewModel.insert(note);

                            Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Note not saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                addEditActivityAddResultLauncher.launch(intent);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.delete_all_notes){
            noteViewModel.deleteAllNotes();
            Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }



    }
}