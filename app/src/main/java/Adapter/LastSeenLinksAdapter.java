package Adapter;

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
import com.example.avp.player.ExoPlayerActivity;

import java.util.ArrayList;

import Model.LastSeenLinkModel;

public class LastSeenLinksAdapter extends RecyclerView.Adapter<LastSeenLinksAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<LastSeenLinkModel> arrayListLinks;
    private Activity activity;

    public LastSeenLinksAdapter(Context context, @NonNull ArrayList<LastSeenLinkModel> arrayListLinks, Activity activity) {
        this.context = context;
        this.arrayListLinks = arrayListLinks;
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
        holder.textView.setText(arrayListLinks.get(position).getLink());

        holder.rlSelect.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ExoPlayerActivity.class);
                intent.putExtra("linkOnVideo", arrayListLinks.get(position).getLink());
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListLinks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlSelect;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlSelect = itemView.findViewById(R.id.rl_select);
            textView = itemView.findViewById(R.id.tv_link);
        }
    }
}
