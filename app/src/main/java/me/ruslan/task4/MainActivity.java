package me.ruslan.task4;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.ruslan.task4.adapters.NotesListAdapter;
import me.ruslan.task4.models.Note;


public class MainActivity extends AppCompatActivity {
    private ListView notes_list;
    private NotesListAdapter adapter;
    public static ArrayList<Note> notes = new ArrayList<Note>() {{
        add(new Note("Test 1", "text 1", "12:01", 1, null));
        add(new Note("Test 2", "text 2", "12:02", 2, null));
        add(new Note("Test 3", "text 3", "12:03", 4, null));
        add(new Note("Test 4", "text 4", "12:04", 1, null));
        add(new Note("Test 5", "text 5", "12:05", 2, null));
        add(new Note("Test 5", "very long text, very long text, very long text, very long text, very long text, very long text, ", "12:05", 4, null));
    }};
    private String searchQuery = "";
    private int filterPriority = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notes_list = findViewById(R.id.notes_list);

        adapter = new NotesListAdapter(notes, getApplicationContext());
        notes_list.setAdapter(adapter);

        notes_list.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Note note = notes.get(i);

            AlertDialog.Builder noteDialog = new AlertDialog.Builder(MainActivity.this);
            noteDialog.setTitle(String.format("Manage note \"%s\"", note.getTitle()));

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            Button iconBtn = new Button(getApplicationContext());
            iconBtn.setText("Set icon");
            Button deleteBtn = new Button(getApplicationContext());
            deleteBtn.setText("Delete");

            layout.addView(iconBtn);
            layout.addView(deleteBtn);
            noteDialog.setView(layout);

            noteDialog.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

            AlertDialog dialog = noteDialog.create();
            iconBtn.setOnClickListener(view1 -> {
                pickNoteImage(i);
                dialog.dismiss();
            });
            deleteBtn.setOnClickListener(view1 -> {
                notes.remove(i);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            });

            dialog.show();
            return true;
        });
    }

    public void pickNoteImage(int noteIdx) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1000+noteIdx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode >= 1000 && requestCode < 1000+notes.size()) {
            Note note = notes.get(requestCode-1000);
            note.setImage(data.getData());
            adapter.notifyDataSetChanged();
            try {
                copyFile(data.getData(), new File(getFilesDir(), System.currentTimeMillis() + ".png"));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void copyFile(Uri uri, File destinationFile) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        destinationFile.getParentFile().mkdirs();
        OutputStream outputStream = new FileOutputStream(destinationFile);

        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            AlertDialog.Builder searchDialog = new AlertDialog.Builder(MainActivity.this);
            searchDialog.setTitle("Search notes");

            EditText searchET = new EditText(getApplicationContext());
            searchET.setHint("Note title or text");
            searchET.setText(searchQuery);
            searchDialog.setView(searchET);

            searchDialog.setPositiveButton("Search", (dialog, whichButton) -> {
                searchQuery = searchET.getText().toString();
                adapter.filter(searchQuery, filterPriority);
            });

            searchDialog.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

            searchDialog.setNeutralButton("Clear", (dialog, i) -> {
                searchQuery = "";
                adapter.filter(searchQuery, filterPriority);
            });

            searchDialog.show();
            return true;
        } else if (id == R.id.menu_filter) {
            AlertDialog.Builder searchDialog = new AlertDialog.Builder(MainActivity.this);
            searchDialog.setTitle("Filter notes");

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(8, 8, 8, 8);
            CheckBox low = new CheckBox(getApplicationContext());
            CheckBox med = new CheckBox(getApplicationContext());
            CheckBox hig = new CheckBox(getApplicationContext());

            low.setText("Low");
            med.setText("Medium");
            hig.setText("High");
            low.setChecked(filterPriority == 0 || (filterPriority & 1) == 1);
            med.setChecked(filterPriority == 0 || (filterPriority & 2) == 2);
            hig.setChecked(filterPriority == 0 || (filterPriority & 4) == 4);

            layout.addView(low);
            layout.addView(med);
            layout.addView(hig);
            searchDialog.setView(layout);

            searchDialog.setPositiveButton("Apply", (dialog, whichButton) -> {
                int lowP, medP, higP;
                lowP = low.isChecked() ? 1 : 0;
                medP = med.isChecked() ? 2 : 0;
                higP = hig.isChecked() ? 4 : 0;
                filterPriority = lowP | medP | higP;
                adapter.filter(searchQuery, filterPriority);
            });

            searchDialog.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

            searchDialog.setNeutralButton("Clear", (dialog, i) -> {
                filterPriority = 7;
                adapter.filter(searchQuery, filterPriority);
            });

            searchDialog.show();
            return true;
        } else if (id == R.id.menu_create) {
            AlertDialog.Builder newNote = new AlertDialog.Builder(MainActivity.this);
            newNote.setTitle("Create new note");

            EditText noteTitle = new EditText(getApplicationContext());
            noteTitle.setHint("Note name");
            newNote.setView(noteTitle);

            newNote.setPositiveButton("Create", (dialog, whichButton) -> {
                notes.add(new Note(noteTitle.getText().toString(), null, new SimpleDateFormat("HH:mm").format(new Date()), 0, null));
                adapter.notifyDataSetChanged();
            });

            newNote.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

            newNote.show();
            return true;
        } else if (id == R.id.menu_settings) {
            // TODO: open settings
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }
}