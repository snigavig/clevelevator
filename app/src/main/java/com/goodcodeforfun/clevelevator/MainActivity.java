package com.goodcodeforfun.clevelevator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDEST;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_NIGHTMARE;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_EASY_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_MEDIUM_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_HARD_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_HARDER_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.MotionDetectionService.ELEVATION_DETECTED_BROADCAST_ACTION;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferencesUtils mSharedPreferencesUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferencesUtils = SharedPreferencesUtils.getInstance(getApplicationContext());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        startService(new Intent(this, MotionDetectionService.class));
        sendBroadcast(new Intent(ELEVATION_DETECTED_BROADCAST_ACTION));
    }

    private void setCurrentProgress() {
        TextView currentDifficulty = (TextView) findViewById(R.id.currentLevelTextView);
        TextView nextDifficulty = (TextView) findViewById(R.id.nextLevelTextView);
        ProgressBar levelProgress = (ProgressBar) findViewById(R.id.levelProgressBar);

        currentDifficulty.setText(getString(mSharedPreferencesUtils.getCurrentDifficultyAsStringId()));
        nextDifficulty.setText(getString(mSharedPreferencesUtils.getNextDifficultyAsStringId()));

        int completedLevelsTasks = 0;
        switch (mSharedPreferencesUtils.getDifficulty()) {
            case DIFFICULTY_NIGHTMARE:
                levelProgress.setMax(mSharedPreferencesUtils.getCompletedTaskCount() + 1);
                completedLevelsTasks = LEVEL_HARDER_CORRECT_ANSWER_COUNT;
                break;
            case DIFFICULTY_HARDER:
                levelProgress.setMax(LEVEL_HARDER_CORRECT_ANSWER_COUNT);
                completedLevelsTasks = LEVEL_HARD_CORRECT_ANSWER_COUNT
                        + LEVEL_MEDIUM_CORRECT_ANSWER_COUNT
                        + LEVEL_EASY_CORRECT_ANSWER_COUNT;

                break;
            case DIFFICULTY_HARD:
                levelProgress.setMax(LEVEL_HARD_CORRECT_ANSWER_COUNT);
                completedLevelsTasks = LEVEL_MEDIUM_CORRECT_ANSWER_COUNT
                        + LEVEL_EASY_CORRECT_ANSWER_COUNT;
                break;
            case DIFFICULTY_MEDIUM:
                levelProgress.setMax(LEVEL_MEDIUM_CORRECT_ANSWER_COUNT);
                completedLevelsTasks = LEVEL_EASY_CORRECT_ANSWER_COUNT;
                break;
            case DIFFICULTY_EASY:
            default:
                levelProgress.setMax(LEVEL_EASY_CORRECT_ANSWER_COUNT);
                break;
        }

        levelProgress.setProgress(mSharedPreferencesUtils.getCompletedTaskCount() - completedLevelsTasks);
    }



    @Override
    protected void onResume() {
        setCurrentProgress();
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setCurrentProgress();
    }
}
