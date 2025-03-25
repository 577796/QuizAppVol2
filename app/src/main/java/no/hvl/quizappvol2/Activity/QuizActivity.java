package no.hvl.quizappvol2.Activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.hvl.quizappvol2.ImageDatabase;
import no.hvl.quizappvol2.ImageItem;
import no.hvl.quizappvol2.Fragments.QuizButtonsFragment;
import no.hvl.quizappvol2.Fragments.QuizImageFragment;
import no.hvl.quizappvol2.R;

// Activity that runs the quiz. Implements the callback interface for answer selection.
public class QuizActivity extends AppCompatActivity implements QuizButtonsFragment.OnAnswerSelectedListener {

    private List<ImageItem> quizItems; // List of questions (images)
    private int currentQuestion; // Index of current question
    private ExecutorService executorService; // Thread pool for background tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view); // Set the quiz layout (with fragment containers)

        // Initialize ExecutorService if it's not already
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor(); // One background thread
        }

        // Run DB loading in background
        executorService.execute(() -> {
            quizItems = loadFromDB(); // Load quiz images from DB

            // If successful, load the first question on the UI thread
            if (quizItems != null && !quizItems.isEmpty()) {
                runOnUiThread(this::loadNextQuestion);
            } else {
                // If no images found, show a toast
                runOnUiThread(() -> Toast.makeText(this, "No images in database!", Toast.LENGTH_LONG).show());
            }
        });
    }

    // Loads and displays the next quiz question (image + answer buttons)
    public void loadNextQuestion() {
        // If DB failed or list is empty
        if (quizItems == null || quizItems.isEmpty()) {
            Toast.makeText(this, "No images available for quiz!", Toast.LENGTH_LONG).show();
            return;
        }

        // End of quiz
        if (currentQuestion >= quizItems.size()) {
            Toast.makeText(this, "Quiz is over!", Toast.LENGTH_LONG).show();
            return;
        }

        // Get current item and correct answer
        ImageItem currentItem = quizItems.get(currentQuestion);
        String correctAnswer = currentItem.getDescription();

        // Create both fragments
        QuizImageFragment imageFragment = QuizImageFragment.newInstance(currentItem.getImagePath());
        QuizButtonsFragment buttonsFragment = QuizButtonsFragment.newInstance(correctAnswer, generateOptions(correctAnswer));

        // Set up communication between buttons fragment and activity
        buttonsFragment.setOnAnswerSelectedListener(this);

        // Begin fragment transaction and replace placeholders with new fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.imageContainer, imageFragment); // Replace image fragment
        transaction.replace(R.id.buttonsContainer, buttonsFragment); // Replace buttons fragment
        transaction.commit(); // Apply the transaction

        currentQuestion++; // Move to next question
    }

    // Loads image items from the database (synchronously in background)
    private List<ImageItem> loadFromDB() {
        ImageDatabase db = ImageDatabase.getInstance(this); // Get DB
        List<ImageItem> images = db.imageItemDAO().getAllImagesSync(); // Get all images (not LiveData)

        if (images == null || images.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "No images found in the database!", Toast.LENGTH_LONG).show());
        } else {
            java.util.Collections.shuffle(images); // Randomize image order
        }

        return images;
    }

    // Creates a shuffled list of answer options with 1 correct and 2 unique wrong answers
    private ArrayList<String> generateOptions(String correctAnswer) {
        ArrayList<String> options = new ArrayList<>();
        options.add(correctAnswer); // Add correct answer

        // Set ensures uniqueness by not allowing duplicates
        Set<String> uniqueOptions = new HashSet<>();
        uniqueOptions.add(correctAnswer); // Track uniqueness

        // Shuffle quiz items to mix up wrong answers
        List<ImageItem> shuffledItems = new ArrayList<>(quizItems);
        Collections.shuffle(shuffledItems);

        // Add two unique wrong answers
        for (ImageItem item : shuffledItems) {
            if (uniqueOptions.size() < 3 && !uniqueOptions.contains(item.getDescription())) {
                uniqueOptions.add(item.getDescription());
                options.add(item.getDescription());
            }
        }

        Collections.shuffle(options); // Shuffle answer order so correct is in different spots
        return options;
    }

    // Gets the correct answer for the last displayed question
    public String getCorrectAnswerForDisplayedImage() {
        if (quizItems == null || currentQuestion >= quizItems.size()) {
            return null;
        }
        return quizItems.get(currentQuestion - 1).getDescription(); // Go back one since currentQuestion was incremented
    }

    // Reset score when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuizButtonsFragment.resetScore(); // Call static reset method
    }

    // Interface method called when a user selects an answer (currently unused)
    @Override
    public void onAnswerSelected(boolean isCorrect) {
        // Could be used to log results, provide feedback, or update stats
    }

}
