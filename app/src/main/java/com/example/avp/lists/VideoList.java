package com.example.avp.lists;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.ui.Constants;
import com.example.avp.utils.JsonStateSaveLoader;
import com.example.avp.utils.StateSaveLoader;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import lombok.NonNull;

/* Instruction for implementation:
 * 1) Implement VideoHolder
 * 2) Implement VideoAdapter
 * 3) Implement VideoList
 * Not forget to add your VideoListImplementation in model.videoLists (for getting update of list settings)
 */
public abstract class VideoList {
    protected final RecyclerView videoListRV;
    protected VideosHolder videosHolder;
    protected final Context context;
    protected VideoListSettings listSettings;
    private final Set<Constants.DisplayMode> possibleDisplayModes;

    private static final Executor mExecutor = Executors.newSingleThreadExecutor();

    public VideoList(RecyclerView videoListRV, VideosHolder videosHolder, Context context, VideoListSettings listSettings,
                     Set<Constants.DisplayMode> possibleDisplayModes) {
        if (possibleDisplayModes.isEmpty())
            throw new IllegalArgumentException("No possible display modes");
        this.videoListRV = videoListRV;
        this.videosHolder = videosHolder;
        this.context = context;
        this.listSettings = new VideoListSettings(listSettings);
        this.possibleDisplayModes = possibleDisplayModes;
        if (!possibleDisplayModes.contains(listSettings.displayMode)) {
            this.listSettings.displayMode = possibleDisplayModes.iterator().next();
        }

        videoListRV.setLayoutManager(new GridLayoutManager(context, listSettings.displayMode.getNumOfColumns()));
    }

    protected void updateRecycleView() {
        videoListRV.setAdapter(createVideoAdapter());
    }

    protected abstract VideoAdapter createVideoAdapter();

    /* If you need some params, you can add relevant fields and set them in constructor
     */
    protected abstract @NonNull Task<Void> fetchVideos();

    public final void fetchVideosAndUpdate() {
        fetchVideos().addOnSuccessListener((voidResult) -> {
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
            if (videosHolder.sortBy(newSortParam)) {
                updateRecycleView();
            }
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
