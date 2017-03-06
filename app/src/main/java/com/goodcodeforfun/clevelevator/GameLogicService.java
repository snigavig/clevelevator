package com.goodcodeforfun.clevelevator;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;

import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_EASY;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_MEDIUM;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARD;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_HARDER;
import static com.goodcodeforfun.clevelevator.EquationGenerationUtils.DIFFICULTY_NIGHTMARE;

public class GameLogicService extends IntentService {
    public static final String CORRECT_ANSWER_ACTION = "com.goodcodeforfun.clevelevator.action.CORRECT_ANSWER";
    public static final String WRONG_ANSWER_ACTION = "com.goodcodeforfun.clevelevator.action.WRONG_ANSWER";

    public static final int LEVEL_DEFAULT_CORRECT_ANSWER_COUNT = 0;
    public static final int LEVEL_EASY_CORRECT_ANSWER_COUNT = 10;
    public static final int LEVEL_MEDIUM_CORRECT_ANSWER_COUNT = 50;
    public static final int LEVEL_HARD_CORRECT_ANSWER_COUNT = 100;
    public static final int LEVEL_HARDER_CORRECT_ANSWER_COUNT = 1000;


    public GameLogicService() {
        super("GameLogicService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (CORRECT_ANSWER_ACTION.equals(action)) {
                SharedPreferencesUtils sharedPreferencesUtils = SharedPreferencesUtils.getInstance(getApplicationContext());
                int completedTaskCount = sharedPreferencesUtils.getCompletedTaskCount() + 1;
                int currentDifficulty = sharedPreferencesUtils.getDifficulty();
                boolean isLevelUp = false;
                sharedPreferencesUtils.setCompletedTaskCount(completedTaskCount);

                if (currentDifficulty == DIFFICULTY_EASY && completedTaskCount >= LEVEL_EASY_CORRECT_ANSWER_COUNT) {
                    sharedPreferencesUtils.setDifficulty(DIFFICULTY_MEDIUM);
                    isLevelUp = true;
                }

                if (currentDifficulty == DIFFICULTY_MEDIUM && completedTaskCount >= LEVEL_MEDIUM_CORRECT_ANSWER_COUNT) {
                    sharedPreferencesUtils.setDifficulty(DIFFICULTY_HARD);
                    isLevelUp = true;
                }

                if (currentDifficulty == DIFFICULTY_HARD && completedTaskCount >= LEVEL_HARD_CORRECT_ANSWER_COUNT) {
                    sharedPreferencesUtils.setDifficulty(DIFFICULTY_HARDER);
                    isLevelUp = true;
                }

                if (currentDifficulty == DIFFICULTY_HARDER && completedTaskCount >= LEVEL_HARDER_CORRECT_ANSWER_COUNT) {
                    sharedPreferencesUtils.setDifficulty(DIFFICULTY_NIGHTMARE);
                    isLevelUp = true;
                }

                if (isLevelUp) {
                    handleActionLevelUp();
                } else {
                    handleActionAnswerCorrect();
                }
            } else if (WRONG_ANSWER_ACTION.equals(action)) {
                handleActionAnswerWrong();
            }
        }
    }

    private void handleActionAnswerCorrect() {
        NotificationService.startActionAnswerCorrect(getApplicationContext());
    }

    private void handleActionAnswerWrong() {
        NotificationService.startActionAnswerWrong(getApplicationContext());
    }

    private void handleActionLevelUp() {
        NotificationService.startActionLevelUp(getApplicationContext());
    }
}
