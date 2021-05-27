package com.example.avp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.avp.R;
import com.example.avp.model.Model;
import com.example.avp.player.ExoPlayerActivity;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Model model;
    private final Activity activity;

    private boolean anyPopupOpened = false;
    private PopupWindow lastPopupOpened;

    public VideoAdapter(Model model, Activity activity) {
        this.model = model;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int resource = model.getVideListDisplayMode().equals("gallery") ? R.layout.custom_video : R.layout.custom_video_list_mode;
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(activity.getApplicationContext()).load("file://" + model.getVideoThumb(position))
                .skipMemoryCache(false).into(holder.imageView);
        holder.rlSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.rlSelect.setAlpha(0);

        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getApplicationContext(), ExoPlayerActivity.class);
            model.setCurrentVideFromList(position);
            activity.startActivity(intent);
        });

        if ("list".equals(model.getVideListDisplayMode())) {
            String link = model.getVideoPath(position);
            holder.textView.setText(link);
            holder.textViewVideoName.setText(getVideoName(link));
            holder.imageButton.setOnClickListener(v -> showPopupMenu(v, link));
        }
    }

    private void showPopupWindow(PopupWindow popupWindow, View popupView, int x, int y) {
        if (anyPopupOpened) {
            lastPopupOpened.dismiss();
        }
        anyPopupOpened = true;
        lastPopupOpened = popupWindow;
        popupWindow.showAtLocation(activity.findViewById(R.id.fragment_container), Gravity.CENTER_VERTICAL, x, y);
    }

    private boolean processOnClickInfoItem(String currentVideoLink) {
        View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_info, null);
        popupView.setFocusable(true);

        TextView videoNameTextView = popupView.findViewById(R.id.video_name);
        videoNameTextView.setText(getVideoName(currentVideoLink));

        TextView videoSizeTextView = popupView.findViewById(R.id.video_size);
        videoSizeTextView.setText(getFileSizeMegaBytes(currentVideoLink));

        TextView videoDurationTextView = popupView.findViewById(R.id.video_duration);
        videoDurationTextView.setText(getVideoDuration(currentVideoLink));

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            anyPopupOpened = false;
            return true;
        });

        showPopupWindow(popupWindow, popupView, 0, 0);
        return true;
    }

    private boolean processOnClickRenameItem(String currentVideoLink) {
        //TODO
        return true;
    }

    private boolean onOptionsItemSelected(@NonNull MenuItem item, @NonNull String currentVideoLink) {
        switch (item.getItemId()) {
            case R.id.video_info_item:
                return processOnClickInfoItem(currentVideoLink);
            case R.id.video_rename_item:
                return processOnClickRenameItem(currentVideoLink);
                    /*case R.id.video_delete_item:
                        return true;
                     */
            default:
                return false;
        }
    }

    private void showPopupMenu(View v, String currentVideoLink) {
        PopupMenu popupMenu = new PopupMenu(activity.getApplicationContext(), v);
        popupMenu.inflate(R.menu.video_menu);
        popupMenu.setOnMenuItemClickListener(item -> onOptionsItemSelected(item, currentVideoLink));
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return model.getArrayListVideos().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RelativeLayout rlSelect;
        private TextView textView;
        private ImageButton imageButton;
        private TextView textViewVideoName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            rlSelect = itemView.findViewById(R.id.rl_select);
            if (model.getVideListDisplayMode().equals("list")) {
                textView = itemView.findViewById(R.id.tv_text);
                imageButton = itemView.findViewById(R.id.iv_menu_button);
                textViewVideoName = itemView.findViewById(R.id.tv_video_name);
            }
        }
    }

    private String getVideoName(String link) {
        String[] parts = link.split(File.separator);
        return parts[parts.length - 1];
    }


    private String getFileSizeMegaBytes(String path) {
        File file = new File(path);
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    private String getVideoDuration(String path) {
        Uri uri =  Uri.parse(path);
        MediaPlayer mp = MediaPlayer.create(activity.getApplicationContext(), uri);
        int duration = mp.getDuration();
        mp.release();
        //TODO: move formating to a separate function, add hours
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}
