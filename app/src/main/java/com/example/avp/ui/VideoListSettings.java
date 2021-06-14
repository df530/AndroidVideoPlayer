package com.example.avp.ui;

import android.provider.MediaStore;

import java.io.Serializable;

public class VideoListSettings implements Serializable {

    public String sortedBy = MediaStore.Images.Media.DATE_TAKEN;
    public int columnsNum = 2;
    public boolean reversedOrder = false;
    public String displayMode = "gallery";
}
