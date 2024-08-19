package com.movindu.pub.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.movindu.pub.Models.Expense;
import com.movindu.pub.R;

import java.util.List;

public class IncomeAdapter extends ArrayAdapter<Expense> {
    private Context context;
    private List<Expense> expenses;
    private double total = 0.0;

    public IncomeAdapter(@NonNull Context context, @NonNull List<Expense> expenses) {
        super(context, 0, expenses);
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);
        }

        Expense user_expenses = expenses.get(position);
        total = total + user_expenses.getAmount();

        TextView amount = convertView.findViewById(R.id.nameTextView);
        TextView note = convertView.findViewById(R.id.emailTextView);
        TextView date = convertView.findViewById(R.id.dateTextView);
        LinearLayout MainLO = convertView.findViewById(R.id.lo);
        if(position %2 == 0){
            MainLO.setBackgroundColor(Color.GRAY);
        }
        else{
            MainLO.setBackgroundColor(Color.DKGRAY);
        }

        amount.setText(String.valueOf(user_expenses.getAmount()) );
        note.setText(user_expenses.getNote());
        date.setText(user_expenses.getDate().getYear()+"-"+user_expenses.getDate().getMonth()+"-"+user_expenses.getDate().getDay());

        return convertView;
    }

}
