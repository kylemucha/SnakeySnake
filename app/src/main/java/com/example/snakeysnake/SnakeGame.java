package com.example.snakeysnake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

    class SnakeGame extends SurfaceView implements Runnable {

        private Thread mThread = null;
        private long mNextFrameTime;
        private volatile boolean mPlaying = false;
        private volatile boolean mPaused = true;
        private SoundPool mSP;
        private int mEat_ID = -1;
        private int mCrashID = -1;
        private final int NUM_BLOCKS_WIDE = 40;
        private int mNumBlocksHigh;
        private int mScore;

        private Apple mApple;
        private Snake mSnake;

        private GameUI mGameUI;

        private boolean mGameStarted = false;

        public SnakeGame(Context context, Point size) {
            super(context);
            int blockSize = size.x / NUM_BLOCKS_WIDE;
            mNumBlocksHigh = size.y / blockSize;

            // Initialize the SoundPool
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                mSP = new SoundPool.Builder()
                        .setMaxStreams(5)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else {
                mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            }
            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;
                descriptor = assetManager.openFd("get_apple.ogg");
                mEat_ID = mSP.load(descriptor, 0);
                descriptor = assetManager.openFd("snake_death.ogg");
                mCrashID = mSP.load(descriptor, 0);
            } catch (IOException e) {
                // Error handling
            }

            SurfaceHolder surfaceHolder = getHolder();

            // Call the constructors of our two game objects
            mApple = new Apple(context,
                    new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                    blockSize);

            mSnake = new Snake(context,
                    new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                    blockSize);

            // Initialize the GameUI
            mGameUI = new GameUI(context, surfaceHolder, mScore, mSnake, mApple);
        }

        public void newGame() {
            mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
            mApple.spawn();
            mScore = 0;
            mNextFrameTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (mPlaying) {
                if (!mPaused) {
                    if (updateRequired()) {
                        update();
                    }
                }
                mGameUI.draw();
            }
        }

        public boolean updateRequired() {
            final long TARGET_FPS = 10;
            final long MILLIS_PER_SECOND = 1000;
            if (mNextFrameTime <= System.currentTimeMillis()) {
                mNextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
                return true;
            }
            return false;
        }

        public void update() {
            if (!mGameStarted || mPaused) {
                return;
            }
            mSnake.move();
            if (mSnake.checkDinner(mApple.getLocation())) {
                mApple.spawn();
                mScore++;
                mSP.play(mEat_ID, 100, 100, 0, 0, 1);
            }
            if (mSnake.detectDeath()) {
                mSP.play(mCrashID, 1, 1, 0, 0, 1);
                mPaused = true;
                mGameStarted = false;
                mGameUI.displayTapToPlayMessage();
            }

            mGameUI.setScore(mScore);
            mGameUI.setGameStarted(mGameStarted);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                float touchX = motionEvent.getX();
                float touchY = motionEvent.getY();

                // Check if the touch event is for the pause button
                if (touchX >= 1960 && touchX <= 2088 && touchY >= 808 && touchY <= 936) {
                    if (!mGameStarted) {
                        // Start the game if not started
                        mGameStarted = true;
                        mPaused = false;
                        newGame();
                    } else {
                        mPaused = !mPaused;     // Toggle the pause state if the game is already started
                    }
                    return true;
                }

                // Handle touch events for snake movement if the game is not paused
                if (!mPaused) {
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            mSnake.switchHeading(motionEvent);
                            break;
                        default:
                            break;
                    }
                }
            }
            return true;
        }

        public void pause() {
            mPlaying = false;
            mPaused = true;
            try {
                mThread.join();
            } catch (InterruptedException e) {
                // Error handling
            }
        }

        public void resume() {
            mPlaying = true;
            mPaused = false;
            mThread = new Thread(this);
            mThread.start();
        }
    }

