package com.example.avp.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.avp.ui.Constants;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private final Model model;

    private static Constants.DisplayMode displayMode;
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
        int resource = model.getVideoListDisplayMode() == Constants.DisplayMode.GALLERY ? R.layout.video_gallery_item : R.layout.video_list_item;
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(model.getContext()).load(model.getVideoThumb(position))
                .skipMemoryCache(false).into(holder.imageView);

        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(model.getContext(), ExoPlayerActivity.class);
            intent.putExtra("linkOnVideo", model.getVideoPath(position));
            model.getActivity().startActivity(intent);

        });

        String link = model.getVideoPath(position);
        holder.imageButton.setOnClickListener(v -> showPopupMenu(v, link));
        holder.textViewVideoName.setText(model.getVideoNameByPosition(position));

        if (model.getVideoListDisplayMode() == Constants.DisplayMode.LIST) {
            holder.textView.setText(link);
            holder.imageButton.setOnClickListener(v -> showPopupMenu(v, link));
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

        /*
        TextView videoNameTextView = popupView.findViewById(R.id.tv_video_title);
        videoNameTextView.setText(model.getVideoName(currentVideoLink));

        TextView videoSizeTextView = popupView.findViewById(R.id.video_size);
        videoSizeTextView.setText(model.getFileSizeMegaBytes(currentVideoLink));

        TextView videoDurationTextView = popupView.findViewById(R.id.tv_video_duration);
        videoDurationTextView.setText(model.getVideoDuration(currentVideoLink));
*/
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
        private final ImageButton imageButton;
        private TextView textView;
        private final TextView textViewVideoName;
        private final TextView videoDurationTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_preview);
            rlSelect = itemView.findViewById(R.id.rl_select);
            imageButton = itemView.findViewById(R.id.ib_menu);
            videoDurationTV = itemView.findViewById(R.id.tv_video_duration);
            textViewVideoName = itemView.findViewById(R.id.tv_video_title);

            if (displayMode == Constants.DisplayMode.LIST) {
                textView = itemView.findViewById(R.id.tv_link);
            }
        }
    }
}
