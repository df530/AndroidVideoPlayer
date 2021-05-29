package com.example.avp.player;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;


public class HeadsetButtonReceiver extends BroadcastReceiver {
    private static int clickCount = 0;
    private Context context;
    private static onHeadsetListener headsetListener;

    public HeadsetButtonReceiver() {
        super();
    }

    public HeadsetButtonReceiver(Context context){
        super();
        this.context = context;
        headsetListener = null;
        registerHeadsetReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY) {
                clickCount = clickCount + 1;
                if(clickCount == 2) {
                    handler.sendEmptyMessage(1);
                    clickCount = 0;
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.what == 1) {
                    headsetListener.playOrPause();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    interface onHeadsetListener{
        void playOrPause();
    }

    public void setOnHeadsetListener(onHeadsetListener newHeadsetListener){
        headsetListener = newHeadsetListener;
    }

    public void registerHeadsetReceiver() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(), HeadsetButtonReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(name);
    }

    public void unregisterHeadsetReceiver(){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(), HeadsetButtonReceiver.class.getName());
        audioManager.unregisterMediaButtonEventReceiver(name);
    }
}