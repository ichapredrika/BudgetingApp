package com.icha.budgetingapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.icha.budgetingapp.db.TransHelper;
import com.icha.budgetingapp.entity.Trans;

import java.util.ArrayList;
import java.util.Calendar;

import static com.icha.budgetingapp.AddFragment.EXTRA_EXPENSE;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.AMOUNT;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.DATE;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.DESCRIPTION;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.TYPE;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TYPE_REPEATING = "RepeatingTrans";
    public static final String EXTRA_MESSAGE = "message";

    private final int ID_REPEATING = 101;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        String title = TYPE_REPEATING;
        int notifId = ID_REPEATING;
        showToast(context, title, message);

        insertTrans(context, title, message, notifId);
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = ID_REPEATING;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        pendingIntent.cancel();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        Toast.makeText(context, "Repeating budget is canceled", Toast.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String title, String message) {
        Toast.makeText(context, title + " : " + message, Toast.LENGTH_LONG).show();
    }

    public void setRepeatingAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, "A new repeating budget has been inserted");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * getDuration(), pendingIntent);

        Toast.makeText(context, "Repeating transaction is set", Toast.LENGTH_SHORT).show();
    }

    private long getDuration() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        currentMonth++;

        if (currentMonth > Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY;
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }

        cal.set(Calendar.MONTH, currentMonth);
        int maximumDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, maximumDay);
        long thenTime = cal.getTimeInMillis();

        return (thenTime);

    }

    private void showAlarmNotification(Context context, String title, String message, int notifId) {
        String CHANNEL_ID = "Channel_1";
        String CHANNEL_NAME = "AlarmManager channel";
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_time)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setChannelId(CHANNEL_ID);
            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }
        Notification notification = builder.build();
        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }

    }

    private void insertTrans(Context context, String title, String message, int notifId) {
        UserPreference userPreference = new UserPreference(context);
        ArrayList<Trans> transList = new ArrayList<>();
        transList.addAll(userPreference.getTransList());
        TransHelper transHelper = TransHelper.getInstance(context.getApplicationContext());

        for (int i = 0; i < transList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(DESCRIPTION, transList.get(i).getDescription());
            values.put(AMOUNT, transList.get(i).getAmount());
            values.put(DATE, transList.get(i).getDate());
            values.put(TYPE, transList.get(i).getType());

            long result = transHelper.insert(values);

            if (result > 0) {
                showAlarmNotification(context, title, message, notifId);

                Calendar calendar = Calendar.getInstance();
                String date = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH);
                double expense = transHelper.sumExpense(date);
                double maxSpent = userPreference.getWarning();
                double totalExpense = expense + transList.get(i).getAmount();

                if (transList.get(i).getType().equals(EXTRA_EXPENSE) && totalExpense > maxSpent && maxSpent > 0) {
                    Toast.makeText(context, "You have spent more than maximum monthly spent!", Toast.LENGTH_SHORT).show();
                    maxSpent = totalExpense + 100000;
                    String maxSpentstr = Double.toString(maxSpent);
                    userPreference.setWarning(Float.parseFloat(maxSpentstr));
                }
            } else
                Toast.makeText(context, "Fail adding data", Toast.LENGTH_SHORT).show();
        }
    }
}
