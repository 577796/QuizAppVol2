package no.hvl.quizappvol2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.hvl.quizappvol2.Activity.QuizActivity;
import no.hvl.quizappvol2.R;

// This fragment handles the quiz answer buttons and score display
public class QuizButtonsFragment extends Fragment {

    // Keys used to store values in the fragment's argument Bundle
    private static final String CORRECT_ANSWER_KEY = "correct_answer";
    private static final String OPTIONS_KEY = "options";
    private static final String SCORE_KEY = "score";

    // UI components
    private Button answer1, answer2, answer3, next;
    private TextView scoreText;

    // Static variable to persist score across instances
    private static int score;

    // The correct answer and list of possible answers
    private String correctAnswer;
    private List<String> options;

    // Interface for communicating answer result with the activity
    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    private OnAnswerSelectedListener listener;

    // Factory method to create a new instance of the fragment with bundled arguments
    public static QuizButtonsFragment newInstance(String correctAnswer, ArrayList<String> options) {
        QuizButtonsFragment fragment = new QuizButtonsFragment();
        Bundle args = new Bundle();
        args.putString(CORRECT_ANSWER_KEY, correctAnswer); // Save correct answer
        args.putStringArrayList(OPTIONS_KEY, options); // Save answer options
        args.putInt(SCORE_KEY, score); // Save current score
        fragment.setArguments(args); // Attach bundle to fragment
        return fragment;
    }

    // Setter for the listener to communicate with the activity
    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }

    // Inflate the fragment's layout and initialize logic
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the UI layout
        View view = inflater.inflate(R.layout.fragment_quiz_buttons, container, false);

        // Connect UI components
        answer1 = view.findViewById(R.id.answer1);
        answer2 = view.findViewById(R.id.answer2);
        answer3 = view.findViewById(R.id.answer3);
        next = view.findViewById(R.id.next);
        scoreText = view.findViewById(R.id.score);

        // Retrieve arguments from the bundle
        if (getArguments() != null) {
            correctAnswer = getArguments().getString(CORRECT_ANSWER_KEY);
            options = getArguments().getStringArrayList(OPTIONS_KEY);
            score = getArguments().getInt(SCORE_KEY);
        }

        // Display current score
        scoreText.setText("Score: " + score);

        // Shuffle and assign answer options
        Collections.shuffle(options);
        answer1.setText(options.get(0));
        answer2.setText(options.get(1));
        answer3.setText(options.get(2));

        // Set click listeners for answer buttons
        answer1.setOnClickListener(v -> checkAnswer(answer1));
        answer2.setOnClickListener(v -> checkAnswer(answer2));
        answer3.setOnClickListener(v -> checkAnswer(answer3));

        // Set listener for "Next" button
        next.setOnClickListener(v -> loadNextQuestion());

        return view;
    }

    // Called when the user taps "Next" after answering
    public void loadNextQuestion() {
        if (listener != null) {
            listener.onAnswerSelected(false); // Notify listener
            ((QuizActivity) getActivity()).loadNextQuestion(); // Load next question from the activity
        }
    }

    // Evaluates selected answer and updates UI + score
    private void checkAnswer(Button selectedButton) {
        String selectedAnswer = selectedButton.getText().toString();

        // Disable all answer buttons
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);

        Button correctButton = getCorrectButton(); // Find which button is correct

        if (selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundColor(Color.GREEN); // Correct = green
            score++; // Increase score
            scoreText.setText("Score: " + score); // Update score text
            if (listener != null) {
                listener.onAnswerSelected(true); // Notify correct
            }
        } else {
            selectedButton.setBackgroundColor(Color.RED); // Wrong = red
            correctButton.setBackgroundColor(Color.parseColor("#A5D6A7")); // Show correct answer
            if (listener != null) {
                listener.onAnswerSelected(false); // Notify incorrect
            }
        }

        // Show the "Next" button
        next.setVisibility(View.VISIBLE);
        next.setEnabled(true);
    }

    // Finds the button that holds the correct answer
    private Button getCorrectButton() {
        if (answer1.getText().toString().equals(correctAnswer)) {
            return answer1;
        } else if (answer2.getText().toString().equals(correctAnswer)) {
            return answer2;
        } else {
            return answer3;
        }
    }

    // Resets the score (can be called externally before quiz starts)
    public static void resetScore() {
        score = 0;
    }

}
