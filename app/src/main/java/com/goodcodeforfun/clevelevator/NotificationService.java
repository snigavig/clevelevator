package com.goodcodeforfun.clevelevator;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.goodcodeforfun.clevelevator.GameLogicService.CORRECT_ANSWER_ACTION;
import static com.goodcodeforfun.clevelevator.GameLogicService.WRONG_ANSWER_ACTION;

public class NotificationService extends IntentService {
    private static final String ACTION_SHOW_EQUATION = "com.goodcodeforfun.clevelevator.action.SHOW_EQUATION";
    private static final String ACTION_SHOW_ANSWER_CORRECT = "com.goodcodeforfun.clevelevator.action.SHOW_ANSWER_CORRECT";
    private static final String ACTION_SHOW_ANSWER_WRONG = "com.goodcodeforfun.clevelevator.action.SHOW_ANSWER_WRONG";
    private static final String ACTION_SHOW_LEVEL_UP = "com.goodcodeforfun.clevelevator.action.SHOW_LEVEL_UP";

    private static final String EXTRA_PARAM_MOTION_TYPE = "com.goodcodeforfun.clevelevator.extra.PARAM_MOTION_TYPE";
    private static final int CORRECT_ANSWER_REQUEST_CODE = 303;
    private static final int WRONG_ANSWER_REQUEST_CODE = 404;
    private static final int ONE_MORE_REQUEST_CODE = 505;

    int mNotificationId = 10101;

    public NotificationService() {
        super("NotificationService");
    }

    public static void startActionShowEquation(Context context, String motionType) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SHOW_EQUATION);
        intent.putExtra(EXTRA_PARAM_MOTION_TYPE, motionType);
        context.startService(intent);
    }

    public static void startActionAnswerCorrect(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SHOW_ANSWER_CORRECT);
        context.startService(intent);
    }

    public static void startActionAnswerWrong(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SHOW_ANSWER_WRONG);
        context.startService(intent);
    }

    public static void startActionLevelUp(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_SHOW_LEVEL_UP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SHOW_EQUATION.equals(action)) {
                final String paramMotionType = intent.getStringExtra(EXTRA_PARAM_MOTION_TYPE);
                EquationGenerationUtils.Equation equation = EquationGenerationUtils.generateEquation(
                        SharedPreferencesUtils.getInstance(getApplicationContext()).getDifficulty()
                );
                handleActionShowEquation(
                        equation.printEquation(),
                        equation.getResult(),
                        equation.getFirstWrongResult(),
                        equation.getSecondWrongResult(),
                        paramMotionType);
            } else if (ACTION_SHOW_ANSWER_CORRECT.equals(action)) {
                handleActionShowAnswerCorrect();
            } else if (ACTION_SHOW_ANSWER_WRONG.equals(action)) {
                handleActionShowAnswerWrong();
            } else if (ACTION_SHOW_LEVEL_UP.equals(action)) {
                handleActionShowLevelUp();
            }
        }
    }

    private void handleActionShowEquation(String equation, int result, int firstWrongResult, int secondWrongResult, @Nullable String motionType) {
        NotificationCompat.Builder equationNotification;
        int iconAnswer = R.drawable.answer_button_background;

        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.equation_notification_layout);

        equationNotification = new NotificationCompat.Builder(this);
        equationNotification.setCustomBigContentView(views);
        equationNotification.setSmallIcon(R.mipmap.ic_launcher);

        views.setImageViewResource(R.id.firstAnswerImageButton, iconAnswer);
        views.setImageViewResource(R.id.secondAnswerImageButton, iconAnswer);
        views.setImageViewResource(R.id.thirdAnswerImageButton, iconAnswer);
        views.setTextViewText(R.id.equationTextView, equation);

        List<Integer> answerViews = new ArrayList<>();
        answerViews.add(R.id.firstAnswerTextView);
        answerViews.add(R.id.secondAnswerTextView);
        answerViews.add(R.id.thirdAnswerTextView);
        Collections.shuffle(answerViews);

        Intent correctAnswerIntent = new Intent(this, GameLogicService.class);
        correctAnswerIntent.setAction(CORRECT_ANSWER_ACTION);
        PendingIntent correctAnswerPendingIntent = PendingIntent.getService(this, CORRECT_ANSWER_REQUEST_CODE, correctAnswerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent wrongAnswerIntent = new Intent(this, GameLogicService.class);
        wrongAnswerIntent.setAction(WRONG_ANSWER_ACTION);
        PendingIntent wrongAnswerPendingIntent = PendingIntent.getService(this, WRONG_ANSWER_REQUEST_CODE, wrongAnswerIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        views.setTextViewText(answerViews.get(0), String.valueOf(result));
        views.setOnClickPendingIntent(answerViews.get(0), correctAnswerPendingIntent);

        views.setTextViewText(answerViews.get(1), String.valueOf(firstWrongResult));
        views.setOnClickPendingIntent(answerViews.get(1), wrongAnswerPendingIntent);

        views.setTextViewText(answerViews.get(2), String.valueOf(secondWrongResult));
        views.setOnClickPendingIntent(answerViews.get(2), wrongAnswerPendingIntent);

        if (null != motionType) {
            views.setTextViewText(R.id.appTitleTextView, motionType);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, equationNotification.build());
    }

    private void handleActionShowAnswerCorrect() {
        showInformationNotification(getString(R.string.correct_answer_message));
    }

    private void handleActionShowAnswerWrong() {
        showInformationNotification(getString(R.string.wrong_answer_message));
    }

    private void handleActionShowLevelUp() {
        showInformationNotification(getString(R.string.level_up_message));
    }

    private void showInformationNotification(String contentText) {
        NotificationCompat.Builder informationNotification;
        informationNotification = new NotificationCompat.Builder(this);
        informationNotification.setSmallIcon(R.mipmap.ic_launcher);
        informationNotification.setContentText(contentText);

        Intent oneMoreIntent = new Intent(this, NotificationService.class);
        oneMoreIntent.setAction(ACTION_SHOW_EQUATION);
        PendingIntent oneMorePendingIntent = PendingIntent.getService(this, ONE_MORE_REQUEST_CODE, oneMoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action oneMoreAction = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_add, getString(R.string.one_more_button_label), oneMorePendingIntent).build();
        informationNotification.addAction(oneMoreAction);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, informationNotification.build());
    }

    private void handleActionShowIntroduction() {
        //TODO: implement
    }


}
