package com.example.avp.lists.menu;

import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

import com.example.avp.player.AVPMediaMetaData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Setter;

public final class CustomPopupMenuBuilder {
    private final List<MenuItem> menuItems;
    @Setter
    private View anchorView;
    @Setter
    private AVPMediaMetaData anchorVideoMetaData;

    public CustomPopupMenuBuilder(@NotNull List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public PopupMenu build() {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);

        for (MenuItem menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem.getTitle());
        }
        popupMenu.setOnMenuItemClickListener(item -> onOptionsItemSelected(anchorView.getContext(), item, anchorVideoMetaData));

        return popupMenu;
    }

    private boolean onOptionsItemSelected(@NotNull Context context, @NotNull android.view.MenuItem item, @NotNull AVPMediaMetaData metaData) {
        for (MenuItem menuItem : menuItems) {
            if (item.getTitle().equals(menuItem.getTitle())) {
                menuItem.onClickItem(context, metaData);
                return true;
            }
        }
        return false;
    }
}
