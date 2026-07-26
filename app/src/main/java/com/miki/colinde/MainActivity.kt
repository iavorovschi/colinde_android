package com.miki.colinde;

import static com.miki.colinde.R.id;
import static com.miki.colinde.R.layout;
import static com.miki.colinde.R.menu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;


public class MainActivity extends AppCompatActivity {

    private static final String PRIVATE_PREF = "my_preferences";
    private static final String PREF_SCROLL_DIRECTION = "horizontal_scroll";
    private static final String PREF_SCROLL_TYPE = "scroll_page_by_page";

    private PDFView pdfView;
    private SharedPreferences myPref;
    private MyScrollHandler myScrollHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        Toolbar toolbar = findViewById(id.appBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> openBookMarks());
        toolbar.setNavigationIcon(R.drawable.ic_bookmarks);
        toolbar.setNavigationContentDescription(R.string.book_marks_name);
        myPref = getSharedPreferences(PRIVATE_PREF, MODE_PRIVATE);
        initPdf();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu newMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menu.main_menu, newMenu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(findViewById(R.id.loadingBar).getVisibility() == View.VISIBLE) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case id.search:
                myScrollHandler.onCreateDialog();
                break;
            case id.settings:
                openSettings(id.settings);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private void openBookMarks() {
        if(findViewById(R.id.loadingBar).getVisibility() == View.VISIBLE) {
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(layout.dialog_book_marks, null);
        dialogBuilder.setView(dialogView);

        TextView content = dialogView.findViewById(id.content);
        TextView literateContent = dialogView.findViewById(id.literate_content);
        TextView oldCarols = dialogView.findViewById(id.old_carols);
        TextView viflaim = dialogView.findViewById(id.viflaim);
        TextView greetings = dialogView.findViewById(id.greetings);
        TextView songs = dialogView.findViewById(id.church_songs);
        TextView notes = dialogView.findViewById(id.musical_notes);

        dialogBuilder.setTitle(getResources().getString(R.string.book_marks_name));
        dialogBuilder.setNegativeButton(getResources().getString(R.string.button_cancel), (dialog, id) -> dialog.cancel());
        Dialog dialog = dialogBuilder.create();

        content.setOnClickListener(value -> {
            pdfView.jumpTo(413);
            dialog.dismiss();
        });
        literateContent.setOnClickListener(value -> {
            pdfView.jumpTo(404);
            dialog.dismiss();
        });
        oldCarols.setOnClickListener(value -> {
            pdfView.jumpTo(317);
            dialog.dismiss();
        });
        greetings.setOnClickListener(value -> {
            pdfView.jumpTo(334);
            dialog.dismiss();
        });
        viflaim.setOnClickListener(value -> {
            pdfView.jumpTo(338);
            dialog.dismiss();
        });
        content.setOnClickListener(value -> {
            pdfView.jumpTo(413);
            dialog.dismiss();
        });
        songs.setOnClickListener(value -> {
            pdfView.jumpTo(347);
            dialog.dismiss();
        });
        notes.setOnClickListener(value -> {
            pdfView.jumpTo(359);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void setCheckBox(String pref, MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(PRIVATE_PREF, MODE_PRIVATE).edit();
        editor.putBoolean(pref, !item.isChecked());
        item.setChecked(!item.isChecked());
        editor.apply();
    }

    private void initCheckBoxes(Menu menu) {
        boolean horizontal_scroll = myPref.getBoolean(PREF_SCROLL_DIRECTION, true);
        boolean page_by_page = myPref.getBoolean(PREF_SCROLL_TYPE, true);
        menu.getItem(0).setChecked(horizontal_scroll);
        menu.getItem(1).setChecked(page_by_page);
    }

    @SuppressLint("NonConstantResourceId")
    private void openSettings(int newId) {
        PopupMenu popup = new PopupMenu(this, findViewById(newId));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu.settings_menu, popup.getMenu());
        initCheckBoxes(popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case id.scroll:
                    setCheckBox(PREF_SCROLL_DIRECTION, item);
                    initPdf();
                    break;
                case id.page_by_page:
                    setCheckBox(PREF_SCROLL_TYPE, item);
                    pdfView.setPageSnap(item.isChecked());
                    pdfView.setPageFling(item.isChecked());
                    break;
                default:
                    break;
            }
            keepPopUpAlive(item);
            return false;
        });
        popup.show();
    }

    private void keepPopUpAlive(MenuItem item) {
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(getBaseContext()));
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
    }

    private void initPdf() {
        myScrollHandler = new MyScrollHandler(this);
        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.VISIBLE);
        boolean horizontal_scroll = myPref.getBoolean(PREF_SCROLL_DIRECTION, true);
        boolean page_by_page = myPref.getBoolean(PREF_SCROLL_TYPE, true);
        pdfView = findViewById(id.pdfView);
        pdfView.fromAsset("book.pdf")
                .swipeHorizontal(horizontal_scroll)
                .scrollHandle(myScrollHandler)
                .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .pageSnap(page_by_page) // snap pages to screen boundaries
                .pageFling(page_by_page) // make a fling change only a single page like ViewPager
                .pageFitPolicy(FitPolicy.BOTH)
                .autoSpacing(true)
                .onLoad(pageNumber -> progressBar.setVisibility(View.GONE))
                .load();
    }
}