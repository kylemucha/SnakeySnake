package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class goldBasketball implements Drawable {

    // The location of the ball on the grid
    // Not in pixels
    private Point location = new Point();

    // The range of values we can choose from
    // to spawn a ball
    private Point mSpawnRange;
    private int mSize;

    // An image to represent the ball
    private Bitmap mBitmapGoldBasketball;

    // Set up the ball in the constructor
    goldBasketball(Context context, Point sr, int s) {

        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an ball
        mSize = s;
        // Hide the ball off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmapGoldBasketball = BitmapFactory.decodeResource(context.getResources(), R.drawable.gold_basketball);

        // Resize the bitmap
        mBitmapGoldBasketball = Bitmap.createScaledBitmap(mBitmapGoldBasketball, s, s, false);
    }

    // This is called every time a ball is eaten
    void spawn(){
        // Choose two random values and place the ball
        Random random = new Random();
        do {
            location.x = random.nextInt(mSpawnRange.x) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
        } while(locationIsTooCloseToObstacle());
    }

    // Check if the ball's location is too close to any obstacle
    private boolean locationIsTooCloseToObstacle() {
        for (Point obstacleLoc : GameUI.mObstacle.getLocations()) {
            // Calculate the distance between the ball and the obstacle
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

    // Let SnakeGame know where the ball is
    // SnakeGame can share this with the snake
    Point getLocation(){
        return location;
    }

    // Draw the ball
    @Override
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapGoldBasketball,
                location.x * mSize, location.y * mSize, paint);
    }
}