package com.trevin.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EndScreenActivity extends AppCompatActivity {

    private TextView displayName, finalScore;

    private Button tryAgain, finishGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        displayName = findViewById(R.id.endScreenToastLabel);
        finalScore = findViewById(R.id.endScreenScoreLabel);

        tryAgain = findViewById(R.id.takeAnotherQuiz);
        finishGame = findViewById(R.id.finishButton);

        String name = getIntent().getStringExtra("userName");
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);

        float finalGrade = ((float) score / total) * 100;
        if (finalGrade >= 80){
            displayName.setText("Congratulations " + name + "! You got over 80%");
        } else if (finalGrade > 50){
            displayName.setText("Not bad " + name + ". You got over 50%");
        } else{
            displayName.setText("You might want to try again " + name + ". You got less than 50%");
        }
        finalScore.setText("Your score:\n" + score + " / " + total);

        finishGame.setOnClickListener(v -> finishAffinity());
        tryAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}