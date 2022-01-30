package com.miki.colinde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.Util;

public class MyScrollHandler extends RelativeLayout implements ScrollHandle {

    private final static int HANDLE_LONG = 35;
    private final static int HANDLE_SHORT = 30;
    private final static int DEFAULT_TEXT_SIZE = 14;

    private float relativeHandlerMiddle = 0f;

    protected TextView textView;
    protected Context context;
    private PDFView pdfView;
    private float currentPos;
    private float currentOffsetPosition;

    private final Handler handler = new Handler();
    private final Runnable hidePageScrollerRunnable = this::hide;

    public MyScrollHandler(Context context) {
        super(context);
        this.context = context;
        textView = new TextView(context);
        setVisibility(INVISIBLE);
        setTextColor(Color.WHITE);
        setTextSize(DEFAULT_TEXT_SIZE);
    }

    @Override
    public void setupLayout(PDFView pdfView) {
        int align, width, height;
        Drawable background;
        // determine handler position, default is right (when scrolling vertically) or bottom (when scrolling horizontally)
        if (pdfView.isSwipeVertical()) {
            width = HANDLE_LONG;
            height = HANDLE_SHORT;
            align = ALIGN_PARENT_RIGHT;
            background = ContextCompat.getDrawable(context, R.drawable.default_scroll_handle_right);
        } else {
            width = HANDLE_SHORT;
            height = HANDLE_LONG;
            align = ALIGN_PARENT_BOTTOM;
            background = ContextCompat.getDrawable(context, R.drawable.default_scroll_handle_bottom);

        }
        assert background != null;
        background.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.rgb(28,28,28), BlendModeCompat.SRC_ATOP));
        setBackground(background);

        LayoutParams lp = new LayoutParams(Util.getDP(context, width), Util.getDP(context, height));
        lp.setMargins(0, 0, 0, 0);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(textView, layoutParams);

        lp.addRule(align);
        pdfView.addView(this, lp);

        this.pdfView = pdfView;
    }

    @Override
    public void destroyLayout() {
        pdfView.removeView(this);
    }

    @Override
    public void setScroll(float position) {
        if (!shown()) {
            show();
        } else {
            handler.removeCallbacks(hidePageScrollerRunnable);
        }
        if (pdfView != null) {
            setPosition((pdfView.isSwipeVertical() ? pdfView.getHeight() : pdfView.getWidth()) * position);
        }
    }

    private void setPosition(float pos) {
        if (Float.isInfinite(pos) || Float.isNaN(pos)) {
            return;
        }
        float pdfViewSize;
        if (pdfView.isSwipeVertical()) {
            pdfViewSize = pdfView.getHeight();
        } else {
            pdfViewSize = pdfView.getWidth();
        }
        pos -= relativeHandlerMiddle;

        if (pos < 0) {
            pos = 0;
        } else if (pos > pdfViewSize - Util.getDP(context, HANDLE_SHORT)) {
            pos = pdfViewSize - Util.getDP(context, HANDLE_SHORT);
        }

        if (pdfView.isSwipeVertical()) {
            setY(pos);
        } else {
            setX(pos);
        }

        calculateMiddle();
        invalidate();
    }

    private void calculateMiddle() {
        float pos, viewSize, pdfViewSize;
        if (pdfView.isSwipeVertical()) {
            pos = getY();
            viewSize = getHeight();
            pdfViewSize = pdfView.getHeight();
        } else {
            pos = getX();
            viewSize = getWidth();
            pdfViewSize = pdfView.getWidth();
        }
        relativeHandlerMiddle = ((pos + relativeHandlerMiddle) / pdfViewSize) * viewSize;
    }

    @Override
    public void hideDelayed() {
        handler.postDelayed(hidePageScrollerRunnable, 1000);
    }

    @Override
    public void setPageNum(int pageNum) {
        String text = String.valueOf(pageNum);
        if (!textView.getText().equals(text)) {
            textView.setText(text);
        }
    }

    @Override
    public boolean shown() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(INVISIBLE);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    private boolean isPDFViewReady() {
        return pdfView != null && pdfView.getPageCount() > 0 && !pdfView.documentFitsView();
    }

    @Override
    public boolean callOnClick() {
        return super.callOnClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isPDFViewReady()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                pdfView.stopFling();
                handler.removeCallbacks(hidePageScrollerRunnable);
                if (pdfView.isSwipeVertical()) {
                    currentPos = event.getRawY() - getY();
                } else {
                    currentPos = event.getRawX() - getX();
                }
            case MotionEvent.ACTION_MOVE:
                currentOffsetPosition = pdfView.getCurrentPage();
                if (pdfView.isSwipeVertical()) {
                    setPosition(event.getRawY() - currentPos + relativeHandlerMiddle);
                    pdfView.setPositionOffset(relativeHandlerMiddle / (float) getHeight(), false);
                } else {
                    setPosition(event.getRawX() - currentPos + relativeHandlerMiddle);
                    pdfView.setPositionOffset(relativeHandlerMiddle / (float) getWidth(), false);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                hideDelayed();
                pdfView.performPageSnap();
                if (pdfView.isSwipeVertical() && (currentOffsetPosition == pdfView.getCurrentPage())) {
                    onCreateDialog();
                } else if (!pdfView.isSwipeVertical() && (currentOffsetPosition == pdfView.getCurrentPage())) {
                    onCreateDialog();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    public void onCreateDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_jump_to_page, null);
        dialogBuilder.setView(dialogView);

        TextView pageNumberField = dialogView.findViewById(R.id.page_count);
        String pageNumberString = (pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount();
        pageNumberField.setText(pageNumberString);

        EditText editText = dialogView.findViewById(R.id.page_field);

        dialogBuilder.setTitle(getResources().getString(R.string.page_by_page_title));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.button_ok), null);
        dialogBuilder.setNegativeButton(getResources().getString(R.string.button_cancel), (dialog, id) -> dialog.cancel());
        Dialog dialog = dialogBuilder.create();

        addOkListener(editText, dialog);
        dialog.show();
    }

    private void addOkListener(EditText editText, Dialog dialog) {
        dialog.setOnShowListener(sameDialog -> {
            Button button = ((AlertDialog) sameDialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                int pageNumber;
                try {
                    pageNumber = Integer.parseInt(String.valueOf(editText.getText())) - 1;
                } catch (Exception e) {
                    editText.setError(getResources().getString(R.string.page_number_error));
                    return;
                }

                if (pageNumber < 0 || pageNumber > 420) {
                    editText.setError(getResources().getString(R.string.page_number_error));
                } else {
                    pdfView.jumpTo(pageNumber);
                    sameDialog.dismiss();
                }
            });
        });
    }
}
