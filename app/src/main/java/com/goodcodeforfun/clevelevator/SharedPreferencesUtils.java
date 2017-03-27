package com.goodcodeforfun.clevelevator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDEST;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_NIGHTMARE;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.FORCED_DIFFICULTY_NONE;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_DEFAULT_CORRECT_ANSWER_COUNT;

/**
 * Created by dmytro.mina on 3/2/2017.
 */

public class SharedPreferencesUtils {
    public static final String DIFFICULTY = "DIFFICULTY_KEY";
    public static final String COMPLETED_TASK_COUNT = "COMPLETED_TASK_COUNT_KEY";
    private static final String FORCED_DIFFICULTY = "FORCED_DIFFICULTY_KEY";
    private static final String IS_SHOWING_TASK = "IS_SHOWING_TASK_KEY";
    private static final String IS_DETECTION_ON = "IS_DETECTION_ON_KEY";
    private static final String IS_SOUND_ON = "IS_SOUND_ON_KEY";
    private static final String IS_VIBRATION_ON = "IS_VIBRATION_ON_KEY";

    private static SharedPreferencesUtils mInstance;
    private final SharedPreferences mSharedPreferences;

    private SharedPreferencesUtils(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized SharedPreferencesUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesUtils(context);
        }
        return mInstance;
    }

    public synchronized int getDifficulty() {
        return mSharedPreferences.getInt(DIFFICULTY, DIFFICULTY_EASY);
    }

    public synchronized void setDifficulty(@EquationGenerationUtils.Difficulty int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(DIFFICULTY, value);
        editor.apply();
    }

    public synchronized void incrementDifficulty() {
        int difficulty = getDifficulty();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(DIFFICULTY, ++difficulty);
        editor.apply();
    }

    public synchronized int getNextDifficultyAsStringId() {
        int currentDifficulty = mSharedPreferences.getInt(DIFFICULTY, DIFFICULTY_EASY);
        int nextDifficulty;
        switch (currentDifficulty) {
            case DIFFICULTY_NIGHTMARE:
                nextDifficulty = DIFFICULTY_HARDEST;
                break;
            case DIFFICULTY_HARDER:
                nextDifficulty = DIFFICULTY_NIGHTMARE;
                break;
            case DIFFICULTY_HARD:
                nextDifficulty = DIFFICULTY_HARDER;
                break;
            case DIFFICULTY_MEDIUM:
                nextDifficulty = DIFFICULTY_HARD;
                break;
            case DIFFICULTY_EASY:
            default:
                nextDifficulty = DIFFICULTY_MEDIUM;
                break;
        }
        return getDifficultyAsStringId(nextDifficulty);
    }

    public synchronized int getCurrentDifficultyAsStringId() {
        return getDifficultyAsStringId(mSharedPreferences.getInt(DIFFICULTY, DIFFICULTY_EASY));
    }

    public synchronized int getDifficultyAsStringId(int difficulty) {
        int difficultyStringId;
        switch (difficulty) {
            case FORCED_DIFFICULTY_NONE:
                difficultyStringId = R.string.difficulty_none;
                break;
            case DIFFICULTY_HARDEST:
                difficultyStringId = R.string.difficulty_hardest;
                break;
            case DIFFICULTY_NIGHTMARE:
                difficultyStringId = R.string.difficulty_nightmare;
                break;
            case DIFFICULTY_HARDER:
                difficultyStringId = R.string.difficulty_harder;
                break;
            case DIFFICULTY_HARD:
                difficultyStringId = R.string.difficulty_hard;
                break;
            case DIFFICULTY_MEDIUM:
                difficultyStringId = R.string.difficulty_medium;
                break;
            case DIFFICULTY_EASY:
            default:
                difficultyStringId = R.string.difficulty_easy;
                break;
        }
        return difficultyStringId;
    }

    public synchronized int getForcedDifficulty() {
        return mSharedPreferences.getInt(FORCED_DIFFICULTY, FORCED_DIFFICULTY_NONE);
    }

    public synchronized void setForcedDifficulty(int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(FORCED_DIFFICULTY, value);
        editor.apply();
    }

    public synchronized int getCompletedTaskCount() {
        return mSharedPreferences.getInt(COMPLETED_TASK_COUNT, LEVEL_DEFAULT_CORRECT_ANSWER_COUNT);
    }

    public synchronized void setCompletedTaskCount(int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(COMPLETED_TASK_COUNT, value);
        editor.apply();
    }

    public synchronized boolean isShowingTask() {
        return mSharedPreferences.getBoolean(IS_SHOWING_TASK, false);
    }

    public synchronized void setIsShowingTask(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_SHOWING_TASK, value);
        editor.apply();
    }

    public synchronized boolean isDetectionOn() {
        return mSharedPreferences.getBoolean(IS_DETECTION_ON, true);
    }

    public synchronized void setIsDetectionOn(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_DETECTION_ON, value);
        editor.apply();
    }

    public synchronized boolean isSoundOn() {
        return mSharedPreferences.getBoolean(IS_SOUND_ON, true);
    }

    public synchronized void setIsSoundOn(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_SOUND_ON, value);
        editor.apply();
    }

    public synchronized boolean isVibrationOn() {
        return mSharedPreferences.getBoolean(IS_VIBRATION_ON, true);
    }

    public synchronized void setIsVibrationOn(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_VIBRATION_ON, value);
        editor.apply();
    }
}

