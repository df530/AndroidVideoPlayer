package com.example.avp.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class VideoModel implements Serializable {
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
