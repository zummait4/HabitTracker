package com.example.habittracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.db.DBHelper;
import com.example.habittracker.db.Habit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private LinearLayout layoutWeek;
    private TextView tvMissedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DBHelper(this);

        layoutWeek = findViewById(R.id.layoutWeek);
        tvMissedDays = findViewById(R.id.tvMissedDays);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        List<Habit> habits = dbHelper.getAllHabits();

        if (habits.isEmpty()) {
            tvMissedDays.setText("Нет привычек. Добавьте первую!");
            return;
        }

        Habit mainHabit = habits.get(0);

        drawWeekChart(mainHabit);
        showMissedDays(mainHabit);
    }

    private void drawWeekChart(Habit habit) {
        layoutWeek.removeAllViews();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EE", new Locale("ru")); // ПН, ВТ, СР...
        Calendar calendar = Calendar.getInstance();

        // Массив дат за последние 7 дней (от 6 дней назад до сегодня)
        List<String> last7Days = new ArrayList<>();
        List<String> dayNames = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            last7Days.add(sdf.format(cal.getTime()));
            dayNames.add(dayFormat.format(cal.getTime()));
        }

        List<String> habitLog = getHabitLog(habit.id);
        String today = sdf.format(new Date());

        for (int i = 0; i < 7; i++) {
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setPadding(16, 8, 16, 8);
            dayLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView dayName = new TextView(this);
            dayName.setText(dayNames.get(i));
            dayName.setTextColor(Color.parseColor("#5C3A21"));
            dayName.setTextSize(12);
            dayName.setGravity(android.view.Gravity.CENTER);

            TextView dayStatus = new TextView(this);
            String date = last7Days.get(i);

            if (habitLog.contains(date)) {
                dayStatus.setText("✅");
                dayStatus.setTextSize(20);
            } else if (date.equals(today)) {
                dayStatus.setText("⏳");
                dayStatus.setTextSize(20);
            } else if (date.compareTo(today) < 0) {
                dayStatus.setText("❌");
                dayStatus.setTextSize(20);
            } else {
                dayStatus.setText("?");
                dayStatus.setTextSize(20);
            }
            dayStatus.setGravity(android.view.Gravity.CENTER);

            dayLayout.addView(dayName);
            dayLayout.addView(dayStatus);
            layoutWeek.addView(dayLayout);
        }
    }
    private List<String> getHabitLog(int habitId) {
        List<String> log = new ArrayList<>();
        log.add(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        return log;
    }

    private void showMissedDays(Habit habit) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        List<String> missedDays = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String date = sdf.format(calendar.getTime());

            if (habit.streak == 0 && !date.equals(habit.lastDoneDate)) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM", new Locale("ru"));
                missedDays.add(displayFormat.format(calendar.getTime()));
            }
        }

        if (missedDays.isEmpty()) {
            tvMissedDays.setText("✅ Отличная работа! Нет пропусков за последнюю неделю!");
        } else {
            StringBuilder sb = new StringBuilder("⚠️ Пропущенные дни:\n");
            for (String day : missedDays) {
                sb.append("• ").append(day).append("\n");
            }
            sb.append("\n🔥 Поставь галочку, чтобы не терять серию!");
            tvMissedDays.setText(sb.toString());
        }
    }
}