package com.example.avp.model;

import androidx.annotation.Nullable;

import com.example.avp.player.AVPMediaMetaData;

import lombok.Getter;
import lombok.Setter;

public class LastSeenMetaDataModel {
    @Getter @Setter
    private AVPMediaMetaData metaData;

    public LastSeenMetaDataModel(AVPMediaMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof LastSeenMetaDataModel)
            return ((LastSeenMetaDataModel)obj).metaData.equals(metaData);
        return false;
    }
}
