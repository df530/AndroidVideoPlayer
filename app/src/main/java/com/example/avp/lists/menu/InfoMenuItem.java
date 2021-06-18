package com.example.avp.lists.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.avp.R;
import com.example.avp.player.AVPMediaMetaData;

import java.util.List;
import java.util.function.Function;

public final class InfoMenuItem implements MenuItem {
    private static PopupWindow lastOpenedInfoPW = null;

    private final List<InfoElement> infoElements;
    /* description from javadoc: 'a parent view to get the View.getWindowToken() token from'.
     * Earlier we gave there 'model.getActivity().findViewById(R.id.fragment_container)' -- the fragment layout
     * of mainActivity. But now, I think, we have to give there recyclerView. I will test it.
     */
    private final View parentView;

    public InfoMenuItem(List<InfoElement> infoElements, View parentView) {
        this.infoElements = infoElements;
        this.parentView = parentView;
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClickItem(Context context, AVPMediaMetaData metaData) {
        // I don't know why, parentFragment.getContext() == null sometimes
        LinearLayout infoElementsLL =
                (LinearLayout) LayoutInflater.from(context).inflate(R.layout.popup_info, null);
        //new LinearLayout(context);
        infoElementsLL.setOrientation(LinearLayout.VERTICAL);
        infoElementsLL.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 0, 0);
        infoElementsLL.setLayoutParams(layoutParams);

        for (InfoElement infoElement : infoElements) {
            TextView titleTV = new TextView(context);

            titleTV.setText(infoElement.title);
            titleTV.setTypeface(Typeface.DEFAULT_BOLD);
            titleTV.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams titleLP = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            titleLP.setMargins(10, 10, 0, 0);
            titleTV.setLayoutParams(titleLP);

            TextView valueTV = new TextView(context);
            valueTV.setText(infoElement.getValue.apply(metaData));
            valueTV.setTextColor(Color.GRAY);
            LinearLayout.LayoutParams valueLP = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            valueLP.setMargins(10, 10, 0, 0);
            valueTV.setLayoutParams(valueLP);

            infoElementsLL.addView(titleTV);
            infoElementsLL.addView(valueTV);
        }

        PopupWindow infoPopupWindow = new PopupWindow(
                infoElementsLL,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        infoElementsLL.setOnTouchListener((v, event) -> {
            infoPopupWindow.dismiss();
            lastOpenedInfoPW = null;
            return true;
        });

        showPopupWindow(context, infoPopupWindow, 0, 0);
    }

    private void showPopupWindow(Context context, PopupWindow popupWindow, int x, int y) {
        if (lastOpenedInfoPW != null) {
            lastOpenedInfoPW.dismiss();
        }
        lastOpenedInfoPW = popupWindow;
        popupWindow.showAtLocation(parentView, Gravity.CENTER_VERTICAL, x, y);
    }

    public static class InfoElement {
        private final String title;
        private final Function<AVPMediaMetaData, String> getValue;

        public InfoElement(String title, Function<AVPMediaMetaData, String> getValue) {
            this.title = title;
            this.getValue = getValue;
        }
    }
}
