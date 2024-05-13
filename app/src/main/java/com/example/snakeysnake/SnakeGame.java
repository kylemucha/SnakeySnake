package com.example.snakeysnake;

import android.content.Context;
import android.graphics.Point;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.SharedPreferences;
import java.util.Arrays;
import java.util.Collections;
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
    private int mScore = 0;
    private Apple mApple;
    private goldBasketball mGoldBasketball;
    private redBasketball mRedBasketball;
    private Snake mSnake;
    private GameUI mGameUI;
    private GameAudio mGameAudio;
    private boolean mGameStarted = false;
    private Obstacle mObstacle;
    private static final int NUM_OBSTACLES = 5;
    private WaterBottle mWaterBottle;
    private long waterBottleCooldownStartTime = 0;
    private long waterBottleCooldownPeriod = 4000; // Cooldown period (4 seconds)
    private boolean isWaterBottleOnScreen = false;
    private static final String PREFS = "prefs";
    private static final String SCORES = "scores";
    private static Integer[] topScores = new Integer[10];

    public SnakeGame(Context context, Point size) {
        super(context);
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

        initializeAudio(context);       // Initialize audio
        initializeGameObjects(context, blockSize);
        mGameUI = new GameUI(context,
                getHolder(),
                mScore,
                mSnake,
                mApple,
                mObstacle,
                mGoldBasketball,
                mRedBasketball,
                mWaterBottle);
        loadScores();
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

        mWaterBottle = new WaterBottle(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);
    }

    private void loadScores() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String scoresStr = prefs.getString(SCORES, "");
        if (!scoresStr.isEmpty()) {
            String[] scoreStrings = scoresStr.split(",");
            for (int i = 0; i < topScores.length; i++) {
                topScores[i] = Integer.parseInt(scoreStrings[i]);
            }
        } else {
            Arrays.fill(topScores, 0);
        }
    }
    private void saveScores() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String scoresStr = Arrays.toString(topScores).replaceAll("[\\[\\] ]", "");
        editor.putString(SCORES, scoresStr);
        editor.apply();
    }

    private void updateScores(int newScore) {
        Arrays.sort(topScores, Collections.reverseOrder());
        for (int i = 0; i < topScores.length; i++) {
            if (newScore > topScores[i]) {
                System.arraycopy(topScores, i, topScores, i + 1, topScores.length - 1 - i);
                topScores[i] = newScore;
                break;
            }
        }
        saveScores();
    }

    public void newGame() {
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.spawn();
        mGoldBasketball.spawn();
        mRedBasketball.spawn();
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
        mObstacle.generateRandomLocations(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), NUM_OBSTACLES);   // Generate random obstacle locations
        mWaterBottle.spawn();
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
    public static int getHighScore() {
        return topScores[0]; // Assuming topScores is already sorted in descending order
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

            isWaterBottleOnScreen = false;
            // Reset the water bottle cooldown timer
            waterBottleCooldownStartTime = System.currentTimeMillis();
        }

        // Check if enough time has passed since the last water bottle was eaten
        else if (System.currentTimeMillis() - waterBottleCooldownStartTime >= waterBottleCooldownPeriod) {
            // Spawn the water bottle
            mWaterBottle.spawn();
            isWaterBottleOnScreen = true;

            // Reset the water bottle cooldown timer
            waterBottleCooldownStartTime = System.currentTimeMillis();
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
            updateScores(mScore);  // Update the scores when the game ends
        }

        if (mSnake.checkDinner(mGoldBasketball.getLocation())) {
            mGoldBasketball.spawn();
            mScore = mScore + 3;
            mGameAudio.playSwishSound();    // Swish
        }

        if (mSnake.checkDinner(mWaterBottle.getLocation())) {
            mWaterBottle.spawn();
            mScore++;

            // Reset snake body length
            mSnake.resetBodyLength();
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