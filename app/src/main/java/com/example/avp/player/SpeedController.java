package com.example.avp.player;

import android.widget.TextView;

import lombok.Getter;

public abstract class SpeedController {
    @Getter
    protected final float MIN_SPEED;
    @Getter
    protected final float MAX_SPEED;
    @Getter
    protected final float NORMAL_SPEED;
    @Getter
    protected final float INCREASED_SPEED;
    @Getter
    protected float curSpeed;

    protected float REGULAR_SPEED = 1f;

    public SpeedController(float MIN_SPEED, float MAX_SPEED, float NORMAL_SPEED, float INCREASED_SPEED, float curSpeed) throws IllegalArgumentException {
        if (MIN_SPEED > MAX_SPEED)
            throw new IllegalArgumentException("Min speed more than max speed");
        if (NORMAL_SPEED < MIN_SPEED || NORMAL_SPEED > MAX_SPEED)
            throw new IllegalArgumentException("Normal speed out of range");
        if (INCREASED_SPEED < MIN_SPEED || INCREASED_SPEED > MAX_SPEED)
            throw new IllegalArgumentException("Increased speed out of range");
        if (NORMAL_SPEED > INCREASED_SPEED)
            throw new IllegalArgumentException("Normal speed more than increased speed");
        if (curSpeed < MIN_SPEED || curSpeed > MAX_SPEED)
            throw new IllegalArgumentException("Current speed out of allowed range");

        this.MIN_SPEED = MIN_SPEED;
        this.MAX_SPEED = MAX_SPEED;
        this.NORMAL_SPEED = NORMAL_SPEED;
        this.INCREASED_SPEED = INCREASED_SPEED;
        this.curSpeed = curSpeed;
    }

    public abstract void setCurSpeed(float speedValue) throws IllegalArgumentException;

    public void setCurSpeedMin() {
        setCurSpeed(MIN_SPEED);
    }

    public void setCurSpeedMax() {
        setCurSpeed(MAX_SPEED);
    }

    public void setCurSpeedNormal() {
        setCurSpeed(NORMAL_SPEED);
    }

    public void setCurSpeedIncreased() {
        setCurSpeed(INCREASED_SPEED);
    }
}
