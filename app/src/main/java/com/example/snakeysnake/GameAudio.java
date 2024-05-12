package com.example.snakeysnake;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

class GameAudio {
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;
    private int mSwishID = -1;

    public GameAudio(Context context) {
        buildAudio(context);
    }

    private void buildAudio(Context context) {
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

        // Load the sound files into SoundPool
        mEat_ID = mSP.load(context.getResources().openRawResourceFd(R.raw.whistle), 0);
        if (mEat_ID != 0) {
            Log.d("GameAudio", "Loaded whistle.ogg, sound ID: " + mEat_ID);
        } else {
            Log.e("GameAudio", "Failed to load whistle.ogg");
        }

        mCrashID = mSP.load(context.getResources().openRawResourceFd(R.raw.buzzer), 0);
        if (mCrashID != 0) {
            Log.d("GameAudio", "Loaded buzzer.ogg, sound ID: " + mCrashID);
        } else {
            Log.e("GameAudio", "Failed to load buzzer.ogg");
        }

        mSwishID = mSP.load(context.getResources().openRawResourceFd(R.raw.swish), 0);
        if (mSwishID != 0) {
            Log.d("GameAudio", "Loaded swish.ogg, sound ID: " + mSwishID);
        } else {
            Log.e("GameAudio", "Failed to load swish.ogg");
        }
    }

    public SoundPool getSoundPool() {
        return mSP;
    }

    public int getEatSoundId() {
        return mEat_ID;
    }

    public int getCrashSoundId() {
        return mCrashID;
    }

    public int getSwishSoundId() {
        return mSwishID; }
}
