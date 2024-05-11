package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

public class GameUI {

    private static Paint mPaint;
    private static Canvas mCanvas;
    private static Context mContext;
    private static SurfaceHolder mSurfaceHolder;
    private static int mScore;
    private static boolean mPaused;
    private static Snake mSnake;
    private static Apple mApple;
    private static Obstacle mObstacle;

    private static Bitmap mBitmapKingscourt;
    private static Bitmap mBitmapPause;
    private boolean mGameStarted = false;

    public GameUI(Context context, SurfaceHolder surfaceHolder, int score, Snake snake, Apple apple, Obstacle obstacle) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;
        mPaint = new Paint();
        mScore = score;
        mSnake = snake;
        mApple = apple;
        mObstacle = obstacle; // Passed from the game engine initialization
    }

    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            displayBackground();

            if (!mGameStarted) {
                displayTapToPlayMessage();
                displayAuthorNames();
            } else {
                displayScore(mCanvas, mPaint, mScore);
                displayGameObjects();
                if(mPaused) {

                }
                else {

                }
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public void setGameStarted(boolean gameStarted) {
        mGameStarted = gameStarted;
    }

    public boolean isGameStarted() {
        return mGameStarted;
    }
    public void setScore(int score) {
        this.mScore = score;
    }

    private static void displayBackground() {
        mBitmapKingscourt = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.kingscourt);
        mBitmapKingscourt = Bitmap.createScaledBitmap(mBitmapKingscourt, 2250, 1015, false);
        mCanvas.drawBitmap(mBitmapKingscourt, 0, 0, mPaint);

        mBitmapPause = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pause);
        mBitmapPause = Bitmap.createScaledBitmap(mBitmapPause, 128, 128, false);
        mCanvas.drawBitmap(mBitmapPause, 1960, 808, mPaint);
    }

    public static void displayAuthorNames() {
        mPaint.setColor(Color.argb(255, 168, 39, 182));
        mPaint.setTextSize(60);
        mCanvas.drawText("Kyle Jacob Mucha", 1610, 130, mPaint);
        mCanvas.drawText("Jalen Grant Hall", 1670, 195, mPaint);
        mCanvas.drawText("Galileo Alejandro Perez", 1490, 255, mPaint);

    }

    public void displayTapToPlayMessage() {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(75);
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            mPaint.setTypeface(typeface);

            mCanvas.drawText("Tap the Play Button!", 170, 930, mPaint);
        }
    }

    public static void displayScore(Canvas canvas, Paint paint, int score) {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(120);
            mCanvas.drawText("" + score, 20, 120, mPaint);
        }
    }

    private static void displayGameObjects() {
        mApple.draw(mCanvas, mPaint);
        mSnake.draw(mCanvas, mPaint);
        mObstacle.draw(mCanvas, mPaint); // Draw obstacles
    }
}