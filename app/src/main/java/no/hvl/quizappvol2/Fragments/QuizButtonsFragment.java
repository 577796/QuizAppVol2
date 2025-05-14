// ✅ Cleaned QuizButtonsFragment.java
package no.hvl.quizappvol2.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import no.hvl.quizappvol2.R;
import no.hvl.quizappvol2.ViewModel.QuizViewModel;

public class QuizButtonsFragment extends Fragment {

    private static final String CORRECT_ANSWER_KEY = "correct_answer";
    private static final String OPTIONS_KEY = "options";

    private Button answer1, answer2, answer3, nextButton;
    private String correctAnswer;
    private List<String> options;
    private String selectedAnswer;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(String selectedAnswer);
    }

    private OnAnswerSelectedListener listener;

    public static QuizButtonsFragment newInstance(String correctAnswer, List<String> options) {
        QuizButtonsFragment fragment = new QuizButtonsFragment();
        Bundle args = new Bundle();
        args.putString(CORRECT_ANSWER_KEY, correctAnswer);
        args.putStringArrayList(OPTIONS_KEY, new ArrayList<>(options));
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_buttons, container, false);

        QuizViewModel viewModel = new ViewModelProvider(requireActivity()).get(QuizViewModel.class);

        answer1 = view.findViewById(R.id.answer1);
        answer2 = view.findViewById(R.id.answer2);
        answer3 = view.findViewById(R.id.answer3);
        nextButton = view.findViewById(R.id.next);

        nextButton.setEnabled(false);

        if (getArguments() != null) {
            correctAnswer = getArguments().getString(CORRECT_ANSWER_KEY);
            options = getArguments().getStringArrayList(OPTIONS_KEY);
        }

        if (options != null && options.size() >= 3) {
            answer1.setText(options.get(0));
            answer2.setText(options.get(1));
            answer3.setText(options.get(2));
        }

        View.OnClickListener answerClickListener = v -> {
            if (selectedAnswer != null) return;

            Button clicked = (Button) v;
            selectedAnswer = clicked.getText().toString();

            viewModel.checkAnswer(selectedAnswer);

            applyAnswerFeedback();
            nextButton.setEnabled(true);
        };


        answer1.setOnClickListener(answerClickListener);
        answer2.setOnClickListener(answerClickListener);
        answer3.setOnClickListener(answerClickListener);

        nextButton.setOnClickListener(v -> {
            if (selectedAnswer != null && listener != null) {
                listener.onAnswerSelected(selectedAnswer);
            }
        });

        return view;
    }

    private void applyAnswerFeedback() {
        for (Button b : new Button[]{answer1, answer2, answer3}) {
            b.setEnabled(false);
            String text = b.getText().toString();
            if (text.equals(correctAnswer)) {
                b.setBackgroundColor(Color.parseColor("#A5D6A7")); // Green
            } else if (text.equals(selectedAnswer)) {
                b.setBackgroundColor(Color.parseColor("#EF9A9A")); // Red
            }
        }
    }
}
