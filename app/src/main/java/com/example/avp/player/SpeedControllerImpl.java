package com.example.avp.player;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.bosphere.verticalslider.VerticalSlider;
import com.example.avp.R;
import com.google.android.exoplayer2.PlaybackParameters;

import java.text.DecimalFormat;

import lombok.Getter;

import static com.example.avp.player.ExoPlayerActivity.player;

public class SpeedControllerImpl extends SpeedController {
    private final Activity playerActivity;
    private final VerticalSlider speedVS;
    public SpeedControllerImpl(float MIN_SPEED, float MAX_SPEED, float NORMAL_SPEED, float INCREASED_SPEED, float curSpeed, Activity playerActivity) {
        super(MIN_SPEED, MAX_SPEED, NORMAL_SPEED, INCREASED_SPEED, curSpeed);
        this.playerActivity = playerActivity;
        this.speedVS = playerActivity.findViewById(R.id.speed_vertical_slide);
        speedVS.setOnSliderProgressChangeListener(new VerticalSliderProgressChangeListener());

        setSpeedValueInTextView(R.id.min_speed_value, MIN_SPEED);
        setSpeedValueInTextView(R.id.max_speed_value, MAX_SPEED);
        setCurSpeed(curSpeed);
    }

    private class VerticalSliderProgressChangeListener implements VerticalSlider.OnProgressChangeListener {
        @Override
        public void onProgress(float progress) {
            setCurSpeed(getSpeedFromSliderProgress(progress));
        }
    }

    private void setSpeedValueInTextView(TextView tv, float value) {
        tv.setText((new DecimalFormat("#.##x").format(value)));
    }

    private void setSpeedValueInTextView(int textViewId, float value) {
        setSpeedValueInTextView(playerActivity.findViewById(textViewId), value);
    }

    private float getSliderProgressFromSpeed(float speedValue) {
        if (speedValue < MIN_SPEED || speedValue > MAX_SPEED)
            throw new IllegalArgumentException("New speed value doesn't include in the allowed range");
        if (speedValue <= REGULAR_SPEED)
            return (speedValue - MIN_SPEED) / (REGULAR_SPEED - MIN_SPEED) * 0.5f;
        else
            return 0.5f + (speedValue - REGULAR_SPEED) / (MAX_SPEED - REGULAR_SPEED) * 0.5f;

    }

    private float getSpeedFromSliderProgress(float progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("progress is out of range [0, 1]");
        if (progress <= 0.5f)
            return MIN_SPEED + progress / 0.5f * (REGULAR_SPEED - MIN_SPEED);
        else
            return 1f + (progress - 0.5f) / 0.5f * (MAX_SPEED - 1);
    }

    @Override
    public void setCurSpeed(float speedValue) throws IllegalArgumentException {
        if (speedValue < MIN_SPEED || speedValue > MAX_SPEED)
            throw new IllegalArgumentException("New speed value doesn't include in the allowed range");
        player.setPlaybackParameters(new PlaybackParameters(speedValue));
        speedVS.setProgress(getSliderProgressFromSpeed(speedValue));
        curSpeed = speedValue;
        setSpeedValueInTextView(R.id.speed_value, speedValue);
    }
}
