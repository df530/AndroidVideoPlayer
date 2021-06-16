package com.example.avp.lists;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.lists.menu.CustomPopupMenuBuilder;
import com.example.avp.player.AVPMediaMetaData;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.example.avp.ui.Constants;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final Constants.DisplayMode displayMode;
    private final CustomPopupMenuBuilder popupMenuBuilder;
    private final VideosHolder videosHolder;
    private static final Executor mExecutor = Executors.newSingleThreadExecutor();

    public VideoAdapter(Constants.DisplayMode displayMode, CustomPopupMenuBuilder popupMenuBuilder, VideosHolder videosHolder) {
        this.displayMode = displayMode;
        this.popupMenuBuilder = popupMenuBuilder;
        this.videosHolder = videosHolder;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int resource = displayMode == Constants.DisplayMode.LIST ? R.layout.video_list_item : R.layout.video_gallery_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new VideoAdapter.ViewHolder(view, displayMode);
    }

    // you can use static method from ExoPlayerActivity
    protected abstract void onClickItemListener(View v, AVPMediaMetaData metaData);

    // you can return null to make path TextView invisible, or return path, or link or something else
    protected abstract String getTextForPathTV(AVPMediaMetaData mediaMetaData);

    // you can return null to make link TextView invisible, or return path, or link or something else
    protected abstract String getTextForLinkTV(AVPMediaMetaData mediaMetaData);

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        AVPMediaMetaData metaData = videosHolder.getVideoMetaDataByPositionInList(position);
        holder.selectRL.setOnClickListener(v -> onClickItemListener(v, metaData));

        Tasks.call(mExecutor, metaData::getPreviewBitmap).addOnSuccessListener(holder.previewIV::setImageBitmap);

        holder.titleTV.setText(metaData.getTitle());
        if (metaData.getDuration() != null) {
            holder.durationTV.setText(DurationFormatUtils.formatDuration(metaData.getDuration(), "**H:mm:ss**", true));
        }

        holder.menuIB.setOnClickListener(v -> popupMenuBuilder.build().show());

        if (displayMode == Constants.DisplayMode.LIST) {
            holder.linkTV.setText(getTextForLinkTV(metaData));
            holder.pathTV.setText(getTextForPathTV(metaData));
        }
    }

    @Override
    public int getItemCount() {
        return videosHolder.getSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout selectRL;
        private final ImageView previewIV;
        private final TextView titleTV;
        private final TextView durationTV;
        private final ImageButton menuIB;
        protected TextView pathTV = null;
        protected TextView linkTV = null;

        public ViewHolder(@NonNull View itemView, Constants.DisplayMode displayMode) {
            super(itemView);
            selectRL = itemView.findViewById(R.id.rl_select);
            titleTV = itemView.findViewById(R.id.tv_video_title);
            durationTV = itemView.findViewById(R.id.tv_video_duration);
            previewIV = itemView.findViewById(R.id.iv_preview);
            menuIB = itemView.findViewById(R.id.ib_menu);
            if (displayMode == Constants.DisplayMode.LIST) {
                linkTV = itemView.findViewById(R.id.tv_link);
                pathTV = itemView.findViewById(R.id.tv_path);
            }
        }
    }
}
