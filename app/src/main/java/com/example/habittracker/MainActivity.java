package com.example.habittracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habittracker.db.DBHelper;
import com.example.habittracker.db.Habit;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HabitAdapter adapter;
    private List<Habit> habitList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        habitList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddDialog());

        loadHabits();
    }

    private void loadHabits() {
        habitList.clear();
        habitList.addAll(dbHelper.getAllHabits());
        if (adapter == null) {
            adapter = new HabitAdapter(habitList, this::deleteHabit);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
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

    private void deleteHabit(Habit habit) {
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
}