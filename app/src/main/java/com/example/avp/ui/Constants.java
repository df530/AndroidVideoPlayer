package com.example.avp.ui;


import android.provider.MediaStore;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import lombok.Getter;

public final class Constants {
    private Constants() {
    }

    public static final int PERMISSION_REQUEST_CODE = 123;

    public enum DisplayMode implements Serializable {
        GALLERY(2),
        LIST(1);

        @Getter
        private final int numOfColumns;

        DisplayMode(int numOfColumns) {
            this.numOfColumns = numOfColumns;
        }
    }

    public enum SortParam implements Serializable {
        DATE_TAKEN(MediaStore.Images.Media.DATE_TAKEN),
        NAME(MediaStore.Images.Media.DISPLAY_NAME);

        @Getter
        private final String forDeviceSort;

        SortParam(String forDeviceSort) {
            this.forDeviceSort = forDeviceSort;
        }
    }

    public static final @NotNull String videoListSettingsVariableKey = "VARIABLE_VIDEO_LIST_SETTINGS";
    public static final @NotNull String lastSeenVideosHolderVariableKey = "VARIABLE_LAST_SEEN_VIDEOS_HOLDER";
    public static final @NotNull String arrayListVideosVariableKey = "VARIABLE_ARRAY_LIST_VIDEOS";

    public static final @NotNull String hasVisited = "hasVisited";

    public static final @NotNull String APP_PREFERENCES = "settings";
    public static final @NotNull String APP_PREFERENCES_MODEL = "Model";
    public static final @NotNull String APP_PREFERENCES_MENU = "Menu";
}
