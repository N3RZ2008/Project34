package com.example.project34;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    TextView incomeView, expenditureView, balanceView;
    EditText monthInput, typeInput, categoryInput;
    Button homeButton, applyFilter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        dbHelper = new DBHelper(this);
        incomeView = findViewById(R.id.incomeView);
        expenditureView = findViewById(R.id.expenditureView);
        balanceView = findViewById(R.id.balanceView);
        monthInput = findViewById(R.id.monthInput);
        typeInput = findViewById(R.id.sTypeInput);
        categoryInput = findViewById(R.id.sCategoryInput);
        homeButton = findViewById(R.id.homeButton2);
        applyFilter = findViewById(R.id.applyFilter);

        applyFilter.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            prefs.edit()
                    .putString("filter_month", monthInput.getText().toString().trim())
                    .putString("filter_type", typeInput.getText().toString().trim())
                    .putString("filter_category", categoryInput.getText().toString().trim())
                    .apply();

            updateSummary();
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        updateSummary();
    }
    private void updateSummary() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) return;

        String filterMonth = prefs.getString("filter_month", "");
        String filterType = prefs.getString("filter_type", "");
        String filterCategory = prefs.getString("filter_category", "");

        if (filterMonth.isEmpty()) {
            filterMonth = "";
        } else if (filterMonth.length() == 1) {
            filterMonth = "0" + filterMonth;
        }

        if (!filterType.isEmpty()) {
            filterType = filterType.substring(0,1).toUpperCase() + filterType.substring(1).toLowerCase();
        }

        StringBuilder query = new StringBuilder(
                "SELECT value, type FROM transactions WHERE user_id = ?"
        );
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        if (!filterMonth.isEmpty()) {
            query.append(" AND strftime('%m', date) = ?");
            args.add(filterMonth);
        }

        if (!filterType.isEmpty()) {
            query.append(" AND type = ?");
            args.add(filterType);
        }

        if (!filterCategory.isEmpty()) {
            query.append(" AND category = ?");
            args.add(filterCategory);
        }

        Cursor c = dbHelper.getReadableDatabase().rawQuery(query.toString(), args.toArray(new String[0]));

        double totalIncome = 0.0;
        double totalExpenditure = 0.0;

        while (c.moveToNext()) {
            double value = c.getDouble(c.getColumnIndexOrThrow("value"));
            String type = c.getString(c.getColumnIndexOrThrow("type"));

            if (type.equalsIgnoreCase("Income")) {
                totalIncome += value;
            } else if (type.equalsIgnoreCase("Expenditure")) {
                totalExpenditure += value;
            }
        }

        c.close();

        double balance = totalIncome - totalExpenditure;

        incomeView.setText(String.format("%.2f", totalIncome));
        expenditureView.setText(String.format("%.2f", totalExpenditure));
        balanceView.setText(String.format("%.2f", balance));
    }

}
