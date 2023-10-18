package me.ruslan.task4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import me.ruslan.task4.models.Note;

public class NoteEditActivity extends AppCompatActivity {
    private int real_index;
    private EditText title;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        title = findViewById(R.id.note_title);
        text = findViewById(R.id.note_text);

        real_index = getIntent().getIntExtra("real_index", -1);
        Note note = (Note)getIntent().getSerializableExtra("note");
        title.setText(note.getTitle());
        text.setText(note.getText() == null ? "" : note.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(real_index == -1)
            return;
        MainActivity.updateNote(real_index, title.getText().toString(), text.getText().toString());
    }
}