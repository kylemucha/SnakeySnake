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
    protected static Snake mSnake;
    private static Apple mApple;

    private static goldBasketball mGoldBasketball;
    private static redBasketball mRedBasketball;
    protected static Obstacle mObstacle;
    private static WaterBottle mWaterBottle;

    private static Bitmap mBitmapKingscourt;
    private static Bitmap mBitmapPause;
    private boolean mGameStarted = false;

    public GameUI(Context context, SurfaceHolder surfaceHolder, int score, Snake snake, Apple apple, Obstacle obstacle, goldBasketball goldbasketball, redBasketball redbasketball, WaterBottle waterBottle) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;
        mPaint = new Paint();
        mScore = score;
        mSnake = snake;
        mApple = apple;
        mObstacle = obstacle;
        mGoldBasketball = goldbasketball;
        mRedBasketball = redbasketball;
        mWaterBottle = waterBottle;
    }

    public void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            displayBackground();

            if (!mGameStarted) {
                displayTapToPlayMessage();
                displayAuthorNames();
                displayHighScore(mCanvas, mPaint, mScore);
            } else {
                displayScore(mCanvas, mPaint, mScore);
                //displayHighScore(mCanvas, mPaint);
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

        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mCanvas.drawText("Kyle Jacob Mucha", 1603, 125, mPaint);
        mCanvas.drawText("Jalen Grant Hall", 1663, 190, mPaint);
        mCanvas.drawText("Galileo Alejandro Perez", 1484, 249, mPaint);
    }

    public void displayTapToPlayMessage() {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(75);
            Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            mPaint.setTypeface(typeface);

            mCanvas.drawText("Tap the Play Button!", 170, 930, mPaint);
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mCanvas.drawText("Tap the Play Button!", 164, 925, mPaint);
        }
    }

    public static void displayScore(Canvas canvas, Paint paint, int score) {
        synchronized (mCanvas) {
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(50);
            mCanvas.drawText("SCORE: " + score, 24, 64, mPaint);
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mCanvas.drawText("SCORE: " + score, 20, 60, mPaint);
        }
    }

    public void displayHighScore(Canvas canvas, Paint paint, int score) {
        synchronized (mCanvas) {
            int highScore; // Directly access the static method
            highScore = SnakeGame.getHighScore();
            paint.setTextSize(50);
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            canvas.drawText("HI SCORE: " + highScore, 24, 64, mPaint);
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawText("HI SCORE: " + highScore, 20, 60, mPaint);
        }
    }

    public void showLeaderboard(Integer[] topScores) {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            displayBackground();
            mPaint.setColor(Color.argb(255, 168, 39, 245));
            mPaint.setTextSize(60);
            int centerX = mCanvas.getWidth() / 2;
            int startY = 150;
            // Draw leaderboard to the left
            int leaderboardX = centerX - 300; // Adjusted left from the center

            mCanvas.drawText("LEADERBOARD", leaderboardX, 100, mPaint);
            String[] ordinals = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th"};
            for (int i = 0; i < topScores.length; i++) {
                String text = String.format("%s: %d", ordinals[i], topScores[i]);
                mCanvas.drawText(text, mCanvas.getWidth() / 2 - 150, startY, mPaint);
                startY += 50; // Increase Y coordinate to avoid overlapping text
            }
            // Setup and display the final score to the right
            mPaint.setTextSize(70);
            String finalScoreText = "Final Score: " + mScore;
            int finalScoreX = centerX + 100; // Adjusted right from the center
            mCanvas.drawText(finalScoreText, finalScoreX, 150, mPaint); // Display final score at the same vertical start as leaderboard

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private static void displayGameObjects() {
        mApple.draw(mCanvas, mPaint);
        mGoldBasketball.draw(mCanvas, mPaint);
        mRedBasketball.draw(mCanvas, mPaint);
        mSnake.draw(mCanvas, mPaint);
        mObstacle.draw(mCanvas, mPaint); // Draw obstacles
        mWaterBottle.draw(mCanvas, mPaint);
    }
}