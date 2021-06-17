package com.example.avp.lists;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;
import com.example.avp.utils.StateSaveLoader;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.NonNull;

/* Instruction for implementation:
 * 1) Implement VideoHolder
 * 2) Implement VideoAdapter
 * 3) Implement VideoList
 * Not forget to add your VideoListImplementation in model.videoLists (for getting update of list settings)
 */
public abstract class VideoList {
    protected RecyclerView videoListRV;
    protected VideosHolder videosHolder;
    protected VideoListSettings listSettings;
    private final Set<Constants.DisplayMode> possibleDisplayModes;
    protected final Context context;
    protected final Fragment parentFragment;

    private static final Executor executor = Executors.newFixedThreadPool(2);

    public VideoList(RecyclerView videoListRV, VideosHolder videosHolder, VideoListSettings listSettings,
                     Set<Constants.DisplayMode> possibleDisplayModes, Fragment parentFragment) {
        if (possibleDisplayModes.isEmpty())
            throw new IllegalArgumentException("No possible display modes");
        this.videoListRV = videoListRV;
        this.videosHolder = videosHolder;
        this.listSettings = listSettings;
        this.possibleDisplayModes = possibleDisplayModes;
        if (!possibleDisplayModes.contains(listSettings.displayMode)) {
            this.listSettings.displayMode = possibleDisplayModes.iterator().next();
        }
        this.context = parentFragment.getContext();
        this.parentFragment = parentFragment;
    }

    public void setVideoListRV(@NonNull RecyclerView videoListRV) {
        this.videoListRV = videoListRV;
        videoListRV.setLayoutManager(new GridLayoutManager(context, listSettings.displayMode.getNumOfColumns()));
        updateRecycleView();
    }

    protected void updateRecycleView() {
        videoListRV.setLayoutManager(new GridLayoutManager(context, listSettings.displayMode.getNumOfColumns()));
        videoListRV.setAdapter(createVideoAdapter());
    }

    protected abstract VideoAdapter createVideoAdapter();

    /* If you need some params, you can add relevant fields and set them in constructor
     * use current thread, it will be called not in main thread
     */
    protected abstract @NonNull void fetchVideos();

    public final void fetchVideosAndUpdate() {
        Tasks.call(executor, () -> {
            fetchVideos();
            return null;
        }).addOnSuccessListener((voidResult) -> {
            videosHolder.sortBy(listSettings.sortParam);
            if (listSettings.reversedOrder) {
                videosHolder.reverse();
            }
            updateRecycleView();
        });
    }

    public final void add(AVPMediaMetaData metaData, boolean doUpdateRecycleView) {
        videosHolder.add(metaData);
        if (doUpdateRecycleView) {
            updateRecycleView();
        }
    }

    public final void onReverseOrderChange(boolean reversedOrder) {
        if (listSettings.reversedOrder != reversedOrder) {
            listSettings.reversedOrder = reversedOrder;
            if (videosHolder.reverse()) {
                updateRecycleView();
            }
        }
    }

    public final void onSortedByChange(Constants.SortParam newSortParam) {
        if (listSettings.sortParam != newSortParam) {
            listSettings.sortParam = newSortParam;
            Tasks.call(executor, () -> videosHolder.sortBy(newSortParam))
                    .addOnSuccessListener(wasSorted -> {
                        if (wasSorted) {
                            updateRecycleView();
                        }
                    });
        }
    }

    public final void onDisplayModeChanged(Constants.DisplayMode newDisplayMode) {
        if (possibleDisplayModes.contains(listSettings.displayMode) &&
                newDisplayMode != listSettings.displayMode) {
            listSettings.displayMode = newDisplayMode;
            videoListRV.setLayoutManager(new GridLayoutManager(context, listSettings.displayMode.getNumOfColumns()));
            updateRecycleView();
        }
    }

    /* you can do nothing, if you don't want to save state (gdrive for example)
     * Not forget, that videosHolder maybe null, get class carefully
     */
    public abstract void loadSavedState(StateSaveLoader saveLoader);

    // you can do nothing, if you don't want to save state (gdrive for example)
    public abstract void saveState(StateSaveLoader saveLoader);
}
