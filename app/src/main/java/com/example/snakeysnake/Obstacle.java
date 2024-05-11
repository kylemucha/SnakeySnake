package com.example.snakeysnake;

import static com.example.snakeysnake.R.drawable.cone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

class Obstacle implements Drawable {
    private Bitmap mBitmapObstacle;
    private Point[] locations;
    private int mSize;

    // Constructor
    public Obstacle(Context context, Point[] locs, int size) {
        this.mSize = size;
        this.locations = locs;
        mBitmapObstacle = BitmapFactory.decodeResource(context.getResources(), cone);
        mBitmapObstacle = Bitmap.createScaledBitmap(mBitmapObstacle, size, size, false);
    }

    public void generateRandomLocations(Point gridSize, int numObstacles) {
        Random random = new Random();
        locations = new Point[numObstacles]; // Use the specified number of obstacles
        for (int i = 0; i < numObstacles; i++) {
            int x = random.nextInt(gridSize.x);
            int y = random.nextInt(gridSize.y);
            locations[i] = new Point(x, y);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        for (Point loc : locations) {
            canvas.drawBitmap(mBitmapObstacle, loc.x * mSize, loc.y * mSize, paint);
        }
    }

    public Point[] getLocations() {
        return locations;
    }
}
