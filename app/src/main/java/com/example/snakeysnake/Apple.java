package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Apple implements Drawable {

    // The location of the apple on the grid
    // Not in pixels
    private Point location = new Point();

    // The range of values we can choose from
    // to spawn an apple
    private Point mSpawnRange;
    private int mSize;

    // An image to represent the apple
    private Bitmap mBitmapApple;

    // Set up the apple in the constructor
    Apple(Context context, Point sr, int s) {

        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, s, s, false);
    }

    // This is called every time an apple is eaten
    void spawn(){
        // Choose two random values and place the apple
        Random random = new Random();
        do {
            location.x = random.nextInt(mSpawnRange.x) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
        } while(locationIsTooCloseToObstacle());
    }

    // Check if the apple's location is too close to any obstacle
    private boolean locationIsTooCloseToObstacle() {
        for (Point obstacleLoc : GameUI.mObstacle.getLocations()) {
            // Calculate the distance between the apple and the obstacle
            int distanceX = Math.abs(location.x - obstacleLoc.x);
            int distanceY = Math.abs(location.y - obstacleLoc.y);
            int distance = Math.max(distanceX, distanceY);

            // Check if the distance is less than a certain threshold (5 pixels)
            if (distance < 5) {
                return true;
            }
        }
        return false;
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getLocation(){
        return location;
    }

    // Draw the apple
    @Override
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple,
                location.x * mSize, location.y * mSize, paint);
    }
}