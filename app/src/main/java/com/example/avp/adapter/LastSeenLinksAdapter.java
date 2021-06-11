package com.example.avp.adapter;

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

public class LastSeenLinksAdapter extends RecyclerView.Adapter<LastSeenLinksAdapter.ViewHolder> {

    private final Model model;

    public LastSeenLinksAdapter(Model model) {
        this.model = model;
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

        holder.textViewVideoName.setText(model.getVideoName(link));

        holder.rlSelect.setOnClickListener(v -> {
            Intent intent = new Intent(model.getContext(), ExoPlayerActivity.class);
            intent.putExtra("linkOnVideo", model.getRecentLink(position));
            model.getActivity().startActivity(intent);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlSelect = itemView.findViewById(R.id.rl_select);
            textView = itemView.findViewById(R.id.tv_link);
            textViewVideoName = itemView.findViewById(R.id.tv_video_name);
        }
    }
}
