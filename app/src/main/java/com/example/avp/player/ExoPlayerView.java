package com.example.avp.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.view.GestureDetectorCompat;

import com.example.avp.R;

import org.jetbrains.annotations.NotNull;

import lombok.Setter;

public class ExoPlayerView extends com.github.vkay94.dtpv.DoubleTapPlayerView {
    @Setter
    private SpeedController speedController;

    {
        setControllerAutoShow(false);
    }

    public ExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExoPlayerView(Context context) {
        super(context);
    }

    private final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(
            this.getContext(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (speedController.getCurSpeed() > speedController.getNORMAL_SPEED())
                        speedController.setCurSpeedNormal();
                    else
                        speedController.setCurSpeedIncreased();
                    return true;
                }
            });

    //@SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NotNull MotionEvent ev) {
        boolean touchHandled = super.onTouchEvent(ev);
        /* I read the source code of YouTubeOverlay class and find the line,
         * where this guy check X coordinate. If X coordinate is in left 0.35 part of
         * screen (X < 0.35 * widht), then (as I understand it) he make rewind increment (-10sec).
         * Similarly with forward increment. That is why I check X coordinate (not to make both
         * increment of time and change of speed)
         */
        if (ev.getX() >= this.getWidth() * 0.35 && ev.getX() <= this.getWidth() * 0.65) {
            touchHandled |= gestureDetector.onTouchEvent(ev);
        }
        return touchHandled;
    }

    @Override
    public boolean performClick() {
        LinearLayout sll = findViewById(R.id.speed_linear_layout);
        if (sll.getVisibility() == View.VISIBLE) {
            sll.setVisibility(View.INVISIBLE);
        }
        return super.performClick();
    }
}