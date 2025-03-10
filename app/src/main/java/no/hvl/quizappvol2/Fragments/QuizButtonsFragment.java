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

public class QuizButtonsFragment extends Fragment {

    private static final String CORRECT_ANSWER_KEY = "correct_answer";
    private static final String OPTIONS_KEY = "options";
    private static final String SCORE_KEY = "score"; // ✅ Store score
    private Button answer1, answer2, answer3, next;
    private TextView scoreText;
    private static int score;
    private String correctAnswer;
    private List<String> options;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    private OnAnswerSelectedListener listener;

    public static QuizButtonsFragment newInstance(String correctAnswer, ArrayList<String> options) {
        QuizButtonsFragment fragment = new QuizButtonsFragment();
        Bundle args = new Bundle();
        args.putString(CORRECT_ANSWER_KEY, correctAnswer);
        args.putStringArrayList(OPTIONS_KEY, options);
        args.putInt(SCORE_KEY, score);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_buttons, container, false);

        answer1 = view.findViewById(R.id.answer1);
        answer2 = view.findViewById(R.id.answer2);
        answer3 = view.findViewById(R.id.answer3);
        next = view.findViewById(R.id.next);
        scoreText = view.findViewById(R.id.score);

        if (getArguments() != null) {
            correctAnswer = getArguments().getString(CORRECT_ANSWER_KEY);
            options = getArguments().getStringArrayList(OPTIONS_KEY);
            score = getArguments().getInt(SCORE_KEY);
        }

        scoreText.setText("Score: " + score);

        Collections.shuffle(options);
        answer1.setText(options.get(0));
        answer2.setText(options.get(1));
        answer3.setText(options.get(2));

        answer1.setOnClickListener(v -> checkAnswer(answer1));
        answer2.setOnClickListener(v -> checkAnswer(answer2));
        answer3.setOnClickListener(v -> checkAnswer(answer3));
        next.setOnClickListener(v -> loadNextQuestion());

        return view;
    }

    public void loadNextQuestion() {
        if (listener != null) {
            listener.onAnswerSelected(false);
            ((QuizActivity) getActivity()).loadNextQuestion();
        }
    }

    private void checkAnswer(Button selectedButton) {
        String selectedAnswer = selectedButton.getText().toString();

        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);

        Button correctButton = getCorrectButton();

        if (selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundColor(Color.GREEN);
            score++;
            scoreText.setText("Score: " + score);
            if (listener != null) {
                listener.onAnswerSelected(true);
            }
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            correctButton.setBackgroundColor(Color.parseColor("#A5D6A7"));
            if (listener != null) {
                listener.onAnswerSelected(false);
            }
        }

        next.setVisibility(View.VISIBLE);
        next.setEnabled(true);
    }

    private Button getCorrectButton() {
        if (answer1.getText().toString().equals(correctAnswer)) {
            return answer1;
        } else if (answer2.getText().toString().equals(correctAnswer)) {
            return answer2;
        } else {
            return answer3;
        }
    }
}
