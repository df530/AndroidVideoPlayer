package com.example.avp.model;

import androidx.annotation.Nullable;

public class LastSeenLinkModel {
    private String link;

    public LastSeenLinkModel(String link) {
        this.link = link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof LastSeenLinkModel) {
            return ((LastSeenLinkModel)obj).link.equals(link);
        }
        return false;
    }
}
