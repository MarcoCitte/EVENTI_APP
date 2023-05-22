package com.example.eventiapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final String month;
    private final OnItemListener onItemListener;
    private final List<Events> eventsList;

    public CalendarAdapter(ArrayList<String> daysOfMonth, String month, List<Events> eventsList, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.month = month;
        this.eventsList = eventsList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }


    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        holder.cellMonthText.setText(month);
        holder.bind(eventsList);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText, int isThereEvent);
    }


    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView dayOfMonth;
        public final TextView cellMonthText;
        public final TextView theresEvent;
        private final CalendarAdapter.OnItemListener onItemListener;

        public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            cellMonthText = itemView.findViewById(R.id.cellMonthText);
            theresEvent = itemView.findViewById(R.id.theresEventTV);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        public void bind(List<Events> eventsList) {
            for(Events events : eventsList) {
                if (dayOfMonth.getText() != "") {
                    @SuppressLint("DefaultLocale") String day = String.format("%02d", Integer.parseInt(String.valueOf(dayOfMonth.getText())));
                    String date = cellMonthText.getText() + "-" + day;
                    if (events.getStart()!=null && events.getStart().contains(date)) {
                        theresEvent.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onClick(View view) {
            TextView isThereEventTV=view.findViewById(R.id.theresEventTV);
            onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText(),isThereEventTV.getVisibility());
        }

    }
}