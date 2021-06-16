package com.example.avp.lists.menu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.example.avp.player.AVPMediaMetaData;

import lombok.Getter;

public interface MenuItem {
    String getTitle();
    void onClickItem(Context context, AVPMediaMetaData metaData);

    MenuItem copyLink = new MenuItem() {
        @Override
        public String getTitle() {
            return "Copy link";
        }

        @Override
        public void onClickItem(Context context, AVPMediaMetaData metaData) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", metaData.getLink());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Link copied", Toast.LENGTH_LONG).show();
        }
    };
}
