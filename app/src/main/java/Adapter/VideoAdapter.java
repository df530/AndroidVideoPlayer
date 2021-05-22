package Adapter;

import android.app.Activity;
import android.content.Context;
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
import com.example.avp.player.ExoPlayerActivity;
import com.example.avp.ui.VideoPlayerActivity;

import java.io.File;
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
            String link = arrayListVideos.get(position).getStr_path();
            holder.textView.setText(link);
            holder.textViewVideoName.setText(getVideoName(link));
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }
    }

    private boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.video_info_item:
                View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_info, null);
                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
                popupWindow.showAsDropDown(popupView, 0, 0);

                return true;
                    /*
                    case R.id.video_rename_item:
                        return true;
                    case R.id.video_delete_item:
                        return true;
                     */
            default:
                return false;
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.video_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return arrayListVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private RelativeLayout rlSelect;
        private TextView textView;
        private ImageButton imageButton;
        private TextView textViewVideoName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            rlSelect = itemView.findViewById(R.id.rl_select);
            if (displayMode.equals("list")) {
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
}
