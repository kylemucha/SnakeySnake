package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Point;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceView;
class SnakeGame extends SurfaceView implements Runnable {

    private Thread mThread = null;
    private long mNextFrameTime;
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;
    private int mSwishID = -1;
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mScore;
    private Apple mApple;
    private goldBasketball mGoldBasketball;
    private redBasketball mRedBasketball;
    private Snake mSnake;
    private GameUI mGameUI;
    private GameAudio mGameAudio;
    private boolean mGameStarted = false;
    private Obstacle mObstacle;
    private static final int NUM_OBSTACLES = 5;

    public SnakeGame(Context context, Point size) {
        super(context);
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

        initializeAudio(context);       // Initialize audio
        initializeGameObjects(context, blockSize);
        mGameUI = new GameUI(context, getHolder(), mScore, mSnake, mApple, mObstacle, mGoldBasketball, mRedBasketball);
    }

    private void initializeAudio(Context context) {
        mGameAudio = new GameAudio(context);
        mSP = mGameAudio.getSoundPool();
        mEat_ID = mGameAudio.getEatSoundId();
        mCrashID = mGameAudio.getCrashSoundId();
        mSwishID = mGameAudio.getSwishSoundId();
    }

    private void initializeGameObjects(Context context, int blockSize) {
        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

        mGoldBasketball = new goldBasketball(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

        mRedBasketball = new redBasketball(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

        mObstacle = new Obstacle(context,
                new Point[]{new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh)},
                blockSize);
    }

    public void newGame() {
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.spawn();
        mGoldBasketball.spawn();
        mRedBasketball.spawn();
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
        mObstacle.generateRandomLocations(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), NUM_OBSTACLES);   // Generate random obstacle locations
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
        }
        if (mSnake.checkDinner(mRedBasketball.getLocation())) {
            mRedBasketball.spawn();
            mScore = mScore - 1;
            mGameAudio.playEatSound();   // Whistle
        }
        if (mSnake.detectDeath() || checkCollisionWithObstacle()) {
            mPaused = true;
            mGameStarted = false;
            mGameUI.displayTapToPlayMessage();
            mGameAudio.playCrashSound();  // Buzzer
        }
        if (mSnake.checkDinner(mGoldBasketball.getLocation())) {
            mGoldBasketball.spawn();
            mScore = mScore + 3;
            mGameAudio.playSwishSound();    // Swish
        }

        mGameUI.setScore(mScore);
        mGameUI.setGameStarted(mGameStarted);
    }

    private boolean checkCollisionWithObstacle() {
        Point head = mSnake.getSegmentLocations().get(0);
        for (Point loc : mObstacle.getLocations()) {
            if (head.equals(loc)) {
                return true;
            }
        }
        return false;
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