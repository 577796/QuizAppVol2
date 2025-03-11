package no.hvl.quizappvol2.Activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import no.hvl.quizappvol2.ImageDatabase;
import no.hvl.quizappvol2.ImageItem;
import no.hvl.quizappvol2.Fragments.QuizButtonsFragment;
import no.hvl.quizappvol2.Fragments.QuizImageFragment;
import no.hvl.quizappvol2.R;

public class QuizActivity extends AppCompatActivity implements QuizButtonsFragment.OnAnswerSelectedListener {

    private List<ImageItem> quizItems;
    private int currentQuestion;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view);


        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            quizItems = loadFromDB();
            if (quizItems != null && !quizItems.isEmpty()) {
                runOnUiThread(this::loadNextQuestion);
            } else {
                runOnUiThread(() -> Toast.makeText(this, "No images in database!", Toast.LENGTH_LONG).show());
            }
        });
    }

    public void loadNextQuestion() {
        if (quizItems == null || quizItems.isEmpty()) {
            Toast.makeText(this, "No images available for quiz!", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentQuestion >= quizItems.size()) {
            Toast.makeText(this, "Quiz is over!", Toast.LENGTH_LONG).show();
            return;
        }

        ImageItem currentItem = quizItems.get(currentQuestion);
        String correctAnswer = currentItem.getDescription();

        QuizImageFragment imageFragment = QuizImageFragment.newInstance(currentItem.getImagePath());
        QuizButtonsFragment buttonsFragment = QuizButtonsFragment.newInstance(correctAnswer, generateOptions(correctAnswer));

        buttonsFragment.setOnAnswerSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.imageContainer, imageFragment);
        transaction.replace(R.id.buttonsContainer, buttonsFragment);
        transaction.commit();

        currentQuestion++;
    }

    private List<ImageItem> loadFromDB() {
        ImageDatabase db = ImageDatabase.getInstance(this);
        List<ImageItem> images = db.imageItemDAO().getAllImagesSync(); // Use a synchronous call to avoid LiveData issues

        if (images == null || images.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "No images found in the database!", Toast.LENGTH_LONG).show());
        } else {
            java.util.Collections.shuffle(images);
        }
        return images;
    }

    private ArrayList<String> generateOptions(String correctAnswer) {
        ArrayList<String> options = new ArrayList<>();
        options.add(correctAnswer);
        for (ImageItem item : quizItems) {
            if (!item.getDescription().equals(correctAnswer) && options.size() < 3) {
                options.add(item.getDescription());
            }
            while (options.size() < 3) {
                options.add("Placeholder " + options.size());  // Fallback dummy answers
            }
        }
        return options;
    }

    public String getCorrectAnswerForDisplayedImage() {
        if (quizItems == null || currentQuestion >= quizItems.size()) {
            return null;
        }
        return quizItems.get(currentQuestion - 1).getDescription(); // Get the last displayed question's answer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuizButtonsFragment.resetScore();
    }


    @Override
    public void onAnswerSelected(boolean isCorrect) {}

}
