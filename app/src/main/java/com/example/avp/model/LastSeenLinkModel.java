package com.example.avp.model;

import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

public class LastSeenLinkModel {
    @Getter @Setter
    private String link;

    public LastSeenLinkModel(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof LastSeenLinkModel) {
            return ((LastSeenLinkModel)obj).link.equals(link);
        }
        return false;
    }
}
