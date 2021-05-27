package com.example.avp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avp.R;
import com.example.avp.model.Model;
import com.example.avp.player.ExoPlayerActivity;

import java.io.File;

public class LastSeenLinksAdapter extends RecyclerView.Adapter<LastSeenLinksAdapter.ViewHolder> {

    private final Model model;
    private final Activity activity;

    public LastSeenLinksAdapter(Model model, Activity activity) {
        this.model = model;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LastSeenLinksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_seen_link, parent, false);
        return new LastSeenLinksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastSeenLinksAdapter.ViewHolder holder, int position) {
        holder.rlSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.rlSelect.setAlpha(0);
        String link = model.getRecentLink(position);
        holder.textView.setText(link);

        holder.textViewVideoName.setText(getVideoName(link));

        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getApplicationContext(), ExoPlayerActivity.class);
            intent.putExtra("linkOnVideo", model.getRecentLink(position));
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return model.getLastSeenLinksCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlSelect;
        private TextView textView;
        private TextView textViewVideoName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlSelect = itemView.findViewById(R.id.rl_select);
            textView = itemView.findViewById(R.id.tv_link);
            textViewVideoName = itemView.findViewById(R.id.tv_video_name);
        }
    }

    private String getVideoName(String link) {
        String[] parts = link.split(File.separator);
        return parts[parts.length - 1];
    }
}
