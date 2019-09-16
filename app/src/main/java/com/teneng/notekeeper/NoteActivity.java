package com.teneng.notekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String FIRST_LAUNCH = "first_launched";
    public static final String NOTE_POSITION = "com.teneng.notekeeper.NOTE_POSITION";
    public static final int NOT_POSITION = -1;
    private NoteInfo notes;
    private boolean isNewNote = false;
    private Spinner spinner;
    private EditText noteTitle;
    private EditText noteText;
    private int notePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        spinner = findViewById(R.id.spinner_id);

       // alertMessage();

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> arrayAdapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        arrayAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapterCourses);

        readNoteReceived();

        noteTitle = findViewById(R.id.note_title);
        noteText = findViewById(R.id.note_text);
        
        if (isNewNote) {

        }else {
            displayNotes(spinner, noteTitle, noteText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.send_mail){
            sendEmail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotes();
    }

    private void saveNotes() {
        notes.setCourse((CourseInfo) spinner.getSelectedItem());
        notes.setTitle(noteTitle.getText().toString());
        notes.setText(noteText.getText().toString());
    }

    private void sendEmail() {

        CourseInfo course = (CourseInfo) spinner.getSelectedItem();
        String subject = noteTitle.getText().toString();
        String text = "Hey, Wish you could Join me Learn this PluralSight course \"" + course.getTitle() + "\"\n" + noteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    private void displayNotes(Spinner spinner, EditText noteTitle, EditText noteText) {

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(notes.getCourse());
        spinner.setSelection(courseIndex);

        noteTitle.setText(notes.getTitle());
        noteText.setText(notes.getText());

    }

    private void readNoteReceived() {
        Intent intent = getIntent();
//        notes = intent.getParcelableExtra(NOTE_POSITION);
        int position = intent.getIntExtra(NOTE_POSITION, NOT_POSITION);
        isNewNote = position == NOT_POSITION;

        if (!isNewNote){
            notes = DataManager.getInstance().getNotes().get(position);
        }else{
            createNewNotes();
        }
    }

    private void createNewNotes() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        notes =  dm.getNotes().get(notePosition);
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
}
