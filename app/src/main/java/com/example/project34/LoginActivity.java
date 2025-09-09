package com.example.project34;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button loginButtonL, registerButtonL;
    EditText nameInputL, passwordInputL;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameInputL = findViewById(R.id.nameInputL);
        passwordInputL = findViewById(R.id.passwordInputL);
        loginButtonL = findViewById(R.id.loginButtonL);
        registerButtonL = findViewById(R.id.registerButtonL);
        dbHelper = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        loginButtonL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameInputL.getText().toString();
                String passwordTyped = passwordInputL.getText().toString();
                String passwordHash = CryptoHelper.hashPassword(passwordTyped);

                if (dbHelper.validateLogin(name, passwordHash)) {
                    prefs.edit().putBoolean("loggedIn", true).apply();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("username", name);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButtonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}