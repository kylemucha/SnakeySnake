package com.example.snakeysnake;

import android.graphics.Point;
abstract class GameObject implements Movable, Drawable {
    private Point location;
    private int size;

    public GameObject() {
        this.location = location;
        this.size = size;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isOutOfBounds(Point bounds) {
        return location.x < 0 || location.x >= bounds.x || location.y < 0 || location.y >= bounds.y;
    }

}