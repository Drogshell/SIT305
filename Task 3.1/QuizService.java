package com.trevin.quizapp;

import java.util.List;
import java.util.Map;

public class QuizService {
    private final List<Question> questions;
    private Map<Integer, String> currentShuffledAnswers;
    private int currentQuestionIndex = 0;
    private int score = 0;

    public QuizService(QuestionRepository questionRepository){
        this.questions = questionRepository.getQuestions();
    }

    public Question getCurrentQuestion(){
        if (currentQuestionIndex < questions.size()){
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    public Map<Integer, String> getCurrentAnswers(){
        if (currentShuffledAnswers == null){
            currentShuffledAnswers = getCurrentQuestion().getShuffledAnswers();
        }
        return currentShuffledAnswers;
    }

    public boolean checkAnswer(int selectedIndex){
        Question question = getCurrentQuestion();
        if (question != null){
            boolean isCorrect = selectedIndex == question.getCorrectShuffledIndex();
            if (isCorrect) score++;
            return isCorrect;
        }
        return false;
    }

    public void nextQuestion(){
        currentQuestionIndex++;
        currentShuffledAnswers = null;
    }

    public boolean hasMoreQuestions(){
        return currentQuestionIndex < questions.size();
    }

    public int getScore(){
        return score;
    }

    public int getTotalQuestions(){
        return questions.size();
    }

    public int getCurrentQuestionNumber(){
        return currentQuestionIndex + 1;
    }

}
