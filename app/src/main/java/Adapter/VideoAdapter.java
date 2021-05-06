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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.avp.R;
import com.example.avp.ui.VideoPlayerActivity;

import java.util.ArrayList;

import Model.VideoModel;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<VideoModel> arrayListVideos;
    private Activity activity;

    public VideoAdapter(Context context, ArrayList<VideoModel> arrayListVideos, Activity activity) {
        this.context = context;
        this.arrayListVideos = arrayListVideos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video, parent, false);
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
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("video", arrayListVideos.get(position).getStr_path());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RelativeLayout rlSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_image);
            rlSelect = itemView.findViewById(R.id.rl_select);
        }
    }
}
