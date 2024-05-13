package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

public class WaterBottle implements PowerUp, Drawable {

    private Point location = new Point();
    private Point mSpawnRange;
    private int mSize;
    private Bitmap mBitmapWaterBottle;

    WaterBottle(Context context, Point sr, int s) {

        mSpawnRange = sr;
        mSize = s;
        location.x = -10;   // Hide the water bottle off-screen until the game starts
        mBitmapWaterBottle = BitmapFactory.decodeResource(context.getResources(), R.drawable.wbottle);
        mBitmapWaterBottle = Bitmap.createScaledBitmap(mBitmapWaterBottle, 80, 80, false);
    }

    public void spawn() {
            Random random = new Random();
            location.x = random.nextInt(mSpawnRange.x - 1) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapWaterBottle,
                location.x * mSize, location.y * mSize, paint);
    }

    @Override
    public Point getLocation() {
        return location;
    }
}