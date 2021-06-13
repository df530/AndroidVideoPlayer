package com.example.avp.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Model model;

    private static String displayMode;
    private boolean anyPopupOpened = false;
    private PopupWindow lastPopupOpened;

    public VideoAdapter(Model model) {
        this.model = model;
        displayMode = model.getVideoListDisplayMode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        int resource = model.getVideoListDisplayMode().equals("gallery") ? R.layout.custom_video : R.layout.custom_video_list_mode;
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(model.getContext()).load("file://" + model.getVideoThumb(position))
                .skipMemoryCache(false).into(holder.imageView);
        holder.rlSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.rlSelect.setAlpha(0);

        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(model.getContext(), ExoPlayerActivity.class);
            intent.putExtra("linkOnVideo", model.getVideoPath(position));
            model.getActivity().startActivity(intent);

        });

        String link = model.getVideoPath(position);
        holder.imageButton.setOnClickListener(v -> showPopupMenu(v, link));

        if ("list".equals(model.getVideoListDisplayMode())) {
            holder.textView.setText(link);
            holder.textViewVideoName.setText(model.getVideoName(link));
        }
    }

    private void showPopupWindow(PopupWindow popupWindow, View popupView, int x, int y) {
        if (anyPopupOpened) {
            lastPopupOpened.dismiss();
        }
        anyPopupOpened = true;
        lastPopupOpened = popupWindow;
        popupWindow.showAtLocation(model.getActivity().findViewById(R.id.fragment_container), Gravity.CENTER_VERTICAL, x, y);
    }

    @SuppressLint("ClickableViewAccessibility")
    private boolean processOnClickInfoItem(String currentVideoLink) {
        View popupView = LayoutInflater.from(model.getActivity()).inflate(R.layout.popup_info, null);
        popupView.setFocusable(true);

        TextView videoNameTextView = popupView.findViewById(R.id.video_name);
        videoNameTextView.setText(model.getVideoName(currentVideoLink));

        TextView videoSizeTextView = popupView.findViewById(R.id.video_size);
        videoSizeTextView.setText(model.getFileSizeMegaBytes(currentVideoLink));

        TextView videoDurationTextView = popupView.findViewById(R.id.video_duration);
        videoDurationTextView.setText(model.getVideoDuration(currentVideoLink));

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
            default:
                return false;
        }
    }

    private void showPopupMenu(View v, String currentVideoLink) {
        PopupMenu popupMenu = new PopupMenu(model.getContext(), v);
        popupMenu.inflate(R.menu.video_menu);
        popupMenu.setOnMenuItemClickListener(item -> onOptionsItemSelected(item, currentVideoLink));
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return model.getArrayListVideosSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final RelativeLayout rlSelect;
        private TextView textView;
        private ImageButton imageButton;
        private TextView textViewVideoName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            rlSelect = itemView.findViewById(R.id.rl_select);
            imageButton = itemView.findViewById(R.id.iv_menu_button);

            if (displayMode.equals("list")) {
                textView = itemView.findViewById(R.id.tv_text);
                textViewVideoName = itemView.findViewById(R.id.tv_video_name);
            }
        }
    }
}
