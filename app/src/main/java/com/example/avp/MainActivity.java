package com.example.avp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.avp.player.ExoPlayerActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        //Intent intent = new Intent(this, VideoViewActivity.class);
        Intent intent = new Intent(this, ExoPlayerActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String linkOnVideo = editText.getText().toString();

        intent.putExtra("linkOnVideo", linkOnVideo);
        startActivity(intent);
    }
}