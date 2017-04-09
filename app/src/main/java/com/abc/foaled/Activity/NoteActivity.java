package com.abc.foaled.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.foaled.Database.DatabaseHelper;
import com.abc.foaled.Database.ORMBaseActivity;
import com.abc.foaled.Helpers.UserInfo;
import com.abc.foaled.Models.Horse;
import com.abc.foaled.R;

/**
 * Created by Brendan on 24/02/2017.
 *
 */

public class NoteActivity extends ORMBaseActivity<DatabaseHelper> {
    TextView noteTitle;
    EditText noteContent;
    UserInfo userInfo;

    String title;
    String note;
    int horseID;
    Horse horse;

    boolean editing = false;
    boolean dialogResult = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        userInfo = UserInfo.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_18dp);
        }

        horseID = getIntent().getIntExtra("horseID", 0);
        horse = userInfo.getHorses().get(horseID);
        title = getIntent().getStringExtra("title");
        note = getIntent().getStringExtra("note");

        noteTitle = (TextView) findViewById(R.id.note_activity_title);
        noteContent = (EditText) findViewById(R.id.note_activity_content);
        noteTitle.setText(title);
        noteContent.setText(note);
        noteContent.setFocusableInTouchMode(editing);
    }

    @Override
    public void onPause() {
        if (isFinishing() && hasChanged())
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        super.onPause();
    }

    /**
     *
     * @param menu The menu object to put items in
     * @return Whether or not the menu items were added..?
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_action_bar, menu);
        return true;
    }

    /**
     *
     * @param item The item thatw as clicked in the menu
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_note:
                if (editing)
                    changeNote();
                toggleEditMode(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return Returns true if the note has changed
     */
    private boolean hasChanged() {
        return !noteContent.getText().toString().equals(horse.notes);
    }

    /**
     * Saves the new note
     */
    private void changeNote() {
        if (hasChanged()) {
            horse.notes = noteContent.getText().toString();
            getHelper().getHorseDataDao().update(horse);
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method toggles this screen between edit and read-only mode
     * @param item The item in the menu that was clicked
     */
    private void toggleEditMode(MenuItem item) {

        editing = !editing;
        int actionBarIcon = editing ? R.drawable.ic_done_black_18dp : R.drawable.ic_mode_edit_black_18dp;
        item.setIcon(actionBarIcon);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        noteContent.setFocusableInTouchMode(editing);

        if (editing) {
            noteContent.setSelection(noteContent.getText().length());
            noteContent.requestFocus();
            imm.showSoftInput(noteContent, InputMethodManager.SHOW_IMPLICIT);
        } else {
            noteContent.setSelection(0);
            noteContent.clearFocus();
            imm.hideSoftInputFromWindow(noteContent.getWindowToken(), 0);
        }
    }

    //TODO get this confirmation dialog working?
    private boolean confirmLeave() {
        dialogResult = true;
        if (hasChanged()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(noteContent.getWindowToken(), 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Keep changes?")
                    .setPositiveButton("KEEP", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            changeNote();
                        }
                    })
                    .setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialogResult = false;
                        }
                    }).show();
        }
        return dialogResult;
    }
}
