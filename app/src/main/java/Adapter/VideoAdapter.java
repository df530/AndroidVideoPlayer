package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.avp.R;
import com.example.avp.player.ExoPlayerActivity;
import com.example.avp.ui.VideoPlayerActivity;

import java.util.ArrayList;

import Model.VideoModel;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<VideoModel> arrayListVideos;
    private Activity activity;
    private static String displayMode;

    public VideoAdapter(Context context, ArrayList<VideoModel> arrayListVideos, Activity activity, String displayMode) {
        this.context = context;
        this.arrayListVideos = arrayListVideos;
        this.activity = activity;
        VideoAdapter.displayMode = displayMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (displayMode.equals("gallery")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video_list_mode, parent, false);
        }
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load("file://" + arrayListVideos.get(position).getStr_thumb())
                .skipMemoryCache(false).into(holder.imageView);
        holder.rlSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
        holder.rlSelect.setAlpha(0);

        holder.rlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ExoPlayerActivity.class);
                intent.putExtra("linkOnVideo", arrayListVideos.get(position).getStr_path());
                activity.startActivity(intent);

            }
        });

        if ("list".equals(displayMode)) {
            holder.textView.setText(arrayListVideos.get(position).getStr_path());
        }
    }

    @Override
    public int getItemCount() {
        return arrayListVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RelativeLayout rlSelect;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            rlSelect = itemView.findViewById(R.id.rl_select);
            if (displayMode.equals("list")) {
                textView = itemView.findViewById(R.id.tv_text);
            }
        }
    }
}
