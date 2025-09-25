package com.example.project34;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    ListView transactionsList;
    Button homeButton2;
    DBHelper dbHelper;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        transactionsList = findViewById(R.id.transactionsList);
        homeButton2 = findViewById(R.id.homeButton2);
        dbHelper = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        loadTransactions();

        transactionsList.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            double value = cursor.getDouble(cursor.getColumnIndexOrThrow("value"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));

            Intent intent = new Intent(ListActivity.this, TransactionEditActivity.class);
            intent.putExtra("transactionId", transactionId);
            intent.putExtra("type", type);
            intent.putExtra("desc", description);
            intent.putExtra("value", value);
            intent.putExtra("date", date);
            intent.putExtra("category", category);
            startActivity(intent);
            finish();
        });

        homeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadTransactions() {
        Cursor cursor = dbHelper.getTransactionsByUser(userId);

        if (cursor != null) {
            String[] from = {"type", "description", "value", "date", "category"};
            int[] to = {R.id.tvType, R.id.tvDesc, R.id.tvValue, R.id.tvDate, R.id.tvCategory};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this, R.layout.transaction_item, cursor, from, to, 0
            );

            transactionsList.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No transactions found", Toast.LENGTH_SHORT).show();
        }
    }
}
