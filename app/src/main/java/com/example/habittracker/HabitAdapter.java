package com.example.habittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habittracker.db.Habit;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {
    private List<Habit> habits;
    private OnDeleteListener deleteListener;

    public interface OnDeleteListener {
        void onDelete(Habit habit);
    }

    public HabitAdapter(List<Habit> habits, OnDeleteListener deleteListener) {
        this.habits = habits;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.tvName.setText(habit.name);
        holder.cbDone.setChecked(habit.isDoneToday);

        holder.itemView.setOnLongClickListener(v -> {
            deleteListener.onDelete(habit);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbDone;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHabitName);
            cbDone = itemView.findViewById(R.id.cbDone);
        }
    }
}