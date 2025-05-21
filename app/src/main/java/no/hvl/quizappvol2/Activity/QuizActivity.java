// ✅ Cleaned QuizActivity.java
package no.hvl.quizappvol2.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import no.hvl.quizappvol2.Fragments.QuizButtonsFragment;
import no.hvl.quizappvol2.Fragments.QuizImageFragment;
import no.hvl.quizappvol2.ImageItem;
import no.hvl.quizappvol2.R;
import no.hvl.quizappvol2.ViewModel.QuizViewModel;

public class QuizActivity extends AppCompatActivity implements QuizButtonsFragment.OnAnswerSelectedListener {

    private QuizViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        TextView scoreDisplay = findViewById(R.id.scoreDisplay);
        viewModel.getScore().observe(this, score ->
                scoreDisplay.setText("Score: " + score)
        );

        viewModel.getDataLoaded().observe(this, loaded -> {
            if (loaded) {
                loadNextQuestion();
            }
        });
    }

    public void loadNextQuestion() {
        ImageItem current = viewModel.getCurrentItem();

        if (current == null) {
            Toast.makeText(this, "Quiz is over! Final score: " + viewModel.getScore().getValue(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        QuizImageFragment imageFragment = QuizImageFragment.newInstance(current.getImagePath());
        List<String> options = viewModel.generateOptions();

        QuizButtonsFragment buttonsFragment = QuizButtonsFragment.newInstance(
                current.getDescription(), options
        );
        buttonsFragment.setOnAnswerSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment oldImage = getSupportFragmentManager().findFragmentById(R.id.imageContainer);
        if (oldImage != null) transaction.remove(oldImage);

        transaction.replace(R.id.imageContainer, imageFragment);
        transaction.replace(R.id.buttonsContainer, buttonsFragment);
        transaction.commit();
    }

    @Override
    public void onAnswerSelected(String selectedAnswer) {
        viewModel.advanceQuestion();
        loadNextQuestion();

    }
}
