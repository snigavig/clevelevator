package com.goodcodeforfun.clevelevator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_NIGHTMARE;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.FORCED_DIFFICULTY_NONE;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_EASY_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_HARDER_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_HARD_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.GameLogicService.LEVEL_MEDIUM_CORRECT_ANSWER_COUNT;
import static com.goodcodeforfun.clevelevator.SharedPreferencesUtils.COMPLETED_TASK_COUNT;
import static com.goodcodeforfun.clevelevator.SharedPreferencesUtils.DIFFICULTY;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferencesUtils mSharedPreferencesUtils;

    private TextView currentDifficulty;
    private TextView nextDifficulty;
    private TextView levelProgressText;
    private ProgressBar levelProgress;
    private Spinner difficultySpinner;
    private Switch detectionSwitch;
    private Switch soundSwitch;
    private Switch vibrationSwitch;
    private LinearLayout currentDifficultyContainer;
    private LinearLayout currentDifficultyNoProgressContainer;
    private LinearLayout forceDifficultyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentDifficulty = (TextView) findViewById(R.id.currentLevelTextView);
        nextDifficulty = (TextView) findViewById(R.id.nextLevelTextView);
        levelProgressText = (TextView) findViewById(R.id.levelProgressTextView);
        levelProgress = (ProgressBar) findViewById(R.id.levelProgressBar);
        difficultySpinner = (Spinner) findViewById(R.id.forceDifficultySpinner);
        detectionSwitch = (Switch) findViewById(R.id.detectionSwitch);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        vibrationSwitch = (Switch) findViewById(R.id.vibrationSwitch);
        currentDifficultyContainer = (LinearLayout) findViewById(R.id.currentDifficultyContainer);
        currentDifficultyNoProgressContainer = (LinearLayout) findViewById(R.id.currentDifficultyNoProgressContainer);
        forceDifficultyContainer = (LinearLayout) findViewById(R.id.forceDifficultyContainer);
        mSharedPreferencesUtils = SharedPreferencesUtils.getInstance(getApplicationContext());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        if (mSharedPreferencesUtils.isDetectionOn()) {
            MotionDetectionService.startMotionDetection(this);
        }
    }

    private void setDifficultySpinner() {
        CharSequence[] difficultyArray = getResources().getStringArray(R.array.difficulty);
        List<CharSequence> difficultyList = Arrays.asList(difficultyArray);
        List<CharSequence> mutableDifficultyList = new ArrayList<>(difficultyList);
        int difficulty = mSharedPreferencesUtils.getDifficulty();
        if (difficulty == DIFFICULTY_EASY) {
            forceDifficultyContainer.setVisibility(View.GONE);
            return;
        } else {
            if (forceDifficultyContainer.getVisibility() == View.GONE) {
                forceDifficultyContainer.setVisibility(View.VISIBLE);
            }
        }
        switch (difficulty) {
            case DIFFICULTY_MEDIUM:
                mutableDifficultyList.remove(getString(R.string.difficulty_nightmare));
                mutableDifficultyList.remove(getString(R.string.difficulty_harder));
                mutableDifficultyList.remove(getString(R.string.difficulty_hard));
                mutableDifficultyList.remove(getString(R.string.difficulty_medium));
                break;
            case DIFFICULTY_HARD:
                mutableDifficultyList.remove(getString(R.string.difficulty_nightmare));
                mutableDifficultyList.remove(getString(R.string.difficulty_harder));
                mutableDifficultyList.remove(getString(R.string.difficulty_hard));
                break;
            case DIFFICULTY_HARDER:
                mutableDifficultyList.remove(getString(R.string.difficulty_nightmare));
                mutableDifficultyList.remove(getString(R.string.difficulty_harder));
                break;
            case DIFFICULTY_NIGHTMARE:
                mutableDifficultyList.remove(getString(R.string.difficulty_nightmare));
            default:
                break;
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mutableDifficultyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
        difficultySpinner.setSelection(adapter.getPosition(getString(mSharedPreferencesUtils.getDifficultyAsStringId(mSharedPreferencesUtils.getForcedDifficulty()))));
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != mSharedPreferencesUtils.getForcedDifficulty()) {

                    switch (position) {
                        case 0:
                            mSharedPreferencesUtils.setForcedDifficulty(FORCED_DIFFICULTY_NONE);
                            break;
                        case 1:
                            mSharedPreferencesUtils.setForcedDifficulty(DIFFICULTY_EASY);
                            break;
                        case 2:
                            mSharedPreferencesUtils.setForcedDifficulty(DIFFICULTY_MEDIUM);
                            break;
                        case 3:
                            mSharedPreferencesUtils.setForcedDifficulty(DIFFICULTY_HARD);
                            break;
                        case 4:
                            mSharedPreferencesUtils.setForcedDifficulty(DIFFICULTY_HARDER);
                            break;
                        case 5:
                            mSharedPreferencesUtils.setForcedDifficulty(DIFFICULTY_NIGHTMARE);
                            break;
                    }
                    if (SharedPreferencesUtils.getInstance(MainActivity.this).isShowingTask()
                            && mSharedPreferencesUtils.getForcedDifficulty() != mSharedPreferencesUtils.getDifficulty()) {
                        NotificationService.startActionShowEquation(getApplicationContext(), null);
                    }
                    setupUI(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing selected - nothing to do
            }

        });
    }

    private void setCurrentProgress() {
        currentDifficulty.setText(getString(mSharedPreferencesUtils.getCurrentDifficultyAsStringId()));
        nextDifficulty.setText(getString(mSharedPreferencesUtils.getNextDifficultyAsStringId()));

        int completedLevelsTasks = 0;
        int currentLevelMax;
        switch (mSharedPreferencesUtils.getDifficulty()) {
            case DIFFICULTY_NIGHTMARE:
                completedLevelsTasks = LEVEL_HARDER_CORRECT_ANSWER_COUNT
                        + LEVEL_HARD_CORRECT_ANSWER_COUNT
                        + LEVEL_MEDIUM_CORRECT_ANSWER_COUNT
                        + LEVEL_EASY_CORRECT_ANSWER_COUNT;
                currentLevelMax = completedLevelsTasks + 1;
                break;
            case DIFFICULTY_HARDER:
                completedLevelsTasks = LEVEL_HARD_CORRECT_ANSWER_COUNT
                        + LEVEL_MEDIUM_CORRECT_ANSWER_COUNT
                        + LEVEL_EASY_CORRECT_ANSWER_COUNT;
                currentLevelMax = LEVEL_HARDER_CORRECT_ANSWER_COUNT - completedLevelsTasks;
                break;
            case DIFFICULTY_HARD:
                completedLevelsTasks = LEVEL_MEDIUM_CORRECT_ANSWER_COUNT
                        + LEVEL_EASY_CORRECT_ANSWER_COUNT;
                currentLevelMax = LEVEL_HARD_CORRECT_ANSWER_COUNT - completedLevelsTasks;
                break;
            case DIFFICULTY_MEDIUM:
                completedLevelsTasks = LEVEL_EASY_CORRECT_ANSWER_COUNT;
                currentLevelMax = LEVEL_MEDIUM_CORRECT_ANSWER_COUNT - completedLevelsTasks;
                break;
            case DIFFICULTY_EASY:
            default:
                currentLevelMax = LEVEL_EASY_CORRECT_ANSWER_COUNT;
                break;
        }

        int currentLevelCompletedTasks = mSharedPreferencesUtils.getCompletedTaskCount() - completedLevelsTasks;
        levelProgress.setMax(currentLevelMax);
        levelProgress.setProgress(currentLevelCompletedTasks);

        int levelPercentage = ((100 * currentLevelCompletedTasks) / currentLevelMax);
        if (levelPercentage < 33) {
            levelProgressText.setText(getString(R.string.level_direction_start));
        } else if (levelPercentage >= 33 && levelPercentage < 66) {
            levelProgressText.setText(getString(R.string.level_direction_middle));
        } else {
            levelProgressText.setText(getString(R.string.level_direction_end));
        }
    }

    private void showCurrentDifficultyContainer() {
        currentDifficultyContainer.setVisibility(View.VISIBLE);
        currentDifficultyNoProgressContainer.setVisibility(View.GONE);
    }

    private void hideCurrentDifficultyContainer() {
        currentDifficultyContainer.setVisibility(View.GONE);
        currentDifficultyNoProgressContainer.setVisibility(View.VISIBLE);
    }

    private void setupUI(boolean isSpinnerClick) {
        Log.e("!!!!", String.valueOf(mSharedPreferencesUtils.getForcedDifficulty()));
        Log.e("!!!!", String.valueOf(mSharedPreferencesUtils.getDifficulty()));
        if (mSharedPreferencesUtils.getForcedDifficulty() == FORCED_DIFFICULTY_NONE ||
                mSharedPreferencesUtils.getForcedDifficulty() == mSharedPreferencesUtils.getDifficulty()) {
            showCurrentDifficultyContainer();
            setCurrentProgress();
        } else {
            hideCurrentDifficultyContainer();
        }
        if (!isSpinnerClick) {
            setDifficultySpinner();
        }

        DetectionAppWidget.updateWidget(MainActivity.this);

        detectionSwitch.setChecked(mSharedPreferencesUtils.isDetectionOn());
        detectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferencesUtils.setIsDetectionOn(isChecked);
                if (isChecked) {
                    MotionDetectionService.startMotionDetection(MainActivity.this);
                } else {
                    MotionDetectionService.stopMotionDetection(MainActivity.this);
                }
                DetectionAppWidget.updateWidget(MainActivity.this);
            }
        });

        soundSwitch.setChecked(mSharedPreferencesUtils.isSoundOn());
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferencesUtils.setIsSoundOn(isChecked);
            }
        });

        vibrationSwitch.setChecked(mSharedPreferencesUtils.isVibrationOn());
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferencesUtils.setIsVibrationOn(isChecked);
            }
        });

    }

    @Override
    protected void onResume() {
        setupUI(false);
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (COMPLETED_TASK_COUNT.equals(key) || DIFFICULTY.equals(key)) {
            setupUI(false);
        }
    }
}
