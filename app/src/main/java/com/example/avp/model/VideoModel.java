package com.example.avp.model;

import lombok.Getter;
import lombok.Setter;

public class VideoModel {
    @Getter @Setter
    private String strPath;
    @Getter @Setter
    private String strThumb;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private boolean isGDriveFile;
    @Setter
    private boolean booleanSelected;

    public boolean isBooleanSelected() {
        return booleanSelected;
    }
}
