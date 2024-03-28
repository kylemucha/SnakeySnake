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
    private static Bitmap mBitmapKingscourt;
    private static Bitmap mBitmapPause;
    private boolean mGameStarted = false;

    public GameUI(Context context, SurfaceHolder surfaceHolder, int score, Snake snake, Apple apple) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;
        mPaint = new Paint();
        mScore = score;
        mSnake = snake;
        mApple = apple;
    }

    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            displayBackground();
            displayAuthorNames();

            if (!mGameStarted) {
                displayTapToPlayMessage();
            } else {
                displayScore(mCanvas, mPaint, mScore);
                displayGameObjects();
                if(mPaused) {
                    displayContinueMessage();
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
        mCanvas.drawText("Kyle Jacob Mucha", 1600, 130, mPaint);
        mCanvas.drawText("Jalen Grant Hall", 1630, 190, mPaint);
    }

    public void displayTapToPlayMessage() {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(120);
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            mPaint.setTypeface(typeface);

            mCanvas.drawText("Tap To Play!", 200, 820, mPaint);
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
    }

    public static void displayContinueMessage() {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 0, 0, 0));
            mPaint.setTextSize(150);
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            mPaint.setTypeface(typeface);
            mCanvas.drawText("Press Pause to Resume!", 150, 700, mPaint); //
        }
    }
}