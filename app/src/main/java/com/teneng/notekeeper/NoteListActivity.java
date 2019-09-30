package com.teneng.notekeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    public static final String FIRST_LAUNCH = "com.teneng.notekeeper.FIRST_LAUNCH";
   // private ArrayAdapter<NoteInfo> adapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));

            }
        });

         alertMessage();
        displayNotes();
    }

    private void displayNotes() {

//        final ListView noteList = findViewById(R.id.list_notes);
//
//        List<NoteInfo> noteInfos = DataManager.getInstance().getNotes();
//
//        adapterNotes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteInfos);
//
//        noteList.setAdapter(adapterNotes);
//
//        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
//
////                NoteInfo notes = (NoteInfo) noteList.getItemAtPosition(position);
//                intent.putExtra(NoteActivity.NOTE_POSITION, position);
//                startActivity(intent);
//
//            }
//        });

        final RecyclerView note_list = findViewById(R.id.note_list_rv);
        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        note_list.setLayoutManager(notesLayoutManager);
    }

    public void alertMessage(){

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = sharedPreferences.getBoolean(FIRST_LAUNCH, true);

        if(isFirst){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.welcome_message);
            builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(FIRST_LAUNCH, false);
                    dialogInterface.dismiss();
                }
            });
            builder.setTitle("Welcome to Note Keeper");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // adapterNotes.notifyDataSetChanged();
    }
}
