package com.example.avp.ui;


import org.jetbrains.annotations.NotNull;

public final class Constants {
    private Constants() {
    }

    public static final int PERMISSION_REQUEST_CODE = 123;

    public static final int GALLERY_MODE = 2;
    public static final int LIST_MODE = 1;

    public static final @NotNull String videoListSettingsVariableKey = "VARIABLE_VIDEO_LIST_SETTINGS";
    public static final @NotNull String lastSeenVideosHolderVariableKey = "VARIABLE_LAST_SEEN_VIDEOS_HOLDER";
    public static final @NotNull String arrayListVideosVariableKey = "VARIABLE_ARRAY_LIST_VIDEOS";

    public static final @NotNull String hasVisited = "hasVisited";

    public static final @NotNull String APP_PREFERENCES = "settings";
    public static final @NotNull String APP_PREFERENCES_MODEL = "Model";
    public static final @NotNull String APP_PREFERENCES_MENU = "Menu";
}
