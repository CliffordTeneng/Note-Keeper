package com.teneng.notekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.teneng.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.teneng.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.teneng.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.teneng.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int NOT_POSITION = -1;
    private NoteInfo notes;
    private boolean isNewNote = false;
    private Spinner spinner;
    private EditText noteTitle;
    private EditText noteText;
    private int notePosition;
    private boolean isCancelling;
    private String originalNoteCourseId;
    private String originalNoteTitle;
    private String originalNoteText;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        spinner = findViewById(R.id.spinner_id);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> arrayAdapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        arrayAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapterCourses);

        readNoteReceived();

        if (savedInstanceState == null){

            saveOriginalNotes();

        }else{

            restoreOriginalNoteValues(savedInstanceState);
        }


        noteTitle = findViewById(R.id.note_title);
        noteText = findViewById(R.id.note_text);
        
        if (isNewNote) {

        }else {
            displayNotes(spinner, noteTitle, noteText);
        }
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        originalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNotes() {
        if (isNewNote){
            return;
        }
        originalNoteCourseId = notes.getCourse().getCourseId();
        originalNoteTitle = notes.getTitle();
        originalNoteText = notes.getText();
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
        }else if (item.getItemId() == R.id.cancel){
            isCancelling = true;
            finish();
        }else if (item.getItemId() == R.id.next){
            moveNextNote();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(notePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNextNote() {
        saveNotes();

        ++notePosition;
        notes = DataManager.getInstance().getNotes().get(notePosition);

        saveOriginalNotes();
        displayNotes(spinner,noteTitle, noteText);
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCancelling){

            if (isNewNote){

                DataManager.getInstance().removeNote(notePosition);
            }else {
                storePreviouseNoteValues();
            }
        }else {
            saveNotes();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, originalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);
    }

    private void storePreviouseNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseId);
        notes.setCourse(course);
        notes.setText(originalNoteText);
        notes.setTitle(originalNoteTitle);
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
        position = intent.getIntExtra(NOTE_POSITION, NOT_POSITION);
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


}
