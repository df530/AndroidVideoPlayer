package com.example.avp.model;

import lombok.Getter;
import lombok.Setter;

public class VideoModel {
    @Getter @Setter
    private String strPath;
    @Getter @Setter
    private String strThumb;
    @Setter
    private boolean booleanSelected;

    public boolean isBooleanSelected() {
        return booleanSelected;
    }
}
