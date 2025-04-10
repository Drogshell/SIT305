package com.trevin.quizapp;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private Button startQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = findViewById(R.id.editNameText);
        startQuiz = findViewById(R.id.startButton);

        startQuiz.setOnClickListener(view -> {
            String name = userName.getText().toString().trim();
            if (!name.isEmpty()){
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("userName", name);
                startActivity(intent);
            } else {
                Snackbar.make(view, "Please enter a name!", Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.RED)
                        .setAnchorView(view)
                        .show();
            }
        });
    }
}