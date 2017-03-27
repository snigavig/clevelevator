package com.goodcodeforfun.clevelevator;

import android.app.IntentService;
import android.content.Intent;

import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.FORCED_DIFFICULTY_NONE;

public class GameLogicService extends IntentService {
    public static final String CORRECT_ANSWER_ACTION = "com.goodcodeforfun.clevelevator.action.CORRECT_ANSWER";
    public static final String WRONG_ANSWER_ACTION = "com.goodcodeforfun.clevelevator.action.WRONG_ANSWER";

    public static final int LEVEL_DEFAULT_CORRECT_ANSWER_COUNT = 0;
    public static final int LEVEL_EASY_CORRECT_ANSWER_COUNT = 25;
    public static final int LEVEL_MEDIUM_CORRECT_ANSWER_COUNT = 100;
    public static final int LEVEL_HARD_CORRECT_ANSWER_COUNT = 200;
    public static final int LEVEL_HARDER_CORRECT_ANSWER_COUNT = 1000;


    public GameLogicService() {
        super("GameLogicService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (CORRECT_ANSWER_ACTION.equals(action)) {
                handleActionCorrectAnswer();
            } else if (WRONG_ANSWER_ACTION.equals(action)) {
                showAnswerWrong();
            }
        }
    }

    private void handleActionCorrectAnswer() {
        SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(getApplicationContext());
        int currentDifficulty = sharedPreferencesUtils.getDifficulty();
        int forcedDifficulty = sharedPreferencesUtils.getForcedDifficulty();
        int completedTaskCount = sharedPreferencesUtils.getCompletedTaskCount();
        if (forcedDifficulty == FORCED_DIFFICULTY_NONE || forcedDifficulty == currentDifficulty) {
            completedTaskCount++;
            sharedPreferencesUtils.setCompletedTaskCount(completedTaskCount);
        }

        if (currentDifficulty == DIFFICULTY_EASY && completedTaskCount >= LEVEL_EASY_CORRECT_ANSWER_COUNT ||
                currentDifficulty == DIFFICULTY_MEDIUM && completedTaskCount >= LEVEL_MEDIUM_CORRECT_ANSWER_COUNT ||
                currentDifficulty == DIFFICULTY_HARD && completedTaskCount >= LEVEL_HARD_CORRECT_ANSWER_COUNT ||
                currentDifficulty == DIFFICULTY_HARDER && completedTaskCount >= LEVEL_HARDER_CORRECT_ANSWER_COUNT) {
            sharedPreferencesUtils.incrementDifficulty();
            showLevelUp();
        } else {
            showAnswerCorrect();
        }
    }

    private void showAnswerCorrect() {
        NotificationService.startActionAnswerCorrect(getApplicationContext());
    }

    private void showAnswerWrong() {
        NotificationService.startActionAnswerWrong(getApplicationContext());
    }

    private void showLevelUp() {
        NotificationService.startActionLevelUp(getApplicationContext());
    }
}
