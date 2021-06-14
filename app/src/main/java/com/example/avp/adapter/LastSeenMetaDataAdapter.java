package com.example.avp.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.model.Model;
import com.example.avp.player.AVPMediaMetaData;
import com.example.avp.player.ExoPlayerActivity;

public class LastSeenMetaDataAdapter extends RecyclerView.Adapter<LastSeenMetaDataAdapter.ViewHolder> {
    private final Model model;
    private final Fragment parentFragment;

    public LastSeenMetaDataAdapter(Model model, Fragment parentFragment) {
        this.model = model;
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public LastSeenMetaDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_seen_link, parent, false);
        return new LastSeenMetaDataAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull LastSeenMetaDataAdapter.ViewHolder holder, int position) {
        holder.rlSelect.setBackgroundColor(Color.WHITE);
        holder.rlSelect.setAlpha(0);

        AVPMediaMetaData metaData = model.getRecentMetaData(position);
        holder.textView.setText(metaData.getLink());
        holder.textViewVideoName.setText(metaData.getTitle());
        holder.previewImage.setImageBitmap(metaData.getPreviewBitmap());
        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(model.getContext(), ExoPlayerActivity.class);
            intent.putExtra("linkOnVideo", metaData.getLink());
            parentFragment.startActivityForResult(intent, 1);
        });

        holder.rlSelect.setOnLongClickListener(e -> {
            ClipboardManager clipboard = (ClipboardManager) model.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", metaData.getLink());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(model.getContext(), "Link copied", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return model.getLastSeenVideosListSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rlSelect;
        private final TextView textView;
        private final TextView textViewVideoName;
        private final ImageView previewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlSelect = itemView.findViewById(R.id.rl_select);
            textView = itemView.findViewById(R.id.tv_link);
            textViewVideoName = itemView.findViewById(R.id.tv_video_name);
            previewImage = itemView.findViewById(R.id.iv_image);
        }
    }
}
