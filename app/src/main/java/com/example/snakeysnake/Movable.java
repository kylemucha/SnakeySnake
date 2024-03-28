package com.example.snakeysnake;

import android.view.MotionEvent;

public interface Movable  {

    void move();
    void switchHeading(MotionEvent motionEvent);
}

