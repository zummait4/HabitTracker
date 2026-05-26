package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habittracker.db.DBHelper;
import com.example.habittracker.db.Habit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements HabitAdapter.OnHabitActionListener {
    private RecyclerView recyclerView;
    private HabitAdapter adapter;
    private List<Habit> habitList;
    private DBHelper dbHelper;
    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStats = findViewById(R.id.tvStats);

        dbHelper = new DBHelper(this);
        habitList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddDialog());

        Button btnClearAll = findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(v -> clearAllHabits());

        Button btnStats = findViewById(R.id.btnStats);
        btnStats.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        });

        loadHabits();
    }

    private void loadHabits() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<Habit> allHabits = dbHelper.getAllHabits();

        for (Habit habit : allHabits) {
            if (!today.equals(habit.lastDoneDate)) {
                habit.isDoneToday = false;
                dbHelper.updateDoneStatus(habit.id, false);
            }
        }

        habitList.clear();
        habitList.addAll(dbHelper.getAllHabits());

        if (adapter == null) {
            adapter = new HabitAdapter(habitList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        updateStats();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить привычку");
        EditText input = new EditText(this);
        input.setHint("Название");
        builder.setView(input);
        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                dbHelper.addHabit(name);
                loadHabits();
                Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void clearAllHabits() {
        new AlertDialog.Builder(this)
                .setTitle("Очистить всё")
                .setMessage("Удалить все привычки?")
                .setPositiveButton("Да", (dialog, which) -> {
                    for (Habit habit : habitList) {
                        dbHelper.deleteHabit(habit.id);
                    }
                    loadHabits();
                    Toast.makeText(this, "Все привычки удалены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    public void onDelete(Habit habit) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить")
                .setMessage("Удалить привычку \"" + habit.name + "\"?")
                .setPositiveButton("Да", (dialog, which) -> {
                    dbHelper.deleteHabit(habit.id);
                    loadHabits();
                    Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    public void onStatusChanged(Habit habit) {
        dbHelper.updateDoneStatus(habit.id, habit.isDoneToday);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        int newStreak = habit.streak;

        if (habit.isDoneToday) {
            if (!today.equals(habit.lastDoneDate)) {
                newStreak++;
                habit.lastDoneDate = today;
            }
        } else {
            newStreak = 0;
        }

        habit.streak = newStreak;
        dbHelper.updateStreak(habit.id, newStreak, habit.lastDoneDate);

        int position = habitList.indexOf(habit);
        if (position != -1) {
            adapter.notifyItemChanged(position);
        }
    }

    private void updateStats() {
        tvStats.setText("Всего привычек: " + habitList.size());
    }
}