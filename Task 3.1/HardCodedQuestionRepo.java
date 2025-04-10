package com.trevin.quizapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardCodedQuestionRepo implements QuestionRepository{
    @Override
    public List<Question> getQuestions() {

        List<Question> testQuestions = new ArrayList<>();
        testQuestions.add(new Question("What planet in our solar system has the most moons?", 2, new HashMap<Integer, String>() {{
            put(1, "Jupiter");
            put(2, "Saturn");
            put(3, "Uranus");
        }}));

        testQuestions.add(new Question("Which programming language was created by James Gosling in 1995?", 3, new HashMap<Integer, String>() {{
            put(1, "Python");
            put(2, "C++");
            put(3, "Java");
        }}));

        testQuestions.add(new Question("What is the smallest country in the world by land area?", 1, new HashMap<Integer, String>() {{
            put(1, "Vatican City");
            put(2, "Monaco");
            put(3, "Maldives");
        }}));

        testQuestions.add(new Question("Which animal has the strongest bite force measured in nature?", 3, new HashMap<Integer, String>() {{
            put(1, "Great White Shark");
            put(2, "Hippopotamus");
            put(3, "Saltwater Crocodile");
        }}));

        testQuestions.add(new Question("In what year did Neil Armstrong first walk on the Moon?", 2, new HashMap<Integer, String>() {{
            put(1, "1965");
            put(2, "1969");
            put(3, "1972");
        }}));

        return testQuestions;
    }

}
