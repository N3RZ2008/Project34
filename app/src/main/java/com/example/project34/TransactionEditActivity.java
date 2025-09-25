package com.example.project34;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionEditActivity extends AppCompatActivity {

    TextView editTitle;
    EditText descriptionInput, valueInput, dateInput, categoryInput, typeInput;
    Button homeButton, submitTransactionButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        typeInput = findViewById(R.id.typeInput);
        valueInput = findViewById(R.id.valueInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateInput = findViewById(R.id.dateInput);
        categoryInput = findViewById(R.id.categoryInput);

        editTitle = findViewById(R.id.editTitle);
        submitTransactionButton = findViewById(R.id.submitTransactionButton);
        homeButton = findViewById(R.id.homeButton);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        int transactionId = getIntent().getIntExtra("transactionId", -1);

        if (transactionId != -1) {
            String getType = getIntent().getStringExtra("type");
            double getValue = getIntent().getDoubleExtra("value", 0);
            String getDesc = getIntent().getStringExtra("desc");
            String getDate = getIntent().getStringExtra("date");
            String getCategory = getIntent().getStringExtra("category");

            typeInput.setText(getType);
            valueInput.setText(String.valueOf(getValue));
            descriptionInput.setText(getDesc);
            dateInput.setText(getDate);
            categoryInput.setText(getCategory);
            editTitle.setText("Update Transaction");
            submitTransactionButton.setText("Update");
        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionEditActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dbHelper = new DBHelper(this);

        submitTransactionButton.setOnClickListener(v -> {
            String type = typeInput.getText().toString();
            double value = Double.parseDouble(valueInput.getText().toString());
            String description = descriptionInput.getText().toString();
            String date = dateInput.getText().toString();
            String category = categoryInput.getText().toString();

            if (transactionId == -1) {
                boolean success = dbHelper.insertTransaction(userId, type, description, value, date, category);
                if (success) {
                    Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TransactionEditActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error saving transaction", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean success = dbHelper.updateTransaction(transactionId, type, description, value, date, category);
                if (success) {
                    Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TransactionEditActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error updating transaction", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}