package com.goodcodeforfun.clevelevator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;

import com.goodcodeforfun.clevelevator.EquationGenerationUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.R.attr.key;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDEST;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_NIGHTMARE;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_DEFAULT_CORRECT_ANSWER_COUNT;

/**
 * Created by dmytro.mina on 3/2/2017.
 */

public class SharedPreferencesUtils {
    public static final String DIFFICULTY = "DIFFICULTY_KEY";
    public static final String COMPLETED_TASK_COUNT = "COMPLETED_TASK_COUNT_KEY";

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

    //suppressing because we cannot guarantee that getInt returns value in range
    @SuppressWarnings("WrongConstant")
    public synchronized @EquationGenerationUtils.Difficulty
    int getDifficulty() {
        return mSharedPreferences.getInt(DIFFICULTY, DIFFICULTY_EASY);
    }

    //difficulty hardest is not in int def as it is technical, for the sake of localization strings only
    @SuppressWarnings("WrongConstant")
    public int getNextDifficultyAsStringId() {
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

    //suppressing because we cannot guarantee that getInt returns value in range
    @SuppressWarnings("WrongConstant")
    public int getCurrentDifficultyAsStringId() {
        return getDifficultyAsStringId(mSharedPreferences.getInt(DIFFICULTY, DIFFICULTY_EASY));
    }

    //difficulty hardest is not in int def as it is technical, for the sake of localization strings only
    @SuppressLint("SwitchIntDef")
    public int getDifficultyAsStringId(@EquationGenerationUtils.Difficulty int difficulty) {
        int difficultyStringId;
        switch (difficulty) {
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


    public synchronized void setDifficulty(@EquationGenerationUtils.Difficulty int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(DIFFICULTY, value);
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
}

