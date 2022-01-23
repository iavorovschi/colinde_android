package com.miki.colinde;

import static com.miki.colinde.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;


public class MainActivity extends AppCompatActivity {

    private static final String PRIVATE_PREF = "my_preferences";
    private static final String PREF_SCROLL_DIRECTION = "horizontal_scroll";
    private static final String PREF_SCROLL_TYPE = "scroll_page_by_page";

    private PDFView pdfView;
    private SharedPreferences myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        setSupportActionBar(findViewById(id.appBar));
        myPref = getSharedPreferences(PRIVATE_PREF, MODE_PRIVATE);
        initPdf();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu newMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menu.menu, newMenu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case id.bookmarks:
                Toast.makeText(this, "Bookmarks", Toast.LENGTH_SHORT).show();
                break;
            case id.settings:
                openSettings(id.settings);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
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
        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.VISIBLE);
        boolean horizontal_scroll = myPref.getBoolean(PREF_SCROLL_DIRECTION, true);
        boolean page_by_page = myPref.getBoolean(PREF_SCROLL_TYPE, true);
        pdfView = findViewById(id.pdfView);
        pdfView.fromAsset("book.pdf")
                .swipeHorizontal(horizontal_scroll)
                .scrollHandle(new DefaultScrollHandle(this))
                .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
                .pageSnap(page_by_page) // snap pages to screen boundaries
                .pageFling(page_by_page) // make a fling change only a single page like ViewPager
                .pageFitPolicy(FitPolicy.BOTH)
                .autoSpacing(true)
                .onLoad(pageNumber -> progressBar.setVisibility(View.GONE))
                .load();
    }
}