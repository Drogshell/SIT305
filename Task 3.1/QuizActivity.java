package com.trevin.quizapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private TextView quizTrackerTextView;
    private TextView displayQuestionTextView;
    private Button buttonA, buttonB, buttonC;
    private ProgressBar progressBar;
    private QuizService quizService;

    // A handler allows a timer for each question to make the game more challenging
    private final Handler handler = new Handler();
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = getIntent().getStringExtra("userName");

        quizTrackerTextView = findViewById(R.id.quizTrackerTextView);
        displayQuestionTextView = findViewById(R.id.displayQuestionTextView);
        TextView displayNameTextView = findViewById(R.id.quizViewDisplayNameTextView);

        buttonA = findViewById(R.id.buttonAnswerA);
        buttonB = findViewById(R.id.buttonAnswerB);
        buttonC = findViewById(R.id.buttonAnswerC);

        progressBar = findViewById(R.id.progressBar);

        displayNameTextView.setText("Welcome " + userName + "!");

        quizService = new QuizService(new HardCodedQuestionRepo());

        setUpButtonListeners();
        loadNextQuestion();

    }

    private void setUpButtonListeners() {
        // Since all buttons share the same code, it's best to make the listener here
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // default
                int selected = -1;

                int viewID = v.getId();
                if (viewID == R.id.buttonAnswerA){
                    selected = 1;
                } else if (viewID == R.id.buttonAnswerB) {
                    selected = 2;
                } else if (viewID == R.id.buttonAnswerC) {
                    selected = 3;
                }

                boolean isCorrect = quizService.checkAnswer(selected);

                v.setBackgroundColor(isCorrect ? Color.parseColor("#8BC34A"): Color.parseColor("#C62828"));
                pulseButton(v);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButtonColours();
                        quizService.nextQuestion();
                        if (quizService.hasMoreQuestions()){
                            loadNextQuestion();
                        } else {
                            Intent intent = new Intent(QuizActivity.this, EndScreenActivity.class);
                            intent.putExtra("userName", userName);
                            intent.putExtra("score", quizService.getScore());
                            intent.putExtra("total", quizService.getTotalQuestions());
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 300);
            }
        };

        buttonA.setOnClickListener(listener);
        buttonB.setOnClickListener(listener);
        buttonC.setOnClickListener(listener);

    }

    private void resetButtonColours(){
        int defaultColour = ContextCompat.getColor(this, R.color.prim_variant);
        buttonA.setBackgroundColor(defaultColour);
        buttonB.setBackgroundColor(defaultColour);
        buttonC.setBackgroundColor(defaultColour);
    }

    private void loadNextQuestion() {
        Question question = quizService.getCurrentQuestion();
        quizTrackerTextView.setText(quizService.getCurrentQuestionNumber() + " / " + quizService.getTotalQuestions());
        displayQuestionTextView.setText(question.getQuestion());

        Map<Integer, String> answers = quizService.getCurrentAnswers();
        buttonA.setText(answers.get(1));
        buttonB.setText(answers.get(2));
        buttonC.setText(answers.get(3));

        fadeInViews(displayQuestionTextView, buttonA, buttonB, buttonC);


        int oldProgress = progressBar.getProgress();
        int newProgress = (quizService.getCurrentQuestionNumber() * 100) / quizService.getTotalQuestions();
        AnimateProgressBar(oldProgress, newProgress);

    }

    // This is my very simple attempt at animation. It gets so complicated and I didn't want to waste too much time.
    private void AnimateProgressBar(int from, int to){
        ObjectAnimator anim = ObjectAnimator.ofInt(progressBar, "progress", from, to);
        anim.setDuration(300);
        anim.setInterpolator(new AnticipateInterpolator());
        anim.start();
    }

    private void pulseButton(View button) {
        button.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .withEndAction(() -> button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                )
                .start();
    }

    private void fadeInViews(View... views) {
        for (View view : views) {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(900).start();
        }
    }

}