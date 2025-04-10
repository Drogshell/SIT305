package com.trevin.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {
    private final String question;
    private final int correctAnswerIndex;
    private int correctShuffledIndex;
    private final Map<Integer, String> answers;

    public Question(String question, int correctAnswerIndex, HashMap<Integer, String> answers){
        this.question = question;
        this.correctAnswerIndex = correctAnswerIndex;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public Map<Integer, String> getShuffledAnswers(){
        // Convert the original answers map to a list of entries
        // This gives us access to both the key (1, 2, 3) and the answer string ("A", "B", "C")
        List<Map.Entry<Integer, String>> entryList = new ArrayList<>(answers.entrySet());

        // Randomly shuffle the list of entries
        // This changes the order of answer choices while preserving their original key-value relationships
        Collections.shuffle(entryList);

        // Assign new keys (1, 2, 3) based on the shuffled order — these are button positions
        Map<Integer, String> shuffled = new HashMap<>();
        // newIndex will represent the new key/button position (1 = A, 2 = B, 3 = C)
        int newIndex = 1;
        // correctKey will store the new position of the correct answer after shuffling
        int correctKey = -1;

        // Loop through the shuffled list and populate the new map
        for (Map.Entry<Integer, String> entry: entryList){
            // Assign the shuffled answer to a new key
            shuffled.put(newIndex, entry.getValue());

            // Check if this shuffled entry was originally the correct answer
            // If it was, track what its new key is (so it can be checked later in checkAnswer())
            if (entry.getKey() == correctAnswerIndex){
                correctKey = newIndex;
            }
            newIndex++;
        }
        // Store the new correct answers index so we know which button is right
        correctShuffledIndex = correctKey;
        // Return the shuffled map with new button indices
        return shuffled;
    }

    public int getCorrectShuffledIndex() {
        return correctShuffledIndex;
    }
}
