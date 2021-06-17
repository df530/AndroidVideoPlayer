package com.example.avp.lists;

import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;

import java.io.Serializable;
import java.util.Collection;

public interface VideosHolder extends Serializable {
    AVPMediaMetaData getVideoMetaDataByPositionInList(int position);

    int getSize();

    void add(AVPMediaMetaData metaData);

    default void addAll(Collection<AVPMediaMetaData> collectionOfVideo) {
        for (AVPMediaMetaData metaData : collectionOfVideo)
            add(metaData);
    }

    /* if you want never reverse, do nothing there
     * returns: true if holder changed, false otherwise
     */
    boolean reverse();

    /* if you don't want to sort by some parameter, ignore it
     * returns: true if holder changed, false otherwise
     */
    boolean sortBy(Constants.SortParam sortParam);

    /* Use this key in load and save state
     * This key must be unique for every videoHolder: don't forget about this!!!
     * I thought make singleton for every holder implementation and use
     * class canonical name as serialization key. Methods below help to implement this scheme.
     */
    default String getSerializationKey() {
        return getClass().getCanonicalName();
    }

    /* class VideosHolderImpl implements VideosHolder {
     *     private static final VideosHolderImpl instance = new VideosHolderImpl();
     *     private VideosHolderImpl() {};
     *     @Override
     *     public static VideosHolder getInstance() {
     *         return instance;
     *     }
     * }
     */
}