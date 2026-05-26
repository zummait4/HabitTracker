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
    private OnHabitActionListener listener;

    public interface OnHabitActionListener {
        void onDelete(Habit habit);
        void onStatusChanged(Habit habit);
    }

    public HabitAdapter(List<Habit> habits, OnHabitActionListener listener) {
        this.habits = habits;
        this.listener = listener;
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
        holder.tvStreak.setText("🔥 " + habit.streak + " дн.");

        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(habit.isDoneToday);
        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.isDoneToday = isChecked;
            if (listener != null) {
                listener.onStatusChanged(habit);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDelete(habit);
            }
            return true;
        });

        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).start();
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStreak;
        CheckBox cbDone;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHabitName);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            cbDone = itemView.findViewById(R.id.cbDone);
        }
    }
}