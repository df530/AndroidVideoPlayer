package com.example.avp.ui;

import android.provider.MediaStore;

public class VideoListSettings {

    public String sortedBy = MediaStore.Images.Media.DATE_TAKEN;
    public int columnsNum = 2;
    public boolean reversedOrder = false;
    public String displayMode = "gallery";

    //TODO: field for template
}
